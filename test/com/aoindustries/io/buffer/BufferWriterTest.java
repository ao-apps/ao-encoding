/*
 * ao-encoding - High performance character encoding.
 * Copyright (C) 2013, 2015  AO Industries, Inc.
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
package com.aoindustries.io.buffer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import junit.framework.TestCase;

/**
 * @author  AO Industries, Inc.
 */
abstract public class BufferWriterTest extends TestCase {

    public BufferWriterTest(String testName) {
        super(testName);
    }

	public static interface BufferWriterFactory {
		String getName();
		BufferWriter newBufferWriter();
	}

	public void benchmarkSimulate(BufferWriterFactory factory) throws IOException {
		Writer out = new BufferedWriter(new FileWriter(new File("/dev/null")));
		try {
			final int loops = 1000;
			for(int i=1; i<=10; i++) {
				long startTime = System.nanoTime();
				for(int j=0; j<loops; j++) simulateCalls(factory, out);
				long endTime = System.nanoTime();
				System.out.println(factory.getName() + ": " + i + ": Simulated " + loops + " calls in " + BigDecimal.valueOf(endTime - startTime, 6)+" ms");
			}
		} finally {
			out.close();
		}
	}

	/**
	 * Performs the same set of calls that were performed in JSP request for:
	 *
	 * http://localhost:11156/essential-mining.com/purchase/domains.jsp?cartIndex=2&ui.lang=en&cookie%3AshoppingCart=jPAbu2Xc1JKVicbIGilVSW
	 */
	protected abstract void simulateCalls(BufferWriterFactory factory, Writer out) throws IOException;
}
