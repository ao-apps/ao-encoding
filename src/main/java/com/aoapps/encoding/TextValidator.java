/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import java.io.Writer;
import javax.annotation.concurrent.ThreadSafe;

/**
 * No validation is performed on text.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public final class TextValidator extends MediaValidator {

  TextValidator(Writer out) {
    super(out);
  }

  @Override
  public MediaType getValidMediaInputType() {
    return MediaType.TEXT;
  }

  @Override
  public boolean isValidatingMediaInputType(MediaType inputType) {
    return
        inputType == MediaType.JAVASCRIPT // All invalid characters in JAVASCRIPT are also invalid in TEXT
            || inputType == MediaType.JSON // All invalid characters in JSON are also invalid in TEXT
            || inputType == MediaType.LD_JSON // All invalid characters in LD_JSON are also invalid in TEXT
            || inputType == MediaType.TEXT; // All invalid characters in TEXT are also invalid in TEXT
  }

  @Override
  public boolean canSkipValidation(MediaType outputType) {
    return true; // All characters are valid in TEXT
  }
}
