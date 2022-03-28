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

import com.aoapps.lang.Coercion;
import java.io.IOException;
import java.io.Writer;

/**
 * <p>
 * Encode CSS into XHTML.  The static utility methods only validate the characters since all valid CSS characters are
 * also valid in XHTML and do not require additional encoding.
 * When used as a MediaWriter, it automatically adds the &lt;style&gt; tags and optionally a CDATA block.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public final class CssInXhtmlEncoder extends MediaEncoder {

	// <editor-fold defaultstate="collapsed" desc="Static Utility Methods">
	public static void encodeCssInXhtml(char ch, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.XHTML);
		CssValidator.checkCharacter(ch);
		out.append(ch);
	}

	public static void encodeCssInXhtml(char[] cbuf, Writer out) throws IOException {
		encodeCssInXhtml(cbuf, 0, cbuf.length, out);
	}

	public static void encodeCssInXhtml(char[] cbuf, int off, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, MediaType.XHTML);
		CssValidator.checkCharacters(cbuf, off, len);
		out.write(cbuf, off, len);
	}

	public static void encodeCssInXhtml(CharSequence cs, Appendable out) throws IOException {
		if(cs != null) {
			encodeCssInXhtml(cs, 0, cs.length(), out);
		} else {
			assert Assertions.isValidating(out, MediaType.XHTML);
		}
	}

	public static void encodeCssInXhtml(CharSequence cs, int start, int end, Appendable out) throws IOException {
		assert Assertions.isValidating(out, MediaType.XHTML);
		if(cs != null) {
			CssValidator.checkCharacters(cs, start, end);
			out.append(cs, start, end);
		}
	}

	public static void encodeCssInXhtml(Object value, Appendable out) throws IOException {
		Coercion.append(value, cssInXhtmlEncoder, out);
	}
	// </editor-fold>

	/**
	 * Singleton instance intended for static import for text/css.
	 *
	 * @deprecated  This singleton does not have any context so assumes {@link EncodingContext#DEFAULT}.
	 */
	@Deprecated
	public static final CssInXhtmlEncoder cssInXhtmlEncoder = new CssInXhtmlEncoder(EncodingContext.DEFAULT);

	private final EncodingContext encodingContext;

	CssInXhtmlEncoder(EncodingContext encodingContext) {
		this.encodingContext = encodingContext;
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.CSS;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.CSS
			|| inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in CSS in XHTML
			|| inputType == MediaType.JSON // All invalid characters in JSON are also invalid in CSS in XHTML
			|| inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in CSS in XHTML
			|| inputType == MediaType.XHTML // All invalid characters in XHTML are also invalid in CSS in XHTML
			|| inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in CSS in XHTML
		;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return inputType == MediaType.CSS;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void writePrefixTo(Appendable out) throws IOException {
		super.writePrefixTo(out);
		out.append("<style");
		encodingContext.getDoctype().styleType(out);
		if(encodingContext.getSerialization() == Serialization.XML) {
			out.append(">/*<![CDATA[*/" + WhitespaceWriter.NL);
		} else {
			out.append(">" + WhitespaceWriter.NL);
		}
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		encodeCssInXhtml((char)c, out);
	}

	@Override
	public void write(char[] cbuf, Writer out) throws IOException {
		encodeCssInXhtml(cbuf, out);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		encodeCssInXhtml(cbuf, off, len, out);
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		if(str == null) throw new IllegalArgumentException("str is null");
		encodeCssInXhtml(str, out);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		if(str == null) throw new IllegalArgumentException("str is null");
		encodeCssInXhtml(str, off, off+len, out);
	}

	@Override
	public CssInXhtmlEncoder append(char c, Appendable out) throws IOException {
		encodeCssInXhtml(c, out);
		return this;
	}

	@Override
	public CssInXhtmlEncoder append(CharSequence csq, Appendable out) throws IOException {
		encodeCssInXhtml(csq == null ? "null" : csq, out);
		return this;
	}

	@Override
	public CssInXhtmlEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		encodeCssInXhtml(csq == null ? "null" : csq, start, end, out);
		return this;
	}

	@Override
	public void writeSuffixTo(Appendable out) throws IOException {
		super.writeSuffixTo(out);
		if(encodingContext.getSerialization() == Serialization.XML) {
			out.append(WhitespaceWriter.NL + "/*]]>*/</style>");
		} else {
			out.append(WhitespaceWriter.NL + "</style>");
		}
	}
}
