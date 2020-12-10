/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2018, 2019, 2020  AO Industries, Inc.
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
 * along with ao-encoding.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.encoding;

import com.aoindustries.io.LocalizedIOException;
import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * Encodes arbitrary data for use in a shell script.
 * </p>
 * <p>
 * This implementation is based on <a href="https://www.gnu.org/software/bash/">Bash</a>,
 * it is expected to be compatible with all shells implementing
 * <a href="https://www.gnu.org/software/bash/manual/html_node/ANSI_002dC-Quoting.html">ANSI-C quoting</a>.
 * Per this <a href="https://unix.stackexchange.com/a/371873">Stack Exchange Answer</a>, this includes
 * <a href="http://www.kornshell.org/">ksh</a>, <a href="http://www.zsh.org/">zsh</a>, and some builds of
 * <a href="http://www.in-ulm.de/~mascheck/various/ash/">ash</a>.
 * </p>
 * 
 * @author  AO Industries, Inc.
 */
public class TextInShEncoder extends MediaEncoder {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	/**
	 * From <a href="https://www.gnu.org/software/bash/manual/html_node/ANSI_002dC-Quoting.html">https://www.gnu.org/software/bash/manual/html_node/ANSI_002dC-Quoting.html</a>
	 */
	private static final String[] LOW_CONTROL = {
		"\\x01",
		"\\x02",
		"\\x03",
		"\\x04",
		"\\x05",
		"\\x06",
		"\\a",
		"\\b",
		"\\t",
		null, // \n
		"\\v",
		"\\f",
		"\\r",
		"\\x0E",
		"\\x0F",
		"\\x10",
		"\\x11",
		"\\x12",
		"\\x13",
		"\\x14",
		"\\x15",
		"\\x16",
		"\\x17",
		"\\x18",
		"\\x19",
		"\\x1A",
		"\\x1B",
		"\\x1C",
		"\\x1D",
		"\\x1D",
		"\\x1F"
	};
	private static final String[] HIGH_CONTROL = {
		"\\x7F",
		"\\x80",
		"\\x81",
		"\\x82",
		"\\x83",
		"\\x84",
		"\\x85",
		"\\x86",
		"\\x87",
		"\\x88",
		"\\x89",
		"\\x8A",
		"\\x8B",
		"\\x8C",
		"\\x8D",
		"\\x8E",
		"\\x8F",
		"\\x90",
		"\\x91",
		"\\x92",
		"\\x93",
		"\\x94",
		"\\x95",
		"\\x96",
		"\\x97",
		"\\x98",
		"\\x99",
		"\\x9A",
		"\\x9B",
		"\\x9C",
		"\\x9D",
		"\\x9E",
		"\\x9F"
	};
	static {
		assert LOW_CONTROL.length == 31;
		assert HIGH_CONTROL.length == 33;
	}

	/**
	 * Encodes a single character and returns its String representation
	 * or null if no modification is necessary.  Implemented as
	 * <a href="https://www.gnu.org/software/bash/manual/html_node/ANSI_002dC-Quoting.html">ANSI-C quoting</a>.
	 * The only character not supported is NULL (\x00).
	 *
	 * @see ShValidator#checkCharacter(char)
	 *
	 * @throws  IOException  if any text character cannot be converted for use in a shell script
	 */
	private static String getEscapedCharacter(char c) throws IOException {
		switch(c) {
			case '\\' : return "\\\\";
			case '\'' : return "\\'";
			case '"'  : return "\\\"";
			case '?'  : return "\\?";
		}
		if(
			(c >= 0x20 && c <= 0x7E) // common case first
			|| (c >= 0xA0 && c <= 0xFFFD)
		) return null;
		// 01 to 1F - control characters
		if(c >= 0x01 && c <= 0x1F) return LOW_CONTROL[c - 0x01];
		// 7F to 9F - control characters
		if(c >= 0x7F && c <= 0x9F) return HIGH_CONTROL[c - 0x7F];
		if(c == 0xFFFE) return "\\uFFFE";
		if(c == 0xFFFF) return "\\uFFFF";
		assert c == 0 : "The only character not supported is NULL (\\x00), got " + Integer.toHexString(c);
		throw new LocalizedIOException(ShValidator.RESOURCES, "invalidCharacter", Integer.toHexString(c));
	}

	public static void encodeTextInSh(char ch, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.SH);
		String escaped = getEscapedCharacter(ch);
		if(escaped != null) out.append(escaped);
		else out.append(ch);
	}

	public static void encodeTextInSh(char[] cbuf, Writer out) throws IOException {
		encodeTextInSh(cbuf, 0, cbuf.length, out);
	}

	public static void encodeTextInSh(char[] cbuf, int start, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, MediaType.SH);
		int end = start + len;
		int toPrint = 0;
		for (int c = start; c < end; c++) {
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

	public static void encodeTextInSh(CharSequence S, Appendable out) throws IOException {
		if(S != null) {
			encodeTextInSh(S, 0, S.length(), out);
		}
	}

	public static void encodeTextInSh(CharSequence S, int start, int end, Appendable out) throws IOException {
		if(S != null) {
			assert Assertions.isValidating(out, MediaType.SH);
			int toPrint = 0;
			for (int c = start; c < end; c++) {
				String escaped = getEscapedCharacter(S.charAt(c));
				if(escaped != null) {
					if(toPrint > 0) {
						out.append(S, c - toPrint, c);
						toPrint = 0;
					}
					out.append(escaped);
				} else {
					toPrint++;
				}
			}
			if(toPrint > 0) out.append(S, end - toPrint, end);
		}
	}

	public static void encodeTextInSh(Object value, Appendable out) throws IOException {
		Coercion.append(value, textInShEncoder, out);
	}
	// </editor-fold>

	/**
	 * Singleton instance intended for static import.
	 */
	public static final TextInShEncoder textInShEncoder = new TextInShEncoder();

	private TextInShEncoder() {
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.TEXT;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return inputType == MediaType.TEXT;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return true;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return MediaType.SH;
	}

	@Override
	public void writePrefixTo(Appendable out) throws IOException {
		super.writePrefixTo(out);
		out.append("$'");
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		encodeTextInSh((char)c, out);
	}

	@Override
	public void write(char cbuf[], Writer out) throws IOException {
		encodeTextInSh(cbuf, out);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		encodeTextInSh(cbuf, off, len, out);
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		if(str == null) throw new IllegalArgumentException("str is null");
		encodeTextInSh(str, out);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		if(str == null) throw new IllegalArgumentException("str is null");
		encodeTextInSh(str, off, off + len, out);
	}

	@Override
	public TextInShEncoder append(char c, Appendable out) throws IOException {
		encodeTextInSh(c, out);
		return this;
	}

	@Override
	public TextInShEncoder append(CharSequence csq, Appendable out) throws IOException {
		encodeTextInSh(csq == null ? "null" : csq, out);
		return this;
	}

	@Override
	public TextInShEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		encodeTextInSh(csq == null ? "null" : csq, start, end, out);
		return this;
	}

	@Override
	public void writeSuffixTo(Appendable out) throws IOException {
		super.writeSuffixTo(out);
		out.append('\'');
	}
}
