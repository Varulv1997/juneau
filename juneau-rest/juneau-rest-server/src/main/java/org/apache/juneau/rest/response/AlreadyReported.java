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
package org.apache.juneau.rest.response;

import static org.apache.juneau.rest.response.AlreadyReported.*;

import org.apache.juneau.http.annotation.*;

/**
 * Represents an <c>HTTP 208 Already Reported</c> response.
 *
 * <p>
 * The members of a DAV binding have already been enumerated in a preceding part of the (multistatus) response, and are not being included again.
 *
 * @deprecated Use {@link org.apache.juneau.http.response.AlreadyReported}
 */
@Response(code=CODE, description=MESSAGE)
@Deprecated
public class AlreadyReported extends HttpResponse {

	/** HTTP status code */
	public static final int CODE = 208;

	/** Default message */
	public static final String MESSAGE = "Already Reported";

	/** Reusable instance. */
	public static final AlreadyReported INSTANCE = new AlreadyReported();

	/**
	 * Constructor using HTTP-standard message.
	 */
	public AlreadyReported() {
		this(MESSAGE);
	}

	/**
	 * Constructor using custom message.
	 * @param message Message to send as the response.
	 */
	public AlreadyReported(String message) {
		super(message);
	}
}