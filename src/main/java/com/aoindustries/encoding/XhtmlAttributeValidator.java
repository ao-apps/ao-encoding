/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2013, 2015, 2016, 2017, 2018, 2019, 2020  AO Industries, Inc.
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

import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * Makes sure that all data going through this writer has the correct characters
 * for an XHTML attribute.
 * </p>
 * <p>
 * <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class XhtmlAttributeValidator extends MediaValidator {

	/**
	 * Checks one character, throws IOException if invalid.
	 *
	 * <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>
	 */
	public static void checkCharacter(char c) throws IOException {
		if(
			c == '<'
			|| c == '>'
			|| c == '\''
			|| c == '"'
			|| (
				(c < 0x20 || c > 0xD7FF)
				&& (c < 0xE000 || c > 0xFFFD)
				// high and low surrogates
				//&& (c < 0x10000 || c > 0x10FFFF)
				&& (c < Character.MIN_HIGH_SURROGATE || c > Character.MAX_LOW_SURROGATE)
			)
		) throw new IOException(ApplicationResources.accessor.getMessage("XhtmlAttributeValidator.invalidCharacter", Integer.toHexString(c)));
	}

	/**
	 * Checks a set of characters, throws IOException if invalid
	 */
	public static void checkCharacters(char[] cbuf, int off, int len) throws IOException {
		int end = off + len;
		while(off < end) {
			checkCharacter(cbuf[off++]);
		}
	}

	/**
	 * Checks a set of characters, throws IOException if invalid
	 */
	public static void checkCharacters(CharSequence str, int off, int end) throws IOException {
		while(off < end) {
			checkCharacter(str.charAt(off++));
		}
	}

	protected XhtmlAttributeValidator(Writer out) {
		super(out);
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.XHTML_ATTRIBUTE;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType==MediaType.XHTML_ATTRIBUTE
			|| inputType==MediaType.XHTML       // No validation required (All valid XML attributes are also valid XML)
			|| inputType==MediaType.TEXT        // No validation required
		;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return inputType == MediaType.XHTML_ATTRIBUTE;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return MediaType.XHTML_ATTRIBUTE;
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
		if(str==null) throw new IllegalArgumentException("str is null");
		checkCharacters(str, off, off + len);
		out.write(str, off, len);
	}

	@Override
	public XhtmlAttributeValidator append(CharSequence csq) throws IOException {
		checkCharacters(csq, 0, csq.length());
		out.append(csq);
		return this;
	}

	@Override
	public XhtmlAttributeValidator append(CharSequence csq, int start, int end) throws IOException {
		checkCharacters(csq, start, end);
		out.append(csq, start, end);
		return this;
	}

	@Override
	public XhtmlAttributeValidator append(char c) throws IOException {
		checkCharacter(c);
		out.append(c);
		return this;
	}
}
