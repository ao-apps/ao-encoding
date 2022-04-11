/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2022  AO Industries, Inc.
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

import com.aoapps.lang.Coercion;
import com.aoapps.lang.io.function.IOConsumer;
import com.aoapps.lang.io.function.IOSupplierE;
import java.io.IOException;
import java.io.Writer;
import java.util.function.Predicate;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Streaming versions of media encoders.
 *
 * @see  MediaEncoder
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public final class UrlWriter extends MediaWriter implements Url {

	/**
	 * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
	 * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
	 *                         This allows the indentation to be coordinated between nested content types.
	 * @param  isNoClose  Called to determine result of {@link #isNoClose()}
	 * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer,
	 *                 will only be called to be idempotent, implementation can assume will only be called once.
	 */
	public UrlWriter(
		EncodingContext encodingContext,
		MediaEncoder encoder,
		Writer out,
		boolean outOptimized,
		Whitespace indentDelegate,
		Predicate<? super MediaWriter> isNoClose,
		IOConsumer<? super MediaWriter> closer
	) {
		super(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
	}

	/**
	 * Simplified constructor.
	 *
	 * @param  out  Passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 *
	 * @see  #DEFAULT_IS_NO_CLOSE
	 * @see  #DEFAULT_CLOSER
	 */
	public UrlWriter(
		EncodingContext encodingContext,
		MediaEncoder encoder,
		Writer out
	) {
		this(encodingContext, encoder, out, false, null, DEFAULT_IS_NO_CLOSE, DEFAULT_CLOSER);
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.URL;
	}

	@Override
	public UrlWriter append(char c) throws IOException {
		super.append(c);
		return this;
	}

	@Override
	public UrlWriter append(CharSequence csq) throws IOException {
		super.append(csq);
		return this;
	}

	@Override
	public UrlWriter append(CharSequence csq, int start, int end) throws IOException {
		super.append(csq, start, end);
		return this;
	}

	// <editor-fold desc="Encode - manual self-type and deprecate since not expected" defaultstate="collapsed">
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
	 */
	@Deprecated
	@Override
	public UrlWriter encode(MediaType contentType, char ch) throws IOException {
		super.encode(contentType, ch);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
	 */
	@Deprecated
	@Override
	public UrlWriter encode(MediaType contentType, char[] cbuf) throws IOException {
		super.encode(contentType, cbuf);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
	 */
	@Deprecated
	@Override
	public UrlWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
		super.encode(contentType, cbuf, offset, len);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
	 */
	@Deprecated
	@Override
	public UrlWriter encode(MediaType contentType, CharSequence csq) throws IOException {
		super.encode(contentType, csq);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
	 */
	@Deprecated
	@Override
	public UrlWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
		super.encode(contentType, csq, start, end);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
	 */
	@Deprecated
	@Override
	public UrlWriter encode(MediaType contentType, Object content) throws IOException {
		super.encode(contentType, content);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> UrlWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
		super.encode(contentType, content);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> UrlWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
		super.encode(contentType, content);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
	 */
	@Deprecated
	@Override
	public MediaWriter encode(MediaType contentType) throws IOException {
		return super.encode(contentType);
	}
	// </editor-fold>

	// <editor-fold desc="Url - manual self-type and deprecate since not expected" defaultstate="collapsed">
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing URL
	 */
	@Deprecated
	@Override
	public UrlWriter url(char ch) throws IOException {
		Url.super.url(ch);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing URL
	 */
	@Deprecated
	@Override
	public UrlWriter url(char[] cbuf) throws IOException {
		Url.super.url(cbuf);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing URL
	 */
	@Deprecated
	@Override
	public UrlWriter url(char[] cbuf, int offset, int len) throws IOException {
		Url.super.url(cbuf, offset, len);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing URL
	 */
	@Deprecated
	@Override
	public UrlWriter url(CharSequence csq) throws IOException {
		Url.super.url(csq);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing URL
	 */
	@Deprecated
	@Override
	public UrlWriter url(CharSequence csq, int start, int end) throws IOException {
		Url.super.url(csq, start, end);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing URL
	 */
	@Deprecated
	@Override
	public UrlWriter url(Object url) throws IOException {
		Url.super.url(url);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing URL
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> UrlWriter url(IOSupplierE<?, Ex> url) throws IOException, Ex {
		Url.super.url(url);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing URL
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> UrlWriter url(UrlWritable<Ex> url) throws IOException, Ex {
		Url.super.url(url);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing URL
	 */
	@Deprecated
	@Override
	public UrlWriter url() throws IOException {
		return Url.super.url();
	}
	// </editor-fold>
}
