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
 * along with ao-encoding.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoapps.encoding;

import com.aoapps.lang.io.function.IOSupplierE;
import java.io.IOException;

/**
 * Encodes arbitrary text for safe output.
 *
 * @param  <C>  The current type of writer.
 *
 * @author  AO Industries, Inc.
 */
public interface TextWriter<C extends TextWriter<C>> extends WhitespaceWriter<C> {

	/**
	 * The character used for non-breaking space, which is {@code '\u00A0'}.
	 */
	char NBSP = '\u00A0';

	/**
	 * Writes one non-breaking space character.
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #nbsp(int)
	 * @see  #NBSP
	 */
	default C nbsp() throws IOException {
		return nbsp(1);
	}

	/**
	 * Writes the given number of non-breaking space characters.
	 *
	 * @param  count  When {@code count <= 0}, nothing is written.
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #nbsp()
	 * @see  #NBSP
	 */
	C nbsp(int count) throws IOException;

	/**
	 * Writes the given text with proper encoding.
	 *
	 * @return  {@code this} writer
	 */
	C text(char ch) throws IOException;

	// TODO: codePoint?

	/**
	 * Writes the given text with proper encoding.
	 *
	 * @return  {@code this} writer
	 */
	C text(char[] cbuf) throws IOException;

	/**
	 * Writes the given text with proper encoding.
	 *
	 * @return  {@code this} writer
	 */
	C text(char[] cbuf, int offset, int len) throws IOException;

	/**
	 * Writes the given text with proper encoding.
	 *
	 * @return  {@code this} writer
	 */
	C text(CharSequence csq) throws IOException;

	/**
	 * Writes the given text with proper encoding.
	 *
	 * @return  {@code this} writer
	 */
	C text(CharSequence csq, int start, int end) throws IOException;

	/**
	 * Writes the given text with proper encoding.
	 *
	 * @return  {@code this} writer
	 */
	C text(Object text) throws IOException;

	/**
	 * Writes the given text with proper encoding.
	 *
	 * @param  <Ex>  An arbitrary exception type that may be thrown
	 *
	 * @return  {@code this} writer
	 */
	<Ex extends Throwable> C text(IOSupplierE<?, Ex> text) throws IOException, Ex;

	/**
	 * Writes the given text with proper encoding.
	 *
	 * @param  <Ex>  An arbitrary exception type that may be thrown
	 *
	 * @return  {@code this} writer
	 */
	<Ex extends Throwable> C text(MediaWritable<Ex> text) throws IOException, Ex;

	/**
	 * Writes the given text with proper encoding.
	 * This is well suited for use in a try-with-resources block.
	 *
	 * @return  A new writer that may be used for arbitrary text.
	 *          This writer must be closed for completed calls to {@link MediaEncoder#writeSuffixTo(java.lang.Appendable)}.
	 */
	MediaWriter text() throws IOException;
}
