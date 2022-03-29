/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2022  AO Industries, Inc.
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

import com.aoapps.lang.Throwables;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.LocalizedIOException;
import java.io.Serializable;

/**
 * Exception thrown when an invalid character is detected.
 *
 * @author  AO Industries, Inc.
 */
public class InvalidCharacterException extends LocalizedIOException {

	private static final long serialVersionUID = 1L;

	public InvalidCharacterException(Resources resources, String key, Serializable... args) {
		super(resources, key, args);
	}

	public InvalidCharacterException(Throwable cause, Resources resources, String key, Serializable... args) {
		super(cause, resources, key, args);
	}

	static {
		Throwables.registerSurrogateFactory(InvalidCharacterException.class, (template, cause) ->
			new InvalidCharacterException(cause, template.getResources(), template.getKey(), template.getArgs())
		);
	}
}
