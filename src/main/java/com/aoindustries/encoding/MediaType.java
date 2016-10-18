/*
 * ao-encoding - High performance character encoding.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016  AO Industries, Inc.
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

import com.aoindustries.util.i18n.MarkupType;

/**
 * Supported content types.
 *
 * @author  AO Industries, Inc.
 */
public enum MediaType {

	/**
	 * Arbitrary 8-bit binary data (<code>application/octet-stream</code>).
	 * Please note that some conversions of this will possibly lose data, such
	 * as being contained by XML.  In this case control characters except \t,
	 * \r, and \n will be discarded.  Consider what to do about character
	 * encodings before enabling this.
	 */
	// DATA("application/octet-stream"),

	/**
	 * An XHTML 1.0 document (<code>application/xhtml+xml</code>).
	 */
	XHTML("application/xhtml+xml") {
		@Override
		boolean isUsedFor(String contentType) {
			return
				"application/xhtml+xml".equalsIgnoreCase(contentType)
				|| "text/html".equalsIgnoreCase(contentType)
				// Also use this type for general purpose XML documents
				|| "application/xml".equalsIgnoreCase(contentType)
				|| "text/xml".equalsIgnoreCase(contentType)
			;
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.XHTML;
		}
	},

	/**
	 * Indicates that a value contains a XHTML attribute only.  This is a non-standard
	 * media type and is only used during internal conversions.  The final output
	 * should not be this type.
	 */
	XHTML_ATTRIBUTE("application/xhtml+xml+attribute") {
		@Override
		boolean isUsedFor(String contentType) {
			return "application/xhtml+xml+attribute".equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.NONE;
		}
	},

	/**
	 * An HTML document (<code>text/html</code>).
	 */
	// HTML("text/html"),

	/**
	 * A JavaScript script (<code>text/javascript</code>).
	 */
	JAVASCRIPT("text/javascript") {
		@Override
		boolean isUsedFor(String contentType) {
			return "text/javascript".equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.JAVASCRIPT;
		}
	},

	/**
	 * A JSON script (<code>application/json</code>).
	 */
	JSON("application/json") {
		@Override
		boolean isUsedFor(String contentType) {
			return "application/json".equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.JAVASCRIPT;
		}
	},

	/**
	 * A JSON linked data script (<code>application/ld+json</code>).
	 */
	LD_JSON("application/ld+json") {
		@Override
		boolean isUsedFor(String contentType) {
			return "application/ld+json".equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.JAVASCRIPT;
		}
	},

	/**
	 * Any plaintext document comprised of unicode characters (<code>text/plain</code>).
	 * This is used for any arbitrary, unknown and untrusted data.
	 *
	 * @see #DATA
	 */
	TEXT("text/plain") {
		@Override
		boolean isUsedFor(String contentType) {
			return "text/plain".equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.TEXT;
		}
	},

	/**
	 * A URL-encoded, &amp; (not &amp;amp;) separated URL.
	 */
	URL("text/url") {
		@Override
		boolean isUsedFor(String contentType) {
			return "text/url".equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.NONE;
		}
	};

	private final String contentType;

	private MediaType(String contentType) {
		this.contentType = contentType;
	}

	abstract boolean isUsedFor(String contentType);

	/**
	 * Gets the actual media type, such as <code>text/html</code>.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Gets the markup type compatible with this media type.
	 */
	public abstract MarkupType getMarkupType();

	private static final MediaType[] values = values();

	/**
	 * Gets the media type for the given name using case-insensitive matching.
	 * 
	 * @return  the <code>MediaType</code> or <code>null</code> if not found.
	 */
	public static MediaType getMediaTypeByName(String name) {
		for(MediaType value : values) {
			if(value.name().equalsIgnoreCase(name)) return value;
		}
		return null;
	}

	/**
	 * Gets the media type for the provided textual content type.
	 */
	public static MediaType getMediaTypeForContentType(final String fullContentType) throws MediaException {
		int semiPos = fullContentType.indexOf(';');
		String contentType = ((semiPos==-1) ? fullContentType : fullContentType.substring(0, semiPos)).trim();
		for(MediaType value : values) {
			if(value.isUsedFor(contentType)) return value;
		}
		throw new MediaException(ApplicationResources.accessor.getMessage("MediaType.getMediaType.unknownType", fullContentType));
	}
}
