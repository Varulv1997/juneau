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

import static org.apache.juneau.internal.StringUtils.*;
import static org.apache.juneau.rest.RestParamType.*;

import java.lang.reflect.*;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.juneau.jsonschema.annotation.*;
import org.apache.juneau.jsonschema.annotation.Items;
import org.apache.juneau.jsonschema.annotation.Tag;
import org.apache.juneau.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.cp.Messages;
import org.apache.juneau.dto.swagger.*;
import org.apache.juneau.http.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.http.annotation.Contact;
import org.apache.juneau.http.annotation.License;
import org.apache.juneau.internal.*;
import org.apache.juneau.json.*;
import org.apache.juneau.jsonschema.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.util.RestUtils;
import org.apache.juneau.serializer.*;
import org.apache.juneau.svl.*;

/**
 * Default implementation of {@link RestInfoProvider}.
 *
 * <p>
 * Subclasses can override these methods to tailor how HTTP REST resources are documented.
 *
 * <ul class='seealso'>
 * 	<li class='jf'>{@link RestContext#REST_infoProvider}
 * 	<li class='link'>{@doc RestSwagger}
 * </ul>
 */
final class SwaggerGenerator {

//	private final RestRequest req;
	private final VarResolverSession vr;
	private final Locale locale;
	private final RestContext context;
	private final JsonParser jp = JsonParser.create().ignoreUnknownBeanProperties().build();
	private final JsonSchemaGeneratorSession js;
	private final Class<?> c;
	private final Object resource;
	private final Messages mb;

	/**
	 * Constructor.
	 *
	 * @param context The REST class context.
	 * @param vr The variable resolver.
	 * @param locale The locale.
	 */
	public SwaggerGenerator(RestContext context, VarResolverSession vr, Locale locale) {
		this.vr = vr;
		this.locale = locale;
		this.context = context;
		this.js = context.getJsonSchemaGenerator().createSession();
		this.c = context.getResource().getClass();
		this.resource = context.getResource();
		this.mb = context.getMessages().forLocale(locale);
	}

