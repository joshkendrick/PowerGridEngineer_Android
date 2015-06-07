package com.thirtyonetensoftware.android.powergridengineer.model;

/**
 * Area
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public enum Region {

    PURPLE(1),
    YELLOW(2),
    BROWN(3),
    TEAL(4),
    RED(5),
    GREEN(6),
    BLUE(7);

    private final int value;

    Region(int value) {
        this.value = value;
    }

    public static Region valueOf(int region) {
        for ( Region r : Region.values() ) {
            if ( r.value == region ) {
                return r;
            }
        }

        throw new IllegalArgumentException("Country not found");
    }

    public int getValue() {
        return value;
    }
}
