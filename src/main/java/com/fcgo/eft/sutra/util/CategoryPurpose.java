package com.fcgo.eft.sutra.util;

public class CategoryPurpose {
    public static String get(String categoryPurpose) {
        return switch (categoryPurpose) {
            case "1", "2", "3" -> "SALC";
            case "4" -> "GOVT";
            case "5" -> "GSAL";
            case "6" -> "GTAX";
            default -> "G2GP";
        };
    }
}
