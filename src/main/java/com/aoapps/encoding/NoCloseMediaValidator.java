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

import com.aoapps.lang.io.NoClose;
import com.aoapps.lang.io.NoCloseWriter;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Wraps a {@link MediaValidator} while doing nothing on {@link MediaValidator#close()}.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public final class NoCloseMediaValidator extends MediaValidator {

	/**
	 * Returns {@code out} when it is already a {@link MediaValidator#isNoClose()}, otherwise
	 * returns a new {@link NoCloseMediaValidator} wrapping {@code out}.
	 */
	public static MediaValidator wrap(MediaValidator out) {
		return out.isNoClose() ? out : new NoCloseMediaValidator(out);
	}

	/**
	 * Dispatches to {@link #wrap(com.aoapps.encoding.MediaValidator)} when out is a {@link MediaValidator}, otherwise
	 * dispatches to {@link NoCloseWriter#wrap(java.io.Writer)}.
	 *
	 * @see  #wrap(com.aoapps.encoding.MediaValidator)
	 * @see  NoCloseWriter#wrap(java.io.Writer)
	 */
	@SuppressWarnings("unchecked")
	public static <W extends Writer & NoClose> W wrap(Writer out) {
		if(out instanceof MediaValidator) return (W)wrap((MediaValidator)out);
		return NoCloseWriter.wrap(out);
	}

	private final MediaValidator wrapped;

	/**
	 * @see  #wrap(com.aoapps.encoding.MediaValidator)
	 */
	private NoCloseMediaValidator(MediaValidator out) {
		super(out);
		assert !out.isNoClose() : "Should not have wrapped when already isNoClose()";
		wrapped = out;
	}

	@Override
	public MediaType getValidMediaInputType() {
		return wrapped.getValidMediaInputType();
	}

	@Override
	public boolean isValidatingMediaInputType(MediaType inputType) {
		return wrapped.isValidatingMediaInputType(inputType);
	}

	@Override
	public boolean canSkipValidation(MediaType outputType) {
		return wrapped.canSkipValidation(outputType);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return  the wrapped writer passed through {@link #wrap(java.io.Writer)}
	 *          or {@link #wrap(com.aoapps.encoding.MediaValidator)}.
	 */
	@Override
	public Writer getOut() {
		return wrap(wrapped.getOut());
	}

	@Override
	public NoCloseMediaValidator append(CharSequence csq) throws IOException {
		out.append(csq);
		return this;
	}

	@Override
	public NoCloseMediaValidator append(CharSequence csq, int start, int end) throws IOException {
		out.append(csq, start, end);
		return this;
	}

	@Override
	public NoCloseMediaValidator append(char c) throws IOException {
		out.append(c);
		return this;
	}

	@Override
	public void validate(boolean trim) throws IOException {
		wrapped.validate(trim);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return  {@code true} since this is always no-close.
	 */
	@Override
	public boolean isNoClose() {
		return true;
	}

	/**
	 * Does not close the wrapped validator.
	 */
	@Override
	public void close() {
		// Do nothing
	}
}
