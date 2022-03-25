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

import com.aoapps.lang.Coercion;
import java.io.IOException;
import java.io.Writer;

/**
 * Encode JavaScript into an XHTML attribute.  This does not add any quotes or
 * tags.
 *
 * @author  AO Industries, Inc.
 */
public final class JavaScriptInXhtmlAttributeEncoder extends MediaEncoder {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	/**
	 * Encodes a single character and returns its String representation
	 * or null if no modification is necessary.  Any character that is
	 * not valid in XHTML is encoded to JavaScript \\uxxxx escapes.
	 * " and ' are changed to XHTML entities.
	 */
	private static String getEscapedCharacter(char ch) {
		switch(ch) {
			// These characters are allowed in JavaScript but need encoded for XHTML
			case '<': return "&lt;";
			case '>': return "&gt;";
			case '&': return "&amp;";
			case '"': return "&quot;";
			case '\'': return "&#39;";
			case '\r': return "&#xD;";
			case '\n': return "&#xA;";
			case '\t': return "&#x9;";
			default:
				// Escape using JavaScript unicode escape when needed
				return JavaScript.getUnicodeEscapeString(ch);
		}
	}

	public static void encodeJavaScriptInXhtmlAttribute(char ch, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.XHTML_ATTRIBUTE);
		String escaped = getEscapedCharacter(ch);
		if(escaped!=null) out.append(escaped);
		else out.append(ch);
	}

	public static void encodeJavaScriptInXhtmlAttribute(char[] cbuf, Writer out) throws IOException {
		encodeJavaScriptInXhtmlAttribute(cbuf, 0, cbuf.length, out);
	}

	public static void encodeJavaScriptInXhtmlAttribute(char[] cbuf, int off, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, MediaType.XHTML_ATTRIBUTE);
		int end = off + len;
		int toPrint = 0;
		for (int c = off; c < end; c++) {
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

	public static void encodeJavaScriptInXhtmlAttribute(CharSequence cs, Appendable out) throws IOException {
		if(cs != null) {
			encodeJavaScriptInXhtmlAttribute(cs, 0, cs.length(), out);
		} else {
			assert Assertions.isValidating(out, MediaType.XHTML_ATTRIBUTE);
		}
	}

	public static void encodeJavaScriptInXhtmlAttribute(CharSequence cs, int start, int end, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.XHTML_ATTRIBUTE);
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

	public static void encodeJavaScriptInXhtmlAttribute(Object value, Appendable out) throws IOException {
		Coercion.append(value, javaScriptInXhtmlAttributeEncoder, out);
	}
	// </editor-fold>

	/**
	 * Singleton instance intended for static import.
	 */
	public static final JavaScriptInXhtmlAttributeEncoder javaScriptInXhtmlAttributeEncoder = new JavaScriptInXhtmlAttributeEncoder();

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.JAVASCRIPT;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType==MediaType.JAVASCRIPT
			|| inputType==MediaType.TEXT  // No validation required
		;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return
			inputType==MediaType.JAVASCRIPT
			|| inputType==MediaType.JSON
			|| inputType==MediaType.LD_JSON
		;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return MediaType.XHTML_ATTRIBUTE;
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		encodeJavaScriptInXhtmlAttribute((char)c, out);
	}

	@Override
	public void write(char[] cbuf, Writer out) throws IOException {
		encodeJavaScriptInXhtmlAttribute(cbuf, out);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		encodeJavaScriptInXhtmlAttribute(cbuf, off, len, out);
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		if(str==null) throw new IllegalArgumentException("str is null");
		encodeJavaScriptInXhtmlAttribute(str, out);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		if(str==null) throw new IllegalArgumentException("str is null");
		encodeJavaScriptInXhtmlAttribute(str, off, off+len, out);
	}

	@Override
	public JavaScriptInXhtmlAttributeEncoder append(char c, Appendable out) throws IOException {
		encodeJavaScriptInXhtmlAttribute(c, out);
		return this;
	}

	@Override
	public JavaScriptInXhtmlAttributeEncoder append(CharSequence csq, Appendable out) throws IOException {
		encodeJavaScriptInXhtmlAttribute(csq==null ? "null" : csq, out);
		return this;
	}

	@Override
	public JavaScriptInXhtmlAttributeEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		encodeJavaScriptInXhtmlAttribute(csq==null ? "null" : csq, start, end, out);
		return this;
	}
}
