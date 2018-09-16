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
package org.apache.juneau.rest;

import static org.apache.juneau.http.HttpMethodName.*;
import static org.apache.juneau.jsonschema.JsonSchemaGenerator.*;

import org.apache.juneau.dto.swagger.*;
import org.apache.juneau.dto.swagger.ui.*;
import org.apache.juneau.html.*;
import org.apache.juneau.jso.*;
import org.apache.juneau.json.*;
import org.apache.juneau.plaintext.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.soap.*;
import org.apache.juneau.uon.*;
import org.apache.juneau.urlencoding.*;
import org.apache.juneau.xml.*;
import org.apache.juneau.xmlschema.*;

/**
 * Subclass of {@link RestServlet} with default serializers and parsers defined.
 *
 * <p>
 * Supports the following request <code>Accept</code> header values with the resulting response <code>Content-Type</code>:
 * <table class='styled'>
 * 	<tr>
 * 		<th>Accept</th>
 * 		<th>Content-Type</th>
 * 		<th>Serializer</th>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>application/json<br>text/json</td>
 * 		<td class='code'>application/json</td>
 * 		<td>{@link JsonSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>application/json+simple<br>text/json+simple</td>
 * 		<td class='code'>application/json</td>
 * 		<td>{@link org.apache.juneau.json.SimpleJsonSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>application/json+schema<br>text/json+schema</td>
 * 		<td class='code'>application/json</td>
 * 		<td>{@link JsonSchemaSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/xml</td>
 * 		<td class='code'>text/xml</td>
 * 		<td>{@link XmlDocSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/xml+schema</td>
 * 		<td class='code'>text/xml</td>
 * 		<td>{@link XmlSchemaDocSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/html</td>
 * 		<td class='code'>text/html</td>
 * 		<td>{@link HtmlDocSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/html+stripped</td>
 * 		<td class='code'>text/html</td>
 * 		<td>{@link HtmlStrippedDocSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/uon</td>
 * 		<td class='code'>text/uon</td>
 * 		<td>{@link UonSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>application/x-www-form-urlencoded</td>
 * 		<td class='code'>application/x-www-form-urlencoded</td>
 * 		<td>{@link UrlEncodingSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/xml+soap</td>
 * 		<td class='code'>text/xml</td>
 * 		<td>{@link SoapXmlSerializer}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/plain</td>
 * 		<td class='code'>text/plain</td>
 * 		<td>{@link PlainTextSerializer}</td>
 * 	</tr>
 * </table>
 * <p>
 * Supports the following request <code>Content-Type</code> header values:
 * </p>
 * <table class='styled'>
 * 	<tr>
 * 		<th>Content-Type</th>
 * 		<th>Parser</th>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>application/json<br>text/json</td>
 * 		<td>{@link JsonParser}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/xml<br>application/xml</td>
 * 		<td>{@link XmlParser}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/html<br>text/html+stripped</td>
 * 		<td>{@link HtmlParser}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/uon</td>
 * 		<td>{@link UonParser}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>application/x-www-form-urlencoded</td>
 * 		<td>{@link UrlEncodingParser}</td>
 * 	</tr>
 * 	<tr>
 * 		<td class='code'>text/plain</td>
 * 		<td>{@link PlainTextParser}</td>
 * 	</tr>
 * </table>
 *
 * <p>
 * It should be noted that we do NOT add {@link JsoParser} to the list of parsers since this could cause security
 * issues.
 * Use caution when using this particular parser as it could inadvertently cause code execution security holes.
 *
 * <p>
 * The list of serializers and parsers can be appended to using the
 * {@link RestResource#serializers() @RestResource(serializers)} and
 * {@link RestResource#parsers() @RestResource(parsers)} annotations on subclasses.
 *
 * <p>
 * This subclass also provides a default OPTIONS page by implementing a {@link #getOptions(RestRequest)} that returns a
 * POJO consisting of beans describing the class.
 *
 * <p>
 * The OPTIONS page can be modified or augmented by overriding this method and providing your own data.
 *
 * <h5 class='section'>Notes:</h5>
 * <ul class='spaced-list'>
 * 	<li>
 * 		Provides a default HTML stylesheet by setting {@link HtmlDoc#stylesheet() @HtmlDoc(stylesheet)}
 * 		to <js>"styles/juneau.css"</js>.
 * 	<li>
 * 		Provides a default classpath entry "htdocs" by setting
 * 		{@link RestResource#staticFiles() @RestResource(staticFiles)} to <code>{<js>"htdocs:htdocs"</js>,<js>"styles:styles"</js>}</code>.
 * 		This allows files inside the <code>[servletPackage].htdocs</code> package to be served up under the URL
 * 		<code>/servletPath/htdocs</code>.
 * </ul>
 *
 * <h5 class='section'>See Also:</h5>
 * <ul>
 * 	<li class='link'>{@doc juneau-rest-server.Instantiation.BasicRestServlet}
 * </ul>
 */
@RestResource(

	// Allow OPTIONS requests to be simulated using ?method=OPTIONS query parameter.
	allowedMethodParams="OPTIONS",

	// HTML-page specific settings.
	htmldoc=@HtmlDoc(
		// Basic page navigation links.
		navlinks={
			"up: request:/..",
			"options: servlet:/?method=OPTIONS"
		}
	)
)
public abstract class BasicRestServlet extends RestServlet implements BasicRestConfig {
	private static final long serialVersionUID = 1L;

	/**
	 * [OPTIONS /*] - Show resource options.
	 *
	 * @param req The HTTP request.
	 * @return A bean containing the contents for the OPTIONS page.
	 */
	@RestMethod(name=OPTIONS, path="/*",

		summary="Swagger documentation",
		description="Swagger documentation for this resource.",

		htmldoc=@HtmlDoc(
			// Override the nav links for the swagger page.
			navlinks={
				"back: servlet:/",
				"json: servlet:/?method=OPTIONS&Accept=text/json&plainText=true"
			},
			// Never show aside contents of page inherited from class.
			aside="NONE"
		),

		// POJO swaps to apply to all serializers/parsers on this method.
		pojoSwaps={
			// Use the SwaggerUI swap when rendering Swagger beans.
			// This is a per-media-type swap that only applies to text/html requests.
			SwaggerUI.class
		},

		// Properties to apply to all serializers/parsers and REST-specific API objects on this method.
		properties={
			// Add descriptions to the following types when not specified:
			@Property(name=JSONSCHEMA_addDescriptionsTo, value="bean,collection,array,map,enum"),

			// Add x-example to the following types:
			@Property(name=JSONSCHEMA_addExamplesTo, value="bean,collection,array,map"),

			// Don't generate schema information on the Swagger bean itself or HTML beans.
			@Property(name=JSONSCHEMA_ignoreTypes, value="Swagger,org.apache.juneau.dto.html5.*")
		},

		// Shortcut for boolean properties.
		flags={
			// Use $ref references for bean definitions to reduce duplication in Swagger.
			JSONSCHEMA_useBeanDefs,

			// When parsing generated beans, ignore unknown properties that may only exist as getters and not setters.
			BEAN_ignoreUnknownBeanProperties
		}
	)
	public Swagger getOptions(RestRequest req) {
		// Localized Swagger for this resource is available through the RestRequest object.
		return req.getSwagger();
	}
}
