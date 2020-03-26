/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2015, 2016, 2019, 2020  AO Industries, Inc.
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
 * Indicates that the object validates its input for the provided type.
 * If invalid characters are received it will throw an appropriate exception.
 * When the input is already being validated against equal or more restrictive
 * filtering, the redundant validation is not performed.
 *
 * @author  AO Industries, Inc.
 */
public interface ValidMediaInput {

	/**
	 * Gets the input type.
	 */
	MediaType getValidMediaInputType();

	/**
	 * Checks if this is validating the provided type.
	 */
	boolean isValidatingMediaInputType(MediaType inputType);

	/**
	 * Checks if validation may be skipped when the data being written to this
	 * validator is already known to be valid with the given media type.
	 */
	boolean canSkipValidation(MediaType inputType);
}
