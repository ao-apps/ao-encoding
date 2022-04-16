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
import java.io.IOException;
import java.io.Writer;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Encodes arbitrary text for use as a CSS string.
 * <ul>
 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
@Immutable
public final class TextInStyleEncoder extends MediaEncoder {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	/**
	 * Encodes a single character and returns its String representation
	 * or null if no modification is necessary.
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 *
	 * @see StyleValidator#checkCharacter(char)
	 *
	 * @throws  InvalidCharacterException  if any text character cannot be converted for use within a CSS string
	 */
	private static String getEscapedCharacter(char c) throws InvalidCharacterException {
		switch(c) {
			case '"' : return "\\\"";
			// Not needed inside double quotes overall: case ''' : return "\\'";
			case '\t' : return "\\9 ";
			case '\n' : return "\\A\\\n";
			case '\r' : return "\\D ";
			case '\\' : return "\\\\";
		}
		if(
			(c >= 0x20 && c <= 0x7E) // common case first
			|| (c >= 0xA0 && c <= 0xFFFD)
		) return null;
		throw new InvalidCharacterException(StyleValidator.RESOURCES, "invalidCharacter", Integer.toHexString(c));
	}

	/**
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 */
	public static void encodeTextInStyle(char ch, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.CSS);
		String escaped = getEscapedCharacter(ch);
		if(escaped != null) out.append(escaped);
		else out.append(ch);
	}

	/**
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 */
	public static void encodeTextInStyle(char[] cbuf, Writer out) throws IOException {
		encodeTextInStyle(cbuf, 0, cbuf.length, out);
	}

	/**
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 */
	public static void encodeTextInStyle(char[] cbuf, int off, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, MediaType.CSS);
		int end = off + len;
		int toPrint = 0;
		for (int c = off; c < end; c++) {
			String escaped = getEscapedCharacter(cbuf[c]);
			if(escaped != null) {
				if(toPrint > 0) {
					out.write(cbuf, c - toPrint, toPrint);
					toPrint = 0;
				}
				out.write(escaped);
			} else {
				toPrint++;
			}
		}
		if(toPrint > 0) out.write(cbuf, end - toPrint, toPrint);
	}

	/**
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 */
	public static void encodeTextInStyle(CharSequence cs, Appendable out) throws IOException {
		if(cs != null) {
			encodeTextInStyle(cs, 0, cs.length(), out);
		} else {
			assert Assertions.isValidating(out, MediaType.CSS);
		}
	}

	/**
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 */
	public static void encodeTextInStyle(CharSequence cs, int start, int end, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.CSS);
		if(cs != null) {
			int toPrint = 0;
			for (int c = start; c < end; c++) {
				String escaped = getEscapedCharacter(cs.charAt(c));
				if(escaped != null) {
					if(toPrint > 0) {
						out.append(cs, c - toPrint, c);
						toPrint = 0;
					}
					out.append(escaped);
				} else {
					toPrint++;
				}
			}
			if(toPrint > 0) out.append(cs, end - toPrint, end);
		}
	}

	/**
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 */
	public static void encodeTextInStyle(Object value, Appendable out) throws IOException {
		Coercion.append(value, textInStyleEncoder, out);
	}
	// </editor-fold>

	/**
	 * Singleton instance intended for static import.
	 */
	public static final TextInStyleEncoder textInStyleEncoder = new TextInStyleEncoder();

	private TextInStyleEncoder() {
		// Do nothing
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.TEXT;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.CSS // All invalid characters in CSS are also invalid in TEXT in CSS
			|| inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in TEXT in CSS
			|| inputType == MediaType.JSON // All invalid characters in JSON are also invalid in TEXT in CSS
			|| inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in TEXT in CSS
			|| inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in TEXT in CSS
			|| inputType == MediaType.XHTML // All invalid characters in XHTML are also invalid in TEXT in CSS
		;
	}

	@Override
	public boolean canSkipValidation(MediaType outputType) {
		return
			outputType == MediaType.CSS // All valid characters in CSS are also valid in TEXT in CSS
			|| outputType == MediaType.MYSQL // All valid characters in MYSQL are also valid in TEXT in CSS
			|| outputType == MediaType.PSQL // All valid characters in PSQL are also valid in TEXT in CSS
			|| outputType == MediaType.SH // All valid characters in SH are also valid in TEXT in CSS
		;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return MediaType.CSS;
	}

	@Override
	public void writePrefixTo(Appendable out) throws IOException {
		super.writePrefixTo(out);
		out.append('"');
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		encodeTextInStyle((char)c, out);
	}

	@Override
	public void write(char[] cbuf, Writer out) throws IOException {
		encodeTextInStyle(cbuf, out);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		encodeTextInStyle(cbuf, off, len, out);
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		if(str == null) throw new IllegalArgumentException("str is null");
		encodeTextInStyle(str, out);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		if(str == null) throw new IllegalArgumentException("str is null");
		encodeTextInStyle(str, off, off + len, out);
	}

	@Override
	public TextInStyleEncoder append(char c, Appendable out) throws IOException {
		encodeTextInStyle(c, out);
		return this;
	}

	@Override
	public TextInStyleEncoder append(CharSequence csq, Appendable out) throws IOException {
		encodeTextInStyle(csq == null ? "null" : csq, out);
		return this;
	}

	@Override
	public TextInStyleEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		encodeTextInStyle(csq == null ? "null" : csq, start, end, out);
		return this;
	}

	@Override
	public void writeSuffixTo(Appendable out, boolean trim) throws IOException {
		super.writeSuffixTo(out, trim);
		out.append('"');
	}
}
