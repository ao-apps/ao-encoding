/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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

import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;

import com.aoapps.lang.Coercion;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Encode JavaScript and related formats into XHTML.  The static utility methods
 * only encode the characters.  When used as a {@link MediaWriter}, it automatically
 * adds the &lt;script&gt; tags and optionally a CDATA block.
 *
 * <p>The CDATA block is not added for JSON and LD_JSON, because JSON does not
 * support comments (insert Captain Picard facepalm pic here).  However, as the
 * JSON format will only contain &lt;, &gt;, or &amp; within quoted strings,
 * and those characters are unicode escaped, this should not present a
 * compatibility issue between HTML and XHTML.</p>
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
@Immutable
public final class JavaScriptInXhtmlEncoder extends MediaEncoder {

  // <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
  /**
   * Encodes a single character and returns its String representation
   * or null if no modification is necessary.  Any character that is
   * not valid in XHTML is encoded to JavaScript \\uxxxx escapes.
   */
  private static String getEscapedCharacter(char ch) {
    // These characters are allowed in JavaScript but need encoded for XHTML
    switch (ch) {
      // ']' Is encoded to avoid potential ]]> encoding CDATA early?
      // Imagine script with: if (array[array2[index]]>value) { ... }
      // This didn't work as hoped, just don't use "]]>" in scripts!
      // Note: TextInJavaScriptEncoder always encodes the ">", so dynamic values
      //       in JavaScript strings will never have "]]>".
      // case ']':
      //   return "\\u005d";
      // Commented-out because now using CDATA
      // case '<':
      //   return "&lt;";
      // case '>':
      //   return "&gt;";
      // case '&':
      //   return "&amp;";
      // These character ranges are passed through unmodified
      case '\r':
      case '\n':
      case '\t':
      case '\\':
        return null;
      default:
        // Escape using JavaScript unicode escape when needed.
        return JavaScriptUtil.getUnicodeEscapeString(ch);
    }
  }

  public static void encodeJavascriptInXhtml(char ch, Appendable out) throws IOException {
    assert Assertions.isValidating(out, MediaType.XHTML);
    String escaped = getEscapedCharacter(ch);
    if (escaped != null) {
      out.append(escaped);
    } else {
      out.append(ch);
    }
  }

  public static void encodeJavascriptInXhtml(char[] cbuf, Writer out) throws IOException {
    encodeJavascriptInXhtml(cbuf, 0, cbuf.length, out);
  }

