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

import com.aoapps.lang.Strings;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.LocalizedIOException;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

/**
 * Makes sure that all data going through this writer has the correct characters
 * for URI/URL data.  The URL must be valid <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986 URI</a> or
 * <a href="https://datatracker.ietf.org/doc/html/rfc3987">RFC 3987 IRI</a>.
 *
 * @author  AO Industries, Inc.
 */
public class UrlValidator extends BufferedValidator {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, UrlValidator.class);

	/**
	 * Checks one character, throws IOException if invalid.
	 * <p>
	 * See <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * and <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.3">RFC 3986: Unreserved Characters</a>.
	 * </p>
	 */
	public static void checkCharacter(char c) throws IOException {
		switch(c) {
			/*
			 * Reserved Characters
			 */
			// gen-delims
			case ':' :
			case '/' :
			case '?' :
			case '#' :
			case '[' :
			case ']' :
			case '@' :
			// sub-delims
			case '!' :
			case '$' :
			case '&' :
			case '\'' :
			case '(' :
			case ')' :
			case '*' :
			case '+' :
			case ',' :
			case ';' :
			case '=' :

			/*
			 * Already percent-encoded
			 */
			//
			case '%' :

			/*
			 * IRI-only US-ASCII
			 */
			case '<' :
			case '>' :
			case '"' :
			case ' ' :
			case '{' :
			case '}' :
			case '|' :
			case '\\' :
			case '^' :
			case '`' :

			/*
			 * Unreserved Characters
			 */
			case '-':
			case '.':
			case '_':
			case '~':
				return;
			default:
				if(
					// ALPHA
					(c >= 'a' && c <= 'z')
					|| (c >= 'A' && c <= 'Z')
					// DIGIT
					|| (c >= '0' && c <= '9')
					// IRI
					|| c >= 128
				) {
					return;
				}
		}
		throw new LocalizedIOException(RESOURCES, "invalidCharacter", Integer.toHexString(c));
	}

	/**
	 * Checks a set of characters, throws IOException if invalid
	 * <p>
	 * See <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * and <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.3">RFC 3986: Unreserved Characters</a>.
	 * </p>
	 */
	public static void checkCharacters(char[] cbuf, int off, int len) throws IOException {
		int end = off + len;
		while(off < end) {
			checkCharacter(cbuf[off++]);
		}
	}

	/**
	 * Checks a set of characters, throws IOException if invalid
	 * <p>
	 * See <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.2">RFC 3986: Reserved Characters</a>
	 * and <a href="https://datatracker.ietf.org/doc/html/rfc3986#section-2.3">RFC 3986: Unreserved Characters</a>.
	 * </p>
	 */
	public static void checkCharacters(CharSequence str, int start, int end) throws IOException {
		while(start < end) {
			checkCharacter(str.charAt(start++));
		}
	}
	// </editor-fold>

	protected UrlValidator(Writer out) {
		super(out, 128);
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.URL;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.URL
			|| inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in URL
			|| inputType == MediaType.JSON // All invalid characters in JSON are also invalid in URL
			|| inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in URL
			|| inputType == MediaType.MYSQL // All invalid characters in MYSQL are also invalid in URL
			|| inputType == MediaType.PSQL // All invalid characters in PSQL are also invalid in URL
			|| inputType == MediaType.SH // All invalid characters in SH are also invalid in URL
			|| inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in URL
		;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return inputType == MediaType.URL;
	}

	@Override
	protected void validate(StringBuilder buffer) throws IOException {
		String url = Strings.trim(buffer).toString();
		checkCharacters(url, 0, url.length());
		out.write(url);
	}
}
