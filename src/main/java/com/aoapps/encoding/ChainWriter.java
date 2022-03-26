/*
 * ao-encoding - High performance streaming character encoding.
 * Copyright (C) 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013, 2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import static com.aoapps.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import static com.aoapps.encoding.TextInJavaScriptEncoder.textInJavaScriptEncoder;
import static com.aoapps.encoding.TextInMysqlEncoder.textInMysqlEncoder;
import static com.aoapps.encoding.TextInPsqlEncoder.textInPsqlEncoder;
import static com.aoapps.encoding.TextInShEncoder.textInShEncoder;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import static com.aoapps.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.NullArgumentException;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

/**
 * A chain writer encapsulates a {@link PrintWriter} and returns the {@link ChainWriter}
 * instance on most methods.  This gives the ability to call code like
 * {@code out.print("Hi ").print(name).print('!');}
 *
 * @author  AO Industries, Inc.
 */
public final class ChainWriter implements Appendable, Closeable {

	// <editor-fold defaultstate="collapsed" desc="PrintWriter wrapping">
	private final EncodingContext encodingContext;
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
	public ChainWriter(EncodingContext encodingContext, OutputStream out) {
		this(encodingContext, new PrintWriter(out));
	}

	/**
	 * @see  EncodingContext#DEFAULT
	 *
	 * @deprecated  Please use {@link #ChainWriter(com.aoapps.encoding.EncodingContext, java.io.OutputStream)}
	 */
	@Deprecated
	public ChainWriter(OutputStream out) {
		this(EncodingContext.DEFAULT, out);
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
	public ChainWriter(EncodingContext encodingContext, OutputStream out, boolean autoFlush) {
		this(encodingContext, new PrintWriter(out, autoFlush));
	}

	/**
	 * @see  EncodingContext#DEFAULT
	 *
	 * @deprecated  Please use {@link #ChainWriter(com.aoapps.encoding.EncodingContext, java.io.OutputStream, boolean)}
	 */
	@Deprecated
	public ChainWriter(OutputStream out, boolean autoFlush) {
		this(EncodingContext.DEFAULT, out, autoFlush);
	}

	@SuppressWarnings("deprecation")
	public ChainWriter(EncodingContext encodingContext, PrintWriter out) {
		this.encodingContext = NullArgumentException.checkNotNull(encodingContext, "encodingContext");
		this.out = out;
		javaScriptInXhtmlAttributeWriter = new MediaWriter(encodingContext, javaScriptInXhtmlAttributeEncoder, out);
		try {
			javaScriptInXhtmlWriter = new MediaWriter(
				encodingContext,
				MediaEncoder.getInstance(encodingContext, MediaType.JAVASCRIPT, MediaType.XHTML),
				out
			);
		} catch(UnsupportedEncodingException e) {
			throw new AssertionError("JAVASCRIPT in XHTML is implemented", e);
		}
	}

	/**
	 * @see  EncodingContext#DEFAULT
	 *
	 * @deprecated  Please use {@link #ChainWriter(com.aoapps.encoding.EncodingContext, java.io.PrintWriter)}
	 */
	@Deprecated
	public ChainWriter(PrintWriter out) {
		this(EncodingContext.DEFAULT, out);
	}

	/**
	 * Create a new PrintWriter, if needed, without automatic line flushing.
	 * <p>
	 * When {@code out} is already a {@link PrintWriter}, it is used directly,
	 * regardless of {@code autoFlush} settings.
	 * </p>
	 *
	 * @param  out        A character-output stream
	 */
	public ChainWriter(EncodingContext encodingContext, Writer out) {
		// If out is a PrintWriter, cast instead of wrapping again
		this(
			encodingContext,
			(out instanceof PrintWriter)
			? (PrintWriter)out
			: new PrintWriter(out)
		);
	}

	/**
	 * @see  EncodingContext#DEFAULT
	 *
	 * @deprecated  Please use {@link #ChainWriter(com.aoapps.encoding.EncodingContext, java.io.Writer)}
	 */
	@Deprecated
	public ChainWriter(Writer out) {
		this(EncodingContext.DEFAULT, out);
	}

	/**
	 * Create a new PrintWriter, if needed.
	 * <p>
	 * When {@code out} is already a {@link PrintWriter}, it is used directly,
	 * regardless of {@code autoFlush} settings.
	 * </p>
	 *
	 * @param  out        A character-output stream
	 * @param  autoFlush  A boolean; if true, the println() methods will flush
	 *                    the output buffer
	 */
	public ChainWriter(EncodingContext encodingContext, Writer out, boolean autoFlush) {
		this(
			encodingContext,
			(out instanceof PrintWriter)
			? (PrintWriter)out
			: new PrintWriter(out, autoFlush)
		);
	}

	/**
	 * @see  EncodingContext#DEFAULT
	 *
	 * @deprecated  Please use {@link #ChainWriter(com.aoapps.encoding.EncodingContext, java.io.Writer, boolean)}
	 */
	@Deprecated
	public ChainWriter(Writer out, boolean autoFlush) {
		this(EncodingContext.DEFAULT, out, autoFlush);
	}

	public EncodingContext getEncodingContext() {
		return encodingContext;
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
	public ChainWriter write(char[] buf, int off, int len) {
		out.write(buf, off, len);
		return this;
	}

	/**
	 * Write an array of characters.  This method cannot be inherited from the
	 * Writer class because it must suppress I/O exceptions.
	 */
	public ChainWriter write(char[] buf) {
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
	public ChainWriter print(char[] s) {
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
	public ChainWriter println(char[] x) {
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
	 * @param  value  the value to be encoded
	 *
	 * @see  TextInXhtmlAttributeEncoder
	 * @see  Coercion#write(java.lang.Object, com.aoapps.lang.io.Encoder, java.io.Writer)
	 */
	public ChainWriter textInXmlAttribute(Object value) throws IOException {
		Coercion.write(value, textInXhtmlAttributeEncoder, out);
		return this;
	}

	/**
	 * @param  value  the value to be encoded
	 *
	 * @see  TextInXhtmlEncoder
	 * @see  MarkupCoercion#write(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.lang.io.Encoder, boolean, java.io.Writer)
	 */
	public ChainWriter textInXhtml(Object value) throws IOException {
		MarkupCoercion.write(value, MarkupType.XHTML, false, textInXhtmlEncoder, false, out);
		return this;
	}

	/**
	 * Escapes HTML for displaying in browsers and writes to the internal <code>{@link PrintWriter}</code>.
	 * Has makeBr and makeNbsp enabled.
	 *
	 * @param value the string to be escaped.
	 *
	 * @see  #textInXhtml(java.lang.Object)
	 *
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 */
	@Deprecated
	public ChainWriter encodeHtml(Object value, boolean isXhtml) throws IOException {
		com.aoapps.hodgepodge.util.EncodingUtils.encodeHtml(value, true, true, out, isXhtml);
		return this;
	}

	/**
	 * Escapes HTML for displaying in browsers and writes to the internal <code>{@link PrintWriter}</code>.
	 *
	 * @param value the string to be escaped.
	 * @param makeBr  will write &lt;br /&gt; tags for every newline character
	 * @param makeNbsp  will write &amp;#160; for a space when another space follows
	 *
	 * @see  #textInXhtml(java.lang.Object)
	 *
	 * @deprecated  the effects of makeBr and makeNbsp should be handled by CSS white-space property.
	 */
	@Deprecated
	public ChainWriter encodeHtml(Object value, boolean makeBr, boolean makeNbsp, boolean isXhtml) throws IOException {
		com.aoapps.hodgepodge.util.EncodingUtils.encodeHtml(value, makeBr, makeNbsp, out, isXhtml);
		return this;
	}

	/**
	 * Encodes a javascript string.
	 * Quotes ({@code "…"}) are added around the string.
	 * Also, if the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 *
	 * @param  value  the value to be encoded
	 *
	 * @see  TextInJavaScriptEncoder
	 * @see  MarkupCoercion#write(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.lang.io.Encoder, boolean, java.io.Writer)
	 */
	public ChainWriter textInJavaScript(Object value) throws IOException {
		MarkupCoercion.write(value, MarkupType.JAVASCRIPT, false, textInJavaScriptEncoder, true, out);
		return this;
	}

	/**
	 * Encodes a javascript string for use in an XML attribute context.
	 * Quotes ({@code "…"}) are added around the string.
	 * Also, if the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 *
	 * @param  value  the value to be encoded
	 *
	 * @see  TextInJavaScriptEncoder
	 * @see  JavaScriptInXhtmlAttributeEncoder
	 * @see  MarkupCoercion#write(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.lang.io.Encoder, boolean, java.io.Writer)
	 */
	public ChainWriter textInJavaScriptInXmlAttribute(Object value) throws IOException {
		// Two stage encoding:
		//   1) Text -> JavaScript (with quotes added)
		//   2) JavaScript -> XML Attribute
		MarkupCoercion.write(value, MarkupType.JAVASCRIPT, false, textInJavaScriptEncoder, true, javaScriptInXhtmlAttributeWriter);
		return this;
	}

	/**
	 * Encodes a javascript string for use in an XML body CDATA context.
	 * Quotes ({@code "…"}) are added around the string.
	 * Also, if the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 *
	 * @param  value  the value to be encoded
	 *
	 * @see  TextInJavaScriptEncoder
	 * @see  JavaScriptInXhtmlEncoder
	 * @see  MarkupCoercion#write(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.lang.io.Encoder, boolean, java.io.Writer)
	 */
	public ChainWriter textInJavaScriptInXhtml(Object value) throws IOException {
		// Two stage encoding:
		//   1) Text -> JavaScript (with quotes added)
		//   2) JavaScript -> XHTML
		MarkupCoercion.write(value, MarkupType.JAVASCRIPT, false, textInJavaScriptEncoder, true, javaScriptInXhtmlWriter);
		return this;
	}

	/**
	 * Encodes a MySQL string.
	 * {@code E'…'} quotes are added around the string.
	 * Also, if the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 *
	 * @param  value  the value to be encoded
	 *
	 * @see  TextInMysqlEncoder
	 * @see  MarkupCoercion#write(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.lang.io.Encoder, boolean, java.io.Writer)
	 */
	public ChainWriter textInMysql(Object value) throws IOException {
		MarkupCoercion.write(value, MarkupType.MYSQL, false, textInMysqlEncoder, true, out);
		return this;
	}

	/**
	 * Encodes a psql string.
	 * {@code E'…'} quotes are added around the string.
	 * Also, if the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 *
	 * @param  value  the value to be encoded
	 *
	 * @see  TextInPsqlEncoder
	 * @see  MarkupCoercion#write(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.lang.io.Encoder, boolean, java.io.Writer)
	 */
	public ChainWriter textInPsql(Object value) throws IOException {
		MarkupCoercion.write(value, MarkupType.PSQL, false, textInPsqlEncoder, true, out);
		return this;
	}

	/**
	 * Encodes a sh string.
	 * {@code $'…'} quotes are added around the string.
	 * Also, if the string is translated, comments will be added giving the
	 * translation lookup id to aid in translation of server-translated values.
	 *
	 * @param  value  the value to be encoded
	 *
	 * @see  TextInShEncoder
	 * @see  MarkupCoercion#write(java.lang.Object, com.aoapps.util.i18n.MarkupType, boolean, com.aoapps.lang.io.Encoder, boolean, java.io.Writer)
	 */
	public ChainWriter textInSh(Object value) throws IOException {
		MarkupCoercion.write(value, MarkupType.SH, false, textInShEncoder, true, out);
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
	 * Prints a color in HTML format #xxxxxx, where xxxxxx is the hex code.
	 */
	public ChainWriter writeHtmlColor(int color) throws IOException {
		writeHtmlColor(color, out);
		return this;
	}
	// </editor-fold>
}
