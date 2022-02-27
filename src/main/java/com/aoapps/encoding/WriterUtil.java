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

import com.aoapps.lang.io.Encoder;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * Helpers for implementing {@link WhitespaceWriter}.
 *
 * @see  WhitespaceWriter
 *
 * @author  AO Industries, Inc.
 */
public final class WriterUtil {

	/** Make no instances. */
	private WriterUtil() {throw new AssertionError();}

	/**
	 * The number of characters written per block.
	 */
	public static final int BLOCK_SIZE = 4096;

	/**
	 * {@link WhitespaceWriter#NL} combined with {@link WhitespaceWriter#INDENT} characters.
	 */
	public static final String NLI_CHARS;
	static {
		char[] ch = new char[BLOCK_SIZE];
		ch[0] = WhitespaceWriter.NL;
		Arrays.fill(ch, 1, BLOCK_SIZE, WhitespaceWriter.INDENT);
		NLI_CHARS = new String(ch);
	}

	/**
	 * {@link WhitespaceWriter#INDENT} characters.
	 */
	public static final String INDENT_CHARS;
	static {
		char[] ch = new char[BLOCK_SIZE];
		Arrays.fill(ch, 0, BLOCK_SIZE, WhitespaceWriter.INDENT);
		INDENT_CHARS = new String(ch);
	}

	/**
	 * {@link WhitespaceWriter#SPACE} characters.
	 */
	public static final String SPACE_CHARS;
	static {
		char[] ch = new char[BLOCK_SIZE];
		Arrays.fill(ch, 0, BLOCK_SIZE, WhitespaceWriter.SPACE);
		SPACE_CHARS = new String(ch);
	}

	/**
	 * {@link TextWriter#NBSP} characters.
	 */
	public static final String NBSP_CHARS;
	static {
		char[] ch = new char[BLOCK_SIZE];
		Arrays.fill(ch, 0, BLOCK_SIZE, TextWriter.NBSP);
		NBSP_CHARS = new String(ch);
	}

	/**
	 * Writes a {@link WhitespaceWriter#NL} followed by any number of {@link WhitespaceWriter#INDENT}.
	 *
	 * @see WhitespaceWriter#nli(int)
	 */
	public static void nli(Writer out, int indent) throws IOException {
		if(indent > 0) {
			int count = indent + 1; // Add one for the initial newline
			int block = Math.min(count, BLOCK_SIZE);
			assert block > 1;
			out.write(NLI_CHARS, 0, block);
			count -= block;
			assert count >= 0;
			while(count > 0) {
				if(count == 1) {
					out.append(WhitespaceWriter.INDENT);
					break;
				} else {
					block = Math.min(count, BLOCK_SIZE);
					out.write(INDENT_CHARS, 0, block);
					count -= block;
					assert count >= 0;
				}
			}
		} else {
			out.append(WhitespaceWriter.NL);
		}
	}

	/**
	 * Writes a {@link WhitespaceWriter#NL} followed by any number of {@link WhitespaceWriter#INDENT}
	 * through the given encoder.
	 *
	 * @see WhitespaceWriter#nli(int)
	 */
	public static void nli(Encoder encoder, Writer out, int indent) throws IOException {
		if(indent > 0) {
			int count = indent + 1; // Add one for the initial newline
			int block = Math.min(count, BLOCK_SIZE);
			assert block > 1;
			encoder.write(NLI_CHARS, 0, block, out);
			count -= block;
			assert count >= 0;
			while(count > 0) {
				if(count == 1) {
					encoder.append(WhitespaceWriter.INDENT, out);
					break;
				} else {
					block = Math.min(count, BLOCK_SIZE);
					encoder.write(INDENT_CHARS, 0, block, out);
					count -= block;
					assert count >= 0;
				}
			}
		} else {
			encoder.append(WhitespaceWriter.NL, out);
		}
	}

	/**
	 * Writes any number of {@link WhitespaceWriter#INDENT}.
	 *
	 * @see WhitespaceWriter#indent(int)
	 */
	public static void indent(Writer out, int count) throws IOException {
		while(count > 0) {
			if(count == 1) {
				out.append(WhitespaceWriter.INDENT);
				break;
			} else {
				int block = Math.min(count, BLOCK_SIZE);
				out.write(INDENT_CHARS, 0, block);
				count -= block;
				assert count >= 0;
			}
		}
	}

	/**
	 * Writes any number of {@link WhitespaceWriter#INDENT}
	 * through the given encoder.
	 *
	 * @see WhitespaceWriter#indent(int)
	 */
	public static void indent(Encoder encoder, Writer out, int count) throws IOException {
		while(count > 0) {
			if(count == 1) {
				encoder.append(WhitespaceWriter.INDENT, out);
				break;
			} else {
				int block = Math.min(count, BLOCK_SIZE);
				encoder.write(INDENT_CHARS, 0, block, out);
				count -= block;
				assert count >= 0;
			}
		}
	}

	/**
	 * Writes any number of {@link WhitespaceWriter#SPACE}.
	 *
	 * @see WhitespaceWriter#sp(int)
	 */
	public static void sp(Writer out, int count) throws IOException {
		while(count > 0) {
			if(count == 1) {
				out.append(WhitespaceWriter.SPACE);
				break;
			} else {
				int block = Math.min(count, BLOCK_SIZE);
				out.write(SPACE_CHARS, 0, block);
				count -= block;
				assert count >= 0;
			}
		}
	}

	/**
	 * Writes any number of {@link WhitespaceWriter#SPACE}
	 * through the given encoder.
	 *
	 * @see WhitespaceWriter#sp(int)
	 */
	public static void sp(Encoder encoder, Writer out, int count) throws IOException {
		while(count > 0) {
			if(count == 1) {
				encoder.append(WhitespaceWriter.SPACE, out);
				break;
			} else {
				int block = Math.min(count, BLOCK_SIZE);
				encoder.write(SPACE_CHARS, 0, block, out);
				count -= block;
				assert count >= 0;
			}
		}
	}

	/**
	 * Writes any number of {@link TextWriter#NBSP}.
	 *
	 * @see TextWriter#nbsp(int)
	 */
	public static void nbsp(Writer out, int count) throws IOException {
		while(count > 0) {
			if(count == 1) {
				out.append(TextWriter.NBSP);
				break;
			} else {
				int block = Math.min(count, BLOCK_SIZE);
				out.write(NBSP_CHARS, 0, block);
				count -= block;
				assert count >= 0;
			}
		}
	}

	/**
	 * Writes any number of {@link TextWriter#NBSP}
	 * through the given encoder.
	 *
	 * @see TextWriter#nbsp(int)
	 */
	public static void nbsp(Encoder encoder, Writer out, int count) throws IOException {
		while(count > 0) {
			if(count == 1) {
				encoder.append(TextWriter.NBSP, out);
				break;
			} else {
				int block = Math.min(count, BLOCK_SIZE);
				encoder.write(NBSP_CHARS, 0, block, out);
				count -= block;
				assert count >= 0;
			}
		}
	}
}
