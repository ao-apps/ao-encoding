/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import java.io.IOException;
import java.io.Writer;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Encodes arbitrary text for use as <code>mysql</code> command input.
 * This implementation is based on <a href="https://dev.mysql.com/doc/en/string-literals.html#character-escape-sequences">Table 9.1 Special Character Escape Sequences</a>.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
@Immutable
public final class TextInMysqlEncoder extends MediaEncoder {

  // <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
  /**
   * Encodes a single character and returns its String representation
   * or null if no modification is necessary.  Implemented as
   * <a href="https://dev.mysql.com/doc/en/string-literals.html#character-escape-sequences">Table 9.1 Special Character Escape Sequences</a>.
   *
   * @see MysqlValidator#checkCharacter(char)
   *
   * @throws  InvalidCharacterException  if any text character cannot be converted for use as mysql command input
   */
  private static String getEscapedCharacter(char c) throws InvalidCharacterException {
    switch (c) {
      // Not needed inside single quotes overall:
      // case '"':
      //   return "\\\"";
      case '\0':
        return "\\0";
      case '\'':
        return "''";
      case '\b':
        return "\\b";
      case '\n':
        return null;
      case '\r':
        return "\\r";
      case '\t':
        return "\\t";
      case 26:
        return "\\Z";
      case '\\':
        return "\\\\";
      default:
        if (
            (c >= 0x20 && c <= 0x7E) // common case first
                || (c >= 0xA0 && c <= 0xFFFD)
        ) {
          return null;
        }
        throw new InvalidCharacterException(MysqlValidator.RESOURCES, "invalidCharacter", Integer.toHexString(c));
    }
  }

  /**
   * See <a href="https://dev.mysql.com/doc/en/string-literals.html#character-escape-sequences">Table 9.1 Special Character Escape Sequences</a>.
   */
  public static void encodeTextInMysql(char ch, Appendable out) throws IOException {
    assert Assertions.isValidating(out, MediaType.MYSQL);
    String escaped = getEscapedCharacter(ch);
    if (escaped != null) {
      out.append(escaped);
    } else {
      out.append(ch);
    }
  }

  /**
   * See <a href="https://dev.mysql.com/doc/en/string-literals.html#character-escape-sequences">Table 9.1 Special Character Escape Sequences</a>.
   */
  public static void encodeTextInMysql(char[] cbuf, Writer out) throws IOException {
    encodeTextInMysql(cbuf, 0, cbuf.length, out);
  }

  /**
   * See <a href="https://dev.mysql.com/doc/en/string-literals.html#character-escape-sequences">Table 9.1 Special Character Escape Sequences</a>.
   */
  public static void encodeTextInMysql(char[] cbuf, int off, int len, Writer out) throws IOException {
    assert Assertions.isValidating(out, MediaType.MYSQL);
    int end = off + len;
    int toPrint = 0;
    for (int c = off; c < end; c++) {
      String escaped = getEscapedCharacter(cbuf[c]);
      if (escaped != null) {
        if (toPrint > 0) {
          out.write(cbuf, c - toPrint, toPrint);
          toPrint = 0;
        }
        out.write(escaped);
      } else {
        toPrint++;
      }
    }
    if (toPrint > 0) {
      out.write(cbuf, end - toPrint, toPrint);
    }
  }

  /**
   * See <a href="https://dev.mysql.com/doc/en/string-literals.html#character-escape-sequences">Table 9.1 Special Character Escape Sequences</a>.
   */
  public static void encodeTextInMysql(CharSequence cs, Appendable out) throws IOException {
    if (cs != null) {
      encodeTextInMysql(cs, 0, cs.length(), out);
    } else {
      assert Assertions.isValidating(out, MediaType.MYSQL);
    }
  }

