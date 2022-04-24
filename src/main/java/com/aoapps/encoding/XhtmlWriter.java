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
public final class XhtmlWriter extends WhitespaceWriter implements Xhtml, XhtmlAttribute, Style, JavaScript, Json, LdJson, Text, Url {

  /**
   * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
   * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
   *                         This allows the indentation to be coordinated between nested content types.
   * @param  isNoClose  Called to determine result of {@link #isNoClose()}
   * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer,
   *                 will only be called to be idempotent, implementation can assume will only be called once.
   */
  public XhtmlWriter(
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
  public XhtmlWriter(
      EncodingContext encodingContext,
      MediaEncoder encoder,
      Writer out
  ) {
    this(encodingContext, encoder, out, false, null, DEFAULT_IS_NO_CLOSE, DEFAULT_CLOSER);
  }

  @Override
  public MediaType getValidMediaInputType() {
    return MediaType.XHTML;
  }

  @Override
  public XhtmlWriter append(char c) throws IOException {
    super.append(c);
    return this;
  }

  @Override
  public XhtmlWriter append(CharSequence csq) throws IOException {
    super.append(csq);
    return this;
  }

  @Override
  public XhtmlWriter append(CharSequence csq, int start, int end) throws IOException {
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
  public XhtmlWriter encode(MediaType contentType, char ch) throws IOException {
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
  public XhtmlWriter encode(MediaType contentType, char[] cbuf) throws IOException {
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
  public XhtmlWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
  public XhtmlWriter encode(MediaType contentType, CharSequence csq) throws IOException {
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
  public XhtmlWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
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
  public XhtmlWriter encode(MediaType contentType, Object content) throws IOException {
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
  public <Ex extends Throwable> XhtmlWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
  public <Ex extends Throwable> XhtmlWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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
  public XhtmlWriter nl() throws IOException {
    super.nl();
    return this;
  }

  @Override
  public XhtmlWriter nli() throws IOException {
    super.nli();
    return this;
  }

  @Override
  public XhtmlWriter nli(int depthOffset) throws IOException {
    super.nli(depthOffset);
    return this;
  }

  @Override
  public XhtmlWriter indent() throws IOException {
    super.indent();
    return this;
  }

  @Override
  public XhtmlWriter indent(int depthOffset) throws IOException {
    super.indent(depthOffset);
    return this;
  }

  @Override
  public XhtmlWriter setIndent(boolean indent) {
    super.setIndent(indent);
    return this;
  }

  @Override
  public XhtmlWriter setDepth(int depth) {
    super.setDepth(depth);
    return this;
  }

  @Override
  public XhtmlWriter incDepth() {
    super.incDepth();
    return this;
  }

  @Override
  public XhtmlWriter decDepth() {
    super.decDepth();
    return this;
  }

  @Override
  public XhtmlWriter sp() throws IOException {
    super.sp();
    return this;
  }

  @Override
  public XhtmlWriter sp(int count) throws IOException {
    super.sp(count);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Xhtml - manual self-type and deprecate since not expected" defaultstate="collapsed">
  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing XHTML
   */
  @Deprecated
  @Override
  public XhtmlWriter xhtml(char ch) throws IOException {
    Xhtml.super.xhtml(ch);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing XHTML
   */
  @Deprecated
  @Override
  public XhtmlWriter xhtml(char[] cbuf) throws IOException {
    Xhtml.super.xhtml(cbuf);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing XHTML
   */
  @Deprecated
  @Override
  public XhtmlWriter xhtml(char[] cbuf, int offset, int len) throws IOException {
    Xhtml.super.xhtml(cbuf, offset, len);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing XHTML
   */
  @Deprecated
  @Override
  public XhtmlWriter xhtml(CharSequence csq) throws IOException {
    Xhtml.super.xhtml(csq);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing XHTML
   */
  @Deprecated
  @Override
  public XhtmlWriter xhtml(CharSequence csq, int start, int end) throws IOException {
    Xhtml.super.xhtml(csq, start, end);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing XHTML
   */
  @Deprecated
  @Override
  public XhtmlWriter xhtml(Object xhtml) throws IOException {
    Xhtml.super.xhtml(xhtml);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing XHTML
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> XhtmlWriter xhtml(IOSupplierE<?, Ex> xhtml) throws IOException, Ex {
    Xhtml.super.xhtml(xhtml);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing XHTML
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> XhtmlWriter xhtml(XhtmlWritable<Ex> xhtml) throws IOException, Ex {
    Xhtml.super.xhtml(xhtml);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing XHTML
   */
  @Deprecated
  @Override
  public XhtmlWriter xhtml() throws IOException {
    return Xhtml.super.xhtml();
  }

  // </editor-fold>

  // <editor-fold desc="XhtmlAttribute - manual self-type" defaultstate="collapsed">
  @Override
  public XhtmlWriter xhtmlAttribute(char ch) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(ch);
    return this;
  }

  @Override
  public XhtmlWriter xhtmlAttribute(char[] cbuf) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(cbuf);
    return this;
  }

  @Override
  public XhtmlWriter xhtmlAttribute(char[] cbuf, int offset, int len) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(cbuf, offset, len);
    return this;
  }

  @Override
  public XhtmlWriter xhtmlAttribute(CharSequence csq) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(csq);
    return this;
  }

  @Override
  public XhtmlWriter xhtmlAttribute(CharSequence csq, int start, int end) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(csq, start, end);
    return this;
  }

  @Override
  public XhtmlWriter xhtmlAttribute(Object xhtmlAttribute) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(xhtmlAttribute);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter xhtmlAttribute(IOSupplierE<?, Ex> xhtmlAttribute) throws IOException, Ex {
    XhtmlAttribute.super.xhtmlAttribute(xhtmlAttribute);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter xhtmlAttribute(XhtmlAttributeWritable<Ex> xhtmlAttribute) throws IOException, Ex {
    XhtmlAttribute.super.xhtmlAttribute(xhtmlAttribute);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Style - manual self-type" defaultstate="collapsed">
  @Override
  public XhtmlWriter style(char ch) throws IOException {
    Style.super.style(ch);
    return this;
  }

  @Override
  public XhtmlWriter style(char[] cbuf) throws IOException {
    Style.super.style(cbuf);
    return this;
  }

  @Override
  public XhtmlWriter style(char[] cbuf, int offset, int len) throws IOException {
    Style.super.style(cbuf, offset, len);
    return this;
  }

  @Override
  public XhtmlWriter style(CharSequence csq) throws IOException {
    Style.super.style(csq);
    return this;
  }

  @Override
  public XhtmlWriter style(CharSequence csq, int start, int end) throws IOException {
    Style.super.style(csq, start, end);
    return this;
  }

  @Override
  public XhtmlWriter style(Object style) throws IOException {
    Style.super.style(style);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter style(IOSupplierE<?, Ex> style) throws IOException, Ex {
    Style.super.style(style);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter style(StyleWritable<Ex> style) throws IOException, Ex {
    Style.super.style(style);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="JavaScript - manual self-type" defaultstate="collapsed">
  @Override
  public XhtmlWriter javascript(char ch) throws IOException {
    JavaScript.super.javascript(ch);
    return this;
  }

  @Override
  public XhtmlWriter javascript(char[] cbuf) throws IOException {
    JavaScript.super.javascript(cbuf);
    return this;
  }

  @Override
  public XhtmlWriter javascript(char[] cbuf, int offset, int len) throws IOException {
    JavaScript.super.javascript(cbuf, offset, len);
    return this;
  }

  @Override
  public XhtmlWriter javascript(CharSequence csq) throws IOException {
    JavaScript.super.javascript(csq);
    return this;
  }

  @Override
  public XhtmlWriter javascript(CharSequence csq, int start, int end) throws IOException {
    JavaScript.super.javascript(csq, start, end);
    return this;
  }

  @Override
  public XhtmlWriter javascript(Object javascript) throws IOException {
    JavaScript.super.javascript(javascript);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter javascript(IOSupplierE<?, Ex> javascript) throws IOException, Ex {
    JavaScript.super.javascript(javascript);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter javascript(JavaScriptWritable<Ex> javascript) throws IOException, Ex {
    JavaScript.super.javascript(javascript);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Json - manual self-type" defaultstate="collapsed">
  @Override
  public XhtmlWriter json(char ch) throws IOException {
    Json.super.json(ch);
    return this;
  }

  @Override
  public XhtmlWriter json(char[] cbuf) throws IOException {
    Json.super.json(cbuf);
    return this;
  }

  @Override
  public XhtmlWriter json(char[] cbuf, int offset, int len) throws IOException {
    Json.super.json(cbuf, offset, len);
    return this;
  }

  @Override
  public XhtmlWriter json(CharSequence csq) throws IOException {
    Json.super.json(csq);
    return this;
  }

  @Override
  public XhtmlWriter json(CharSequence csq, int start, int end) throws IOException {
    Json.super.json(csq, start, end);
    return this;
  }

  @Override
  public XhtmlWriter json(Object json) throws IOException {
    Json.super.json(json);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter json(IOSupplierE<?, Ex> json) throws IOException, Ex {
    Json.super.json(json);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter json(JsonWritable<Ex> json) throws IOException, Ex {
    Json.super.json(json);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="LdJson - manual self-type" defaultstate="collapsed">
  @Override
  public XhtmlWriter ldJson(char ch) throws IOException {
    LdJson.super.ldJson(ch);
    return this;
  }

  @Override
  public XhtmlWriter ldJson(char[] cbuf) throws IOException {
    LdJson.super.ldJson(cbuf);
    return this;
  }

  @Override
  public XhtmlWriter ldJson(char[] cbuf, int offset, int len) throws IOException {
    LdJson.super.ldJson(cbuf, offset, len);
    return this;
  }

  @Override
  public XhtmlWriter ldJson(CharSequence csq) throws IOException {
    LdJson.super.ldJson(csq);
    return this;
  }

  @Override
  public XhtmlWriter ldJson(CharSequence csq, int start, int end) throws IOException {
    LdJson.super.ldJson(csq, start, end);
    return this;
  }

  @Override
  public XhtmlWriter ldJson(Object ldJson) throws IOException {
    LdJson.super.ldJson(ldJson);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter ldJson(IOSupplierE<?, Ex> ldJson) throws IOException, Ex {
    LdJson.super.ldJson(ldJson);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter ldJson(LdJsonWritable<Ex> ldJson) throws IOException, Ex {
    LdJson.super.ldJson(ldJson);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Text - manual self-type" defaultstate="collapsed">
  @Override
  public XhtmlWriter nbsp() throws IOException {
    Text.super.nbsp();
    return this;
  }

  @Override
  public XhtmlWriter nbsp(int count) throws IOException {
    Text.super.nbsp(count);
    return this;
  }

  @Override
  public XhtmlWriter text(char ch) throws IOException {
    Text.super.text(ch);
    return this;
  }

  @Override
  public XhtmlWriter text(char[] cbuf) throws IOException {
    Text.super.text(cbuf);
    return this;
  }

  @Override
  public XhtmlWriter text(char[] cbuf, int offset, int len) throws IOException {
    Text.super.text(cbuf, offset, len);
    return this;
  }

  @Override
  public XhtmlWriter text(CharSequence csq) throws IOException {
    Text.super.text(csq);
    return this;
  }

  @Override
  public XhtmlWriter text(CharSequence csq, int start, int end) throws IOException {
    Text.super.text(csq, start, end);
    return this;
  }

  @Override
  public XhtmlWriter text(Object text) throws IOException {
    Text.super.text(text);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter text(IOSupplierE<?, Ex> text) throws IOException, Ex {
    Text.super.text(text);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter text(TextWritable<Ex> text) throws IOException, Ex {
    Text.super.text(text);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Url - manual self-type" defaultstate="collapsed">
  @Override
  public XhtmlWriter url(char ch) throws IOException {
    Url.super.url(ch);
    return this;
  }

  @Override
  public XhtmlWriter url(char[] cbuf) throws IOException {
    Url.super.url(cbuf);
    return this;
  }

  @Override
  public XhtmlWriter url(char[] cbuf, int offset, int len) throws IOException {
    Url.super.url(cbuf, offset, len);
    return this;
  }

  @Override
  public XhtmlWriter url(CharSequence csq) throws IOException {
    Url.super.url(csq);
    return this;
  }

  @Override
  public XhtmlWriter url(CharSequence csq, int start, int end) throws IOException {
    Url.super.url(csq, start, end);
    return this;
  }

  @Override
  public XhtmlWriter url(Object url) throws IOException {
    Url.super.url(url);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter url(IOSupplierE<?, Ex> url) throws IOException, Ex {
    Url.super.url(url);
    return this;
  }

  @Override
  public <Ex extends Throwable> XhtmlWriter url(UrlWritable<Ex> url) throws IOException, Ex {
    Url.super.url(url);
    return this;
  }
  // </editor-fold>
}
