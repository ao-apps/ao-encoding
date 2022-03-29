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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.junit.Test;

public class MediaEncoderTest {

	private static class NoInvalidAllowedAppendable implements Appendable {
		private final MediaValidator canonical;
		private final MediaEncoder encoder;
		private final char ch;
		private NoInvalidAllowedAppendable(
			MediaValidator canonical,
			MediaEncoder encoder,
			char ch
		) {
			this.canonical = canonical;
			this.encoder = encoder;
			this.ch = ch;
		}

		@Override
		public NoInvalidAllowedAppendable append(char c) {
			try {
				canonical.append(c);
			} catch(IOException e) {
				throw new AssertionError(
					String.format(
						"encoder (%s) wrote invalid output for character 0x%X that canonical (%s) caught: 0x%X",
						encoder.getClass().getSimpleName(),
						(int)ch,
						canonical.getClass().getSimpleName(),
						(int)c
					),
					e
				);
			}
			return this;
		}

		@Override
		public NoInvalidAllowedAppendable append(CharSequence csq) {
			try {
				canonical.append(csq);
			} catch(IOException e) {
				throw new AssertionError(
					String.format(
						"encoder (%s) wrote invalid output for character 0x%X that canonical (%s) caught: %s",
						encoder.getClass().getSimpleName(),
						(int)ch,
						canonical.getClass().getSimpleName(),
						csq
					),
					e
				);
			}
			return this;
		}

		@Override
		public NoInvalidAllowedAppendable append(CharSequence csq, int start, int end) {
			try {
				canonical.append(csq, start, end);
			} catch(IOException e) {
				throw new AssertionError(
					String.format(
						"encoder (%s) wrote invalid output for character 0x%X that canonical (%s) caught: %s",
						encoder.getClass().getSimpleName(),
						(int)ch,
						canonical.getClass().getSimpleName(),
						csq.subSequence(start, end)
					),
					e
				);
			}
			return this;
		}
	}

	/**
	 * Tests that all characters output by {@link MediaEncoder} are
	 * {@linkplain MediaValidator#getMediaValidator(com.aoapps.encoding.MediaType, java.io.Writer) valid in the canonical validator}
	 * in the declared {@link MediaEncoder#getValidMediaOutputType()}.
	 */
	@Test
	public void testAllOutputValid() {
		final Writer nullOut = NullWriter.getInstance();
		for(MediaType containerType : MediaType.values()) {
			// MediaValidator
			MediaValidator canonical;
			try {
				canonical = MediaValidator.getMediaValidator(containerType, nullOut);
			} catch(UnsupportedEncodingException e) {
				throw new AssertionError("All media types must have validator implementations: " + containerType.name(), e);
			}
			// MediaEncoder
			for(MediaType contentType : MediaType.values()) {
				try {
					MediaEncoder encoder = MediaEncoder.getInstance(EncodingContext.DEFAULT, contentType, containerType);
					if(encoder != null) {
						for(int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; c++) {
							char ch = (char)c;
							NoInvalidAllowedAppendable noInvalidAllowed = new NoInvalidAllowedAppendable(
								canonical,
								encoder,
								ch
							);
							// No errors should happen in writePrefixTo, all output must be valid
							try {
								encoder.writePrefixTo(noInvalidAllowed);
								canonical.validate(false);
							} catch(IOException e) {
								throw new AssertionError(
									"Error from writePrefixTo: " + encoder.getClass().getSimpleName() + " into "
									+ canonical.getClass().getSimpleName(),
									e
								);
							}
							try {
								// encoder may reject characters, but any rejected by canonical are a problem
								encoder.append(ch, noInvalidAllowed);
								encoder.writeSuffixTo(noInvalidAllowed, false);
							} catch(IOException e) {
								// Invalid character caught by encoder: OK
							}
							// Canonical may have buffered validation
							try {
								canonical.validate(false);
							} catch(IOException e) {
								throw new AssertionError(
									"Error from final canonical validation: " + encoder.getClass().getSimpleName() + " into "
									+ canonical.getClass().getSimpleName(),
									e
								);
							}
						}
					}
				} catch(UnsupportedEncodingException e) {
					// OK if not supported
				}
			}
		}
	}
}
