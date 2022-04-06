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

/**
 * Streaming versions of media encoders.
 *
 * @see  MediaEncoder
 *
 * @author  AO Industries, Inc.
 */
public final class LdJsonWriter extends WhitespaceWriter implements JavaScript, Json, LdJson, Text, Url {

	/**
	 * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
	 * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
	 *                         This allows the indentation to be coordinated between nested content types.
	 * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer
	 */
	public LdJsonWriter(
		EncodingContext encodingContext,
		MediaType inputType,
		MediaEncoder encoder,
		Writer out,
		boolean outOptimized,
		Whitespace indentDelegate,
		IOConsumer<? super Writer> closer
	) {
		super(encodingContext, inputType, encoder, out, outOptimized, indentDelegate, closer);
		assert encoder.isValidatingMediaInputType(MediaType.LD_JSON);
	}

	/**
	 * @param  out  Passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 */
	public LdJsonWriter(
		EncodingContext encodingContext,
		MediaEncoder encoder,
		Writer out
	) {
		this(encodingContext, encoder.getValidMediaInputType(), encoder, out, false, null, Writer::close);
	}

	@Override
	LdJsonWriter newMediaWriter(
		EncodingContext encodingContext,
		MediaType inputType,
		MediaEncoder encoder,
		Writer out,
		boolean outOptimized,
		Whitespace indentDelegate,
		IOConsumer<? super Writer> closer
	) {
		return new LdJsonWriter(encodingContext, inputType, encoder, out, outOptimized, indentDelegate, closer);
	}

	@Override
	public LdJsonWriter append(char c) throws IOException {
		super.append(c);
		return this;
	}

	@Override
	public LdJsonWriter append(CharSequence csq) throws IOException {
		super.append(csq);
		return this;
	}

