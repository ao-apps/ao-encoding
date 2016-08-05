/*
 * ao-encoding - High performance character encoding.
 * Copyright (C) 2013, 2015, 2016  AO Industries, Inc.
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

import com.aoindustries.io.Writable;
import com.aoindustries.util.EncodingUtils;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Coerces objects to String compatible with JSP Expression Language (JSP EL)
 * and the Java Standard Taglib (JSTL).
 *
 * TODO: Once no longer used by ChainWriter, this should go to the aocode-public-taglib project.
 *
 * @author  AO Industries, Inc.
 */
public final class Coercion  {

	private static final Logger logger = Logger.getLogger(Coercion.class.getName());

	/**
	 * Converts an object to a string.
	 */
	public static String toString(Object value) {
		return EncodingUtils.toString(value);
		/* Code will move here once encodingutils no longer used
		// If A is a string, then the result is A.
		if(value instanceof String) return (String)value;
		// Otherwise, if A is null, then the result is "".
		if(value == null) return "";
		// Otherwise, if A.toString() throws an exception, then raise an error
		String str = value.toString();
		// Otherwise, the result is A.toString();
		return str;
		*/
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
		} catch(Exception e) {
			logger.log(
				Level.WARNING,
				"Cannot get direct access to the "+BODY_CONTENT_IMPL_CLASS+"."+WRITER_FIELD+" field.  "
				+ "Unwrapping of BodyContent disabled.  "
				+ "The system will behave correctly, but some optimizations are disabled.",
				e
			);
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
	 * Coerces an object to a String representation, supporting streaming for specialized types.
	 */
	public static void write(Object value, Writer out) throws IOException {
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
				Writable writable = (Writable)value;
				if(writable.isFastToString()) {
					out.write(writable.toString());
				} else {
					// Avoid intermediate String from Writable
					writable.writeTo(unwrap(out));
				}
			} else {
				// Otherwise, if A.toString() throws an exception, then raise an error
				// Otherwise, the result is A.toString();
				out.write(value.toString());
			}
		}
	}

	/**
	 * Coerces an object to a String representation, supporting streaming for specialized types.
	 * 
	 * @param  encoder  if null, no encoding is performed - write through
	 */
	public static void write(Object value, MediaEncoder encoder, Writer out) throws IOException {
		if(encoder==null) {
			write(value, out);
		} else {
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
			} else if(value == null) {
				// Otherwise, if A is null, then the result is "".
				// Write nothing
			} else if(value instanceof Writable) {
				Writable writable = (Writable)value;
				if(writable.isFastToString()) {
					encoder.write(writable.toString(), out);
				} else {
					// Avoid intermediate String from Writable
					writable.writeTo(encoder, out);
				}
			} else {
				// Otherwise, if A.toString() throws an exception, then raise an error
				// Otherwise, the result is A.toString();
				encoder.write(value.toString(), out);
			}
		}
	}

	/**
     * Make no instances.
     */
    private Coercion() {
    }
}
