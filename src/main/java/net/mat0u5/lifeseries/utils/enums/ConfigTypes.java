package net.mat0u5.lifeseries.utils.enums;

public enum ConfigTypes {
    NULL(""),

    STRING("string"),
    BOOLEAN("boolean"),
    INTEGER("integer"),
    DOUBLE("double"),
    TEXT("text"),

    HEARTS("hearts"),
    PERCENTAGE("percentage"),

    GROUP("group");

    private final String text;
    ConfigTypes(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public boolean parentString() {
        return this == STRING;
    }
    public boolean parentText() {
        return this == TEXT;
    }
    public boolean parentBoolean() {
        return this == BOOLEAN;
    }
    public boolean parentInteger() {
        return this == INTEGER || this == HEARTS;
    }
    public boolean parentDouble() {
        return this == DOUBLE || this == PERCENTAGE;
    }

    public static ConfigTypes getFromString(String string) {
        if (string.equalsIgnoreCase(STRING.toString())) return STRING;
        if (string.equalsIgnoreCase(BOOLEAN.toString())) return BOOLEAN;
        if (string.equalsIgnoreCase(INTEGER.toString())) return INTEGER;
        if (string.equalsIgnoreCase(DOUBLE.toString())) return DOUBLE;
        if (string.equalsIgnoreCase(TEXT.toString())) return TEXT;
        if (string.equalsIgnoreCase(HEARTS.toString())) return HEARTS;
        if (string.equalsIgnoreCase(PERCENTAGE.toString())) return PERCENTAGE;
        return NULL;
    }
}
