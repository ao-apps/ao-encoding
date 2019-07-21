/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2015, 2016, 2017, 2018, 2019  AO Industries, Inc.
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

import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute;
import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.JavaScriptInXhtmlEncoder.encodeJavaScriptInXhtml;
import static com.aoindustries.encoding.JavaScriptInXhtmlEncoder.javaScriptInXhtmlEncoder;
import static com.aoindustries.encoding.TextInJavaScriptEncoder.encodeTextInJavaScript;
import static com.aoindustries.encoding.TextInJavaScriptEncoder.textInJavaScriptEncoder;
import static com.aoindustries.encoding.TextInMysqlEncoder.textInMysqlEncoder;
import static com.aoindustries.encoding.TextInPsqlEncoder.textInPsqlEncoder;
import static com.aoindustries.encoding.TextInShEncoder.textInShEncoder;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.TextInXhtmlEncoder.encodeTextInXhtml;
import static com.aoindustries.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoindustries.io.Writable;
import com.aoindustries.sql.SQLUtility;
import com.aoindustries.util.Sequence;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import com.aoindustries.util.i18n.MarkupType;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Locale;

/**
 * A chain writer encapsulates a <code>{@link PrintWriter}</code> and returns the <code>{@link ChainWriter}</code>
 * instance on most methods.  This gives the ability to call code like
 * {@code out.print("Hi ").print(name).print('!');}
 *
 * @author  AO Industries, Inc.
 */
final public class ChainWriter implements Appendable, Closeable {

	// <editor-fold defaultstate="collapsed" desc="PrintWriter wrapping">
	private final PrintWriter out;
	private final MediaWriter javaScriptInXhtmlAttributeWriter;
	private final MediaWriter javaScriptInXhtmlWriter;

	/**
	 * Create a new PrintWriter, without automatic line flushing, from an
	 * existing OutputStream.  This convenience constructor creates the
	 * necessary intermediate OutputStreamWriter, which will convert characters
	 * into bytes using the default character encoding.
	 *
	 * @param  out        An output stream
	 */
	public ChainWriter(OutputStream out) {
		this(new PrintWriter(out));
	}

	/**
	 * Create a new PrintWriter from an existing OutputStream.  This
	 * convenience constructor creates the necessary intermediate
	 * OutputStreamWriter, which will convert characters into bytes using the
	 * default character encoding.
	 *
	 * @param  out        An output stream
	 * @param  autoFlush  A boolean; if true, the println() methods will flush
	 *                    the output buffer
	 */
	public ChainWriter(OutputStream out, boolean autoFlush) {
		this(new PrintWriter(out, autoFlush));
	}

	public ChainWriter(PrintWriter out) {
		this.out=out;
		javaScriptInXhtmlAttributeWriter = new MediaWriter(javaScriptInXhtmlAttributeEncoder, out);
		javaScriptInXhtmlWriter = new MediaWriter(javaScriptInXhtmlEncoder, out);
	}

	/**
	 * Create a new PrintWriter, without automatic line flushing.
	 *
	 * @param  out        A character-output stream
	 */
	public ChainWriter(Writer out) {
		this(new PrintWriter(out));
	}

	/**
	 * Create a new PrintWriter.
	 *
	 * @param  out        A character-output stream
	 * @param  autoFlush  A boolean; if true, the println() methods will flush
	 *                    the output buffer
	 */
	public ChainWriter(Writer out, boolean autoFlush) {
		this(new PrintWriter(out, autoFlush));
	}