	/**
	 * Returns the localized swagger for this REST resource.
	 *
	 * <p>
	 * Subclasses can override this method to customize the Swagger.
	 *
	 * @return
	 * 	A new Swagger instance.
	 * 	<br>Never <jk>null</jk>.
	 * @throws Exception Error occurred.
	 */
	public Swagger getSwagger() throws Exception {

		ClassInfo rci = ClassInfo.of(resource).resolved();

		rci.getSimpleName();

		// Load swagger JSON from classpath.
		OMap omSwagger = context.getClasspathResource(OMap.class, MediaType.JSON, rci.getSimpleName() + ".json", locale);
		if (omSwagger == null)
			omSwagger = context.getClasspathResource(OMap.class, MediaType.JSON, rci.getSimpleName() + ".json", locale);
		if (omSwagger == null)
			omSwagger = new OMap();

		// Combine it with @Rest(swagger)
		for (Rest rr : rci.getAnnotations(Rest.class)) {

			OMap sInfo = omSwagger.getMap("info", true);

			sInfo
				.ase("title",
					firstNonEmpty(
						sInfo.getString("title"),
						resolve(rr.title())
					)
				)
				.ase("description",
					firstNonEmpty(
						sInfo.getString("description"),
						resolve(rr.description())
					)
				);

			ResourceSwagger r = rr.swagger();

			omSwagger.appendAll(parseMap(r.value(), "@ResourceSwagger(value) on class {0}", c));

			if (! ResourceSwaggerAnnotation.empty(r)) {
				OMap info = omSwagger.getMap("info", true);

				info
					.ase("title", resolve(r.title()))
					.ase("description", resolve(r.description()))
					.ase("version", resolve(r.version()))
					.ase("termsOfService", resolve(r.termsOfService()))
					.ase("contact",
						merge(
							info.getMap("contact"),
							toMap(r.contact(), "@ResourceSwagger(contact) on class {0}", c)
						)
					)
					.ase("license",
						merge(
							info.getMap("license"),
							toMap(r.license(), "@ResourceSwagger(license) on class {0}", c)
						)
					);
			}

			omSwagger
				.ase("externalDocs",
					merge(
						omSwagger.getMap("externalDocs"),
						toMap(r.externalDocs(), "@ResourceSwagger(externalDocs) on class {0}", c)
					)
				)
				.ase("tags",
					merge(
						omSwagger.getList("tags"),
						toList(r.tags(), "@ResourceSwagger(tags) on class {0}", c)
					)
				);
		}

		omSwagger.ase("externalDocs", parseMap(mb.findFirstString("externalDocs"), "Messages/externalDocs on class {0}", c));

		OMap info = omSwagger.getMap("info", true);

		info
			.ase("title", resolve(mb.findFirstString("title")))
			.ase("description", resolve(mb.findFirstString("description")))
			.ase("version", resolve(mb.findFirstString("version")))
			.ase("termsOfService", resolve(mb.findFirstString("termsOfService")))
			.ase("contact", parseMap(mb.findFirstString("contact"), "Messages/contact on class {0}", c))
			.ase("license", parseMap(mb.findFirstString("license"), "Messages/license on class {0}", c));

		if (info.isEmpty())
			omSwagger.remove("info");

		OList
			produces = omSwagger.getList("produces", true),
			consumes = omSwagger.getList("consumes", true);
		if (consumes.isEmpty())
			consumes.addAll(context.getConsumes());
		if (produces.isEmpty())
			produces.addAll(context.getProduces());

		Map<String,OMap> tagMap = new LinkedHashMap<>();
		if (omSwagger.containsKey("tags")) {
			for (OMap om : omSwagger.getList("tags").elements(OMap.class)) {
				String name = om.getString("name");
				if (name == null)
					throw new SwaggerException(null, "Tag definition found without name in swagger JSON.");
				tagMap.put(name, om);
			}
		}

		String s = mb.findFirstString("tags");
		if (s != null) {
			for (OMap m : parseListOrCdl(s, "Messages/tags on class {0}", c).elements(OMap.class)) {
				String name = m.getString("name");
				if (name == null)
					throw new SwaggerException(null, "Tag definition found without name in resource bundle on class {0}", c) ;
				if (tagMap.containsKey(name))
					tagMap.get(name).putAll(m);
				else
					tagMap.put(name, m);
			}
		}

		// Load our existing bean definitions into our session.
		OMap definitions = omSwagger.getMap("definitions", true);
		for (String defId : definitions.keySet())
			js.addBeanDef(defId, new OMap(definitions.getMap(defId)));

		// Iterate through all the @RestMethod methods.
		for (RestMethodContext sm : context.getMethodContexts()) {

			BeanSession bs = sm.createBeanSession();

			Method m = sm.method;
			MethodInfo mi = MethodInfo.of(m);
			RestMethod rm = mi.getLastAnnotation(RestMethod.class);
			String mn = m.getName();

			// Get the operation from the existing swagger so far.
			OMap op = getOperation(omSwagger, sm.getPathPattern(), sm.getHttpMethod().toLowerCase());

			// Add @RestMethod(swagger)
			MethodSwagger ms = rm.swagger();

			op.appendAll(parseMap(ms.value(), "@MethodSwagger(value) on class {0} method {1}", c, m));
			op.ase("operationId",
				firstNonEmpty(
					resolve(ms.operationId()),
					op.getString("operationId"),
					mn
				)
			);
			op.ase("summary",
				firstNonEmpty(
					resolve(ms.summary()),
					resolve(mb.findFirstString(mn + ".summary")),
					op.getString("summary"),
					resolve(rm.summary())
				)
			);
			op.ase("description",
				firstNonEmpty(
					resolve(ms.description()),
					resolve(mb.findFirstString(mn + ".description")),
					op.getString("description"),
					resolve(rm.description())
				)
			);
			op.ase("deprecated",
				firstNonEmpty(
					resolve(ms.deprecated()),
					(m.getAnnotation(Deprecated.class) != null || m.getDeclaringClass().getAnnotation(Deprecated.class) != null) ? "true" : null
				)
			);
			op.ase("tags",
				merge(
					parseListOrCdl(mb.findFirstString(mn + ".tags"), "Messages/tags on class {0} method {1}", c, m),
					parseListOrCdl(ms.tags(), "@MethodSwagger(tags) on class {0} method {1}", c, m)
				)
			);
			op.ase("schemes",
				merge(
					parseListOrCdl(mb.findFirstString(mn + ".schemes"), "Messages/schemes on class {0} method {1}", c, m),
					parseListOrCdl(ms.schemes(), "@MethodSwagger(schemes) on class {0} method {1}", c, m)
				)
			);
			op.ase("consumes",
				firstNonEmpty(
					parseListOrCdl(mb.findFirstString(mn + ".consumes"), "Messages/consumes on class {0} method {1}", c, m),
					parseListOrCdl(ms.consumes(), "@MethodSwagger(consumes) on class {0} method {1}", c, m)
				)
			);
			op.ase("produces",
				firstNonEmpty(
					parseListOrCdl(mb.findFirstString(mn + ".produces"), "Messages/produces on class {0} method {1}", c, m),
					parseListOrCdl(ms.produces(), "@MethodSwagger(produces) on class {0} method {1}", c, m)
				)
			);
			op.ase("parameters",
				merge(
					parseList(mb.findFirstString(mn + ".parameters"), "Messages/parameters on class {0} method {1}", c, m),
					parseList(ms.parameters(), "@MethodSwagger(parameters) on class {0} method {1}", c, m)
				)
			);
			op.ase("responses",
				merge(
					parseMap(mb.findFirstString(mn + ".responses"), "Messages/responses on class {0} method {1}", c, m),
					parseMap(ms.responses(), "@MethodSwagger(responses) on class {0} method {1}", c, m)
				)
			);
			op.ase("externalDocs",
				merge(
					op.getMap("externalDocs"),
					parseMap(mb.findFirstString(mn + ".externalDocs"), "Messages/externalDocs on class {0} method {1}", c, m),
					toMap(ms.externalDocs(), "@MethodSwagger(externalDocs) on class {0} method {1}", c, m)
				)
			);

			if (op.containsKey("tags"))
				for (String tag : op.getList("tags").elements(String.class))
					if (! tagMap.containsKey(tag))
						tagMap.put(tag, OMap.of("name", tag));

			OMap paramMap = new OMap();
			if (op.containsKey("parameters"))
				for (OMap param : op.getList("parameters").elements(OMap.class))
					paramMap.put(param.getString("in") + '.' + ("body".equals(param.getString("in")) ? "body" : param.getString("name")), param);

			// Finally, look for parameters defined on method.
			for (RestMethodParam mp : sm.methodParams) {

				RestParamType in = mp.getParamType();
				ParamInfo mpi = mp.getMethodParamInfo();

				if (in.isAny(BODY, QUERY, FORM_DATA, HEADER, PATH)) {

					String key = in.toString() + '.' + (in == BODY ? "body" : mp.getName());

					OMap param = paramMap.getMap(key, true);

					param.append("in", in);

					if (in != BODY)
						param.append("name", mp.name);

					try {
						if (mpi != null) {
							if (in == BODY) {
								for (Body a : mpi.getAnnotations(Body.class))
									merge(param, a);
							} else if (in == QUERY) {
								for (Query a : mpi.getAnnotations(Query.class))
									merge(param, a);
							} else if (in == FORM_DATA) {
								for (FormData a : mpi.getAnnotations(FormData.class))
									merge(param, a);
							} else if (in == HEADER) {
								for (Header a : mpi.getAnnotations(Header.class))
									merge(param, a);
							} else if (in == PATH) {
								for (Path a : mpi.getAnnotations(Path.class))
									merge(param, a);
							}
						}
					} catch (ParseException e) {
						throw new SwaggerException(e, "Malformed swagger JSON object encountered in {0} class {1} method parameter {2}", in, c, mpi);
					}


					if ((in == BODY || in == PATH) && ! param.containsKeyNotEmpty("required"))
						param.put("required", true);

					if (in == BODY) {
						param.ase("schema", getSchema(param.getMap("schema"), mp.getType(), bs));
						addBodyExamples(sm, param, false, mp.getType());
					} else {
						mergePartSchema(param, getSchema(param.getMap("schema"), mp.getType(), bs));
						addParamExample(sm, param, in, mp.getType());
					}
				}
			}

			if (! paramMap.isEmpty())
				op.put("parameters", paramMap.values());

			OMap responses = op.getMap("responses", true);

			for (ClassInfo eci : mi.getExceptionTypes()) {
				if (eci.hasAnnotation(Response.class)) {
					List<Response> la = eci.getAnnotations(Response.class);
					Set<Integer> codes = getCodes(la, 500);
					for (Response a : la) {
						for (Integer code : codes) {
							OMap om = responses.getMap(String.valueOf(code), true);
							merge(om, a);
							if (! om.containsKey("schema"))
								om.ase("schema", getSchema(om.getMap("schema"), eci.inner(), bs));
						}
					}
					for (MethodInfo ecmi : eci.getAllMethodsParentFirst()) {
						ResponseHeader a = ecmi.getLastAnnotation(ResponseHeader.class);
						if (a == null)
							a = ecmi.getResolvedReturnType().getLastAnnotation(ResponseHeader.class);
						if (a != null && ! isMulti(a)) {
							String ha = a.name();
							for (Integer code : codes) {
								OMap header = responses.getMap(String.valueOf(code), true).getMap("headers", true).getMap(ha, true);
								merge(header, a);
								mergePartSchema(header, getSchema(header, ecmi.getReturnType().innerType(), bs));
							}
						}
					}
				}
			}

			if (mi.hasAnnotation(Response.class) || mi.getResolvedReturnType().hasAnnotation(Response.class)) {
				List<Response> la = mi.getAnnotations(Response.class);
				Set<Integer> codes = getCodes(la, 200);
				for (Response a : la) {
					for (Integer code : codes) {
						OMap om = responses.getMap(String.valueOf(code), true);
						merge(om, a);
						if (! om.containsKey("schema"))
							om.ase("schema", getSchema(om.getMap("schema"), m.getGenericReturnType(), bs));
						addBodyExamples(sm, om, true, m.getGenericReturnType());
					}
				}
				if (mi.getReturnType().hasAnnotation(Response.class)) {
					for (MethodInfo ecmi : mi.getReturnType().getAllMethodsParentFirst()) {
						if (ecmi.hasAnnotation(ResponseHeader.class)) {
							ResponseHeader a = ecmi.getLastAnnotation(ResponseHeader.class);
							String ha = a.name();
							if (! isMulti(a)) {
								for (Integer code : codes) {
									OMap header = responses.getMap(String.valueOf(code), true).getMap("headers", true).getMap(ha, true);
									merge(header, a);
									mergePartSchema(header, getSchema(header, ecmi.getReturnType().innerType(), bs));
								}
							}
						}
					}
				}
			} else if (m.getGenericReturnType() != void.class) {
				OMap om = responses.getMap("200", true);
				if (! om.containsKey("schema"))
					om.ase("schema", getSchema(om.getMap("schema"), m.getGenericReturnType(), bs));
				addBodyExamples(sm, om, true, m.getGenericReturnType());
			}

			// Finally, look for @ResponseHeader parameters defined on method.
			for (RestMethodParam mp : sm.methodParams) {

				RestParamType in = mp.getParamType();
				ParamInfo mpi = mp.getMethodParamInfo();

				if (in == RESPONSE_HEADER) {
					List<ResponseHeader> la = mpi.getAnnotations(ResponseHeader.class);
					Set<Integer> codes = getCodes2(la, 200);
					for (ResponseHeader a : la) {
						if (! isMulti(a)) {
							for (Integer code : codes) {
								OMap header = responses.getMap(String.valueOf(code), true).getMap("headers", true).getMap(mp.name, true);
								merge(header, a);
								mergePartSchema(header, getSchema(header, Value.getParameterType(mp.type), bs));
							}
						}
					}

				} else if (in == RESPONSE) {
					List<Response> la = mpi.getAnnotations(Response.class);
					Set<Integer> codes = getCodes(la, 200);
					for (Response a : la) {
						for (Integer code : codes) {
							OMap response = responses.getMap(String.valueOf(code), true);
							merge(response, a);
						}
					}
					Type type = Value.getParameterType(mp.type);
					if (type != null) {
						for (String code : responses.keySet()) {
							OMap om = responses.getMap(code);
							if (! om.containsKey("schema"))
								om.ase("schema", getSchema(om.getMap("schema"), type, bs));
						}
					}
				}
			}

			// Add default response descriptions.
			for (Map.Entry<String,Object> e : responses.entrySet()) {
				String key = e.getKey();
				OMap val = responses.getMap(key);
				if (StringUtils.isDecimal(key))
					val.appendIf(false, true, true, "description", RestUtils.getHttpResponseText(Integer.parseInt(key)));
			}

			if (responses.isEmpty())
				op.remove("responses");
			else
				op.put("responses", new TreeMap<>(responses));

			if (! op.containsKey("consumes")) {
				List<MediaType> mConsumes = sm.supportedContentTypes;
				if (! mConsumes.equals(consumes))
					op.put("consumes", mConsumes);
			}

			if (! op.containsKey("produces")) {
				List<MediaType> mProduces = sm.supportedAcceptTypes;
				if (! mProduces.equals(produces))
					op.put("produces", mProduces);
			}
		}

		if (js.getBeanDefs() != null)
			for (Map.Entry<String,OMap> e : js.getBeanDefs().entrySet())
				definitions.put(e.getKey(), fixSwaggerExtensions(e.getValue()));
		if (definitions.isEmpty())
			omSwagger.remove("definitions");

		if (! tagMap.isEmpty())
			omSwagger.put("tags", tagMap.values());

		if (consumes.isEmpty())
			omSwagger.remove("consumes");
		if (produces.isEmpty())
			omSwagger.remove("produces");

//		try {
//			if (! omSwagger.isEmpty())
//				assertNoEmpties(omSwagger);
//		} catch (SwaggerException e1) {
//			System.err.println(omSwagger.toString(SimpleJsonSerializer.DEFAULT_READABLE));
//			throw e1;
//		}

		try {
			String swaggerJson = SimpleJsonSerializer.DEFAULT_READABLE.toString(omSwagger);
//			System.err.println(swaggerJson);
			return jp.parse(swaggerJson, Swagger.class);
		} catch (Exception e) {
			throw new RestServletException(e, "Error detected in swagger.");
		}
	}

