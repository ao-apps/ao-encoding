/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2013, 2015, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-encoding.
 *
 * ao-encoding is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-encoding is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-encoding.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.encoding;

import com.aoindustries.io.AppendableWriter;
import com.aoindustries.io.Writable;
import com.aoindustries.lang.Strings;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Segment;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

/**
 * Coerces objects to String compatible with JSP Expression Language (JSP EL)
 * and the Java Standard Taglib (JSTL).  Also adds support for seamless output
 * of XML DOM nodes.
 *
 * @author  AO Industries, Inc.
 */
public final class Coercion  {

	private static final Logger logger = Logger.getLogger(Coercion.class.getName());

	/**
	 * Converts an object to a string.
	 */
	@SuppressWarnings("deprecation")
	public static String toString(Object value) {
		return com.aoindustries.util.EncodingUtils.toString(value);
		/* TODO: Code will move here once encodingutils no longer used */
	}

	private static final String BODY_CONTENT_IMPL_CLASS = "org.apache.jasper.runtime.BodyContentImpl";
	private static final String WRITER_FIELD = "writer";

	private static final Class<?> bodyContentImplClass;
	private static final Field writerField;
	static {
		Class<?> clazz;
		Field field;
		try {
			clazz = Class.forName(BODY_CONTENT_IMPL_CLASS);
			field = clazz.getDeclaredField(WRITER_FIELD);
			field.setAccessible(true);
		} catch(RuntimeException | ReflectiveOperationException e) {
			if(logger.isLoggable(Level.INFO)) {
				logger.log(
					Level.INFO,
					"Cannot get direct access to the "+BODY_CONTENT_IMPL_CLASS+"."+WRITER_FIELD+" field.  "
					+ "Unwrapping of BodyContent disabled.  "
					+ "The system will behave correctly, but some optimizations are disabled.",
					e
				);
			}
			clazz = null;
			field = null;
		}
		bodyContentImplClass = clazz;
		writerField = field;
		//System.err.println("DEBUG: bodyContentImplClass="+bodyContentImplClass);
		//System.err.println("DEBUG: writerField="+writerField);
	}

	/**
	 * Unwraps a writer to expose any wrapped writer.  The wrapped writer is
	 * only returned when it is write-through, meaning the wrapper doesn't modify
	 * the data written, and writes to the wrapped writer immediately (no buffering).
	 *
	 * This is used to access the wrapped write for Catalina's implementation of
	 * the servlet BodyContent.  This allows implementations of BufferResult to
	 * more efficiently write their contents to recognized writer implementations.
	 */
	private static Writer unwrap(Writer out) throws IOException {
		while(true) {
			Class<? extends Writer> outClass = out.getClass();
			// Note: bodyContentImplClass will be null when direct access disabled
			if(outClass==bodyContentImplClass) {
				try {
					Writer writer = (Writer)writerField.get(out);
					// When the writer field is non-null, BodyContent is pass-through and we may safely directly access the wrapped writer.
					if(writer!=null) {
						// Will keep looping to unwrap the wrapped out
						out = writer;
					} else {
						// BodyContent is buffering, must use directly
						return out;
					}
				} catch(IllegalAccessException e) {
					throw new IOException(e);
				}
			} else {
				// No unwrapping
				return out;
			}
		}
	}

	/**
	 * Unwraps an appendable to expose any wrapped appendable.  The wrapped appendable is
	 * only returned when it is write-through, meaning the wrapper doesn't modify
	 * the data written, and appends to the wrapped appendable immediately (no buffering).
	 * <p>
	 * There is currently no implementation of appendable-specific unwrapping,
	 * but this is here for consistency with the writer unwrapping.
	 * </p>
	 */
	private static Appendable unwrap(Appendable out) throws IOException {
		if(out instanceof Writer) return unwrap(out);
		return out;
	}

