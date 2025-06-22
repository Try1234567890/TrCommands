package me.tr.trCommands.utility;

public class Utility {
    public static String toCamelCase(String text) {
        if (text == null || text.isEmpty()) return text;
        String[] parts = text.split("[ _-]+");
        StringBuilder camelCase = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            camelCase.append(parts[i].substring(0, 1).toUpperCase());
            camelCase.append(parts[i].substring(1).toLowerCase());
        }
        return camelCase.toString();
    }

    public static String toPascalCase(String text) {
        if (text == null || text.isEmpty()) return text;
        String[] parts = text.split("[ _-]+");
        StringBuilder pascalCase = new StringBuilder();
        for (String part : parts) {
            pascalCase.append(part.substring(0, 1).toUpperCase());
            pascalCase.append(part.substring(1).toLowerCase());
        }
        return pascalCase.toString();
    }

    public static String toSnakeCase(String text) {
        if (text == null || text.isEmpty()) return text;
        String[] parts = text.split("[ _-]+");
        return String.join("_", parts).toLowerCase();
    }

    public static boolean isNull(String str) {
        return str == null || str.isEmpty();
    }
}
