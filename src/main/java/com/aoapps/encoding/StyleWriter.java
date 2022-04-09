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

/**
 * Streaming versions of media encoders.
 *
 * @see  MediaEncoder
 *
 * @author  AO Industries, Inc.
 */
public final class StyleWriter extends WhitespaceWriter implements Style, Text, Url {

	/**
	 * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
	 * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
	 *                         This allows the indentation to be coordinated between nested content types.
	 * @param  isNoClose  Called to determine result of {@link #isNoClose()}
	 * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer,
	 *                 will only be called to be idempotent, implementation can assume will only be called once.
	 */
	public StyleWriter(
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
	public StyleWriter(
		EncodingContext encodingContext,
		MediaEncoder encoder,
		Writer out
	) {
		this(encodingContext, encoder, out, false, null, DEFAULT_IS_NO_CLOSE, DEFAULT_CLOSER);
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.CSS;
	}

	@Override
	public StyleWriter append(char c) throws IOException {
		super.append(c);
		return this;
	}

	@Override
	public StyleWriter append(CharSequence csq) throws IOException {
		super.append(csq);
		return this;
	}

	@Override
	public StyleWriter append(CharSequence csq, int start, int end) throws IOException {
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
	public StyleWriter encode(MediaType contentType, char ch) throws IOException {
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
	public StyleWriter encode(MediaType contentType, char[] cbuf) throws IOException {
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
	public StyleWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
	public StyleWriter encode(MediaType contentType, CharSequence csq) throws IOException {
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
	public StyleWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
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
	public StyleWriter encode(MediaType contentType, Object content) throws IOException {
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
	public <Ex extends Throwable> StyleWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
	public <Ex extends Throwable> StyleWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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

	// <editor-fold desc="Whitespace - manual self-type" defaultstate="collapsed">
	@Override
	public StyleWriter nl() throws IOException {
		super.nl();
		return this;
	}

	@Override
	public StyleWriter nli() throws IOException {
		super.nli();
		return this;
	}

	@Override
	public StyleWriter nli(int depthOffset) throws IOException {
		super.nli(depthOffset);
		return this;
	}

	@Override
	public StyleWriter indent() throws IOException {
		super.indent();
		return this;
	}

	@Override
	public StyleWriter indent(int depthOffset) throws IOException {
		super.indent(depthOffset);
		return this;
	}

	@Override
	public StyleWriter setIndent(boolean indent) {
		super.setIndent(indent);
		return this;
	}

	@Override
	public StyleWriter setDepth(int depth) {
		super.setDepth(depth);
		return this;
	}

	@Override
	public StyleWriter incDepth() {
		super.incDepth();
		return this;
	}

	@Override
	public StyleWriter decDepth() {
		super.decDepth();
		return this;
	}

	@Override
	public StyleWriter sp() throws IOException {
		super.sp();
		return this;
	}

	@Override
	public StyleWriter sp(int count) throws IOException {
		super.sp(count);
		return this;
	}
	// </editor-fold>

	// <editor-fold desc="Style - manual self-type and deprecate since not expected" defaultstate="collapsed">
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing CSS stylesheet
	 */
	@Deprecated
	@Override
	public StyleWriter style(char ch) throws IOException {
		Style.super.style(ch);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing CSS stylesheet
	 */
	@Deprecated
	@Override
	public StyleWriter style(char[] cbuf) throws IOException {
		Style.super.style(cbuf);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing CSS stylesheet
	 */
	@Deprecated
	@Override
	public StyleWriter style(char[] cbuf, int offset, int len) throws IOException {
		Style.super.style(cbuf, offset, len);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing CSS stylesheet
	 */
	@Deprecated
	@Override
	public StyleWriter style(CharSequence csq) throws IOException {
		Style.super.style(csq);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing CSS stylesheet
	 */
	@Deprecated
	@Override
	public StyleWriter style(CharSequence csq, int start, int end) throws IOException {
		Style.super.style(csq, start, end);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing CSS stylesheet
	 */
	@Deprecated
	@Override
	public StyleWriter style(Object style) throws IOException {
		Style.super.style(style);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing CSS stylesheet
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> StyleWriter style(IOSupplierE<?, Ex> style) throws IOException, Ex {
		Style.super.style(style);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing CSS stylesheet
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> StyleWriter style(StyleWritable<Ex> style) throws IOException, Ex {
		Style.super.style(style);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing CSS stylesheet
	 */
	@Deprecated
	@Override
	public StyleWriter style() throws IOException {
		return Style.super.style();
	}
	// </editor-fold>

	// <editor-fold desc="Text - manual self-type" defaultstate="collapsed">
	@Override
	public StyleWriter nbsp() throws IOException {
		Text.super.nbsp();
		return this;
	}

	@Override
	public StyleWriter nbsp(int count) throws IOException {
		Text.super.nbsp(count);
		return this;
	}

	@Override
	public StyleWriter text(char ch) throws IOException {
		Text.super.text(ch);
		return this;
	}

	@Override
	public StyleWriter text(char[] cbuf) throws IOException {
		Text.super.text(cbuf);
		return this;
	}

	@Override
	public StyleWriter text(char[] cbuf, int offset, int len) throws IOException {
		Text.super.text(cbuf, offset, len);
		return this;
	}

	@Override
	public StyleWriter text(CharSequence csq) throws IOException {
		Text.super.text(csq);
		return this;
	}

	@Override
	public StyleWriter text(CharSequence csq, int start, int end) throws IOException {
		Text.super.text(csq, start, end);
		return this;
	}

	@Override
	public StyleWriter text(Object text) throws IOException {
		Text.super.text(text);
		return this;
	}

	@Override
	public <Ex extends Throwable> StyleWriter text(IOSupplierE<?, Ex> text) throws IOException, Ex {
		Text.super.text(text);
		return this;
	}

	@Override
	public <Ex extends Throwable> StyleWriter text(TextWritable<Ex> text) throws IOException, Ex {
		Text.super.text(text);
		return this;
	}
	// </editor-fold>

	// <editor-fold desc="Url - manual self-type" defaultstate="collapsed">
	@Override
	public StyleWriter url(char ch) throws IOException {
		Url.super.url(ch);
		return this;
	}

	@Override
	public StyleWriter url(char[] cbuf) throws IOException {
		Url.super.url(cbuf);
		return this;
	}

	@Override
	public StyleWriter url(char[] cbuf, int offset, int len) throws IOException {
		Url.super.url(cbuf, offset, len);
		return this;
	}

	@Override
	public StyleWriter url(CharSequence csq) throws IOException {
		Url.super.url(csq);
		return this;
	}

	@Override
	public StyleWriter url(CharSequence csq, int start, int end) throws IOException {
		Url.super.url(csq, start, end);
		return this;
	}

	@Override
	public StyleWriter url(Object url) throws IOException {
		Url.super.url(url);
		return this;
	}

	@Override
	public <Ex extends Throwable> StyleWriter url(IOSupplierE<?, Ex> url) throws IOException, Ex {
		Url.super.url(url);
		return this;
	}

	@Override
	public <Ex extends Throwable> StyleWriter url(UrlWritable<Ex> url) throws IOException, Ex {
		Url.super.url(url);
		return this;
	}
	// </editor-fold>
}
