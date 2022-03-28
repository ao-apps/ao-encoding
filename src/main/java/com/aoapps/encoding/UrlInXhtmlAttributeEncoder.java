/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Encodes a URL into an XHTML attribute.  It {@link EncodingContext#encodeURL(java.lang.String)} to
 * rewrite the URL as needed.
 *
 * @author  AO Industries, Inc.
 */
public class UrlInXhtmlAttributeEncoder extends BufferedEncoder {

	private final EncodingContext encodingContext;

	public UrlInXhtmlAttributeEncoder(EncodingContext encodingContext) {
		super(128);
		this.encodingContext = encodingContext;
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.URL;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in URL in XHTML_ATTRIBUTE
			|| inputType == MediaType.JSON // All invalid characters in JSON are also invalid in URL in XHTML_ATTRIBUTE
			|| inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in URL in XHTML_ATTRIBUTE
			|| inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in URL in XHTML_ATTRIBUTE
			|| inputType == MediaType.URL // All invalid characters in URL are also invalid in URL in XHTML_ATTRIBUTE
		;
	}

	@Override
	public boolean canSkipValidation(MediaType outputType) {
		return
			outputType == MediaType.URL // All valid characters in URL are also valid in URL in XHTML_ATTRIBUTE
		;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return MediaType.XHTML_ATTRIBUTE;
	}

	@Override
	@SuppressWarnings("StringEquality")
	protected void writeSuffix(CharSequence buffer, Appendable out) throws IOException {
		String url = buffer.toString();
		UrlValidator.checkCharacters(url, 0, url.length());
		String encoded;
		if(encodingContext != null) {
			encoded = encodingContext.encodeURL(url);
			if(encoded != url) UrlValidator.checkCharacters(encoded, 0, encoded.length());
		} else {
			encoded = url;
		}
		// XHTML_ATTRIBUTE does not support \uFFFE or \uFFFF
		if(encoded.indexOf('\uFFFE') != -1) {
			Charset charset = encodingContext.getCharacterEncoding();
			String charsetName = charset.name();
			String newUrl = encoded.replace(
				"\uFFFE",
				(charset == EncodingContext.DEFAULT.getCharacterEncoding())
					// Use precomputed for default charset
					? UrlInXhtmlEncoder.DEFAULT_FFFE
					// Compute for current non-default charset
					: URLEncoder.encode("\uFFFE", charsetName)
			);
			assert newUrl.length() > encoded.length();
			assert URLDecoder.decode(encoded, charsetName).equals(URLDecoder.decode(newUrl, charsetName));
			encoded = newUrl;
		}
		if(encoded.indexOf('\uFFFF') != -1) {
			Charset charset = encodingContext.getCharacterEncoding();
			String charsetName = charset.name();
			String newUrl = encoded.replace(
				"\uFFFF",
				(charset == EncodingContext.DEFAULT.getCharacterEncoding())
					// Use precomputed for default charset
					? UrlInXhtmlEncoder.DEFAULT_FFFF
					// Compute for current non-default charset
					: URLEncoder.encode("\uFFFF", charset.name())
			);
			assert newUrl.length() > encoded.length();
			assert URLDecoder.decode(encoded, charsetName).equals(URLDecoder.decode(newUrl, charsetName));
			encoded = newUrl;
		}
		TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute(encoded, out);
	}
}
