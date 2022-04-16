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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.concurrent.NotThreadSafe;
import org.junit.Before;
import org.junit.Test;

@NotThreadSafe
public class ValidMediaInputTest {

	private List<ValidMediaInput> validators;

	@Before
	public void initialize() {
		final Writer nullOut = NullWriter.getInstance();
		// Create all the known types of ValidMediaInput
		validators = new ArrayList<>();
		for(MediaType contentType : MediaType.values()) {
			// MediaValidator
			validators.add(MediaValidator.getMediaValidator(contentType, nullOut));
			// MediaEncoder
			for(MediaType containerType : MediaType.values()) {
				try {
					MediaEncoder encoder = MediaEncoder.getInstance(EncodingContext.DEFAULT, contentType, containerType);
					if(encoder != null) {
						validators.add(encoder);
					}
				} catch(UnsupportedEncodingException e) {
					// OK if not supported
				}
			}
		}
	}

	/**
	 * <p>
	 * Tests that all true results of {@link ValidMediaInput#isValidatingMediaInputType(com.aoapps.encoding.MediaType)}
	 * catch all characters that are invalid in the media type.  Characters are compared versus the media-type
	 * specific validators from {@link MediaValidator#getMediaValidator(com.aoapps.encoding.MediaType, java.io.Writer)}.
	 * </p>
	 * <p>
	 * Tests that the implementation of {@link ValidMediaInput#isValidatingMediaInputType(com.aoapps.encoding.MediaType)}
	 * is maximally broad for most optimized implementation.  All possible character values are compared, and if any
	 * validator is determined to disallow all characters, it will ensure a true result from
	 * {@link ValidMediaInput#isValidatingMediaInputType(com.aoapps.encoding.MediaType)}.
	 * </p>
	 */
	@Test
	public void testIsValidatingMediaInputTypeConsistency() {
		final Writer nullOut = NullWriter.getInstance();
		for(ValidMediaInput validator : validators) {
			for(MediaType inputType : MediaType.values()) {
				// Find canonical validator for comparison
				MediaValidator canonical = MediaValidator.getMediaValidator(inputType, nullOut);
				if(validator.isValidatingMediaInputType(inputType)) {
					// Check all possible characters
					for(int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; c++) {
						char ch = (char)c;
						boolean canonicalInvalid;
						try {
							canonical.write(ch);
							canonical.validate(false);
							canonicalInvalid = false;
						} catch(IOException e) {
							assert e instanceof InvalidCharacterException;
							canonicalInvalid = true;
						}
						if(canonicalInvalid) {
							boolean validatorInvalid;
							try {
								if(validator instanceof MediaValidator) {
									MediaValidator mv = (MediaValidator)validator;
									mv.append(ch);
									mv.validate(false);
								} else if(validator instanceof MediaEncoder) {
									MediaEncoder me = (MediaEncoder)validator;
									me.writePrefixTo(nullOut);
									me.append(ch, nullOut);
									me.writeSuffixTo(nullOut, false);
								} else {
									throw new AssertionError("Unexpected type of validator: " + validator.getClass().getName());
								}
								validatorInvalid = false;
							} catch(IOException e2) {
								assert e2 instanceof InvalidCharacterException;
								validatorInvalid = true;
							}
							// The validator must also catch the invalid character
							if(!validatorInvalid) {
								throw new AssertionError(
									String.format(
										"canonical (%s) caught invalid character that validator (%s) did not: 0x%X",
										canonical.getClass().getSimpleName(),
										validator.getClass().getSimpleName(),
										c
									)
								);
							}
						}
					}
				} else {
					// If this validator also catches all illegal characters from the canonical validator, it should also
					// declare the inputType in isValidatingMediaInputType
					boolean overlap = true;
					for(int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; c++) {
						char ch = (char)c;
						boolean canonicalInvalid;
						try {
							canonical.append(ch);
							canonical.validate(false);
							canonicalInvalid = false;
						} catch(IOException e) {
							assert e instanceof InvalidCharacterException;
							canonicalInvalid = true;
						}
						if(canonicalInvalid) {
							boolean validatorInvalid;
							try {
								if(validator instanceof MediaValidator) {
									MediaValidator mv = (MediaValidator)validator;
									mv.write(ch);
									mv.validate(false);
								} else if(validator instanceof MediaEncoder) {
									MediaEncoder me = (MediaEncoder)validator;
									me.writePrefixTo(nullOut);
									me.write(ch, nullOut);
									me.writeSuffixTo(nullOut, false);
								} else {
									throw new AssertionError("Unexpected type of validator: " + validator.getClass().getName());
								}
								validatorInvalid = false;
							} catch(IOException e2) {
								assert e2 instanceof InvalidCharacterException;
								validatorInvalid = true;
							}
							if(!validatorInvalid) {
								overlap = false;
								break;
							}
						}
					}
					if(overlap) {
						throw new AssertionError(
							String.format(
								"All invalid characters in canonical (%s) are also invalid in validator (%s), "
								+ "validator should also declare this media type in isValidatingMediaInputType(inputType = %s)",
								canonical.getClass().getSimpleName(),
								validator.getClass().getSimpleName(),
								inputType.name()
							)
						);
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * Tests that all true results of {@link ValidMediaInput#canSkipValidation(com.aoapps.encoding.MediaType)}
	 * allow all characters that are valid in the media type.  Characters are compared versus the media-type
	 * specific validators from {@link MediaValidator#getMediaValidator(com.aoapps.encoding.MediaType, java.io.Writer)}.
	 * </p>
	 * <p>
	 * Tests that the implementation of {@link ValidMediaInput#canSkipValidation(com.aoapps.encoding.MediaType)}
	 * is maximally broad for most optimized implementation.  All possible character values are compared, and if any
	 * validator is determined to allow all characters, it will ensure a true result from
	 * {@link ValidMediaInput#canSkipValidation(com.aoapps.encoding.MediaType)}.
	 * </p>
	 */
	@Test
	public void testCanSkipValidation() {
		final Writer nullOut = NullWriter.getInstance();
		for(ValidMediaInput validator : validators) {
			for(MediaType outputType : MediaType.values()) {
				// Find canonical validator for comparison
				MediaValidator canonical = MediaValidator.getMediaValidator(outputType, nullOut);
				if(validator.canSkipValidation(outputType)) {
					// Check all possible characters
					for(int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; c++) {
						char ch = (char)c;
						boolean canonicalValid;
						try {
							canonical.append(ch);
							canonical.validate(false);
							canonicalValid = true;
						} catch(IOException e) {
							assert e instanceof InvalidCharacterException;
							canonicalValid = false;
						}
						if(canonicalValid) {
							boolean validatorValid;
							try {
								if(validator instanceof MediaValidator) {
									MediaValidator mv = (MediaValidator)validator;
									mv.write(ch);
									mv.validate(false);
								} else if(validator instanceof MediaEncoder) {
									MediaEncoder me = (MediaEncoder)validator;
									me.writePrefixTo(nullOut);
									me.write(ch, nullOut);
									me.writeSuffixTo(nullOut, false);
								} else {
									throw new AssertionError("Unexpected type of validator: " + validator.getClass().getName());
								}
								validatorValid = true;
							} catch(IOException e2) {
								assert e2 instanceof InvalidCharacterException;
								validatorValid = false;
							}
							// The validator must also allow the valid character
							if(!validatorValid) {
								throw new AssertionError(
									String.format(
										"canonical (%s) allows valid character that validator (%s) did not: 0x%X",
										canonical.getClass().getSimpleName(),
										validator.getClass().getSimpleName(),
										c
									)
								);
							}
						}
					}
				} else {
					// If this validator also allows all legal characters from the canonical validator, it should also
					// declare the outputType in canSkipValidation
					boolean overlap = true;
					for(int c = Character.MIN_VALUE; c <= Character.MAX_VALUE; c++) {
						char ch = (char)c;
						boolean canonicalValid;
						try {
							canonical.write(ch);
							canonical.validate(false);
							canonicalValid = true;
						} catch(IOException e) {
							assert e instanceof InvalidCharacterException;
							canonicalValid = false;
						}
						if(canonicalValid) {
							boolean validatorValid;
							try {
								if(validator instanceof MediaValidator) {
									MediaValidator mv = (MediaValidator)validator;
									mv.append(ch);
									mv.validate(false);
								} else if(validator instanceof MediaEncoder) {
									MediaEncoder me = (MediaEncoder)validator;
									me.writePrefixTo(nullOut);
									me.append(ch, nullOut);
									me.writeSuffixTo(nullOut, false);
								} else {
									throw new AssertionError("Unexpected type of validator: " + validator.getClass().getName());
								}
								validatorValid = true;
							} catch(IOException e2) {
								assert e2 instanceof InvalidCharacterException;
								validatorValid = false;
							}
							if(!validatorValid) {
								overlap = false;
								break;
							}
						}
					}
					if(overlap) {
						throw new AssertionError(
							String.format(
								"All valid characters in canonical (%s) are also valid in validator (%s), "
								+ "validator should also declare this media type in canSkipValidation(outputType = %s)",
								canonical.getClass().getSimpleName(),
								validator.getClass().getSimpleName(),
								outputType.name()
							)
						);
					}
				}
			}
		}
	}
}