	/**
	 * Writes an object's String representation,
	 * supporting streaming for specialized types.
	 * <ol>
	 * <li>{@link Node} will be output as {@link StandardCharsets#UTF_8}.</li>
	 * </ol>
	 */
	public static void write(Object value, Writer out) throws IOException {
		assert out != null;
		if(out instanceof MediaWriter) {
			// Unwrap media writer and use encoder directly
			MediaWriter mediaWriter = (MediaWriter)out;
			write(
				value,
				mediaWriter.getEncoder(),
				mediaWriter.getOut()
			);
		} else {
			if(value instanceof String) {
				// If A is a string, then the result is A.
				out.write((String)value);
			} else if(value == null) {
				// Otherwise, if A is null, then the result is "".
				// Write nothing
			} else if(value instanceof Writable) {
				// Otherwise, if is a Writable, support optimizations
				Writable writable = (Writable)value;
				if(writable.isFastToString()) {
					out.write(writable.toString());
				} else {
					// Avoid intermediate String from Writable
					writable.writeTo(unwrap(out));
				}
			} else if(value instanceof CharSequence) {
				// Otherwise, support CharSequence
				out.append((CharSequence)value);
			} else if(value instanceof char[]) {
				// Otherwise, support char[]
				out.write((char[])value);
			} else if(value instanceof Node) {
				// Otherwise, if is a DOM node, serialize the output
				try {
					// Can use thread-local or pooled transformers if performance is ever an issue
					TransformerFactory transFactory = TransformerFactory.newInstance();
					Transformer transformer = transFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
					transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
					transformer.transform(
						new DOMSource((Node)value),
						new StreamResult(unwrap(out))
					);
				} catch(TransformerException e) {
					throw new IOException(e);
				}
			} else {
				// Otherwise, if A.toString() throws an exception, then raise an error
				// Otherwise, the result is A.toString();
				out.write(value.toString());
			}
		}
	}

	/**
	 * Writes an object's String representation,
	 * supporting streaming for specialized types.
	 * 
	 * @param  encoder  if null, no encoding is performed - write through
	 */
	public static void write(Object value, MediaEncoder encoder, Writer out) throws IOException {
		if(encoder==null) {
			write(value, out);
		} else {
			// Otherwise, if A is null, then the result is "".
			// Write nothing
			if(value != null) {
				// Unwrap out to avoid unnecessary validation of known valid output
				while(true) {
					out = unwrap(out);
					if(out instanceof MediaValidator) {
						MediaValidator validator = (MediaValidator)out;
						if(validator.canSkipValidation(encoder.getValidMediaOutputType())) {
							// Can skip validation, write directly to the wrapped output through the encoder
							out = validator.getOut();
						} else {
							break;
						}
					} else {
						break;
					}
				}
				// Write through the given encoder
				if(value instanceof String) {
					// If A is a string, then the result is A.
					encoder.write((String)value, out);
				} else if(value instanceof Writable) {
					// Otherwise, if is a Writable, support optimizations
					Writable writable = (Writable)value;
					if(writable.isFastToString()) {
						encoder.write(writable.toString(), out);
					} else {
						// Avoid intermediate String from Writable
						writable.writeTo(encoder, out);
					}
				} else if(value instanceof CharSequence) {
					// Otherwise, support CharSequence
					encoder.append((CharSequence)value, out);
				} else if(value instanceof char[]) {
					// Otherwise, support char[]
					encoder.write((char[])value, out);
				} else if(value instanceof Node) {
					// Otherwise, if is a DOM node, serialize the output
					try {
						// Can use thread-local or pooled transformers if performance is ever an issue
						TransformerFactory transFactory = TransformerFactory.newInstance();
						Transformer transformer = transFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
						transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
						transformer.transform(
							new DOMSource((Node)value),
							new StreamResult(new MediaWriter(encoder, out))
						);
					} catch(TransformerException e) {
						throw new IOException(e);
					}
				} else {
					// Otherwise, if A.toString() throws an exception, then raise an error
					// Otherwise, the result is A.toString();
					encoder.write(value.toString(), out);
				}
			}
		}
	}

	/**
	 * Writes an object's String representation with markup enabled,
	 * supporting streaming for specialized types.
	 * 
	 * @see  MarkupType
	 */
	public static void write(Object value, MarkupType markupType, Writer out) throws IOException {
		if(value != null) {
			BundleLookupThreadContext threadContext;
			if(
				markupType == null
				|| markupType == MarkupType.NONE
				|| (threadContext = BundleLookupThreadContext.getThreadContext(false)) == null
				// Avoid intermediate String from Writable
				|| (
					value instanceof Writable
					&& !((Writable)value).isFastToString()
				)
				// Other types that will not be converted to String for bundle lookups
				|| value instanceof char[]
				|| value instanceof Node
			) {
				write(value, out);
			} else {
				String str = toString(value);
				BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
				if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
				out.write(str);
				if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
			}
		}
	}

