package com.sllibrary.util.math;

public class Numbers {

    private Numbers() {
    }

    public static boolean isNegative(double d) {
        return Double.doubleToRawLongBits(d) < 0;
    }
}
