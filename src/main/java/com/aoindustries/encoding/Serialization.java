/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.collections.EnumerationIterator;
import com.aoindustries.io.ContentType;
import com.aoindustries.lang.Strings;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * The type of serialization ({@link #SGML} or {@link #XML}).
 *
 * @author  AO Industries, Inc.
 */
public enum Serialization {
	SGML {
		@Override
		public String getContentType() {
			return ContentType.HTML;
		}

		@Override
		public String getSelfClose() {
			return ">";
		}

		// Override to write single character instead of string
		@Override
		public Serialization selfClose(Appendable out) throws IOException {
			out.append('>');
			return this;
		}
	},
	XML {
		@Override
		public String getContentType() {
			return ContentType.XHTML;
		}

		@Override
		public String getSelfClose() {
			return " />";
		}
	};

	/**
	 * The default serialization for older implementations that do not set any.
	 *
	 * @see  #XML
	 */
	public static final Serialization DEFAULT = Serialization.XML;

	/**
	 * Gets the content-type header to use for this serialization.
	 */
	abstract public String getContentType();

	/**
	 * Gets the self-closing tag characters.
	 */
	abstract public String getSelfClose();

	/**
	 * Appends the self-closing tag characters.
	 */
	public Serialization selfClose(Appendable out) throws IOException {
		out.append(getSelfClose());
		return this;
	}

	/**
	 * Determine if the content may be served as <code>application/xhtml+xml</code> by the
	 * rules defined in <a href="http://www.w3.org/TR/xhtml-media-types/">http://www.w3.org/TR/xhtml-media-types/</a>
	 * Default to <code>application/xhtml+xml</code> as discussed at
	 * <a href="https://web.archive.org/web/20080913043830/http://www.smackthemouse.com/xhtmlxml">http://www.smackthemouse.com/xhtmlxml</a>
	 */
	public static Serialization select(Iterator<? extends String> acceptHeaderValues) {
		// Some test accept headers:
		//   Firefox: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5
		//   IE 6: */*
		//   IE 8: */*
		//   IE 8 Compat: */*

		boolean hasAcceptHeader = false;
		boolean hasAcceptApplicationXhtmlXml = false;
		boolean hasAcceptTextHtml = false;
		boolean hasAcceptStarStar = false;
		if(acceptHeaderValues != null) {
			while(acceptHeaderValues.hasNext()) {
				hasAcceptHeader = true;
				for(String value : Strings.split(acceptHeaderValues.next(), ',')) {
					value = value.trim();
					List<String> params = Strings.split(value, ';');
					int paramsSize = params.size();
					if(paramsSize > 0) {
						String acceptType = params.get(0).trim();
						if(acceptType.equals("*/*")) {
							// No q parameter parsing for */*
							hasAcceptStarStar = true;
						} else if(
							// Parse and check the q for these two types
							acceptType.equalsIgnoreCase(ContentType.XHTML)
							|| acceptType.equalsIgnoreCase(ContentType.HTML)
						) {
							// Find any q value
							boolean hasNegativeQ = false;
							for(int paramNum = 1; paramNum < paramsSize; paramNum++) {
								String paramSet = params.get(paramNum).trim();
								if(paramSet.startsWith("q=") || paramSet.startsWith("Q=")) {
									try {
										float q = Float.parseFloat(paramSet.substring(2).trim());
										if(q < 0) {
											hasNegativeQ = true;
											break;
										}
									} catch(NumberFormatException err) {
										// Intentionally ignored
									}
								}
							}
							if(!hasNegativeQ) {
								if(acceptType.equalsIgnoreCase(ContentType.XHTML)) hasAcceptApplicationXhtmlXml = true;
								else if(acceptType.equalsIgnoreCase(ContentType.HTML)) hasAcceptTextHtml = true;
								else throw new AssertionError("Unexpected value for acceptType: " + acceptType);
							}
						}
					}
				}
			}
		}
		// If the Accept header explicitly contains application/xhtml+xml  (with either no "q" parameter or a positive "q" value) deliver the document using that media type.
		if(hasAcceptApplicationXhtmlXml) return XML;
		// If the Accept header explicitly contains text/html  (with either no "q" parameter or a positive "q" value) deliver the document using that media type.
		if(hasAcceptTextHtml) return SGML;
		// If the accept header contains "*/*" (a convention some user agents use to indicate that they will accept anything), deliver the document using text/html.
		if(hasAcceptStarStar) return SGML;
		// If has no accept headers
		if(!hasAcceptHeader) return XML;
		// This choice is not clear from either of the cited documents.  If there is an accept line,
		// and it doesn't have */* or application/xhtml+xml or text/html, we'll serve as text/html
		// since it is a fairly broken client anyway and would be even less likely to know xhtml.
		return SGML;
	}

	public static Serialization select(Iterable<? extends String> acceptHeaderValues) {
		return select(acceptHeaderValues.iterator());
	}

	public static Serialization select(Enumeration<? extends String> acceptHeaderValues) {
		return select(
			new EnumerationIterator<>(acceptHeaderValues)
		);
	}
}