	/**
	 * Writes an object's String representation with markup enabled using the provided encoder,
	 * supporting streaming for specialized types.
	 *
	 * @param  encoder  no encoding performed when null
	 * @param  encoderPrefixSuffix  This includes the encoder {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefix}
	 *                              and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable) suffix}.
	 *
	 * @see  MarkupType
	 */
	public static void write(Object value, MarkupType markupType, MediaEncoder encoder, boolean encoderPrefixSuffix, Writer out) throws IOException {
		if(encoder == null) {
			write(value, markupType, out);
		} else if(value != null) {
			BundleLookupThreadContext threadContext;
			if(
				markupType == null
				|| markupType == MarkupType.NONE
				|| (threadContext = BundleLookupThreadContext.getThreadContext(false)) == null
				// Avoid intermediate String from Writable
				|| (
					value instanceof Writable
					&& !((Writable)value).isFastToString()
				)
				// Other types that will not be converted to String for bundle lookups
				|| value instanceof char[]
				|| value instanceof Node
			) {
				if(encoderPrefixSuffix) encoder.writePrefixTo(out);
				write(value, encoder, out);
				if(encoderPrefixSuffix) encoder.writeSuffixTo(out);
			} else {
				String str = toString(value);
				BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
				if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
				if(encoderPrefixSuffix) encoder.writePrefixTo(out);
				encoder.write(str, out);
				if(encoderPrefixSuffix) encoder.writeSuffixTo(out);
				if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
			}
		}
	}

	/**
	 * Appends an object's String representation,
	 * supporting streaming for specialized types.
	 * <ol>
	 * <li>{@link Node} will be output as {@link StandardCharsets#UTF_8}.</li>
	 * </ol>
	 */
	public static void append(Object value, Appendable out) throws IOException {
		assert out != null;
		if(out instanceof Writer) {
			write(value, (Writer)out);
		} else {
			if(value instanceof String) {
				// If A is a string, then the result is A.
				out.append((String)value);
			} else if(value == null) {
				// Otherwise, if A is null, then the result is "".
				// Write nothing
			} else if(value instanceof Writable) {
				// Otherwise, if is a Writable, support optimizations
				Writable writable = (Writable)value;
				if(writable.isFastToString()) {
					out.append(writable.toString());
				} else {
					// Avoid intermediate String from Writable
					writable.appendTo(unwrap(out));
				}
			} else if(value instanceof CharSequence) {
				// Otherwise, support CharSequence
				out.append((CharSequence)value);
			} else if(value instanceof char[]) {
				// Otherwise, support char[]
				char[] chs = (char[])value;
				out.append(new Segment(chs, 0, chs.length));
			} else if(value instanceof Node) {
				// Otherwise, if is a DOM node, serialize the output
				try {
					// Can use thread-local or pooled transformers if performance is ever an issue
					TransformerFactory transFactory = TransformerFactory.newInstance();
					Transformer transformer = transFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
					transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
					transformer.transform(
						new DOMSource((Node)value),
						new StreamResult(AppendableWriter.wrap(unwrap(out)))
					);
				} catch(TransformerException e) {
					throw new IOException(e);
				}
			} else {
				// Otherwise, if A.toString() throws an exception, then raise an error
				// Otherwise, the result is A.toString();
				out.append(value.toString());
			}
		}
	}

	/**
	 * Appends an object's String representation,
	 * supporting streaming for specialized types.
	 * 
	 * @param  encoder  if null, no encoding is performed - write through
	 */
	public static void append(Object value, MediaEncoder encoder, Appendable out) throws IOException {
		if(encoder==null) {
			append(value, out);
		} else if(out instanceof Writer) {
			write(value, encoder, (Writer)out);
		} else {
			// Otherwise, if A is null, then the result is "".
			// Write nothing
			if(value != null) {
				// Unwrap out to avoid unnecessary validation of known valid output
				while(true) {
					out = unwrap(out);
					if(out instanceof MediaValidator) {
						MediaValidator validator = (MediaValidator)out;
						if(validator.canSkipValidation(encoder.getValidMediaOutputType())) {
							// Can skip validation, write directly to the wrapped output through the encoder
							out = validator.getOut();
						} else {
							break;
						}
					} else {
						break;
					}
				}
				// Write through the given encoder
				if(value instanceof String) {
					// If A is a string, then the result is A.
					encoder.append((String)value, out);
				} else if(value instanceof Writable) {
					// Otherwise, if is a Writable, support optimizations
					Writable writable = (Writable)value;
					if(writable.isFastToString()) {
						encoder.append(writable.toString(), out);
					} else {
						// Avoid intermediate String from Writable
						writable.appendTo(encoder, out);
					}
				} else if(value instanceof CharSequence) {
					// Otherwise, support CharSequence
					encoder.append((CharSequence)value, out);
				} else if(value instanceof char[]) {
					// Otherwise, support char[]
					char[] chs = (char[])value;
					encoder.append(new Segment(chs, 0, chs.length), out);
				} else if(value instanceof Node) {
					// Otherwise, if is a DOM node, serialize the output
					try {
						// Can use thread-local or pooled transformers if performance is ever an issue
						TransformerFactory transFactory = TransformerFactory.newInstance();
						Transformer transformer = transFactory.newTransformer();
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
						transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
						transformer.transform(
							new DOMSource((Node)value),
							new StreamResult(new MediaWriter(encoder, AppendableWriter.wrap(out)))
						);
					} catch(TransformerException e) {
						throw new IOException(e);
					}
				} else {
					// Otherwise, if A.toString() throws an exception, then raise an error
					// Otherwise, the result is A.toString();
					encoder.append(value.toString(), out);
				}
			}
		}
	}

