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

import java.io.*;
import java.nio.charset.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.juneau.*;
import org.apache.juneau.encoders.*;
import org.apache.juneau.http.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.serializer.*;

/**
 * Represents an HTTP response for a REST resource.
 * 
 * <p>
 * Essentially an extended {@link HttpServletResponse} with some special convenience methods that allow you to easily
 * output POJOs as responses.
 * 
 * <p>
 * Since this class extends {@link HttpServletResponse}, developers are free to use these convenience methods, or
 * revert to using lower level methods like any other servlet response.
 * 
 * <h5 class='section'>Example:</h5>
 * <p class='bcode'>
 * 	<ja>@RestMethod</ja>(name=<jsf>GET</jsf>)
 * 	<jk>public void</jk> doGet(RestRequest req, RestResponse res) {
 * 		res.setOutput(<js>"Simple string response"</js>);
 * 	}
 * </p>
 * 
 * 
 * <h5 class='section'>Documentation:</h5>
 * <ul>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-rest-server.RestResponse">Overview &gt; RestResponse</a>
 * </ul>
 */
public final class RestResponse extends HttpServletResponseWrapper {

	private final RestRequest request;
	private RestJavaMethod restJavaMethod;
	private Object output;                       // The POJO being sent to the output.
	private boolean isNullOutput;                // The output is null (as opposed to not being set at all)
	private RequestProperties properties;                // Response properties
	private ServletOutputStream os;
	private PrintWriter w;
	private HtmlDocBuilder htmlDocBuilder;

	/**
	 * Constructor.
	 */
	RestResponse(RestContext context, RestRequest req, HttpServletResponse res) {
		super(res);
		this.request = req;

		for (Map.Entry<String,Object> e : context.getDefaultResponseHeaders().entrySet())
			setHeader(e.getKey(), StringUtils.toString(e.getValue()));

		try {
			String passThroughHeaders = req.getHeader("x-response-headers");
			if (passThroughHeaders != null) {
				HttpPartParser p = context.getPartParser();
				ObjectMap m = p.parse(HttpPartType.HEADER, passThroughHeaders, context.getBeanContext().getClassMeta(ObjectMap.class));
				for (Map.Entry<String,Object> e : m.entrySet())
					setHeader(e.getKey(), e.getValue().toString());
			}
		} catch (Exception e1) {
			throw new RestException(SC_BAD_REQUEST, "Invalid format for header 'x-response-headers'.  Must be in URL-encoded format.").initCause(e1);
		}
	}

	/*
	 * Called from RestServlet after a match has been made but before the guard or method invocation.
	 */
	final void init(RestJavaMethod rjm, RequestProperties properties) {
		this.restJavaMethod = rjm;
		this.properties = properties;

		// Find acceptable charset
		String h = request.getHeader("accept-charset");
		String charset = null;
		if (h == null)
			charset = rjm.defaultCharset;
		else for (MediaTypeRange r : MediaTypeRange.parse(h)) {
			if (r.getQValue() > 0) {
				MediaType mt = r.getMediaType();
				if (mt.getType().equals("*"))
					charset = rjm.defaultCharset;
				else if (Charset.isSupported(mt.getType()))
					charset = mt.getType();
				if (charset != null)
					break;
			}
		}

		if (charset == null)
			throw new RestException(SC_NOT_ACCEPTABLE, "No supported charsets in header ''Accept-Charset'': ''{0}''", request.getHeader("Accept-Charset"));
		super.setCharacterEncoding(charset);
	}

	/**
	 * Gets the serializer group for the response.
	 * 
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-rest-server.Serializers">Overview &gt; Serializers</a>
	 * </ul>
	 * 
	 * @return The serializer group for the response.
	 */
	public SerializerGroup getSerializers() {
		return restJavaMethod.serializers;
	}

	/**
	 * Returns the media types that are valid for <code>Accept</code> headers on the request.
	 * 
	 * @return The set of media types registered in the parser group of this request.
	 */
	public List<MediaType> getSupportedMediaTypes() {
		return restJavaMethod.supportedAcceptTypes;
	}

	/**
	 * Returns the codings that are valid for <code>Accept-Encoding</code> and <code>Content-Encoding</code> headers on
	 * the request.
	 * 
	 * @return The set of media types registered in the parser group of this request.
	 * @throws RestServletException
	 */
	public List<String> getSupportedEncodings() throws RestServletException {
		return restJavaMethod.encoders.getSupportedEncodings();
	}

