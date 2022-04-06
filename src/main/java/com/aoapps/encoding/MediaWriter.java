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

import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.CoercionOptimizer;
import com.aoapps.lang.NullArgumentException;
import com.aoapps.lang.Throwables;
import com.aoapps.lang.io.EncoderWriter;
import com.aoapps.lang.io.Writable;
import com.aoapps.lang.io.function.IOConsumer;
import com.aoapps.lang.io.function.IOSupplierE;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

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
public abstract class MediaWriter extends EncoderWriter implements ValidMediaFilter, Encode {

	final EncodingContext encodingContext;
	final MediaType inputType;
	final MediaEncoder encoder;
	final Whitespace indentDelegate;
	private final IOConsumer<? super Writer> closer;

	private Map<MediaType, MediaWriter> mediaWriters;

	/**
	 * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
	 * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
	 *                         This allows the indentation to be coordinated between nested content types.
	 * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer
	 */
	@SuppressWarnings("AssertWithSideEffects")
	MediaWriter(
		EncodingContext encodingContext,
		MediaType inputType,
		MediaEncoder encoder,
		Writer out,
		boolean outOptimized,
		Whitespace indentDelegate,
		IOConsumer<? super Writer> closer
	) {
		super(encoder, out, outOptimized);
		Writer optimized = null;
		assert !((optimized = getOut()) instanceof MediaValidator) || !((MediaValidator)optimized).canSkipValidation(encoder.getValidMediaOutputType()) :
			"Validation should have been skipped by " + CoercionOptimizer.class.getName() + " registered by " + MediaValidator.class.getName()
			+ " for outputType = " + encoder.getValidMediaOutputType().name();
		assert !(optimized instanceof MediaValidator) || ((MediaValidator)optimized).isValidatingMediaInputType(encoder.getValidMediaOutputType()) :
			"MediaValidator = " + optimized.getClass().getName() + " is not validating outputType = " + encoder.getValidMediaOutputType().name();
		assert encoder.isValidatingMediaInputType(inputType);
		this.encodingContext = NullArgumentException.checkNotNull(encodingContext, "encodingContext");
		this.inputType = inputType;
		this.encoder = encoder;
		this.indentDelegate = indentDelegate;
		this.closer = closer;
	}

	public EncodingContext getEncodingContext() {
		return encodingContext;
	}

	@Override
	public MediaEncoder getEncoder() {
		return encoder;
	}

	@Override
	public void close() throws IOException {
		if(closer != null) closer.accept(out);
	}

	/**
	 * Creates a new instance of writer of the same type as the current writer.
	 *
	 * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
	 * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
	 *                         This allows the indentation to be coordinated between nested content types.
	 * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer
	 */
	abstract MediaWriter newMediaWriter(
		EncodingContext encodingContext,
		MediaType inputType,
		MediaEncoder encoder,
		Writer out,
		boolean outOptimized,
		Whitespace indentDelegate,
		IOConsumer<? super Writer> closer
	);

	MediaWriter getMediaWriter(MediaType contentType) throws UnsupportedEncodingException {
		Map<MediaType, MediaWriter> mws = mediaWriters;
		MediaWriter mediaWriter;
		if(mws == null) {
			mws = new EnumMap<>(MediaType.class);
			mediaWriters = mws;
			mediaWriter = null;
		} else {
			mediaWriter = mws.get(contentType);
		}
		if(mediaWriter == null) {
			MediaEncoder newEncoder = MediaEncoder.getInstance(encodingContext, contentType, inputType/*encoder.getValidMediaInputType()*/);
			assert this == Coercion.optimize(this, newEncoder) : "There are no CoercionOptimizer registered for MediaWritable";
			if(newEncoder == null) {
				mediaWriter = this;
			} else {
				mediaWriter = contentType.newMediaWriter(
					encodingContext,
					contentType,
					newEncoder,
					this,
					true, // There are no CoercionOptimizer registered for MediaWritable
					(indentDelegate != null) ? indentDelegate
						: (this instanceof Whitespace) ? (Whitespace)this
						: null,
					closing -> {throw new AssertionError("Never closed");}
				);
				assert contentType.getEncodeClass().isInstance(mediaWriter);
			}
			mws.put(contentType, mediaWriter);
		}
		return mediaWriter;
	}

