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

import static org.apache.juneau.assertions.Assertions.*;
import static org.apache.juneau.rest.testutils.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.runners.MethodSorters.*;

import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.dto.swagger.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.http.annotation.Response;
import org.apache.juneau.jsonschema.annotation.Schema;
import org.junit.*;

@SuppressWarnings({"serial"})
@FixMethodOrder(NAME_ASCENDING)
public class Swagger_Response_Test {

	//-----------------------------------------------------------------------------------------------------------------
	// Swagger tests
	//-----------------------------------------------------------------------------------------------------------------

	@Rest
	public static class A {

		@Response(
			description={"a","b"},
			schema=@Schema(type="string"),
			headers=@ResponseHeader(name="foo",type="string"),
			example="'a'",
			examples=" {foo:'a'} "
		)
		public static class A1 {
			public A1(String x){}
		}
		@RestMethod
		public void a(Value<A1> r) {}
		@RestMethod
		public A1 b() {return null;}

		@Response(
			api={
				"description:'a\nb',",
				"schema:{type:'string'},",
				"headers:{foo:{type:'string'}},",
				"example:'a',",
				"examples:{foo:'a'}"
			}
		)
		public static class A2 {
			public A2(String x){}
		}
		@RestMethod
		public void c(Value<A2> r) {}
		@RestMethod
		public A2 d() {return null;}

		@Response(
			api={
				"description:'b',",
				"schema:{type:'number'},",
				"headers:{bar:{type:'number'}},",
				"example:'b',",
				"examples:{bar:'b'}"
			},
			description={"a","b"},
			schema=@Schema(type="string"),
			headers=@ResponseHeader(name="foo",type="string"),
			example="'a'",
			examples=" {foo:'a'} "
		)
		public static class A3 {
			public A3(String x){}
		}
		@RestMethod
		public void e(Value<A3> r) {}
		@RestMethod
		public A3 f() {return null;}

		@Response(code=100)
		public static class A4 {}
		@RestMethod
		public void g(Value<A4> r) {}
		@RestMethod
		public A4 h() {return null;}

		@Response(100)
		public static class A5 {}
		@RestMethod
		public void i(Value<A5> r) {}
		@RestMethod
		public A5 j() {return null;}

		@Response(headers=@ResponseHeader(name="foo",api=" type:'b' "))
		public static class A6 {}
		@RestMethod
		public void k(Value<A6> r) {}
		@RestMethod
		public A6 l() {return null;}
	}

	@Test
	public void a01_fromPojo() throws Exception {
		Swagger s = getSwagger(A.class);
		ResponseInfo x;

		x = s.getResponseInfo("/a","get",200);
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getSchema()).json().is("{type:'string'}");
		assertObject(x.getHeaders()).json().is("{foo:{type:'string'}}");
		assertEquals("'a'", x.getExample());
		assertObject(x.getExamples()).json().is("{foo:'a'}");

