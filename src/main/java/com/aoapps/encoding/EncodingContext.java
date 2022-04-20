/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.concurrent.ThreadSafe;

/**
 * The current encoding context may perform URL rewriting.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public interface EncodingContext {

  /**
   * Default encoding context.
   * Also used when there is no context available.
   *
   * @see  Doctype#DEFAULT
   * @see  Serialization#DEFAULT
   * @see  StandardCharsets#UTF_8
   */
  EncodingContext DEFAULT = new EncodingContext() {};

  /**
   * Encoding context for XML always.
   *
   * @see  Doctype#DEFAULT
   * @see  Serialization#XML
   * @see  StandardCharsets#UTF_8
   */
  @SuppressWarnings("Convert2Lambda") // Cannot actually convert to Lambda, despite NetBeans 12.0 suggesting
  EncodingContext XML = new EncodingContext() {
    @Override
    public Serialization getSerialization() {
      return Serialization.XML;
    }
  };

  /**
   * Encoding context for SGML always.
   *
   * @see  Doctype#DEFAULT
   * @see  Serialization#SGML
   * @see  StandardCharsets#UTF_8
   */
  @SuppressWarnings("Convert2Lambda") // Cannot actually convert to Lambda, despite NetBeans 12.0 suggesting
  EncodingContext SGML = new EncodingContext() {
    @Override
    public Serialization getSerialization() {
      return Serialization.SGML;
    }
  };

  /**
   * Encodes a URL for the current encoding context.
   * The resulting URL must be valid <a href="https://datatracker.ietf.org/doc/html/rfc3986">RFC 3986 URI</a> or
   * <a href="https://datatracker.ietf.org/doc/html/rfc3987">RFC 3987 IRI</a>.
   * <p>
   * Defaults to performing no encoding.
   * </p>
   */
  default String encodeURL(String url) {
    return url;
  }

  /**
   * The current doctype.
   * <p>
   * Defaults to {@link Doctype#DEFAULT}.
   * </p>
   */
  default Doctype getDoctype() {
    return Doctype.DEFAULT;
  }

  /**
   * The current serialization.
   * <p>
   * Defaults to {@link Serialization#DEFAULT}.
   * </p>
   */
  default Serialization getSerialization() {
    return Serialization.DEFAULT;
  }

  /**
   * The output character encoding.
   * <p>
   * Defaults to {@link StandardCharsets#UTF_8}.
   * </p>
   */
  default Charset getCharacterEncoding() {
    return StandardCharsets.UTF_8;
  }
}
