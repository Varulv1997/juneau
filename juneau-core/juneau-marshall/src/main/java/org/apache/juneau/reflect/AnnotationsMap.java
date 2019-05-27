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
package org.apache.juneau.reflect;

import java.lang.annotation.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.marshall.*;

/**
 * A mapping of annotation classes to lists of annotations.
 */
public class AnnotationsMap extends HashMap<Class<? extends Annotation>,List<AnnotationInfo<? extends Annotation>>> {

	private static final long serialVersionUID = 1L;

	/**
	 * Adds an annotation to this map.
	 *
	 * @param a The annotation to add.
	 * @return This object (for method chaining).
	 */
	public AnnotationsMap add(AnnotationInfo<? extends Annotation> a) {
		if (a == null || ! accept(a))
			return this;
		Class<? extends Annotation> c = a.getAnnotation().annotationType();
		List<AnnotationInfo<? extends Annotation>> l = super.get(c);
		if (l == null) {
			l = new ArrayList<>();
			put(c, l);
		}
		l.add(a);
		return this;
	}

	/**
	 * Overridable method for filtering annotations added to this map.
	 *
	 * @param a The annotation to check.
	 * @return <jk>true</jk> if annotation should be added to this map.
	 */
	public boolean accept(AnnotationInfo<? extends Annotation> a) {
		return true;
	}

	/**
	 * Returns the list of annotations of the specified type.
	 *
	 * @param <T> The annotation type.
	 * @param c The annotation type.
	 * @return The list of annotations, <jk>null</jk> if not found.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AnnotationInfo<? extends Annotation>> List<T> get(Class<T> c) {
		return (List<T>)super.get(c);
	}

	@Override
	public String toString() {
		ObjectMap m = new ObjectMap();
		for (Class<?> k : this.keySet()) {
			ObjectList l = new ObjectList();
			for (AnnotationInfo<?> ai : get(k))
				l.add(ai.toObjectMap());
			m.put(k.getSimpleName(), l);
		}
		return SimpleJson.DEFAULT_READABLE.toString(m);
	}
}