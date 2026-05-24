package game.toweoftrials.model;

public class Item {
    public enum ItemRarity {
        COMMON, RARE, EPIC, LEGENDARY
    }

    private final String name;
    private final ItemRarity rarity;
    private final String description;

    public Item(String name, ItemRarity rarity, String description) {
        this.name = name;
        this.rarity = rarity;
        this.description = description;
    }

    public String getName() { return name; }
}