	//=================================================================================================================
	// Utility methods
	//=================================================================================================================

	private boolean isMulti(ResponseHeader h) {
		if ("*".equals(h.name()) || "*".equals(h.value()))
			return true;
		return false;
	}

	private OMap resolve(OMap om) throws ParseException {
		OMap om2 = null;
		if (om.containsKey("_value")) {
			om = om.modifiable();
			om2 = parseMap(om.remove("_value"));
		} else {
			om2 = new OMap();
		}
		for (Map.Entry<String,Object> e : om.entrySet()) {
			Object val = e.getValue();
			if (val instanceof OMap) {
				val = resolve((OMap)val);
			} else if (val instanceof OList) {
				val = resolve((OList) val);
			} else if (val instanceof String) {
				val = resolve(val.toString());
			}
			om2.put(e.getKey(), val);
		}
		return om2;
	}

	private OList resolve(OList om) throws ParseException {
		OList ol2 = new OList();
		for (Object val : om) {
			if (val instanceof OMap) {
				val = resolve((OMap)val);
			} else if (val instanceof OList) {
				val = resolve((OList) val);
			} else if (val instanceof String) {
				val = resolve(val.toString());
			}
			ol2.add(val);
		}
		return ol2;
	}

	private String resolve(String[]...s) {
		for (String[] ss : s) {
			if (ss.length != 0)
				return resolve(joinnl(ss));
		}
		return null;
	}