  public static void encodeJavascriptInXhtml(char[] cbuf, int off, int len, Writer out) throws IOException {
    assert Assertions.isValidating(out, MediaType.XHTML);
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

  public static void encodeJavascriptInXhtml(CharSequence cs, Appendable out) throws IOException {
    if (cs != null) {
      encodeJavascriptInXhtml(cs, 0, cs.length(), out);
    } else {
      assert Assertions.isValidating(out, MediaType.XHTML);
    }
  }

  public static void encodeJavascriptInXhtml(CharSequence cs, int start, int end, Appendable out) throws IOException {
    assert Assertions.isValidating(out, MediaType.XHTML);
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

  public static void encodeJavascriptInXhtml(Object value, Appendable out) throws IOException {
    Coercion.append(value, javascriptInXhtmlEncoder, out);
  }
  // </editor-fold>

  /**
   * Singleton instance intended for static import for application/javascript.
   *
   * @deprecated  This singleton does not have any context so assumes {@link EncodingContext#DEFAULT}.
   */
  @Deprecated
  public static final JavaScriptInXhtmlEncoder javascriptInXhtmlEncoder = new JavaScriptInXhtmlEncoder(MediaType.JAVASCRIPT, EncodingContext.DEFAULT);

  /**
   * Singleton instance intended for static import for application/json.
   *
   * @deprecated  This singleton does not have any context so assumes {@link EncodingContext#DEFAULT}.
   */
  @Deprecated
  public static final JavaScriptInXhtmlEncoder jsonInXhtmlEncoder = new JavaScriptInXhtmlEncoder(MediaType.JSON, EncodingContext.DEFAULT);

  /**
   * Singleton instance intended for static import for application/ld+json.
   *
   * @deprecated  This singleton does not have any context so assumes {@link EncodingContext#DEFAULT}.
   */
  @Deprecated
  public static final JavaScriptInXhtmlEncoder ldJsonInXhtmlEncoder = new JavaScriptInXhtmlEncoder(MediaType.LD_JSON, EncodingContext.DEFAULT);

  private final MediaType contentType;
  private final EncodingContext encodingContext;

  JavaScriptInXhtmlEncoder(MediaType contentType, EncodingContext encodingContext) {
    this.contentType = contentType;
    this.encodingContext = encodingContext;
  }

  @Override
  public MediaType getValidMediaInputType() {
    return contentType;
  }

  @Override
  public boolean isValidatingMediaInputType(MediaType inputType) {
    return
        inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in JAVASCRIPT in XHTML
            || inputType == MediaType.JSON // All invalid characters in JSON are also invalid in JAVASCRIPT in XHTML
            || inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in JAVASCRIPT in XHTML
            || inputType == MediaType.TEXT; // All invalid characters in TEXT are also invalid in JAVASCRIPT in XHTML
  }

  @Override
  public boolean canSkipValidation(MediaType outputType) {
    return true; // All characters are valid in JAVASCRIPT in XHTML
  }

  @Override
  public MediaType getValidMediaOutputType() {
    return MediaType.XHTML;
  }

  @Override
  public void writePrefixTo(Appendable out) throws IOException {
    super.writePrefixTo(out);
    out.append("<script");
    boolean doCdata;
    if (contentType == MediaType.JAVASCRIPT) {
      encodingContext.getDoctype().scriptType(out);
      doCdata = encodingContext.getSerialization() == Serialization.XML;
    } else {
      out.append(" type=\"");
      encodeTextInXhtmlAttribute(contentType.getContentType(), out);
      out.append('"');
      doCdata = false;
    }
    if (doCdata) {
      out.append(">//<![CDATA[" + Whitespace.NL);
    } else {
      out.append(">" + Whitespace.NL);
    }
  }

  @Override
  public void write(int c, Writer out) throws IOException {
    encodeJavascriptInXhtml((char) c, out);
  }

  @Override
  public void write(char[] cbuf, Writer out) throws IOException {
    encodeJavascriptInXhtml(cbuf, out);
  }

  @Override
  public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
    encodeJavascriptInXhtml(cbuf, off, len, out);
  }

  @Override
  public void write(String str, Writer out) throws IOException {
    if (str == null) {
      throw new IllegalArgumentException("str is null");
    }
    encodeJavascriptInXhtml(str, out);
  }

  @Override
  public void write(String str, int off, int len, Writer out) throws IOException {
    if (str == null) {
      throw new IllegalArgumentException("str is null");
    }
    encodeJavascriptInXhtml(str, off, off + len, out);
  }

  @Override
  public JavaScriptInXhtmlEncoder append(char c, Appendable out) throws IOException {
    encodeJavascriptInXhtml(c, out);
    return this;
  }

  @Override
  public JavaScriptInXhtmlEncoder append(CharSequence csq, Appendable out) throws IOException {
    encodeJavascriptInXhtml(csq == null ? "null" : csq, out);
    return this;
  }

  @Override
  public JavaScriptInXhtmlEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
    encodeJavascriptInXhtml(csq == null ? "null" : csq, start, end, out);
    return this;
  }

  @Override
  public void writeSuffixTo(Appendable out, boolean trim) throws IOException {
    super.writeSuffixTo(out, trim);
    if (
        contentType == MediaType.JAVASCRIPT
            && encodingContext.getSerialization() == Serialization.XML
    ) {
      out.append(Whitespace.NL + "//]]></script>");
    } else {
      out.append(Whitespace.NL + "</script>");
    }
  }
}
