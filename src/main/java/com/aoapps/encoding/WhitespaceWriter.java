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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Streaming versions of media encoders that can write whitespace.
 *
 * @see  MediaEncoder
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public abstract class WhitespaceWriter extends MediaWriter implements Whitespace {

  /**
   * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
   * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
   * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
   *                         This allows the indentation to be coordinated between nested content types.
   * @param  isNoClose  Called to determine result of {@link #isNoClose()}
   * @param  closer  Called on {@link #close()}, which may optionally perform final suffix write and/or close the underlying writer,
   *                 will only be called to be idempotent, implementation can assume will only be called once.
   */
  WhitespaceWriter(
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
   * {@inheritDoc}
   *
   * @return  {@link #indentDelegate} when present, otherwise {@code this}
   */
  @Override
  Whitespace getIndentDelegate() {
    return (indentDelegate != null) ? indentDelegate : this;
  }

  @Override
  public WhitespaceWriter append(char c) throws IOException {
    super.append(c);
    return this;
  }

  @Override
  public WhitespaceWriter append(CharSequence csq) throws IOException {
    super.append(csq);
    return this;
  }

  @Override
  public WhitespaceWriter append(CharSequence csq, int start, int end) throws IOException {
    super.append(csq, start, end);
    return this;
  }

  // <editor-fold desc="Encode - manual self-type" defaultstate="collapsed">
  @Override
  public WhitespaceWriter encode(MediaType contentType, char ch) throws IOException {
    super.encode(contentType, ch);
    return this;
  }

  @Override
  public WhitespaceWriter encode(MediaType contentType, char[] cbuf) throws IOException {
    super.encode(contentType, cbuf);
    return this;
  }

  @Override
  public WhitespaceWriter encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
    super.encode(contentType, cbuf, offset, len);
    return this;
  }

  @Override
  public WhitespaceWriter encode(MediaType contentType, CharSequence csq) throws IOException {
    super.encode(contentType, csq);
    return this;
  }

  @Override
  public WhitespaceWriter encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException {
    super.encode(contentType, csq, start, end);
    return this;
  }

  @Override
  public WhitespaceWriter encode(MediaType contentType, Object content) throws IOException {
    super.encode(contentType, content);
    return this;
  }

  @Override
  public <Ex extends Throwable> WhitespaceWriter encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
    super.encode(contentType, content);
    return this;
  }

  @Override
  public <Ex extends Throwable> WhitespaceWriter encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
    super.encode(contentType, content);
    return this;
  }
  // </editor-fold>

  // <editor-fold desc="Whitespace - implementation" defaultstate="collapsed">
  /**
   * Is indenting enabled?
   */
  // Matches AnyDocument.indent
  private volatile boolean indent;

  /**
   * Current indentation level.
   */
  // Matches AnyDocument.depth
  private final AtomicInteger depth = new AtomicInteger();

  // Matches AnyDocument.nl()
  @Override
  public WhitespaceWriter nl() throws IOException {
    encoder.append(NL, out);
    return this;
  }

  // Matches AnyDocument.nli()
  @Override
  public WhitespaceWriter nli() throws IOException {
    Whitespace.super.nli();
    return this;
  }

  // Matches AnyDocument.nli(int)
  @Override
  public WhitespaceWriter nli(int depthOffset) throws IOException {
    if (getIndent()) {
      int d = getDepth();
      assert d >= 0;
      d += depthOffset;
      int spaces;
      if (d < 0) {
        // Handle underflow and overflow
        spaces = (depthOffset < 0) ? 0 : Integer.MAX_VALUE;
      } else if (d > (Integer.MAX_VALUE / INDENT_SPACES)) {
        spaces = Integer.MAX_VALUE;
      } else {
        spaces = d * INDENT_SPACES;
      }
      WriterUtil.nlsp(encoder, out, spaces);
    } else {
      encoder.append(NL, out);
    }
    return this;
  }

  // Matches AnyDocument.indent()
  @Override
  public WhitespaceWriter indent() throws IOException {
    Whitespace.super.indent();
    return this;
  }

  // Matches AnyDocument.indent(int)
  @Override
  public WhitespaceWriter indent(int depthOffset) throws IOException {
    Whitespace.super.indent(depthOffset);
    return this;
  }

  // Matches AnyDocument.getIndent()
  @Override
  public boolean getIndent() {
    return (indentDelegate != null) ? indentDelegate.getIndent() : indent;
  }

  // Matches AnyDocument.setIndent(int)
  @Override
  public WhitespaceWriter setIndent(boolean indent) {
    if (indentDelegate != null) {
      indentDelegate.setIndent(indent);
    } else {
      this.indent = indent;
    }
    return this;
  }

  // Matches AnyDocument.getDepth()
  @Override
  public int getDepth() {
    return (indentDelegate != null) ? indentDelegate.getDepth() : depth.get();
  }

  // Matches AnyDocument.setDepth(int)
  @Override
  public WhitespaceWriter setDepth(int depth) {
    if (indentDelegate != null) {
      indentDelegate.setDepth(depth);
    } else {
      if (depth < 0) {
        throw new IllegalArgumentException("depth < 0: " + depth);
      }
      this.depth.set(depth);
    }
    return this;
  }

  // Matches AnyDocument.incDepth()
  @Override
  public WhitespaceWriter incDepth() {
    if (indentDelegate != null) {
      indentDelegate.incDepth();
    } else {
      if (getIndent()) {
        int d = depth.incrementAndGet();
        if (d < 0) {
          depth.set(Integer.MAX_VALUE);
        }
      }
      assert depth.get() >= 0;
    }
    return this;
  }

  // Matches AnyDocument.decDepth()
  @Override
  public WhitespaceWriter decDepth() {
    if (indentDelegate != null) {
      indentDelegate.decDepth();
    } else {
      if (getIndent()) {
        int d = depth.decrementAndGet();
        if (d < 0) {
          depth.set(0);
        }
      }
      assert depth.get() >= 0;
    }
    return this;
  }

  // Matches AnyDocument.sp()
  @Override
  public WhitespaceWriter sp() throws IOException {
    encoder.append(SPACE, out);
    return this;
  }

  // Matches AnyDocument.sp(int)
  @Override
  public WhitespaceWriter sp(int count) throws IOException {
    WriterUtil.sp(encoder, out, count);
    return this;
  }
  // </editor-fold>
}