	/**
	 * Sets the HTTP output on the response.
	 * 
	 * <p>
	 * The object type can be anything allowed by the registered response handlers.
	 * 
	 * <p>
	 * Calling this method is functionally equivalent to returning the object in the REST Java method.
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<ja>@RestMethod</ja>(..., path=<js>"/example2/{personId}"</js>)
	 * 	<jk>public void</jk> doGet2(RestResponse res, <ja>@Path</ja> UUID personId) {
	 * 		Person p = getPersonById(personId);
	 * 		res.setOutput(p);
	 * 	}
	 * </p>
	 * 
	 * <h5 class='section'>Notes:</h5>
	 * <ul>
	 * 	<li>Calling this method with a <jk>null</jk> value is NOT the same as not calling this method at all.
	 * 		<br>A <jk>null</jk> output value means we want to serialize <jk>null</jk> as a response (e.g. as a JSON <code>null</code>).
	 * 		<br>Not calling this method or returning a value means you're handing the response yourself via the underlying stream or writer.
	 * 		<br>This distinction affects the {@link #hasOutput()} method behavior.
	 * </ul>
	 * 
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jf'>{@link RestContext#REST_responseHandlers}
	 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-rest-server.MethodReturnTypes">Overview &gt; Method Return Types</a>
	 * </ul>
	 * 
	 * @param output The output to serialize to the connection.
	 * @return This object (for method chaining).
	 */
	public RestResponse setOutput(Object output) {
		this.output = output;
		this.isNullOutput = output == null;
		return this;
	}

	/**
	 * Returns a programmatic interface for setting properties for the HTML doc view.
	 * 
	 * <p>
	 * This is the programmatic equivalent to the {@link RestMethod#htmldoc() @RestMethod.htmldoc()} annotation.
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<jc>// Declarative approach.</jc>
	 * 	<ja>@RestMethod</ja>(
	 * 		htmldoc=<ja>@HtmlDoc</ja>(
	 * 			header={
	 * 				<js>"&lt;p&gt;This is my REST interface&lt;/p&gt;"</js>
	 * 			},
	 * 			aside={
	 * 				<js>"&lt;p&gt;Custom aside content&lt;/p&gt;"</js>
	 * 			}
	 * 		)
	 * 	)
	 * 	<jk>public</jk> Object doGet(RestResponse res) {
	 * 		
	 * 		<jc>// Equivalent programmatic approach.</jc>
	 * 		res.getHtmlDocBuilder()
	 * 			.header(<js>"&lt;p&gt;This is my REST interface&lt;/p&gt;"</js>)
	 * 			.aside(<js>"&lt;p&gt;Custom aside content&lt;/p&gt;"</js>);
	 * 	}
	 * </p>
	 * 
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='ja'>{@link RestMethod#htmldoc()}
	 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-rest-server.HtmlDocAnnotation">Overview &gt; @HtmlDoc</a>
	 * </ul>
	 * 
	 * @return A new programmatic interface for setting properties for the HTML doc view.
	 */
	public HtmlDocBuilder getHtmlDocBuilder() {
		if (htmlDocBuilder == null)
			htmlDocBuilder = new HtmlDocBuilder(properties);
		return htmlDocBuilder;
	}

	/**
	 * Retrieve the properties active for this request.
	 * 
	 * <p>
	 * This contains all resource and method level properties from the following:
	 * <ul>
	 * 	<li class='ja'>{@link RestResource#properties()}
	 * 	<li class='ja'>{@link RestMethod#properties()}
	 * 	<li class='jm'>{@link RestContextBuilder#set(String, Object)}
	 * </ul>
	 * 
	 * <p>
	 * The returned object is modifiable and allows you to override session-level properties before
	 * they get passed to the serializers.
	 * <br>However, properties are open-ended, and can be used for any purpose.
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<ja>@RestMethod</ja>(
	 * 		properties={
	 * 			<ja>@Property</ja>(name=<jsf>SERIALIZER_sortMaps</jsf>, value=<js>"false"</js>)
	 * 		}
	 * 	)
	 * 	<jk>public</jk> Map doGet(RestResponse res, <ja>@Query</ja>(<js>"sortMaps"</js>) Boolean sortMaps) {
	 * 		
	 * 		<jc>// Override value if specified through query parameter.</jc>
	 * 		<jk>if</jk> (sortMaps != <jk>null</jk>)
	 * 			res.getProperties().put(<jsf>SERIALIZER_sortMaps</jsf>, sortMaps);
	 * 
	 * 		<jk>return</jk> <jsm>getMyMap</jsm>();
	 * 	}
	 * </p>
	 * 
	 * <h5 class='section'>See Also:</h5>
	 * <ul>
	 * 	<li class='jm'>{@link #prop(String, Object)}
	 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-rest-server.Properties">Overview &gt; Properties</a>
	 * </ul>
	 * 
	 * @return The properties active for this request.
	 */
	public RequestProperties getProperties() {
		return properties;
	}