	private String resolve(String s) {
		if (s == null)
			return null;
		return vr.resolve(s.trim());
	}

	private OMap parseMap(String[] o, String location, Object...args) throws ParseException {
		if (o.length == 0)
			return OMap.EMPTY_MAP;
		try {
			return parseMap(o);
		} catch (ParseException e) {
			throw new SwaggerException(e, "Malformed swagger JSON object encountered in " + location + ".", args);
		}
	}

	private OMap parseMap(String o, String location, Object...args) throws ParseException {
		try {
			return parseMap(o);
		} catch (ParseException e) {
			throw new SwaggerException(e, "Malformed swagger JSON object encountered in " + location + ".", args);
		}
	}

	private OMap parseMap(Object o) throws ParseException {
		if (o == null)
			return null;
		if (o instanceof String[])
			o = joinnl((String[])o);
		if (o instanceof String) {
			String s = o.toString();
			if (s.isEmpty())
				return null;
			s = resolve(s);
			if ("IGNORE".equalsIgnoreCase(s))
				return OMap.of("ignore", true);
			if (! isJsonObject(s, true))
				s = "{" + s + "}";
			return OMap.ofJson(s);
		}
		if (o instanceof OMap)
			return (OMap)o;
		throw new SwaggerException(null, "Unexpected data type ''{0}''.  Expected OMap or String.", o.getClass().getName());
	}

	private OList parseList(Object o, String location, Object...locationArgs) throws ParseException {
		try {
			if (o == null)
				return null;
			String s = (o instanceof String[] ? joinnl((String[])o) : o.toString());
			if (s.isEmpty())
				return null;
			s = resolve(s);
			if (! isJsonArray(s, true))
				s = "[" + s + "]";
			return OList.ofJson(s);
		} catch (ParseException e) {
			throw new SwaggerException(e, "Malformed swagger JSON array encountered in "+location+".", locationArgs);
		}
	}

	private OList parseListOrCdl(Object o, String location, Object...locationArgs) throws ParseException {
		try {
			if (o == null)
				return null;
			String s = (o instanceof String[] ? joinnl((String[])o) : o.toString());
			if (s.isEmpty())
				return null;
			s = resolve(s);
			return StringUtils.parseListOrCdl(s);
		} catch (ParseException e) {
			throw new SwaggerException(e, "Malformed swagger JSON array encountered in "+location+".", locationArgs);
		}
	}

	private OMap newMap(OMap om, String[] value, String location, Object...locationArgs) throws ParseException {
		if (value.length == 0)
			return om == null ? new OMap() : om;
			OMap om2 = parseMap(joinnl(value), location, locationArgs);
		if (om == null)
			return om2;
		return om.appendAll(om2);
	}

	private OMap merge(OMap...maps) {
		OMap m = maps[0];
		for (int i = 1; i < maps.length; i++) {
			if (maps[i] != null) {
				if (m == null)
					m = new OMap();
				m.putAll(maps[i]);
			}
		}
		return m;
	}

	private OList merge(OList...lists) {
		OList l = lists[0];
		for (int i = 1; i < lists.length; i++) {
			if (lists[i] != null) {
				if (l == null)
					l = new OList();
				l.addAll(lists[i]);
			}
		}
		return l;
	}

