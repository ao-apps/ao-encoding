/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2026  AO Industries, Inc.
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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import org.junit.Test;

@ThreadSafe
@Immutable
public class TextInShEncoderTest {

  private String encodeChar(char ch) throws IOException {
    StringBuilder sb = new StringBuilder(4);
    TextInShEncoder.encodeTextInSh(ch, sb);
    return sb.toString();
  }

  @Test
  public void testControlCharacters() throws IOException {
    for (char ch = 1; ch <= 0x1F; ch++) {
      String expected;
      switch (ch) {
        case 0x07:
          expected = "\\a";
          break;
        case 0x08:
          expected = "\\b";
          break;
        case 0x09:
          expected = "\\t";
          break;
        case 0x0A:
          expected = "\n";
          break;
        case 0x0B:
          expected = "\\v";
          break;
        case 0x0C:
          expected = "\\f";
          break;
        case 0x0D:
          expected = "\\r";
          break;
        default:
          expected = String.format("\\x%02X", (int) ch);
      }
      assertEquals(expected, encodeChar(ch));
    }
    for (char ch = 0x7F; ch <= 0x9F; ch++) {
      assertEquals(String.format("\\x%02X", (int) ch), encodeChar(ch));
    }
  }
}
