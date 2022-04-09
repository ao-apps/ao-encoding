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

import com.aoapps.lang.io.NullWriter;
import java.io.UnsupportedEncodingException;
import java.util.EnumSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class MediaWriterTest {

	/**
	 * Tests that all {@link MediaWriter} implementations have a set of per-type interfaces precisely
	 * matching the encoders supported by {@link MediaEncoder#getInstance(com.aoapps.encoding.EncodingContext, com.aoapps.encoding.MediaType, com.aoapps.encoding.MediaType)}.
	 */
	@Test
	public void testPerTypeInterfacesMatchEncoders() {
		for(MediaType containerType : MediaType.values()) {
			MediaWriter mediaWriter = containerType.newMediaWriter(EncodingContext.DEFAULT, new ValidateOnlyEncoder(containerType), NullWriter.getInstance());
			// Find the set of supported MediaEncoder
			Set<MediaType> supportedEncoders = EnumSet.noneOf(MediaType.class);
			// Find the set of implemented interfaces
			Set<MediaType> implementedInterfaces = EnumSet.noneOf(MediaType.class);
			for(MediaType contentType : MediaType.values()) {
				try {
					MediaEncoder.getInstance(EncodingContext.DEFAULT, contentType, containerType);
					supportedEncoders.add(contentType);
				} catch(UnsupportedEncodingException e) {
					// Not supported
				}
				assertTrue(contentType.getEncodeInterface().isInterface());
				if(contentType.getEncodeInterface().isInstance(mediaWriter)) {
					implementedInterfaces.add(contentType);
				}
			}
			assertEquals("Set of per-type interfaces must match set of supported media encoders", supportedEncoders, implementedInterfaces);
		}
	}
}
