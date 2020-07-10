// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau.http;

import static org.apache.juneau.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import static org.apache.juneau.http.StreamResource.*;

import java.io.*;

import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class StreamResource_Test {

	@Test
	public void a01_basic() throws Exception {
		StreamResource x = create();
		File f = File.createTempFile("test","txt");

		assertNull(x.getContentType());
		assertNull(x.getContent());
		assertNull(x.getContentEncoding());
		assertList(x.getHeaders()).isSize(0);

		of("foo").assertBytes().string().is("foo");
		of(new StringReader("foo")).assertBytes().string().is("foo");
		of("foo".getBytes()).assertBytes().string().is("foo");
		of(new ByteArrayInputStream("foo".getBytes())).assertBytes().string().is("foo");
		of(null).assertBytes().string().isEmpty();
		of(f).assertBytes().string().isEmpty();

		x = of("foo");
		assertStream(x.getContent()).string().is("foo");
		assertTrue(x.isRepeatable());
		assertFalse(x.isStreaming());

		x = of(new StringReader("foo"));
		assertStream(x.getContent()).string().is("foo");
		assertFalse(x.isRepeatable());
		assertTrue(x.isStreaming());

		x = of("foo".getBytes());
		assertStream(x.getContent()).string().is("foo");
		assertTrue(x.isRepeatable());
		assertFalse(x.isStreaming());

		x = of(new ByteArrayInputStream("foo".getBytes()));
		assertStream(x.getContent()).string().is("foo");
		assertFalse(x.isRepeatable());
		assertTrue(x.isStreaming());

		x = of(null);
		assertStream(x.getContent()).string().doesNotExist();
		assertFalse(x.isRepeatable());
		assertFalse(x.isStreaming());

		x = of(f);
		assertStream(x.getContent()).string().isEmpty();
		assertTrue(x.isRepeatable());
		assertFalse(x.isStreaming());

		x = of("foo").cache();
		assertStream(x.getContent()).string().is("foo");
		assertStream(x.getContent()).string().is("foo");
		assertTrue(x.isRepeatable());

		x = of(new StringReader("foo")).cache();
		assertStream(x.getContent()).string().is("foo");
		assertStream(x.getContent()).string().is("foo");
		assertTrue(x.isRepeatable());

		x = of("foo".getBytes()).cache();
		assertStream(x.getContent()).string().is("foo");
		assertStream(x.getContent()).string().is("foo");
		assertTrue(x.isRepeatable());

		x = of(new ByteArrayInputStream("foo".getBytes())).cache();
		assertStream(x.getContent()).string().is("foo");
		assertStream(x.getContent()).string().is("foo");
		assertTrue(x.isRepeatable());

		x = of(null).cache();
		assertStream(x.getContent()).string().doesNotExist();
		assertStream(x.getContent()).string().doesNotExist();
		assertTrue(x.isRepeatable());

		assertLong(of("foo").getContentLength()).is(3l);
		assertLong(of("foo".getBytes()).getContentLength()).is(3l);
		assertLong(of(f).getContentLength()).is(0l);

		x = of("foo").header("Foo","bar").header("Foo","baz");
		assertString(x.getStringHeader("Foo")).is("baz");
		assertString(x.getStringHeader("Bar")).doesNotExist();
		assertString(x.getFirstHeader("Foo").toString()).is("Foo: bar");
		assertString(x.getLastHeader("Foo").toString()).is("Foo: baz");
		assertObject(x.getFirstHeader("Bar")).doesNotExist();
		assertObject(x.getLastHeader("Bar")).doesNotExist();
		assertObject(x.getHeaders()).json().is("['Foo: bar','Foo: baz']");
	}
}
