package game.toweoftrials.model;

public class Item {
    public enum ItemRarity {
        COMMON, RARE, EPIC, LEGENDARY
    }

    private String name;
    private ItemRarity rarity;
    private String description;

    public Item(String name, ItemRarity rarity, String description) {
        this.name = name;
        this.rarity = rarity;
        this.description = description;
    }

    public String getName() { return name; }
    public ItemRarity getRarity() { return rarity; }
    public String getDescription() { return description; }
}