	@SafeVarargs
	private final <T> T firstNonEmpty(T...t) {
		return ObjectUtils.firstNonEmpty(t);
	}

	private OMap toMap(ExternalDocs a, String location, Object...locationArgs) throws ParseException {
		if (ExternalDocsAnnotation.empty(a))
			return null;
		OMap om = newMap(new OMap(), a.value(), location, locationArgs)
			.ase("description", resolve(joinnl(a.description())))
			.ase("url", resolve(a.url()));
		return nullIfEmpty(om);
	}

	private OMap toMap(Contact a, String location, Object...locationArgs) throws ParseException {
		if (ContactAnnotation.empty(a))
			return null;
		OMap om = newMap(new OMap(), a.value(), location, locationArgs)
			.ase("name", resolve(a.name()))
			.ase("url", resolve(a.url()))
			.ase("email", resolve(a.email()));
		return nullIfEmpty(om);
	}

	private OMap toMap(License a, String location, Object...locationArgs) throws ParseException {
		if (LicenseAnnotation.empty(a))
			return null;
		OMap om = newMap(new OMap(), a.value(), location, locationArgs)
			.ase("name", resolve(a.name()))
			.ase("url", resolve(a.url()));
		return nullIfEmpty(om);
	}

	private OMap toMap(Tag a, String location, Object...locationArgs) throws ParseException {
		OMap om = newMap(new OMap(), a.value(), location, locationArgs);
		om
			.ase("name", resolve(a.name()))
			.ase("description", resolve(joinnl(a.description())))
			.asn("externalDocs", merge(om.getMap("externalDocs"), toMap(a.externalDocs(), location, locationArgs)));
		return nullIfEmpty(om);
	}

	private OList toList(Tag[] aa, String location, Object...locationArgs) throws ParseException {
		if (aa.length == 0)
			return null;
		OList ol = new OList();
		for (Tag a : aa)
			ol.add(toMap(a, location, locationArgs));
		return nullIfEmpty(ol);
	}

	private OMap getSchema(OMap schema, Type type, BeanSession bs) throws Exception {

		if (type == Swagger.class)
			return null;

		schema = newMap(schema);

		ClassMeta<?> cm = bs.getClassMeta(type);

		if (schema.getBoolean("ignore", false))
			return null;

		if (schema.containsKey("type") || schema.containsKey("$ref"))
			return schema;

		OMap om = fixSwaggerExtensions(schema.appendAll(js.getSchema(cm)));

		return nullIfEmpty(om);
	}

	/**
	 * Replaces non-standard JSON-Schema attributes with standard Swagger attributes.
	 */
	private OMap fixSwaggerExtensions(OMap om) {
		om
			.asn("discriminator", om.remove("x-discriminator"))
			.asn("readOnly", om.remove("x-readOnly"))
			.asn("xml", om.remove("x-xml"))
			.asn("externalDocs", om.remove("x-externalDocs"))
			.asn("example", om.remove("x-example"));
		return nullIfEmpty(om);
	}

	private void addBodyExamples(RestMethodContext sm, OMap piri, boolean response, Type type) throws Exception {

		String sex = piri.getString("x-example");

		if (sex == null) {
			OMap schema = resolveRef(piri.getMap("schema"));
			if (schema != null)
				sex = schema.getString("example", schema.getString("x-example"));
		}

		if (isEmpty(sex))
			return;

		Object example = null;
		if (isJson(sex)) {
			example = jp.parse(sex, type);
		} else {
			ClassMeta<?> cm = js.getClassMeta(type);
			if (cm.hasStringMutater()) {
				example = cm.getStringMutater().mutate(sex);
			}
		}

		String examplesKey = response ? "examples" : "x-examples";  // Parameters don't have an examples attribute.

		OMap examples = piri.getMap(examplesKey);
		if (examples == null)
			examples = new OMap();

		List<MediaType> mediaTypes = response ? sm.getSerializers().getSupportedMediaTypes() : sm.getParsers().getSupportedMediaTypes();

		for (MediaType mt : mediaTypes) {
			if (mt != MediaType.HTML) {
				Serializer s2 = sm.getSerializers().getSerializer(mt);
				if (s2 != null) {
					SerializerSessionArgs args =
						SerializerSessionArgs
							.create()
							.locale(locale)
							.mediaType(mt)
							.useWhitespace(true)
						;
					try {
						String eVal = s2.createSession(args).serializeToString(example);
						examples.put(s2.getPrimaryMediaType().toString(), eVal);
					} catch (Exception e) {
						System.err.println("Could not serialize to media type ["+mt+"]: " + e.getLocalizedMessage());  // NOT DEBUG
					}
				}
			}
		}

		if (! examples.isEmpty())
			piri.put(examplesKey, examples);
	}

	private void addParamExample(RestMethodContext sm, OMap piri, RestParamType in, Type type) throws Exception {

		String s = piri.getString("x-example");

		if (isEmpty(s))
			return;

		OMap examples = piri.getMap("x-examples");
		if (examples == null)
			examples = new OMap();

		String paramName = piri.getString("name");

		if (in == QUERY)
			s = "?" + urlEncodeLax(paramName) + "=" + urlEncodeLax(s);
		else if (in == FORM_DATA)
			s = paramName + "=" + s;
		else if (in == HEADER)
			s = paramName + ": " + s;
		else if (in == PATH)
			s = sm.getPathPattern().replace("{"+paramName+"}", urlEncodeLax(s));

		examples.put("example", s);

		if (! examples.isEmpty())
			piri.put("x-examples", examples);
	}


	private OMap resolveRef(OMap m) {
		if (m == null)
			return null;
		if (m.containsKey("$ref") && js.getBeanDefs() != null) {
			String ref = m.getString("$ref");
			if (ref.startsWith("#/definitions/"))
				return js.getBeanDefs().get(ref.substring(14));
		}
		return m;
	}

