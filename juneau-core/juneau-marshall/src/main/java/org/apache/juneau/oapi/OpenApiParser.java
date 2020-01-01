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
package org.apache.juneau.oapi;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.uon.*;

/**
 * OpenAPI part parser.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc juneau-marshall.OpenApiDetails.Parsers}
 * </ul>
 */
@ConfigurableContext
public class OpenApiParser extends UonParser implements OpenApiCommon {

	//-------------------------------------------------------------------------------------------------------------------
	// Configurable properties
	//-------------------------------------------------------------------------------------------------------------------

	static final String PREFIX = "OpenApiParser";

	//-------------------------------------------------------------------------------------------------------------------
	// Predefined instances
	//-------------------------------------------------------------------------------------------------------------------

	/** Reusable instance of {@link OpenApiParser}. */
	public static final OpenApiParser DEFAULT = new OpenApiParser(PropertyStore.DEFAULT);


	//-------------------------------------------------------------------------------------------------------------------
	// Instance
	//-------------------------------------------------------------------------------------------------------------------

	/**
	 * Constructor.
	 *
	 * @param ps The property store containing all the settings for this object.
	 */
	public OpenApiParser(PropertyStore ps) {
		this(ps, "text/openapi");
	}

	/**
	 * Constructor.
	 *
	 * @param ps
	 * 	The property store containing all the settings for this object.
	 * @param consumes
	 * 	The list of media types that this parser consumes (e.g. <js>"application/json"</js>, <js>"*&#8203;/json"</js>).
	 */
	public OpenApiParser(PropertyStore ps, String...consumes) {
		super(ps, consumes);
	}

	@Override /* Context */
	public OpenApiParserBuilder builder() {
		return new OpenApiParserBuilder(getPropertyStore());
	}

	/**
	 * Instantiates a new clean-slate {@link OpenApiParserBuilder} object.
	 *
	 * <p>
	 * This is equivalent to simply calling <code><jk>new</jk> UonPartParserBuilder()</code>.
	 *
	 * <p>
	 * Note that this method creates a builder initialized to all default settings, whereas {@link #builder()} copies
	 * the settings of the object called on.
	 *
	 * @return A new {@link OpenApiParserBuilder} object.
	 */
	public static OpenApiParserBuilder create() {
		return new OpenApiParserBuilder();
	}

	//-------------------------------------------------------------------------------------------------------------------
	// Entry point methods
	//-------------------------------------------------------------------------------------------------------------------

	@Override
	public OpenApiParserSession createSession() {
		return new OpenApiParserSession(this, ParserSessionArgs.DEFAULT);
	}

	@Override
	public OpenApiParserSession createPartSession(ParserSessionArgs args) {
		return new OpenApiParserSession(this, args);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Other methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* Context */
	public ObjectMap toMap() {
		return super.toMap()
			.append("OpenApiParser", new DefaultFilteringObjectMap()
			);
	}
}
