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
package com.aoindustries.encoding;

import java.io.IOException;

/**
 * See <a href="https://html.spec.whatwg.org/#content-models:space-characters">3.2.5 Content models / ASCII whitespace</a>.
 *
 * @param  <C>  The current type of writer.
 *
 * @author  AO Industries, Inc.
 */
public interface WhitespaceWriter<C> {

	/**
	 * The character used for newlines.
	 * <p>
	 * This is {@code '\n'} on all platforms.  If a different newline is required,
	 * such as {@code "\r\n"} for email, filter the output.
	 * </p>
	 */
	char NL = '\n';

	/**
	 * The character used for indentation, which is {@code '\t'}.
	 */
	char INDENT = '\t';

	/**
	 * Writes a newline.
	 * <p>
	 * This is {@code '\n'} on all platforms.  If a different newline is required,
	 * such as {@code "\r\n"} for email, filter the output.
	 * </p>
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #NL
	 * @see  #nli()
	 * @see  #nli(int)
	 */
	C nl() throws IOException;

	/**
	 * Writes a newline, followed by current indentation when {@linkplain #getIndent() indentation enabled}.
	 * <p>
	 * This is {@code '\n'} on all platforms.  If a different newline is required,
	 * such as {@code "\r\n"} for email, filter the output.
	 * </p>
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #nli(int)
	 * @see  #nl()
	 * @see  #indent()
	 * @see  #getIndent()
	 * @see  #setIndent(boolean)
	 */
	default C nli() throws IOException {
		return nli(0);
	}

	/**
	 * Writes a newline, followed by current indentation with a depth offset when {@linkplain #getIndent() indentation enabled}.
	 * <p>
	 * This is {@code '\n'} on all platforms.  If a different newline is required,
	 * such as {@code "\r\n"} for email, filter the output.
	 * </p>
	 *
	 * @param  depthOffset  A value added to the current indentation depth.
	 *                      For example, pass {@code -1} when performing a newline before a closing tag or ending curly brace.
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #nli()
	 * @see  #nl()
	 * @see  #indent(int)
	 * @see  #getIndent()
	 * @see  #setIndent(boolean)
	 */
	default C nli(int depthOffset) throws IOException {
		nl();
		return indent(depthOffset);
	}

	/**
	 * Writes the current indentation when {@linkplain #getIndent() indentation enabled}.
	 *
	 * @see  #indent(int)
	 * @see  #INDENT
	 * @see  #nli()
	 * @see  #getIndent()
	 * @see  #setIndent(boolean)
	 */
	default C indent() throws IOException {
		return indent(0);
	}

	/**
	 * Writes the current indentation with a depth offset when {@linkplain #getIndent() indentation enabled}.
	 *
	 * @param  depthOffset  A value added to the current indentation depth.
	 *                      For example, pass {@code -1} when performing a newline before a closing tag or ending curly brace.
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #indent()
	 * @see  #INDENT
	 * @see  #nli(int)
	 * @see  #getIndent()
	 * @see  #setIndent(boolean)
	 */
	C indent(int depthOffset) throws IOException;

	/**
	 * Gets if indentation is currently enabled, off by default.
	 */
	boolean getIndent();

	/**
	 * Enables or disabled indentation.
	 */
	C setIndent(boolean indent);

	/**
	 * Gets the current indentation depth, which begins at zero.
	 * This value is not updated when indentation is disabled.
	 * Not all tags will trigger indentation.
	 */
	int getDepth();

	/**
	 * Sets the indentation depth.
	 */
	C setDepth(int depth);

	/**
	 * Increments the indentation depth, if enabled.
	 */
	C incDepth();

	/**
	 * Decrements the indentation depth, if enabled.
	 */
	C decDepth();
}
