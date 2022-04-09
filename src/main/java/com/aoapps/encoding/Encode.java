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

import com.aoapps.lang.io.function.IOSupplierE;
import java.io.IOException;

/**
 * Encodes arbitrary nested types for safe output.
 *
 * @author  AO Industries, Inc.
 */
public interface Encode {

	// <editor-fold desc="Encode - definition" defaultstate="collapsed">
	/**
	 * Encodes the given nested type with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	default Encode encode(MediaType contentType, char ch) throws IOException {
		try (MediaWriter mw = encode(contentType)) {
			mw.append(ch);
		}
		return this;
	}

	/**
	 * Encodes the given nested type with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	default Encode encode(MediaType contentType, char[] cbuf) throws IOException {
		try (MediaWriter mw = encode(contentType)) {
			if(cbuf != null) mw.write(cbuf);
		}
		return this;
	}

	/**
	 * Encodes the given nested type with proper encoding.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 *
	 * @return  {@code this} writer
	 */
	default Encode encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
		try (MediaWriter mw = encode(contentType)) {
			if(cbuf != null) mw.write(cbuf, offset, len);
		}
		return this;
	}

	/**
	 * Encodes the given nested type with proper encoding.
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
	Encode encode(MediaType contentType, CharSequence csq) throws IOException;

	/**
	 * Encodes the given nested type with proper encoding.
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
	Encode encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException;

	/**
	 * Encodes the given nested type with proper encoding.
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
	Encode encode(MediaType contentType, Object content) throws IOException;

	/**
	 * Encodes the given nested type with proper encoding.
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
	default <Ex extends Throwable> Encode encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
		return encode(contentType, (content == null) ? null : content.get());
	}

	/**
	 * Encodes the given nested type with proper encoding.
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
	default <Ex extends Throwable> Encode encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
		try (MediaWriter mw = encode(contentType)) {
			if(content != null) {
				content.writeTo(mw);
			}
		}
		return this;
	}

	/**
	 * Encodes the given nested type with proper encoding.
	 * This is well suited for use in a try-with-resources block.
	 * <p>
	 * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
	 * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code "…"}.
	 * </p>
	 * <p>
	 * Does not perform any translation markups.
	 * </p>
	 *
	 * @return  A new writer that may be used for the given content type.
	 *          This writer must be closed for completed calls to {@link MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean)}.
	 *          <p>
	 *          The returned writer will be of the specific subclass of {@link MediaWriter} matching {@code contentType}
	 *          (see {@link MediaType#getMediaWriterClass()}.  This means {@link MediaWriter#getValidMediaInputType()} will
	 *          be {@code contentType}.
	 *          </p>
	 */
	MediaWriter encode(MediaType contentType) throws IOException;
	// </editor-fold>
}
