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

import java.io.IOException;

/**
 * @param  <Ex>  An arbitrary exception type that may be thrown
 *
 * @author  AO Industries,Inc.
 */
@FunctionalInterface
public interface LdJsonWritable<Ex extends Throwable> extends JsonWritable<Ex> {

	@Override
	default void writeTo(JsonWriter writer) throws IOException, Ex {
		if(writer instanceof LdJsonWriter) {
			writeTo((LdJsonWriter)writer);
		} else {
			throw new AssertionError("Expected " + LdJsonWriter.class.getName() + ", got " + (writer == null ? null : writer.getClass().getName()));
		}
	}

	void writeTo(LdJsonWriter writer) throws IOException, Ex;
}