	/**
	 * Appends an object's String representation with markup enabled,
	 * supporting streaming for specialized types.
	 * 
	 * @see  MarkupType
	 */
	public static void append(Object value, MarkupType markupType, Appendable out) throws IOException {
		if(value != null) {
			BundleLookupThreadContext threadContext;
			if(out instanceof Writer) {
				write(value, markupType, (Writer)out);
			} else if(
				markupType == null
				|| markupType == MarkupType.NONE
				|| (threadContext = BundleLookupThreadContext.getThreadContext(false)) == null
				// Avoid intermediate String from Writable
				|| (
					value instanceof Writable
					&& !((Writable)value).isFastToString()
				)
				// Other types that will not be converted to String for bundle lookups
				|| value instanceof char[]
				|| value instanceof Node
			) {
				append(value, out);
			} else {
				String str = toString(value);
				BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
				if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
				out.append(str);
				if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
			}
		}
	}

	/**
	 * Appends an object's String representation with markup enabled using the provided encoder,
	 * supporting streaming for specialized types.
	 *
	 * @param  encoder  no encoding performed when null
	 * @param  encoderPrefixSuffix  This includes the encoder {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefix}
	 *                              and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable) suffix}.
	 *
	 * @see  MarkupType
	 */
	public static void append(Object value, MarkupType markupType, MediaEncoder encoder, boolean encoderPrefixSuffix, Appendable out) throws IOException {
		if(encoder == null) {
			append(value, markupType, out);
		} else if(value != null) {
			BundleLookupThreadContext threadContext;
			if(out instanceof Writer) {
				write(value, markupType, encoder, encoderPrefixSuffix, (Writer)out);
			} else if(
				markupType == null
				|| markupType == MarkupType.NONE
				|| (threadContext = BundleLookupThreadContext.getThreadContext(false)) == null
				// Avoid intermediate String from Writable
				|| (
					value instanceof Writable
					&& !((Writable)value).isFastToString()
				)
				// Other types that will not be converted to String for bundle lookups
				|| value instanceof char[]
				|| value instanceof Node
			) {
				if(encoderPrefixSuffix) encoder.writePrefixTo(out);
				append(value, encoder, out);
				if(encoderPrefixSuffix) encoder.writeSuffixTo(out);
			} else {
				String str = toString(value);
				BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
				if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
				if(encoderPrefixSuffix) encoder.writePrefixTo(out);
				encoder.append(str, out);
				if(encoderPrefixSuffix) encoder.writeSuffixTo(out);
				if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
			}
		}
	}

	/**
	 * Checks if a value is null or empty.
	 */
	public static boolean isEmpty(Object value) throws IOException {
		if(value instanceof String) {
			// If A is a string, then the result is A.
			return ((String)value).isEmpty();
		} else if(value == null) {
			// Otherwise, if A is null, then the result is "".
			return true;
		} else if(value instanceof Writable) {
			// Otherwise, if is a Writable, support optimizations
			return ((Writable)value).getLength() == 0;
		} else if(value instanceof CharSequence) {
			// Otherwise, support CharSequence
			return ((CharSequence)value).length() == 0;
		} else if(value instanceof char[]) {
			// Otherwise, support char[]
			return ((char[])value).length == 0;
		} else if(value instanceof Node) {
			// Otherwise, if is a DOM node, serialize the output
			return false; // There is a node, is not empty
		} else {
			// Otherwise, if A.toString() throws an exception, then raise an error
			// Otherwise, the result is A.toString();
			return value.toString().isEmpty();
		}
	}

