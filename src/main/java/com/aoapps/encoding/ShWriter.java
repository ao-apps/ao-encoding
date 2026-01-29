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
public final class ShWriter extends WhitespaceWriter implements Text, Sh {

  /**
   * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
   * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
   *                         This allows the indentation to be coordinated between nested content types.
   * @param  isNoClose  Called to determine result of {@link ShWriter#isNoClose()}
   * @param  closer  Called on {@link ShWriter#close()}, which may optionally perform final suffix write and/or close the underlying writer,
   *                 will only be called to be idempotent, implementation can assume will only be called once.
   */
  public ShWriter(
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
   * @see  ShWriter#DEFAULT_IS_NO_CLOSE
   * @see  ShWriter#DEFAULT_CLOSER
   */
  public ShWriter(
      EncodingContext encodingContext,
      MediaEncoder encoder,
      Writer out
  ) {
    this(encodingContext, encoder, out, false, null, DEFAULT_IS_NO_CLOSE, DEFAULT_CLOSER);
  }

  @Override
  public MediaType getValidMediaInputType() {
    return MediaType.SH;
  }

  @Override
  public ShWriter append(char c) throws IOException {
    super.append(c);
    return this;
  }

  @Override
  public ShWriter append(CharSequence csq) throws IOException {
    super.append(csq);
    return this;
  }

  @Override
  public ShWriter append(CharSequence csq, int start, int end) throws IOException {
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
  public ShWriter encode(MediaType contentType, char ch) throws IOException {
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
  public ShWriter encode(MediaType contentType, char[] cbuf) throws IOException {
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
  public ShWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
  public ShWriter encode(MediaType contentType, CharSequence csq) throws IOException {
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
  public ShWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
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
  public ShWriter encode(MediaType contentType, Object content) throws IOException {
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
  public <Ex extends Throwable> ShWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
  public <Ex extends Throwable> ShWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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
  public ShWriter nl() throws IOException {
    super.nl();
    return this;
  }

  @Override
  public ShWriter nli() throws IOException {
    super.nli();
    return this;
  }

  @Override
  public ShWriter nli(int depthOffset) throws IOException {
    super.nli(depthOffset);
    return this;
  }

  @Override
  public ShWriter indent() throws IOException {
    super.indent();
    return this;
  }

  @Override
  public ShWriter indent(int depthOffset) throws IOException {
    super.indent(depthOffset);
    return this;
  }

  @Override
  public ShWriter setIndent(boolean indent) {
    super.setIndent(indent);
    return this;
  }

  @Override
  public ShWriter setDepth(int depth) {
    super.setDepth(depth);
    return this;
  }

  @Override
  public ShWriter incDepth() {
    super.incDepth();
    return this;
  }

  @Override
  public ShWriter decDepth() {
    super.decDepth();
    return this;
  }

  @Override
  public ShWriter sp() throws IOException {
    super.sp();
    return this;
  }

  @Override
  public ShWriter sp(int count) throws IOException {
    super.sp(count);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Text - manual self-type" defaultstate="collapsed">
  @Override
  public ShWriter nbsp() throws IOException {
    Text.super.nbsp();
    return this;
  }

  @Override
  public ShWriter nbsp(int count) throws IOException {
    Text.super.nbsp(count);
    return this;
  }

  @Override
  public ShWriter text(char ch) throws IOException {
    Text.super.text(ch);
    return this;
  }

  @Override
  public ShWriter text(char[] cbuf) throws IOException {
    Text.super.text(cbuf);
    return this;
  }

  @Override
  public ShWriter text(char[] cbuf, int offset, int len) throws IOException {
    Text.super.text(cbuf, offset, len);
    return this;
  }

  @Override
  public ShWriter text(CharSequence csq) throws IOException {
    Text.super.text(csq);
    return this;
  }

  @Override
  public ShWriter text(CharSequence csq, int start, int end) throws IOException {
    Text.super.text(csq, start, end);
    return this;
  }

  @Override
  public ShWriter text(Object text) throws IOException {
    Text.super.text(text);
    return this;
  }

  @Override
  public <Ex extends Throwable> ShWriter text(IOSupplierE<?, Ex> text) throws IOException, Ex {
    Text.super.text(text);
    return this;
  }

  @Override
  public <Ex extends Throwable> ShWriter text(TextWritable<Ex> text) throws IOException, Ex {
    Text.super.text(text);
    return this;
  }

  // </editor-fold>

  // <editor-fold desc="Sh - manual self-type and deprecate since not expected" defaultstate="collapsed">
  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing Bourne shell script
   */
  @Deprecated
  @Override
  public ShWriter sh(char ch) throws IOException {
    Sh.super.sh(ch);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing Bourne shell script
   */
  @Deprecated
  @Override
  public ShWriter sh(char[] cbuf) throws IOException {
    Sh.super.sh(cbuf);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing Bourne shell script
   */
  @Deprecated
  @Override
  public ShWriter sh(char[] cbuf, int offset, int len) throws IOException {
    Sh.super.sh(cbuf, offset, len);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing Bourne shell script
   */
  @Deprecated
  @Override
  public ShWriter sh(CharSequence csq) throws IOException {
    Sh.super.sh(csq);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing Bourne shell script
   */
  @Deprecated
  @Override
  public ShWriter sh(CharSequence csq, int start, int end) throws IOException {
    Sh.super.sh(csq, start, end);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing Bourne shell script
   */
  @Deprecated
  @Override
  public ShWriter sh(Object sh) throws IOException {
    Sh.super.sh(sh);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing Bourne shell script
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> ShWriter sh(IOSupplierE<?, Ex> sh) throws IOException, Ex {
    Sh.super.sh(sh);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing Bourne shell script
   */
  @Deprecated
  @Override
  public <Ex extends Throwable> ShWriter sh(ShWritable<Ex> sh) throws IOException, Ex {
    Sh.super.sh(sh);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Already writing Bourne shell script
   */
  @Deprecated
  @Override
  public ShWriter sh() throws IOException {
    return Sh.super.sh();
  }
  // </editor-fold>
}
