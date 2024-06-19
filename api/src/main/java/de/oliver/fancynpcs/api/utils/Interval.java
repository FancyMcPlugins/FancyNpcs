/*
 * MIT License
 *
 * Copyright (c) 2023 Grabsky <44530932+Grabsky@users.noreply.github.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * HORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.oliver.fancynpcs.api.utils;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Date;

import static de.oliver.fancynpcs.api.utils.Interval.Unit.*;


/**
 * {@link Interval} is simple (but not very extensible) object that provides methods for
 * unit conversion and creation of human-readable 'elapsed time' strings.
 * <p>
 * This API is for internal use only and can change at any time.
 */
@ApiStatus.Internal
public final class Interval {

    private final long value;

    public Interval(final long value) {
        this.value = value;
    }

    /**
     * Returns {@link Interval} object of current time.
     */
    public static @NotNull Interval now() {
        return new Interval(System.currentTimeMillis());
    }

    /**
     * Returns {@link Interval} object constructed from provided {@link Long long} {@code (interval)}.
     * It is expected that provided value is <u>already</u> a difference between two timestamps.
     */
    public static @NotNull Interval of(final long interval, final @NotNull Unit unit) {
        return new Interval(interval * unit.factor);
    }

    /**
     * Returns {@link Interval} object constructed from provided {@link Double double} {@code (interval)}.
     * It is expected that provided value is <u>already</u> a difference between two timestamps.
     */
    public static @NotNull Interval of(final double interval, final @NotNull Unit unit) {
        return new Interval(Math.round(interval * unit.factor));
    }

    /**
     * Returns {@link Interval} of time between {@code n} and {@code m}.
     */
    public static @NotNull Interval between(final long n, final long m, final @NotNull Unit unit) {
        return new Interval((n - m) * unit.factor);
    }

    /**
     * Returns {@link Interval} of time between {@code n} and {@code m}.
     */
    public static @NotNull Interval between(final double n, final double m, final @NotNull Unit unit) {
        return new Interval(Math.round((n - m) * unit.factor));
    }

    /**
     * Returns interval converted to specified {@link Unit} {@code (unit)}. <br />
     * <pre>
     * Interval.of(1500, Interval.Unit.MILLISECONDS).as(Interval.Unit.SECONDS) // 1.5F
     * Interval.of(300, Interval.Unit.SECONDS).as(Interval.Unit.MINUTES) // 5F
     * </pre>
     */
    public double as(final @NotNull Unit unit) {
        return (double) (value / unit.factor);
    }

    /**
     * Returns a copy of (this) {@link Interval} with {@code n} of {@link Unit} added.
     */
    public @NotNull Interval add(final @NotNull Interval other) {
        return new Interval(this.value + other.value);
    }

    /**
     * Returns a copy of (this) {@link Interval} with {@code n} of {@link Unit} added.
     */
    public @NotNull Interval add(final long n, final @NotNull Unit unit) {
        return new Interval(this.value + (n * unit.factor));
    }

    /**
     * Returns a copy of (this) {@link Interval} with {@code n} of {@link Unit} removed.
     */
    public @NotNull Interval remove(final @NotNull Interval other) {
        return new Interval(this.value - other.value);
    }

    /**
     * Returns a copy of (this) {@link Interval} with {@code n} of {@link Unit} removed.
     */
    public @NotNull Interval remove(final long n, final @NotNull Unit unit) {
        return new Interval(this.value - (n * unit.factor));
    }

    /**
     * Returns new {@link Date} created from (this) {@link Interval}.
     */
    public @NotNull Date toDate() {
        return new Date(this.value);
    }

    /**
     * Returns new {@link Instant} created from (this) {@link Interval}.
     */
    public @NotNull Instant toInstant() {
        return Instant.ofEpochMilli(this.value);
    }

    /**
     * Returns formatted {@link String} expressing this {@link Interval}.
     * <pre>
     * final Interval i = Interval.between(lastJoinedMillis, currentTimeMillis, Interval.Unit.MILLISECONDS);
     * System.out.println(i.toString()) + " ago"; // eg. '1d 7h 32min 10s ago'
     * </pre>
     */
    @Override
    public @NotNull String toString() {
        // Returning milliseconds for values below 1000. (less than one second)
        if (value < 1000)
            return value % YEARS.getFactor() % MONTHS.getFactor() % DAYS.getFactor() % HOURS.getFactor() % MINUTES.getFactor() % SECONDS.getFactor() / MILLISECONDS.getFactor() + "ms";
        ;
        // Calculation values, the ugly way.
        final long years = value / YEARS.getFactor();
        final long months = value % YEARS.getFactor() / MONTHS.getFactor();
        final long days = value % YEARS.getFactor() % MONTHS.getFactor() / DAYS.getFactor();
        final long hours = value % YEARS.getFactor() % MONTHS.getFactor() % DAYS.getFactor() / HOURS.getFactor();
        final long minutes = value % YEARS.getFactor() % MONTHS.getFactor() % DAYS.getFactor() % HOURS.getFactor() / MINUTES.getFactor();
        final long seconds = value % YEARS.getFactor() % MONTHS.getFactor() % DAYS.getFactor() % HOURS.getFactor() % MINUTES.getFactor() / SECONDS.getFactor();
        // Creating a new output StringBuilder object.
        final StringBuilder builder = new StringBuilder();
        // Appending to the StringBuilder.
        if (years > 0L) builder.append(years).append("y ");
        if (months > 0L) builder.append(months).append("mo ");
        if (days > 0L) builder.append(days).append("d ");
        if (hours > 0L) builder.append(hours).append("h ");
        if (minutes > 0L) builder.append(minutes).append("min ");
        if (seconds > 0L) builder.append(seconds).append("s");
        // Removing last character if a whitespace.
        if (builder.charAt(builder.length() - 1) == ' ')
            builder.deleteCharAt(builder.length() - 1);
        // Building a String and returning.
        return builder.toString();
    }

    public enum Unit {
        MILLISECONDS(1L, "ms"),
        TICKS(50L, "t"),
        SECONDS(1_000L, "s"),
        MINUTES(60_000L, "min"),
        HOURS(3_600_000L, "h"),
        DAYS(86_400_000L, "d"),
        MONTHS(2_629_800_000L, "mo"),
        YEARS(31_557_600_000L, "y");

        private final long factor;
        private final String shortCode;

        Unit(final long factor, final @NotNull String shortCode) {
            this.factor = factor;
            this.shortCode = shortCode;
        }

        /**
         * Returns {@link Unit} or {@code null} from provided short code.
         */
        public static @Nullable Unit fromShortCode(final @NotNull String shortCode) {
            // Iterating over all units and finding one that matches provided short code.
            for (final Unit unit : Unit.values())
                if (unit.shortCode.equalsIgnoreCase(shortCode) == true)
                    return unit;
            // Unit has not been found. Returning null.
            return null;
        }

        public long getFactor() {
            return factor;
        }

        public @NotNull String getShortCode() {
            return shortCode;
        }

    }

}