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

import java.io.IOException;
import javax.annotation.concurrent.ThreadSafe;

/**
 * See <a href="https://html.spec.whatwg.org/multipage/dom.html#content-models:space-characters">3.2.5 Content models / ASCII whitespace</a>.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public interface Whitespace {

	// <editor-fold desc="Whitespace - definition" defaultstate="collapsed">
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
	 * The character used for space, which is {@code ' '}.
	 */
	char SPACE = ' ';

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
	Whitespace nl() throws IOException;

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
	default Whitespace nli() throws IOException {
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
	default Whitespace nli(int depthOffset) throws IOException {
		nl();
		return indent(depthOffset);
	}

	/**
	 * Writes the current indentation when {@linkplain #getIndent() indentation enabled}.
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #indent(int)
	 * @see  #INDENT
	 * @see  #nli()
	 * @see  #getIndent()
	 * @see  #setIndent(boolean)
	 */
	default Whitespace indent() throws IOException {
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
	Whitespace indent(int depthOffset) throws IOException;

	/**
	 * Gets if indentation is currently enabled, off by default.
	 */
	boolean getIndent();

	/**
	 * Enables or disabled indentation.
	 *
	 * @return  {@code this} writer
	 */
	Whitespace setIndent(boolean indent);

	/**
	 * Gets the current indentation depth, which begins at zero.
	 * This value is not updated when indentation is disabled.
	 * Not all tags will trigger indentation.
	 */
	int getDepth();

	/**
	 * Sets the indentation depth.
	 *
	 * @return  {@code this} writer
	 */
	Whitespace setDepth(int depth);

	/**
	 * Increments the indentation depth, if enabled.
	 *
	 * @return  {@code this} writer
	 */
	Whitespace incDepth();

	/**
	 * Decrements the indentation depth, if enabled.
	 *
	 * @return  {@code this} writer
	 */
	Whitespace decDepth();

	/**
	 * Writes one space character.
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #sp(int)
	 * @see  #SPACE
	 */
	default Whitespace sp() throws IOException {
		return sp(1);
	}

	/**
	 * Writes the given number of space characters.
	 *
	 * @param  count  When {@code count <= 0}, nothing is written.
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #sp()
	 * @see  #SPACE
	 */
	Whitespace sp(int count) throws IOException;
	// </editor-fold>
}
