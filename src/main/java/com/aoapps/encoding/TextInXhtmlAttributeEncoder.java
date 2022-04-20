/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2013, 2015, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * Encodes arbitrary text into an XHTML attribute.  Minimal conversion is performed, just
 * encoding of necessary values and throwing an IOException when any character
 * is found that cannot be converted to XHTML entities.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
@Immutable
public final class TextInXhtmlAttributeEncoder extends MediaEncoder {

  // <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
  /**
   * Encodes a single character and returns its String representation
   * or null if no modification is necessary.
   *
   * @see XhtmlMediaValidator#checkCharacter(char)
   *
   * @throws  InvalidCharacterException  if any text character cannot be converted to XHTML attribute
   */
  private static String getEscapedCharacter(char c) throws InvalidCharacterException {
    switch (c) {
      case '<': return "&lt;";
      case '>': return "&gt;";
      case '&': return "&amp;";
      case '\'': return "&#39;";
      case '"': return "&quot;";
      case '\t': return "&#x9;";
      case '\r': return "&#xD;";
      case '\n': return "&#xA;";
      // case ' ': return null;
      default:
        XhtmlAttributeValidator.checkCharacter(c);
        return null;
    }
  }

  /**
   * See <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>.
   */
  public static void encodeTextInXhtmlAttribute(char ch, Appendable out) throws IOException {
    assert Assertions.isValidating(out, MediaType.XHTML_ATTRIBUTE);
    String escaped = getEscapedCharacter(ch);
    if (escaped != null) {
      out.append(escaped);
    } else {
      out.append(ch);
    }
  }

  /**
   * See <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>.
   */
  public static void encodeTextInXhtmlAttribute(char[] cbuf, Writer out) throws IOException {
    encodeTextInXhtmlAttribute(cbuf, 0, cbuf.length, out);
  }

  /**
   * See <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>.
   */
  public static void encodeTextInXhtmlAttribute(char[] cbuf, int off, int len, Writer out) throws IOException {
    assert Assertions.isValidating(out, MediaType.XHTML_ATTRIBUTE);
    int end = off + len;
    int toPrint = 0;
    for (int c = off; c < end; c++) {
      String escaped = getEscapedCharacter(cbuf[c]);
      if (escaped != null) {
        if (toPrint>0) {
          out.write(cbuf, c-toPrint, toPrint);
          toPrint=0;
        }
        out.write(escaped);
      } else {
        toPrint++;
      }
    }
    if (toPrint>0) {
      out.write(cbuf, end-toPrint, toPrint);
    }
  }

  /**
   * See <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>.
   */
  public static void encodeTextInXhtmlAttribute(CharSequence cs, Appendable out) throws IOException {
    if (cs != null) {
      encodeTextInXhtmlAttribute(cs, 0, cs.length(), out);
    } else {
      assert Assertions.isValidating(out, MediaType.XHTML_ATTRIBUTE);
    }
  }

  /**
   * See <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>.
   */
  public static void encodeTextInXhtmlAttribute(CharSequence cs, int start, int end, Appendable out) throws IOException {
    assert Assertions.isValidating(out, MediaType.XHTML_ATTRIBUTE);
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
   * See <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>.
   */
  public static void encodeTextInXhtmlAttribute(Object value, Appendable out) throws IOException {
    Coercion.append(value, textInXhtmlAttributeEncoder, out);
  }
  // </editor-fold>

  /**
   * Singleton instance intended for static import.
   */
  public static final TextInXhtmlAttributeEncoder textInXhtmlAttributeEncoder = new TextInXhtmlAttributeEncoder();

  private TextInXhtmlAttributeEncoder() {
    // Do nothing
  }

  @Override
  public MediaType getValidMediaInputType() {
    return MediaType.TEXT;
  }

  @Override
  public boolean isValidatingMediaInputType(MediaType inputType) {
    return
      inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in TEXT in XHTML_ATTRIBUTE
      || inputType == MediaType.JSON // All invalid characters in JSON are also invalid in TEXT in XHTML_ATTRIBUTE
      || inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in TEXT in XHTML_ATTRIBUTE
      || inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in TEXT in XHTML_ATTRIBUTE
      || inputType == MediaType.XHTML // All invalid characters in XHTML are also invalid in TEXT in XHTML_ATTRIBUTE
    ;
  }

  @Override
  public boolean canSkipValidation(MediaType outputType) {
    return
      outputType == MediaType.CSS // All valid characters in CSS are also valid in TEXT in XHTML_ATTRIBUTE
      || outputType == MediaType.MYSQL // All valid characters in MYSQL are also valid in TEXT in XHTML_ATTRIBUTE
      || outputType == MediaType.PSQL // All valid characters in PSQL are also valid in TEXT in XHTML_ATTRIBUTE
      || outputType == MediaType.SH // All valid characters in SH are also valid in TEXT in XHTML_ATTRIBUTE
      || outputType == MediaType.XHTML // All valid characters in XHTML are also valid in TEXT in XHTML_ATTRIBUTE
      || outputType == MediaType.XHTML_ATTRIBUTE // All valid characters in XHTML_ATTRIBUTE are also valid in TEXT in XHTML_ATTRIBUTE
    ;
  }

  @Override
  public MediaType getValidMediaOutputType() {
    return MediaType.XHTML_ATTRIBUTE;
  }

  @Override
  public void write(int c, Writer out) throws IOException {
    encodeTextInXhtmlAttribute((char)c, out);
  }

  @Override
  public void write(char[] cbuf, Writer out) throws IOException {
    encodeTextInXhtmlAttribute(cbuf, out);
  }

  @Override
  public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
    encodeTextInXhtmlAttribute(cbuf, off, len, out);
  }

  @Override
  public void write(String str, Writer out) throws IOException {
    if (str == null) {
      throw new IllegalArgumentException("str is null");
    }
    encodeTextInXhtmlAttribute(str, out);
  }

  @Override
  public void write(String str, int off, int len, Writer out) throws IOException {
    if (str == null) {
      throw new IllegalArgumentException("str is null");
    }
    encodeTextInXhtmlAttribute(str, off, off+len, out);
  }

  @Override
  public TextInXhtmlAttributeEncoder append(char c, Appendable out) throws IOException {
    encodeTextInXhtmlAttribute(c, out);
    return this;
  }

  @Override
  public TextInXhtmlAttributeEncoder append(CharSequence csq, Appendable out) throws IOException {
    encodeTextInXhtmlAttribute(csq == null ? "null" : csq, out);
    return this;
  }

  @Override
  public TextInXhtmlAttributeEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
    encodeTextInXhtmlAttribute(csq == null ? "null" : csq, start, end, out);
    return this;
  }
}
