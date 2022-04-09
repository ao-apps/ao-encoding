/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

/**
 * <p>
 * Verifies all characters going through this filter are valid for an XHTML attribute.
 * </p>
 * <p>
 * <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public final class XhtmlAttributeValidator extends MediaValidator {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, XhtmlAttributeValidator.class);

	/**
	 * Checks one character, throws {@link InvalidCharacterException} if invalid.
	 * <p>
	 * See <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>.
	 * </p>
	 */
	public static void checkCharacter(char c) throws InvalidCharacterException {
		if(
			c == '<'
			|| c == '>'
			|| c == '\''
			|| c == '"'
			|| c < 0x20
			|| c > 0xFFFD
		) throw new InvalidCharacterException(RESOURCES, "invalidCharacter", Integer.toHexString(c));
	}

	/**
	 * Checks a set of characters, throws {@link InvalidCharacterException} if invalid
	 * <p>
	 * See <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>.
	 * </p>
	 */
	public static void checkCharacters(char[] cbuf, int off, int len) throws InvalidCharacterException {
		int end = off + len;
		while(off < end) {
			checkCharacter(cbuf[off++]);
		}
	}

	/**
	 * Checks a set of characters, throws {@link InvalidCharacterException} if invalid
	 * <p>
	 * See <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>.
	 * </p>
	 */
	public static void checkCharacters(CharSequence str, int start, int end) throws InvalidCharacterException {
		while(start < end) {
			checkCharacter(str.charAt(start++));
		}
	}
	// </editor-fold>

	XhtmlAttributeValidator(Writer out) {
		super(out);
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.XHTML_ATTRIBUTE;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in XHTML_ATTRIBUTE
			|| inputType == MediaType.JSON // All invalid characters in JSON are also invalid in XHTML_ATTRIBUTE
			|| inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in XHTML_ATTRIBUTE
			|| inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in XHTML_ATTRIBUTE
			|| inputType == MediaType.XHTML // All invalid characters in XHTML are also invalid in XHTML_ATTRIBUTE
			|| inputType == MediaType.XHTML_ATTRIBUTE // All invalid characters in XHTML_ATTRIBUTE are also invalid in XHTML_ATTRIBUTE
		;
	}

	@Override
	public boolean canSkipValidation(MediaType outputType) {
		return
			outputType == MediaType.XHTML_ATTRIBUTE // All valid characters in XHTML_ATTRIBUTE are also valid in XHTML_ATTRIBUTE
		;
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
