/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2021  AO Industries, Inc.
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
import java.io.Writer;

/**
 * Buffers the content to perform final validation.
 *
 * @author  AO Industries, Inc.
 */
public abstract class BufferedValidator extends MediaValidator {

	/**
	 * Buffers all contents to pass to validate.
	 */
	private final StringBuilder buffer;

	protected BufferedValidator(Writer out, int initialCapacity) {
		super(out);
		this.buffer = new StringBuilder(initialCapacity);
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
	public final void validate() throws IOException {
		validate(buffer);
		buffer.setLength(0);
	}

	protected abstract void validate(StringBuilder buffer) throws IOException;
}
