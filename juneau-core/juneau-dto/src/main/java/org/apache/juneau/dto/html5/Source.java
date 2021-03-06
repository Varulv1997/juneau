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
package org.apache.juneau.dto.html5;

import java.net.*;
import java.net.URI;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;

/**
 * DTO for an HTML {@doc ExtHTML5.embedded-content-0#the-source-element <source>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc DtoHtml5}
 * </ul>
 */
@Bean(typeName="source")
public class Source extends HtmlElementVoid {

	/**
	 * Creates an empty {@link Source} element.
	 */
	public Source() {}

	/**
	 * Creates a {@link Source} element with the specified {@link Source#src(Object)} and {@link Source#type(String)}
	 * attributes.
	 *
	 * @param src The {@link Source#src(Object)} attribute.
	 * @param type The {@link Source#type(String)} attribute.
	 */
	public Source(Object src, String type) {
		src(src).type(type);
	}

	/**
	 * {@doc ExtHTML5.embedded-content-0#attr-source-src src} attribute.
	 *
	 * <p>
	 * Address of the resource.
	 *
	 * <p>
	 * The value can be of any of the following types: {@link URI}, {@link URL}, {@link String}.
	 * Strings must be valid URIs.
	 *
	 * <p>
	 * URIs defined by {@link UriResolver} can be used for values.
	 *
	 * @param src
	 * 	The new value for this attribute.
	 * 	Typically a {@link URL} or {@link String}.
	 * @return This object (for method chaining).
	 */
	public final Source src(Object src) {
		attrUri("src", src);
		return this;
	}

	/**
	 * {@doc ExtHTML5.embedded-content-0#attr-source-type type} attribute.
	 *
	 * <p>
	 * Type of embedded resource.
	 *
	 * @param type The new value for this attribute.
	 * @return This object (for method chaining).
	 */
	public final Source type(String type) {
		attr("type", type);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Source _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Source id(String id) {
		super.id(id);
		return this;
	}
}
