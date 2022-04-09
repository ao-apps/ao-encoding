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
import java.io.Writer;

/**
 * Passes through all characters unaltered after validating with the given validator.
 * This is useful when an encoder is needed for API requirements (such as required for {@link MediaWriter}),
 * despite no character encoding needed.
 *
 * @author  AO Industries, Inc.
 */
public class ValidateOnlyEncoder extends MediaEncoder {

	private final MediaValidator validator;

	/**
	 * Writes through to the given validator.
	 */
	public ValidateOnlyEncoder(MediaValidator validator) {
		this.validator = validator;
	}

	/**
	 * Writes through to the correct validator for the given content type, then discards all output.
	 *
	 * @see  MediaValidator#getMediaValidator(com.aoapps.encoding.MediaType, java.io.Writer)
	 * @see  NullWriter#getInstance()
	 */
	public ValidateOnlyEncoder(MediaType contentType) {
		this(MediaValidator.getMediaValidator(contentType, NullWriter.getInstance()));
		assert validator.getValidMediaInputType() == contentType;
	}

	@Override
	public MediaType getValidMediaInputType() {
		return validator.getValidMediaInputType();
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return validator.isValidatingMediaInputType(inputType);
	}

	@Override
	public boolean canSkipValidation(MediaType outputType) {
		return validator.canSkipValidation(outputType);
	}

	@Override
	public MediaType getValidMediaOutputType() {
		assert validator.getValidMediaInputType() == validator.getValidMediaOutputType();
		return validator.getValidMediaOutputType();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return  The result of {@link MediaValidator#isBuffered()}
	 */
	@Override
	public final boolean isBuffered() {
		return validator.isBuffered();
	}

	@Override
	public void write(int c, Writer out) throws IOException {
		assert Assertions.isValidating(out, validator.getValidMediaInputType());
		validator.write(c);
		out.write(c);
	}

	@Override
	public void write(char[] cbuf, Writer out) throws IOException {
		assert Assertions.isValidating(out, validator.getValidMediaInputType());
		validator.write(cbuf);
		out.write(cbuf);
	}

	@Override
	public void write(char[] cbuf, int off, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, validator.getValidMediaInputType());
		validator.write(cbuf, off, len);
		out.write(cbuf, off, len);
	}

	@Override
	public void write(String str, Writer out) throws IOException {
		assert Assertions.isValidating(out, validator.getValidMediaInputType());
		validator.write(str);
		out.write(str);
	}

	@Override
	public void write(String str, int off, int len, Writer out) throws IOException {
		assert Assertions.isValidating(out, validator.getValidMediaInputType());
		validator.write(str, off, len);
		out.write(str, off, len);
	}

	@Override
	public ValidateOnlyEncoder append(char c, Appendable out) throws IOException {
		assert Assertions.isValidating(out, validator.getValidMediaInputType());
		validator.append(c);
		out.append(c);
		return this;
	}

	@Override
	public ValidateOnlyEncoder append(CharSequence csq, Appendable out) throws IOException {
		assert Assertions.isValidating(out, validator.getValidMediaInputType());
		validator.append(csq);
		out.append(csq);
		return this;
	}

	@Override
	public ValidateOnlyEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException {
		assert Assertions.isValidating(out, validator.getValidMediaInputType());
		validator.append(csq, start, end);
		out.append(csq, start, end);
		return this;
	}

	@Override
	public void writeSuffixTo(Appendable out, boolean trim) throws IOException {
		super.writeSuffixTo(out, trim);
		validator.validate(trim);
	}
}
