/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2013, 2015, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

/**
 * Buffers the content to perform final output on writeSuffix.
 *
 * @author  AO Industries, Inc.
 */
public abstract class BufferedEncoder extends MediaEncoder {

	/**
	 * Buffers all contents to pass to writeSuffix.
	 */
	private final StringBuilder buffer;

	protected BufferedEncoder(int initialCapacity) {
		this.buffer = new StringBuilder(initialCapacity);
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
	public final void write(int c, Writer out) {
		assert Assertions.isValidating(out, getValidMediaOutputType());
		buffer.append((char)c);
	}

	@Override
	public final void write(char[] cbuf, Writer out) {
		assert Assertions.isValidating(out, getValidMediaOutputType());
		buffer.append(cbuf);
	}

	@Override
	public final void write(char[] cbuf, int off, int len, Writer out) {
		assert Assertions.isValidating(out, getValidMediaOutputType());
		buffer.append(cbuf, off, len);
	}

	@Override
	public final void write(String str, Writer out) {
		assert Assertions.isValidating(out, getValidMediaOutputType());
		if(str==null) throw new IllegalArgumentException("str is null");
		buffer.append(str);
	}

	@Override
	public final void write(String str, int off, int len, Writer out) {
		assert Assertions.isValidating(out, getValidMediaOutputType());
		if(str==null) throw new IllegalArgumentException("str is null");
		buffer.append(str, off, off+len);
	}

	@Override
	public final BufferedEncoder append(char c, Appendable out) {
		assert Assertions.isValidating(out, getValidMediaOutputType());
		buffer.append(c);
		return this;
	}

	@Override
	public final BufferedEncoder append(CharSequence csq, Appendable out) {
		assert Assertions.isValidating(out, getValidMediaOutputType());
		buffer.append(csq);
		return this;
	}

	@Override
	public final BufferedEncoder append(CharSequence csq, int start, int end, Appendable out) {
		assert Assertions.isValidating(out, getValidMediaOutputType());
		buffer.append(csq, start, end);
		return this;
	}

	@Override
	public final void writeSuffixTo(Appendable out, boolean trim) throws IOException {
		super.writeSuffixTo(out, trim);
		try {
			writeSuffix(trim ? Strings.trim(buffer) : buffer, out);
		} finally {
			// Tests require buffer reset even on failure
			buffer.setLength(0);
		}
	}

	protected abstract void writeSuffix(CharSequence buffer, Appendable out) throws IOException;
}
