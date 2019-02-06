package DyeMaker;

//TODO: use a more generalized name

import java.util.Arrays;

public enum Dyes {
    //Dyes
    RED("Red Dye", 1763, 1951, 1234, 3),
    BLUE("Blue Dye", 1767, 1793, 5678, 2),
    YELLOW("Yellow Dye", 1765, 1957, 3366, 2),
    ONIONS("Onions", 1957, 1957, 3366, 0); //TODO: change format

    public final String name;
    public final int amountRequired;
    public final int dyeId;
    public final int ingredientItemId;
    public final int ingredientObjectId;

    Dyes(String name, int dyeId, int ingredientItemId, int ingredientObjectId, int amt) {
        this.name = name;
        this.dyeId = dyeId;
        this.ingredientItemId = ingredientItemId;
        this.ingredientObjectId = ingredientObjectId;
        this.amountRequired = amt;
    }

    //returns array of values for selected enum
    public static Dyes forName(String name) {
        return Arrays.stream(Dyes.values()).filter(f -> f.name.equals(name)).findFirst().orElse(null);
    }

    //getters

    public String getName() {
        return this.name;
    }

    public int getAmountRequired() {
        return this.amountRequired;
    }

    public int getDyeId() {
        return this.dyeId;
    }

    public int getingredientItemId() {
        return this.ingredientItemId;
    }

    public int getingredientObjectId() {
        return this.ingredientObjectId;
    }

}