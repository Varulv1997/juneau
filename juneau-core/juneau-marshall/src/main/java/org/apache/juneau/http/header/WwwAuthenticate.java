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
package org.apache.juneau.http.header;

import java.util.function.*;

import org.apache.juneau.http.annotation.*;

/**
 * Represents a parsed <l>WWW-Authenticate </l> HTTP response header.
 *
 * <p>
 * Indicates the authentication scheme that should be used to access the requested entity.
 *
 * <h5 class='figure'>Example</h5>
 * <p class='bcode w800'>
 * 	WWW-Authenticate: Basic
 * </p>
 *
 * <h5 class='topic'>RFC2616 Specification</h5>
 *
 * The WWW-Authenticate response-header field MUST be included in 401 (Unauthorized) response messages.
 * The field value consists of at least one challenge that indicates the authentication scheme(s) and parameters
 * applicable to the Request-URI.
 *
 * <p class='bcode w800'>
 * 	WWW-Authenticate  = "WWW-Authenticate" ":" 1#challenge
 * </p>
 *
 * <p>
 * The HTTP access authentication process is described in "HTTP Authentication: Basic and Digest Access Authentication".
 * User agents are advised to take special care in parsing the WWW-Authenticate field value as it might contain more
 * than one challenge, or if more than one WWW-Authenticate header field is provided, the contents of a challenge
 * itself can contain a comma-separated list of authentication parameters.
 *
 * <ul class='seealso'>
 * 	<li class='extlink'>{@doc ExtRFC2616}
 * </ul>
 */
@Header("WWW-Authenticate")
public class WwwAuthenticate extends BasicStringHeader {

	private static final long serialVersionUID = 1L;

	/**
	 * Convenience creator.
	 *
	 * @param value
	 * 	The header value.
	 * 	<br>Can be any of the following:
	 * 	<ul>
	 * 		<li>{@link String}
	 * 		<li>Anything else - Converted to <c>String</c> then parsed.
	 * 	</ul>
	 * @return A new {@link WwwAuthenticate} object.
	 */
	public static WwwAuthenticate of(Object value) {
		if (value == null)
			return null;
		return new WwwAuthenticate(value);
	}

	/**
	 * Convenience creator using supplier.
	 *
	 * <p>
	 * Header value is re-evaluated on each call to {@link #getValue()}.
	 *
	 * @param value
	 * 	The header value supplier.
	 * 	<br>Can be any of the following:
	 * 	<ul>
	 * 		<li>{@link String}
	 * 		<li>Anything else - Converted to <c>String</c> then parsed.
	 * 	</ul>
	 * @return A new {@link WwwAuthenticate} object.
	 */
	public static WwwAuthenticate of(Supplier<?> value) {
		if (value == null)
			return null;
		return new WwwAuthenticate(value);
	}

	/**
	 * Constructor.
	 *
	 * @param value
	 * 	The header value.
	 * 	<br>Can be any of the following:
	 * 	<ul>
	 * 		<li>{@link String}
	 * 		<li>Anything else - Converted to <c>String</c> then parsed.
	 * 		<li>A {@link Supplier} of anything on this list.
	 * 	</ul>
	 */
	public WwwAuthenticate(Object value) {
		super("WWW-Authenticate", value);
	}

	/**
	 * Constructor
	 *
	 * @param value
	 * 	The header value.
	 */
	public WwwAuthenticate(String value) {
		this((Object)value);
	}
}
