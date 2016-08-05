/*
 * ao-encoding - High performance character encoding.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015  AO Industries, Inc.
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

import java.io.IOException;
import java.io.Writer;

/**
 * Makes sure that all data going through this writer has the correct characters
 * for URI/URL data.
 *
 * @author  AO Industries, Inc.
 */
public class UrlValidator extends MediaValidator {

    /**
     * Checks one character, throws IOException if invalid.
     * @see java.net.URLEncoder
     * @return <code>true</code> if found the first '?'.
     */
    public static boolean checkCharacter(int c, boolean foundQuestionMark) throws IOException {
        if(foundQuestionMark) {
            switch(c) {
                case '.':
                case '-':
                case '*':
                case '_':
                case '+': // converted space
                case '%': // encoded value
                // Other characters used outside the URL data
                //case ':':
                //case '/':
                //case '@':
                //case ';':
                //case '?':
                // Parameter separators
                case '=':
                case '&':
                // Anchor separator
                case '#':
                    return true;
                default:
                    if(
                        (c<'a' || c>'z')
                        && (c<'A' || c>'Z')
                        && (c<'0' || c>'9')
                    ) throw new IOException(ApplicationResources.accessor.getMessage("UrlValidator.invalidCharacter", Integer.toHexString(c)));
                    return true;
            }
        } else {
            return c=='?';
        }
    }

    /**
     * Checks a set of characters, throws IOException if invalid
     */
    public static boolean checkCharacters(char[] cbuf, int off, int len, boolean foundQuestionMark) throws IOException {
        int end = off + len;
        while(off<end) foundQuestionMark = checkCharacter(cbuf[off++], foundQuestionMark);
        return foundQuestionMark;
    }

    /**
     * Checks a set of characters, throws IOException if invalid
     */
    public static boolean checkCharacters(CharSequence str, int off, int end, boolean foundQuestionMark) throws IOException {
        while(off<end) foundQuestionMark = checkCharacter(str.charAt(off++), foundQuestionMark);
        return foundQuestionMark;
    }

    private boolean foundQuestionMark = false;

    protected UrlValidator(Writer out) {
        super(out);
    }

    @Override
    public boolean isValidatingMediaInputType(MediaType inputType) {
        return
            inputType==MediaType.URL
            || inputType==MediaType.TEXT        // No validation required
        ;
    }


	@Override
	public boolean canSkipValidation(MediaType inputType) {
		return inputType == MediaType.URL;
	}

	@Override
    public MediaType getValidMediaOutputType() {
        return MediaType.URL;
    }

    @Override
    public void write(int c) throws IOException {
        foundQuestionMark = checkCharacter(c, foundQuestionMark);
        out.write(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        foundQuestionMark = checkCharacters(cbuf, off, len, foundQuestionMark);
        out.write(cbuf, off, len);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if(str==null) throw new IllegalArgumentException("str is null");
        foundQuestionMark = checkCharacters(str, off, off + len, foundQuestionMark);
        out.write(str, off, len);
    }

    @Override
    public UrlValidator append(CharSequence csq) throws IOException {
        foundQuestionMark = checkCharacters(csq, 0, csq.length(), foundQuestionMark);
        out.append(csq);
        return this;
    }

    @Override
    public UrlValidator append(CharSequence csq, int start, int end) throws IOException {
        foundQuestionMark = checkCharacters(csq, start, end, foundQuestionMark);
        out.append(csq, start, end);
        return this;
    }

    @Override
    public UrlValidator append(char c) throws IOException {
        foundQuestionMark = checkCharacter(c, foundQuestionMark);
        out.append(c);
        return this;
    }
}
