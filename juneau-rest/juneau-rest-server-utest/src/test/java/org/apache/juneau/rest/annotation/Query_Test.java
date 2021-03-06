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

import static org.apache.juneau.http.HttpMethod.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import java.util.*;

import org.apache.juneau.collections.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.json.*;
import org.apache.juneau.jsonschema.annotation.Items;
import org.apache.juneau.rest.*;
import org.apache.juneau.rest.RestRequest;
import org.apache.juneau.rest.client.*;
import org.apache.juneau.rest.mock.*;
import org.apache.juneau.testutils.pojos.*;
import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class Query_Test {

	//------------------------------------------------------------------------------------------------------------------
	// Simple tests
	//------------------------------------------------------------------------------------------------------------------

	@Rest
	public static class A {
		@RestMethod
		public String a(RestRequest req, @Query(n="p1",aev=true) String p1, @Query(n="p2",aev=true) int p2) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"],p2=["+p2+","+q.getString("p2")+","+q.get("p2", int.class)+"]";
		}
		@RestMethod(method=POST)
		public String b(RestRequest req, @Query(n="p1",aev=true) String p1, @Query(n="p2",aev=true) int p2) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"],p2=["+p2+","+q.getString("p2")+","+q.get("p2", int.class)+"]";
		}
	}

	@Test
	public void a01_basic() throws Exception {
		RestClient a = MockRestClient.build(A.class);

		a.get("/a?p1=p1&p2=2").run().assertBody().is("p1=[p1,p1,p1],p2=[2,2,2]");
		a.get("/a?p1&p2").run().assertBody().is("p1=[null,null,null],p2=[0,null,0]");
		a.get("/a?p1=&p2=").run().assertBody().is("p1=[,,],p2=[0,,0]");
		a.get("/a").run().assertBody().is("p1=[null,null,null],p2=[0,null,0]");
		a.get("/a?p1").run().assertBody().is("p1=[null,null,null],p2=[0,null,0]");
		a.get("/a?p1=").run().assertBody().is("p1=[,,],p2=[0,null,0]");
		a.get("/a?p2").run().assertBody().is("p1=[null,null,null],p2=[0,null,0]");
		a.get("/a?p2=").run().assertBody().is("p1=[null,null,null],p2=[0,,0]");
		a.get("/a?p1=foo&p2").run().assertBody().is("p1=[foo,foo,foo],p2=[0,null,0]");
		a.get("/a?p1&p2=1").run().assertBody().is("p1=[null,null,null],p2=[1,1,1]");
		String x1 = "a%2Fb%25c%3Dd+e"; // [x/y%z=a+b]
		a.get("/a?p1="+x1+"&p2=1").run().assertBody().is("p1=[a/b%c=d e,a/b%c=d e,a/b%c=d e],p2=[1,1,1]");

		a.post("/b?p1=p1&p2=2", null).run().assertBody().is("p1=[p1,p1,p1],p2=[2,2,2]");
		a.post("/b?p1&p2", null).run().assertBody().is("p1=[null,null,null],p2=[0,null,0]");
		a.post("/b?p1=&p2=", null).run().assertBody().is("p1=[,,],p2=[0,,0]");
		a.post("/b", null).run().assertBody().is("p1=[null,null,null],p2=[0,null,0]");
		a.post("/b?p1", null).run().assertBody().is("p1=[null,null,null],p2=[0,null,0]");
		a.post("/b?p1=", null).run().assertBody().is("p1=[,,],p2=[0,null,0]");
		a.post("/b?p2", null).run().assertBody().is("p1=[null,null,null],p2=[0,null,0]");
		a.post("/b?p2=", null).run().assertBody().is("p1=[null,null,null],p2=[0,,0]");
		a.post("/b?p1=foo&p2", null).run().assertBody().is("p1=[foo,foo,foo],p2=[0,null,0]");
		a.post("/b?p1&p2=1", null).run().assertBody().is("p1=[null,null,null],p2=[1,1,1]");
		String x2 = "a%2Fb%25c%3Dd+e"; // [x/y%z=a+b]
		a.post("/b?p1="+x2+"&p2=1", null).run().assertBody().is("p1=[a/b%c=d e,a/b%c=d e,a/b%c=d e],p2=[1,1,1]");
	}

	//------------------------------------------------------------------------------------------------------------------
	// UON parameters
	//------------------------------------------------------------------------------------------------------------------

	@Rest
	public static class B {
		@RestMethod
		public String a(RestRequest req, @Query(n="p1") String p1) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"]";
		}
		@RestMethod
		public String b(RestRequest req, @Query(n="p1",f="uon") String p1) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"]";
		}
		@RestMethod(method=POST)
		public String c(RestRequest req, @Query(n="p1") String p1) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"]";
		}
		@RestMethod(method=POST)
		public String d(RestRequest req, @Query(n="p1",f="uon") String p1) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"]";
		}
	}

	@Test
	public void b01_uonParameters() throws Exception {
		RestClient b = MockRestClient.build(B.class);
		b.get("/a?p1=p1").run().assertBody().is("p1=[p1,p1,p1]");
		b.get("/a?p1='p1'").run().assertBody().is("p1=['p1','p1','p1']");
		b.get("/b?p1=p1").run().assertBody().is("p1=[p1,p1,p1]");
		b.get("/b?p1='p1'").run().assertBody().is("p1=[p1,'p1','p1']");
		b.post("/c?p1=p1", null).run().assertBody().is("p1=[p1,p1,p1]");
		b.post("/c?p1='p1'", null).run().assertBody().is("p1=['p1','p1','p1']");
		b.post("/d?p1=p1", null).run().assertBody().is("p1=[p1,p1,p1]");
		b.post("/d?p1='p1'", null).run().assertBody().is("p1=[p1,'p1','p1']");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Multipart parameters (e.g. &key=val1,&key=val2).
	//------------------------------------------------------------------------------------------------------------------

	@Rest(serializers=SimpleJsonSerializer.class)
	public static class C {
		public static class C1 {
			public String a;
			public int b;
			public boolean c;
		}

		@RestMethod
		public Object a(@Query(n="x",cf="multi") String[] x) {
			return x;
		}
		@RestMethod
		public Object b(@Query(n="x",cf="multi") int[] x) {
			return x;
		}
		@RestMethod
		public Object c(@Query(n="x",cf="multi") List<String> x) {
			return x;
		}
		@RestMethod
		public Object d(@Query(n="x",cf="multi") List<Integer> x) {
			return x;
		}
		@RestMethod
		public Object e(@Query(n="x",cf="multi",items=@Items(f="uon")) C1[] x) {
			return x;
		}
		@RestMethod
		public Object f(@Query(n="x",cf="multi",items=@Items(f="uon")) List<C1> x) {
			return x;
		}
	}

	@Test
	public void c01_multipartParams() throws Exception {
		RestClient c = MockRestClient.build(C.class);
		c.get("/a?x=a").run().assertBody().is("['a']");
		c.get("/a?x=a&x=b").run().assertBody().is("['a','b']");
		c.get("/b?x=1").run().assertBody().is("[1]");
		c.get("/b?x=1&x=2").run().assertBody().is("[1,2]");
		c.get("/c?x=a").run().assertBody().is("['a']");
		c.get("/c?x=a&x=b").run().assertBody().is("['a','b']");
		c.get("/d?x=1").run().assertBody().is("[1]");
		c.get("/d?x=1&x=2").run().assertBody().is("[1,2]");
		c.get("/e?x=a=1,b=2,c=false").run().assertBody().is("[{a:'1',b:2,c:false}]");
		c.get("/e?x=a=1,b=2,c=false&x=a=3,b=4,c=true").run().assertBody().is("[{a:'1',b:2,c:false},{a:'3',b:4,c:true}]");
		c.get("/f?x=a=1,b=2,c=false").run().assertBody().is("[{a:'1',b:2,c:false}]");
		c.get("/f?x=a=1,b=2,c=false&x=a=3,b=4,c=true").run().assertBody().is("[{a:'1',b:2,c:false},{a:'3',b:4,c:true}]");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Default values.
	//------------------------------------------------------------------------------------------------------------------

	@Rest
	public static class D {
		@RestMethod(defaultQuery={"f1:1","f2=2"," f3 : 3 "})
		public OMap a(RequestQuery query) {
			return OMap.of()
				.a("f1", query.getString("f1"))
				.a("f2", query.getString("f2"))
				.a("f3", query.getString("f3"));
		}
		@RestMethod
		public OMap b(@Query("f1") String f1, @Query("f2") String f2, @Query("f3") String f3) {
			return OMap.of()
				.a("f1", f1)
				.a("f2", f2)
				.a("f3", f3);
		}
		@RestMethod
		public OMap c(@Query(n="f1",df="1") String f1, @Query(n="f2",df="2") String f2, @Query(n="f3",df="3") String f3) {
			return OMap.of()
				.a("f1", f1)
				.a("f2", f2)
				.a("f3", f3);
		}
		@RestMethod(defaultQuery={"f1:1","f2=2"," f3 : 3 "})
		public OMap d(@Query(n="f1",df="4") String f1, @Query(n="f2",df="5") String f2, @Query(n="f3",df="6") String f3) {
			return OMap.of()
				.a("f1", f1)
				.a("f2", f2)
				.a("f3", f3);
		}
	}

	@Test
	public void d01_defaultValues() throws Exception {
		RestClient d = MockRestClient.build(D.class);
		d.get("/a").run().assertBody().is("{f1:'1',f2:'2',f3:'3'}");
		d.get("/a").query("f1",4).query("f2",5).query("f3",6).run().assertBody().is("{f1:'4',f2:'5',f3:'6'}");
		d.get("/b").run().assertBody().is("{f1:null,f2:null,f3:null}");
		d.get("/b").query("f1",4).query("f2",5).query("f3",6).run().assertBody().is("{f1:'4',f2:'5',f3:'6'}");
		d.get("/c").run().assertBody().is("{f1:'1',f2:'2',f3:'3'}");
		d.get("/c").query("f1",4).query("f2",5).query("f3",6).run().assertBody().is("{f1:'4',f2:'5',f3:'6'}");
		d.get("/d").run().assertBody().is("{f1:'4',f2:'5',f3:'6'}");
		d.get("/d").query("f1",7).query("f2",8).query("f3",9).run().assertBody().is("{f1:'7',f2:'8',f3:'9'}");
	}

	//------------------------------------------------------------------------------------------------------------------
	// Optional query parameter.
	//------------------------------------------------------------------------------------------------------------------

	@Rest(serializers=SimpleJsonSerializer.class)
	public static class E {
		@RestMethod
		public Object a(@Query("f1") Optional<Integer> f1) throws Exception {
			assertNotNull(f1);
			return f1;
		}
		@RestMethod
		public Object b(@Query("f1") Optional<ABean> f1) throws Exception {
			assertNotNull(f1);
			return f1;
		}
		@RestMethod
		public Object c(@Query("f1") Optional<List<ABean>> f1) throws Exception {
			assertNotNull(f1);
			return f1;
		}
		@RestMethod
		public Object d(@Query("f1") List<Optional<ABean>> f1) throws Exception {
			return f1;
		}
	}

	@Test
	public void e01_optionalParams() throws Exception {
		RestClient e = MockRestClient.buildJson(E.class);
		e.get("/a?f1=123")
			.run()
			.assertCode().is(200)
			.assertBody().is("123");
		e.get("/a")
			.run()
			.assertCode().is(200)
			.assertBody().is("null");
		e.get("/b?f1=a=1,b=foo")
			.run()
			.assertCode().is(200)
			.assertBody().is("{a:1,b:'foo'}");
		e.get("/b")
			.run()
			.assertCode().is(200)
			.assertBody().is("null");
		e.get("/c?f1=@((a=1,b=foo))")
			.run()
			.assertCode().is(200)
			.assertBody().is("[{a:1,b:'foo'}]");
		e.get("/c")
			.run()
			.assertCode().is(200)
			.assertBody().is("null");
		e.get("/d?f1=@((a=1,b=foo))")
			.run()
			.assertCode().is(200)
			.assertBody().is("[{a:1,b:'foo'}]");
		e.get("/d")
			.run()
			.assertCode().is(200)
			.assertBody().is("null");
	}
}
