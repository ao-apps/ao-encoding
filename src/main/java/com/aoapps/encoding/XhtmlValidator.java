/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.encoding;

import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.LocalizedIOException;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

/**
 * <p>
 * Makes sure that all data going through this writer has the correct characters
 * for XHTML.
 * </p>
 * <p>
 * <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class XhtmlValidator extends MediaValidator {

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, XhtmlValidator.class);

	/**
	 * Checks one character, throws IOException if invalid.
	 *
	 * <a href="http://www.w3.org/TR/REC-xml/#charsets">http://www.w3.org/TR/REC-xml/#charsets</a>
	 */
	public static void checkCharacter(char c) throws IOException {
		if(
			(c < 0x20 || c > 0xD7FF) // common case first
			&& c != 0x9
			&& c != 0xA
			&& c != 0xD
			&& (c < 0xE000 || c > 0xFFFD)
			// high and low surrogates
			//&& (c < 0x10000 || c > 0x10FFFF)
			&& (c < Character.MIN_HIGH_SURROGATE || c > Character.MAX_LOW_SURROGATE)

		) throw new LocalizedIOException(RESOURCES, "invalidCharacter", Integer.toHexString(c));
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

	protected XhtmlValidator(Writer out) {
		super(out);
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.XHTML;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType==MediaType.XHTML
			|| inputType==MediaType.TEXT        // No validation required
		;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return
			inputType == MediaType.XHTML
			|| inputType == MediaType.XHTML_ATTRIBUTE // All valid XML attributes are also valid XML
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
	public XhtmlValidator append(CharSequence csq) throws IOException {
		checkCharacters(csq, 0, csq.length());
		out.append(csq);
		return this;
	}

	@Override
	public XhtmlValidator append(CharSequence csq, int start, int end) throws IOException {
		checkCharacters(csq, start, end);
		out.append(csq, start, end);
		return this;
	}

	@Override
	public XhtmlValidator append(char c) throws IOException {
		checkCharacter(c);
		out.append(c);
		return this;
	}
}
