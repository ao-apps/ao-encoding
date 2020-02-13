/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2016, 2019, 2020  AO Industries, Inc.
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

/**
 * The current encoding context may perform URL rewriting.
 *
 * @author  AO Industries, Inc.
 */
public interface EncodingContext {

	/**
	 * The default doctype for older implementations that do not set any.
	 * Also used when there is no context available.
	 */
	Doctype DEFAULT_DOCTYPE = Doctype.HTML5;

	/**
	 * The default serialization for older implementations that do not set any.
	 * Also used when there is no context available.
	 */
	Serialization DEFAULT_SERIALIZATION = Serialization.XML;

	/**
	 * Encodes a URL for the current encoding context.
	 * The resulting URL must be valid <a href="https://tools.ietf.org/html/rfc3986">RFC 3986</a>.
	 * <p>
	 * TODO: Allow RFC 3987, too
	 * </p>
	 */
	String encodeURL(String url);

	/**
	 * The current doctype.
	 */
	default Doctype getDoctype() {
		return DEFAULT_DOCTYPE;
	}

	/**
	 * The current serialization.
	 */
	default Serialization getSerialization() {
		return DEFAULT_SERIALIZATION;
	}
}
