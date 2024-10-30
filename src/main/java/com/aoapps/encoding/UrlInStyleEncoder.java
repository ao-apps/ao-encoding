/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2022, 2024  AO Industries, Inc.
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Encodes a URL into CSS, using {@link EncodingContext#encodeURL(java.lang.String)}
 * to rewrite the URL as needed and surrounds it in <code>url("…")</code>.
 *
 * <p>See <a href="https://www.w3.org/TR/CSS2/syndata.html#uri">4.3.4 URLs and URIs</a>.</p>
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public class UrlInStyleEncoder extends BufferedEncoder {

  private final EncodingContext encodingContext;

  UrlInStyleEncoder(EncodingContext encodingContext) {
    super(128);
    this.encodingContext = encodingContext;
  }

  @Override
  public MediaType getValidMediaInputType() {
    return MediaType.URL;
  }

  @Override
  public boolean isValidatingMediaInputType(MediaType inputType) {
    return
        inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in URL in CSS
            || inputType == MediaType.JSON // All invalid characters in JSON are also invalid in URL in CSS
            || inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in URL in CSS
            || inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in URL in CSS
            || inputType == MediaType.URL; // All invalid characters in URL are also invalid in URL in CSS
  }

  @Override
  public boolean canSkipValidation(MediaType outputType) {
    return
        outputType == MediaType.URL; // All valid characters in URL are also valid in URL in CSS
  }

  @Override
  public MediaType getValidMediaOutputType() {
    return MediaType.CSS;
  }

  @Override
  public void writePrefixTo(Appendable out) throws IOException {
    super.writePrefixTo(out);
    out.append("url(\"");
  }

  private static final char BEGIN_ENCODE = '\u0080';
  private static final char END_ENCODE = '\u009F';
  /**
   * Precomputed escapes for the expected case of encoding matching {@link EncodingContext#DEFAULT}.
   */
  private static final String[] DEFAULT_ESCAPES = new String[END_ENCODE + 1 - BEGIN_ENCODE];

  static {
    String charsetName = EncodingContext.DEFAULT.getCharacterEncoding().name();
    try {
      for (char ch = BEGIN_ENCODE; ch <= END_ENCODE; ch++) {
        DEFAULT_ESCAPES[ch - BEGIN_ENCODE] = URLEncoder.encode(Character.toString(ch), charsetName);
      }
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError("Default encoding must be supported on all platforms: " + charsetName, e);
    }
  }

  @Override
  @SuppressWarnings({"StringEquality", "AssignmentToForLoopParameter"})
  protected void writeSuffix(CharSequence buffer, Appendable out) throws IOException {
    String url = buffer.toString();
    int len = url.length();
    UrlValidator.checkCharacters(url, 0, len);
    String encoded;
    if (encodingContext != null) {
      encoded = encodingContext.encodeURL(url);
      if (encoded != url) {
        UrlValidator.checkCharacters(encoded, 0, encoded.length());
      }
    } else {
      encoded = url;
    }
    // CSS does not support \u0080 through \u009F, \uFFFE, or \uFFFF
    for (int i = 0; i < len; i++) {
      char ch = encoded.charAt(i);
      if (
          (ch >= BEGIN_ENCODE && ch <= END_ENCODE)
              || ch == '\uFFFE'
              || ch == '\uFFFF'
      ) {
        StringBuilder sb = new StringBuilder(len + (len - i) * 2); // Enough room should all additional characters need to be %HH encoded
        sb.append(encoded, 0, i);
        Charset charset = (encodingContext == null ? EncodingContext.DEFAULT : encodingContext).getCharacterEncoding();
        String charsetName = charset.name();
        String fffeEscape;
        String ffffEscape;
        String[] escapes;
        if (charset == EncodingContext.DEFAULT.getCharacterEncoding()) {
          // Use precomputed for default charset
          fffeEscape = UrlInXhtmlEncoder.DEFAULT_FFFE;
          ffffEscape = UrlInXhtmlEncoder.DEFAULT_FFFF;
          escapes = DEFAULT_ESCAPES;
        } else {
          // Escapes computed once each for current non-default charset
          fffeEscape = null;
          ffffEscape = null;
          escapes = new String[END_ENCODE + 1 - BEGIN_ENCODE];
        }
        while (true) {
          if (ch >= BEGIN_ENCODE && ch <= END_ENCODE) {
            int escapeIndex = ch - BEGIN_ENCODE;
            String escape = escapes[escapeIndex];
            if (escape == null) {
              assert charset != EncodingContext.DEFAULT.getCharacterEncoding();
              assert escapes != DEFAULT_ESCAPES;
              escape = URLEncoder.encode(Character.toString(ch), charsetName);
              escapes[escapeIndex] = escape;
            }
            sb.append(escape);
          } else if (ch == '\uFFFE') {
            if (fffeEscape == null) {
              fffeEscape = URLEncoder.encode("\uFFFE", charsetName);
            }
            sb.append(fffeEscape);
          } else if (ch == '\uFFFF') {
            if (ffffEscape == null) {
              ffffEscape = URLEncoder.encode("\uFFFF", charsetName);
            }
            sb.append(ffffEscape);
          } else {
            sb.append(ch);
          }
          i++;
          if (i < len) {
            ch = encoded.charAt(i);
          } else {
            break;
          }
        }
        assert sb.length() > encoded.length();
        String newUrl = sb.toString();
        assert URLDecoder.decode(encoded, charsetName).equals(URLDecoder.decode(newUrl, charsetName));
        encoded = newUrl;
        break;
      }
    }
    TextInStyleEncoder.encodeTextInStyle(encoded, out);
    out.append("\")");
  }
}
