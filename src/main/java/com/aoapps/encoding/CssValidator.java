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

import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.LocalizedIOException;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

/**
 * <ul>
 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
 * </ul>
 *
 * @author  AO Industries, Inc.
 */
// TODO: identifier(...) methods, similar to text(...), to escape arbitrary identifiers?
//       Would same apply to JavaScript?
public class CssValidator extends MediaValidator {

	static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, CssValidator.class);

	/**
	 * Checks one character, throws IOException if invalid.
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 */
	public static void checkCharacter(char c) throws IOException {
		if(
			(c < 0x20 || c > 0x7E) // common case first
			&& c != '\t'
			&& c != '\n'
			&& c != '\r'
			// 7F to 9F - control characters
			&& (c < 0xA0 || c > 0xFFFD)
		) throw new LocalizedIOException(RESOURCES, "invalidCharacter", Integer.toHexString(c));
	}

	/**
	 * Checks a set of characters, throws IOException if invalid
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 */
	public static void checkCharacters(char[] cbuf, int off, int len) throws IOException {
		int end = off + len;
		while(off < end) {
			checkCharacter(cbuf[off++]);
		}
	}

	/**
	 * Checks a set of characters, throws IOException if invalid
	 * <ul>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#characters">4.1.3 Characters and case</a>.</li>
	 * <li>See <a href="https://www.w3.org/TR/CSS2/syndata.html#strings">4.3.7 Strings</a>.</li>
	 * </ul>
	 */
	public static void checkCharacters(CharSequence str, int start, int end) throws IOException {
		while(start < end) {
			checkCharacter(str.charAt(start++));
		}
	}

	protected CssValidator(Writer out) {
		super(out);
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.CSS;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.CSS
			|| inputType == MediaType.XHTML // No validation required (All valid CSS characters are also valid XML)
			|| inputType == MediaType.TEXT // No validation required
		;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return inputType == MediaType.CSS;
	}

	@Override
	public void write(int c) throws IOException {
		checkCharacter((char)c);
		out.write(c);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		checkCharacters(cbuf, off, len);
		out.write(cbuf, off, len);
	}

	@Override
	public void write(String str, int off, int len) throws IOException {
		if(str == null) throw new IllegalArgumentException("str is null");
		checkCharacters(str, off, off + len);
		out.write(str, off, len);
	}

	@Override
	public CssValidator append(CharSequence csq) throws IOException {
		checkCharacters(csq, 0, csq.length());
		out.append(csq);
		return this;
	}

	@Override
	public CssValidator append(CharSequence csq, int start, int end) throws IOException {
		checkCharacters(csq, start, end);
		out.append(csq, start, end);
		return this;
	}

	@Override
	public CssValidator append(char c) throws IOException {
		checkCharacter(c);
		out.append(c);
		return this;
	}
}
