package ru.vsu.cs.tolkacheva_u_a.task1.util;

public final class StringUtils {
    private StringUtils() {}

    public static boolean isNullOrBlank(String source) {
        return source == null || source.isBlank();
    }
}