	@Override
	public LdJsonWriter append(CharSequence csq, int start, int end) throws IOException {
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
	public LdJsonWriter encode(MediaType contentType, char ch) throws IOException {
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
	public LdJsonWriter encode(MediaType contentType, char[] cbuf) throws IOException {
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
	public LdJsonWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
	public LdJsonWriter encode(MediaType contentType, CharSequence csq) throws IOException {
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
	public LdJsonWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
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
	public LdJsonWriter encode(MediaType contentType, Object content) throws IOException {
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
	public <Ex extends Throwable> LdJsonWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
	public <Ex extends Throwable> LdJsonWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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
	public LdJsonWriter nl() throws IOException {
		super.nl();
		return this;
	}

	@Override
	public LdJsonWriter nli() throws IOException {
		super.nli();
		return this;
	}

	@Override
	public LdJsonWriter nli(int depthOffset) throws IOException {
		super.nli(depthOffset);
		return this;
	}

	@Override
	public LdJsonWriter indent() throws IOException {
		super.indent();
		return this;
	}

	@Override
	public LdJsonWriter indent(int depthOffset) throws IOException {
		super.indent(depthOffset);
		return this;
	}

	@Override
	public LdJsonWriter setIndent(boolean indent) {
		super.setIndent(indent);
		return this;
	}

	@Override
	public LdJsonWriter setDepth(int depth) {
		super.setDepth(depth);
		return this;
	}

	@Override
	public LdJsonWriter incDepth() {
		super.incDepth();
		return this;
	}

	@Override
	public LdJsonWriter decDepth() {
		super.decDepth();
		return this;
	}

	@Override
	public LdJsonWriter sp() throws IOException {
		super.sp();
		return this;
	}

	@Override
	public LdJsonWriter sp(int count) throws IOException {
		super.sp(count);
		return this;
	}
	// </editor-fold>

	// <editor-fold desc="JavaScript - manual self-type and deprecate since not expected" defaultstate="collapsed">
	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter javascript(char ch) throws IOException {
		JavaScript.super.javascript(ch);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter javascript(char[] cbuf) throws IOException {
		JavaScript.super.javascript(cbuf);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter javascript(char[] cbuf, int offset, int len) throws IOException {
		JavaScript.super.javascript(cbuf, offset, len);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter javascript(CharSequence csq) throws IOException {
		JavaScript.super.javascript(csq);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter javascript(CharSequence csq, int start, int end) throws IOException {
		JavaScript.super.javascript(csq, start, end);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter javascript(Object javascript) throws IOException {
		JavaScript.super.javascript(javascript);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> LdJsonWriter javascript(IOSupplierE<?, Ex> javascript) throws IOException, Ex {
		JavaScript.super.javascript(javascript);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> LdJsonWriter javascript(JavaScriptWritable<Ex> javascript) throws IOException, Ex {
		JavaScript.super.javascript(javascript);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
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
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter json(char ch) throws IOException {
		Json.super.json(ch);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter json(char[] cbuf) throws IOException {
		Json.super.json(cbuf);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter json(char[] cbuf, int offset, int len) throws IOException {
		Json.super.json(cbuf, offset, len);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter json(CharSequence csq) throws IOException {
		Json.super.json(csq);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter json(CharSequence csq, int start, int end) throws IOException {
		Json.super.json(csq, start, end);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter json(Object json) throws IOException {
		Json.super.json(json);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> LdJsonWriter json(IOSupplierE<?, Ex> json) throws IOException, Ex {
		Json.super.json(json);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> LdJsonWriter json(JsonWritable<Ex> json) throws IOException, Ex {
		Json.super.json(json);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
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
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter ldJson(char ch) throws IOException {
		LdJson.super.ldJson(ch);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter ldJson(char[] cbuf) throws IOException {
		LdJson.super.ldJson(cbuf);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter ldJson(char[] cbuf, int offset, int len) throws IOException {
		LdJson.super.ldJson(cbuf, offset, len);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter ldJson(CharSequence csq) throws IOException {
		LdJson.super.ldJson(csq);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter ldJson(CharSequence csq, int start, int end) throws IOException {
		LdJson.super.ldJson(csq, start, end);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter ldJson(Object ldJson) throws IOException {
		LdJson.super.ldJson(ldJson);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> LdJsonWriter ldJson(IOSupplierE<?, Ex> ldJson) throws IOException, Ex {
		LdJson.super.ldJson(ldJson);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public <Ex extends Throwable> LdJsonWriter ldJson(LdJsonWritable<Ex> ldJson) throws IOException, Ex {
		LdJson.super.ldJson(ldJson);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated  Already writing JSON linked data
	 */
	@Deprecated
	@Override
	public LdJsonWriter ldJson() throws IOException {
		return LdJson.super.ldJson();
	}
	// </editor-fold>

	// <editor-fold desc="Text - manual self-type" defaultstate="collapsed">
	@Override
	public LdJsonWriter nbsp() throws IOException {
		Text.super.nbsp();
		return this;
	}

	@Override
	public LdJsonWriter nbsp(int count) throws IOException {
		Text.super.nbsp(count);
		return this;
	}

	@Override
	public LdJsonWriter text(char ch) throws IOException {
		Text.super.text(ch);
		return this;
	}

	@Override
	public LdJsonWriter text(char[] cbuf) throws IOException {
		Text.super.text(cbuf);
		return this;
	}

	@Override
	public LdJsonWriter text(char[] cbuf, int offset, int len) throws IOException {
		Text.super.text(cbuf, offset, len);
		return this;
	}

	@Override
	public LdJsonWriter text(CharSequence csq) throws IOException {
		Text.super.text(csq);
		return this;
	}

	@Override
	public LdJsonWriter text(CharSequence csq, int start, int end) throws IOException {
		Text.super.text(csq, start, end);
		return this;
	}

	@Override
	public LdJsonWriter text(Object text) throws IOException {
		Text.super.text(text);
		return this;
	}

	@Override
	public <Ex extends Throwable> LdJsonWriter text(IOSupplierE<?, Ex> text) throws IOException, Ex {
		Text.super.text(text);
		return this;
	}

	@Override
	public <Ex extends Throwable> LdJsonWriter text(TextWritable<Ex> text) throws IOException, Ex {
		Text.super.text(text);
		return this;
	}
	// </editor-fold>

	// <editor-fold desc="Url - manual self-type" defaultstate="collapsed">
	@Override
	public LdJsonWriter url(char ch) throws IOException {
		Url.super.url(ch);
		return this;
	}

	@Override
	public LdJsonWriter url(char[] cbuf) throws IOException {
		Url.super.url(cbuf);
		return this;
	}

	@Override
	public LdJsonWriter url(char[] cbuf, int offset, int len) throws IOException {
		Url.super.url(cbuf, offset, len);
		return this;
	}

	@Override
	public LdJsonWriter url(CharSequence csq) throws IOException {
		Url.super.url(csq);
		return this;
	}

	@Override
	public LdJsonWriter url(CharSequence csq, int start, int end) throws IOException {
		Url.super.url(csq, start, end);
		return this;
	}

	@Override
	public LdJsonWriter url(Object url) throws IOException {
		Url.super.url(url);
		return this;
	}

	@Override
	public <Ex extends Throwable> LdJsonWriter url(IOSupplierE<?, Ex> url) throws IOException, Ex {
		Url.super.url(url);
		return this;
	}

	@Override
	public <Ex extends Throwable> LdJsonWriter url(UrlWritable<Ex> url) throws IOException, Ex {
		Url.super.url(url);
		return this;
	}
	// </editor-fold>
}
