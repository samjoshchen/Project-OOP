package com.martminds.enums;

public enum Category {
    APPAREL("Apparel"),
    ACCESSORIES("Accessories"),
    ELECTRONICS("Electronics"),
    BOOKS("Books"),
    HOME_GARDEN("Home & Garden"),
    SPORTS_OUTDOORS("Sports & Outdoors"),
    TOYS_GAMES("Toys & Games"),
    BEAUTY_PERSONAL_CARE("Beauty & Personal Care"),
    FOOD_BEVERAGES("Food & Beverages"),
    MUSIC_ENTERTAINMENT("Music & Entertainment");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromDisplayName(String displayName) {
        for (Category category : Category.values()) {
            if (category.displayName.equalsIgnoreCase(displayName)) {
                return category;
            }
        }
        return null;
    }

    public static Category fromNumber(int number) {
        if (number < 1 || number > values().length) {
            return null;
        }
        return values()[number - 1];
    }

    @Override
    public String toString() {
        return displayName;
    }
}
