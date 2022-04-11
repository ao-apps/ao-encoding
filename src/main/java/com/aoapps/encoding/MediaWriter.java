/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2013, 2015, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with ao-encoding.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.encoding;

import com.aoapps.hodgepodge.i18n.BundleLookupMarkup;
import com.aoapps.hodgepodge.i18n.BundleLookupThreadContext;
import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.CoercionOptimizer;
import com.aoapps.lang.NullArgumentException;
import com.aoapps.lang.Throwables;
import com.aoapps.lang.io.EncoderWriter;
import com.aoapps.lang.io.NoClose;
import com.aoapps.lang.io.Writable;
import com.aoapps.lang.io.function.IOConsumer;
import com.aoapps.lang.io.function.IOSupplierE;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Streaming versions of media encoders.
 * <p>
 * Note: The specialized subclasses implement {@linkplain Encode per-type interfaces} precisely matching
 * {@linkplain MediaEncoder#getInstance(com.aoapps.encoding.EncodingContext, com.aoapps.encoding.MediaType, com.aoapps.encoding.MediaType) the supported encoders}.
 * </p>
 *
 * @see  MediaEncoder
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public abstract class MediaWriter extends EncoderWriter implements ValidMediaFilter, Encode, NoClose {

	/**
	 * The {@code isNoClose(MediaWriter)} implementation used for methods where not specified, such as
	 * {@link MediaType#newMediaWriter(com.aoapps.encoding.EncodingContext, com.aoapps.encoding.MediaEncoder, java.io.Writer)}.
	 * <p>
	 * Defaults to {@code (out instanceof NoClose) && ((NoClose)out).isNoClose()}
	 * </p>
	 */
	public static final Predicate<? super MediaWriter> DEFAULT_IS_NO_CLOSE = mediaWriter -> {
		Writer out = mediaWriter.out;
		return (out instanceof NoClose) && ((NoClose)out).isNoClose();
	};

	/**
	 * The {@code closer(MediaWriter)} implementation used for methods where not specified, such as
	 * {@link MediaType#newMediaWriter(com.aoapps.encoding.EncodingContext, com.aoapps.encoding.MediaEncoder, java.io.Writer)}.
	 * <p>
	 * Defaults to {@code mediaWriter.out.close()}
	 * </p>
	 */
	public static final IOConsumer<? super MediaWriter> DEFAULT_CLOSER = mediaWriter -> mediaWriter.out.close();

	final EncodingContext encodingContext;
	final MediaEncoder encoder;
	final Whitespace indentDelegate;
	private final Predicate<? super MediaWriter> isNoClose;
	private final AtomicReference<IOConsumer<? super MediaWriter>> closerRef;

	/**
	 * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
	 * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
	 *                         This allows the indentation to be coordinated between nested content types.
	 * @param  isNoClose  Called to determine result of {@link #isNoClose()}
	 * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer,
	 *                 will only be called to be idempotent, implementation can assume will only be called once.
	 */
	@SuppressWarnings({"AssertWithSideEffects", "LeakingThisInConstructor", "OverridableMethodCallInConstructor"})
	MediaWriter(
		EncodingContext encodingContext,
		MediaEncoder encoder,
		Writer out,
		boolean outOptimized,
		Whitespace indentDelegate,
		Predicate<? super MediaWriter> isNoClose,
		IOConsumer<? super MediaWriter> closer
	) {
		super(encoder, out, outOptimized);
		Writer optimized = null;
		assert !((optimized = getOut()) instanceof MediaValidator) || !((MediaValidator)optimized).canSkipValidation(encoder.getValidMediaOutputType()) :
			"Validation should have been skipped by " + CoercionOptimizer.class.getName() + " registered by " + MediaValidator.class.getName()
			+ " for outputType = " + encoder.getValidMediaOutputType().name();
		assert !(optimized instanceof MediaValidator) || ((MediaValidator)optimized).isValidatingMediaInputType(encoder.getValidMediaOutputType()) :
			"MediaValidator = " + optimized.getClass().getName() + " is not validating outputType = " + encoder.getValidMediaOutputType().name();
		this.encodingContext = NullArgumentException.checkNotNull(encodingContext, "encodingContext");
		this.encoder = encoder;
		this.indentDelegate = indentDelegate;
		this.isNoClose = isNoClose;
		this.closerRef = new AtomicReference<>(closer);
		MediaType inputType = null;
		assert encoder.isValidatingMediaInputType(inputType = getValidMediaInputType());
		assert inputType.getEncodeInterface().isInstance(this) : "Nested self-encoding is always allowed";
		assert inputType.getMediaWriterClass() == getClass();
	}

	public EncodingContext getEncodingContext() {
		return encodingContext;
	}

	@Override
	public MediaEncoder getEncoder() {
		return encoder;
	}

	@Override
	public final boolean isNoClose() {
		return (isNoClose != null) && isNoClose.test(this);
	}

	@Override
	public final void close() throws IOException {
		IOConsumer<? super MediaWriter> closer = closerRef.getAndSet(null);
		if(closer != null) closer.accept(this);
	}

	/**
	 * Gets the indentation delegate that should be used for sub-writers.
	 *
	 * @return  {@link #indentDelegate} when present, otherwise {@code null} for none
	 */
	Whitespace getIndentDelegate() {
		return indentDelegate;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Gets the media type this specific type of media writer represents.
	 * This is a one-to-one relationship: every media type has a specific writer.
	 * </p>
	 */
	@Override
	public abstract MediaType getValidMediaInputType();

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return encoder.isValidatingMediaInputType(inputType);
	}

	@Override
	public boolean canSkipValidation(MediaType outputType) {
		return encoder.canSkipValidation(outputType);
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return encoder.getValidMediaOutputType();
	}

	@Override
	public MediaWriter append(char c) throws IOException {
		super.append(c);
		return this;
	}

	@Override
	public MediaWriter append(CharSequence csq) throws IOException {
		super.append(csq);
		return this;
	}

	@Override
	public MediaWriter append(CharSequence csq, int start, int end) throws IOException {
		super.append(csq, start, end);
		return this;
	}

	// <editor-fold desc="Encode - manual self-type and implementation" defaultstate="collapsed">
	@Override
	public MediaWriter encode(MediaType contentType, char ch) throws IOException {
		Encode.super.encode(contentType, ch);
		return this;
	}

	@Override
	public MediaWriter encode(MediaType contentType, char[] cbuf) throws IOException {
		Encode.super.encode(contentType, cbuf);
		return this;
	}

	@Override
	public MediaWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
		Encode.super.encode(contentType, cbuf, offset, len);
		return this;
	}

	@Override
	public MediaWriter encode(MediaType contentType, CharSequence csq) throws IOException {
		// Allow text markup from translations
		MediaType containerType = getValidMediaInputType();
		if(contentType == containerType) {
			// Already in the requested media type, no prefix/suffix required
			if(csq != null) {
				boolean buffered = encoder.isBuffered();
				MarkupType markupType = (buffered ? containerType : encoder.getValidMediaOutputType()).getMarkupType();
				assert markupType != null;
				BundleLookupThreadContext threadContext;
				if(
					markupType == MarkupType.NONE
					|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
					// Other types that will not be converted to String for bundle lookups
					|| !(csq instanceof String)
				) {
					encoder.append(csq, out);
				} else {
					String str = (String)csq;
					BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
					if(buffered) {
						// Do not bypass buffered encoder for markup
						if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, encoder, out);
						encoder.write(str, out);
						if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, encoder, out);
					} else {
						// Bypass encoder for markup
						if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
						encoder.write(str, out);
						if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
					}
				}
			}
		} else {
			// In different media type, need prefix/suffix
			MediaEncoder newEncoder = MediaEncoder.getInstance(encodingContext, contentType, containerType);
			if(newEncoder == null) {
				// Already in a compatible context that does not strictly require character encoding, but still need prefix/suffix
				newEncoder = new ValidateOnlyEncoder(contentType);
			}
			Writer optimized = Coercion.optimize(this, newEncoder);
			if(newEncoder.isBuffered()) {
				// Do not bypass buffered encoder for markup
				newEncoder.writePrefixTo(optimized);
				if(csq != null) {
					MarkupType markupType = contentType.getMarkupType();
					assert markupType != null;
					BundleLookupThreadContext threadContext;
					if(
						markupType == MarkupType.NONE
						|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
						// Other types that will not be converted to String for bundle lookups
						|| !(csq instanceof String)
					) {
						newEncoder.append(csq, optimized);
					} else {
						String str = (String)csq;
						BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
						if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, newEncoder, optimized);
						newEncoder.write(str, optimized);
						if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, newEncoder, optimized);
					}
				}
				newEncoder.writeSuffixTo(optimized, false);
			} else {
				// Bypass encoder for markup
				MarkupType markupType = containerType.getMarkupType();
				assert markupType != null;
				BundleLookupThreadContext threadContext;
				if(
					csq == null
					|| markupType == MarkupType.NONE
					|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
					// Other types that will not be converted to String for bundle lookups
					|| !(csq instanceof String)
				) {
					newEncoder.writePrefixTo(optimized);
					if(csq != null) newEncoder.append(csq, optimized);
					newEncoder.writeSuffixTo(optimized, false);
				} else {
					String str = (String)csq;
					BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup(str);
					if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, optimized);
					newEncoder.writePrefixTo(optimized);
					newEncoder.write(str, optimized);
					newEncoder.writeSuffixTo(optimized, false);
					if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, optimized);
				}
			}
		}
		return this;
	}

	@Override
	public MediaWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
		// Allow text markup from translations
		MediaType containerType = getValidMediaInputType();
		if(contentType == containerType) {
			// Already in the requested media type, no prefix/suffix required
			if(csq != null) {
				boolean buffered = encoder.isBuffered();
				MarkupType markupType = (buffered ? containerType : encoder.getValidMediaOutputType()).getMarkupType();
				assert markupType != null;
				BundleLookupThreadContext threadContext;
				if(
					markupType == MarkupType.NONE
					|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
					// Other types that will not be converted to String for bundle lookups
					|| !(csq instanceof String)
				) {
					encoder.append(csq, start, end, out);
				} else {
					BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup((String)csq);
					if(buffered) {
						// Do not bypass buffered encoder for markup
						if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, encoder, out);
						encoder.append(csq, start, end, out);
						if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, encoder, out);
					} else {
						// Bypass encoder for markup
						if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, out);
						encoder.append(csq, start, end, out);
						if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, out);
					}
				}
			}
		} else {
			// In different media type, need prefix/suffix
			MediaEncoder newEncoder = MediaEncoder.getInstance(encodingContext, contentType, containerType);
			if(newEncoder == null) {
				// Already in a compatible context that does not strictly require character encoding, but still need prefix/suffix
				newEncoder = new ValidateOnlyEncoder(contentType);
			}
			Writer optimized = Coercion.optimize(this, newEncoder);
			if(newEncoder.isBuffered()) {
				// Do not bypass buffered encoder for markup
				newEncoder.writePrefixTo(optimized);
				if(csq != null) {
					MarkupType markupType = contentType.getMarkupType();
					assert markupType != null;
					BundleLookupThreadContext threadContext;
					if(
						markupType == MarkupType.NONE
						|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
						// Other types that will not be converted to String for bundle lookups
						|| !(csq instanceof String)
					) {
						newEncoder.append(csq, start, end, optimized);
					} else {
						BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup((String)csq);
						if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, newEncoder, optimized);
						newEncoder.append(csq, start, end, optimized);
						if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, newEncoder, optimized);
					}
				}
				newEncoder.writeSuffixTo(optimized, false);
			} else {
				// Bypass encoder for markup
				MarkupType markupType = containerType.getMarkupType();
				assert markupType != null;
				BundleLookupThreadContext threadContext;
				if(
					csq == null
					|| markupType == MarkupType.NONE
					|| (threadContext = BundleLookupThreadContext.getThreadContext()) == null
					// Other types that will not be converted to String for bundle lookups
					|| !(csq instanceof String)
				) {
					newEncoder.writePrefixTo(optimized);
					if(csq != null) newEncoder.append(csq, start, end, optimized);
					newEncoder.writeSuffixTo(optimized, false);
				} else {
					BundleLookupMarkup lookupMarkup = threadContext.getLookupMarkup((String)csq);
					if(lookupMarkup != null) lookupMarkup.appendPrefixTo(markupType, optimized);
					newEncoder.writePrefixTo(optimized);
					newEncoder.append(csq, start, end, optimized);
					newEncoder.writeSuffixTo(optimized, false);
					if(lookupMarkup != null) lookupMarkup.appendSuffixTo(markupType, optimized);
				}
			}
		}
		return this;
	}

	@Override
	@SuppressWarnings("UseSpecificCatch")
	public MediaWriter encode(MediaType contentType, Object content) throws IOException {
		// Support Optional
		while(content instanceof Optional) {
			content = ((Optional<?>)content).orElse(null);
		}
		while(content instanceof IOSupplierE<?, ?>) {
			try {
				content = ((IOSupplierE<?, ?>)content).get();
			} catch(Throwable t) {
				throw Throwables.wrap(t, IOException.class, IOException::new);
			}
		}
		if(content instanceof char[]) {
			return encode(contentType, (char[])content);
		}
		if(content instanceof CharSequence) {
			return encode(contentType, (CharSequence)content);
		}
		if(content instanceof Writable) {
			Writable writable = (Writable)content;
			if(writable.isFastToString()) {
				return encode(contentType, writable.toString());
			}
		}
		if(content instanceof MediaWritable) {
			try {
				return encode(contentType, (MediaWritable<?>)content);
			} catch(Throwable t) {
				throw Throwables.wrap(t, IOException.class, IOException::new);
			}
		}
		// Allow text markup from translations
		MediaType containerType = getValidMediaInputType();
		if(contentType == containerType) {
			// Already in the requested media type, no prefix/suffix required
			if(encoder.isBuffered()) {
				// Do not bypass buffered encoder for markup
				MarkupCoercion.write(
					content,
					containerType.getMarkupType(),
					true,
					encoder,
					false,
					out,
					true
				);
			} else {
				// Bypass encoder for markup
				MarkupCoercion.write(
					content,
					encoder.getValidMediaOutputType().getMarkupType(),
					false,
					encoder,
					false,
					out,
					true
				);
			}
		} else {
			// In different media type, need prefix/suffix
			MediaEncoder newEncoder = MediaEncoder.getInstance(encodingContext, contentType, containerType);
			if(newEncoder == null) {
				// Already in a compatible context that does not strictly require character encoding, but still need prefix/suffix
				newEncoder = new ValidateOnlyEncoder(contentType);
			}
			if(newEncoder.isBuffered()) {
				// Do not bypass buffered encoder for markup
				MarkupCoercion.write(
					content,
					contentType.getMarkupType(),
					true,
					newEncoder,
					true,
					this,
					true // There are no CoercionOptimizer registered for MediaWritable
				);
			} else {
				// Bypass encoder for markup
				MarkupCoercion.write(
					content,
					containerType.getMarkupType(),
					false,
					newEncoder,
					true,
					this,
					true // There are no CoercionOptimizer registered for MediaWritable
				);
			}
		}
		return this;
	}

	@Override
	public <Ex extends Throwable> MediaWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
		Encode.super.encode(contentType, content);
		return this;
	}

	@Override
	public <Ex extends Throwable> MediaWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
		Encode.super.encode(contentType, content);
		return this;
	}

	@Override
	public MediaWriter encode(MediaType contentType) throws IOException {
		MediaWriter newMediaWriter;
		MediaType containerType = getValidMediaInputType();
		if(contentType == containerType) {
			// Already in the requested media type, no prefix/suffix required
			if(isNoClose()) {
				// close() is already protected, can use this writer directly
				newMediaWriter = this;
			} else {
				// Create new writer to ignore close
				newMediaWriter = contentType.newMediaWriter(
					encodingContext,
					encoder,
					out,
					true,
					getIndentDelegate(),
					mediaWriter -> true, // isNoClose
					null // Ignore close
				);
			}
		} else {
			// In different media type, need prefix/suffix
			MediaEncoder newEncoder = MediaEncoder.getInstance(encodingContext, contentType, containerType);
			if(newEncoder == null) {
				// Already in a compatible context that does not strictly require character encoding, but still need prefix/suffix
				newEncoder = new ValidateOnlyEncoder(contentType);
			}
			newMediaWriter = contentType.newMediaWriter(
				encodingContext,
				newEncoder,
				this,
				true, // There are no CoercionOptimizer registered for MediaWritable
				getIndentDelegate(),
				mediaWriter -> false, // !isNoClose
				closing -> closing.writeSuffix(false)
			);
			newMediaWriter.writePrefix();
		}
		assert contentType.getEncodeInterface().isInterface();
		assert contentType.getEncodeInterface().isInstance(newMediaWriter) : "Nested self-encoding is always allowed";
		assert contentType.getMediaWriterClass() == newMediaWriter.getClass();
		return newMediaWriter;
	}
	// </editor-fold>

	// TODO: comments

}
