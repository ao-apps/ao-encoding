/*
 * ao-encoding - High performance character encoding.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016  AO Industries, Inc.
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

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Verify that the data passing through this filter is valid for the provided media type.
 *
 * @author  AO Industries, Inc.
 */
abstract public class MediaValidator extends FilterWriter implements ValidMediaFilter {

	/**
	 * Gets the media validator for the given type.  If the given writer is
	 * already validator for the requested type, will return the provided writer.
	 *
	 * @exception MediaException when unable to find an appropriate validator.
	 */
	public static MediaValidator getMediaValidator(MediaType contentType, Writer out) throws MediaException {
		// If the existing out is already validating for this type, use it.
		// This occurs when one validation validates to a set of characters that are a subset of the requested validator.
		// For example, a URL is always valid TEXT.
		if(out instanceof MediaValidator) {
			MediaValidator inputValidator = (MediaValidator)out;
			if(inputValidator.isValidatingMediaInputType(contentType)) return inputValidator;
		}
		// Add filter if needed for the given type
		switch(contentType) {
			case JAVASCRIPT:
			case JSON:
			case LD_JSON:
				return new JavaScriptValidator(out, contentType);
			case TEXT:
				return new TextValidator(out);
			case URL:
				return new UrlValidator(out);
			case XHTML:
				return new XhtmlValidator(out);
			case XHTML_ATTRIBUTE:
				return new XhtmlAttributeValidator(out);
			default:
				throw new MediaException(ApplicationResources.accessor.getMessage("MediaValidator.unableToFindValidator", contentType.getContentType()));
		}
	}

	protected MediaValidator(Writer out) {
		super(out);
	}

	/**
	 * Gets the wrapped writer.
	 */
	public Writer getOut() {
		return out;
	}

	/**
	 * Checks if validation may be skipped when the data being written to this
	 * validator is already known to be valid with the given media type.
	 */
	abstract public boolean canSkipValidation(MediaType inputType);

	/**
	 * The default implementation of this append method in Writer converts
	 * to a String for backward-compatibility.  This passes the append directly
	 * to the wrapped Writer.
	 */
	@Override
	public MediaValidator append(CharSequence csq) throws IOException {
		out.append(csq);
		return this;
	}

	/**
	 * The default implementation of this append method in Writer converts
	 * to a String for backward-compatibility.  This passes the append directly
	 * to the wrapped Writer.
	 */
	@Override
	public MediaValidator append(CharSequence csq, int start, int end) throws IOException {
		out.append(csq, start, end);
		return this;
	}

	/**
	 * The default implementation of this append method in Writer calls
	 * the write(int) method for backward-compatibility.  This passes the
	 * append directly to the wrapped Writer.
	 */
	@Override
	public MediaValidator append(char c) throws IOException {
		out.append(c);
		return this;
	}
}