	private OMap getOperation(OMap om, String path, String httpMethod) {
		if (! om.containsKey("paths"))
			om.put("paths", new OMap());
		om = om.getMap("paths");
		if (! om.containsKey(path))
			om.put(path, new OMap());
		om = om.getMap(path);
		if (! om.containsKey(httpMethod))
			om.put(httpMethod, new OMap());
		return om.getMap(httpMethod);
	}

	private static OMap newMap(OMap om) {
		if (om == null)
			return new OMap();
		return om.modifiable();
	}

	private OMap merge(OMap om, Body a) throws ParseException {
		if (BodyAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.value().length > 0)
			om.putAll(parseMap(a.value()));
		if (a.api().length > 0)
			om.putAll(parseMap(a.api()));
		return om
			.ase("description", resolve(a.description(), a.d()))
			.ase("x-example", resolve(a.example(), a.ex()))
			.ase("x-examples", parseMap(a.examples()), parseMap(a.exs()))
			.asf("required", a.required() || a.r())
			.ase("schema", merge(om.getMap("schema"), a.schema()))
		;
	}

	private OMap merge(OMap om, Query a) throws ParseException {
		if (QueryAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.api().length > 0)
			om.putAll(parseMap(a.api()));
		return om
			.asf("allowEmptyValue", a.allowEmptyValue() || a.aev())
			.ase("collectionFormat", a.collectionFormat(), a.cf())
			.ase("default", joinnl(a._default(), a.df()))
			.ase("description", resolve(a.description(), a.d()))
			.ase("enum", toSet(a._enum()), toSet(a.e()))
			.ase("x-example", resolve(a.example(), a.ex()))
			.asf("exclusiveMaximum", a.exclusiveMaximum() || a.emax())
			.asf("exclusiveMinimum", a.exclusiveMinimum() || a.emin())
			.ase("format", a.format(), a.f())
			.ase("items", merge(om.getMap("items"), a.items()))
			.ase("maximum", a.maximum(), a.max())
			.asmo("maxItems", a.maxItems(), a.maxi())
			.asmo("maxLength", a.maxLength(), a.maxl())
			.ase("minimum", a.minimum(), a.min())
			.asmo("minItems", a.minItems(), a.mini())
			.asmo("minLength", a.minLength(), a.minl())
			.ase("multipleOf", a.multipleOf(), a.mo())
			.ase("pattern", a.pattern(), a.p())
			.asf("required", a.required() || a.r())
			.ase("type", a.type(), a.t())
			.asf("uniqueItems", a.uniqueItems() || a.ui())
		;
	}

	private OMap merge(OMap om, FormData a) throws ParseException {
		if (FormDataAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.api().length > 0)
			om.putAll(parseMap(a.api()));
		return om
			.asf("allowEmptyValue", a.allowEmptyValue() || a.aev())
			.ase("collectionFormat", a.collectionFormat(), a.cf())
			.ase("default", joinnl(a._default(), a.df()))
			.ase("description", resolve(a.description(), a.d()))
			.ase("enum", toSet(a._enum()), toSet(a.e()))
			.ase("x-example", resolve(a.example(), a.ex()))
			.asf("exclusiveMaximum", a.exclusiveMaximum() || a.emax())
			.asf("exclusiveMinimum", a.exclusiveMinimum() || a.emin())
			.ase("format", a.format(), a.f())
			.ase("items", merge(om.getMap("items"), a.items()))
			.ase("maximum", a.maximum(), a.max())
			.asmo("maxItems", a.maxItems(), a.maxi())
			.asmo("maxLength", a.maxLength(), a.maxl())
			.ase("minimum", a.minimum(), a.min())
			.asmo("minItems", a.minItems(), a.mini())
			.asmo("minLength", a.minLength(), a.minl())
			.ase("multipleOf", a.multipleOf(), a.mo())
			.ase("pattern", a.pattern(), a.p())
			.asf("required", a.required())
			.ase("type", a.type(), a.t())
			.asf("uniqueItems", a.uniqueItems() || a.ui())
		;
	}

	private OMap merge(OMap om, Header a) throws ParseException {
		if (HeaderAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.api().length > 0)
			om.putAll(parseMap(a.api()));
		return om
			.ase("collectionFormat", a.collectionFormat(), a.cf())
			.ase("default", joinnl(a._default(), a.df()))
			.ase("description", resolve(a.description(), a.d()))
			.ase("enum", toSet(a._enum()), toSet(a.e()))
			.ase("x-example", resolve(a.example(), a.ex()))
			.asf("exclusiveMaximum", a.exclusiveMaximum() || a.emax())
			.asf("exclusiveMinimum", a.exclusiveMinimum() || a.emin())
			.ase("format", a.format(), a.f())
			.ase("items", merge(om.getMap("items"), a.items()))
			.ase("maximum", a.maximum(), a.max())
			.asmo("maxItems", a.maxItems(), a.maxi())
			.asmo("maxLength", a.maxLength(), a.maxl())
			.ase("minimum", a.minimum(), a.min())
			.asmo("minItems", a.minItems(), a.mini())
			.asmo("minLength", a.minLength(), a.minl())
			.ase("multipleOf", a.multipleOf(), a.mo())
			.ase("pattern", a.pattern(), a.p())
			.asf("required", a.required() || a.r())
			.ase("type", a.type(), a.t())
			.asf("uniqueItems", a.uniqueItems() || a.ui())
		;
	}

