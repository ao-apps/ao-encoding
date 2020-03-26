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
	 */
	// TODO: Move to Doctype.DEFAULT?
	Doctype DEFAULT_DOCTYPE = Doctype.HTML5;

	/**
	 * The default serialization for older implementations that do not set any.
	 */
	// TODO: Move to Serialization.DEFAULT?
	Serialization DEFAULT_SERIALIZATION = Serialization.XML;

	/**
	 * Default encoding context.
	 * Also used when there is no context available.
	 *
	 * @see  #DEFAULT_DOCTYPE
	 * @see  #DEFAULT_SERIALIZATION
	 */
	EncodingContext DEFAULT = new EncodingContext() {};

	/**
	 * Encoding context for XML always.
	 *
	 * @see  #DEFAULT_DOCTYPE
	 * @see  Serialization#XML
	 */
	EncodingContext XML = new EncodingContext() {
		@Override
		public Serialization getSerialization() {
			return Serialization.XML;
		}
	};

	/**
	 * Encoding context for SGML always.
	 *
	 * @see  #DEFAULT_DOCTYPE
	 * @see  Serialization#SGML
	 */
	EncodingContext SGML = new EncodingContext() {
		@Override
		public Serialization getSerialization() {
			return Serialization.SGML;
		}
	};

	/**
	 * Encodes a URL for the current encoding context.
	 * The resulting URL must be valid <a href="https://tools.ietf.org/html/rfc3986">RFC 3986</a>.
	 * <p>
	 * TODO: Allow RFC 3987, too
	 * </p>
	 * <p>
	 * Defaults to performing no encoding.
	 * </p>
	 */
	default String encodeURL(String url) {
		return url;
	}

	/**
	 * The current doctype.
	 * <p>
	 * Defaults to {@link #DEFAULT_DOCTYPE}.
	 * </p>
	 */
	default Doctype getDoctype() {
		return DEFAULT_DOCTYPE;
	}

	/**
	 * The current serialization.
	 * <p>
	 * Defaults to {@link #DEFAULT_SERIALIZATION}.
	 * </p>
	 */
	default Serialization getSerialization() {
		return DEFAULT_SERIALIZATION;
	}
}
