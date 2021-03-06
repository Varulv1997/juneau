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
import static org.junit.runners.MethodSorters.*;

import org.apache.juneau.dto.swagger.*;
import org.apache.juneau.http.header.*;
import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class Swagger_RestMethod_Parameters {

	//------------------------------------------------------------------------------------------------------------------
	// Swagger on default headers.
	//------------------------------------------------------------------------------------------------------------------

	@Rest
	public static class A {

		@RestMethod
		public String accept(Accept accept) {
			return accept.getValue();
		}
		@RestMethod
		public String acceptCharset(AcceptCharset acceptCharset) {
			return acceptCharset.getValue();
		}
		@RestMethod
		public String acceptEncoding(AcceptEncoding acceptEncoding) {
			return acceptEncoding.getValue();
		}
		@RestMethod
		public String acceptLanguage(AcceptLanguage acceptLanguage) {
			return acceptLanguage.getValue();
		}
		@RestMethod
		public String authorization(Authorization authorization) {
			return authorization.getValue();
		}
		@RestMethod
		public String cacheControl(CacheControl cacheControl) {
			return cacheControl.getValue();
		}
		@RestMethod
		public String connection(Connection connection) {
			return connection.getValue();
		}
		@RestMethod
		public String contentLength(ContentLength contentLength) {
			return contentLength.getValue();
		}
		@RestMethod
		public String contentType(ContentType contentType) {
			return contentType.getValue();
		}
		@RestMethod
		public String date(org.apache.juneau.http.header.Date date) {
			return date.getValue();
		}
		@RestMethod
		public String expect(Expect expect) {
			return expect.getValue();
		}
		@RestMethod
		public String from(From from) {
			return from.getValue();
		}
		@RestMethod
		public String host(Host host) {
			return host.getValue();
		}
		@RestMethod
		public String ifMatch(IfMatch ifMatch) {
			return ifMatch.getValue();
		}
		@RestMethod
		public String ifModifiedSince(IfModifiedSince ifModifiedSince) {
			return ifModifiedSince.getValue();
		}
		@RestMethod
		public String ifNoneMatch(IfNoneMatch ifNoneMatch) {
			return ifNoneMatch.getValue();
		}
		@RestMethod
		public String ifRange(IfRange ifRange) {
			return ifRange.getValue();
		}
		@RestMethod
		public String ifUnmodifiedSince(IfUnmodifiedSince ifUnmodifiedSince) {
			return ifUnmodifiedSince.getValue();
		}
		@RestMethod
		public String maxForwards(MaxForwards maxForwards) {
			return maxForwards.getValue();
		}
		@RestMethod
		public String pragma(Pragma pragma) {
			return pragma.getValue();
		}
		@RestMethod
		public String proxyAuthorization(ProxyAuthorization proxyAuthorization) {
			return proxyAuthorization.getValue();
		}
		@RestMethod
		public String range(Range range) {
			return range.getValue();
		}
		@RestMethod
		public String referer(Referer referer) {
			return referer.getValue();
		}
		@RestMethod
		public String te(TE te) {
			return te.getValue();
		}
		@RestMethod
		public String upgrade(Upgrade upgrade) {
			return upgrade.getValue();
		}
		@RestMethod
		public String userAgent(UserAgent userAgent) {
			return userAgent.getValue();
		}
		@RestMethod
		public String warning(Warning warning) {
			return warning.getValue();
		}
	}

	@Test
	public void a01_headerParameters() throws Exception {
		Swagger s = getSwagger(A.class);
		ParameterInfo x;

		x = s.getParameterInfo("/accept","get","header","Accept");
		assertObject(x).json().is("{'in':'header',name:'Accept',type:'string'}");

		x = s.getParameterInfo("/acceptCharset","get","header","Accept-Charset");
		assertObject(x).json().is("{'in':'header',name:'Accept-Charset',type:'string'}");

		x = s.getParameterInfo("/acceptEncoding","get","header","Accept-Encoding");
		assertObject(x).json().is("{'in':'header',name:'Accept-Encoding',type:'string'}");

		x = s.getParameterInfo("/acceptLanguage","get","header","Accept-Language");
		assertObject(x).json().is("{'in':'header',name:'Accept-Language',type:'string'}");

		x = s.getParameterInfo("/authorization","get","header","Authorization");
		assertObject(x).json().is("{'in':'header',name:'Authorization',type:'string'}");

		x = s.getParameterInfo("/cacheControl","get","header","Cache-Control");
		assertObject(x).json().is("{'in':'header',name:'Cache-Control',type:'string'}");

		x = s.getParameterInfo("/connection","get","header","Connection");
		assertObject(x).json().is("{'in':'header',name:'Connection',type:'string'}");

		x = s.getParameterInfo("/contentLength","get","header","Content-Length");
		assertObject(x).json().is("{'in':'header',name:'Content-Length',type:'integer',format:'int64'}");

		x = s.getParameterInfo("/contentType","get","header","Content-Type");
		assertObject(x).json().is("{'in':'header',name:'Content-Type',type:'string'}");

		x = s.getParameterInfo("/date","get","header","Date");
		assertObject(x).json().is("{'in':'header',name:'Date',type:'string'}");

		x = s.getParameterInfo("/expect","get","header","Expect");
		assertObject(x).json().is("{'in':'header',name:'Expect',type:'string'}");

		x = s.getParameterInfo("/from","get","header","From");
		assertObject(x).json().is("{'in':'header',name:'From',type:'string'}");

		x = s.getParameterInfo("/host","get","header","Host");
		assertObject(x).json().is("{'in':'header',name:'Host',type:'string'}");

		x = s.getParameterInfo("/ifMatch","get","header","If-Match");
		assertObject(x).json().is("{'in':'header',name:'If-Match',type:'string'}");

		x = s.getParameterInfo("/ifModifiedSince","get","header","If-Modified-Since");
		assertObject(x).json().is("{'in':'header',name:'If-Modified-Since',type:'string'}");

		x = s.getParameterInfo("/ifNoneMatch","get","header","If-None-Match");
		assertObject(x).json().is("{'in':'header',name:'If-None-Match',type:'string'}");

		x = s.getParameterInfo("/ifRange","get","header","If-Range");
		assertObject(x).json().is("{'in':'header',name:'If-Range',type:'string'}");

		x = s.getParameterInfo("/ifUnmodifiedSince","get","header","If-Unmodified-Since");
		assertObject(x).json().is("{'in':'header',name:'If-Unmodified-Since',type:'string'}");

		x = s.getParameterInfo("/maxForwards","get","header","Max-Forwards");
		assertObject(x).json().is("{'in':'header',name:'Max-Forwards',type:'integer',format:'int32'}");

		x = s.getParameterInfo("/pragma","get","header","Pragma");
		assertObject(x).json().is("{'in':'header',name:'Pragma',type:'string'}");

		x = s.getParameterInfo("/proxyAuthorization","get","header","Proxy-Authorization");
		assertObject(x).json().is("{'in':'header',name:'Proxy-Authorization',type:'string'}");

		x = s.getParameterInfo("/range","get","header","Range");
		assertObject(x).json().is("{'in':'header',name:'Range',type:'string'}");

		x = s.getParameterInfo("/referer","get","header","Referer");
		assertObject(x).json().is("{'in':'header',name:'Referer',type:'string'}");

		x = s.getParameterInfo("/te","get","header","TE");
		assertObject(x).json().is("{'in':'header',name:'TE',type:'string'}");

		x = s.getParameterInfo("/upgrade","get","header","Upgrade");
		assertObject(x).json().is("{'in':'header',name:'Upgrade',type:'string'}");

		x = s.getParameterInfo("/userAgent","get","header","User-Agent");
		assertObject(x).json().is("{'in':'header',name:'User-Agent',type:'string'}");

		x = s.getParameterInfo("/warning","get","header","Warning");
		assertObject(x).json().is("{'in':'header',name:'Warning',type:'string'}");
	}
}
