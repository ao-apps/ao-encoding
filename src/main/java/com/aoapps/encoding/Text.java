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

import com.aoapps.lang.io.function.IOSupplierE;
import java.io.IOException;

/**
 * Encodes arbitrary text for safe output.
 *
 * @author  AO Industries, Inc.
 */
public interface Text extends Encode {

	// <editor-fold desc="Encode - manual self-type" defaultstate="collapsed">
	@Override
	default Text encode(MediaType contentType, char ch) throws IOException {
		Encode.super.encode(contentType, ch);
		return this;
	}

	@Override
	default Text encode(MediaType contentType, char[] cbuf) throws IOException {
		Encode.super.encode(contentType, cbuf);
		return this;
	}

	@Override
	default Text encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
		Encode.super.encode(contentType, cbuf, offset, len);
		return this;
	}

	@Override
	Text encode(MediaType contentType, CharSequence csq) throws IOException;

	@Override
	Text encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException;

	@Override
	Text encode(MediaType contentType, Object content) throws IOException;

	@Override
	default <Ex extends Throwable> Text encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
		Encode.super.encode(contentType, content);
		return this;
	}

	@Override
	default <Ex extends Throwable> Text encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
		Encode.super.encode(contentType, content);
		return this;
	}
	// </editor-fold>

	// <editor-fold desc="Text - definition" defaultstate="collapsed">
	/**
	 * The character used for non-breaking space, which is {@code '\u00A0'}.
	 */
	char NBSP = '\u00A0';

	/**
	 * Writes one non-breaking space character.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #nbsp(int)
	 * @see  #NBSP
	 */
	default Text nbsp() throws IOException {
		return nbsp(1);
	}

	/**
	 * Writes the given number of non-breaking space characters.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 *
	 * @param  count  When {@code count <= 0}, nothing is written.
	 *
	 * @return  {@code this} writer
	 *
	 * @see  #nbsp()
	 * @see  #NBSP
	 */
	default Text nbsp(int count) throws IOException {
		try (TextWriter tmw = text()) {
			WriterUtil.nbsp(tmw, count);
		}
		return this;
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	default Text text(char ch) throws IOException {
		return encode(MediaType.TEXT, ch);
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	default Text text(char[] cbuf) throws IOException {
		return encode(MediaType.TEXT, cbuf);
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	default Text text(char[] cbuf, int offset, int len) throws IOException {
		return encode(MediaType.TEXT, cbuf, offset, len);
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * If the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	default Text text(CharSequence csq) throws IOException {
		return encode(MediaType.TEXT, csq);
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * If the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	default Text text(CharSequence csq, int start, int end) throws IOException {
		return encode(MediaType.TEXT, csq, start, end);
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * If the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	default Text text(Object text) throws IOException {
		return encode(MediaType.TEXT, text);
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * If the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 * </p>
	 *
	 * @param  <Ex>  An arbitrary exception type that may be thrown
	 *
	 * @return  {@code this} writer
	 */
	default <Ex extends Throwable> Text text(IOSupplierE<?, Ex> text) throws IOException, Ex {
		return encode(MediaType.TEXT, text);
	}

	/**
	 * Writes the given text with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * Does not perform any translation markups.
	 * </p>
	 *
	 * @param  <Ex>  An arbitrary exception type that may be thrown
	 *
	 * @return  {@code this} writer
	 */
	default <Ex extends Throwable> Text text(TextWritable<Ex> text) throws IOException, Ex {
		return encode(MediaType.TEXT, text);
	}

	/**
	 * Writes the given text with proper encoding.
	 * This is well suited for use in a try-with-resources block.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * Does not perform any translation markups.
	 * </p>
	 *
	 * @return  A new writer that may be used for arbitrary text.
	 *          This writer must be closed for completed calls to {@link MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean)}.
	 */
	default TextWriter text() throws IOException {
		return (TextWriter)encode(MediaType.TEXT);
	}
	// </editor-fold>
}
