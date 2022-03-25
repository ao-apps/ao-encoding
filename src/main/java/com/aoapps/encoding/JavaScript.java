/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

/**
 * Utilities helping JavaScript encoder implementations.
 *
 * @author  AO Industries, Inc.
 */
final class JavaScript {

	/** Make no instances. */
	private JavaScript() {throw new AssertionError();}

	private static final char[] hexChars={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	private static char getHex(int value) {
		return hexChars[value & 15];
	}

	/**
	 * The Strings are kept here after first created.
	 */
	// 0x0 <= ch < 0x20
	private static final int ENCODE_RANGE_1_END   = 0x20;
	private static final String[] javaScriptUnicodeEscapeStrings1 = new String[ENCODE_RANGE_1_END];
	// 0xD800 <= ch < 0xE000
	private static final int ENCODE_RANGE_2_START = 0xD800;
	private static final int ENCODE_RANGE_2_END   = 0xE000;
	private static final String[] javaScriptUnicodeEscapeStrings2 = new String[ENCODE_RANGE_2_END - ENCODE_RANGE_2_START];
	// 0xFFFE <= ch < 0x10000
	private static final String FFFE = "\\ufffe";
	private static final String FFFF = "\\uffff";
	static {
		for(int ch = 0; ch < ENCODE_RANGE_1_END; ch++) {
			// Escape using JavaScript unicode escape.
			javaScriptUnicodeEscapeStrings1[ch] =
				("\\u" + getHex(ch >>> 12) + getHex(ch >>> 8) + getHex(ch >>> 4) + getHex(ch)).intern();
		}
		for(int ch = ENCODE_RANGE_2_START; ch < ENCODE_RANGE_2_END; ch++) {
			// Escape using JavaScript unicode escape.
			javaScriptUnicodeEscapeStrings2[ch - ENCODE_RANGE_2_START] =
				("\\u" + getHex(ch >>> 12) + getHex(ch >>> 8) + getHex(ch >>> 4) + getHex(ch)).intern();
		}
		assert 0xfffe >= ENCODE_RANGE_2_END;
	}

	/**
	 * Gets the Unicode escape for a JavaScript character or null if may be passed-through without escape.
	 * <p>
	 * Code points outside the BMP (0x10000+) are simply handled as their
	 * individual surrogates {@code "\\uhhhh\\uhhhh"}-escaped.  This is safe,
	 * completely interoperable between Java and JavaScript, and works with
	 * simple one-char-at-a-time streaming implementations.
	 * </p>
	 *
	 * @param ch  The character to encode
	 *
	 * @return  the encoded form of the character or {@code null} when no
	 *          encoding needed
	 */
	static String getUnicodeEscapeString(char ch) {
		int chInt = ch;
		if(chInt < ENCODE_RANGE_1_END) {
			return javaScriptUnicodeEscapeStrings1[chInt];
		}
		if(chInt >= ENCODE_RANGE_2_START) {
			if(chInt < ENCODE_RANGE_2_END) {
				return javaScriptUnicodeEscapeStrings2[chInt - ENCODE_RANGE_2_START];
			}
			if(chInt == 0xfffe) return FFFE;
			if(chInt == 0xffff) return FFFF;
		}
		// No encoding needed
		return null;
	}
}
