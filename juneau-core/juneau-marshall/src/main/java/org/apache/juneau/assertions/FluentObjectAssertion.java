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

import java.util.function.*;

import org.apache.juneau.internal.*;
import org.apache.juneau.json.*;
import org.apache.juneau.marshall.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.serializer.*;

/**
 * Used for fluent assertion calls against POJOs.
 *
 * @param <R> The return type.
 */
@FluentSetters(returns="FluentObjectAssertion<R>")
public class FluentObjectAssertion<R> extends FluentAssertion<R> {

	private final Object value;

	private static JsonSerializer JSON = JsonSerializer.create()
		.ssq()
		.build();

	private static JsonSerializer JSON_SORTED = JsonSerializer.create()
		.ssq()
		.sortProperties()
		.sortCollections()
		.sortMaps()
		.build();

	/**
	 * Constructor.
	 *
	 * @param value The object being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentObjectAssertion(Object value, R returns) {
		this(null, value, returns);
	}

	/**
	 * Constructor.
	 *
	 * @param creator The assertion that created this assertion.
	 * @param value The object being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentObjectAssertion(Assertion creator, Object value, R returns) {
		super(creator, returns);
		this.value = value;
	}

	/**
	 * Asserts that the object is an instance of the specified class.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Validates that the specified object is an instance of MyBean.</jc>
	 * 	<jsm>assertObject<jsm>(myPojo).instanceOf(MyBean.<jk>class</jk>);
	 * </p>
	 *
	 * @param parent The value to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isType(Class<?> parent) throws AssertionError {
		exists();
		assertNotNull("parent", parent);
		if (! ClassInfo.of(value).isChildOf(parent))
			throw error("Unexpected class.\n\tExpect=[{0}]\n\tActual=[{1}]", className(parent), className(value));
		return returns();
	}

	/**
	 * Converts this object to text using the specified serializer and returns it as a new assertion.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Validates that the specified object is an instance of MyBean.</jc>
	 * 	<jsm>assertObject<jsm>(myPojo).serialized(XmlSerializer.<jsf>DEFAULT</jsf>).is(<js>"&lt;object>&lt;foo>bar&lt;/foo>&lt;baz>qux&lt;/baz>&lt;/object>"</js>);
	 * </p>
	 *
	 * @param ws The serializer to use to convert the object to text.
	 * @return A new fluent string assertion.
	 */
	public FluentStringAssertion<R> serialized(WriterSerializer ws) {
		try {
			String s = ws.serialize(this.value);
			return new FluentStringAssertion<>(this, s, returns());
		} catch (SerializeException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Converts this object to a string using {@link Object#toString} and returns it as a new assertion.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Validates that the specified object is "foobar" after converting to a string.</jc>
	 * 	<jsm>assertObject<jsm>(myPojo).string().is(<js>"foobar"</js>);
	 * </p>
	 *
	 * @return A new fluent string assertion.
	 */
	public FluentStringAssertion<R> string() {
		return new FluentStringAssertion<>(this, value == null ? null : value.toString(), returns());
	}

	/**
	 * Converts this object to a string using the specified function and returns it as a new assertion.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Validates that the specified object is "foobar" after converting to a string.</jc>
	 * 	<jsm>assertObject<jsm>(myPojo).string(<jv>x</jv>-><jv>x</jv>.toString()).is(<js>"foobar"</js>);
	 * </p>
	 *
	 * @param function The conversion function.
	 * @return A new fluent string assertion.
	 */
	public FluentStringAssertion<R> string(Function<Object,String> function) {
		return new FluentStringAssertion<>(this, function.apply(value), returns());
	}

	/**
	 * Converts this object to a string using the specified function and returns it as a new assertion.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Validates that the specified object is "foobar" after converting to a string.</jc>
	 * 	<jsm>assertObject<jsm>(myPojo).string(MyBean.<jk>class</jk>,<jv>x</jv>-><jv>x</jv>.myBeanMethod()).is(<js>"foobar"</js>);
	 * </p>
	 *
	 * @param c The class of the object being converted.
	 * @param function The conversion function.
	 * @param <T> The class of the object being converted.
	 * @return A new fluent string assertion.
	 */
	@SuppressWarnings("unchecked")
	public <T> FluentStringAssertion<R> string(Class<T> c, Function<T,String> function) {
		return new FluentStringAssertion<>(this, function.apply((T)value), returns());
	}

	/**
	 * Converts this object to simplified JSON and returns it as a new assertion.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Validates that the specified object is an instance of MyBean.</jc>
	 * 	<jsm>assertObject<jsm>(myPojo).json().is(<js>"{foo:'bar',baz:'qux'}"</js>);
	 * </p>
	 *
	 * @return A new fluent string assertion.
	 */
	public FluentStringAssertion<R> json() {
		return serialized(JSON);
	}

	/**
	 * Converts this object to sorted simplified JSON and returns it as a new assertion.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Validates that the specified object is an instance of MyBean.</jc>
	 * 	<jsm>assertObject<jsm>(myPojo).jsonSorted().is(<js>"{baz:'qux',foo:'bar'}"</js>);
	 * </p>
	 *
	 * @return A new fluent string assertion.
	 */
	public FluentStringAssertion<R> jsonSorted() {
		return serialized(JSON_SORTED);
	}

	/**
	 * Verifies that two objects are equivalent after converting them both to JSON.
	 *
	 * @param o The object to compare against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R sameAs(Object o) throws AssertionError {
		return sameAsSerialized(o, JSON);
	}

	/**
	 * Verifies that two objects are equivalent after converting them both to sorted JSON.
	 *
	 * <p>
	 * Properties, maps, and collections are all sorted on both objects before comparison.
	 *
	 * @param o The object to compare against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R sameAsSorted(Object o) {
		return sameAsSerialized(o, JSON_SORTED);
	}

	/**
	 * Asserts that the specified object is the same as this object after converting both to strings using the specified serializer.
	 *
	 * @param o The object to compare against.
	 * @param serializer The serializer to use to serialize this object.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R sameAsSerialized(Object o, WriterSerializer serializer) {
		try {
			String s1 = serializer.serialize(this.value);
			String s2 = serializer.serialize(o);
			if (! StringUtils.isEquals(s1, s2))
				throw error("Unexpected comparison.\n\tExpect=[{0}]\n\tActual=[{1}]", s2, s1);
		} catch (SerializeException e) {
			throw new RuntimeException(e);
		}
		return returns();
	}

	/**
	 * Asserts that the value equals the specified value.
	 *
	 * @param value The value to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isEqual(Object value) throws AssertionError {
		if (this.value == value)
			return returns();
		exists();
		if (! this.value.equals(equivalent(value)))
			throw error("Unexpected value.\n\tExpect=[{0}]\n\tActual=[{1}]", value, this.value);
		return returns();
	}

	/**
	 * Asserts that the value equals the specified value.
	 *
	 * <p>
	 * Equivalent to {@link #isEqual(Object)}.
	 *
	 * @param value The value to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R is(Object value) throws AssertionError {
		return isEqual(equivalent(value));
	}

	/**
	 * Asserts that the value equals the specified value.
	 *
	 * @param value The value to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R doesNotEqual(Object value) throws AssertionError {
		if (this.value == null && value != null || this.value != null && value == null)
			return returns();
		if (this.value == null || this.value.equals(equivalent(value)))
			throw error("Unexpected value.\n\tExpected not=[{0}]\n\tActual=[{1}]", value, this.value);
		return returns();
	}

	/**
	 * Asserts that the value passes the specified predicate test.
	 *
	 * @param test The predicate to use to test the value.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R passes(Predicate<Object> test) throws AssertionError {
		if (! test.test(value))
			throw error("Value did not pass predicate test.\n\tValue=[{0}]", value);
		return returns();
	}

	/**
	 * Asserts that the value passes the specified predicate test.
	 *
	 * @param c The class type of the object being tested.
	 * @param <T> The class type of the object being tested.
	 * @param test The predicate to use to test the value.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	@SuppressWarnings("unchecked")
	public <T> R passes(Class<T> c, Predicate<T> test) throws AssertionError {
		isType(c);
		if (! test.test((T)value))
			throw error("Value did not pass predicate test.\n\tValue=[{0}]", value);
		return returns();
	}

	/**
	 * Asserts that the object is not null.
	 *
	 * <p>
	 * Equivalent to {@link #isNotNull()}.
	 *
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R exists() throws AssertionError {
		return isNotNull();
	}

	/**
	 * Asserts that the object is null.
	 *
	 * <p>
	 * Equivalent to {@link #isNotNull()}.
	 *
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R doesNotExist() throws AssertionError {
		return isNull();
	}

	/**
	 * Asserts that the object is not null.
	 *
	 * <p>
	 * Equivalent to {@link #isNotNull()}.
	 *
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isNotNull() throws AssertionError {
		if (value == null)
			throw error("Value was null.");
		return returns();
	}

	/**
	 * Asserts that the object i null.
	 *
	 * <p>
	 * Equivalent to {@link #isNotNull()}.
	 *
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isNull() throws AssertionError {
		if (value != null)
			throw error("Value was not null.");
		return returns();
	}

	/**
	 * Asserts that the value equals the specified value.
	 *
	 * <p>
	 * Equivalent to {@link #doesNotEqual(Object)}.
	 *
	 * @param value The value to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isNot(Object value) throws AssertionError {
		return doesNotEqual(equivalent(value));
	}

	/**
	 * Asserts that the value is one of the specified values.
	 *
	 * @param values The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isAny(Object...values) throws AssertionError {
		exists();
		for (Object v : values)
			if (this.value.equals(equivalent(v)))
				return returns();
		throw error("Expected value not found.\n\tExpect=[{0}]\n\tActual=[{1}]", SimpleJson.DEFAULT.toString(values), value);
	}

	/**
	 * Asserts that the value is one of the specified values.
	 *
	 * @param values The values to check against.
	 * @return The response object (for method chaining).
	 * @throws AssertionError If assertion failed.
	 */
	public R isNotAny(Object...values) throws AssertionError {
		exists();
		for (Object v : values)
			if (this.value.equals(equivalent(v)))
				throw error("Unexpected value found.\n\tUnexpected=[{0}]\n\tActual=[{1}]", v, value);
		return returns();
	}

	/**
	 * Subclasses can override this method to provide special conversions on objects being compared.
	 *
	 * @param o The object to cast.
	 * @return The cast object.
	 */
	protected Object equivalent(Object o) {
		return o;
	}

	// <FluentSetters>

	@Override /* GENERATED - Assertion */
	public FluentObjectAssertion<R> msg(String msg, Object...args) {
		super.msg(msg, args);
		return this;
	}

	@Override /* GENERATED - Assertion */
	public FluentObjectAssertion<R> stderr() {
		super.stderr();
		return this;
	}

	@Override /* GENERATED - Assertion */
	public FluentObjectAssertion<R> stdout() {
		super.stdout();
		return this;
	}

	// </FluentSetters>
}
