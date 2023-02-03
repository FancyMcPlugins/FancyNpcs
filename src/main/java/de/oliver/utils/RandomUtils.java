package de.oliver.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    public static boolean random(double percentage) {
        if (ThreadLocalRandom.current().nextDouble(0, 100) <= percentage) return true;
        return false;
    }

    public static double randomInRange(double min, double max) {
        return (ThreadLocalRandom.current().nextDouble() * (max - min))+min;
    }

}
