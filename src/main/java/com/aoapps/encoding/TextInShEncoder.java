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
public final class TextInShEncoder extends MediaEncoder {

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
	 * @throws  InvalidCharacterException  if any text character cannot be converted for use in a shell script
	 */
	private static String getEscapedCharacter(char c) throws InvalidCharacterException {
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
		throw new InvalidCharacterException(ShValidator.RESOURCES, "invalidCharacter", Integer.toHexString(c));
	}

	/**
	 * See <a href="https://www.tldp.org/LDP/abs/html/special-chars.html#CONTROLCHARREF">Advanced Bash-Scripting Guide: Special Characters: Control Characters</a>.
	 */
	public static void encodeTextInSh(char ch, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.SH);
		String escaped = getEscapedCharacter(ch);
		if(escaped != null) out.append(escaped);
		else out.append(ch);
	}

	/**
	 * See <a href="https://www.tldp.org/LDP/abs/html/special-chars.html#CONTROLCHARREF">Advanced Bash-Scripting Guide: Special Characters: Control Characters</a>.
	 */
	public static void encodeTextInSh(char[] cbuf, Writer out) throws IOException {
		encodeTextInSh(cbuf, 0, cbuf.length, out);
	}

	/**
	 * See <a href="https://www.tldp.org/LDP/abs/html/special-chars.html#CONTROLCHARREF">Advanced Bash-Scripting Guide: Special Characters: Control Characters</a>.
	 */
	public static void encodeTextInSh(char[] cbuf, int off, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, MediaType.SH);
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
	 * See <a href="https://www.tldp.org/LDP/abs/html/special-chars.html#CONTROLCHARREF">Advanced Bash-Scripting Guide: Special Characters: Control Characters</a>.
	 */
	public static void encodeTextInSh(CharSequence cs, Appendable out) throws IOException {
		if(cs != null) {
			encodeTextInSh(cs, 0, cs.length(), out);
		} else {
			assert Assertions.isValidating(out, MediaType.SH);
		}
	}

	/**
	 * See <a href="https://www.tldp.org/LDP/abs/html/special-chars.html#CONTROLCHARREF">Advanced Bash-Scripting Guide: Special Characters: Control Characters</a>.
	 */
	public static void encodeTextInSh(CharSequence cs, int start, int end, Appendable out) throws IOException {
		if(cs != null) {
			assert Assertions.isValidating(out, MediaType.SH);
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
	 * See <a href="https://www.tldp.org/LDP/abs/html/special-chars.html#CONTROLCHARREF">Advanced Bash-Scripting Guide: Special Characters: Control Characters</a>.
	 */
	public static void encodeTextInSh(Object value, Appendable out) throws IOException {
		Coercion.append(value, textInShEncoder, out);
	}
	// </editor-fold>

	/**
	 * Singleton instance intended for static import.
	 */
	public static final TextInShEncoder textInShEncoder = new TextInShEncoder();

	private TextInShEncoder() {
		// Do nothing
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.TEXT;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in TEXT in SH
			|| inputType == MediaType.JSON // All invalid characters in JSON are also invalid in TEXT in SH
			|| inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in TEXT in SH
			|| inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in TEXT in SH
		;
	}

	@Override
	public boolean canSkipValidation(MediaType outputType) {
		return
			outputType == MediaType.CSS // All valid characters in CSS are also valid in TEXT in SH
			|| outputType == MediaType.MYSQL // All valid characters in MYSQL are also valid in TEXT in SH
			|| outputType == MediaType.PSQL // All valid characters in PSQL are also valid in TEXT in SH
			|| outputType == MediaType.SH // All valid characters in SH are also valid in TEXT in SH
			|| outputType == MediaType.URL // All valid characters in URL are also valid in TEXT in SH
			|| outputType == MediaType.XHTML // All valid characters in XHTML are also valid in TEXT in SH
			|| outputType == MediaType.XHTML_ATTRIBUTE // All valid characters in XHTML_ATTRIBUTE are also valid in TEXT in SH
		;
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
	public void write(char[] cbuf, Writer out) throws IOException {
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
	public void writeSuffixTo(Appendable out, boolean trim) throws IOException {
		super.writeSuffixTo(out, trim);
		out.append('\'');
	}
}
