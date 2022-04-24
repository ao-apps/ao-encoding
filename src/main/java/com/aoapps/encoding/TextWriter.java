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
public final class TextWriter extends WhitespaceWriter implements Xhtml, XhtmlAttribute, Style, JavaScript, Json, LdJson, Text, Url, Sh, Mysql, Psql {

  /**
   * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
   * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
   *                         This allows the indentation to be coordinated between nested content types.
   * @param  isNoClose  Called to determine result of {@link #isNoClose()}
   * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer,
   *                 will only be called to be idempotent, implementation can assume will only be called once.
   */
  public TextWriter(
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
  public TextWriter(
      EncodingContext encodingContext,
      MediaEncoder encoder,
      Writer out
  ) {
    this(encodingContext, encoder, out, false, null, DEFAULT_IS_NO_CLOSE, DEFAULT_CLOSER);
  }

  @Override
  public MediaType getValidMediaInputType() {
    return MediaType.TEXT;
  }

  @Override
  public TextWriter append(char c) throws IOException {
    super.append(c);
    return this;
  }

  @Override
  public TextWriter append(CharSequence csq) throws IOException {
    super.append(csq);
    return this;
  }

  @Override
  public TextWriter append(CharSequence csq, int start, int end) throws IOException {
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
  public TextWriter encode(MediaType contentType, char ch) throws IOException {
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
  public TextWriter encode(MediaType contentType, char[] cbuf) throws IOException {
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
  public TextWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
  public TextWriter encode(MediaType contentType, CharSequence csq) throws IOException {
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
  public TextWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
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
  public TextWriter encode(MediaType contentType, Object content) throws IOException {
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
  public <Ex extends Throwable> TextWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
  public <Ex extends Throwable> TextWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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
  public TextWriter nl() throws IOException {
    super.nl();
    return this;
  }

  @Override
  public TextWriter nli() throws IOException {
    super.nli();
    return this;
  }

  @Override
  public TextWriter nli(int depthOffset) throws IOException {
    super.nli(depthOffset);
    return this;
  }

  @Override
  public TextWriter indent() throws IOException {
    super.indent();
    return this;
  }

  @Override
  public TextWriter indent(int depthOffset) throws IOException {
    super.indent(depthOffset);
    return this;
  }

  @Override
  public TextWriter setIndent(boolean indent) {
    super.setIndent(indent);
    return this;
  }

  @Override
  public TextWriter setDepth(int depth) {
    super.setDepth(depth);
    return this;
  }

  @Override
  public TextWriter incDepth() {
    super.incDepth();
    return this;
  }

  @Override
  public TextWriter decDepth() {
    super.decDepth();
    return this;
  }

  @Override
  public TextWriter sp() throws IOException {
    super.sp();
    return this;
  }

  @Override
  public TextWriter sp(int count) throws IOException {
    super.sp(count);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Xhtml - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter xhtml(char ch) throws IOException {
    Xhtml.super.xhtml(ch);
    return this;
  }

  @Override
  public TextWriter xhtml(char[] cbuf) throws IOException {
    Xhtml.super.xhtml(cbuf);
    return this;
  }

  @Override
  public TextWriter xhtml(char[] cbuf, int offset, int len) throws IOException {
    Xhtml.super.xhtml(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter xhtml(CharSequence csq) throws IOException {
    Xhtml.super.xhtml(csq);
    return this;
  }

  @Override
  public TextWriter xhtml(CharSequence csq, int start, int end) throws IOException {
    Xhtml.super.xhtml(csq, start, end);
    return this;
  }

  @Override
  public TextWriter xhtml(Object xhtml) throws IOException {
    Xhtml.super.xhtml(xhtml);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter xhtml(IOSupplierE<?, Ex> xhtml) throws IOException, Ex {
    Xhtml.super.xhtml(xhtml);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter xhtml(XhtmlWritable<Ex> xhtml) throws IOException, Ex {
    Xhtml.super.xhtml(xhtml);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="XhtmlAttribute - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter xhtmlAttribute(char ch) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(ch);
    return this;
  }

  @Override
  public TextWriter xhtmlAttribute(char[] cbuf) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(cbuf);
    return this;
  }

  @Override
  public TextWriter xhtmlAttribute(char[] cbuf, int offset, int len) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter xhtmlAttribute(CharSequence csq) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(csq);
    return this;
  }

  @Override
  public TextWriter xhtmlAttribute(CharSequence csq, int start, int end) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(csq, start, end);
    return this;
  }

  @Override
  public TextWriter xhtmlAttribute(Object xhtmlAttribute) throws IOException {
    XhtmlAttribute.super.xhtmlAttribute(xhtmlAttribute);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter xhtmlAttribute(IOSupplierE<?, Ex> xhtmlAttribute) throws IOException, Ex {
    XhtmlAttribute.super.xhtmlAttribute(xhtmlAttribute);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter xhtmlAttribute(XhtmlAttributeWritable<Ex> xhtmlAttribute) throws IOException, Ex {
    XhtmlAttribute.super.xhtmlAttribute(xhtmlAttribute);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Style - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter style(char ch) throws IOException {
    Style.super.style(ch);
    return this;
  }

  @Override
  public TextWriter style(char[] cbuf) throws IOException {
    Style.super.style(cbuf);
    return this;
  }

  @Override
  public TextWriter style(char[] cbuf, int offset, int len) throws IOException {
    Style.super.style(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter style(CharSequence csq) throws IOException {
    Style.super.style(csq);
    return this;
  }

  @Override
  public TextWriter style(CharSequence csq, int start, int end) throws IOException {
    Style.super.style(csq, start, end);
    return this;
  }

  @Override
  public TextWriter style(Object style) throws IOException {
    Style.super.style(style);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter style(IOSupplierE<?, Ex> style) throws IOException, Ex {
    Style.super.style(style);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter style(StyleWritable<Ex> style) throws IOException, Ex {
    Style.super.style(style);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="JavaScript - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter javascript(char ch) throws IOException {
    JavaScript.super.javascript(ch);
    return this;
  }

  @Override
  public TextWriter javascript(char[] cbuf) throws IOException {
    JavaScript.super.javascript(cbuf);
    return this;
  }

  @Override
  public TextWriter javascript(char[] cbuf, int offset, int len) throws IOException {
    JavaScript.super.javascript(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter javascript(CharSequence csq) throws IOException {
    JavaScript.super.javascript(csq);
    return this;
  }

  @Override
  public TextWriter javascript(CharSequence csq, int start, int end) throws IOException {
    JavaScript.super.javascript(csq, start, end);
    return this;
  }

  @Override
  public TextWriter javascript(Object javascript) throws IOException {
    JavaScript.super.javascript(javascript);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter javascript(IOSupplierE<?, Ex> javascript) throws IOException, Ex {
    JavaScript.super.javascript(javascript);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter javascript(JavaScriptWritable<Ex> javascript) throws IOException, Ex {
    JavaScript.super.javascript(javascript);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Json - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter json(char ch) throws IOException {
    Json.super.json(ch);
    return this;
  }

  @Override
  public TextWriter json(char[] cbuf) throws IOException {
    Json.super.json(cbuf);
    return this;
  }

  @Override
  public TextWriter json(char[] cbuf, int offset, int len) throws IOException {
    Json.super.json(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter json(CharSequence csq) throws IOException {
    Json.super.json(csq);
    return this;
  }

  @Override
  public TextWriter json(CharSequence csq, int start, int end) throws IOException {
    Json.super.json(csq, start, end);
    return this;
  }

  @Override
  public TextWriter json(Object json) throws IOException {
    Json.super.json(json);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter json(IOSupplierE<?, Ex> json) throws IOException, Ex {
    Json.super.json(json);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter json(JsonWritable<Ex> json) throws IOException, Ex {
    Json.super.json(json);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="LdJson - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter ldJson(char ch) throws IOException {
    LdJson.super.ldJson(ch);
    return this;
  }

  @Override
  public TextWriter ldJson(char[] cbuf) throws IOException {
    LdJson.super.ldJson(cbuf);
    return this;
  }

  @Override
  public TextWriter ldJson(char[] cbuf, int offset, int len) throws IOException {
    LdJson.super.ldJson(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter ldJson(CharSequence csq) throws IOException {
    LdJson.super.ldJson(csq);
    return this;
  }

  @Override
  public TextWriter ldJson(CharSequence csq, int start, int end) throws IOException {
    LdJson.super.ldJson(csq, start, end);
    return this;
  }

  @Override
  public TextWriter ldJson(Object ldJson) throws IOException {
    LdJson.super.ldJson(ldJson);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter ldJson(IOSupplierE<?, Ex> ldJson) throws IOException, Ex {
    LdJson.super.ldJson(ldJson);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter ldJson(LdJsonWritable<Ex> ldJson) throws IOException, Ex {
    LdJson.super.ldJson(ldJson);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Text - manual self-type and deprecate since not expected" defaultstate="collapsed">
  @Override
  public TextWriter nbsp() throws IOException {
    Text.super.nbsp();
    return this;
  }

  @Override
  public TextWriter nbsp(int count) throws IOException {
    Text.super.nbsp(count);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing text
   */
  @Deprecated
  @Override
  public TextWriter text(char ch) throws IOException {
    Text.super.text(ch);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing text
   */
  @Deprecated
  @Override
  public TextWriter text(char[] cbuf) throws IOException {
    Text.super.text(cbuf);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing text
   */
  @Deprecated
  @Override
  public TextWriter text(char[] cbuf, int offset, int len) throws IOException {
    Text.super.text(cbuf, offset, len);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing text
   */
  @Deprecated
  @Override
  public TextWriter text(CharSequence csq) throws IOException {
    Text.super.text(csq);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing text
   */
  @Deprecated
  @Override
  public TextWriter text(CharSequence csq, int start, int end) throws IOException {
    Text.super.text(csq, start, end);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing text
   */
  @Deprecated
  @Override
  public TextWriter text(Object text) throws IOException {
    Text.super.text(text);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing text
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> TextWriter text(IOSupplierE<?, Ex> text) throws IOException, Ex {
    Text.super.text(text);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing text
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> TextWriter text(TextWritable<Ex> text) throws IOException, Ex {
    Text.super.text(text);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing text
   */
  @Deprecated
  @Override
  public TextWriter text() throws IOException {
    return Text.super.text();
  }

  // </editor-fold>

  // <editor-fold desc="Url - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter url(char ch) throws IOException {
    Url.super.url(ch);
    return this;
  }

  @Override
  public TextWriter url(char[] cbuf) throws IOException {
    Url.super.url(cbuf);
    return this;
  }

  @Override
  public TextWriter url(char[] cbuf, int offset, int len) throws IOException {
    Url.super.url(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter url(CharSequence csq) throws IOException {
    Url.super.url(csq);
    return this;
  }

  @Override
  public TextWriter url(CharSequence csq, int start, int end) throws IOException {
    Url.super.url(csq, start, end);
    return this;
  }

  @Override
  public TextWriter url(Object url) throws IOException {
    Url.super.url(url);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter url(IOSupplierE<?, Ex> url) throws IOException, Ex {
    Url.super.url(url);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter url(UrlWritable<Ex> url) throws IOException, Ex {
    Url.super.url(url);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Sh - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter sh(char ch) throws IOException {
    Sh.super.sh(ch);
    return this;
  }

  @Override
  public TextWriter sh(char[] cbuf) throws IOException {
    Sh.super.sh(cbuf);
    return this;
  }

  @Override
  public TextWriter sh(char[] cbuf, int offset, int len) throws IOException {
    Sh.super.sh(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter sh(CharSequence csq) throws IOException {
    Sh.super.sh(csq);
    return this;
  }

  @Override
  public TextWriter sh(CharSequence csq, int start, int end) throws IOException {
    Sh.super.sh(csq, start, end);
    return this;
  }

  @Override
  public TextWriter sh(Object sh) throws IOException {
    Sh.super.sh(sh);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter sh(IOSupplierE<?, Ex> sh) throws IOException, Ex {
    Sh.super.sh(sh);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter sh(ShWritable<Ex> sh) throws IOException, Ex {
    Sh.super.sh(sh);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Mysql - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter mysql(char ch) throws IOException {
    Mysql.super.mysql(ch);
    return this;
  }

  @Override
  public TextWriter mysql(char[] cbuf) throws IOException {
    Mysql.super.mysql(cbuf);
    return this;
  }

  @Override
  public TextWriter mysql(char[] cbuf, int offset, int len) throws IOException {
    Mysql.super.mysql(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter mysql(CharSequence csq) throws IOException {
    Mysql.super.mysql(csq);
    return this;
  }

  @Override
  public TextWriter mysql(CharSequence csq, int start, int end) throws IOException {
    Mysql.super.mysql(csq, start, end);
    return this;
  }

  @Override
  public TextWriter mysql(Object mysql) throws IOException {
    Mysql.super.mysql(mysql);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter mysql(IOSupplierE<?, Ex> mysql) throws IOException, Ex {
    Mysql.super.mysql(mysql);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter mysql(MysqlWritable<Ex> mysql) throws IOException, Ex {
    Mysql.super.mysql(mysql);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Psql - manual self-type" defaultstate="collapsed">
  @Override
  public TextWriter psql(char ch) throws IOException {
    Psql.super.psql(ch);
    return this;
  }

  @Override
  public TextWriter psql(char[] cbuf) throws IOException {
    Psql.super.psql(cbuf);
    return this;
  }

  @Override
  public TextWriter psql(char[] cbuf, int offset, int len) throws IOException {
    Psql.super.psql(cbuf, offset, len);
    return this;
  }

  @Override
  public TextWriter psql(CharSequence csq) throws IOException {
    Psql.super.psql(csq);
    return this;
  }

  @Override
  public TextWriter psql(CharSequence csq, int start, int end) throws IOException {
    Psql.super.psql(csq, start, end);
    return this;
  }

  @Override
  public TextWriter psql(Object psql) throws IOException {
    Psql.super.psql(psql);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter psql(IOSupplierE<?, Ex> psql) throws IOException, Ex {
    Psql.super.psql(psql);
    return this;
  }

  @Override
  public <Ex extends Throwable> TextWriter psql(PsqlWritable<Ex> psql) throws IOException, Ex {
    Psql.super.psql(psql);
    return this;
  }
  // </editor-fold>
}
