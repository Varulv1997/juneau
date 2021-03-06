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
package org.apache.juneau.html.annotation;

import static org.apache.juneau.html.HtmlSerializer.*;

import org.apache.juneau.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.svl.*;

/**
 * Utility classes and methods for the {@link HtmlConfig @HtmlConfig} annotation.
 */
public class HtmlConfigAnnotation {

	/**
	 * Applies {@link HtmlConfig} annotations to a {@link PropertyStoreBuilder}.
	 */
	public static class Apply extends ConfigApply<HtmlConfig> {

		/**
		 * Constructor.
		 *
		 * @param c The annotation class.
		 * @param vr The resolver for resolving values in annotations.
		 */
		public Apply(Class<HtmlConfig> c, VarResolverSession vr) {
			super(c, vr);
		}

		@Override
		public void apply(AnnotationInfo<HtmlConfig> ai, PropertyStoreBuilder psb, VarResolverSession vr) {
			HtmlConfig a = ai.getAnnotation();
			if (! a.addBeanTypes().isEmpty())
				psb.set(HTML_addBeanTypes, bool(a.addBeanTypes()));
			if (! a.addKeyValueTableHeaders().isEmpty())
				psb.set(HTML_addKeyValueTableHeaders, bool(a.addKeyValueTableHeaders()));
			if (! a.disableDetectLabelParameters().isEmpty())
				psb.set(HTML_disableDetectLabelParameters, bool(a.disableDetectLabelParameters()));
			if (! a.disableDetectLinksInStrings().isEmpty())
				psb.set(HTML_disableDetectLinksInStrings, bool(a.disableDetectLinksInStrings()));
			if (! a.labelParameter().isEmpty())
				psb.set(HTML_labelParameter, string(a.labelParameter()));
			if (! a.uriAnchorText().isEmpty())
				psb.set(HTML_uriAnchorText, string(a.uriAnchorText()));
		}
	}
}