	private OMap merge(OMap om, Path a) throws ParseException {
		if (PathAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.api().length > 0)
			om.putAll(parseMap(a.api()));
		return om
			.ase("collectionFormat", a.collectionFormat(), a.cf())
			.ase("description", resolve(a.description(), a.d()))
			.ase("enum", toSet(a._enum()), toSet(a.e()))
			.ase("x-example", resolve(a.example(), a.ex()))
			.asf("exclusiveMaximum", a.exclusiveMaximum() || a.emax())
			.asf("exclusiveMinimum", a.exclusiveMinimum() || a.emin())
			.ase("format", a.format(), a.f())
			.ase("items", merge(om.getMap("items"), a.items()))
			.ase("maximum", a.maximum(), a.max())
			.asmo("maxItems", a.maxItems(), a.maxi())
			.asmo("maxLength", a.maxLength(), a.maxl())
			.ase("minimum", a.minimum(), a.min())
			.asmo("minItems", a.minItems(), a.mini())
			.asmo("minLength", a.minLength(), a.minl())
			.ase("multipleOf", a.multipleOf(), a.mo())
			.ase("pattern", a.pattern(), a.p())
			.ase("type", a.type(), a.t())
			.asf("uniqueItems", a.uniqueItems() || a.ui())
		;
	}

	private OMap merge(OMap om, Schema a) throws ParseException {
		if (SchemaAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.value().length > 0)
			om.putAll(parseMap(a.value()));
		return om
			.ase("additionalProperties", toOMap(a.additionalProperties()))
			.ase("allOf", joinnl(a.allOf()))
			.ase("collectionFormat", a.collectionFormat(), a.cf())
			.ase("default", joinnl(a._default(), a.df()))
			.ase("discriminator", a.discriminator())
			.ase("description", resolve(a.description()), resolve(a.d()))
			.ase("enum", toSet(a._enum()), toSet(a.e()))
			.ase("x-example", resolve(a.example()), resolve(a.ex()))
			.ase("examples", parseMap(a.examples()), parseMap(a.exs()))
			.asf("exclusiveMaximum", a.exclusiveMaximum() || a.emax())
			.asf("exclusiveMinimum", a.exclusiveMinimum() || a.emin())
			.ase("externalDocs", merge(om.getMap("externalDocs"), a.externalDocs()))
			.ase("format", a.format(), a.f())
			.ase("ignore", a.ignore() ? "true" : null)
			.ase("items", merge(om.getMap("items"), a.items()))
			.ase("maximum", a.maximum(), a.max())
			.asmo("maxItems", a.maxItems(), a.maxi())
			.asmo("maxLength", a.maxLength(), a.maxl())
			.asmo("maxProperties", a.maxProperties(), a.maxp())
			.ase("minimum", a.minimum(), a.min())
			.asmo("minItems", a.minItems(), a.mini())
			.asmo("minLength", a.minLength(), a.minl())
			.asmo("minProperties", a.minProperties(), a.minp())
			.ase("multipleOf", a.multipleOf(), a.mo())
			.ase("pattern", a.pattern(), a.p())
			.ase("properties", toOMap(a.properties()))
			.asf("readOnly", a.readOnly() || a.ro())
			.asf("required", a.required() || a.r())
			.ase("title", a.title())
			.ase("type", a.type(), a.t())
			.asf("uniqueItems", a.uniqueItems() || a.ui())
			.ase("xml", joinnl(a.xml()))
			.ase("$ref", a.$ref())
		;
	}

	private OMap merge(OMap om, ExternalDocs a) throws ParseException {
		if (ExternalDocsAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.value().length > 0)
			om.putAll(parseMap(a.value()));
		return om
			.ase("description", resolve(a.description()))
			.ase("url", a.url())
		;
	}

	private OMap merge(OMap om, Items a) throws ParseException {
		if (ItemsAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.value().length > 0)
			om.putAll(parseMap(a.value()));
		return om
			.ase("collectionFormat", a.collectionFormat(), a.cf())
			.ase("default", joinnl(a._default(), a.df()))
			.ase("enum", toSet(a._enum()), toSet(a.e()))
			.ase("format", a.format(), a.f())
			.asf("exclusiveMaximum", a.exclusiveMaximum() || a.emax())
			.asf("exclusiveMinimum", a.exclusiveMinimum() || a.emin())
			.ase("items", merge(om.getMap("items"), a.items()))
			.ase("maximum", a.maximum(), a.max())
			.asmo("maxItems", a.maxItems(), a.maxi())
			.asmo("maxLength", a.maxLength(), a.maxl())
			.ase("minimum", a.minimum(), a.min())
			.asmo("minItems", a.minItems(), a.mini())
			.asmo("minLength", a.minLength(), a.minl())
			.ase("multipleOf", a.multipleOf(), a.mo())
			.ase("pattern", a.pattern(), a.p())
			.asf("uniqueItems", a.uniqueItems() || a.ui())
			.ase("type", a.type(), a.t())
			.ase("$ref", a.$ref())
		;
	}

	private OMap merge(OMap om, SubItems a) throws ParseException {
		if (SubItemsAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.value().length > 0)
			om.putAll(parseMap(a.value()));
		return om
			.ase("collectionFormat", a.collectionFormat(), a.cf())
			.ase("default", joinnl(a._default(), a.df()))
			.ase("enum", toSet(a._enum()), toSet(a.e()))
			.asf("exclusiveMaximum", a.exclusiveMaximum() || a.emax())
			.asf("exclusiveMinimum", a.exclusiveMinimum() || a.emin())
			.ase("format", a.format(), a.f())
			.ase("items", toOMap(a.items()))
			.ase("maximum", a.maximum(), a.max())
			.asmo("maxItems", a.maxItems(), a.maxi())
			.asmo("maxLength", a.maxLength(), a.maxl())
			.ase("minimum", a.minimum(), a.min())
			.asmo("minItems", a.minItems(), a.mini())
			.asmo("minLength", a.minLength(), a.minl())
			.ase("multipleOf", a.multipleOf(), a.mo())
			.ase("pattern", a.pattern(), a.p())
			.ase("type", a.type(), a.t())
			.asf("uniqueItems", a.uniqueItems() || a.ui())
			.ase("$ref", a.$ref())
		;
	}

