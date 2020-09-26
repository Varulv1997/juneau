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
 * DTO for an HTML {@doc ExtHTML5.grouping-content#the-blockquote-element <blockquote>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc DtoHtml5}
 * </ul>
 */
@Bean(typeName="blockquote")
public class Blockquote extends HtmlElementMixed {

	/**
	 * Creates an empty {@link Blockquote} element.
	 */
	public Blockquote() {}

	/**
	 * Creates a {@link Blockquote} element with the specified child nodes.
	 *
	 * @param children The child nodes.
	 */
	public Blockquote(Object...children) {
		children(children);
	}

	/**
	 * {@doc ExtHTML5.grouping-content#attr-blockquote-cite cite}
	 * attribute.
	 *
	 * <p>
	 * Link to the source of the quotation.
	 *
	 * @param cite The new value for this attribute.
	 * @return This object (for method chaining).
	 */
	public final Blockquote cite(String cite) {
		attr("cite", cite);
		return this;
	}


	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Blockquote _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Blockquote id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElement */
	public final Blockquote style(String style) {
		super.style(style);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Blockquote children(Object...children) {
		super.children(children);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Blockquote child(Object child) {
		super.child(child);
		return this;
	}
}
