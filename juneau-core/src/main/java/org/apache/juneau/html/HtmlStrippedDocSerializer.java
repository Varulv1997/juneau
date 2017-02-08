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
package org.apache.juneau.html;

import java.lang.reflect.*;
import java.util.*;

import org.apache.juneau.annotation.*;
import org.apache.juneau.serializer.*;

/**
 * Serializes POJOs to HTTP responses as stripped HTML.
 *
 * <h5 class='section'>Media types:</h5>
 * <p>
 * 	Handles <code>Accept</code> types: <code>text/html+stripped</code>
 * <p>
 * 	Produces <code>Content-Type</code> types: <code>text/html</code>
 *
 * <h5 class='section'>Description:</h5>
 * <p>
 * 	Produces the same output as {@link HtmlDocSerializer}, but without the header and body tags and page title and description.
 * 	Used primarily for JUnit testing the {@link HtmlDocSerializer} class.
 */
@Produces(value="text/html+stripped",contentType="text/html")
public class HtmlStrippedDocSerializer extends HtmlSerializer {

	//---------------------------------------------------------------------------
	// Overridden methods
	//---------------------------------------------------------------------------

	@Override /* Serializer */
	protected void doSerialize(SerializerSession session, Object o) throws Exception {
		HtmlSerializerSession s = (HtmlSerializerSession)session;
		HtmlWriter w = s.getWriter();
		if (o == null
			|| (o instanceof Collection && ((Collection<?>)o).size() == 0)
			|| (o.getClass().isArray() && Array.getLength(o) == 0))
			w.sTag(1, "p").append("No Results").eTag("p").nl();
		else
			super.doSerialize(s, o);
	}
}