	/**
	 * Returns the provided value (possibly converted to a different form, like String) or null if the value is empty.
	 * 
	 * @see  #isEmpty(java.lang.Object)
	 */
	public static Object nullIfEmpty(Object value) throws IOException {
		if(value instanceof String) {
			// If A is a string, then the result is A.
			return Strings.nullIfEmpty((String)value);
		} else if(value == null) {
			// Otherwise, if A is null, then the result is "".
			return null;
		} else if(value instanceof Writable) {
			// Otherwise, if is a Writable, support optimizations
			return ((Writable)value).getLength() == 0 ? null : value;
		} else if(value instanceof CharSequence) {
			// Otherwise, support CharSequence
			return ((CharSequence)value).length() == 0 ? null : value;
		} else if(value instanceof char[]) {
			// Otherwise, support char[]
			return ((char[])value).length == 0 ? null : value;
		} else if(value instanceof Node) {
			// Otherwise, if is a DOM node, serialize the output
			return value; // There is a node, is not empty
		} else {
			// Otherwise, if A.toString() throws an exception, then raise an error
			// Otherwise, the result is A.toString();
			return Strings.nullIfEmpty(value.toString());
		}
	}

	/**
	 * Returns the provided value trimmed, as per rules of {@link Strings#isWhitespace(int)}.
	 *
	 * @return  The original value, a trimmed version of the value (possibly of a different type),
	 *          a trimmed {@link String} representation of the object,
	 *          or {@code null} when the value is {@code null}.
	 */
	public static Object trim(Object value) throws IOException {
		if(value instanceof String) {
			// If A is a string, then the result is A.
			return Strings.trim((String)value);
		} else if(value == null) {
			// Otherwise, if A is null, then the result is "".
			return null;
		} else if(value instanceof Writable) {
			// Otherwise, if is a Writable, support optimizations
			Writable writable = (Writable)value;
			if(writable.isFastToString()) {
				return Strings.trim(writable.toString());
			} else {
				return writable.trim();
			}
		} else if(value instanceof CharSequence) {
			// Otherwise, support CharSequence
			return Strings.trim((CharSequence)value);
		} else if(value instanceof char[]) {
			// Otherwise, support char[]
			char[] chs = (char[])value;
			return Strings.trim(new Segment(chs, 0, chs.length));
		} else if(value instanceof Node) {
			// Otherwise, if is a DOM node, serialize the output
			return value; // There is a node, is not empty
		} else {
			// Otherwise, if A.toString() throws an exception, then raise an error
			// Otherwise, the result is A.toString();
			return Strings.trim(value.toString());
		}
	}

	/**
	 * Returns the provided value trimmed, as per rules of {@link Strings#isWhitespace(int)},
	 * or {@code null} if the value is empty after trimming.
	 *
	 * @return  The original value, a trimmed version of the value (possibly of a different type),
	 *          a trimmed {@link String} representation of the object,
	 *          or {@code null} when the value is {@code null}.
	 */
	public static Object trimNullIfEmpty(Object value) throws IOException {
		if(value instanceof String) {
			// If A is a string, then the result is A.
			return Strings.trimNullIfEmpty((String)value);
		} else if(value == null) {
			// Otherwise, if A is null, then the result is "".
			return null;
		} else if(value instanceof Writable) {
			// Otherwise, if is a Writable, support optimizations
			Writable writable = (Writable)value;
			if(writable.isFastToString()) {
				return Strings.trimNullIfEmpty(writable.toString());
			} else {
				writable = writable.trim();
				return writable.getLength() == 0 ? null : writable;
			}
		} else if(value instanceof CharSequence) {
			// Otherwise, support CharSequence
			return Strings.trimNullIfEmpty((CharSequence)value);
		} else if(value instanceof char[]) {
			// Otherwise, support char[]
			char[] chs = (char[])value;
			return Strings.trimNullIfEmpty(new Segment(chs, 0, chs.length));
		} else if(value instanceof Node) {
			// Otherwise, if is a DOM node, serialize the output
			return value; // There is a node, is not empty
		} else {
			// Otherwise, if A.toString() throws an exception, then raise an error
			// Otherwise, the result is A.toString();
			return Strings.trimNullIfEmpty(value.toString());
		}
	}

	/**
	 * Returns the provided number or zero if the value is empty.
	 * 
	 * @see  #isEmpty(java.lang.Object)
	 */
	public static int zeroIfEmpty(Integer value) throws IOException {
		if(isEmpty(value)) {
			return 0;
		} else {
			assert value != null;
			return value;
		}
	}

	/**
	 * Make no instances.
	 */
	private Coercion() {
	}
}
