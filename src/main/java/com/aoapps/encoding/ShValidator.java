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

import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.LocalizedIOException;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

/**
 * Although shell scripts can potentially parse and execute with lots of unprintable and binary values,
 * we are validating a more strict subset of characters that are generally text editor and copy/paste friendly.
 * We will consider shell scripts relying on unusual characters, even if technically supported, to be
 * invalid.  This choice is made to help isolate bugs and accidental conflations of data and code.
 * <p>
 * This implementation is based on <a href="https://www.gnu.org/software/bash/">Bash</a>, but
 * it is expected to be compatible with other shells.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class ShValidator extends MediaValidator {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, ShValidator.class);

	/**
	 * Checks one character, throws IOException if invalid.
	 * <p>
	 * See <a href="https://www.tldp.org/LDP/abs/html/special-chars.html#CONTROLCHARREF">Advanced Bash-Scripting Guide: Special Characters: Control Characters</a>.
	 * </p>
	 */
	public static void checkCharacter(char c) throws IOException {
		if(
			(c < 0x20 || c > 0x7E) // common case first
			&& c != '\t'
			&& c != '\n'
			// 7F to 9F - control characters
			&& (c < 0xA0 || c > 0xFFFD)
		) throw new LocalizedIOException(RESOURCES, "invalidCharacter", Integer.toHexString(c));
	}

	/**
	 * Checks a set of characters, throws IOException if invalid
	 * <p>
	 * See <a href="https://www.tldp.org/LDP/abs/html/special-chars.html#CONTROLCHARREF">Advanced Bash-Scripting Guide: Special Characters: Control Characters</a>.
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
	 * See <a href="https://www.tldp.org/LDP/abs/html/special-chars.html#CONTROLCHARREF">Advanced Bash-Scripting Guide: Special Characters: Control Characters</a>.
	 * </p>
	 */
	public static void checkCharacters(CharSequence str, int start, int end) throws IOException {
		while(start < end) {
			checkCharacter(str.charAt(start++));
		}
	}
	// </editor-fold>

	protected ShValidator(Writer out) {
		super(out);
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.SH;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.CSS // All invalid characters in CSS are also invalid in SH
			|| inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in SH
			|| inputType == MediaType.JSON // All invalid characters in JSON are also invalid in SH
			|| inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in SH
			|| inputType == MediaType.MYSQL // All invalid characters in MYSQL are also invalid in SH
			|| inputType == MediaType.PSQL // All invalid characters in PSQL are also invalid in SH
			|| inputType == MediaType.SH // All invalid characters in SH are also invalid in SH
			|| inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in SH
			|| inputType == MediaType.XHTML // All invalid characters in XHTML are also invalid in SH
		;
	}

	@Override
	public boolean canSkipValidation(MediaType outputType) {
		return
			outputType == MediaType.MYSQL // All valid characters in MYSQL are also valid in SH
			|| outputType == MediaType.PSQL // All valid characters in PSQL are also valid in SH
			|| outputType == MediaType.SH // All valid characters in SH are also valid in SH
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
		if(str == null) throw new IllegalArgumentException("str is null");
		checkCharacters(str, off, off + len);
		out.write(str, off, len);
	}

	@Override
	public ShValidator append(CharSequence csq) throws IOException {
		checkCharacters(csq, 0, csq.length());
		out.append(csq);
		return this;
	}

	@Override
	public ShValidator append(CharSequence csq, int start, int end) throws IOException {
		checkCharacters(csq, start, end);
		out.append(csq, start, end);
		return this;
	}

	@Override
	public ShValidator append(char c) throws IOException {
		checkCharacter(c);
		out.append(c);
		return this;
	}
}
