package com.refinedmods.refinedstorage.the_vault;

import java.util.List;
import java.util.Objects;

public class JewelAttribute {

    public static final List<JewelAttribute> ATTRIBUTES = List.of(
            new JewelAttribute("the_vault:jewel_size", Float.class, 218, 218, 218),
            new JewelAttribute("the_vault:wooden_affinity", Boolean.class, 178, 88, 11),
            new JewelAttribute("the_vault:ornate_affinity", Boolean.class, 233, 37, 37),
            new JewelAttribute("the_vault:gilded_affinity", Boolean.class, 202, 161, 18),
            new JewelAttribute("the_vault:living_affinity", Boolean.class, 113, 252, 64),
            new JewelAttribute("the_vault:coin_affinity", Boolean.class, 252, 252, 0),
            new JewelAttribute("the_vault:picking", Boolean.class, 231, 231, 231),
            new JewelAttribute("the_vault:axing", Boolean.class, 194, 170, 120),
            new JewelAttribute("the_vault:shovelling", Boolean.class, 223, 227, 157),
            new JewelAttribute("the_vault:hammer_size", Integer.class, 26, 145, 113),
            new JewelAttribute("the_vault:mining_speed", Float.class, 71, 184, 245),
            new JewelAttribute("the_vault:copiously", Float.class, 244, 70, 126),
            new JewelAttribute("the_vault:item_quantity", Float.class, 232, 138, 18),
            new JewelAttribute("the_vault:item_rarity", Float.class, 229, 184, 18),
            new JewelAttribute("the_vault:durability", Integer.class, 223, 208, 254),
            new JewelAttribute("the_vault:trap_disarming", Float.class, 127, 66, 252),
            new JewelAttribute("the_vault:reach", Float.class, 132, 215, 255),
            new JewelAttribute("the_vault:immortality", Float.class, 173, 139, 193),
            new JewelAttribute("the_vault:soulbound", Boolean.class, 150, 100, 253),
            new JewelAttribute("the_vault:pulverizing", Boolean.class, 114, 177, 114),
            new JewelAttribute("the_vault:smelting", Boolean.class, 252, 68, 0)
    );

    public static JewelAttribute of(String id) {
        return ATTRIBUTES.stream()
                .filter(attr -> Objects.equals(attr.id, id))
                .findFirst().get();
    }

    public final String id;
    public final Class<?> valueType;

    public final int red;
    public final int green;
    public final int blue;

    protected JewelAttribute(String id, Class<?> valueType, int red, int green, int blue) {
        this.id = id;
        this.valueType = valueType;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int intColor() {
        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }

}
