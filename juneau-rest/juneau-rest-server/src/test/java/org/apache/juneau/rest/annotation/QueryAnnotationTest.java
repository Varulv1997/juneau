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

import static org.apache.juneau.http.HttpMethodName.*;

import java.util.*;

import org.apache.juneau.httppart.*;
import org.apache.juneau.json.*;
import org.apache.juneau.rest.*;
import org.apache.juneau.rest.mock.*;
import org.junit.*;
import org.junit.runners.*;

/**
 * Tests related to @Query annotation.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("javadoc")
public class QueryAnnotationTest {
	
	//====================================================================================================
	// Simple tests
	//====================================================================================================
	@RestResource
	public static class A {
		@RestMethod(name=GET)
		public String get(RestRequest req, @Query("p1") String p1, @Query("p2") int p2) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"],p2=["+p2+","+q.getString("p2")+","+q.get("p2", int.class)+"]";
		}
		@RestMethod(name=POST)
		public String post(RestRequest req, @Query("p1") String p1, @Query("p2") int p2) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"],p2=["+p2+","+q.getString("p2")+","+q.get("p2", int.class)+"]";
		}
	}
	static MockRest a = MockRest.create(A.class);
	
	@Test
	public void a01_get() throws Exception {
		a.request("GET", "?p1=p1&p2=2").execute().assertBody("p1=[p1,p1,p1],p2=[2,2,2]");
		a.request("GET", "?p1&p2").execute().assertBody("p1=[null,null,null],p2=[0,null,0]");
		a.request("GET", "?p1=&p2=").execute().assertBody("p1=[,,],p2=[0,,0]");
		a.request("GET", "/").execute().assertBody("p1=[null,null,null],p2=[0,null,0]");
		a.request("GET", "?p1").execute().assertBody("p1=[null,null,null],p2=[0,null,0]");
		a.request("GET", "?p1=").execute().assertBody("p1=[,,],p2=[0,null,0]");
		a.request("GET", "?p2").execute().assertBody("p1=[null,null,null],p2=[0,null,0]");
		a.request("GET", "?p2=").execute().assertBody("p1=[null,null,null],p2=[0,,0]");
		a.request("GET", "?p1=foo&p2").execute().assertBody("p1=[foo,foo,foo],p2=[0,null,0]");
		a.request("GET", "?p1&p2=1").execute().assertBody("p1=[null,null,null],p2=[1,1,1]");
		String x = "a%2Fb%25c%3Dd+e"; // [x/y%z=a+b]
		a.request("GET", "?p1="+x+"&p2=1").execute().assertBody("p1=[a/b%c=d e,a/b%c=d e,a/b%c=d e],p2=[1,1,1]");
	}
	@Test
	public void a02_post() throws Exception {
		a.request("POST", "?p1=p1&p2=2").execute().assertBody("p1=[p1,p1,p1],p2=[2,2,2]");
		a.request("POST", "?p1&p2").execute().assertBody("p1=[null,null,null],p2=[0,null,0]");
		a.request("POST", "?p1=&p2=").execute().assertBody("p1=[,,],p2=[0,,0]");
		a.request("POST", "/").execute().assertBody("p1=[null,null,null],p2=[0,null,0]");
		a.request("POST", "?p1").execute().assertBody("p1=[null,null,null],p2=[0,null,0]");
		a.request("POST", "?p1=").execute().assertBody("p1=[,,],p2=[0,null,0]");
		a.request("POST", "?p2").execute().assertBody("p1=[null,null,null],p2=[0,null,0]");
		a.request("POST", "?p2=").execute().assertBody("p1=[null,null,null],p2=[0,,0]");
		a.request("POST", "?p1=foo&p2").execute().assertBody("p1=[foo,foo,foo],p2=[0,null,0]");
		a.request("POST", "?p1&p2=1").execute().assertBody("p1=[null,null,null],p2=[1,1,1]");
		String x = "a%2Fb%25c%3Dd+e"; // [x/y%z=a+b]
		a.request("POST", "?p1="+x+"&p2=1").execute().assertBody("p1=[a/b%c=d e,a/b%c=d e,a/b%c=d e],p2=[1,1,1]");
	}
	
	//====================================================================================================
	// Plain parameters
	//====================================================================================================
	@RestResource
	public static class B {
		@RestMethod(name=GET)
		public String get(RestRequest req, @Query(value="p1",parser=SimplePartParser.class) String p1) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"]";
		}
		@RestMethod(name=POST)
		public String post(RestRequest req, @Query(value="p1",parser=SimplePartParser.class) String p1) throws Exception {
			RequestQuery q = req.getQuery();
			return "p1=["+p1+","+req.getQuery().getString("p1")+","+q.get("p1", String.class)+"]";
		}
	}
	static MockRest b = MockRest.create(B.class);
	
	@Test
	public void b01_get() throws Exception {
		b.request("GET", "?p1=p1").execute().assertBody("p1=[p1,p1,p1]");
		b.request("GET", "?p1='p1'").execute().assertBody("p1=['p1','p1',p1]");
	}
	@Test
	public void b02_post() throws Exception {
		b.request("POST", "?p1=p1").execute().assertBody("p1=[p1,p1,p1]");
		b.request("POST", "?p1='p1'").execute().assertBody("p1=['p1','p1',p1]");
	}
	
	
	//====================================================================================================
	// Multipart parameters (e.g. &key=val1,&key=val2).
	//====================================================================================================
	@RestResource(serializers=JsonSerializer.Simple.class)
	public static class C {
		public static class C01 {
			public String a;
			public int b;
			public boolean c;
		}
		
		@RestMethod(name=GET, path="/StringArray")
		public Object c01(@Query(value="x",multipart=true) String[] x) {
			return x;
		}
		@RestMethod(name=GET, path="/intArray")
		public Object c02(@Query(value="x",multipart=true) int[] x) {
			return x;
		}
		@RestMethod(name=GET, path="/ListOfStrings")
		public Object c03(@Query(value="x",multipart=true) List<String> x) {
			return x;
		}
		@RestMethod(name=GET, path="/ListOfIntegers")
		public Object c04(@Query(value="x",multipart=true) List<Integer> x) {
			return x;
		}
		@RestMethod(name=GET, path="/BeanArray")
		public Object c05(@Query(value="x",multipart=true) C01[] x) {
			return x;
		}
		@RestMethod(name=GET, path="/ListOfBeans")
		public Object c06(@Query(value="x",multipart=true) List<C01> x) {
			return x;
		}
	}
	static MockRest c = MockRest.create(C.class);

	@Test
	public void c01_StringArray() throws Exception {
		c.request("GET", "/StringArray?x=a").execute().assertBody("['a']");
		c.request("GET", "/StringArray?x=a&x=b").execute().assertBody("['a','b']");
	}
	@Test
	public void c02_intArray() throws Exception {
		c.request("GET", "/intArray?x=1").execute().assertBody("[1]");
		c.request("GET", "/intArray?x=1&x=2").execute().assertBody("[1,2]");
	}
	@Test
	public void c03_ListOfStrings() throws Exception {
		c.request("GET", "/ListOfStrings?x=a").execute().assertBody("['a']");
		c.request("GET", "/ListOfStrings?x=a&x=b").execute().assertBody("['a','b']");
	}
	@Test
	public void c04_ListOfIntegers() throws Exception {
		c.request("GET", "/ListOfIntegers?x=1").execute().assertBody("[1]");
		c.request("GET", "/ListOfIntegers?x=1&x=2").execute().assertBody("[1,2]");
	}
	@Test
	public void c05_BeanArray() throws Exception {
		c.request("GET", "/BeanArray?x=(a=1,b=2,c=false)").execute().assertBody("[{a:'1',b:2,c:false}]");
		c.request("GET", "/BeanArray?x=(a=1,b=2,c=false)&x=(a=3,b=4,c=true)").execute().assertBody("[{a:'1',b:2,c:false},{a:'3',b:4,c:true}]");
	}
	@Test
	public void c06_ListOfBeans() throws Exception {
		c.request("GET", "/ListOfBeans?x=(a=1,b=2,c=false)").execute().assertBody("[{a:'1',b:2,c:false}]");
		c.request("GET", "/ListOfBeans?x=(a=1,b=2,c=false)&x=(a=3,b=4,c=true)").execute().assertBody("[{a:'1',b:2,c:false},{a:'3',b:4,c:true}]");
	}
}
