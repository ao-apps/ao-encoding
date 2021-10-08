/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.lang.Coercion;
import java.io.IOException;
import java.io.Writer;

/**
 * Encodes arbitrary data into XHTML.  Minimal conversion is performed, just
 * encoding of necessary values and throwing an IOException when any character
 * is found that cannot be converted to XHTML entities.
 * 
 * @author  AO Industries, Inc.
 */
public class TextInXhtmlEncoder extends MediaEncoder {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	/**
	 * Encodes a single character and returns its String representation
	 * or null if no modification is necessary.
	 *
	 * @see XhtmlValidator#checkCharacter(char)
	 *
	 * @throws  IOException  if any text character cannot be converted to XHTML
	 */
	private static String getEscapedCharacter(char c) throws IOException {
		// 0.0037849858401 ms per call with switch
		// 0.00360391125522 ms per call with if, no room to improve here
		switch(c) {
			case '<': return "&lt;";
			case '>': return "&gt;";
			case '&': return "&amp;";
			default:
				XhtmlValidator.checkCharacter(c);
				return null;
		}
	}

	public static void encodeTextInXhtml(char ch, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.XHTML);
		String escaped = getEscapedCharacter(ch);
		if(escaped!=null) out.append(escaped);
		else out.append(ch);
	}

	public static void encodeTextInXhtml(char[] cbuf, Writer out) throws IOException {
		encodeTextInXhtml(cbuf, 0, cbuf.length, out);
	}

	public static void encodeTextInXhtml(char[] cbuf, int start, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, MediaType.XHTML);
		int end = start+len;
		int toPrint = 0;
		for (int c = start; c < end; c++) {
			String escaped = getEscapedCharacter(cbuf[c]);
			if(escaped!=null) {
				if(toPrint>0) {
					out.write(cbuf, c-toPrint, toPrint);
					toPrint=0;
				}
				out.write(escaped);
			} else {
				toPrint++;
			}
		}
		if(toPrint>0) out.write(cbuf, end-toPrint, toPrint);
	}

	public static void encodeTextInXhtml(CharSequence S, Appendable out) throws IOException {
		if(S!=null) {
			encodeTextInXhtml(S, 0, S.length(), out);
		} else {
			assert Assertions.isValidating(out, MediaType.XHTML);
		}
	}

	public static void encodeTextInXhtml(CharSequence S, int start, int end, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.XHTML);
		if(S!=null) {
			int toPrint = 0;
			for (int c = start; c < end; c++) {
				String escaped = getEscapedCharacter(S.charAt(c));
				if(escaped!=null) {
					if(toPrint>0) {
						out.append(S, c-toPrint, c);
						toPrint=0;
					}
					out.append(escaped);
				} else {
					toPrint++;
				}
			}
			if(toPrint>0) out.append(S, end-toPrint, end);
		}
	}

	public static void encodeTextInXhtml(Object value, Appendable out) throws IOException {
		Coercion.append(value, textInXhtmlEncoder, out);
	}
	// </editor-fold>

	/**
	 * Singleton instance intended for static import.
	 */
	public static final TextInXhtmlEncoder textInXhtmlEncoder = new TextInXhtmlEncoder();

	private TextInXhtmlEncoder() {
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.TEXT;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType==MediaType.TEXT
		;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return true;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		encodeTextInXhtml((char)c, out);
	}

	@Override
	public void write(char[] cbuf, Writer out) throws IOException {
		encodeTextInXhtml(cbuf, out);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		encodeTextInXhtml(cbuf, off, len, out);
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		if(str==null) throw new IllegalArgumentException("str is null");
		encodeTextInXhtml(str, out);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		if(str==null) throw new IllegalArgumentException("str is null");
		encodeTextInXhtml(str, off, off+len, out);
	}

	@Override
	public TextInXhtmlEncoder append(char c, Appendable out) throws IOException {
		encodeTextInXhtml(c, out);
		return this;
	}

	@Override
	public TextInXhtmlEncoder append(CharSequence csq, Appendable out) throws IOException {
		encodeTextInXhtml(csq==null ? "null" : csq, out);
		return this;
	}

	@Override
	public TextInXhtmlEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		encodeTextInXhtml(csq==null ? "null" : csq, start, end, out);
		return this;
	}
}
