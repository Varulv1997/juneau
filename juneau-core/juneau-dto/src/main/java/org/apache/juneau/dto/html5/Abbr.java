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

import org.apache.juneau.annotation.*;

/**
 * DTO for an HTML {@doc ExtHTML5.text-level-semantics#the-abbr-element <abbr>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc DtoHtml5}
 * </ul>
 */
@Bean(typeName="abbr")
public class Abbr extends HtmlElementMixed {

	/**
	 * Creates an empty {@link Abbr} element.
	 */
	public Abbr() {}

	/**
	 * Creates an {@link Abbr} element with the specified {@link Abbr#title(String)} attribute and
	 * {@link Abbr#children(Object[])} nodes.
	 *
	 * @param title The {@link Abbr#title(String)} attribute.
	 * @param children The {@link Abbr#children(Object[])} nodes.
	 */
	public Abbr(String title, Object...children) {
		title(title).children(children);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Abbr _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Abbr id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElement */
	public final Abbr style(String style) {
		super.style(style);
		return this;
	}

	@Override /* HtmlElement */
	public final Abbr title(String title) {
		super.title(title);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Abbr children(Object...children) {
		super.children(children);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Abbr child(Object child) {
		super.child(child);
		return this;
	}
}