	/**
	 * Shortcut for calling <code>getProperties().append(name, value);</code> fluently.
	 * 
	 * @param name The property name.
	 * @param value The property value.
	 * @return This object (for method chaining).
	 */
	public RestResponse prop(String name, Object value) {
		this.properties.append(name, value);
		return this;
	}

	/**
	 * Shortcut method that allows you to use var-args to simplify setting array output.
	 * 
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode'>
	 * 	<jc>// Instead of...</jc>
	 * 	response.setOutput(<jk>new</jk> Object[]{x,y,z});
	 * 
	 * 	<jc>// ...call this...</jc>
	 * 	response.setOutput(x,y,z);
	 * </p>
	 * 
	 * @param output The output to serialize to the connection.
	 * @return This object (for method chaining).
	 */
	public RestResponse setOutputs(Object...output) {
		this.output = output;
		return this;
	}

	/**
	 * Returns the output that was set by calling {@link #setOutput(Object)}.
	 * 
	 * @return The output object.
	 */
	public Object getOutput() {
		return output;
	}

	/**
	 * Returns <jk>true</jk> if this response has any output associated with it.
	 * 
	 * @return <jk>true</jk> if {@link #setOutput(Object)} has been called, even if the value passed was <jk>null</jk>.
	 */
	public boolean hasOutput() {
		return output != null || isNullOutput;
	}

	/**
	 * Sets the output to a plain-text message regardless of the content type.
	 * 
	 * @param text The output text to send.
	 * @return This object (for method chaining).
	 * @throws IOException If a problem occurred trying to write to the writer.
	 */
	public RestResponse sendPlainText(String text) throws IOException {
		setContentType("text/plain");
		getNegotiatedWriter().write(text);
		return this;
	}

	/**
	 * Equivalent to {@link HttpServletResponse#getOutputStream()}, except wraps the output stream if an {@link Encoder}
	 * was found that matched the <code>Accept-Encoding</code> header.
	 * 
	 * @return A negotiated output stream.
	 * @throws IOException
	 */
	public ServletOutputStream getNegotiatedOutputStream() throws IOException {
		if (os == null) {
			Encoder encoder = null;
			EncoderGroup encoders = restJavaMethod.encoders;
			
			String ae = request.getHeader("Accept-Encoding");
			if (! (ae == null || ae.isEmpty())) {
				EncoderMatch match = encoders.getEncoderMatch(ae);
				if (match == null) {
					// Identity should always match unless "identity;q=0" or "*;q=0" is specified.
					if (ae.matches(".*(identity|\\*)\\s*;\\s*q\\s*=\\s*(0(?!\\.)|0\\.0).*")) {
						throw new RestException(SC_NOT_ACCEPTABLE,
							"Unsupported encoding in request header ''Accept-Encoding'': ''{0}''\n\tSupported codings: {1}",
							ae, encoders.getSupportedEncodings()
						);
					}
				} else {
					encoder = match.getEncoder();
					String encoding = match.getEncoding().toString();

					// Some clients don't recognize identity as an encoding, so don't set it.
					if (! encoding.equals("identity"))
						setHeader("content-encoding", encoding);
				}
			}
			os = getOutputStream();
			if (encoder != null) {
				@SuppressWarnings("resource")
				final OutputStream os2 = encoder.getOutputStream(os);
				os = new ServletOutputStream(){
					@Override /* OutputStream */
					public final void write(byte[] b, int off, int len) throws IOException {
						os2.write(b, off, len);
					}
					@Override /* OutputStream */
					public final void write(int b) throws IOException {
						os2.write(b);
					}
					@Override /* OutputStream */
					public final void flush() throws IOException {
						os2.flush();
					}
					@Override /* OutputStream */
					public final void close() throws IOException {
						os2.close();
					}
					@Override /* ServletOutputStream */
					public boolean isReady() {
						return true;
					}
					@Override /* ServletOutputStream */
					public void setWriteListener(WriteListener arg0) {
						throw new NoSuchMethodError();
					}
				};
			}
		}
		return os;
	}

