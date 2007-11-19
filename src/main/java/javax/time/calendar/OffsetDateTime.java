/*
 * Copyright (c) 2007, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar;

import java.io.Serializable;

import javax.time.Instant;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.PeriodView;

/**
 * A calendrical representation of a date-time with a zone offset from UTC,
 * such as 2007-12-03T10:15:30+02:00.
 * <p>
 * OffsetDateTime is an immutable calendrical that represents a date-time, often
 * viewed as year-month-day-hour-minute-second-offset.
 * This class stores all date and time fields, to a precision of nanoseconds,
 * as well as a time zone. Thus, for example, the value
 * "2nd October 2007 at 13:45.30.123456789 +02:00" can be stored in an OffsetDateTime.
 * <p>
 * OffsetDateTime is thread-safe and immutable.
 *
 * @author Stephen Colebourne
 */
public final class OffsetDateTime
        implements ReadableDateTime, Calendrical, Comparable<OffsetDateTime>, Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = -456761901L;

    /**
     * The local date-time.
     */
    private final LocalDateTime dateTime;
    /**
     * The zone offset.
     */
    private final ZoneOffset offset;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>OffsetDateTime</code>.
     * <p>
     * The second and nanosecond fields will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return an OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, ZoneOffset offset) {
        return dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, 0, 0, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code>.
     * <p>
     * The nanosecond field will be set to zero by this factory method.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param offset  the zone offset, not null
     * @return an OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, ZoneOffset offset) {
        return dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, 0, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code>.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @param offset  the zone offset, not null
     * @return an OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(int year, int monthOfYear, int dayOfMonth,
            int hourOfDay, int minuteOfHour, int secondOfMinute, int nanoOfSecond, ZoneOffset offset) {
        LocalDateTime dt = LocalDateTime.dateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond);
        return dateTime(dt, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code> from an <code>Instant</code>.
     *
     * @param instant  the instant to convert, not null
     * @param offset  the zone offset, not null
     * @return an OffsetDateTime object, never null
     */
    public static OffsetDateTime dateTime(Instant instant, ZoneOffset offset) {
        if (instant == null) {
            throw new NullPointerException("The instant must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("The zone offset must not be null");
        }
        long epochSecs = instant.getEpochSeconds();
        long days = epochSecs / 24 * 60 * 60;
//        days += DAYS_FROM_0000_TO_1970;
        int secsOfDay = (int) (epochSecs % 24 * 60 * 60);
        if (secsOfDay < 0) {
            secsOfDay += 24 * 60 * 60;
            days--;
        }
        secsOfDay += 24 * 60 * 60;
        secsOfDay += offset.getAmountSeconds();
        secsOfDay %= 24 * 60 * 60;
        int hour = secsOfDay / 60 * 60;
        int min = (secsOfDay / 60) % 60;
        int sec = secsOfDay % 60;
        int nano = instant.getNanoOfSecond();
        LocalDateTime dateTime = LocalDateTime.dateTime(0, 0, 0, hour, min, sec, nano);
        return new OffsetDateTime(dateTime, offset);
    }

    /**
     * Obtains an instance of <code>OffsetDateTime</code>.
     *
     * @param dateTime  the date-time to represent, not null
     * @param offset  the zone offset, not null
     * @return an OffsetDateTime object, never null
     * @throws IllegalCalendarFieldValueException if any field is invalid
     */
    public static OffsetDateTime dateTime(LocalDateTime dateTime, ZoneOffset offset) {
        if (dateTime == null) {
            throw new NullPointerException("The date-time must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("The zone offset must not be null");
        }
        return new OffsetDateTime(dateTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param dateTime  the date-time, validated as not null
     * @param offset  the zone offset, validated as not null
     */
    private OffsetDateTime(LocalDateTime dateTime, ZoneOffset offset) {
        this.dateTime = dateTime;
        this.offset = offset;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the calendrical state which provides internal access to
     * this date-time.
     *
     * @return the calendar state for this instance, never null
     */
    @Override
    public CalendricalState getCalendricalState() {
        return null;  // TODO
    }

    /**
     * Gets the chronology that describes the calendar system rules for
     * this date-time.
     *
     * @return the ISO chronology, never null
     */
    public ISOChronology getChronology() {
        return ISOChronology.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified calendar field is supported.
     * <p>
     * This method queries whether this <code>OffsetDateTime</code> can
     * be queried using the specified calendar field.
     *
     * @param field  the field to query, not null
     * @return true if the field is supported
     */
    public boolean isSupported(TimeFieldRule field) {
        return dateTime.isSupported(field);
    }

    /**
     * Gets the value of the specified calendar field.
     * <p>
     * This method queries the value of the specified calendar field.
     * If the calendar field is not supported then an exception is thrown.
     *
     * @param field  the field to query, not null
     * @return the value for the field
     * @throws UnsupportedCalendarFieldException if the field is not supported
     */
    public int get(TimeFieldRule field) {
        return dateTime.get(field);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>Year</code> initialised to the
     * year of this date-time.
     *
     * @return the year object, never null
     */
    public Year year() {
        return dateTime.year();
    }

    /**
     * Gets an instance of <code>MonthOfYear</code> initialised to the
     * month of this date-time.
     *
     * @return the month object, never null
     */
    public MonthOfYear monthOfYear() {
        return dateTime.monthOfYear();
    }

    /**
     * Gets an instance of <code>YearMonth</code> initialised to the
     * year and month of this date-time.
     *
     * @return the year-month object, never null
     */
    public YearMonth yearMonth() {
        return dateTime.yearMonth();
    }

    /**
     * Gets an instance of <code>MonthDay</code> initialised to the
     * month and day of month of this date-time.
     *
     * @return the month-day object, never null
     */
    public MonthDay monthDay() {
        return dateTime.monthDay();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an instance of <code>LocalDateTime</code> initialised to the
     * same date-time.
     *
     * @return the date-time object, never null
     */
    public LocalDateTime localDateTime() {
        return dateTime;
    }

    /**
     * Gets an instance of <code>LocalDate</code> initialised to the
     * date of this date-time.
     *
     * @return the date object, never null
     */
    public LocalDate localDate() {
        return dateTime.date();
    }

    /**
     * Gets an instance of <code>OffsetDate</code> initialised to the
     * date of this date-time.
     *
     * @return the date object, never null
     */
    public OffsetDate offsetDate() {
        return OffsetDate.date(localDate(), offset);
    }

    /**
     * Gets an instance of <code>LocalTime</code> initialised to the
     * time of this date-time.
     *
     * @return the time object, never null
     */
    public LocalTime localTime() {
        return dateTime.time();
    }

    /**
     * Gets an instance of <code>OffsetTime</code> initialised to the
     * time of this date-time.
     *
     * @return the time object, never null
     */
    public OffsetTime offsetTime() {
        return OffsetTime.time(localTime(), offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the zone offset.
     *
     * @return the zone offset, never null
     */
    public ZoneOffset getOffset() {
        return offset;
    }

    /**
     * Returns a copy of this OffsetDateTime with a different zone offset.
     * <p>
     * This method changes the offset stored in this zoned date to a different
     * offset. No calculation is performed. The result simply represents the same
     * date and the new offset.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withOffset(ZoneOffset offset) {
        return offset == this.offset ? this : dateTime(dateTime, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Adjusts the local date-time using the specified offset.
     * <p>
     * This method changes the zoned time from one offset to another.
     * If this time represents 10:30+02:00 and the offset specified is
     * +03:00, then this method will return 11:30+03:00.
     * <p>
     * To change the offset whilst keeping the local time,
     * use {@link #withOffset(ZoneOffset)}.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param offset  the zone offset to change to, not null
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime adjustLocalDateTime(ZoneOffset offset) {
        if (offset.equals(this.offset)) {
            return this;
        }
        int difference = offset.getAmountSeconds() - this.offset.getAmountSeconds();
        LocalDateTime adjusted = dateTime.plusSeconds(difference);
        return new OffsetDateTime(adjusted, offset);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the ISO proleptic year value.
     * <p>
     * The year 1AD is represented by 1.<br />
     * The year 1BC is represented by 0.<br />
     * The year 2BC is represented by -1.<br />
     *
     * @return the year, from MIN_YEAR to MAX_YEAR
     */
    public int getYear() {
        return dateTime.getYear();
    }

    /**
     * Gets the month of year value.
     * <p>
     * This method returns the numerical value for the month, from 1 to 12.
     * The enumerated constant is returned by {@link #monthOfYear()}.
     *
     * @return the month of year, from 1 (January) to 12 (December)
     */
    public int getMonthOfYear() {
        return dateTime.getMonthOfYear();
    }

    /**
     * Gets the day of year value.
     *
     * @return the day of year, from 1 to 366
     */
    public int getDayOfYear() {
        return dateTime.getDayOfYear();
    }

    /**
     * Gets the day of month value.
     *
     * @return the day of month, from 1 to 31
     */
    public int getDayOfMonth() {
        return dateTime.getDayOfMonth();
    }

    /**
     * Gets the day of week value.
     *
     * @return the day of week, never null
     */
    public DayOfWeek getDayOfWeek() {
        return dateTime.getDayOfWeek();
    }

    /**
     * Gets the hour of day value.
     *
     * @return the hour of day, from 0 to 23
     */
    public int getHourOfDay() {
        return dateTime.getHourOfDay();
    }

    /**
     * Gets the minute of hour value.
     *
     * @return the minute of hour, from 0 to 59
     */
    public int getMinuteOfHour() {
        return dateTime.getMinuteOfHour();
    }

    /**
     * Gets the second of minute value.
     *
     * @return the second of minute, from 0 to 59
     */
    public int getSecondOfMinute() {
        return dateTime.getSecondOfMinute();
    }

    /**
     * Gets the nanosecond fraction of a second expressed as an int.
     *
     * @return the nano of second, from 0 to 999,999,999
     */
    public int getNanoOfSecond() {
        return dateTime.getNanoOfSecond();
    }

    /**
     * Gets the nanosecond fraction of a second expressed as a double.
     *
     * @return the nano of second, from 0 to 0.999,999,999
     */
    public double getNanoFraction() {
        return dateTime.getNanoFraction();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDateTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendrical  the calendrical values to update to, not null
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime with(Calendrical calendrical) {
        LocalDateTime newDT = dateTime.with(calendrical);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param calendricals  the calendrical values to update to, no nulls
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime with(Calendrical... calendricals) {
        LocalDateTime newDT = dateTime.with(calendricals);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDateTime with the year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_YEAR to MAX_YEAR
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withYear(int year) {
        LocalDateTime newDT = dateTime.withYear(year);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the month of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withMonthOfYear(int monthOfYear) {
        LocalDateTime newDT = dateTime.withMonthOfYear(monthOfYear);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the day of month value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withDayOfMonth(int dayOfMonth) {
        LocalDateTime newDT = dateTime.withDayOfMonth(dayOfMonth);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the date set to the last day of month.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withLastDayOfMonth() {
        LocalDateTime newDT = dateTime.withLastDayOfMonth();
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the day of year value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfYear  the day of year to represent, from 1 to 366
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withDayOfYear(int dayOfYear) {
        LocalDateTime newDT = dateTime.withDayOfYear(dayOfYear);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the date set to the last day of year.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withLastDayOfYear() {
        LocalDateTime newDT = dateTime.withLastDayOfYear();
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the day of week value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param dayOfWeek  the day of week to represent, from 1 (Monday) to 7 (Sunday)
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withDayOfWeek(int dayOfWeek) {
        LocalDateTime newDT = dateTime.withDayOfWeek(dayOfWeek);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the date values altered.
     * <p>
     * This method will return a new instance with the same time fields,
     * but altered date fields.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param year  the year to represent, from MIN_VALUE + 1 to MAX_VALUE
     * @param monthOfYear  the month of year to represent, from 1 (January) to 12 (December)
     * @param dayOfMonth  the day of month to represent, from 1 to 31
     * @return a new updated OffsetDateTime
     */
    public OffsetDateTime withDate(int year, int monthOfYear, int dayOfMonth) {
        LocalDateTime newDT = dateTime.withDate(year, monthOfYear, dayOfMonth);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the hour of day value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withHourOfDay(int hourOfDay) {
        LocalDateTime newDT = dateTime.withHourOfDay(hourOfDay);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the minute of hour value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withMinuteOfHour(int minuteOfHour) {
        LocalDateTime newDT = dateTime.withMinuteOfHour(minuteOfHour);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the second of minute value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withSecondOfMinute(int secondOfMinute) {
        LocalDateTime newDT = dateTime.withSecondOfMinute(secondOfMinute);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the nano of second value altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanoOfSecond  the nano of second to represent, from 0 to 999,999,999
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withNanoOfSecond(int nanoOfSecond) {
        LocalDateTime newDT = dateTime.withNanoOfSecond(nanoOfSecond);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the time values altered.
     * <p>
     * This method will return a new instance with the same date fields,
     * but altered time fields.
     * This is a shorthand for {@link #withTime(int,int,int)} and sets
     * the second field to zero.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withTime(int hourOfDay, int minuteOfHour) {
        LocalDateTime newDT = dateTime.withTime(hourOfDay, minuteOfHour);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the time values altered.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hourOfDay  the hour of day to represent, from 0 to 23
     * @param minuteOfHour  the minute of hour to represent, from 0 to 59
     * @param secondOfMinute  the second of minute to represent, from 0 to 59
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime withTime(int hourOfDay, int minuteOfHour, int secondOfMinute) {
        LocalDateTime newDT = dateTime.withTime(hourOfDay, minuteOfHour, secondOfMinute);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDateTime with the specified period added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param period  the period to add, not null
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plus(PeriodView period) {
        LocalDateTime newDT = dateTime.plus(period);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified periods added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param periods  the periods to add, no nulls
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plus(PeriodView... periods) {
        LocalDateTime newDT = dateTime.plus(periods);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this OffsetDateTime with the specified period in years added.
     * <p>
     * This method add the specified amount to the years field in three steps:
     * <ol>
     * <li>Add the input years to the year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2008-02-29 (leap year) plus one year would result in the
     * invalid date 2009-02-29 (standard year). Instead of returning an invalid
     * result, the last valid day of the month, 2009-02-28, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param years  the years to add, may be negative
     * @return a new updated OffsetDateTime, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public OffsetDateTime plusYears(int years) {
        LocalDateTime newDT = dateTime.plusYears(years);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in months added.
     * <p>
     * This method add the specified amount to the months field in three steps:
     * <ol>
     * <li>Add the input months to the month of year field</li>
     * <li>Check if the resulting date would be invalid</li>
     * <li>Adjust the day of month to the last valid day if necessary</li>
     * </ol>
     * <p>
     * For example, 2007-03-31 plus one month would result in the invalid date
     * 2007-04-31. Instead of returning an invalid result, the last valid day
     * of the month, 2007-04-30, is selected instead.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param months  the months to add, may be negative
     * @return a new updated OffsetDateTime, never null
     * @throws ArithmeticException if the calculation overflows
     * @throws IllegalCalendarFieldValueException if the result contains an invalid field
     */
    public OffsetDateTime plusMonths(int months) {
        LocalDateTime newDT = dateTime.plusMonths(months);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in weeks added.
     * <p>
     * This method add the specified amount in weeks to the days field incrementing
     * the month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one week would result in the 2009-01-07.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param weeks  the weeks to add, may be negative
     * @return a new updated OffsetDateTime, never null
     * @throws ArithmeticException if the calculation overflows
     */
    public OffsetDateTime plusWeeks(int weeks) {
        LocalDateTime newDT = dateTime.plusWeeks(weeks);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in days added.
     * <p>
     * This method add the specified amount to the days field incrementing the
     * month and year fields as necessary to ensure the result remains valid.
     * The result is only invalid if the maximum/minimum year is exceeded.
     * <p>
     * For example, 2008-12-31 plus one day would result in the 2009-01-01.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param days  the days to add, may be negative
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusDays(int days) {
        LocalDateTime newDT = dateTime.plusDays(days);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in hours added.
     * <p>
     * This method uses field based addition.
     * This method changes the field by the specified number of hours.
     * This may, at daylight savings cutover, result in a duration being added
     * that is more or less than the specified number of hours.
     * <p>
     * For example, consider a zone offset where the spring DST cutover means that
     * the local times 01:00 to 01:59 do not exist. Using this method, adding
     * a duration of 2 hours to 00:30 will result in 02:30, but it is important
     * to note that only a duration of 1 hour was actually added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hours  the hours to add, may be negative
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusHours(int hours) {
        LocalDateTime newDT = dateTime.plusHours(hours);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutes  the minutes to add, may be negative
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusMinutes(int minutes) {
        LocalDateTime newDT = dateTime.plusMinutes(minutes);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param seconds  the seconds to add, may be negative
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusSeconds(int seconds) {
        LocalDateTime newDT = dateTime.plusSeconds(seconds);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    /**
     * Returns a copy of this OffsetDateTime with the specified period in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanos  the nanos to add, may be negative
     * @return a new updated OffsetDateTime, never null
     */
    public OffsetDateTime plusNanos(int nanos) {
        LocalDateTime newDT = dateTime.plusNanos(nanos);
        return (newDT == dateTime ? this : new OffsetDateTime(newDT, offset));
    }

    //-----------------------------------------------------------------------
    /**
     * Converts this date-time to an <code>Instant</code>.
     *
     * @return an Instant representing the same instant, never null
     */
    public Instant toInstant() {
        return null;  // TODO
    }

    /**
     * Converts this date-time to a <code>LocalDate</code>.
     *
     * @return a LocalDate representing the date fields of this date-time, never null
     */
    public LocalDate toLocalDate() {
        return dateTime.toLocalDate();
    }

    /**
     * Converts this date-time to a <code>LocalTime</code>.
     *
     * @return a LocalTime representing the time fields of this date-time, never null
     */
    public LocalTime toLocalTime() {
        return dateTime.toLocalTime();
    }

    /**
     * Converts this date-time to a <code>LocalDateTime</code>.
     *
     * @return a LocalDateTime representing the fields of this date-time, never null
     */
    public LocalDateTime toLocalDateTime() {
        return dateTime;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this date-time to another date-time based on the UTC
     * equivalent date-times.
     * <p>
     * This ordering is inconsistent with <code>equals()</code> as two
     * date-times with the same instant will compare as equal regardless of
     * the actual offsets.
     *
     * @param other  the other date-time to compare to, not null
     * @return the comparator value, negative if less, postive if greater
     * @throws NullPointerException if <code>other</code> is null
     */
    public int compareTo(OffsetDateTime other) {
        if (offset.equals(other.offset)) {
            return dateTime.compareTo(other.dateTime);
        }
        LocalDateTime thisUTC = dateTime.plusSeconds(-offset.getAmountSeconds());
        LocalDateTime otherUTC = other.dateTime.plusSeconds(other.offset.getAmountSeconds());
        return thisUTC.compareTo(otherUTC);
    }

    /**
     * Is this date-time after the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this is after the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isAfter(OffsetDateTime other) {
        return compareTo(other) > 0;
    }

    /**
     * Is this date-time before the specified date-time.
     *
     * @param other  the other date-time to compare to, not null
     * @return true if this point is before the specified date-time
     * @throws NullPointerException if <code>other</code> is null
     */
    public boolean isBefore(OffsetDateTime other) {
        return compareTo(other) < 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Is this date-time equal to the specified date-time.
     * <p>
     * This compares the date-time and the offset.
     *
     * @param other  the other date-time to compare to, null returns false
     * @return true if this point is equal to the specified date-time
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof OffsetDateTime) {
            OffsetDateTime zonedDateTime = (OffsetDateTime) other;
            return dateTime.equals(zonedDateTime.dateTime) && offset.equals(zonedDateTime.offset);
        }
        return false;
    }

    /**
     * A hashcode for this date-time.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return dateTime.hashCode() ^ offset.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Outputs the date-time as a <code>String</code>.
     * <p>
     * The output will be one of the following formats:
     * <ul>
     * <li>'yyyy-MM-ddThh:mmZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ssZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSSSSZ'</li>
     * <li>'yyyy-MM-ddThh:mm:ss.SSSSSSSSSZ'</li>
     * </ul>
     * where 'Z' is the id of the zone offset, such as '+02:30' or 'Z'.
     * The format used will be the shortest that outputs the full value of
     * the time where the omitted parts are implied to be zero.
     *
     * @return the formatted date-time string, never null
     */
    @Override
    public String toString() {
        return dateTime.toString() + offset.toString();
    }

}