	public PrintWriter getPrintWriter() {
		return out;
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Nearly PrintWriter source compatible">

	/**
	 * Flushes the stream.
	* <p>
	* Unlike <code>{@link PrintWriter#flush()}</code>, exceptions are thrown immediately, as requiring the caller
	* to remember to invoke <code>{@link PrintWriter#checkError()}</code> too easily leads to swallowed
	* exceptions and hard-to-diagnose runtime problems.
	* </p>
	 */
	public ChainWriter flush() throws IOException {
		//out.flush();
		if(out.checkError()) throw new IOException("Error occured on underlying PrintWriter");
		return this;
	}

	/**
	 * Closes the stream.
	* <p>
	* Unlike <code>{@link PrintWriter#close()}</code>, exceptions are thrown immediately, as requiring the caller
	* to remember to invoke <code>{@link PrintWriter#checkError()}</code> too easily leads to swallowed
	* exceptions and hard-to-diagnose runtime problems.
	* </p>
	 */
	@Override
	public void close() throws IOException {
		out.close();
		if(out.checkError()) throw new IOException("Error occured on underlying PrintWriter");
	}

	/** Write a single character. */
	public ChainWriter write(int c) {
		out.write(c);
		return this;
	}

	/** Write a portion of an array of characters. */
	public ChainWriter write(char buf[], int off, int len) {
		out.write(buf, off, len);
		return this;
	}

	/**
	 * Write an array of characters.  This method cannot be inherited from the
	 * Writer class because it must suppress I/O exceptions.
	 */
	public ChainWriter write(char buf[]) {
		out.write(buf);
		return this;
	}

	/** Write a portion of a string. */
	public ChainWriter write(String s, int off, int len) {
		out.write(s, off, len);
		return this;
	}

	/**
	 * Write a string.  This method cannot be inherited from the Writer class
	 * because it must suppress I/O exceptions.
	 */
	public ChainWriter write(String s) {
		out.write(s);
		return this;
	}

	/**
	 * Print a boolean value.  The string produced by <code>{@link
	 * java.lang.String#valueOf(boolean)}</code> is translated into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link
	 * #write(int)}</code> method.
	 *
	 * @param      b   The <code>boolean</code> to be printed
	 */
	public ChainWriter print(boolean b) {
		out.print(b);
		return this;
	}

	/**
	 * Print a character.  The character is translated into one or more bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link
	 * #write(int)}</code> method.
	 *
	 * @param      c   The <code>char</code> to be printed
	 */
	public ChainWriter print(char c) {
		out.print(c);
		return this;
	}

	/**
	 * Print an integer.  The string produced by <code>{@link
	 * java.lang.String#valueOf(int)}</code> is translated into bytes according
	 * to the platform's default character encoding, and these bytes are
	 * written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      i   The <code>int</code> to be printed
	 * @see        java.lang.Integer#toString(int)
	 */
	public ChainWriter print(int i) {
		out.print(i);
		return this;
	}

	/**
	 * Print a long integer.  The string produced by <code>{@link
	 * java.lang.String#valueOf(long)}</code> is translated into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      l   The <code>long</code> to be printed
	 */
	public ChainWriter print(long l) {
		out.print(l);
		return this;
	}

	/**
	 * Print a floating-point number.  The string produced by <code>{@link
	 * java.lang.String#valueOf(float)}</code> is translated into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      f   The <code>float</code> to be printed
	 * @see        java.lang.Float#toString(float)
	 */
	public ChainWriter print(float f) {
		out.print(f);
		return this;
	}

	/**
	 * Print a double-precision floating-point number.  The string produced by
	 * <code>{@link java.lang.String#valueOf(double)}</code> is translated into
	 * bytes according to the platform's default character encoding, and these
	 * bytes are written in exactly the manner of the <code>{@link
	 * #write(int)}</code> method.
	 *
	 * @param      d   The <code>double</code> to be printed
	 */
	public ChainWriter print(double d) {
		out.print(d);
		return this;
	}

	/**
	 * Print an array of characters.  The characters are converted into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      s   The array of chars to be printed
	 *
	 * @throws  NullPointerException  If <code>s</code> is {@code null}
	 */
	public ChainWriter print(char s[]) {
		out.print(s);
		return this;
	}

	/**
	 * Print a string.  If the argument is {@code null} then the string
	 * <code>"null"</code> is printed.  Otherwise, the string's characters are
	 * converted into bytes according to the platform's default character
	 * encoding, and these bytes are written in exactly the manner of the
	 * <code>{@link #write(int)}</code> method.
	 *
	 * @param      s   The <code>String</code> to be printed
	 */
	public ChainWriter print(String s) {
		out.print(s);
		return this;
	}

	/**
	 * Print an object.  The string produced by the <code>{@link
	 * java.lang.String#valueOf(Object)}</code> method is translated into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      obj   The <code>Object</code> to be printed
	 * @see        java.lang.Object#toString()
	 */
	public ChainWriter print(Object obj) {
		out.print(obj);
		return this;
	}

	/**
	 * Terminate the current line by writing the line separator string.  The
	 * line separator string is defined by the system property
	 * <code>line.separator</code>, and is not necessarily a single newline
	 * character (<code>'\n'</code>).
	 */
	public ChainWriter println() {
		out.println();
		return this;
	}

	/**
	 * Print a boolean value and then terminate the line.  This method behaves
	 * as though it invokes <code>{@link #print(boolean)}</code> and then
	 * <code>{@link #println()}</code>.
	 */
	public ChainWriter println(boolean x) {
		out.println(x);
		return this;
	}

	/**
	 * Print a character and then terminate the line.  This method behaves as
	 * though it invokes <code>{@link #print(char)}</code> and then <code>{@link
	 * #println()}</code>.
	 */
	public ChainWriter println(char x) {
		out.println(x);
		return this;
	}

	/**
	 * Print an integer and then terminate the line.  This method behaves as
	 * though it invokes <code>{@link #print(int)}</code> and then <code>{@link
	 * #println()}</code>.
	 */
	public ChainWriter println(int x) {
		out.println(x);
		return this;
	}

	/**
	 * Print a long integer and then terminate the line.  This method behaves
	 * as though it invokes <code>{@link #print(long)}</code> and then
	 * <code>{@link #println()}</code>.
	 */
	public ChainWriter println(long x) {
		out.println(x);
		return this;
	}

	/**
	 * Print a floating-point number and then terminate the line.  This method
	 * behaves as though it invokes <code>{@link #print(float)}</code> and then
	 * <code>{@link #println()}</code>.
	 */
	public ChainWriter println(float x) {
		out.println(x);
		return this;
	}

	/**
	 * Print a double-precision floating-point number and then terminate the
	 * line.  This method behaves as though it invokes <code>{@link
	 * #print(double)}</code> and then <code>{@link #println()}</code>.
	 */
	public ChainWriter println(double x) {
		out.println(x);
		return this;
	}

	/**
	 * Print an array of characters and then terminate the line.  This method
	 * behaves as though it invokes <code>{@link #print(char[])}</code> and then
	 * <code>{@link #println()}</code>.
	 */
	public ChainWriter println(char x[]) {
		out.println(x);
		return this;
	}

	/**
	 * Print a String and then terminate the line.  This method behaves as
	 * though it invokes <code>{@link #print(String)}</code> and then
	 * <code>{@link #println()}</code>.
	 */
	public ChainWriter println(String x) {
		out.println(x);
		return this;
	}

	/**
	 * Print an Object and then terminate the line.  This method behaves as
	 * though it invokes <code>{@link #print(Object)}</code> and then
	 * <code>{@link #println()}</code>.
	 */
	public ChainWriter println(Object x) {
		out.println(x);
		return this;
	}

	public ChainWriter printf(String format, Object ... args) {
		out.printf(format, args);
		return this;
	}

	public ChainWriter printf(Locale l, String format, Object ... args) {
		out.printf(l, format, args);
		return this;
	}

	public ChainWriter format(String format, Object ... args) {
		out.format(format, args);
		return this;
	}

	public ChainWriter format(Locale l, String format, Object ... args) {
		out.format(l, format, args);
		return this;
	}

	@Override
	public ChainWriter append(CharSequence csq) {
		out.append(csq);
		return this;
	}

	@Override
	public ChainWriter append(CharSequence csq, int start, int end) {
		out.append(csq, start, end);
		return this;
	}

	@Override
	public ChainWriter append(char c) {
		out.append(c);
		return this;
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Encoding Methods">
	/**
	 * @see  TextInXhtmlAttributeEncoder
	 *
	 * @param  value  the value to be encoded
	 */
	public ChainWriter encodeXmlAttribute(Object value) throws IOException {
		Coercion.write(value, textInXhtmlAttributeEncoder, out);
		return this;
	}

	/**
	 * @see  TextInXhtmlEncoder
	 *
	 * @param  value  the value to be encoded
	 */
	public ChainWriter encodeXhtml(Object value) throws IOException {
		if(value!=null) {
			if(
				value instanceof Writable
				&& !((Writable)value).isFastToString()
			) {
				// Avoid unnecessary toString calls
				Coercion.write(value, textInXhtmlEncoder, out);
			} else {
				String str = Coercion.toString(value);
				BundleLookupMarkup lookupMarkup;
				BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext(false);
				if(threadContext!=null) {
					lookupMarkup = threadContext.getLookupMarkup(str);
				} else {
					lookupMarkup = null;
				}
				if(lookupMarkup!=null) lookupMarkup.appendPrefixTo(MarkupType.XHTML, out);
				textInXhtmlEncoder.write(str, out);
				if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(MarkupType.XHTML, out);
			}
		}
		return this;
	}

	/**
	 * Escapes HTML for displaying in browsers and writes to the internal <code>{@link PrintWriter}</code>.
	 * Has makeBr and makeNbsp enabled.
	 *
	 * @param value the string to be escaped.
	 *
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * @see  #encodeXhtml(java.lang.Object)
	 */
	@Deprecated
	public ChainWriter encodeHtml(Object value) throws IOException {
		com.aoindustries.util.EncodingUtils.encodeHtml(value, true, true, out);
		return this;
	}

	/**
	 * Escapes HTML for displaying in browsers and writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @param value the string to be escaped.
	 * @param make_br  will write &lt;br /&gt; tags for every newline character
	 * @param make_nbsp  will write &amp;#160; for a space when another space follows
	 *
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 * @see  #encodeXhtml(java.lang.Object)
	 */
	@Deprecated
	public ChainWriter encodeHtml(Object value, boolean make_br, boolean make_nbsp) throws IOException {
		com.aoindustries.util.EncodingUtils.encodeHtml(value, make_br, make_nbsp, out);
		return this;
	}

	/**
	 * @see TextInJavaScriptEncoder#encodeTextInJavaScript(java.lang.CharSequence, java.lang.Appendable)
	 *
	 * @deprecated
	 */
	@Deprecated
	public ChainWriter encodeJavaScriptString(String S) throws IOException {
		encodeTextInJavaScript(S, out);
		return this;
	}

	/**
	 * Encodes a javascript string for use in an XML attribute context.  Quotes
	 * are added around the string.  Also, if the string is translated, comments
	 * will be added giving the translation lookup id to aid in translation of
	 * server-translated values in JavaScript.
	 * 
	 * @see  Coercion#toString(java.lang.Object)
	 */
	public ChainWriter encodeJavaScriptStringInXmlAttribute(Object value) throws IOException {
		// Two stage encoding:
		//   1) Text -> JavaScript (with quotes added)
		//   2) JavaScript -> XML Attribute
		if(
			value instanceof Writable
			&& !((Writable)value).isFastToString()
		) {
			// Avoid unnecessary toString calls
			textInJavaScriptEncoder.writePrefixTo(javaScriptInXhtmlAttributeWriter);
			Coercion.write(value, textInJavaScriptEncoder, javaScriptInXhtmlAttributeWriter);
			textInJavaScriptEncoder.writeSuffixTo(javaScriptInXhtmlAttributeWriter);
		} else {
			String str = Coercion.toString(value);
			BundleLookupMarkup lookupMarkup;
			BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext(false);
			if(threadContext!=null) {
				lookupMarkup = threadContext.getLookupMarkup(str);
			} else {
				lookupMarkup = null;
			}
			if(lookupMarkup!=null) lookupMarkup.appendPrefixTo(MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeWriter);
			textInJavaScriptEncoder.writePrefixTo(javaScriptInXhtmlAttributeWriter);
			textInJavaScriptEncoder.write(str, javaScriptInXhtmlAttributeWriter);
			textInJavaScriptEncoder.writeSuffixTo(javaScriptInXhtmlAttributeWriter);
			if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeWriter);
		}
		return this;
	}

	/**
	 * Encodes a javascript string for use in an XML body CDATA context.  Quotes
	 * are added around the string.  Also, if the string is translated, comments
	 * will be added giving the translation lookup id to aid in translation of
	 * server-translated values in JavaScript.
	 * 
	 * @see  Coercion#toString(java.lang.Object)
	 */
	public ChainWriter encodeJavaScriptStringInXhtml(Object value) throws IOException {
		// Two stage encoding:
		//   1) Text -> JavaScript (with quotes added)
		//   2) JavaScript -> XHTML
		if(
			value instanceof Writable
			&& !((Writable)value).isFastToString()
		) {
			// Avoid unnecessary toString calls
			textInJavaScriptEncoder.writePrefixTo(javaScriptInXhtmlWriter);
			Coercion.write(value, textInJavaScriptEncoder, javaScriptInXhtmlWriter);
			textInJavaScriptEncoder.writeSuffixTo(javaScriptInXhtmlWriter);
		} else {
			String str = Coercion.toString(value);
			BundleLookupMarkup lookupMarkup;
			BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext(false);
			if(threadContext!=null) {
				lookupMarkup = threadContext.getLookupMarkup(str);
			} else {
				lookupMarkup = null;
			}
			if(lookupMarkup!=null) lookupMarkup.appendPrefixTo(MarkupType.JAVASCRIPT, javaScriptInXhtmlWriter);
			textInJavaScriptEncoder.writePrefixTo(javaScriptInXhtmlWriter);
			textInJavaScriptEncoder.write(str, javaScriptInXhtmlWriter);
			textInJavaScriptEncoder.writeSuffixTo(javaScriptInXhtmlWriter);
			if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(MarkupType.JAVASCRIPT, javaScriptInXhtmlWriter);
		}
		return this;
	}

	/**
	 * Prints a value that may be placed in a URL.
	 *
	 * @deprecated  Use URLEncoder instead.
	 * @see URLEncoder
	 */
	@Deprecated
	public ChainWriter printEU(String value) {
		int len = value.length();
		for (int c = 0; c < len; c++) {
			char ch = value.charAt(c);
			if (ch == ' ') out.print('+');
			else {
				if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) out.print(ch);
				else {
					out.print('%');
					out.print(getHex(ch >>> 4));
					out.print(getHex(ch));
				}
			}
		}
		return this;
	}