	private OMap merge(OMap om, Response a) throws ParseException {
		if (ResponseAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.api().length > 0)
			om.putAll(parseMap(a.api()));
		return om
			.ase("description", resolve(a.description(), a.d()))
			.ase("x-example", resolve(a.example(), a.ex()))
			.ase("examples", parseMap(a.examples()), parseMap(a.exs()))
			.ase("headers", merge(om.getMap("headers"), a.headers()))
			.ase("schema", merge(om.getMap("schema"), a.schema()))
		;
	}

	private OMap merge(OMap om, ResponseHeader[] a) throws ParseException {
		if (a.length == 0)
			return om;
		om = newMap(om);
		for (ResponseHeader aa : a) {
			String name = StringUtils.firstNonEmpty(aa.name(), aa.value());
			if (isEmpty(name))
				throw new RuntimeException("@ResponseHeader used without name or value.");
			om.getMap(name, true).putAll(merge(null, aa));
		}
		return om;
	}

	private OMap merge(OMap om, ResponseHeader a) throws ParseException {
		if (ResponseHeaderAnnotation.empty(a))
			return om;
		om = newMap(om);
		if (a.api().length > 0)
			om.putAll(parseMap(a.api()));
		return om
			.ase("collectionFormat", a.collectionFormat(), a.cf())
			.ase("default", joinnl(a._default(), a.df()))
			.ase("description", resolve(a.description(), a.d()))
			.ase("enum", toSet(a._enum()), toSet(a.e()))
			.ase("x-example", resolve(a.example(), a.ex()))
			.asf("exclusiveMaximum", a.exclusiveMaximum() || a.emax())
			.asf("exclusiveMinimum", a.exclusiveMinimum() || a.emin())
			.ase("format", a.format(), a.f())
			.ase("items", merge(om.getMap("items"), a.items()))
			.ase("maximum", a.maximum(), a.max())
			.asmo("maxItems", a.maxItems(), a.maxi())
			.asmo("maxLength", a.maxLength(), a.maxl())
			.ase("minimum", a.minimum(), a.min())
			.asmo("minItems", a.minItems(), a.mini())
			.asmo("minLength", a.minLength(), a.minl())
			.ase("multipleOf", a.multipleOf(), a.mo())
			.ase("pattern", a.pattern(), a.p())
			.ase("type", a.type(), a.t())
			.asf("uniqueItems", a.uniqueItems() || a.ui())
			.ase("$ref", a.$ref())
		;
	}

	private OMap mergePartSchema(OMap param, OMap schema) {
		if (schema != null) {
			param
				.appendIf(false, true, true, "collectionFormat", schema.remove("collectionFormat"))
				.appendIf(false, true, true, "default", schema.remove("default"))
				.appendIf(false, true, true, "description", schema.remove("enum"))
				.appendIf(false, true, true, "enum", schema.remove("enum"))
				.appendIf(false, true, true, "x-example", schema.remove("x-example"))
				.appendIf(false, true, true, "exclusiveMaximum", schema.remove("exclusiveMaximum"))
				.appendIf(false, true, true, "exclusiveMinimum", schema.remove("exclusiveMinimum"))
				.appendIf(false, true, true, "format", schema.remove("format"))
				.appendIf(false, true, true, "items", schema.remove("items"))
				.appendIf(false, true, true, "maximum", schema.remove("maximum"))
				.appendIf(false, true, true, "maxItems", schema.remove("maxItems"))
				.appendIf(false, true, true, "maxLength", schema.remove("maxLength"))
				.appendIf(false, true, true, "minimum", schema.remove("minimum"))
				.appendIf(false, true, true, "minItems", schema.remove("minItems"))
				.appendIf(false, true, true, "minLength", schema.remove("minLength"))
				.appendIf(false, true, true, "multipleOf", schema.remove("multipleOf"))
				.appendIf(false, true, true, "pattern", schema.remove("pattern"))
				.appendIf(false, true, true, "required", schema.remove("required"))
				.appendIf(false, true, true, "type", schema.remove("type"))
				.appendIf(false, true, true, "uniqueItems", schema.remove("uniqueItems"));

			if ("object".equals(param.getString("type")) && ! schema.isEmpty())
				param.put("schema", schema);
		}

		return param;
	}



	private OMap toOMap(String[] ss) throws ParseException {
		if (ss.length == 0)
			return null;
		String s = joinnl(ss);
		if (s.isEmpty())
			return null;
		if (! isJsonObject(s, true))
			s = "{" + s + "}";
		s = resolve(s);
		return OMap.ofJson(s);
	}

	private Set<String> toSet(String[] ss) throws ParseException {
		if (ss.length == 0)
			return null;
		String s = joinnl(ss);
		if (s.isEmpty())
			return null;
		s = resolve(s);
		Set<String> set = ASet.of();
		for (Object o : StringUtils.parseListOrCdl(s))
			set.add(o.toString());
		return set;
	}

	static String joinnl(String[]...s) {
		for (String[] ss : s) {
			if (ss.length != 0)
			return StringUtils.joinnl(ss).trim();
		}
		return "";
	}

	private static Set<Integer> getCodes(List<Response> la, Integer def) {
		Set<Integer> codes = new TreeSet<>();
		for (Response a : la) {
			for (int i : a.value())
				codes.add(i);
			for (int i : a.code())
				codes.add(i);
		}
		if (codes.isEmpty() && def != null)
			codes.add(def);
		return codes;
	}

	private static Set<Integer> getCodes2(List<ResponseHeader> la, Integer def) {
		Set<Integer> codes = new TreeSet<>();
		for (ResponseHeader a : la) {
			for (int i : a.code())
				codes.add(i);
		}
		if (codes.isEmpty() && def != null)
			codes.add(def);
		return codes;
	}

	private static OMap nullIfEmpty(OMap m) {
		return (m == null || m.isEmpty() ? null : m);
	}

	private static OList nullIfEmpty(OList l) {
		return (l == null || l.isEmpty() ? null : l);
	}
}
