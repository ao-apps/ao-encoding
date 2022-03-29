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

import com.aoapps.lang.NullArgumentException;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.io.Encoder;
import com.aoapps.lang.io.LocalizedUnsupportedEncodingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ResourceBundle;

/**
 * <p>
 * Encodes media to allow it to be contained in a different type of media.
 * For example, one may have plaintext inside of HTML, or arbitrary text inside
 * a JavaScript String inside an onclick attribute of an area tag in a XHTML
 * document.  All necessary encoding is automatically performed.
 * </p>
 * <p>
 * Each encoder both validates its input characters and produces valid output
 * characters.
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public abstract class MediaEncoder implements Encoder, ValidMediaFilter {

	private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, MediaEncoder.class);

	/**
	 * Gets the media encoder for the requested types or {@code null} if
	 * no encoding is necessary.  When an encoder is returned it is also a validator
	 * for the contentType and produces valid output for the containerType.
	 * <p>
	 * An encoder is not needed when no prefix or suffix is needed, all valid
	 * {@code contentType} characters are also valid in {@code containerType},
	 * and with the same representation.  Furthermore, there must be some meaningful
	 * relationship between {@code contentType} and {@code containerType} -
	 * simply having compatible characters alone is insufficient.
	 * </p>
	 * <p>
	 * When no encoder is returned, it is necessary to use {@linkplain MediaValidator a separate validator}
	 * if character validation is required.
	 * </p>
	 * <p>
	 * Please note that all types can be encoded both to and from {@link MediaType#TEXT}.  Thus, when a specialized
	 * encoder is not available (as indicated by throwing {@link UnsupportedEncodingException}), can always use
	 * an intermediate TEXT to connect between types, such as CSS -&gt; TEXT -&gt; XHTML will just display the raw CSS
	 * directly.
	 * </p>
	 * <p>
	 * No automatic intermediate TEXT conversion is done, because the addition of new encoders could suddenly change
	 * the semantics.
	 * </p>
	 *
	 * @param  encodingContext  Required encoding context
	 *
	 * @return the encoder or <code>null</code> if no encoding is necessary
	 *
	 * @exception UnsupportedEncodingException when unable to encode the content into the container
	 *                                         either because it is impossible or not yet implemented.
	 */
	public static MediaEncoder getInstance(EncodingContext encodingContext, MediaType contentType, MediaType containerType) throws UnsupportedEncodingException {
		NullArgumentException.checkNotNull(encodingContext, "encodingContext");
		final MediaEncoder encoder;
		switch(contentType) {
			case CSS :
				switch(containerType) {
					case CSS :
					case TEXT :            return null;
					case XHTML :           encoder = new CssInXhtmlEncoder(encodingContext); break;
					case XHTML_ATTRIBUTE : encoder = CssInXhtmlAttributeEncoder.cssInXhtmlAttributeEncoder; break;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				break;
			case JAVASCRIPT :
				switch(containerType) {
					case JAVASCRIPT :
					case JSON :
					case LD_JSON :         return null;
					case TEXT :            return null;
					case XHTML :           encoder = new JavaScriptInXhtmlEncoder(contentType, encodingContext); break;
					case XHTML_ATTRIBUTE : encoder = JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder; break;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				break;
			case JSON :
			case LD_JSON :
				switch(containerType) {
					case JAVASCRIPT :
					case JSON :
					case LD_JSON :         return null;
					case TEXT :            return null;
					case XHTML :           encoder = new JavaScriptInXhtmlEncoder(contentType, encodingContext); break;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				break;
			case MYSQL :
				switch(containerType) {
					case MYSQL :           return null;
					case TEXT :            return null;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				//break;
			case PSQL :
				switch(containerType) {
					case PSQL :            return null;
					case TEXT :            return null;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				//break;
			case SH :
				switch(containerType) {
					case SH :              return null;
					case TEXT :            return null;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				//break;
			case TEXT:
				switch(containerType) {
					case CSS :             encoder = TextInCssEncoder.textInCssEncoder; break;
					case JAVASCRIPT :      encoder = TextInJavaScriptEncoder.textInJavaScriptEncoder; break;
					case JSON :            encoder = TextInJavaScriptEncoder.textInJsonEncoder; break;
					case LD_JSON :         encoder = TextInJavaScriptEncoder.textInLdJsonEncoder; break;
					case MYSQL :           encoder = TextInMysqlEncoder.textInMysqlEncoder; break;
					case PSQL :            encoder = TextInPsqlEncoder.textInPsqlEncoder; break;
					case SH :              encoder = TextInShEncoder.textInShEncoder; break;
					case TEXT :            return null;
					case XHTML :           encoder = TextInXhtmlEncoder.textInXhtmlEncoder; break;
					case XHTML_ATTRIBUTE : encoder = TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder; break;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				break;
			case URL :
				switch(containerType) {
					case CSS :             encoder = new UrlInCssEncoder(encodingContext); break;
					case JAVASCRIPT :
					case JSON :
					case LD_JSON :         encoder = new UrlInJavaScriptEncoder(containerType, encodingContext); break;
					case TEXT :            return null;
					case URL :             return null;
					case XHTML :           encoder = new UrlInXhtmlEncoder(encodingContext); break;
					case XHTML_ATTRIBUTE : encoder = new UrlInXhtmlAttributeEncoder(encodingContext); break;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				break;
			case XHTML :
				switch(containerType) {
					case TEXT :            return null;
					case XHTML :           return null;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				//break;
			case XHTML_ATTRIBUTE :
				switch(containerType) {
					case TEXT :            return null;
					case XHTML :           return null;
					case XHTML_ATTRIBUTE : return null;
					default :              throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
				}
				//break;
			default : throw new LocalizedUnsupportedEncodingException(RESOURCES, "unableToFindEncoder", contentType.getContentType(), containerType.getContentType());
		}
		// Make sure types match - bug catching
		assert encoder.getValidMediaOutputType() == containerType :
			"encoder.getValidMediaOutputType() != containerType: " + encoder.getValidMediaOutputType() + " != " + containerType;
		assert encoder.getValidMediaInputType() == contentType :
			"encoder.getValidMediaInputType() != contentType: " + encoder.getValidMediaInputType() + " != " + contentType;
		assert encoder.isValidatingMediaInputType(contentType) :
			"encoder = " + encoder.getClass().getName() + " is not validating contentType = " + contentType.name();
		return encoder;
	}

	protected MediaEncoder() {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation validates media types in assertion but writes nothing.
	 * </p>
	 */
	@Override
	public void writePrefixTo(Appendable out) throws IOException {
		assert Assertions.isValidating(out, getValidMediaOutputType());
	}

	@Override
	public abstract void write(int c, Writer out) throws IOException;

	@Override
	public abstract void write(char[] cbuf, Writer out) throws IOException;

	@Override
	public abstract void write(char[] cbuf, int off, int len, Writer out) throws IOException;

	@Override
	public abstract void write(String str, Writer out) throws IOException;

	@Override
	public abstract void write(String str, int off, int len, Writer out) throws IOException;

	@Override
	public abstract MediaEncoder append(char c, Appendable out) throws IOException;

	@Override
	public abstract MediaEncoder append(CharSequence csq, Appendable out) throws IOException;

	@Override
	public abstract MediaEncoder append(CharSequence csq, int start, int end, Appendable out) throws IOException;

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation calls {@link #writeSuffixTo(java.lang.Appendable, boolean)} without trimming.
	 * </p>
	 *
	 * @deprecated  Please use {@link #writeSuffixTo(java.lang.Appendable, boolean)} while specifying desired trim.
	 */
	@Deprecated
	@Override
	public final void writeSuffixTo(Appendable out) throws IOException {
		writeSuffixTo(out, false);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation validates media types in assertion but writes nothing.
	 * </p>
	 */
	@Override
	public void writeSuffixTo(Appendable out, boolean trim) throws IOException {
		assert Assertions.isValidating(out, getValidMediaOutputType());
	}
}