	/**
	 * This is must be used within a {@code E'...'} string.
	 *
	 * @see  TextInMysqlEncoder
	 *
	 * @param  value  the value to be encoded
	 */
	public ChainWriter encodeMysql(Object value) throws IOException {
		Coercion.write(value, textInMysqlEncoder, out);
		return this;
	}

	/**
	 * This is must be used within a {@code E'...'} string.
	 *
	 * @see  TextInPsqlEncoder
	 *
	 * @param  value  the value to be encoded
	 */
	public ChainWriter encodePsql(Object value) throws IOException {
		Coercion.write(value, textInPsqlEncoder, out);
		return this;
	}

	/**
	 * This is must be used within a {@code $'...'} string.
	 *
	 * @see  TextInShEncoder
	 *
	 * @param  value  the value to be encoded
	 */
	public ChainWriter encodeSh(Object value) throws IOException {
		Coercion.write(value, textInShEncoder, out);
		return this;
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="HTML Utilities">
	private static final char[] hexChars={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	private static char getHex(int value) {
		return hexChars[value & 15];
	}

	/**
	 * Prints a color in HTML format #xxxxxx, where xxxxxx is the hex code.
	 */
	public static void writeHtmlColor(int color, Appendable out) throws IOException {
		out.append('#');
		out.append(getHex(color >>> 20));
		out.append(getHex(color >>> 16));
		out.append(getHex(color >>> 12));
		out.append(getHex(color >>> 8));
		out.append(getHex(color >>> 4));
		out.append(getHex(color));
	}

	/**
	 * @deprecated  Please use writeHtmlColor instead.
	 */
	@Deprecated
	public ChainWriter printHTMLColor(int color) throws IOException {
		return writeHtmlColor(color);
	}

	/**
	 * Prints a color in HTML format #xxxxxx, where xxxxxx is the hex code.
	 */
	public ChainWriter writeHtmlColor(int color) throws IOException {
		writeHtmlColor(color, out);
		return this;
	}

	/**
	 * Prints a JavaScript script that will preload the image at the provided URL.
	 *
	 * @param url This should be the URL-encoded URL, but with only a standalone ampersand (&amp;) as parameter separator
	 *             (not &amp;amp;)
	 */
	public static void writeHtmlImagePreloadJavaScript(String url, Appendable out) throws IOException {
		out.append("<script type='text/javascript'>\n"
				+ "  var img=new Image();\n"
				+ "  img.src=\"");
		// Escape for javascript
		StringBuilder javascript = new StringBuilder(url.length());
		encodeTextInJavaScript(url, javascript);
		// Encode for XML attribute
		encodeJavaScriptInXhtmlAttribute(javascript, out);
		out.append("\";\n"
				+ "</script>");
	}

	/**
	 * @deprecated  Please use writeHtmlImagePreloadJavaScript instead.
	 */
	@Deprecated
	public ChainWriter printImagePreloadJS(String url) throws IOException {
		return writeHtmlImagePreloadJavaScript(url);
	}

	/**
	 * Prints a JavaScript script that will preload the image at the provided URL.
	 */
	public ChainWriter writeHtmlImagePreloadJavaScript(String url) throws IOException {
		writeHtmlImagePreloadJavaScript(url, out);
		return this;
	}

	/**
	 * Writes a JavaScript script tag that shows a date in the user's locale.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 */
	public static void writeDateJavaScript(long date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
		String dateString = SQLUtility.getDate(date);
		long id = sequence.getNextSequenceValue();
		String idString = Long.toString(id);
		// Write the element
		out.append("<span id=\"chainWriterDate");
		out.append(idString);
		out.append("\">");
		encodeTextInXhtml(dateString, out);
		out.append("</span>");
		// Write the shared script only on first sequence
		if(id==1) {
			scriptOut.append("  function chainWriterUpdateDate(id, millis, serverValue) {\n"
						   + "    if(document.getElementById) {\n"
						   + "      var date=new Date(millis);\n"
						   + "      var clientValue=date.getFullYear() + \"-\";\n"
						   + "      var month=date.getMonth()+1;\n"
						   + "      if(month<10) clientValue+=\"0\";\n"
						   + "      clientValue+=month+\"-\";\n"
						   + "      var day=date.getDate();\n"
						   + "      if(day<10) clientValue+=\"0\";\n"
						   + "      clientValue+=day;\n"
						   + "      if(clientValue!=serverValue) document.getElementById(\"chainWriterDate\"+id).firstChild.nodeValue=clientValue;\n"
						   + "    }\n"
						   + "  }\n");
		}
		scriptOut.append("  chainWriterUpdateDate(");
		scriptOut.append(idString);
		scriptOut.append(", ");
		scriptOut.append(Long.toString(date));
		scriptOut.append(", \"");
		encodeJavaScriptInXhtml(dateString, scriptOut);
		scriptOut.append("\");\n");
	}

	/**
	 * Writes a JavaScript script tag that shows a date in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 *
	 * @see  #writeDateJavaScript(long, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public static void writeDateJavaScript(Long date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
		if(date == null) out.append("&#160;");
		else writeDateJavaScript(date.longValue(), sequence, out, scriptOut);
	}

	/**
	 * Writes a JavaScript script tag that shows a date in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 *
	 * @see  #writeDateJavaScript(long, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public static void writeDateJavaScript(Date date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
		if(date == null) out.append("&#160;");
		else writeDateJavaScript(date.getTime(), sequence, out, scriptOut);
	}

	/**
	 * Prints a JavaScript script tag that shows a date in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code -1}.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @deprecated
	 * @see  #writeDateJavaScript(long, com.aoindustries.util.Sequence, java.lang.Appendable)
	 */
	@Deprecated
	public ChainWriter printDateJS(long date, Sequence sequence, Appendable scriptOut) throws IOException {
		if(date == -1) out.append("&#160;");
		else writeDateJavaScript(date, sequence, scriptOut);
		return this;
	}

	/**
	 * Writes a JavaScript script tag that shows a date in the user's locale.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @see  #writeDateJavaScript(long, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public ChainWriter writeDateJavaScript(long date, Sequence sequence, Appendable scriptOut) throws IOException {
		writeDateJavaScript(date, sequence, out, scriptOut);
		return this;
	}

	/**
	 * Writes a JavaScript script tag that shows a date in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @see  #writeDateJavaScript(java.lang.Long, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public ChainWriter writeDateJavaScript(Long date, Sequence sequence, Appendable scriptOut) throws IOException {
		writeDateJavaScript(date, sequence, out, scriptOut);
		return this;
	}

	/**
	 * Writes a JavaScript script tag that shows a date in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @see  #writeDateJavaScript(java.util.Date, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public ChainWriter writeDateJavaScript(Date date, Sequence sequence, Appendable scriptOut) throws IOException {
		writeDateJavaScript(date, sequence, out, scriptOut);
		return this;
	}

	/**
	 * Writes a JavaScript script tag that shows a date and time in the user's locale.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 */
	public static void writeDateTimeJavaScript(long date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
		String dateTimeString = SQLUtility.getDateTime(date, false);
		long id = sequence.getNextSequenceValue();
		String idString = Long.toString(id);
		// Write the element
		out.append("<span id=\"chainWriterDateTime");
		out.append(idString);
		out.append("\">");
		encodeTextInXhtml(dateTimeString, out);
		out.append("</span>");
		// Write the shared script only on first sequence
		if(id==1) {
			scriptOut.append("  function chainWriterUpdateDateTime(id, millis, serverValue) {\n"
						   + "    if(document.getElementById) {\n"
						   + "      var date=new Date(millis);\n"
						   + "      var clientValue=date.getFullYear() + \"-\";\n"
						   + "      var month=date.getMonth()+1;\n"
						   + "      if(month<10) clientValue+=\"0\";\n"
						   + "      clientValue+=month+\"-\";\n"
						   + "      var day=date.getDate();\n"
						   + "      if(day<10) clientValue+=\"0\";\n"
						   + "      clientValue+=day+\" \";\n"
						   + "      var hour=date.getHours();\n"
						   + "      if(hour<10) clientValue+=\"0\";\n"
						   + "      clientValue+=hour+\":\";\n"
						   + "      var minute=date.getMinutes();\n"
						   + "      if(minute<10) clientValue+=\"0\";\n"
						   + "      clientValue+=minute+\":\";\n"
						   + "      var second=date.getSeconds();\n"
						   + "      if(second<10) clientValue+=\"0\";\n"
						   + "      clientValue+=second;\n"
						   + "      if(clientValue!=serverValue) document.getElementById(\"chainWriterDateTime\"+id).firstChild.nodeValue=clientValue;\n"
						   + "    }\n"
						   + "  }\n");
		}
		scriptOut.append("  chainWriterUpdateDateTime(");
		scriptOut.append(idString);
		scriptOut.append(", ");
		scriptOut.append(Long.toString(date));
		scriptOut.append(", \"");
		encodeJavaScriptInXhtml(dateTimeString, scriptOut);
		scriptOut.append("\");\n");
	}

	/**
	 * Writes a JavaScript script tag that shows a date and time in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 *
	 * @see  #writeDateTimeJavaScript(long, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public static void writeDateTimeJavaScript(Long date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
		if(date == null) out.append("&#160;");
		else writeDateTimeJavaScript(date.longValue(), sequence, out, scriptOut);
	}

	/**
	 * Writes a JavaScript script tag that shows a date and time in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 *
	 * @see  #writeDateTimeJavaScript(long, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public static void writeDateTimeJavaScript(Date date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
		if(date == null) out.append("&#160;");
		else writeDateTimeJavaScript(date.getTime(), sequence, out, scriptOut);
	}

	/**
	 * Writes a JavaScript script tag that shows a date and time in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code -1}.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @deprecated
	 * @see #writeDateTimeJavaScript(long, com.aoindustries.util.Sequence, java.lang.Appendable)
	 */
	@Deprecated
	public ChainWriter printDateTimeJS(long date, Sequence sequence, Appendable scriptOut) throws IOException {
		if(date == -1) out.append("&#160;");
		else writeDateTimeJavaScript(date, sequence, scriptOut);
		return this;
	}

	/**
	 * Writes a JavaScript script tag that shows a date and time in the user's locale.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @see #writeDateTimeJavaScript(long, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public ChainWriter writeDateTimeJavaScript(long date, Sequence sequence, Appendable scriptOut) throws IOException {
		writeDateTimeJavaScript(date, sequence, out, scriptOut);
		return this;
	}

	/**
	 * Writes a JavaScript script tag that shows a date and time in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @see #writeDateTimeJavaScript(java.lang.Long, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public ChainWriter writeDateTimeJavaScript(Long date, Sequence sequence, Appendable scriptOut) throws IOException {
		writeDateTimeJavaScript(date, sequence, out, scriptOut);
		return this;
	}

	/**
	 * Writes a JavaScript script tag that shows a date and time in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @see #writeDateTimeJavaScript(java.util.Date, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public ChainWriter writeDateTimeJavaScript(Date date, Sequence sequence, Appendable scriptOut) throws IOException {
		writeDateTimeJavaScript(date, sequence, out, scriptOut);
		return this;
	}

	/**
	 * Writes a JavaScript script tag that a time in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * <p>
	 * Because this needs to modify the DOM it can lead to poor performance or large data sets.
	 * To provide more performance options, the JavaScript is written to scriptOut.  This could
	 * then be buffered into one long script to execute at once or using body.onload.
	 * </p>
	 * <p>
	 * The provided sequence should start at one for any given HTML page because parts of the
	 * script will only be written when the sequence is equal to one.
	 * </p>
	 */
	public static void writeTimeJavaScript(Date date, Sequence sequence, Appendable out, Appendable scriptOut) throws IOException {
		if(date == null) out.append("&#160;");
		else {
			String timeString = SQLUtility.getTime(date.getTime(), false);
			long id = sequence.getNextSequenceValue();
			String idString = Long.toString(id);
			// Write the element
			out.append("<span id=\"chainWriterTime");
			out.append(idString);
			out.append("\">");
			encodeTextInXhtml(timeString, out);
			out.append("</span>");
			// Write the shared script only on first sequence
			if(id==1) {
				scriptOut.append("  function chainWriterUpdateTime(id, millis, serverValue) {\n"
							   + "    if(document.getElementById) {\n"
							   + "      var date=new Date(millis);\n"
							   + "      var hour=date.getHours();\n"
							   + "      var clientValue=(hour<10)?\"0\":\"\";\n"
							   + "      clientValue+=hour+\":\";\n"
							   + "      var minute=date.getMinutes();\n"
							   + "      if(minute<10) clientValue+=\"0\";\n"
							   + "      clientValue+=minute+\":\";\n"
							   + "      var second=date.getSeconds();\n"
							   + "      if(second<10) clientValue+=\"0\";\n"
							   + "      clientValue+=second;\n"
							   + "      if(clientValue!=serverValue) document.getElementById(\"chainWriterTime\"+id).firstChild.nodeValue=clientValue;\n"
							   + "    }\n"
							   + "  }\n");
			}
			scriptOut.append("  chainWriterUpdateTime(");
			scriptOut.append(idString);
			scriptOut.append(", ");
			scriptOut.append(Long.toString(date.getTime()));
			scriptOut.append(", \"");
			encodeJavaScriptInXhtml(timeString, scriptOut);
			scriptOut.append("\");\n");
		}
	}

	/**
	 * Writes a JavaScript script tag that a time in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code -1}.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @deprecated
	 * @see #writeTimeJavaScript(java.util.Date, com.aoindustries.util.Sequence, java.lang.Appendable)
	 */
	@Deprecated
	public ChainWriter printTimeJS(long date, Sequence sequence, Appendable scriptOut) throws IOException {
		return writeTimeJavaScript(date == -1 ? null : new Date(date), sequence, scriptOut);
	}

	/**
	 * Writes a JavaScript script tag that a time in the user's locale.
	 * Prints <code>&amp;#160;</code> if the date is {@code null}.
	 * Writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @see #writeTimeJavaScript(java.util.Date, com.aoindustries.util.Sequence, java.lang.Appendable, java.lang.Appendable)
	 */
	public ChainWriter writeTimeJavaScript(Date date, Sequence sequence, Appendable scriptOut) throws IOException {
		writeTimeJavaScript(date, sequence, out, scriptOut);
		return this;
	}
	// </editor-fold>
}
