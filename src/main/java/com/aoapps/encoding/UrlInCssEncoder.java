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

import com.aoapps.lang.Strings;
import java.io.IOException;

/**
 * Encodes a URL into CSS, using {@link EncodingContext#encodeURL(java.lang.String)}
 * to rewrite the URL as needed and surrounds it in <code>url("…")</code>.
 * <p>
 * See <a href="https://www.w3.org/TR/CSS2/syndata.html#uri">4.3.4 URLs and URIs</a>.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class UrlInCssEncoder extends BufferedEncoder {

	private final EncodingContext encodingContext;

	UrlInCssEncoder(EncodingContext encodingContext) {
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
			inputType == MediaType.URL
			|| inputType == MediaType.TEXT        // No validation required
		;
	}

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return inputType == MediaType.URL;
	}

	@Override
	public MediaType getValidMediaOutputType() {
		return MediaType.CSS;
	}

	@Override
	public void writePrefixTo(Appendable out) throws IOException {
		super.writePrefixTo(out);
		out.append("url(\"");
	}

	@Override
	@SuppressWarnings("StringEquality")
	protected void writeSuffix(StringBuilder buffer, Appendable out) throws IOException {
		String url = Strings.trim(buffer).toString();
		UrlValidator.checkCharacters(url, 0, url.length());
		String encoded;
		if(encodingContext != null) {
			encoded = encodingContext.encodeURL(url);
			if(encoded != url) UrlValidator.checkCharacters(encoded, 0, encoded.length());
		} else {
			encoded = url;
		}
		TextInCssEncoder.encodeTextInCss(encoded, out);
		out.append("\")");
	}
}
