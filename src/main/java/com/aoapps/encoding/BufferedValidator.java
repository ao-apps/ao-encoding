/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.Strings;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Buffers the content to perform final validation.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public abstract class BufferedValidator extends MediaValidator {

	/**
	 * Buffers all contents to pass to validate.
	 */
	private final StringBuffer buffer;

	BufferedValidator(Writer out, int initialCapacity) {
		super(out);
		this.buffer = new StringBuffer(initialCapacity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return  {@code true} since buffered
	 */
	@Override
	public final boolean isBuffered() {
		return true;
	}

	@Override
	public final void write(int c) {
		buffer.append((char)c);
	}

	@Override
	public final void write(char[] cbuf) {
		buffer.append(cbuf);
	}

	@Override
	public final void write(char[] cbuf, int off, int len) {
		buffer.append(cbuf, off, len);
	}

	@Override
	public final void write(String str) {
		if(str==null) throw new IllegalArgumentException("str is null");
		buffer.append(str);
	}

	@Override
	public final void write(String str, int off, int len) {
		if(str==null) throw new IllegalArgumentException("str is null");
		buffer.append(str, off, off+len);
	}

	@Override
	public final BufferedValidator append(char c) {
		buffer.append(c);
		return this;
	}

	@Override
	public final BufferedValidator append(CharSequence csq) {
		buffer.append(csq);
		return this;
	}

	@Override
	public final BufferedValidator append(CharSequence csq, int start, int end) {
		buffer.append(csq, start, end);
		return this;
	}

	/**
	 * Performs final validation and clears the buffer for reuse.
	 */
	@Override
	public final void validate(boolean trim) throws IOException {
		try {
			validate(trim ? Strings.trim(buffer) : buffer);
		} finally {
			// Tests require buffer reset even on failure
			buffer.setLength(0);
		}
	}

	abstract void validate(CharSequence buffer) throws IOException;
}
