/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoindustries.i18n.Resources;
import com.aoindustries.io.Encoder;
import com.aoindustries.io.LocalizedUnsupportedEncodingException;
import com.aoindustries.lang.Coercion;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Verify that the data passing through this filter is valid for the provided media type.
 *
 * @author  AO Industries, Inc.
 */
abstract public class MediaValidator extends FilterWriter implements ValidMediaFilter {

	private static final Logger logger = Logger.getLogger(MediaValidator.class.getName());

	private static final Resources RESOURCES = Resources.getResources(MediaValidator.class);

	/**
	 * Gets the media validator for the given type.  If the given writer is
	 * already validator for the requested type, will return the provided writer.
	 *
	 * @exception UnsupportedEncodingException when unable to find an appropriate validator.
	 */
	public static MediaValidator getMediaValidator(MediaType contentType, Writer out) throws UnsupportedEncodingException {
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
			case SH:
				return new ShValidator(out);
			case MYSQL:
				return new MysqlValidator(out);
			case PSQL:
				return new PsqlValidator(out);
			case TEXT:
				return new TextValidator(out);
			case URL:
				return new UrlValidator(out);
			case XHTML:
				return new XhtmlValidator(out);
			case XHTML_ATTRIBUTE:
				return new XhtmlAttributeValidator(out);
			default:
				throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindValidator", contentType.getContentType());
		}
	}

	protected MediaValidator(Writer out) {
		super(out);
	}

	/**
	 * The output type must be the same as the input type.
	 *
	 * @return  The result of {@link #getValidMediaInputType()}
	 */
	@Override
	final public MediaType getValidMediaOutputType() {
		return getValidMediaInputType();
	}

	/**
	 * Gets the wrapped writer.
	 */
	public Writer getOut() {
		return out;
	}

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

	static {
		// Unwrap out to avoid unnecessary validation of known valid output
		Coercion.registerOptimizer((Writer out, Encoder encoder) -> {
			if(encoder instanceof MediaEncoder) {
				MediaEncoder mediaEncoder = (MediaEncoder)encoder;
				assert Assertions.isValidating(out, mediaEncoder.getValidMediaOutputType());
				if(out instanceof MediaValidator) {
					MediaValidator validator = (MediaValidator)out;
					if(validator.canSkipValidation(mediaEncoder.getValidMediaOutputType())) {
						// Can skip validation, write directly to the wrapped output through the encoder
						if(logger.isLoggable(Level.FINER)) {
							logger.finer(
								"Skipping validation of " + mediaEncoder.getValidMediaOutputType()
								+ " into " + validator.getValidMediaInputType()
							);
						}
						return validator.getOut();
					}
				}
			}
			return out;
		});
	}
}
