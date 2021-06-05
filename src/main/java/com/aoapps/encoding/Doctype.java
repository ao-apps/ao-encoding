/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.encoding;

import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoapps.lang.io.ContentType;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author  AO Industries, Inc.
 */
public enum Doctype {
	// See http://www.ibm.com/developerworks/library/x-think45/
	HTML5 {
		@Override
		public String getDoctype(Serialization serialization) {
			return "<!DOCTYPE html>" + WhitespaceWriter.NL;
		}
		@Override
		public String getScriptType() {
			return "";
		}
		@Override
		public Doctype scriptType(Appendable out) throws IOException {
			// Do nothing
			return this;
		}
		@Override
		public String getStyleType() {
			return "";
		}
		@Override
		public Doctype styleType(Appendable out) throws IOException {
			// Do nothing
			return this;
		}
		@Override
		public boolean supportsIRI() {
			return true;
		}
	},
	STRICT {
		@Override
		public String getDoctype(Serialization serialization) {
			switch(serialization) {
				case SGML:
					return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">" + WhitespaceWriter.NL;
				case XML:
					return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" + WhitespaceWriter.NL;
				default:
					throw new AssertionError();
			}
		}
		@Override
		@SuppressWarnings("deprecation")
		public String getScriptType() {
			return " type=\"" + ContentType.JAVASCRIPT_OLD + '"';
		}
		@Override
		public String getStyleType() {
			return " type=\"" + ContentType.CSS + '"';
		}
	},
	TRANSITIONAL {
		@Override
		public String getDoctype(Serialization serialization) {
			switch(serialization) {
				case SGML:
					return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + WhitespaceWriter.NL;
				case XML:
					return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + WhitespaceWriter.NL;
				default:
					throw new AssertionError();
			}
		}
		@Override
		public String getScriptType() {
			return STRICT.getScriptType();
		}
		@Override
		public Doctype scriptType(Appendable out) throws IOException {
			return STRICT.scriptType(out);
		}
		@Override
		public String getStyleType() {
			return STRICT.getStyleType();
		}
		@Override
		public Doctype styleType(Appendable out) throws IOException {
			return STRICT.styleType(out);
		}
	},
	FRAMESET {
		@Override
		public String getDoctype(Serialization serialization) {
			switch(serialization) {
				case SGML:
					return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">" + WhitespaceWriter.NL;
				case XML:
					return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">" + WhitespaceWriter.NL;
				default:
					throw new AssertionError();
			}
		}
		@Override
		public String getScriptType() {
			return STRICT.getScriptType();
		}
		@Override
		public Doctype scriptType(Appendable out) throws IOException {
			return STRICT.scriptType(out);
		}
		@Override
		public String getStyleType() {
			return STRICT.getStyleType();
		}
		@Override
		public Doctype styleType(Appendable out) throws IOException {
			return STRICT.styleType(out);
		}
	},
	NONE {
		@Override
		public String getXmlDeclaration(Serialization serialization, String documentEncoding) {
			return "";
		}
		@Override
		public boolean xmlDeclaration(Serialization serialization, String documentEncoding, Appendable out) {
			// Do nothing
			return false;
		}
		@Override
		public String getDoctype(Serialization serialization) {
			return "";
		}
		@Override
		public boolean doctype(Serialization serialization, Appendable out) throws IOException {
			// Do nothing
			return false;
		}
		@Override
		public String getScriptType() {
			// Very old doctype-less, support IE6: http://www.javascriptkit.com/javatutors/languageattri3.shtml
			return " language=\"JavaScript1.3\"";
		}
		@Override
		public String getStyleType() {
			return STRICT.getStyleType();
		}
		@Override
		public Doctype styleType(Appendable out) throws IOException {
			return STRICT.styleType(out);
		}
	};

	/**
	 * The default doctype for older implementations that do not set any.
	 *
	 * @see  #HTML5
	 */
	public static final Doctype DEFAULT = Doctype.HTML5;

	private static boolean isUTF8(String documentEncoding) {
		return
			StandardCharsets.UTF_8.name().equalsIgnoreCase(documentEncoding)
			|| Charset.forName(documentEncoding) == StandardCharsets.UTF_8;
	}

	public String getXmlDeclaration(Serialization serialization, String documentEncoding) {
		try {
			StringBuilder sb = new StringBuilder();
			xmlDeclaration(serialization, documentEncoding, sb);
			return sb.toString();
		} catch(IOException e) {
			throw new AssertionError("IOException should not occur on StringBuilder", e);
		}
	}

	/**
	 * @return  {@code true} when declaration written (including trailing {@link WhitespaceWriter#NL})
	 */
	public boolean xmlDeclaration(Serialization serialization, String documentEncoding, Appendable out) throws IOException {
		if(serialization == Serialization.XML && !isUTF8(documentEncoding)) {
			out.append("<?xml version=\"1.0\" encoding=\"");
			encodeTextInXhtmlAttribute(documentEncoding, out);
			out.append("\"?>" + WhitespaceWriter.NL);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the <a href="https://www.w3schools.com/tags/tag_doctype.asp">HTML doctype declaration</a> line.
	 */
	public abstract String getDoctype(Serialization serialization);

	/**
	 * Appends the <a href="https://www.w3schools.com/tags/tag_doctype.asp">HTML doctype declaration</a> line, if any.
	 *
	 * @return  {@code true} when doctype written (including trailing {@link WhitespaceWriter#NL})
	 */
	public boolean doctype(Serialization serialization, Appendable out) throws IOException {
		String doctype = getDoctype(serialization);
		if(doctype.isEmpty()) {
			return false;
		} else {
			assert doctype.charAt(doctype.length() - 1) == WhitespaceWriter.NL;
			out.append(doctype);
			return true;
		}
	}

	/**
	 * Gets the default script type/language attribute, if any.
	 *
	 * @return  The attribute, starting with a space, or {@code ""} for none.
	 */
	abstract public String getScriptType();

	/**
	 * Appends the default script type/language attribute, if any.
	 */
	public Doctype scriptType(Appendable out) throws IOException {
		out.append(getScriptType());
		return this;
	}

	/**
	 * Gets the default style type attribute, if any.
	 *
	 * @return  The attribute, starting with a space, or {@code ""} for none.
	 */
	abstract public String getStyleType();

	/**
	 * Appends the default style type attribute, if any.
	 */
	public Doctype styleType(Appendable out) throws IOException {
		out.append(getStyleType());
		return this;
	}

	/**
	 * Does this doctype support <a href="https://tools.ietf.org/html/rfc3987">RFC 3987 IRI</a>
	 * Unicode format URLs?
	 */
	public boolean supportsIRI() {
		return false;
	}
}
