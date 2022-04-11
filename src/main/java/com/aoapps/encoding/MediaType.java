/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.ContentType;
import com.aoapps.lang.io.Encoder;
import com.aoapps.lang.io.LocalizedUnsupportedEncodingException;
import com.aoapps.lang.io.function.IOConsumer;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Supported content types.
 *
 * @author  AO Industries, Inc.
 */
@ThreadSafe
@Immutable
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

		@Override
		public XhtmlWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new XhtmlWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public XhtmlWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new XhtmlWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return Xhtml.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return XhtmlWriter.class;
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

		@Override
		public XhtmlAttributeWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new XhtmlAttributeWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public XhtmlAttributeWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new XhtmlAttributeWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return XhtmlAttribute.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return XhtmlAttributeWriter.class;
		}
	},

	/**
	 * An HTML document (<code>text/html</code>).
	 */
	// HTML(ContentType.HTML),

	/**
	 * A CSS stylesheet (<code>text/css</code>).
	 */
	CSS(ContentType.CSS) {
		@Override
		boolean isUsedFor(String contentType) {
			return ContentType.CSS.equalsIgnoreCase(contentType);
		}

		@Override
		public MarkupType getMarkupType() {
			return MarkupType.CSS;
		}

		@Override
		public StyleWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new StyleWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public StyleWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new StyleWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return Style.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return StyleWriter.class;
		}
	},

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

		@Override
		public JavaScriptWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new JavaScriptWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public JavaScriptWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new JavaScriptWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return JavaScript.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return JavaScriptWriter.class;
		}
	},

	/**
	 * A JSON object graph (<code>application/json</code>).
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

		@Override
		public JsonWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new JsonWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public JsonWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new JsonWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return Json.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return JsonWriter.class;
		}
	},

	/**
	 * JSON linked data (<code>application/ld+json</code>).
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

		@Override
		public LdJsonWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new LdJsonWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public LdJsonWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new LdJsonWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return LdJson.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return LdJsonWriter.class;
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

		@Override
		public TextWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new TextWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public TextWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new TextWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return Text.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return TextWriter.class;
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

		@Override
		public boolean getTrimBuffer() {
			return true;
		}

		@Override
		public UrlWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new UrlWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public UrlWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new UrlWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return Url.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return UrlWriter.class;
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

		@Override
		public ShWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new ShWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public ShWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new ShWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return Sh.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return ShWriter.class;
		}
	},

	/**
	 * MySQL <code>mysql</code> command input (<code>text/x-mysql</code>).
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

		@Override
		public MysqlWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new MysqlWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public MysqlWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new MysqlWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return Mysql.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return MysqlWriter.class;
		}
	},

	/**
	 * PostgreSQL <code>psql</code> command input (<code>text/x-psql</code>).
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

		@Override
		public PsqlWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out,
			boolean outOptimized,
			Whitespace indentDelegate,
			Predicate<? super MediaWriter> isNoClose,
			IOConsumer<? super MediaWriter> closer
		) {
			return new PsqlWriter(encodingContext, encoder, out, outOptimized, indentDelegate, isNoClose, closer);
		}

		@Override
		public PsqlWriter newMediaWriter(
			EncodingContext encodingContext,
			MediaEncoder encoder,
			Writer out
		) {
			return new PsqlWriter(encodingContext, encoder, out);
		}

		@Override
		Class<? extends Encode> getEncodeInterface() {
			return Psql.class;
		}

		@Override
		Class<? extends MediaWriter> getMediaWriterClass() {
			return PsqlWriter.class;
		}
	};

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, MediaType.class);

	private final String contentType;

	private MediaType(String contentType) {
		this.contentType = contentType;
		assert getEncodeInterface().isAssignableFrom(getMediaWriterClass()) : "Nested self-encoding is always allowed";
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

	/**
	 * Should this content type generally be trimmed before validation?
	 *
	 * @see  MediaValidator#validate(boolean)
	 * @see  Encoder#writeSuffixTo(java.lang.Appendable, boolean)
	 * @see  MediaEncoder#writeSuffixTo(java.lang.Appendable, boolean)
	 */
	public boolean getTrimBuffer() {
		return false;
	}

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

	/**
	 * Creates a new instance of {@link MediaWriter} for this media type.
	 *
	 * @param  out  Conditionally passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 * @param  outOptimized  Is {@code out} already known to have been passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}?
	 * @param  indentDelegate  When non-null, indentation depth is get/set on the provided {@link Whitespace}, otherwise tracks directly on this writer.
	 *                         This allows the indentation to be coordinated between nested content types.
	 * @param  isNoClose  Called to determine result of {@link MediaWriter#isNoClose()}
	 * @param  closer  Called on {@link MediaWriter#close()}, which may optionally perform final suffix write and/or close the underlying writer,
	 *                 will only be called to be idempotent, implementation can assume will only be called once.
	 */
	public abstract MediaWriter newMediaWriter(
		EncodingContext encodingContext,
		MediaEncoder encoder,
		Writer out,
		boolean outOptimized,
		Whitespace indentDelegate,
		Predicate<? super MediaWriter> isNoClose,
		IOConsumer<? super MediaWriter> closer
	);

	/**
	 * Creates a new instance of {@link MediaWriter} for this media type.
	 *
	 * @param  out  Passed through {@link Coercion#optimize(java.io.Writer, com.aoapps.lang.io.Encoder)}
	 *
	 * @see  MediaWriter#DEFAULT_IS_NO_CLOSE
	 * @see  MediaWriter#DEFAULT_CLOSER
	 */
	public abstract MediaWriter newMediaWriter(
		EncodingContext encodingContext,
		MediaEncoder encoder,
		Writer out
	);

	/**
	 * Gets the per-type encode interface matching this media type.
	 */
	abstract Class<? extends Encode> getEncodeInterface();

	/**
	 * Gets the per-type writer class matching this media type.
	 */
	abstract Class<? extends MediaWriter> getMediaWriterClass();
}