	@Override /* ServletResponse */
	public ServletOutputStream getOutputStream() throws IOException {
		if (os == null)
			os = super.getOutputStream();
		return os;
	}

	/**
	 * Returns <jk>true</jk> if {@link #getOutputStream()} has been called.
	 * 
	 * @return <jk>true</jk> if {@link #getOutputStream()} has been called.
	 */
	public boolean getOutputStreamCalled() {
		return os != null;
	}

	/**
	 * Returns the writer to the response body.
	 * 
	 * <p>
	 * This methods bypasses any specified encoders and returns a regular unbuffered writer.
	 * Use the {@link #getNegotiatedWriter()} method if you want to use the matched encoder (if any).
	 */
	@Override /* ServletResponse */
	public PrintWriter getWriter() throws IOException {
		return getWriter(true);
	}

	/**
	 * Convenience method meant to be used when rendering directly to a browser with no buffering.
	 * 
	 * <p>
	 * Sets the header <js>"x-content-type-options=nosniff"</js> so that output is rendered immediately on IE and Chrome
	 * without any buffering for content-type sniffing.
	 * 
	 * <p>
	 * This can be useful if you want to render a streaming 'console' on a web page.
	 * 
	 * @param contentType The value to set as the <code>Content-Type</code> on the response.
	 * @return The raw writer.
	 * @throws IOException
	 */
	public PrintWriter getDirectWriter(String contentType) throws IOException {
		setContentType(contentType);
		setHeader("x-content-type-options", "nosniff");
		return getWriter();
	}

	/**
	 * Equivalent to {@link HttpServletResponse#getWriter()}, except wraps the output stream if an {@link Encoder} was
	 * found that matched the <code>Accept-Encoding</code> header and sets the <code>Content-Encoding</code>
	 * header to the appropriate value.
	 * 
	 * @return The negotiated writer.
	 * @throws IOException
	 */
	public PrintWriter getNegotiatedWriter() throws IOException {
		return getWriter(false);
	}

	private PrintWriter getWriter(boolean raw) throws IOException {
		if (w != null)
			return w;

		// If plain text requested, override it now.
		if (request.isPlainText())
			setHeader("Content-Type", "text/plain");

		try {
			OutputStream out = (raw ? getOutputStream() : getNegotiatedOutputStream());
			w = new PrintWriter(new OutputStreamWriter(out, getCharacterEncoding()));
			return w;
		} catch (UnsupportedEncodingException e) {
			String ce = getCharacterEncoding();
			setCharacterEncoding("UTF-8");
			throw new RestException(SC_NOT_ACCEPTABLE, "Unsupported charset in request header ''Accept-Charset'': ''{0}''", ce);
		}
	}

	/**
	 * Returns the <code>Content-Type</code> header stripped of the charset attribute if present.
	 * 
	 * @return The <code>media-type</code> portion of the <code>Content-Type</code> header.
	 */
	public MediaType getMediaType() {
		return MediaType.forString(getContentType());
	}

	/**
	 * Redirects to the specified URI.
	 * 
	 * <p>
	 * Relative URIs are always interpreted as relative to the context root.
	 * This is similar to how WAS handles redirect requests, and is different from how Tomcat handles redirect requests.
	 */
	@Override /* ServletResponse */
	public void sendRedirect(String uri) throws IOException {
		char c = (uri.length() > 0 ? uri.charAt(0) : 0);
		if (c != '/' && uri.indexOf("://") == -1)
			uri = request.getContextPath() + '/' + uri;
		super.sendRedirect(uri);
	}

	/**
	 * Returns the HTTP-part serializer associated with this response.
	 * 
	 * @return The HTTP-part serializer associated with this response.
	 */
	public HttpPartSerializer getPartSerializer() {
		return restJavaMethod.partSerializer;
	}

	@Override /* ServletResponse */
	public void setHeader(String name, String value) {
		// Jetty doesn't set the content type correctly if set through this method.
		// Tomcat/WAS does.
		if (name.equalsIgnoreCase("Content-Type"))
			super.setContentType(value);
		else
			super.setHeader(name, value);
	}


	@Override /* ServletResponse */
	public void flushBuffer() throws IOException {
		if (w != null)
			w.flush();
		if (os != null)
			os.flush();
		super.flushBuffer();
	}
}