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
 * Encodes JSON linked data for safe output.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public interface LdJson extends Encode {

  // <editor-fold desc="Encode - manual self-type and deprecate since not expected" defaultstate="collapsed">
  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  default LdJson encode(MediaType contentType, char ch) throws IOException {
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
  default LdJson encode(MediaType contentType, char[] cbuf) throws IOException {
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
  default LdJson encode(MediaType contentType, char[] cbuf, int offset, int len) throws IOException {
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
  LdJson encode(MediaType contentType, CharSequence csq) throws IOException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  LdJson encode(MediaType contentType, CharSequence csq, int start, int end) throws IOException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  LdJson encode(MediaType contentType, Object content) throws IOException;

  /**
   * {@inheritDoc}
   *
   * @deprecated  Encoding of arbitrary content types is not expected since all supported types have per-type methods.
   */
  @Deprecated
  @Override
  default <Ex extends Throwable> LdJson encode(MediaType contentType, IOSupplierE<?, Ex> content) throws IOException, Ex {
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
  default <Ex extends Throwable> LdJson encode(MediaType contentType, MediaWritable<Ex> content) throws IOException, Ex {
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

  // <editor-fold desc="LdJson - definition" defaultstate="collapsed">
  /**
   * Writes the given JSON linked data with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code <script type="application/ld+json">…</script>}.</p>
   *
   * @return  {@code this} writer
   */
  default LdJson ldJson(char ch) throws IOException {
    return encode(MediaType.LD_JSON, ch);
  }

  /**
   * Writes the given JSON linked data with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code <script type="application/ld+json">…</script>}.</p>
   *
   * @return  {@code this} writer
   */
  default LdJson ldJson(char[] cbuf) throws IOException {
    return encode(MediaType.LD_JSON, cbuf);
  }

  /**
   * Writes the given JSON linked data with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code <script type="application/ld+json">…</script>}.</p>
   *
   * @return  {@code this} writer
   */
  default LdJson ldJson(char[] cbuf, int offset, int len) throws IOException {
    return encode(MediaType.LD_JSON, cbuf, offset, len);
  }

  /**
   * Writes the given JSON linked data with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code <script type="application/ld+json">…</script>}.</p>
   *
   * <p>If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.</p>
   *
   * @return  {@code this} writer
   */
  default LdJson ldJson(CharSequence csq) throws IOException {
    return encode(MediaType.LD_JSON, csq);
  }

  /**
   * Writes the given JSON linked data with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code <script type="application/ld+json">…</script>}.</p>
   *
   * <p>If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.</p>
   *
   * @return  {@code this} writer
   */
  default LdJson ldJson(CharSequence csq, int start, int end) throws IOException {
    return encode(MediaType.LD_JSON, csq, start, end);
  }

  /**
   * Writes the given JSON linked data with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code <script type="application/ld+json">…</script>}.</p>
   *
   * <p>If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.</p>
   *
   * @return  {@code this} writer
   */
  default LdJson ldJson(Object ldJson) throws IOException {
    return encode(MediaType.LD_JSON, ldJson);
  }

  /**
   * Writes the given JSON linked data with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code <script type="application/ld+json">…</script>}.</p>
   *
   * <p>If the string is translated, comments will be added giving the
   * translation lookup id to aid in translation of server-translated values.</p>
   *
   * @param  <Ex>  An arbitrary exception type that may be thrown
   *
   * @return  {@code this} writer
   */
  default <Ex extends Throwable> LdJson ldJson(IOSupplierE<?, Ex> ldJson) throws IOException, Ex {
    return encode(MediaType.LD_JSON, ldJson);
  }

  /**
   * Writes the given JSON linked data with proper encoding.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code <script type="application/ld+json">…</script>}.</p>
   *
   * <p>Does not perform any translation markups.</p>
   *
   * @param  <Ex>  An arbitrary exception type that may be thrown
   *
   * @return  {@code this} writer
   */
  default <Ex extends Throwable> LdJson ldJson(LdJsonWritable<Ex> ldJson) throws IOException, Ex {
    return encode(MediaType.LD_JSON, ldJson);
  }

  /**
   * Writes the given JSON linked data with proper encoding.
   * This is well suited for use in a try-with-resources block.
   *
   * <p>Adds {@linkplain MediaEncoder#writePrefixTo(java.lang.Appendable) prefixes}
   * and {@linkplain MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean) suffixes} by media type, such as {@code <script type="application/ld+json">…</script>}.</p>
   *
   * <p>Does not perform any translation markups.</p>
   *
   * @return  A new writer that may be used for arbitrary JSON linked data.
   *          This writer must be closed for completed calls to {@link MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean)}.
   */
  default LdJsonWriter ldJson() throws IOException {
    return (LdJsonWriter) encode(MediaType.LD_JSON);
  }
  // </editor-fold>
}
