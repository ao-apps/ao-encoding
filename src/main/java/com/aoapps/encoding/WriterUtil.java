/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.io.Encoder;
import com.aoapps.lang.util.BufferManager;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Helpers for implementing {@link Whitespace}.
 *
 * @see  Whitespace
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
@Immutable
public final class WriterUtil {

  /** Make no instances. */
  private WriterUtil() {
    throw new AssertionError();
  }

  /**
   * The number of characters written per block.
   */
  public static final int BLOCK_SIZE = BufferManager.BUFFER_SIZE;

  /**
   * {@link Whitespace#NL} combined with {@link Whitespace#SPACE} characters.
   */
  public static final String NL_SPACE_CHARS;
  static {
    char[] ch = new char[BLOCK_SIZE];
    ch[0] = Whitespace.NL;
    Arrays.fill(ch, 1, BLOCK_SIZE, Whitespace.SPACE);
    NL_SPACE_CHARS = new String(ch);
  }

  /**
   * {@link Whitespace#SPACE} characters.
   */
  public static final String SPACE_CHARS;
  static {
    char[] ch = new char[BLOCK_SIZE];
    Arrays.fill(ch, 0, BLOCK_SIZE, Whitespace.SPACE);
    SPACE_CHARS = new String(ch);
  }

  /**
   * {@link Text#NBSP} characters.
   */
  public static final String NBSP_CHARS;
  static {
    char[] ch = new char[BLOCK_SIZE];
    Arrays.fill(ch, 0, BLOCK_SIZE, Text.NBSP);
    NBSP_CHARS = new String(ch);
  }

  /**
   * Writes a {@link Whitespace#NL} followed by any number of {@link Whitespace#SPACE}.
   *
   * @see Whitespace#nli(int)
   */
  public static void nlsp(Writer out, int count) throws IOException {
    if (count > 0) {
      count += 1; // Add one for the initial newline
      if (count < 0) {
        // Handle overflow
        count = Integer.MAX_VALUE;
      }
      int block = Math.min(count, BLOCK_SIZE);
      assert block > 1;
      out.write(NL_SPACE_CHARS, 0, block);
      count -= block;
      assert count >= 0;
      if (count > 0) {
        sp(out, count);
      }
    } else {
      out.append(Whitespace.NL);
    }
  }

  /**
   * Writes a {@link Whitespace#NL} followed by any number of {@link Whitespace#SPACE}
   * through the given encoder.
   *
   * @see Whitespace#nli(int)
   */
  public static void nlsp(Encoder encoder, Writer out, int count) throws IOException {
    if (count > 0) {
      count += 1; // Add one for the initial newline
      if (count < 0) {
        // Handle overflow
        count = Integer.MAX_VALUE;
      }
      int block = Math.min(count, BLOCK_SIZE);
      assert block > 1;
      encoder.write(NL_SPACE_CHARS, 0, block, out);
      count -= block;
      assert count >= 0;
      if (count > 0) {
        sp(encoder, out, count);
      }
    } else {
      encoder.append(Whitespace.NL, out);
    }
  }

  /**
   * Writes any number of {@link Whitespace#SPACE}.
   *
   * @see Whitespace#sp(int)
   */
  public static void sp(Writer out, int count) throws IOException {
    while (count > 0) {
      if (count == 1) {
        out.append(Whitespace.SPACE);
        break;
      } else {
        int block = Math.min(count, BLOCK_SIZE);
        out.write(SPACE_CHARS, 0, block);
        count -= block;
        assert count >= 0;
      }
    }
  }

  /**
   * Writes any number of {@link Whitespace#SPACE}
   * through the given encoder.
   *
   * @see Whitespace#sp(int)
   */
  public static void sp(Encoder encoder, Writer out, int count) throws IOException {
    while (count > 0) {
      if (count == 1) {
        encoder.append(Whitespace.SPACE, out);
        break;
      } else {
        int block = Math.min(count, BLOCK_SIZE);
        encoder.write(SPACE_CHARS, 0, block, out);
        count -= block;
        assert count >= 0;
      }
    }
  }

  /**
   * Writes any number of {@link Text#NBSP}.
   *
   * @see Text#nbsp(int)
   */
  public static void nbsp(Writer out, int count) throws IOException {
    while (count > 0) {
      if (count == 1) {
        out.append(Text.NBSP);
        break;
      } else {
        int block = Math.min(count, BLOCK_SIZE);
        out.write(NBSP_CHARS, 0, block);
        count -= block;
        assert count >= 0;
      }
    }
  }

  /**
   * Writes any number of {@link Text#NBSP}
   * through the given encoder.
   *
   * @see Text#nbsp(int)
   */
  public static void nbsp(Encoder encoder, Writer out, int count) throws IOException {
    while (count > 0) {
      if (count == 1) {
        encoder.append(Text.NBSP, out);
        break;
      } else {
        int block = Math.min(count, BLOCK_SIZE);
        encoder.write(NBSP_CHARS, 0, block, out);
        count -= block;
        assert count >= 0;
      }
    }
  }
}
