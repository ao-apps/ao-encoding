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
import javax.annotation.concurrent.ThreadSafe;

/**
 * Encodes <code>psql</code> command input for safe output.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public interface Psql extends Encode {

  // <editor-fold desc="Encode - manual self-type and deprecate since not expected" defaultstate="collapsed">
  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  default Psql encode(MediaType contentType, char ch) throws IOException {
    Encode.super.encode(contentType, ch);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  default Psql encode(MediaType contentType, char[] cbuf) throws IOException {
    Encode.super.encode(contentType, cbuf);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  default Psql encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
    Encode.super.encode(contentType, cbuf, offset, len);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  Psql encode(MediaType contentType, CharSequence csq) throws IOException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  Psql encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  Psql encode(MediaType contentType, Object content) throws IOException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  default <Ex extends Throwable> Psql encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
    Encode.super.encode(contentType, content);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  default <Ex extends Throwable> Psql encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
    Encode.super.encode(contentType, content);
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  MediaWriter encode(MediaType contentType) throws IOException;
  // </editor-fold>

  // <editor-fold desc="Psql - definition" defaultstate="collapsed">
  /**
   * Writes the given <code>psql</code> command input with proper encoding.
   * <p>
   * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.
   * </p>
   *
   * @return  {@code this} writer
   */
  default Psql psql(char ch) throws IOException {
    return encode(MediaType.PSQL, ch);
  }

  /**
   * Writes the given <code>psql</code> command input with proper encoding.
   * <p>
   * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.
   * </p>
   *
   * @return  {@code this} writer
   */
  default Psql psql(char[] cbuf) throws IOException {
    return encode(MediaType.PSQL, cbuf);
  }

  /**
   * Writes the given <code>psql</code> command input with proper encoding.
   * <p>
   * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.
   * </p>
   *
   * @return  {@code this} writer
   */
  default Psql psql(char[] cbuf, int offset, int len) throws IOException {
    return encode(MediaType.PSQL, cbuf, offset, len);
  }

  /**
   * Writes the given <code>psql</code> command input with proper encoding.
   * <p>
   * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.
   * </p>
   * <p>
   * If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.
   * </p>
   *
   * @return  {@code this} writer
   */
  default Psql psql(CharSequence csq) throws IOException {
    return encode(MediaType.PSQL, csq);
  }

  /**
   * Writes the given <code>psql</code> command input with proper encoding.
   * <p>
   * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.
   * </p>
   * <p>
   * If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.
   * </p>
   *
   * @return  {@code this} writer
   */
  default Psql psql(CharSequence csq, int start, int end) throws IOException {
    return encode(MediaType.PSQL, csq, start, end);
  }

  /**
   * Writes the given <code>psql</code> command input with proper encoding.
   * <p>
   * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.
   * </p>
   * <p>
   * If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.
   * </p>
   *
   * @return  {@code this} writer
   */
  default Psql psql(Object psql) throws IOException {
    return encode(MediaType.PSQL, psql);
  }

  /**
   * Writes the given <code>psql</code> command input with proper encoding.
   * <p>
   * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.
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
  default <Ex extends Throwable> Psql psql(IOSupplierE<?, Ex> psql) throws IOException, Ex {
    return encode(MediaType.PSQL, psql);
  }

  /**
   * Writes the given <code>psql</code> command input with proper encoding.
   * <p>
   * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.
   * </p>
   * <p>
   * Does not perform any translation markups.
   * </p>
   *
   * @param  <Ex>  An arbitrary exception type that may be thrown
   *
   * @return  {@code this} writer
   */
  default <Ex extends Throwable> Psql psql(PsqlWritable<Ex> psql) throws IOException, Ex {
    return encode(MediaType.PSQL, psql);
  }

  /**
   * Writes the given <code>psql</code> command input with proper encoding.
   * This is well suited for use in a try-with-resources block.
   * <p>
   * Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.
   * </p>
   * <p>
   * Does not perform any translation markups.
   * </p>
   *
   * @return  A new writer that may be used for arbitrary <code>psql</code> command input.
   *          This writer must be closed for completed calls to {@link MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean)}.
   */
  default PsqlWriter psql() throws IOException {
    return (PsqlWriter)encode(MediaType.PSQL);
  }
  // </editor-fold>
}
