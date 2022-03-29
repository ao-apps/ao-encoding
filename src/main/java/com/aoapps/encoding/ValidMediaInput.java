/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2015, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
	 * Checks if this is validating the provided type, which allows one validator to be substituted in place of another.
	 * This is acceptable when this validator is equal to, or more strict, than the given {@code inputType}.
	 * <p>
	 * Please note that this validator only needs to block invalid characters for {@code inputType}.  This validator
	 * does not need to let through all characters, just block the invalid.  This is a one-way optimization.
	 * </p>
	 * <p>
	 * This is almost the inverse operation of {@link #isValidatingMediaInputType(com.aoapps.encoding.MediaType)}, with
	 * the exception of characters only invalid due to nested encodings.
	 * </p>
	 *
	 * @see  #canSkipValidation(com.aoapps.encoding.MediaType)
	 *
	 * @return {@code true} when this validator will throw exceptions on all invalid characters from the given {@code inputType}
	 */
	boolean isValidatingMediaInputType(MediaType inputType);

	/**
	 * Checks if validation may be skipped when the characters being written to this
	 * validator is already known to be valid with the given media type.  This may be done when every valid character
	 * for the given media type is also valid within this validator.
	 * <p>
	 * This is almost the inverse operation of {@link #isValidatingMediaInputType(com.aoapps.encoding.MediaType)}, with
	 * the exception of characters only invalid due to nested encodings.
	 * </p>
	 *
	 * @see  #isValidatingMediaInputType(com.aoapps.encoding.MediaType)
	 *
	 * @return {@code true} when all valid characters of the given {@code outputType} are also valid on this validator
	 */
	boolean canSkipValidation(MediaType outputType);
}
