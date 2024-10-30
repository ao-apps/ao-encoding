/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2022, 2024  AO Industries, Inc.
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
 * Encodes <code>mysql</code> command input for safe output.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public interface Mysql extends Encode {

  // <editor-fold desc="Encode - manual self-type and deprecate since not expected" defaultstate="collapsed">
  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  default Mysql encode(MediaType contentType, char ch) throws IOException {
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
  default Mysql encode(MediaType contentType, char[] cbuf) throws IOException {
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
  default Mysql encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
  Mysql encode(MediaType contentType, CharSequence csq) throws IOException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  Mysql encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  Mysql encode(MediaType contentType, Object content) throws IOException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  default <Ex extends Throwable> Mysql encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
  default <Ex extends Throwable> Mysql encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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

  // <editor-fold desc="Mysql - definition" defaultstate="collapsed">
  /**
   * Writes the given <code>mysql</code> command input with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.</p>
   *
   * @return  {@code this} writer
   */
  default Mysql mysql(char ch) throws IOException {
    return encode(MediaType.MYSQL, ch);
  }

  /**
   * Writes the given <code>mysql</code> command input with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.</p>
   *
   * @return  {@code this} writer
   */
  default Mysql mysql(char[] cbuf) throws IOException {
    return encode(MediaType.MYSQL, cbuf);
  }

  /**
   * Writes the given <code>mysql</code> command input with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.</p>
   *
   * @return  {@code this} writer
   */
  default Mysql mysql(char[] cbuf, int offset, int len) throws IOException {
    return encode(MediaType.MYSQL, cbuf, offset, len);
  }

  /**
   * Writes the given <code>mysql</code> command input with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.</p>
   *
   * <p>If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.</p>
   *
   * @return  {@code this} writer
   */
  default Mysql mysql(CharSequence csq) throws IOException {
    return encode(MediaType.MYSQL, csq);
  }

  /**
   * Writes the given <code>mysql</code> command input with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.</p>
   *
   * <p>If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.</p>
   *
   * @return  {@code this} writer
   */
  default Mysql mysql(CharSequence csq, int start, int end) throws IOException {
    return encode(MediaType.MYSQL, csq, start, end);
  }

  /**
   * Writes the given <code>mysql</code> command input with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.</p>
   *
   * <p>If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.</p>
   *
   * @return  {@code this} writer
   */
  default Mysql mysql(Object mysql) throws IOException {
    return encode(MediaType.MYSQL, mysql);
  }

  /**
   * Writes the given <code>mysql</code> command input with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.</p>
   *
   * <p>If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.</p>
   *
   * @param  <Ex>  An arbitrary exception type that may be thrown
   *
   * @return  {@code this} writer
   */
  default <Ex extends Throwable> Mysql mysql(IOSupplierE<?, Ex> mysql) throws IOException, Ex {
    return encode(MediaType.MYSQL, mysql);
  }

  /**
   * Writes the given <code>mysql</code> command input with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.</p>
   *
   * <p>Does not perform any translation markups.</p>
   *
   * @param  <Ex>  An arbitrary exception type that may be thrown
   *
   * @return  {@code this} writer
   */
  default <Ex extends Throwable> Mysql mysql(MysqlWritable<Ex> mysql) throws IOException, Ex {
    return encode(MediaType.MYSQL, mysql);
  }

  /**
   * Writes the given <code>mysql</code> command input with proper encoding.
   * This is well suited for use in a try-with-resources block.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type.</p>
   *
   * <p>Does not perform any translation markups.</p>
   *
   * @return  A new writer that may be used for arbitrary <code>mysql</code> command input.
   *          This writer must be closed for completed calls to {@link MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean)}.
   */
  default MysqlWriter mysql() throws IOException {
    return (MysqlWriter) encode(MediaType.MYSQL);
  }
  // </editor-fold>
}