	@Override
	public MediaType getValidMediaInputType() {
		return inputType;
	}

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
		Encode.super.encode(contentType, csq);
		return this;
	}

	@Override
	public MediaWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
		Encode.super.encode(contentType, csq, start, end);
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
		MediaWriter mw = getMediaWriter(contentType);
		assert mw.encoder.isValidatingMediaInputType(contentType);
		if(mw == this) {
			// Already in the compatible context, no prefix/suffix required
			MediaEncoder newEncoder;
			Writer newOut;
			boolean outOptimized;
			if(contentType == inputType) {
				// Use same encoder
				newEncoder = encoder;
				newOut = out;
				outOptimized = true;
			} else {
				// Require new encoder
				newEncoder = MediaEncoder.getInstance(encodingContext, contentType, inputType);
				if(newEncoder == null) {
					newEncoder = new ValidateOnlyEncoder(contentType);
				}
				newOut = this; // Must run through this level of encoding
				outOptimized = false;
			}
			if(contentType == inputType) {
				// Is compatible, no wrapping needed
				MarkupCoercion.write(
					content,
					contentType.getMarkupType(),
					false, // Should this be true?
					newEncoder,
					false,
					newOut,
					outOptimized
				);
			} else {
				// Wrap in new MediaWriter for compatibility
				MediaWriter typeWrapper = contentType.newMediaWriter(
					encodingContext,
					contentType,
					newEncoder,
					newOut,
					outOptimized,
					(indentDelegate != null) ? indentDelegate
						: (this instanceof Whitespace) ? (Whitespace)this
						: null,
					null // Ignore close: before other Coercion writes, too?
				);
				MarkupCoercion.write(
					content,
					contentType.getMarkupType(),
					typeWrapper
				);
			}
		} else {
			// Content within a different context
			assert contentType != inputType;
			assert contentType == mw.inputType;
			MarkupCoercion.write(
				content,
				contentType.getMarkupType(), // mw.encoder.getValidMediaInputType().getMarkupType()?
				false,
				mw.encoder,
				true,
				mw.out,
				true
			);
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
		MediaWriter mw = getMediaWriter(contentType);
		if(mw == this) {
			// Already in the compatible context, no prefix/suffix required
			MediaEncoder newEncoder;
			Writer newOut;
			boolean outOptimized;
			if(contentType == inputType) {
				// Use same encoder
				newEncoder = encoder;
				newOut = out;
				outOptimized = true;
			} else {
				// Require new encoder
				newEncoder = MediaEncoder.getInstance(encodingContext, contentType, inputType);
				if(newEncoder == null) {
					newEncoder = new ValidateOnlyEncoder(contentType);
				}
				newOut = this; // Must run through this level of encoding
				outOptimized = false;
			}
			newMediaWriter = contentType.newMediaWriter(
				encodingContext,
				contentType,
				newEncoder,
				newOut,
				outOptimized,
				(indentDelegate != null) ? indentDelegate
					: (this instanceof Whitespace) ? (Whitespace)this
					: null,
				null // Ignore close
			);
		} else {
			// Prefix/suffix required
			newMediaWriter = contentType.newMediaWriter(
				mw.encodingContext,
				contentType,
				mw.encoder,
				mw.out,
				true,
				(mw.indentDelegate != null) ? mw.indentDelegate
					: (mw instanceof Whitespace) ? (Whitespace)mw
					: null,
				closing -> mw.encoder.writeSuffixTo(mw.out, false)
			);
			mw.encoder.writePrefixTo(mw.out);
		}
		assert contentType.getEncodeClass().isInstance(newMediaWriter) : "Nested self-encoding is always allowed";
		return newMediaWriter;
	}
	// </editor-fold>

	// TODO: comments

}
