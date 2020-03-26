/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2020  AO Industries, Inc.
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

import java.io.Writer;

/**
 * Utility methods helping with assertions.
 *
 * @author  AO Industries, Inc.
 */
final class Assertions  {

	static boolean isValidating(ValidMediaInput out, MediaType mediaType) {
		return out.canSkipValidation(mediaType) || out.isValidatingMediaInputType(mediaType);
	}

	static boolean isValidating(Appendable out, MediaType mediaType) {
		return !(out instanceof ValidMediaInput) || isValidating((ValidMediaInput)out, mediaType);
	}

	static boolean isValidating(Writer out, MediaType mediaType) {
		return !(out instanceof ValidMediaInput) || isValidating((ValidMediaInput)out, mediaType);
	}

	/**
	 * Make no instances.
	 */
	private Assertions() {
	}
}
