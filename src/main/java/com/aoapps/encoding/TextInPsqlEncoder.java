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
import com.aoapps.lang.io.LocalizedIOException;
import java.io.IOException;
import java.io.Writer;

/**
 * Encodes arbitrary data for use in the <code>psql</code> command line.
 * This implementation is based on <a href="https://www.postgresql.org/docs/current/static/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS">4.1.2.2. String Constants with C-style Escapes</a>.
 *
 * @author  AO Industries, Inc.
 */
public final class TextInPsqlEncoder extends MediaEncoder {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	/**
	 * From <a href="https://www.postgresql.org/docs/current/static/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS">4.1.2.2. String Constants with C-style Escapes</a>
	 */
	private static final String[] LOW_CONTROL = {
		"\\x01",
		"\\x02",
		"\\x03",
		"\\x04",
		"\\x05",
		"\\x06",
		"\\x07",
		"\\b",
		"\\t",
		null, // \n
		"\\x0B",
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
		"\\x1E",
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
	 * <a href="https://www.postgresql.org/docs/current/static/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS">4.1.2.2. String Constants with C-style Escapes</a>.
	 * The only character not supported is NULL (\x00).
	 *
	 * @see PsqlValidator#checkCharacter(char)
	 *
	 * @throws  IOException  if any text character cannot be converted for use in the psql command line
	 */
	private static String getEscapedCharacter(char c) throws IOException {
		switch(c) {
			case '\\' : return "\\\\";
			case '\'' : return "''";
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
		throw new LocalizedIOException(PsqlValidator.RESOURCES, "invalidCharacter", Integer.toHexString(c));
	}

	/**
	 * See <a href="https://www.postgresql.org/docs/current/static/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS">4.1.2.2. String Constants with C-style Escapes</a>.
	 */
	public static void encodeTextInPsql(char ch, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.PSQL);
		String escaped = getEscapedCharacter(ch);
		if(escaped != null) out.append(escaped);
		else out.append(ch);
	}

	/**
	 * See <a href="https://www.postgresql.org/docs/current/static/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS">4.1.2.2. String Constants with C-style Escapes</a>.
	 */
	public static void encodeTextInPsql(char[] cbuf, Writer out) throws IOException {
		encodeTextInPsql(cbuf, 0, cbuf.length, out);
	}

	/**
	 * See <a href="https://www.postgresql.org/docs/current/static/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS">4.1.2.2. String Constants with C-style Escapes</a>.
	 */
	public static void encodeTextInPsql(char[] cbuf, int off, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, MediaType.PSQL);
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
	 * See <a href="https://www.postgresql.org/docs/current/static/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS">4.1.2.2. String Constants with C-style Escapes</a>.
	 */
	public static void encodeTextInPsql(CharSequence cs, Appendable out) throws IOException {
		if(cs != null) {
			encodeTextInPsql(cs, 0, cs.length(), out);
		} else {
			assert Assertions.isValidating(out, MediaType.PSQL);
		}
	}

	/**
	 * See <a href="https://www.postgresql.org/docs/current/static/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS">4.1.2.2. String Constants with C-style Escapes</a>.
	 */
	public static void encodeTextInPsql(CharSequence cs, int start, int end, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.PSQL);
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
	 * See <a href="https://www.postgresql.org/docs/current/static/sql-syntax-lexical.html#SQL-SYNTAX-CONSTANTS">4.1.2.2. String Constants with C-style Escapes</a>.
	 */
	public static void encodeTextInPsql(Object value, Appendable out) throws IOException {
		Coercion.append(value, textInPsqlEncoder, out);
	}
	// </editor-fold>

	/**
	 * Singleton instance intended for static import.
	 */
	public static final TextInPsqlEncoder textInPsqlEncoder = new TextInPsqlEncoder();

	private TextInPsqlEncoder() {
		// Do nothing
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
		return MediaType.PSQL;
	}

	@Override
	public void writePrefixTo(Appendable out) throws IOException {
		super.writePrefixTo(out);
		out.append("E'");
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		encodeTextInPsql((char)c, out);
	}

	@Override
	public void write(char[] cbuf, Writer out) throws IOException {
		encodeTextInPsql(cbuf, out);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		encodeTextInPsql(cbuf, off, len, out);
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		if(str == null) throw new IllegalArgumentException("str is null");
		encodeTextInPsql(str, out);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		if(str == null) throw new IllegalArgumentException("str is null");
		encodeTextInPsql(str, off, off + len, out);
	}

	@Override
	public TextInPsqlEncoder append(char c, Appendable out) throws IOException {
		encodeTextInPsql(c, out);
		return this;
	}

	@Override
	public TextInPsqlEncoder append(CharSequence csq, Appendable out) throws IOException {
		encodeTextInPsql(csq == null ? "null" : csq, out);
		return this;
	}

	@Override
	public TextInPsqlEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		encodeTextInPsql(csq == null ? "null" : csq, start, end, out);
		return this;
	}

	@Override
	public void writeSuffixTo(Appendable out) throws IOException {
		super.writeSuffixTo(out);
		out.append('\'');
	}
}
