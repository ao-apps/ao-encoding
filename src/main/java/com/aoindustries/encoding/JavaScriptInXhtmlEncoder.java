/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2019  AO Industries, Inc.
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

import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * Encode JavaScript and related formats into XHTML.  The static utility methods
 * only encode the characters.  When used as a MediaWriter, it automatically
 * adds the &lt;script&gt; tags and optionally a CDATA block.
 * </p>
 * <p>
 * The CDATA block is not added for JSON and LD_JSON, because JSON does not
 * support comments (insert Captain Picard facepalm pic here).  However, as the
 * JSON format will only contain &lt;, &gt;, or &amp; within quoted strings,
 * and those characters are unicode escaped, this should not present a
 * compatibility issue between HTML and XHTML.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
final public class JavaScriptInXhtmlEncoder extends MediaEncoder {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	/**
	 * Encodes a single character and returns its String representation
	 * or null if no modification is necessary.  Any character that is
	 * not valid in XHTML, or is '&lt;', '&amp;', or '&gt;' is encoded to
	 * JavaScript \\uxxxx escapes.
	 */
	private static String getEscapedCharacter(char ch) {
		// These characters are allowed in JavaScript but need encoded for XHTML
		switch(ch) {
			// ']' Is encoded to avoid potential ]]> encoding CDATA early?
			// Imagine script with: if(array[array2[index]]>value) { ... }
			// This didn't work as hoped, just don't use "]]>" in scripts!
			// Note: TextInJavaScriptEncoder always encodes the ">", so dynamic values
			//       in JavaScript strings will never have "]]>".
			// TODO: Find a better way that is both HTML and XHTML compatible.
			// case ']' : return "\\u005d";
			// Commented-out because now using CDATA
			// case '<': return "&lt;";
			// case '>': return "&gt;";
			// case '&': return "&amp;";
			// These character ranges are passed through unmodified
			case '\r':
			case '\n':
			case '\t':
			case '\\':
				return null;
			default:
				// Escape using JavaScript unicode escape when needed.
				return NewEncodingUtils.getJavaScriptUnicodeEscapeString(ch);
		}
	}

	public static void encodeJavaScriptInXhtml(char ch, Appendable out) throws IOException {
		String escaped = getEscapedCharacter(ch);
		if(escaped!=null) out.append(escaped);
		else out.append(ch);
	}

	public static void encodeJavaScriptInXhtml(char[] cbuf, Writer out) throws IOException {
		encodeJavaScriptInXhtml(cbuf, 0, cbuf.length, out);
	}

	public static void encodeJavaScriptInXhtml(char[] cbuf, int start, int len, Writer out) throws IOException {
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

	public static void encodeJavaScriptInXhtml(CharSequence S, Appendable out) throws IOException {
		if(S!=null) {
			encodeJavaScriptInXhtml(S, 0, S.length(), out);
		}
	}

	public static void encodeJavaScriptInXhtml(CharSequence S, int start, int end, Appendable out) throws IOException {
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
	// </editor-fold>

	/**
	 * Singleton instance intended for static import for text/javascript.
	 */
	public static final JavaScriptInXhtmlEncoder javaScriptInXhtmlEncoder = new JavaScriptInXhtmlEncoder(MediaType.JAVASCRIPT);

	/**
	 * Singleton instance intended for static import for application/json.
	 */
	public static final JavaScriptInXhtmlEncoder jsonInXhtmlEncoder = new JavaScriptInXhtmlEncoder(MediaType.JSON);

	/**
	 * Singleton instance intended for static import for application/ld+json.
	 */
	public static final JavaScriptInXhtmlEncoder ldJsonInXhtmlEncoder = new JavaScriptInXhtmlEncoder(MediaType.LD_JSON);

	private final MediaType contentType;

	private JavaScriptInXhtmlEncoder(MediaType contentType) {
		this.contentType = contentType;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType==MediaType.JAVASCRIPT
			|| inputType==MediaType.JSON
			|| inputType==MediaType.LD_JSON
			|| inputType==MediaType.TEXT  // No validation required
		;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void writePrefixTo(Appendable out) throws IOException {
		out.append("<script type=\"");
		encodeTextInXhtmlAttribute(contentType.getContentType(), out);
		out.append("\">\n");
		if(contentType == MediaType.JAVASCRIPT) out.append("  // <![CDATA[\n");
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		encodeJavaScriptInXhtml((char)c, out);
	}

	@Override
	public void write(char cbuf[], Writer out) throws IOException {
		encodeJavaScriptInXhtml(cbuf, out);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		encodeJavaScriptInXhtml(cbuf, off, len, out);
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		if(str==null) throw new IllegalArgumentException("str is null");
		encodeJavaScriptInXhtml(str, out);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		if(str==null) throw new IllegalArgumentException("str is null");
		encodeJavaScriptInXhtml(str, off, off+len, out);
	}

	@Override
	public JavaScriptInXhtmlEncoder append(char c, Appendable out) throws IOException {
		encodeJavaScriptInXhtml(c, out);
		return this;
	}

	@Override
	public JavaScriptInXhtmlEncoder append(CharSequence csq, Appendable out) throws IOException {
		encodeJavaScriptInXhtml(csq==null ? "null" : csq, out);
		return this;
	}

	@Override
	public JavaScriptInXhtmlEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		encodeJavaScriptInXhtml(csq==null ? "null" : csq, start, end, out);
		return this;
	}

	@Override
	public void writeSuffixTo(Appendable out) throws IOException {
		if(contentType == MediaType.JAVASCRIPT) out.append("  // ]]>\n");
		out.append("</script>");
	}
}
