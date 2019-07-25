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
package org.apache.juneau.transforms;

import java.lang.reflect.*;
import java.time.*;
import java.time.chrono.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;

import org.apache.juneau.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.transform.*;

/**
 * Swap that converts {@link Temporal} objects to strings.
 *
 * <p>
 * Uses the {@link DateTimeFormatter} class for converting {@link Temporal} objects to and from strings.
 *
 * <p>
 * Supports any of the following temporal objects:
 * <ul>
 * 	<li>{@link HijrahDate}
 * 	<li>{@link Instant}
 * 	<li>{@link JapaneseDate}
 * 	<li>{@link LocalDate}
 * 	<li>{@link LocalDateTime}
 * 	<li>{@link LocalTime}
 * 	<li>{@link MinguoDate}
 * 	<li>{@link OffsetDateTime}
 * 	<li>{@link OffsetTime}
 * 	<li>{@link ThaiBuddhistDate}
 * 	<li>{@link Year}
 * 	<li>{@link YearMonth}
 * 	<li>{@link ZonedDateTime}
 * </ul>
 */
public class TemporalSwap extends StringSwap<Temporal> {

	/**
	 * Default swap to {@link DateTimeFormatter#BASIC_ISO_DATE}.
	 * <p>
	 * Example: <js>"20111203"</js>
	 */
	public static class BasicIsoDate extends TemporalSwap {
		/** Constructor.*/
		public BasicIsoDate() {
			super("BASIC_ISO_DATE", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_DATE}.
	 * <p>
	 * Example: <js>"2011-12-03+01:00"</js> or <js>"2011-12-03"</js>
	 */
	public static class IsoDate extends TemporalSwap {
		/** Constructor.*/
		public IsoDate() {
			super("ISO_DATE", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_DATE_TIME}.
	 * <p>
	 * Example: <js>"2011-12-03T10:15:30+01:00[Europe/Paris]"</js>
	 */
	public static class IsoDateTime extends TemporalSwap {
		/** Constructor.*/
		public IsoDateTime() {
			super("ISO_DATE_TIME", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_INSTANT}.
	 * <p>
	 * Example: <js>"2011-12-03T10:15:30Z"</js>
	 */
	public static class IsoInstant extends TemporalSwap {
		/** Constructor.*/
		public IsoInstant() {
			super("ISO_INSTANT", false);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_LOCAL_DATE}.
	 * <p>
	 * Example: <js>"2011-12-03"</js>
	 */
	public static class IsoLocalDate extends TemporalSwap {
		/** Constructor.*/
		public IsoLocalDate() {
			super("ISO_LOCAL_DATE", false);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
	 * <p>
	 * Example: <js>"2011-12-03T10:15:30"</js>
	 */
	public static class IsoLocalDateTime extends TemporalSwap {
		/** Constructor.*/
		public IsoLocalDateTime() {
			super("ISO_LOCAL_DATE_TIME", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_LOCAL_TIME}.
	 * <p>
	 * Example: <js>"10:15:30"</js>
	 */
	public static class IsoLocalTime extends TemporalSwap {
		/** Constructor.*/
		public IsoLocalTime() {
			super("ISO_LOCAL_TIME", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_OFFSET_DATE}.
	 * <p>
	 * Example: <js>"2011-12-03"</js>
	 */
	public static class IsoOffsetDate extends TemporalSwap {
		/** Constructor.*/
		public IsoOffsetDate() {
			super("ISO_OFFSET_DATE", false);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}.
	 * <p>
	 * Example: <js>"2011-12-03T10:15:30+01:00"</js>
	 */
	public static class IsoOffsetDateTime extends TemporalSwap {
		/** Constructor.*/
		public IsoOffsetDateTime() {
			super("ISO_OFFSET_DATE_TIME", false);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_OFFSET_TIME}.
	 * <p>
	 * Example: <js>"10:15:30+01:00"</js>
	 */
	public static class IsoOffsetTime extends TemporalSwap {
		/** Constructor.*/
		public IsoOffsetTime() {
			super("ISO_OFFSET_TIME", false);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_ORDINAL_DATE}.
	 * <p>
	 * Example: <js>"2012-337"</js>
	 */
	public static class IsoOrdinalDate extends TemporalSwap {
		/** Constructor.*/
		public IsoOrdinalDate() {
			super("ISO_ORDINAL_DATE", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_TIME}.
	 * <p>
	 * Example: <js>"10:15:30+01:00"</js> or <js>"10:15:30"</js>
	 */
	public static class IsoTime extends TemporalSwap {
		/** Constructor.*/
		public IsoTime() {
			super("ISO_TIME", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_WEEK_DATE}.
	 * <p>
	 * Example: <js>"2012-W48-6"</js>
	 */
	public static class IsoWeekDate extends TemporalSwap {
		/** Constructor.*/
		public IsoWeekDate() {
			super("ISO_WEEK_DATE", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_WEEK_DATE}.
	 * <p>
	 * Example: <js>"2011"</js>
	 */
	public static class IsoYear extends TemporalSwap {
		/** Constructor.*/
		public IsoYear() {
			super("uuuu", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_WEEK_DATE}.
	 * <p>
	 * Example: <js>"2011-12"</js>
	 */
	public static class IsoYearMonth extends TemporalSwap {
		/** Constructor.*/
		public IsoYearMonth() {
			super("uuuu-MM", true);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#ISO_ZONED_DATE_TIME}.
	 * <p>
	 * Example: <js>"2011-12-03T10:15:30+01:00[Europe/Paris]"</js>
	 */
	public static class IsoZonedDateTime extends TemporalSwap {
		/** Constructor.*/
		public IsoZonedDateTime() {
			super("ISO_ZONED_DATE_TIME", false);
		}
	}

	/**
	 * Default swap to {@link DateTimeFormatter#RFC_1123_DATE_TIME}.
	 * <p>
	 * Example: <js>"Tue, 3 Jun 2008 11:05:30 GMT"</js>
	 */
	public static class Rfc1123DateTime extends TemporalSwap {
		/** Constructor.*/
		public Rfc1123DateTime() {
			super("RFC_1123_DATE_TIME", false);
		}
	}

	private static final ZoneId Z = ZoneId.of("Z");
	private static final Map<Class<? extends Temporal>,Method> FROM_METHODS = new ConcurrentHashMap<>();

	private static Method findParseMethod(Class<? extends Temporal> c) throws ExecutableException {
		Method m = FROM_METHODS.get(c);
		if (m == null) {
			m = ClassInfo.of(c).getStaticPublicMethodInner("from", c, TemporalAccessor.class);
			if (m == null)
				throw new ExecutableException("Parse method not found on temporal class ''{0}''", c.getSimpleName());
			FROM_METHODS.put(c, m);
		}
		return m;
	}

	private final DateTimeFormatter formatter;
	private final boolean zoneOptional;

	/**
	 * Constructor.
	 *
	 * @param pattern The timestamp format or name of predefined {@link DateTimeFormatter}.
	 * @param zoneOptional <jk>true</jk> if the time zone on the pattern is optional.
	 */
	public TemporalSwap(String pattern, boolean zoneOptional) {
		super(Temporal.class);
		this.formatter = DateUtils.getFormatter(pattern);
		this.zoneOptional = zoneOptional;
	}

	/**
	 * Returns <jk>true</jk> if the time zone on the pattern is optional.
	 *
	 * <p>
	 * If it's not optional, then local dates/times must be converted into zoned times using the session time zone.
	 * Otherwise, local date/times are fine.
	 *
	 * @return <jk>true</jk> if the time zone on the pattern is optional.
	 */
	protected boolean zoneOptional() {
		return zoneOptional;
	}

	@Override /* PojoSwap */
	public String swap(BeanSession session, Temporal o) throws Exception {
		if (o == null)
			return null;
		o = convertToSerializable(session, o);
		return formatter.format(o);
	}

	/**
	 * Converts the specified temporal object to a form suitable to be serialized using any pattern.
	 *
	 * @param session The current bean session.
	 * @param t The temporal object to convert.
	 * @return The converted temporal object.
	 */
	protected Temporal convertToSerializable(BeanSession session, Temporal t) {

		ZoneId zoneId = session.getTimeZoneId();
		Class<? extends Temporal> tc = t.getClass();

		// Instant is always serialized in GMT.
		if (tc == Instant.class)
			return ZonedDateTime.from(defaulting(t, Z));

		// These can handle any pattern.
		if (tc == ZonedDateTime.class || tc == OffsetDateTime.class)
			return t;

		// Pattern optionally includes a time zone, so zoned and local date-times are good.
		if (zoneOptional()) {
			if (tc == LocalDateTime.class)
				return t;
			if (tc == OffsetTime.class)
				return ZonedDateTime.from(defaulting(t, zoneId));
			return LocalDateTime.from(defaulting(t, zoneId));
		}

		return ZonedDateTime.from(defaulting(t, zoneId));
	}

	@SuppressWarnings("unchecked")
	@Override /* PojoSwap */
	public Temporal unswap(BeanSession session, String f, ClassMeta<?> hint) throws Exception {
		if (hint == null)
			hint = session.getClassMeta(Instant.class);
		Class<? extends Temporal> tc = (Class<? extends Temporal>)hint.getInnerClass();

		ZoneId offset = session.getTimeZoneId();

		if (tc == Instant.class)
			offset = Z;

		Method parseMethod = findParseMethod(tc);

		TemporalAccessor ta = defaulting(formatter.parse(f), offset);
		return (Temporal)parseMethod.invoke(null, ta);
	}

	private final TemporalAccessor defaulting(TemporalAccessor t, ZoneId zoneId) {
		return new DefaultingTemporalAccessor(t, zoneId);
	}
}
