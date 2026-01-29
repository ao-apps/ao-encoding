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
public final class PsqlWriter extends WhitespaceWriter implements Text, Psql {

  /**
   * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
   * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
   *                         This allows the indentation to be coordinated between nested content types.
   * @param  isNoClose  Called to determine result of {@link PsqlWriter#isNoClose()}
   * @param  closer  Called on {@link PsqlWriter#close()}, which may optionally perform final suffix write and/or close the underlying writer,
   *                 will only be called to be idempotent, implementation can assume will only be called once.
   */
  public PsqlWriter(
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
   * @see  PsqlWriter#DEFAULT_IS_NO_CLOSE
   * @see  PsqlWriter#DEFAULT_CLOSER
   */
  public PsqlWriter(
      EncodingContext encodingContext,
      MediaEncoder encoder,
      Writer out
  ) {
    this(encodingContext, encoder, out, false, null, DEFAULT_IS_NO_CLOSE, DEFAULT_CLOSER);
  }

  @Override
  public MediaType getValidMediaInputType() {
    return MediaType.PSQL;
  }

  @Override
  public PsqlWriter append(char c) throws IOException {
    super.append(c);
    return this;
  }

  @Override
  public PsqlWriter append(CharSequence csq) throws IOException {
    super.append(csq);
    return this;
  }

  @Override
  public PsqlWriter append(CharSequence csq, int start, int end) throws IOException {
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
  public PsqlWriter encode(MediaType contentType, char ch) throws IOException {
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
  public PsqlWriter encode(MediaType contentType, char[] cbuf) throws IOException {
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
  public PsqlWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
  public PsqlWriter encode(MediaType contentType, CharSequence csq) throws IOException {
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
  public PsqlWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
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
  public PsqlWriter encode(MediaType contentType, Object content) throws IOException {
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
  public <Ex extends Throwable> PsqlWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
  public <Ex extends Throwable> PsqlWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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
  public PsqlWriter nl() throws IOException {
    super.nl();
    return this;
  }

  @Override
  public PsqlWriter nli() throws IOException {
    super.nli();
    return this;
  }

  @Override
  public PsqlWriter nli(int depthOffset) throws IOException {
    super.nli(depthOffset);
    return this;
  }

  @Override
  public PsqlWriter indent() throws IOException {
    super.indent();
    return this;
  }

  @Override
  public PsqlWriter indent(int depthOffset) throws IOException {
    super.indent(depthOffset);
    return this;
  }

  @Override
  public PsqlWriter setIndent(boolean indent) {
    super.setIndent(indent);
    return this;
  }

  @Override
  public PsqlWriter setDepth(int depth) {
    super.setDepth(depth);
    return this;
  }

  @Override
  public PsqlWriter incDepth() {
    super.incDepth();
    return this;
  }

  @Override
  public PsqlWriter decDepth() {
    super.decDepth();
    return this;
  }

  @Override
  public PsqlWriter sp() throws IOException {
    super.sp();
    return this;
  }

  @Override
  public PsqlWriter sp(int count) throws IOException {
    super.sp(count);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Text - manual self-type" defaultstate="collapsed">
  @Override
  public PsqlWriter nbsp() throws IOException {
    Text.super.nbsp();
    return this;
  }

  @Override
  public PsqlWriter nbsp(int count) throws IOException {
    Text.super.nbsp(count);
    return this;
  }

  @Override
  public PsqlWriter text(char ch) throws IOException {
    Text.super.text(ch);
    return this;
  }

  @Override
  public PsqlWriter text(char[] cbuf) throws IOException {
    Text.super.text(cbuf);
    return this;
  }

  @Override
  public PsqlWriter text(char[] cbuf, int offset, int len) throws IOException {
    Text.super.text(cbuf, offset, len);
    return this;
  }

  @Override
  public PsqlWriter text(CharSequence csq) throws IOException {
    Text.super.text(csq);
    return this;
  }

  @Override
  public PsqlWriter text(CharSequence csq, int start, int end) throws IOException {
    Text.super.text(csq, start, end);
    return this;
  }

  @Override
  public PsqlWriter text(Object text) throws IOException {
    Text.super.text(text);
    return this;
  }

  @Override
  public <Ex extends Throwable> PsqlWriter text(IOSupplierE<?, Ex> text) throws IOException, Ex {
    Text.super.text(text);
    return this;
  }

  @Override
  public <Ex extends Throwable> PsqlWriter text(TextWritable<Ex> text) throws IOException, Ex {
    Text.super.text(text);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Psql - manual self-type and deprecate since not expected" defaultstate="collapsed">
  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing PostgreSQL <code>psql</code> command input
   */
  @Deprecated
  @Override
  public PsqlWriter psql(char ch) throws IOException {
    Psql.super.psql(ch);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing PostgreSQL <code>psql</code> command input
   */
  @Deprecated
  @Override
  public PsqlWriter psql(char[] cbuf) throws IOException {
    Psql.super.psql(cbuf);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing PostgreSQL <code>psql</code> command input
   */
  @Deprecated
  @Override
  public PsqlWriter psql(char[] cbuf, int offset, int len) throws IOException {
    Psql.super.psql(cbuf, offset, len);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing PostgreSQL <code>psql</code> command input
   */
  @Deprecated
  @Override
  public PsqlWriter psql(CharSequence csq) throws IOException {
    Psql.super.psql(csq);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing PostgreSQL <code>psql</code> command input
   */
  @Deprecated
  @Override
  public PsqlWriter psql(CharSequence csq, int start, int end) throws IOException {
    Psql.super.psql(csq, start, end);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing PostgreSQL <code>psql</code> command input
   */
  @Deprecated
  @Override
  public PsqlWriter psql(Object psql) throws IOException {
    Psql.super.psql(psql);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing PostgreSQL <code>psql</code> command input
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> PsqlWriter psql(IOSupplierE<?, Ex> psql) throws IOException, Ex {
    Psql.super.psql(psql);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing PostgreSQL <code>psql</code> command input
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> PsqlWriter psql(PsqlWritable<Ex> psql) throws IOException, Ex {
    Psql.super.psql(psql);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing PostgreSQL <code>psql</code> command input
   */
  @Deprecated
  @Override
  public PsqlWriter psql() throws IOException {
    return Psql.super.psql();
  }
  // </editor-fold>
}
