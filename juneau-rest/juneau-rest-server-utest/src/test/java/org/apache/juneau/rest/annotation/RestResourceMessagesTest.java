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

import org.apache.juneau.collections.*;
import org.apache.juneau.rest.mock2.*;
import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class RestResourceMessagesTest {

	//====================================================================================================
	// Setup
	//====================================================================================================

	static OMap convertToMap(ResourceBundle rb) {
		OMap m = new OMap();
		for (String k : new TreeSet<>(rb.keySet()))
			m.put(k, rb.getString(k));
		return m;
	}

	//====================================================================================================
	// Basic tests
	//====================================================================================================

	@Rest(messages="RestResourceMessagesTest1")
	public static class A {
		@RestMethod
		public OMap a01(ResourceBundle rb) {
			return convertToMap(rb);
		}
	}
	static MockRestClient a = MockRestClient.build(A.class);

	@Test
	public void a01() throws Exception {
		// Parent resource should just pick up values from its bundle.
		a.get("/a01").run().assertBody().is("{key1:'value1a',key2:'value2a'}");
	}

	//====================================================================================================
	// Overridden on subclass.
	//====================================================================================================

	@Rest(messages="RestResourceMessagesTest2")
	public static class B extends A {}
	static MockRestClient b = MockRestClient.build(B.class);

	@Test
	public void b01() throws Exception {
		// Child resource should pick up values from both parent and child,
		// ordered child before parent.
		b.get("/a01").run().assertBody().is("{key1:'value1a',key2:'value2b',key3:'value3b'}");
	}
}
