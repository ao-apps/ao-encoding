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
public class JsonWriter extends JavaScriptWriter {

  /**
   * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
   * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
   *                         This allows the indentation to be coordinated between nested content types.
   * @param  isNoClose  Called to determine result of {@link JsonWriter#isNoClose()}
   * @param  closer  Called on {@link JsonWriter#close()}, which may optionally perform final suffix write and/or close the underlying writer,
   *                 will only be called to be idempotent, implementation can assume will only be called once.
   */
  public JsonWriter(
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
   * @see  JsonWriter#DEFAULT_IS_NO_CLOSE
   * @see  JsonWriter#DEFAULT_CLOSER
   */
  public JsonWriter(
      EncodingContext encodingContext,
      MediaEncoder encoder,
      Writer out
  ) {
    super(encodingContext, encoder, out);
  }

  @Override
  public MediaType getValidMediaInputType() {
    return MediaType.JSON;
  }

  @Override
  public JsonWriter append(char c) throws IOException {
    super.append(c);
    return this;
  }

  @Override
  public JsonWriter append(CharSequence csq) throws IOException {
    super.append(csq);
    return this;
  }

  @Override
  public JsonWriter append(CharSequence csq, int start, int end) throws IOException {
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
  public JsonWriter encode(MediaType contentType, char ch) throws IOException {
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
  public JsonWriter encode(MediaType contentType, char[] cbuf) throws IOException {
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
  public JsonWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
  public JsonWriter encode(MediaType contentType, CharSequence csq) throws IOException {
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
  public JsonWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
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
  public JsonWriter encode(MediaType contentType, Object content) throws IOException {
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
  public <Ex extends Throwable> JsonWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
  public <Ex extends Throwable> JsonWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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
  public JsonWriter nl() throws IOException {
    super.nl();
    return this;
  }

  @Override
  public JsonWriter nli() throws IOException {
    super.nli();
    return this;
  }

  @Override
  public JsonWriter nli(int depthOffset) throws IOException {
    super.nli(depthOffset);
    return this;
  }

  @Override
  public JsonWriter indent() throws IOException {
    super.indent();
    return this;
  }

  @Override
  public JsonWriter indent(int depthOffset) throws IOException {
    super.indent(depthOffset);
    return this;
  }

  @Override
  public JsonWriter setIndent(boolean indent) {
    super.setIndent(indent);
    return this;
  }

  @Override
  public JsonWriter setDepth(int depth) {
    super.setDepth(depth);
    return this;
  }

  @Override
  public JsonWriter incDepth() {
    super.incDepth();
    return this;
  }

  @Override
  public JsonWriter decDepth() {
    super.decDepth();
    return this;
  }

  @Override
  public JsonWriter sp() throws IOException {
    super.sp();
    return this;
  }

  @Override
  public JsonWriter sp(int count) throws IOException {
    super.sp(count);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="JavaScript - manual self-type and deprecate since not expected" defaultstate="collapsed">
  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter javascript(char ch) throws IOException {
    super.javascript(ch);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter javascript(char[] cbuf) throws IOException {
    super.javascript(cbuf);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter javascript(char[] cbuf, int offset, int len) throws IOException {
    super.javascript(cbuf, offset, len);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter javascript(CharSequence csq) throws IOException {
    super.javascript(csq);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter javascript(CharSequence csq, int start, int end) throws IOException {
    super.javascript(csq, start, end);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter javascript(Object javascript) throws IOException {
    super.javascript(javascript);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> JsonWriter javascript(IOSupplierE<?, Ex> javascript) throws IOException, Ex {
    super.javascript(javascript);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> JsonWriter javascript(JavaScriptWritable<Ex> javascript) throws IOException, Ex {
    super.javascript(javascript);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JavaScriptWriter javascript() throws IOException {
    return super.javascript();
  }

  // </editor-fold>

  // <editor-fold desc="Json - manual self-type and deprecate since not expected" defaultstate="collapsed">
  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter json(char ch) throws IOException {
    super.json(ch);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter json(char[] cbuf) throws IOException {
    super.json(cbuf);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter json(char[] cbuf, int offset, int len) throws IOException {
    super.json(cbuf, offset, len);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter json(CharSequence csq) throws IOException {
    super.json(csq);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter json(CharSequence csq, int start, int end) throws IOException {
    super.json(csq, start, end);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter json(Object json) throws IOException {
    super.json(json);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> JsonWriter json(IOSupplierE<?, Ex> json) throws IOException, Ex {
    super.json(json);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> JsonWriter json(JsonWritable<Ex> json) throws IOException, Ex {
    super.json(json);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter json() throws IOException {
    return super.json();
  }

  // </editor-fold>

  // <editor-fold desc="LdJson - manual self-type and deprecate since not expected" defaultstate="collapsed">
  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter ldJson(char ch) throws IOException {
    super.ldJson(ch);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter ldJson(char[] cbuf) throws IOException {
    super.ldJson(cbuf);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter ldJson(char[] cbuf, int offset, int len) throws IOException {
    super.ldJson(cbuf, offset, len);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter ldJson(CharSequence csq) throws IOException {
    super.ldJson(csq);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter ldJson(CharSequence csq, int start, int end) throws IOException {
    super.ldJson(csq, start, end);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public JsonWriter ldJson(Object ldJson) throws IOException {
    super.ldJson(ldJson);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> JsonWriter ldJson(IOSupplierE<?, Ex> ldJson) throws IOException, Ex {
    super.ldJson(ldJson);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> JsonWriter ldJson(LdJsonWritable<Ex> ldJson) throws IOException, Ex {
    super.ldJson(ldJson);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing JSON object graph
   */
  @Deprecated
  @Override
  public LdJsonWriter ldJson() throws IOException {
    return super.ldJson();
  }

  // </editor-fold>

  // <editor-fold desc="Text - manual self-type" defaultstate="collapsed">
  @Override
  public JsonWriter nbsp() throws IOException {
    super.nbsp();
    return this;
  }

  @Override
  public JsonWriter nbsp(int count) throws IOException {
    super.nbsp(count);
    return this;
  }

  @Override
  public JsonWriter text(char ch) throws IOException {
    super.text(ch);
    return this;
  }

  @Override
  public JsonWriter text(char[] cbuf) throws IOException {
    super.text(cbuf);
    return this;
  }

  @Override
  public JsonWriter text(char[] cbuf, int offset, int len) throws IOException {
    super.text(cbuf, offset, len);
    return this;
  }

  @Override
  public JsonWriter text(CharSequence csq) throws IOException {
    super.text(csq);
    return this;
  }

  @Override
  public JsonWriter text(CharSequence csq, int start, int end) throws IOException {
    super.text(csq, start, end);
    return this;
  }

  @Override
  public JsonWriter text(Object text) throws IOException {
    super.text(text);
    return this;
  }

  @Override
  public <Ex extends Throwable> JsonWriter text(IOSupplierE<?, Ex> text) throws IOException, Ex {
    super.text(text);
    return this;
  }

  @Override
  public <Ex extends Throwable> JsonWriter text(TextWritable<Ex> text) throws IOException, Ex {
    super.text(text);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Url - manual self-type" defaultstate="collapsed">
  @Override
  public JsonWriter url(char ch) throws IOException {
    super.url(ch);
    return this;
  }

  @Override
  public JsonWriter url(char[] cbuf) throws IOException {
    super.url(cbuf);
    return this;
  }

  @Override
  public JsonWriter url(char[] cbuf, int offset, int len) throws IOException {
    super.url(cbuf, offset, len);
    return this;
  }

  @Override
  public JsonWriter url(CharSequence csq) throws IOException {
    super.url(csq);
    return this;
  }

  @Override
  public JsonWriter url(CharSequence csq, int start, int end) throws IOException {
    super.url(csq, start, end);
    return this;
  }

  @Override
  public JsonWriter url(Object url) throws IOException {
    super.url(url);
    return this;
  }

  @Override
  public <Ex extends Throwable> JsonWriter url(IOSupplierE<?, Ex> url) throws IOException, Ex {
    super.url(url);
    return this;
  }

  @Override
  public <Ex extends Throwable> JsonWriter url(UrlWritable<Ex> url) throws IOException, Ex {
    super.url(url);
    return this;
  }
  // </editor-fold>
}