  /**
   * See <a href="https://dev.mysql.com/doc/en/string-literals.html#character-escape-sequences">Table 9.1 Special Character Escape Sequences</a>.
   */
  public static void encodeTextInMysql(CharSequence cs, int start, int end, Appendable out) throws IOException {
    assert Assertions.isValidating(out, MediaType.MYSQL);
    if (cs != null) {
      int toPrint = 0;
      for (int c = start; c < end; c++) {
        String escaped = getEscapedCharacter(cs.charAt(c));
        if (escaped != null) {
          if (toPrint > 0) {
            out.append(cs, c - toPrint, c);
            toPrint = 0;
          }
          out.append(escaped);
        } else {
          toPrint++;
        }
      }
      if (toPrint > 0) {
        out.append(cs, end - toPrint, end);
      }
    }
  }

  /**
   * See <a href="https://dev.mysql.com/doc/en/string-literals.html#character-escape-sequences">Table 9.1 Special Character Escape Sequences</a>.
   */
  public static void encodeTextInMysql(Object value, Appendable out) throws IOException {
    Coercion.append(value, textInMysqlEncoder, out);
  }
  // </editor-fold>

  /**
   * Singleton instance intended for static import.
   */
  public static final TextInMysqlEncoder textInMysqlEncoder = new TextInMysqlEncoder();

  private TextInMysqlEncoder() {
    // Do nothing
  }

  @Override
  public MediaType getValidMediaInputType() {
    return MediaType.TEXT;
  }

  @Override
  public boolean isValidatingMediaInputType(MediaType inputType) {
    return
        inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in TEXT in MYSQL
            || inputType == MediaType.JSON // All invalid characters in JSON are also invalid in TEXT in MYSQL
            || inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in TEXT in MYSQL
            || inputType == MediaType.TEXT; // All invalid characters in TEXT are also invalid in TEXT in MYSQL
  }

  @Override
  public boolean canSkipValidation(MediaType outputType) {
    return
        outputType == MediaType.CSS // All valid characters in CSS are also valid in TEXT in MYSQL
            || outputType == MediaType.MYSQL // All valid characters in MYSQL are also valid in TEXT in MYSQL
            || outputType == MediaType.PSQL // All valid characters in PSQL are also valid in TEXT in MYSQL
            || outputType == MediaType.SH; // All valid characters in SH are also valid in TEXT in MYSQL
  }

  @Override
  public MediaType getValidMediaOutputType() {
    return MediaType.MYSQL;
  }

  @Override
  public void writePrefixTo(Appendable out) throws IOException {
    super.writePrefixTo(out);
    out.append("E'");
  }

  @Override
  public void write(int c, Writer out) throws IOException {
    encodeTextInMysql((char) c, out);
  }

  @Override
  public void write(char[] cbuf, Writer out) throws IOException {
    encodeTextInMysql(cbuf, out);
  }

  @Override
  public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
    encodeTextInMysql(cbuf, off, len, out);
  }

  @Override
  public void write(String str, Writer out) throws IOException {
    if (str == null) {
      throw new IllegalArgumentException("str is null");
    }
    encodeTextInMysql(str, out);
  }

  @Override
  public void write(String str, int off, int len, Writer out) throws IOException {
    if (str == null) {
      throw new IllegalArgumentException("str is null");
    }
    encodeTextInMysql(str, off, off + len, out);
  }

  @Override
  public TextInMysqlEncoder append(char c, Appendable out) throws IOException {
    encodeTextInMysql(c, out);
    return this;
  }

  @Override
  public TextInMysqlEncoder append(CharSequence csq, Appendable out) throws IOException {
    encodeTextInMysql(csq == null ? "null" : csq, out);
    return this;
  }

  @Override
  public TextInMysqlEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
    encodeTextInMysql(csq == null ? "null" : csq, start, end, out);
    return this;
  }

  @Override
  public void writeSuffixTo(Appendable out, boolean trim) throws IOException {
    super.writeSuffixTo(out, trim);
    out.append('\'');
  }
}
