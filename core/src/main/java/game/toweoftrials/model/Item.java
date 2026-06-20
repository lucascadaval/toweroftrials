package game.toweoftrials.model;

public class Item {
    public enum ItemType {
        SWORD, SHIELD, HELMET, ARMOR, RING, NECKLACE
    }

    public enum ItemRarity {
        COMMON, RARE, EPIC, LEGENDARY
    }

    private final String name;
    private final ItemType type;
    private final ItemRarity rarity;
    private final String description;
    private final String iconPath;
    
    // Stats
    public int hpBonus;
    public int attackBonus;
    public int defenseBonus;
    public int speedBonus;

    public Item(String name, ItemType type, ItemRarity rarity, String description, String iconPath) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.description = description;
        this.iconPath = iconPath;
    }

    public String getName() { return name; }
    public ItemType getType() { return type; }
    public ItemRarity getRarity() { return rarity; }
    public String getDescription() { return description; }
    public String getIconPath() { return iconPath; }
    
    public String getFullIconPath() {
        String folder = "";
        switch (type) {
            case SWORD: folder = "swords"; break;
            case SHIELD: folder = "shields"; break;
            case HELMET: folder = "helmets"; break;
            case ARMOR: folder = "armors"; break;
            case RING: folder = "rings"; break;
            case NECKLACE: folder = "necklaces"; break;
        }
        return "items/" + folder + "/" + iconPath;
    }
    
    public Item setStats(int hp, int atk, int def, int spd) {
        this.hpBonus = hp;
        this.attackBonus = atk;
        this.defenseBonus = def;
        this.speedBonus = spd;
        return this;
    }
}
