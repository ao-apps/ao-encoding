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
public class JavaScriptWriter extends WhitespaceWriter implements JavaScript, Json, LdJson, Text, Url {

	/**
	 * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
	 * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
	 *                         This allows the indentation to be coordinated between nested content types.
	 * @param  isNoClose  Called to determine result of {@link #isNoClose()}
	 * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer,
	 *                 will only be called to be idempotent, implementation can assume will only be called once.
	 */
	public JavaScriptWriter(
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
	public JavaScriptWriter(
		EncodingContext encodingContext,
		MediaEncoder encoder,
		Writer out
	) {
		this(encodingContext, encoder, out, false, null, DEFAULT_IS_NO_CLOSE, DEFAULT_CLOSER);
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.JAVASCRIPT;
	}

	@Override
	public JavaScriptWriter append(char c) throws IOException {
		super.append(c);
		return this;
	}

	@Override
	public JavaScriptWriter append(CharSequence csq) throws IOException {
		super.append(csq);
		return this;
	}

	@Override
	public JavaScriptWriter append(CharSequence csq, int start, int end) throws IOException {
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
	public JavaScriptWriter encode(MediaType contentType, char ch) throws IOException {
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
	public JavaScriptWriter encode(MediaType contentType, char[] cbuf) throws IOException {
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
	public JavaScriptWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
	public JavaScriptWriter encode(MediaType contentType, CharSequence csq) throws IOException {
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
	public JavaScriptWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
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
	public JavaScriptWriter encode(MediaType contentType, Object content) throws IOException {
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
	public <Ex extends Throwable> JavaScriptWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
	public <Ex extends Throwable> JavaScriptWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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
	public JavaScriptWriter nl() throws IOException {
		super.nl();
		return this;
	}

	@Override
	public JavaScriptWriter nli() throws IOException {
		super.nli();
		return this;
	}

	@Override
	public JavaScriptWriter nli(int depthOffset) throws IOException {
		super.nli(depthOffset);
		return this;
	}

	@Override
	public JavaScriptWriter indent() throws IOException {
		super.indent();
		return this;
	}

	@Override
	public JavaScriptWriter indent(int depthOffset) throws IOException {
		super.indent(depthOffset);
		return this;
	}

	@Override
	public JavaScriptWriter setIndent(boolean indent) {
		super.setIndent(indent);
		return this;
	}

	@Override
	public JavaScriptWriter setDepth(int depth) {
		super.setDepth(depth);
		return this;
	}

	@Override
	public JavaScriptWriter incDepth() {
		super.incDepth();
		return this;
	}

	@Override
	public JavaScriptWriter decDepth() {
		super.decDepth();
		return this;
	}

	@Override
	public JavaScriptWriter sp() throws IOException {
		super.sp();
		return this;
	}

	@Override
	public JavaScriptWriter sp(int count) throws IOException {
		super.sp(count);
		return this;
	}
	// </editor-fold>

	// <editor-fold desc="JavaScript - manual self-type and deprecate since not expected" defaultstate="collapsed">
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter javascript(char ch) throws IOException {
		JavaScript.super.javascript(ch);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter javascript(char[] cbuf) throws IOException {
		JavaScript.super.javascript(cbuf);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter javascript(char[] cbuf, int offset, int len) throws IOException {
		JavaScript.super.javascript(cbuf, offset, len);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter javascript(CharSequence csq) throws IOException {
		JavaScript.super.javascript(csq);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter javascript(CharSequence csq, int start, int end) throws IOException {
		JavaScript.super.javascript(csq, start, end);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter javascript(Object javascript) throws IOException {
		JavaScript.super.javascript(javascript);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> JavaScriptWriter javascript(IOSupplierE<?, Ex> javascript) throws IOException, Ex {
		JavaScript.super.javascript(javascript);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> JavaScriptWriter javascript(JavaScriptWritable<Ex> javascript) throws IOException, Ex {
		JavaScript.super.javascript(javascript);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter javascript() throws IOException {
		return JavaScript.super.javascript();
	}
	// </editor-fold>

	// <editor-fold desc="Json - manual self-type and deprecate since not expected" defaultstate="collapsed">
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter json(char ch) throws IOException {
		Json.super.json(ch);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter json(char[] cbuf) throws IOException {
		Json.super.json(cbuf);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter json(char[] cbuf, int offset, int len) throws IOException {
		Json.super.json(cbuf, offset, len);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter json(CharSequence csq) throws IOException {
		Json.super.json(csq);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter json(CharSequence csq, int start, int end) throws IOException {
		Json.super.json(csq, start, end);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter json(Object json) throws IOException {
		Json.super.json(json);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> JavaScriptWriter json(IOSupplierE<?, Ex> json) throws IOException, Ex {
		Json.super.json(json);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> JavaScriptWriter json(JsonWritable<Ex> json) throws IOException, Ex {
		Json.super.json(json);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JsonWriter json() throws IOException {
		return Json.super.json();
	}
	// </editor-fold>

	// <editor-fold desc="LdJson - manual self-type and deprecate since not expected" defaultstate="collapsed">
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter ldJson(char ch) throws IOException {
		LdJson.super.ldJson(ch);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter ldJson(char[] cbuf) throws IOException {
		LdJson.super.ldJson(cbuf);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter ldJson(char[] cbuf, int offset, int len) throws IOException {
		LdJson.super.ldJson(cbuf, offset, len);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter ldJson(CharSequence csq) throws IOException {
		LdJson.super.ldJson(csq);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter ldJson(CharSequence csq, int start, int end) throws IOException {
		LdJson.super.ldJson(csq, start, end);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public JavaScriptWriter ldJson(Object ldJson) throws IOException {
		LdJson.super.ldJson(ldJson);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> JavaScriptWriter ldJson(IOSupplierE<?, Ex> ldJson) throws IOException, Ex {
		LdJson.super.ldJson(ldJson);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> JavaScriptWriter ldJson(LdJsonWritable<Ex> ldJson) throws IOException, Ex {
		LdJson.super.ldJson(ldJson);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JavaScript script
	 */
	@Deprecated
	@Override
	public LdJsonWriter ldJson() throws IOException {
		return LdJson.super.ldJson();
	}
	// </editor-fold>

	// <editor-fold desc="Text - manual self-type" defaultstate="collapsed">
	@Override
	public JavaScriptWriter nbsp() throws IOException {
		Text.super.nbsp();
		return this;
	}

	@Override
	public JavaScriptWriter nbsp(int count) throws IOException {
		Text.super.nbsp(count);
		return this;
	}

	@Override
	public JavaScriptWriter text(char ch) throws IOException {
		Text.super.text(ch);
		return this;
	}

	@Override
	public JavaScriptWriter text(char[] cbuf) throws IOException {
		Text.super.text(cbuf);
		return this;
	}

	@Override
	public JavaScriptWriter text(char[] cbuf, int offset, int len) throws IOException {
		Text.super.text(cbuf, offset, len);
		return this;
	}

	@Override
	public JavaScriptWriter text(CharSequence csq) throws IOException {
		Text.super.text(csq);
		return this;
	}

	@Override
	public JavaScriptWriter text(CharSequence csq, int start, int end) throws IOException {
		Text.super.text(csq, start, end);
		return this;
	}

	@Override
	public JavaScriptWriter text(Object text) throws IOException {
		Text.super.text(text);
		return this;
	}

	@Override
	public <Ex extends Throwable> JavaScriptWriter text(IOSupplierE<?, Ex> text) throws IOException, Ex {
		Text.super.text(text);
		return this;
	}

	@Override
	public <Ex extends Throwable> JavaScriptWriter text(TextWritable<Ex> text) throws IOException, Ex {
		Text.super.text(text);
		return this;
	}
	// </editor-fold>

	// <editor-fold desc="Url - manual self-type" defaultstate="collapsed">
	@Override
	public JavaScriptWriter url(char ch) throws IOException {
		Url.super.url(ch);
		return this;
	}

	@Override
	public JavaScriptWriter url(char[] cbuf) throws IOException {
		Url.super.url(cbuf);
		return this;
	}

	@Override
	public JavaScriptWriter url(char[] cbuf, int offset, int len) throws IOException {
		Url.super.url(cbuf, offset, len);
		return this;
	}

	@Override
	public JavaScriptWriter url(CharSequence csq) throws IOException {
		Url.super.url(csq);
		return this;
	}

	@Override
	public JavaScriptWriter url(CharSequence csq, int start, int end) throws IOException {
		Url.super.url(csq, start, end);
		return this;
	}

	@Override
	public JavaScriptWriter url(Object url) throws IOException {
		Url.super.url(url);
		return this;
	}

	@Override
	public <Ex extends Throwable> JavaScriptWriter url(IOSupplierE<?, Ex> url) throws IOException, Ex {
		Url.super.url(url);
		return this;
	}

	@Override
	public <Ex extends Throwable> JavaScriptWriter url(UrlWritable<Ex> url) throws IOException, Ex {
		Url.super.url(url);
		return this;
	}
	// </editor-fold>
}
