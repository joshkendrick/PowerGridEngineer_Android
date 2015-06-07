package com.thirtyonetensoftware.android.powergridengineer.model;

/**
 * Country
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 */
public enum Country {

    UNITED_STATES(1),
    GERMANY(2);

    private final int value;

    Country(int value) {
        this.value = value;
    }

    public static Country valueOf(int country) {
        for ( Country c : Country.values() ) {
            if ( c.value == country ) {
                return c;
            }
        }

        throw new IllegalArgumentException("Country not found");
    }

    public int getValue() {
        return value;
    }
}
