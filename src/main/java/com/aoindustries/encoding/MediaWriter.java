/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2013, 2015, 2016, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.exception.WrappedException;
import com.aoindustries.io.EncoderWriter;
import com.aoindustries.lang.NullArgumentException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Streaming versions of media encoders.
 * 
 * @see  MediaEncoder
 *
 * @author  AO Industries, Inc.
 */
public class MediaWriter extends EncoderWriter implements ValidMediaFilter {

	private final EncodingContext encodingContext;
	private final MediaEncoder encoder;

	private MediaWriter textWriter;

	public MediaWriter(EncodingContext encodingContext, MediaEncoder encoder, Writer out) {
		super(encoder, out);
		this.encodingContext = NullArgumentException.checkNotNull(encodingContext, "encodingContext");
		this.encoder = encoder;
	}

	public EncodingContext getEncodingContext() {
		return encodingContext;
	}

	@Override
	public MediaEncoder getEncoder() {
		return encoder;
	}

	private MediaWriter getTextWriter() throws UnsupportedEncodingException {
		if(textWriter == null) {
			MediaEncoder textEncoder = MediaEncoder.getInstance(encodingContext, MediaType.TEXT, encoder.getValidMediaInputType());
			textWriter = (textEncoder == null) ? this : new MediaWriter(encodingContext, textEncoder, this);
		}
		return textWriter;
	}

	@Override
	public MediaType getValidMediaInputType() {
		return encoder.getValidMediaInputType();
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return encoder.isValidatingMediaInputType(inputType);
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return encoder.canSkipValidation(inputType);
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

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * Does not perform any translation markups.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	public MediaWriter text(char ch) throws IOException {
		MediaWriter tw = getTextWriter();
		if(tw != this) textWriter.encoder.writePrefixTo(this);
		tw.append(ch);
		if(tw != this) textWriter.encoder.writeSuffixTo(this);
		return this;
	}

	// TODO: codePoint?

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * Does not perform any translation markups.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	public MediaWriter text(char[] cbuf) throws IOException {
		MediaWriter tw = getTextWriter();
		if(tw != this) textWriter.encoder.writePrefixTo(this);
		tw.write(cbuf);
		if(tw != this) textWriter.encoder.writeSuffixTo(this);
		return this;
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * Does not perform any translation markups.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	public MediaWriter text(char[] cbuf, int offset, int len) throws IOException {
		MediaWriter tw = getTextWriter();
		if(tw != this) textWriter.encoder.writePrefixTo(this);
		tw.write(cbuf, offset, len);
		if(tw != this) textWriter.encoder.writeSuffixTo(this);
		return this;
	}

	// TODO: text(CharSequence)?
	// TODO: text(CharSequence, int, int)?

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * If the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	public MediaWriter text(Object text) throws IOException {
		while(text instanceof Supplier<?,?>) {
			try {
				text = ((Supplier<?,?>)text).get();
			} catch(Error|RuntimeException|IOException e) {
				throw e;
			} catch(Throwable t) {
				throw new WrappedException(t);
			}
		}
		if(text instanceof char[]) {
			return text((char[])text);
		}
		if(text instanceof MediaWritable) {
			try {
				return text((MediaWritable<?>)text);
			} catch(Error|RuntimeException|IOException e) {
				throw e;
			} catch(Throwable t) {
				throw new WrappedException(t);
			}
		}
		// Allow text markup from translations
		MediaWriter tw = getTextWriter();
		if(tw == this) {
			// Already in a textual context
			Coercion.write(
				text,
				MediaType.TEXT.getMarkupType(),
				encoder,
				false,
				out
			);
		} else {
			// Text within a non-textual context
			Coercion.write(
				text,
				encoder.getValidMediaInputType().getMarkupType(),
				tw.encoder,
				true,
				this
			);
		}
		return this;
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * If the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	public <Ex extends Throwable> MediaWriter text(Supplier<?,Ex> text) throws IOException, Ex {
		return text((text == null) ? null : text.get());
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * Does not perform any translation markups.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	public <Ex extends Throwable> MediaWriter text(MediaWritable<Ex> text) throws IOException, Ex {
		try (MediaWriter tw = text()) {
			if(text != null) {
				text.writeTo(tw);
			}
		}
		return this;
	}

	/**
	 * Writes the given text with proper encoding.
	 * This is well suited for use in a try-with-resources block.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * Does not perform any translation markups.
	 * </p>
	 *
	 * @return  A new writer that may be used for arbitrary text.
	 *          This writer must be closed for completed calls to {@link MediaEncoder#writeSuffixTo(java.lang.Appendable)}.
	 */
	public MediaWriter text() throws IOException {
		MediaWriter tw = getTextWriter();
		if(tw != this) textWriter.encoder.writePrefixTo(this);
		return new MediaWriter(
			tw.encodingContext,
			tw.encoder,
			tw.out
		) {
			@Override
			public void close() throws IOException {
				if(tw != this) textWriter.encoder.writeSuffixTo(this);
			}
		};
	}

	// TODO: A set of out() methods that take MediaType and value

	// TODO: comments

	/**
	 * This is {@code '\n'} on all platforms.  If a different newline is required,
	 * such as {@code "\r\n"} for email, filter the output.
	 */
	// TODO: Is nl() appropriate here?
	public MediaWriter nl() throws IOException {
		write('\n');
		return this;
	}
}
