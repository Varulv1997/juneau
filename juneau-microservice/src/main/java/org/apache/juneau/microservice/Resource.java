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
package org.apache.juneau.microservice;

import org.apache.juneau.rest.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.svl.vars.*;

/**
 * Superclass for all REST resources.
 * <p>
 * In additional to the functionality of the {@link RestServletDefault} group,
 * augments the {@link RestContext#getVarResolver()} method with the following additional variable types:
 * <ul class='spaced-list'>
 * 	<li>
 * 		<code class='snippet'>$ARG{...}</code> - Command line arguments pulled from {@link Microservice#getArgs()}.
 * 		
 *			<h6 class='figure'>Example:</h6>
 * 		<p class='bcode'>
 * 	String firstArg = request.getVarResolver().resolve(<js>"$ARG{0}"</js>);  <jc>// First argument.</jc>
 * 	String namedArg = request.getVarResolver().resolve(<js>"$ARG{myarg}"</js>);  <jc>// Named argument (e.g. "myarg=foo"). </jc>
 * 		</p>
 * 	<li>
 * 		<code class='snippet'>$MF{...}</code> - Manifest file entries pulled from {@link Microservice#getManifest()}.
 * 		
 * 		<h6 class='figure'>Example:</h6>
 * 		<p class='bcode'>
 * 	String mainClass = request.getVarResolver().resolve(<js>"$MF{Main-Class}"</js>);  <jc>// Main class. </jc>
 * 		</p>
 * </ul>
 */
@SuppressWarnings("serial")
@RestResource(
	htmldoc=@HtmlDoc(
		links="{up:'request:/..',options:'servlet:/?method=OPTIONS'}"
	),
	config="$S{juneau.configFile}",
	stylesheet="$C{REST/stylesheet,styles/juneau.css}"
)
public abstract class Resource extends RestServletDefault {

	@Override /* RestServlet */
	public synchronized void init(RestConfig config) throws Exception {
		config
			.addVars(ArgsVar.class, ManifestFileVar.class)
			.addVarContextObject(ArgsVar.SESSION_args, Microservice.getArgs())
			.addVarContextObject(ManifestFileVar.SESSION_manifest, Microservice.getManifest());
		super.init(config);
	}
}
