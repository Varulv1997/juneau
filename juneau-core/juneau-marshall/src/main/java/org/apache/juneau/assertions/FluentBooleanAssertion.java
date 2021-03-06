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
package org.apache.juneau.assertions;


import org.apache.juneau.internal.*;

/**
 * Used for fluent assertion calls against booleans.
 *
 * @param <R> The return type.
 */
@FluentSetters(returns="FluentBooleanAssertion<R>")
public class FluentBooleanAssertion<R> extends FluentComparableAssertion<R> {

	private final Boolean value;

	/**
	 * Constructor.
	 *
	 * @param value The value being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentBooleanAssertion(Boolean value, R returns) {
		this(null, value, returns);
	}

	/**
	 * Constructor.
	 *
	 * @param creator The assertion that created this assertion.
	 * @param value The value being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentBooleanAssertion(Assertion creator, Boolean value, R returns) {
		super(creator, value, returns);
		this.value = value;
	}

	/**
	 * Asserts that the value is true.
	 *
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isTrue() throws AssertionError {
		exists();
		if (value == false)
			throw error("Value was false.");
		return returns();
	}

	/**
	 * Asserts that the value is false.
	 *
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isFalse() throws AssertionError {
		exists();
		if (value == true)
			throw error("Value was true.");
		return returns();
	}

	// <FluentSetters>

	@Override /* GENERATED - Assertion */
	public FluentBooleanAssertion<R> msg(String msg, Object...args) {
		super.msg(msg, args);
		return this;
	}

	@Override /* GENERATED - Assertion */
	public FluentBooleanAssertion<R> stderr() {
		super.stderr();
		return this;
	}

	@Override /* GENERATED - Assertion */
	public FluentBooleanAssertion<R> stdout() {
		super.stdout();
		return this;
	}

	// </FluentSetters>
}
