/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;

/**
 * @deprecated  Use {@link com.aoapps.lang.Coercion} or {@link MarkupCoercion} instead.
 */
@Deprecated
public abstract class Coercion {

	/** Make no instances. */
	private Coercion() {throw new AssertionError();}

	/**
	 * @deprecated  Use {@link com.aoapps.lang.Coercion#toString(java.lang.Object)} instead.
	 */
	@Deprecated
	public static String toString(Object value) {
		return com.aoapps.lang.Coercion.toString(value);
	}

	/**
	 * @deprecated  Use {@link com.aoapps.lang.Coercion#write(java.lang.Object, java.io.Writer)} instead.
	 */
	@Deprecated
	public static void write(Object value, Writer out) throws IOException {
		com.aoapps.lang.Coercion.write(value, out);
	}

	/**
	 * @deprecated  Use {@link com.aoapps.lang.Coercion#write(java.lang.Object, com.aoapps.lang.io.Encoder, java.io.Writer)} instead.
	 */
	@Deprecated
	public static void write(Object value, MediaEncoder encoder, Writer out) throws IOException {
		assert encoder == null || Assertions.isValidating(out, encoder.getValidMediaOutputType());
		com.aoapps.lang.Coercion.write(value, encoder, out);
	}

	/**
	 * @deprecated  Use {@link MarkupCoercion#write(java.lang.Object, com.aoapps.util.i18n.MarkupType, java.io.Writer)} instead.
	 */
	@Deprecated
	public static void write(Object value, MarkupType markupType, Writer out) throws IOException {
		MarkupCoercion.write(value, markupType, out);
	}

	/**
	 * @deprecated  Use {@link MarkupCoercion#write(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.lang.io.Encoder, boolean, java.io.Writer)} instead.
	 */
	@Deprecated
	public static void write(Object value, MarkupType markupType, boolean encodeLookupMarkup, MediaEncoder encoder, boolean encoderPrefixSuffix, Writer out) throws IOException {
		assert encoder == null || Assertions.isValidating(out, encoder.getValidMediaOutputType());
		MarkupCoercion.write(value, markupType, encodeLookupMarkup, encoder, encoderPrefixSuffix, out);
	}

	/**
	 * @deprecated  Lookup markup may conditionally need to be encoded based on markup type and context.  Please use
	 *              {@link #write(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.encoding.MediaEncoder, boolean, java.io.Writer)}
	 *              while determining whether the lookup markup should be written through the encoder.
	 *              <p>
	 *              This method defaults to {@code encodeLookupMarkup = false} for compatibility with the previous release.
	 *              </p>
	 */
	@Deprecated
	public static void write(Object value, MarkupType markupType, MediaEncoder encoder, boolean encoderPrefixSuffix, Writer out) throws IOException {
		write(value, markupType, false, encoder, encoderPrefixSuffix, out);
	}

	/**
	 * @deprecated  Use {@link com.aoapps.lang.Coercion#append(java.lang.Object, java.lang.Appendable)} instead.
	 */
	@Deprecated
	public static void append(Object value, Appendable out) throws IOException {
		com.aoapps.lang.Coercion.append(value, out);
	}

	/**
	 * @deprecated  Use {@link com.aoapps.lang.Coercion#append(java.lang.Object, com.aoapps.lang.io.Encoder, java.lang.Appendable)} instead.
	 */
	@Deprecated
	public static void append(Object value, MediaEncoder encoder, Appendable out) throws IOException {
		assert encoder == null || Assertions.isValidating(out, encoder.getValidMediaOutputType());
		com.aoapps.lang.Coercion.append(value, encoder, out);
	}

	/**
	 * @deprecated  Use {@link MarkupCoercion#append(java.lang.Object, com.aoapps.util.i18n.MarkupType, java.lang.Appendable)} instead.
	 */
	@Deprecated
	public static void append(Object value, MarkupType markupType, Appendable out) throws IOException {
		MarkupCoercion.append(value, markupType, out);
	}

	/**
	 * @deprecated  Use {@link MarkupCoercion#append(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.lang.io.Encoder, boolean, java.lang.Appendable)} instead.
	 */
	@Deprecated
	public static void append(Object value, MarkupType markupType, boolean encodeLookupMarkup, MediaEncoder encoder, boolean encoderPrefixSuffix, Appendable out) throws IOException {
		assert encoder == null || Assertions.isValidating(out, encoder.getValidMediaOutputType());
		MarkupCoercion.append(value, markupType, encodeLookupMarkup, encoder, encoderPrefixSuffix, out);
	}

	/**
	 * @deprecated  Lookup markup may conditionally need to be encoded based on markup type and context.  Please use
	 *              {@link #append(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.encoding.MediaEncoder, boolean, java.lang.Appendable)}
	 *              while determining whether the lookup markup should be written through the encoder.
	 *              <p>
	 *              This method defaults to {@code encodeLookupMarkup = false} for compatibility with the previous release.
	 *              </p>
	 */
	@Deprecated
	public static void append(Object value, MarkupType markupType, MediaEncoder encoder, boolean encoderPrefixSuffix, Appendable out) throws IOException {
		append(value, markupType, false, encoder, encoderPrefixSuffix, out);
	}

	/**
	 * @deprecated  Use {@link com.aoapps.lang.Coercion#isEmpty(java.lang.Object)} instead.
	 */
	@Deprecated
	public static boolean isEmpty(Object value) throws IOException {
		return com.aoapps.lang.Coercion.isEmpty(value);
	}

	/**
	 * @deprecated  Use {@link com.aoapps.lang.Coercion#nullIfEmpty(java.lang.Object)} instead.
	 */
	@Deprecated
	public static Object nullIfEmpty(Object value) throws IOException {
		return com.aoapps.lang.Coercion.nullIfEmpty(value);
	}

	/**
	 * @deprecated  Use {@link com.aoapps.lang.Coercion#trim(java.lang.Object)} instead.
	 */
	@Deprecated
	public static Object trim(Object value) throws IOException {
		return com.aoapps.lang.Coercion.trim(value);
	}

	/**
	 * @deprecated  Use {@link com.aoapps.lang.Coercion#trimNullIfEmpty(java.lang.Object)} instead.
	 */
	@Deprecated
	public static Object trimNullIfEmpty(Object value) throws IOException {
		return com.aoapps.lang.Coercion.trimNullIfEmpty(value);
	}

	/**
	 * Returns the provided number or zero if the value is empty.
	 *
	 * @deprecated  This is out-of-place since it has nothing to do with String coercion.
	 *              Use inline {@code (value == null) ? 0 : value} instead.
	 */
	@Deprecated
	public static int zeroIfEmpty(Integer value) throws IOException {
		return (value == null) ? 0 : value;
	}
}