		x = s.getResponseInfo("/b","get",200);
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getSchema()).json().is("{type:'string'}");
		assertObject(x.getHeaders()).json().is("{foo:{type:'string'}}");
		assertEquals("'a'", x.getExample());
		assertObject(x.getExamples()).json().is("{foo:'a'}");

		x = s.getResponseInfo("/c","get",200);
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getSchema()).json().is("{type:'string'}");
		assertObject(x.getHeaders()).json().is("{foo:{type:'string'}}");
		assertEquals("a", x.getExample());
		assertObject(x.getExamples()).json().is("{foo:'a'}");

		x = s.getResponseInfo("/d","get",200);
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getSchema()).json().is("{type:'string'}");
		assertObject(x.getHeaders()).json().is("{foo:{type:'string'}}");
		assertEquals("a", x.getExample());
		assertObject(x.getExamples()).json().is("{foo:'a'}");

		x = s.getResponseInfo("/e","get",200);
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getSchema()).json().is("{type:'string'}");
		assertObject(x.getHeaders()).json().is("{bar:{type:'number'},foo:{type:'string'}}");
		assertEquals("'a'", x.getExample());
		assertObject(x.getExamples()).json().is("{foo:'a'}");

		x = s.getResponseInfo("/f","get",200);
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getSchema()).json().is("{type:'string'}");
		assertObject(x.getHeaders()).json().is("{bar:{type:'number'},foo:{type:'string'}}");
		assertEquals("'a'", x.getExample());
		assertObject(x.getExamples()).json().is("{foo:'a'}");

		x = s.getResponseInfo("/g","get",100);
		assertEquals("Continue", x.getDescription());

		x = s.getResponseInfo("/h","get",100);
		assertEquals("Continue", x.getDescription());

		x = s.getResponseInfo("/i","get",100);
		assertEquals("Continue", x.getDescription());

		x = s.getResponseInfo("/j","get",100);
		assertEquals("Continue", x.getDescription());

		x = s.getResponseInfo("/k","get",200);
		assertObject(x.getHeaders()).json().is("{foo:{type:'b'}}");

		x = s.getResponseInfo("/l","get",200);
		assertObject(x.getHeaders()).json().is("{foo:{type:'b'}}");
	}

	@Rest
	public static class B {

		@Response(schema=@Schema(" type:'number' "))
		public static class B1 {}
		@RestMethod
		public void a(Value<B1> r) {}
		@RestMethod
		public B1 b() {return null;}

		@Response
		public static class B2 {
			public String f1;
		}
		@RestMethod
		public void c(Value<B2> b) {}
		@RestMethod
		public B2 d() {return null;}

		@Response
		public static class B3 extends LinkedList<String> {
			private static final long serialVersionUID = 1L;
		}
		@RestMethod
		public void e(Value<B3> b) {}
		@RestMethod
		public B3 f() {return null;}

		@Response
		public static class B4 {}
		@RestMethod
		public void g(Value<B4> b) {}
		@RestMethod
		public B4 h() {return null;}
	}

	@Test
	public void b01_schemaFromPojo() throws Exception {
		Swagger s = getSwagger(B.class);
		ResponseInfo x;

		x = s.getResponseInfo("/a","get",200);
		assertObject(x.getSchema()).json().is("{type:'number'}");

		x = s.getResponseInfo("/b","get",200);
		assertObject(x.getSchema()).json().is("{type:'number'}");

		x = s.getResponseInfo("/c","get",200);
		assertObject(x.getSchema()).json().is("{type:'object',properties:{f1:{type:'string'}}}");

		x = s.getResponseInfo("/d","get",200);
		assertObject(x.getSchema()).json().is("{type:'object',properties:{f1:{type:'string'}}}");

		x = s.getResponseInfo("/e","get",200);
		assertObject(x.getSchema()).json().is("{type:'array',items:{type:'string'}}");

		x = s.getResponseInfo("/f","get",200);
		assertObject(x.getSchema()).json().is("{type:'array',items:{type:'string'}}");

		x = s.getResponseInfo("/g","get",200);
		assertObject(x.getSchema()).json().is("{type:'string'}");

		x = s.getResponseInfo("/h","get",200);
		assertObject(x.getSchema()).json().is("{type:'string'}");
	}

	@Rest
	public static class C {

		@Response(example="{f1:'a'}")
		public static class C1 {
			public String f1;
		}
		@RestMethod
		public void a(Value<C1> r) {}
		@RestMethod
		public C1 b() {return null;}

		@Response(examples={" foo:'b' "})
		public static class C2 {
			public C2(String x){}
		}
		@RestMethod
		public void c(Value<C2> r) {}
		@RestMethod
		public C2 d() {return null;}
	}

	@Test
	public void c01_exampleFromPojo() throws Exception {
		Swagger sc = getSwagger(C.class);
		ResponseInfo x;

		x = sc.getResponseInfo("/a","get",200);
		assertEquals("{f1:'a'}", x.getExample());

		x = sc.getResponseInfo("/b","get",200);
		assertEquals("{f1:'a'}", x.getExample());

		x = sc.getResponseInfo("/c","get",200);
		assertObject(x.getExamples()).json().is("{foo:'b'}");

		x = sc.getResponseInfo("/d","get",200);
		assertObject(x.getExamples()).json().is("{foo:'b'}");
	}

	@Rest
	public static class D {

		@Response(
			description= {"a","b"},
			schema=@Schema(type="string"),
			headers=@ResponseHeader(name="foo",type="string"),
			example=" 'a' ",
			examples=" {foo:'a'} "
		)
		public static class D1 extends Throwable {}
		@RestMethod
		public void a() throws D1 {}

		@Response(
			api={
				"description:'a\nb',",
				"schema:{type:'string'},",
				"headers:{foo:{type:'string'}},",
				"example:'a',",
				"examples:{foo:'a'}"
			}
		)
		public static class D2 extends Throwable {}
		@RestMethod
		public void b() throws D2 {}

		@Response(
			api={
				"description:'b',",
				"schema:{type:'number'},",
				"headers:{bar:{type:'number'}},",
				"example:'b',",
				"examples:{bar:'b'}"
			},
			description= {"a","b"},
			schema=@Schema(type="string"),
			headers=@ResponseHeader(name="foo",type="string"),
			example=" 'a' ",
			examples=" {foo:'a'} "
		)
		public static class D3 extends Throwable {}
		@RestMethod
		public void c() throws D3 {}

		@Response(code=100)
		public static class D4 extends Throwable {}
		@RestMethod
		public void d() throws D4 {}

		@Response(code=100)
		public static class D5 extends Throwable {}
		@RestMethod
		public void e() throws D5 {}

		@Response(headers=@ResponseHeader(name="foo", api=" {type:'number'} "))
		public static class D6 extends Throwable {}
		@RestMethod
		public void f() throws D6 {}
	}

	@Test
	public void d01_fromThrowable() throws Exception {
		Swagger s = getSwagger(D.class);
		ResponseInfo x;

		x = s.getResponseInfo("/a","get",500);
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getSchema()).json().is("{type:'string'}");
		assertObject(x.getHeaders()).json().is("{foo:{type:'string'}}");
		assertEquals("'a'", x.getExample());
		assertObject(x.getExamples()).json().is("{foo:'a'}");

		x = s.getResponseInfo("/b","get",500);
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getSchema()).json().is("{type:'string'}");
		assertObject(x.getHeaders()).json().is("{foo:{type:'string'}}");
		assertObject(x.getExample()).json().is("'a'");
		assertObject(x.getExamples()).json().is("{foo:'a'}");

		x = s.getResponseInfo("/c","get",500);
		assertEquals("a\nb", x.getDescription());
		assertObject(x.getSchema()).json().is("{type:'string'}");
		assertObject(x.getHeaders()).json().is("{bar:{type:'number'},foo:{type:'string'}}");
		assertEquals("'a'", x.getExample());
		assertObject(x.getExamples()).json().is("{foo:'a'}");

		x = s.getResponseInfo("/d","get",100);
		assertEquals("Continue", x.getDescription());

		x = s.getResponseInfo("/e","get",100);
		assertEquals("Continue", x.getDescription());

		x = s.getResponseInfo("/f","get",500);
		assertObject(x.getHeaders()).json().is("{foo:{type:'number'}}");
	}

	@Rest
	public static class E {

		@Response(schema=@Schema(" type:'number' "))
		public static class E1 extends Throwable {}
		@RestMethod
		public void a() throws E1 {}
	}

	@Test
	public void e01_schemaFromThrowable() throws Exception {
		Swagger s = getSwagger(E.class);

		ResponseInfo x = s.getResponseInfo("/a","get",500);
		assertObject(x.getSchema()).json().is("{type:'number'}");
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Examples
	//-----------------------------------------------------------------------------------------------------------------

	@Rest
	public static class F {

		@Response(example={" {f1:'b'} "})
		public static class F1 extends Throwable {}
		@RestMethod
		public void a() throws F1 {}

		@Response(examples={" foo:'b' "})
		public static class F2 extends Throwable {}
		@RestMethod
		public void b() throws F2 {}
	}

	@Test
	public void f01_exampeFromThrowable() throws Exception {
		Swagger s = getSwagger(F.class);
		ResponseInfo x;

		x = s.getResponseInfo("/a","get",500);
		assertEquals("{f1:'b'}", x.getExample());

		x = s.getResponseInfo("/b","get",500);
		assertObject(x.getExamples()).json().is("{foo:'b'}");
	}
}
