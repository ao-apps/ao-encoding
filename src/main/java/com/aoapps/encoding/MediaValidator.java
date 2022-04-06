/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.Encoder;
import com.aoapps.lang.io.NoClose;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Verifies all characters going through this filter are valid for the given media type.
 *
 * @author  AO Industries, Inc.
 */
public abstract class MediaValidator extends FilterWriter implements ValidMediaFilter {

	private static final Logger logger = Logger.getLogger(MediaValidator.class.getName());

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, MediaValidator.class);

	/**
	 * Gets the media validator for the given type.  If the given writer is
	 * {@linkplain MediaValidator#isValidatingMediaInputType(com.aoapps.encoding.MediaType) already validator for the requested type},
	 * will return the provided writer.
	 * <p>
	 * Please note, the returned validator may be more strict than the requested media type.  It is guaranteed to
	 * forbid at least all the invalid characters of {@code contentType}, but may also forbid more.
	 * </p>
	 * <p>
	 * When the returned {@code validator != out}, {@link #validate(boolean)} must be called to finalize the validation.
	 * When the returned {@code validator == out}, {@link #validate(boolean)} should not be called, since the provided writer
	 * will finalize the validation within its proper scope.
	 * </p>
	 *
	 * @param  out  When is already validating the content type, is returned directly.
	 *              Otherwise, will be optimized via {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 *              then passed through {@link NoCloseMediaValidator#wrap(java.io.Writer)} when {@code out} is {@link NoClose}.
	 *
	 * @return  A new validator or <code>out</code> when the given writer is
	 *          {@linkplain MediaValidator#isValidatingMediaInputType(com.aoapps.encoding.MediaType) already a validator for the requested type}.
	 */
	public static MediaValidator getMediaValidator(MediaType contentType, Writer out) {
		// If the existing out is already validating for this type, use it.
		// This occurs when the existing validator will also catch all invalid characters on the new content type.
		// For example: All invalid characters in XHTML are also invalid in XHTML_ATTRIBUTE (but not the other way around)
		final MediaValidator mvTemp;
		final MediaValidator inputValidator;
		if(
			out instanceof MediaValidator
			&& (mvTemp = (MediaValidator)out).isValidatingMediaInputType(contentType)
		) {
			inputValidator = mvTemp;
		} else {
			// Add filter if needed for the given type
			switch(contentType) {
				case CSS:
					inputValidator = new StyleValidator(out);
					break;
				case JAVASCRIPT:
				case JSON:
				case LD_JSON:
					inputValidator = new JavaScriptValidator(out, contentType);
					break;
				case SH:
					inputValidator = new ShValidator(out);
					break;
				case MYSQL:
					inputValidator = new MysqlValidator(out);
					break;
				case PSQL:
					inputValidator = new PsqlValidator(out);
					break;
				case TEXT:
					inputValidator = new TextValidator(out);
					break;
				case URL:
					inputValidator = new UrlValidator(out);
					break;
				case XHTML:
					inputValidator = new XhtmlValidator(out);
					break;
				case XHTML_ATTRIBUTE:
					inputValidator = new XhtmlAttributeValidator(out);
					break;
				default:
					throw new AssertionError(RESOURCES.getMessage("unableToFindValidator", contentType.getContentType()));
			}
			assert inputValidator.getValidMediaInputType() == contentType :
				"inputValidator.getValidMediaInputType() != contentType: " + inputValidator.getValidMediaInputType().name() + " != " + contentType.name();
		}
		assert inputValidator.isValidatingMediaInputType(contentType) :
			"inputValidator = " + inputValidator.getClass().getName() + " is not validating contentType = " + contentType.name();
		return inputValidator;
	}

	/**
	 * Optimize while maintaining {@link NoClose}
	 */
	private static Writer optimize(Writer out) {
		Writer optimized = Coercion.optimize(out, null);
		// Maintain NoClose
		if(optimized != out && out instanceof NoClose) {
			optimized = NoCloseMediaValidator.wrap(optimized);
		}
		return optimized;
	}

	/**
	 * @param  out  Will be optimized via {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 *              then passed through {@link NoCloseMediaValidator#wrap(java.io.Writer)} when {@code out} is {@link NoClose}.
	 */
	protected MediaValidator(Writer out) {
		super(optimize(out));
	}

	/**
	 * The output type must be the same as the input type.
	 *
	 * @return  The result of {@link #getValidMediaInputType()}
	 */
	@Override
	public final MediaType getValidMediaOutputType() {
		return getValidMediaInputType();
	}

	/**
	 * Gets the wrapped writer, which has been optimized via
	 * {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 * then passed through {@link NoCloseMediaValidator#wrap(java.io.Writer)} when {@code out} is {@link NoClose}.
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

	/**
	 * Performs final validation and resets the validator for reuse.
	 *
	 * @param  trim  Requests that the buffer be trimmed, if buffered and trim supported.
	 */
	@SuppressWarnings("NoopMethodInAbstractClass")
	public void validate(boolean trim) throws IOException {
		// Nothing to do since nothing buffered and everything already validated
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
						Writer newOut = validator.getOut();
						assert !(validator instanceof NoClose) || (newOut instanceof NoClose) : "NoClose must have been maintained during optimized wrapping";
						return newOut;
					}
				}
			}
			return out;
		});
	}
}
