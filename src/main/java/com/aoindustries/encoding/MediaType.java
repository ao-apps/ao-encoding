/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2018, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoindustries.i18n.Resources;
import com.aoindustries.io.ContentType;
import com.aoindustries.io.LocalizedUnsupportedEncodingException;
import com.aoindustries.util.i18n.MarkupType;
import java.io.UnsupportedEncodingException;

/**
 * Supported content types.
 *
 * @author  AO Industries, Inc.
 */
public enum MediaType {

	/**
	 * An (X)HTML document (<code>application/xhtml+xml</code>).
	 */
	XHTML(ContentType.XHTML) {
		@Override
		@SuppressWarnings("deprecation")
		boolean isUsedFor(String contentType) {
			return
				ContentType.XHTML.equalsIgnoreCase(contentType)
				|| ContentType.HTML.equalsIgnoreCase(contentType)
				// Also use this type for general purpose XML documents
				|| ContentType.XML.equalsIgnoreCase(contentType)
				|| ContentType.XML_OLD.equalsIgnoreCase(contentType)
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
	XHTML_ATTRIBUTE(ContentType.XHTML_ATTRIBUTE) {
		@Override
		boolean isUsedFor(String contentType) {
			return ContentType.XHTML_ATTRIBUTE.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.NONE;
		}
	},

	/**
	 * An HTML document (<code>text/html</code>).
	 */
	// HTML(ContentType.HTML),

	/**
	 * A JavaScript script (<code>application/javascript</code>).
	 */
	JAVASCRIPT(ContentType.JAVASCRIPT) {
		@Override
		@SuppressWarnings("deprecation")
		boolean isUsedFor(String contentType) {
			return
				ContentType.JAVASCRIPT.equalsIgnoreCase(contentType)
				|| ContentType.JAVASCRIPT_OLD.equalsIgnoreCase(contentType)
				|| ContentType.ECMASCRIPT.equalsIgnoreCase(contentType)
				|| ContentType.ECMASCRIPT_OLD.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.JAVASCRIPT;
		}
	},

	/**
	 * A JSON script (<code>application/json</code>).
	 */
	JSON(ContentType.JSON) {
		@Override
		boolean isUsedFor(String contentType) {
			return ContentType.JSON.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			// JSON doesn't support comments
			return MarkupType.NONE;
		}
	},

	/**
	 * A JSON linked data script (<code>application/ld+json</code>).
	 */
	LD_JSON(ContentType.LD_JSON) {
		@Override
		boolean isUsedFor(String contentType) {
			return ContentType.LD_JSON.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			// JSON doesn't support comments
			return MarkupType.NONE;
		}
	},

	/**
	 * Any plaintext document comprised of unicode characters (<code>text/plain</code>).
	 * This is used for any arbitrary, unknown and untrusted data.
	 */
	TEXT(ContentType.TEXT) {
		@Override
		boolean isUsedFor(String contentType) {
			return ContentType.TEXT.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.TEXT;
		}
	},

	/**
	 * A URL-encoded, &amp; (not &amp;amp;) separated URL.
	 */
	URL(ContentType.URL) {
		@Override
		boolean isUsedFor(String contentType) {
			return ContentType.URL.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.NONE;
		}
	},

	/**
	 * A Bourne shell script (<code>text/x-sh</code>).
	 */
	SH(ContentType.SH) {
		@Override
		boolean isUsedFor(String contentType) {
			return ContentType.SH.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.SH;
		}
	},

	/**
	 * The MySQL <code>mysql</code> command line (<code>text/x-mysql</code>).
	 */
	MYSQL(ContentType.MYSQL) {
		@Override
		boolean isUsedFor(String contentType) {
			return ContentType.MYSQL.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.MYSQL;
		}
	},

	/**
	 * The PostgreSQL <code>psql</code> command line (<code>text/x-psql</code>).
	 */
	PSQL(ContentType.PSQL) {
		@Override
		boolean isUsedFor(String contentType) {
			return ContentType.PSQL.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.PSQL;
		}
	};

	private static final Resources RESOURCES = Resources.getResources(MediaType.class);

	private final String contentType;

	private MediaType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Delegates to {@link #getContentType()}.
	 */
	@Override
	public String toString() {
		return getContentType();
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
	public static MediaType getMediaTypeForContentType(final String fullContentType) throws UnsupportedEncodingException {
		int semiPos = fullContentType.indexOf(';');
		String contentType = ((semiPos==-1) ? fullContentType : fullContentType.substring(0, semiPos)).trim();
		for(MediaType value : values) {
			if(value.isUsedFor(contentType)) return value;
		}
		throw new LocalizedUnsupportedEncodingException(RESOURCES, "getMediaType.unknownType", fullContentType);
	}
}
