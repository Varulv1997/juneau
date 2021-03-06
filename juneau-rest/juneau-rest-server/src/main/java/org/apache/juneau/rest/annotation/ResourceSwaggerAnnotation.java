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
package org.apache.juneau.rest.annotation;

import static org.apache.juneau.internal.ArrayUtils.*;

import java.lang.annotation.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.jsonschema.annotation.*;

/**
 * Utility classes and methods for the {@link ResourceSwagger @ResourceSwagger} annotation.
 */
public class ResourceSwaggerAnnotation {

	/** Default value */
	public static final ResourceSwagger DEFAULT = create().build();

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @return A new builder object.
	 */
	public static Builder create() {
		return new Builder();
	}

	/**
	 * Returns <jk>true</jk> if the specified annotation contains all default values.
	 *
	 * @param a The annotation to check.
	 * @return <jk>true</jk> if the specified annotation contains all default values.
	 */
	public static boolean empty(ResourceSwagger a) {
		return a == null || DEFAULT.equals(a);
	}

	/**
	 * Builder class for the {@link ResourceSwagger} annotation.
	 *
	 * <ul class='seealso'>
	 * 	<li class='jm'>{@link BeanContextBuilder#annotations(Annotation...)}
	 * </ul>
	 */
	public static class Builder extends AnnotationBuilder {

		Contact contact = ContactAnnotation.DEFAULT;
		ExternalDocs externalDocs = ExternalDocsAnnotation.DEFAULT;
		License license = LicenseAnnotation.DEFAULT;
		String version="";
		String[] description={}, termsOfService={}, title={}, value={};
		Tag[] tags={};

		/**
		 * Constructor.
		 */
		public Builder() {
			super(ResourceSwagger.class);
		}

		/**
		 * Instantiates a new {@link ResourceSwagger @ResourceSwagger} object initialized with this builder.
		 *
		 * @return A new {@link ResourceSwagger @ResourceSwagger} object.
		 */
		public ResourceSwagger build() {
			return new Impl(this);
		}

		/**
		 * Sets the {@link ResourceSwagger#contact()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object (for method chaining).
		 */
		public Builder contact(Contact value) {
			this.contact = value;
			return this;
		}

		/**
		 * Sets the {@link ResourceSwagger#description()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object (for method chaining).
		 */
		public Builder description(String...value) {
			this.description = value;
			return this;
		}

		/**
		 * Sets the {@link ResourceSwagger#externalDocs()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object (for method chaining).
		 */
		public Builder externalDocs(ExternalDocs value) {
			this.externalDocs = value;
			return this;
		}

		/**
		 * Sets the {@link ResourceSwagger#license()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object (for method chaining).
		 */
		public Builder license(License value) {
			this.license = value;
			return this;
		}

		/**
		 * Sets the {@link ResourceSwagger#tags()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object (for method chaining).
		 */
		public Builder tags(Tag...value) {
			this.tags = value;
			return this;
		}

		/**
		 * Sets the {@link ResourceSwagger#termsOfService()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object (for method chaining).
		 */
		public Builder termsOfService(String...value) {
			this.termsOfService = value;
			return this;
		}

		/**
		 * Sets the {@link ResourceSwagger#title()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object (for method chaining).
		 */
		public Builder title(String...value) {
			this.title = value;
			return this;
		}

		/**
		 * Sets the {@link ResourceSwagger#value()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object (for method chaining).
		 */
		public Builder value(String...value) {
			this.value = value;
			return this;
		}

		/**
		 * Sets the {@link ResourceSwagger#version()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object (for method chaining).
		 */
		public Builder version(String value) {
			this.version = value;
			return this;
		}

		// <FluentSetters>

		// </FluentSetters>
	}

	private static class Impl extends AnnotationImpl implements ResourceSwagger {

		private final Contact contact;
		private final ExternalDocs externalDocs;
		private final License license;
		private final String version;
		private final String[] description, termsOfService, title, value;
		private final Tag[] tags;

		Impl(Builder b) {
			super(b);
			this.contact = b.contact;
			this.description = copyOf(b.description);
			this.externalDocs = b.externalDocs;
			this.license = b.license;
			this.tags = copyOf(b.tags);
			this.termsOfService = copyOf(b.termsOfService);
			this.title = copyOf(b.title);
			this.value = copyOf(b.value);
			this.version = b.version;
			postConstruct();
		}

		@Override /* ResourceSwagger */
		public Contact contact() {
			return contact;
		}

		@Override /* ResourceSwagger */
		public String[] description() {
			return description;
		}

		@Override /* ResourceSwagger */
		public ExternalDocs externalDocs() {
			return externalDocs;
		}

		@Override /* ResourceSwagger */
		public License license() {
			return license;
		}

		@Override /* ResourceSwagger */
		public Tag[] tags() {
			return tags;
		}

		@Override /* ResourceSwagger */
		public String[] termsOfService() {
			return termsOfService;
		}

		@Override /* ResourceSwagger */
		public String[] title() {
			return title;
		}

		@Override /* ResourceSwagger */
		public String[] value() {
			return value;
		}

		@Override /* ResourceSwagger */
		public String version() {
			return version;
		}
	}
}