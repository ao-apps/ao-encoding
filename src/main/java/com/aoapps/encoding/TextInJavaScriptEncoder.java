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
 * Encodes arbitrary text into a JavaScript string.  The static utility
 * methods do not add the quotes.  When used as a MediaWriter, the text is
 * automatically surrounded by double quotes.  Any binary data is encoded with
 * \\uxxxx escapes.
 *
 * @author  AO Industries, Inc.
 */
public final class TextInJavaScriptEncoder extends MediaEncoder {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	/**
	 * Encodes a single character and returns its String representation
	 * or null if no modification is necessary.
	 */
	private static String getEscapedCharacter(char ch) {
		switch(ch) {
			case '"': return "\\\"";
			case '\'': return "\\'";
			case '\\': return "\\\\";
			case '\b': return "\\b";
			case '\f': return "\\f";
			case '\r': return "\\r";
			case '\n': return "\\n";
			case '\t': return "\\t";

			// Encode the following as unicode because escape for HTML and XHTML is different
			case '&': return "\\u0026";
			case '<': return "\\u003c";
			// Note: This also prevents the potential closing of scripts with "</script>" or CDATA blocks with "]]>" inside strings.
			case '>': return "\\u003e";
			default:
				if(ch<' ') return JavaScript.getUnicodeEscapeString(ch);
				// No conversion necessary
				return null;
		}
	}

	public static void encodeTextInJavaScript(char ch, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.JAVASCRIPT);
		String escaped = getEscapedCharacter(ch);
		if(escaped!=null) out.append(escaped);
		else out.append(ch);
	}

	public static void encodeTextInJavaScript(char[] cbuf, Writer out) throws IOException {
		encodeTextInJavaScript(cbuf, 0, cbuf.length, out);
	}

	public static void encodeTextInJavaScript(char[] cbuf, int off, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, MediaType.JAVASCRIPT);
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

	public static void encodeTextInJavaScript(CharSequence cs, Appendable out) throws IOException {
		if(cs != null) {
			encodeTextInJavaScript(cs, 0, cs.length(), out);
		} else {
			assert Assertions.isValidating(out, MediaType.JAVASCRIPT);
		}
	}

	public static void encodeTextInJavaScript(CharSequence cs, int start, int end, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.JAVASCRIPT);
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

	public static void encodeTextInJavaScript(Object value, Appendable out) throws IOException {
		Coercion.append(value, textInJavaScriptEncoder, out);
	}
	// </editor-fold>

	/**
	 * Singleton instance intended for static import for application/javascript.
	 */
	public static final TextInJavaScriptEncoder textInJavaScriptEncoder = new TextInJavaScriptEncoder(MediaType.JAVASCRIPT);

	/**
	 * Singleton instance intended for static import for application/json.
	 */
	public static final TextInJavaScriptEncoder textInJsonEncoder = new TextInJavaScriptEncoder(MediaType.JSON);

	/**
	 * Singleton instance intended for static import for application/ld+json.
	 */
	public static final TextInJavaScriptEncoder textInLdJsonEncoder = new TextInJavaScriptEncoder(MediaType.LD_JSON);

	private final MediaType outputType;

	private TextInJavaScriptEncoder(MediaType outputType) {
		this.outputType = outputType;
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.TEXT;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.TEXT
			|| inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in TEXT in JAVASCRIPT
			|| inputType == MediaType.JSON // All invalid characters in JSON are also invalid in TEXT in JAVASCRIPT
			|| inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in TEXT in JAVASCRIPT
		;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return true;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return outputType;
	}

	@Override
	public void writePrefixTo(Appendable out) throws IOException {
		super.writePrefixTo(out);
		out.append('"');
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		encodeTextInJavaScript((char)c, out);
	}

	@Override
	public void write(char[] cbuf, Writer out) throws IOException {
		encodeTextInJavaScript(cbuf, out);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		encodeTextInJavaScript(cbuf, off, len, out);
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		if(str==null) throw new IllegalArgumentException("str is null");
		encodeTextInJavaScript(str, out);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		if(str==null) throw new IllegalArgumentException("str is null");
		encodeTextInJavaScript(str, off, off+len, out);
	}

	@Override
	public TextInJavaScriptEncoder append(char c, Appendable out) throws IOException {
		encodeTextInJavaScript(c, out);
		return this;
	}

	@Override
	public TextInJavaScriptEncoder append(CharSequence csq, Appendable out) throws IOException {
		encodeTextInJavaScript(csq==null ? "null" : csq, out);
		return this;
	}

	@Override
	public TextInJavaScriptEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		encodeTextInJavaScript(csq==null ? "null" : csq, start, end, out);
		return this;
	}

	@Override
	public void writeSuffixTo(Appendable out) throws IOException {
		super.writeSuffixTo(out);
		out.append('"');
	}
}
