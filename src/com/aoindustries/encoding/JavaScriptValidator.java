/*
 * ao-encoding - High performance character encoding.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015  AO Industries, Inc.
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
 * along with ao-encoding.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.encoding;

import java.io.Writer;

/**
 * Currently performs no validation of the character because JavaScript can
 * use the entire Unicode character set.
 *
 * @author  AO Industries, Inc.
 */
public class JavaScriptValidator extends MediaValidator {

    protected JavaScriptValidator(Writer out) {
        super(out);
    }

    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.JAVASCRIPT
            || inputType==MediaType.TEXT        // No validation required
        ;
    }

	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return inputType == MediaType.JAVASCRIPT;
	}

	@Override
    public MediaType getValidMediaOutputType() {
        return MediaType.JAVASCRIPT;
    }
}
