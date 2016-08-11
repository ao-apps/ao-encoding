/*
 * ao-encoding - High performance character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2015, 2016  AO Industries, Inc.
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
 * Indicates that at object can be trusted to generate output with only
 * valid characters for the provided type.  This will allow input validation
 * of the same type to be skipped.
 *
 * Note: This is currently not used to skip input validation.  Input validation
 * is always performed as a means of catching bugs the result in incorrect output
 * of nested content.
 *
 * @author  AO Industries, Inc.
 */
public interface ValidMediaOutput {

	/**
	 * Gets the output type.
	 */
	MediaType getValidMediaOutputType();
}
