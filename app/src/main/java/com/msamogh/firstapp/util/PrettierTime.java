package com.msamogh.firstapp.util;

import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Month;
import org.ocpsoft.prettytime.units.Second;
import org.ocpsoft.prettytime.units.Week;
import org.ocpsoft.prettytime.units.Year;

/**
 * Created by root on 7/2/15.
 */
public class PrettierTime extends PrettyTime {

    public PrettierTime() {
        super();
        registerUnit(new Hour(), new TimeFormat() {
            @Override
            public String format(final Duration duration) {
                return Math.abs(duration.getQuantity()) + "h";
            }

            @Override
            public String formatUnrounded(Duration duration) {
                return format(duration);
            }

            @Override
            public String decorate(Duration duration, String time) {
                return time;
            }

            @Override
            public String decorateUnrounded(Duration duration, String time) {
                return decorate(duration, time);
            }
        });

        registerUnit(new Minute(), new TimeFormat() {
            @Override
            public String format(final Duration duration) {
                return Math.abs(duration.getQuantity()) + "m";
            }

            @Override
            public String formatUnrounded(Duration duration) {
                return format(duration);
            }

            @Override
            public String decorate(Duration duration, String time) {
                return time;
            }

            @Override
            public String decorateUnrounded(Duration duration, String time) {
                return decorate(duration, time);
            }
        });

        registerUnit(new Second(), new TimeFormat() {
            @Override
            public String format(final Duration duration) {
                return Math.abs(duration.getQuantity()) + "s";
            }

            @Override
            public String formatUnrounded(Duration duration) {
                return format(duration);
            }

            @Override
            public String decorate(Duration duration, String time) {
                return time;
            }

            @Override
            public String decorateUnrounded(Duration duration, String time) {
                return decorate(duration, time);
            }
        });

        registerUnit(new Day(), new TimeFormat() {
            @Override
            public String format(final Duration duration) {
                return Math.abs(duration.getQuantity()) + "d";
            }

            @Override
            public String formatUnrounded(Duration duration) {
                return format(duration);
            }

            @Override
            public String decorate(Duration duration, String time) {
                return time;
            }

            @Override
            public String decorateUnrounded(Duration duration, String time) {
                return decorate(duration, time);
            }
        });

        registerUnit(new Week(), new TimeFormat() {
            @Override
            public String format(final Duration duration) {
                return Math.abs(duration.getQuantity()) + "w";
            }

            @Override
            public String formatUnrounded(Duration duration) {
                return format(duration);
            }

            @Override
            public String decorate(Duration duration, String time) {
                return time;
            }

            @Override
            public String decorateUnrounded(Duration duration, String time) {
                return decorate(duration, time);
            }
        });

        registerUnit(new Month(), new TimeFormat() {
            @Override
            public String format(final Duration duration) {
                return Math.abs(duration.getQuantity()) + "mo";
            }

            @Override
            public String formatUnrounded(Duration duration) {
                return format(duration);
            }

            @Override
            public String decorate(Duration duration, String time) {
                return time;
            }

            @Override
            public String decorateUnrounded(Duration duration, String time) {
                return decorate(duration, time);
            }
        });

        registerUnit(new JustNow(), new TimeFormat() {
            @Override
            public String format(final Duration duration) {
                return "now";
            }

            @Override
            public String formatUnrounded(Duration duration) {
                return format(duration);
            }

            @Override
            public String decorate(Duration duration, String time) {
                return time;
            }

            @Override
            public String decorateUnrounded(Duration duration, String time) {
                return decorate(duration, time);
            }
        });

        registerUnit(new Year(), new TimeFormat() {
            @Override
            public String format(final Duration duration) {
                return Math.abs(duration.getQuantity()) + "y";
            }

            @Override
            public String formatUnrounded(Duration duration) {
                return format(duration);
            }

            @Override
            public String decorate(Duration duration, String time) {
                return time;
            }

            @Override
            public String decorateUnrounded(Duration duration, String time) {
                return decorate(duration, time);
            }
        });

    }

}
