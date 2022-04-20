/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.encoding.i18n;

import com.aoapps.hodgepodge.i18n.EditableResourceBundle;
import java.util.Locale;
import javax.annotation.concurrent.ThreadSafe;

/**
 * @author  AO Industries, Inc.
 */
@ThreadSafe
public final class ApplicationResources_ja extends EditableResourceBundle {

  public ApplicationResources_ja() {
    super(
      Locale.JAPANESE,
      ApplicationResources.bundleSet,
      ApplicationResources.getSourceFile("ApplicationResources_ja.properties")
    );
  }
}
