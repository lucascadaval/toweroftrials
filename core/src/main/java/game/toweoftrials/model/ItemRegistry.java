package game.toweoftrials.model;

import com.badlogic.gdx.utils.ObjectMap;

public class ItemRegistry {
    private static final ObjectMap<String, Item> items = new ObjectMap<>();

    static {
        // Swords
        reg(new Item("Rusted Gremlin Blade", Item.ItemType.SWORD, Item.ItemRarity.COMMON, "A jagged, rusted blade.", "rusted_gremlin_blade.png").setStats(0, 5, 0, 0));
        reg(new Item("Piercing Gaze Rapier", Item.ItemType.SWORD, Item.ItemRarity.RARE, "It feels like it's watching you.", "piercing_gaze_rapier.png").setStats(0, 12, 0, 5));
        reg(new Item("Sharp Chitin Scimitar", Item.ItemType.SWORD, Item.ItemRarity.RARE, "Carved from a giant crab claw.", "sharp_chitin_scimitar.png").setStats(0, 15, 2, 0));
        reg(new Item("Plague Doctor's Scalpel", Item.ItemType.SWORD, Item.ItemRarity.EPIC, "Sharp enough to cut through souls.", "plague_doctors_scalpel.png").setStats(0, 25, 0, 10));
        reg(new Item("Giant Ogre Fang", Item.ItemType.SWORD, Item.ItemRarity.EPIC, "A massive, heavy tooth.", "giant_ogre_fang.png").setStats(0, 35, 5, -5));
        reg(new Item("Swamp Light Katana", Item.ItemType.SWORD, Item.ItemRarity.EPIC, "Glows with a faint green light.", "swamp_light_katana.png").setStats(0, 30, 0, 15));
        reg(new Item("Black Marble Sword", Item.ItemType.SWORD, Item.ItemRarity.EPIC, "Cold and unyielding.", "black_marble_sword.png").setStats(20, 40, 10, 0));
        reg(new Item("Knight's Soul Blade", Item.ItemType.SWORD, Item.ItemRarity.LEGENDARY, "The ultimate weapon.", "knights_soul_blade.png").setStats(50, 60, 20, 20));

        // Armors
        reg(new Item("Thick Slime Robe", Item.ItemType.ARMOR, Item.ItemRarity.COMMON, "Sticky but protective.", "thick_slime_robe.png").setStats(20, 0, 5, -2));
        reg(new Item("Demonic Observer Mantle", Item.ItemType.ARMOR, Item.ItemRarity.RARE, "Covered in unblinking eyes.", "demonic_observer_mantle.png").setStats(40, 5, 10, 0));
        reg(new Item("Salty Scale Cuirass", Item.ItemType.ARMOR, Item.ItemRarity.RARE, "Hardened by the deep sea.", "salty_scale_cuirass.png").setStats(50, 0, 15, 0));
        reg(new Item("Ancient Leather Robes", Item.ItemType.ARMOR, Item.ItemRarity.EPIC, "Smells like old books.", "ancient_leather_robes.png").setStats(60, 10, 20, 5));
        reg(new Item("Cobra Scale Breastplate", Item.ItemType.ARMOR, Item.ItemRarity.EPIC, "Vibrant and dangerous.", "cobra_scale_breastplate.png").setStats(80, 15, 25, 10));
        reg(new Item("Cold-Blooded O-Yoroi", Item.ItemType.ARMOR, Item.ItemRarity.EPIC, "Traditional samurai armor.", "cold_blooded_o_yoroi.png").setStats(100, 20, 35, 0));
        reg(new Item("Mage's Ectoplasmic Tailcoat", Item.ItemType.ARMOR, Item.ItemRarity.EPIC, "Semi-transparent and spooky.", "mages_ectoplasmic_tailcoat.png").setStats(70, 30, 20, 15));
        reg(new Item("Eternal Bone Shroud", Item.ItemType.ARMOR, Item.ItemRarity.LEGENDARY, "Death's own embrace.", "eternal_bone_shroud.png").setStats(200, 40, 50, 20));

        // Helmets
        reg(new Item("Drenched Cloth Hood", Item.ItemType.HELMET, Item.ItemRarity.COMMON, "Soaked in sewer water.", "drenched_cloth_hood.png").setStats(5, 0, 2, 0));
        reg(new Item("Mask of Multiple Glances", Item.ItemType.HELMET, Item.ItemRarity.RARE, "See everything at once.", "mask_of_multiple_glances.png").setStats(10, 2, 5, 5));
        reg(new Item("Jagged Coral Helmet", Item.ItemType.HELMET, Item.ItemRarity.RARE, "Watch your ears.", "jagged_coral_helmet.png").setStats(15, 0, 10, -2));
        reg(new Item("Crow Beak Mask", Item.ItemType.HELMET, Item.ItemRarity.EPIC, "Classic plague doctor look.", "crow_beak_mask.png").setStats(20, 5, 12, 10));
        reg(new Item("Crawling Beast Skull", Item.ItemType.HELMET, Item.ItemRarity.EPIC, "The head of a jungle beast.", "crawling_beast_skull.png").setStats(30, 10, 15, 0));
        reg(new Item("Mammoth Ivory Kabuto", Item.ItemType.HELMET, Item.ItemRarity.EPIC, "Incredibly heavy and sturdy.", "mammoth_ivory_kabuto.png").setStats(50, 0, 25, -10));
        reg(new Item("Fierce Shadows Hood", Item.ItemType.HELMET, Item.ItemRarity.EPIC, "Hides your face in darkness.", "fierce_shadows_hood.png").setStats(40, 15, 10, 20));
        reg(new Item("Lich King Crown", Item.ItemType.HELMET, Item.ItemRarity.LEGENDARY, "The crown of the dead king.", "lich_king_crown.png").setStats(100, 30, 30, 10));

        // Shields
        reg(new Item("Battered Manhole Cover", Item.ItemType.SHIELD, Item.ItemRarity.COMMON, "A heavy iron disk.", "battered_manhole_cover.png").setStats(10, 0, 8, -5));
        reg(new Item("Stitched Eyelid Aegis", Item.ItemType.SHIELD, Item.ItemRarity.RARE, "Grotesque but effective.", "stitched_eyelid_aegis.png").setStats(20, 2, 15, 0));
        reg(new Item("Reinforced Chitin Shield", Item.ItemType.SHIELD, Item.ItemRarity.RARE, "A giant lobster shell.", "reinforced_chitin_shield.png").setStats(25, 0, 20, 0));
        reg(new Item("Petrified Spell Tome", Item.ItemType.SHIELD, Item.ItemRarity.EPIC, "A book turned to stone.", "petrified_tome.png").setStats(30, 10, 25, 0));
        reg(new Item("Old Warrior Shield", Item.ItemType.SHIELD, Item.ItemRarity.EPIC, "Dented from many battles.", "old_warrior_shield.png").setStats(50, 5, 40, -5));
        reg(new Item("Reptilian Plate Shield", Item.ItemType.SHIELD, Item.ItemRarity.EPIC, "Tough scales on wood.", "reptilian_plate_shield.png").setStats(60, 0, 35, 5));
        reg(new Item("Cursed Family Shield", Item.ItemType.SHIELD, Item.ItemRarity.EPIC, "Whispers to you.", "cursed_family_shield.png").setStats(40, 20, 30, 0));
        reg(new Item("Desecrated Ribcage Barricade", Item.ItemType.SHIELD, Item.ItemRarity.LEGENDARY, "A wall of ancient bones.", "desecrated_ribcage_barricade.png").setStats(150, 10, 70, 0));

        // Rings
        reg(new Item("Oxidized Copper Ring", Item.ItemType.RING, Item.ItemRarity.COMMON, "Cheap and green.", "oxidized_copper_ring.png").setStats(0, 1, 1, 1));
        reg(new Item("Slit Pupil Ring", Item.ItemType.RING, Item.ItemRarity.RARE, "The eye follows movement.", "slit_pupil_ring.png").setStats(0, 5, 0, 10));
        reg(new Item("Abyssal Tide Ring", Item.ItemType.RING, Item.ItemRarity.RARE, "Cold to the touch.", "abyssal_tide_ring.png").setStats(10, 5, 5, 0));
        reg(new Item("Toxic Blossom Ring", Item.ItemType.RING, Item.ItemRarity.EPIC, "Smells dangerously sweet.", "toxic_blossom_ring.png").setStats(0, 15, 0, 5));
        reg(new Item("Lethal Stinger Ring", Item.ItemType.RING, Item.ItemRarity.EPIC, "A sharp, venomous point.", "lethal_stinger_ring.png").setStats(0, 20, 0, 0));
        reg(new Item("Fallen Comet Ring", Item.ItemType.RING, Item.ItemRarity.EPIC, "Still radiates star heat.", "fallen_comet_ring.png").setStats(20, 20, 0, 0));
        reg(new Item("Phantom Roar Ring", Item.ItemType.RING, Item.ItemRarity.EPIC, "Vibrates with energy.", "phantom_roar_ring.png").setStats(0, 25, 0, 10));
        reg(new Item("Perpetual Void Ring", Item.ItemType.RING, Item.ItemRarity.LEGENDARY, "A ring that consumes light.", "perpetual_void_ring.png").setStats(50, 40, 10, 10));

        // Necklaces
        reg(new Item("Toxic Bubble Amulet", Item.ItemType.NECKLACE, Item.ItemRarity.COMMON, "Contains a green gas.", "toxic_bubble_amulet.png").setStats(10, 2, 0, 0));
        reg(new Item("Fallen Angel Tear Pendant", Item.ItemType.NECKLACE, Item.ItemRarity.RARE, "A crystalized blue drop.", "fallen_angel_tear_pendant.png").setStats(20, 5, 5, 0));
        reg(new Item("Deep Sea Choker", Item.ItemType.NECKLACE, Item.ItemRarity.RARE, "Made of glowing shells.", "deep_sea_choker.png").setStats(15, 0, 10, 5));
        reg(new Item("Ash and Frost Reliquary", Item.ItemType.NECKLACE, Item.ItemRarity.EPIC, "Contrast of temperatures.", "ash_and_frost_reliquary.png").setStats(30, 10, 10, 0));
        reg(new Item("Hive Queen Amber Amulet", Item.ItemType.NECKLACE, Item.ItemRarity.EPIC, "A bee trapped in amber.", "hive_queen_amber_amulet.png").setStats(20, 20, 5, 5));
        reg(new Item("Ancient Shogun Medalion", Item.ItemType.NECKLACE, Item.ItemRarity.EPIC, "Symbol of authority.", "ancient_shogun_medalion.png").setStats(40, 15, 15, 0));
        reg(new Item("Bloodstained Silver Necklace", Item.ItemType.NECKLACE, Item.ItemRarity.EPIC, "Old and cursed silver.", "bloodstained_silver_necklace.png").setStats(0, 30, 10, 10));
        reg(new Item("Reliquary of Chained Souls", Item.ItemType.NECKLACE, Item.ItemRarity.LEGENDARY, "You hear faint screams.", "reliquary_of_chained_souls.png").setStats(100, 50, 20, 0));
    }

    private static void reg(Item item) {
        items.put(item.getName(), item);
    }

    public static Item get(String name) {
        return items.get(name);
    }
    
    public static ObjectMap<String, Item> getAll() {
        return items;
    }
}
