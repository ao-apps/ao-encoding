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

/**
 * Encodes a URL into a JavaScript string.  It {@link EncodingContext#encodeURL(java.lang.String)}
 * to rewrite the URL as needed and surrounds it in double quotes.
 *
 * @author  AO Industries, Inc.
 */
public class UrlInJavaScriptEncoder extends BufferedEncoder {

	private final MediaType outputType;
	private final EncodingContext encodingContext;

	UrlInJavaScriptEncoder(MediaType outputType, EncodingContext encodingContext) {
		super(128);
		if(
			outputType != MediaType.JAVASCRIPT
			&& outputType != MediaType.JSON
			&& outputType != MediaType.LD_JSON
		) {
			throw new IllegalArgumentException("Unsupported output type: " + outputType);
		}
		this.outputType = outputType;
		this.encodingContext = encodingContext;
	}

	@Override
	public MediaType getValidMediaInputType() {
		return MediaType.URL;
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return
			inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in URL in JAVASCRIPT
			|| inputType == MediaType.JSON // All invalid characters in JSON are also invalid in URL in JAVASCRIPT
			|| inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in URL in JAVASCRIPT
			|| inputType == MediaType.TEXT // All invalid characters in TEXT are also invalid in URL in JAVASCRIPT
			|| inputType == MediaType.URL // All invalid characters in URL are also invalid in URL in JAVASCRIPT
		;
	}

	@Override
	public boolean canSkipValidation(MediaType outputType) {
		return
			outputType == MediaType.URL // All valid characters in URL are also valid in URL in JAVASCRIPT
		;
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
		TextInJavaScriptEncoder.encodeTextInJavaScript(encoded, out);
		out.append('"');
	}
}
