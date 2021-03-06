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
package org.apache.juneau.rest.annotation;

import static org.junit.runners.MethodSorters.*;

import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.http.annotation.Response;
import org.apache.juneau.json.*;
import org.apache.juneau.jsonschema.annotation.Schema;
import org.apache.juneau.oapi.*;
import org.apache.juneau.rest.client.*;
import org.apache.juneau.rest.mock.*;
import org.junit.*;

@SuppressWarnings({"serial"})
@FixMethodOrder(NAME_ASCENDING)
public class Response_Test {

	//-----------------------------------------------------------------------------------------------------------------
	// HTTP status code
	//-----------------------------------------------------------------------------------------------------------------

	@Rest
	public static class A {
		@RestMethod
		public A1 a() {
			return new A1();
		}
		@RestMethod
		public String b() throws A2 {
			throw new A2();
		}
	}

	@Response(code=201)
	public static class A1 {
		@Override
		public String toString() {return "foo";}
	}

	@Response(code=501)
	public static class A2 extends Exception {
		@Override
		public String toString() {return "foo";}
	}

	@Test
	public void a01_httpstatusCodes() throws Exception {
		RestClient a = MockRestClient.buildLax(A.class);
		a.get("/a")
			.run()
			.assertCode().is(201)
			.assertBody().is("foo");
		a.get("/b")
			.run()
			.assertCode().is(501);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// OpenApiSerializer
	//-----------------------------------------------------------------------------------------------------------------

	@Rest(serializers=OpenApiSerializer.class,defaultAccept="text/openapi")
	public static class B {
		@Response
		@RestMethod
		public String a() {
			return "foo";
		}
		@RestMethod
		public B1 b() {
			return new B1();
		}
		@RestMethod
		public String c() throws B2 {
			throw new B2();
		}
		@RestMethod
		public void d(@Response Value<String> value) {
			value.set("foo");
		}
	}

	@Response
	public static class B1 {
		@Override
		public String toString() {return "foo";}
	}

	@Response
	public static class B2 extends Exception {
		@Override
		public String toString() {return "foo";}
	}

	@Test
	public void b01_openApi() throws Exception {
		RestClient b = MockRestClient.buildLax(B.class);
		b.get("/a")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo");
		b.get("/b")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo");
		b.get("/c")
			.run()
			.assertCode().is(500)
			.assertBody().is("foo");
		b.get("/d")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo");
	}

	//-----------------------------------------------------------------------------------------------------------------
	// OpenAPI with schema
	//-----------------------------------------------------------------------------------------------------------------

	@Rest(serializers=OpenApiSerializer.class,defaultAccept="text/openapi")
	public static class D {
		@Response(schema=@Schema(collectionFormat="pipes"))
		@RestMethod
		public String[] a() {
			return new String[]{"foo","bar"};
		}
		@Response(schema=@Schema(type="string",format="byte"))
		@RestMethod
		public byte[] b() {
			return "foo".getBytes();
		}
		@RestMethod
		public D1 c() {
			return new D1();
		}
		@RestMethod
		public D2 d() {
			return new D2();
		}
		@RestMethod
		public String e() throws D3 {
			throw new D3();
		}
		@RestMethod
		public String f() throws D4 {
			throw new D4();
		}
		@RestMethod
		public void g(@Response(schema=@Schema(collectionFormat="pipes")) Value<String[]> value) {
			value.set(new String[]{"foo","bar"});
		}
		@RestMethod
		public void h(@Response(schema=@Schema(type="string",format="byte")) Value<byte[]> value) {
			value.set("foo".getBytes());
		}
	}

	@Response(schema=@Schema(type="array",collectionFormat="pipes"))
	public static class D1 {
		public String[] toStringArray() {
			return new String[]{"foo","bar"};
		}
	}

	@Response(schema=@Schema(format="byte"))
	public static class D2 {
		public byte[] toByteArray() {
			return "foo".getBytes();
		}
	}

	@Response(schema=@Schema(type="array",collectionFormat="pipes"))
	public static class D3 extends Exception {
		public String[] toStringArray() {
			return new String[]{"foo","bar"};
		}
	}

	@Response(schema=@Schema(format="byte"))
	public static class D4 extends Exception {
		public byte[] toByteArray() {
			return "foo".getBytes();
		}
	}

	@Test
	public void d01_openApi_withSchema() throws Exception {
		RestClient d = MockRestClient.buildLax(D.class);
		d.get("/a")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo|bar");
		d.get("/b")
			.run()
			.assertCode().is(200)
			.assertBody().is("Zm9v");
		d.get("/c")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo|bar");
		d.get("/d")
			.run()
			.assertCode().is(200)
			.assertBody().is("Zm9v");
		d.get("/e")
			.run()
			.assertCode().is(500)
			.assertBody().is("foo|bar");
		d.get("/f")
			.run()
			.assertCode().is(500)
			.assertBody().is("Zm9v");
		d.get("/g")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo|bar");
		d.get("/h")
			.run()
			.assertCode().is(200)
			.assertBody().is("Zm9v");
	}

	//-----------------------------------------------------------------------------------------------------------------
	// No serializers
	//-----------------------------------------------------------------------------------------------------------------

	@Rest
	public static class E {
		@RestMethod
		public void a(@Response Value<E1> body) {
			body.set(new E1());
		}
		@RestMethod
		public void b(Value<E2> body) {
			body.set(new E2());
		}
		@RestMethod
		@Response
		public E1 c() {
			return new E1();
		}
		@RestMethod
		public E2 d() {
			return new E2();
		}
	}

	public static class E1 {
		@Override
		public String toString() {return "foo";}
	}

	@Response
	public static class E2 {
		@Override
		public String toString() {return "foo";}
	}

	@Test
	public void e01_defaultSerialization() throws Exception {
		RestClient e = MockRestClient.build(E.class);
		e.get("/a")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo");
		e.get("/b")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo");
		e.get("/c")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo");
		e.get("/d")
			.run()
			.assertCode().is(200)
			.assertBody().is("foo");
	}

	//-----------------------------------------------------------------------------------------------------------------
	// JSON Accept
	//-----------------------------------------------------------------------------------------------------------------

	@Rest(serializers=SimpleJsonSerializer.class)
	public static class G {
		@RestMethod
		public void a(@Response Value<List<Integer>> body) {
			body.set(AList.of(1,2));
		}
		@RestMethod
		public void b(Value<G1> body) {
			body.set(new G1());
		}
		@RestMethod
		@Response
		public List<Integer> c() {
			return AList.of(1,2);
		}
		@RestMethod
		public G1 d() {
			return new G1();
		}
	}

	@Response
	public static class G1 extends ArrayList<Integer> {
		public G1() {
			add(1);
			add(2);
		}
	}

	@Test
	public void g01_json() throws Exception {
		RestClient g = MockRestClient.build(G.class);
		g.get("/a").json()
			.run()
			.assertCode().is(200)
			.assertBody().is("[1,2]");
		g.get("/b").json()
			.run()
			.assertCode().is(200)
			.assertBody().is("[1,2]");
		g.get("/c").json()
			.run()
			.assertCode().is(200)
			.assertBody().is("[1,2]");
		g.get("/d").json()
			.run()
			.assertCode().is(200)
			.assertBody().is("[1,2]");
	}
}
