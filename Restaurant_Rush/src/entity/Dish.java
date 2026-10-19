
package entity;

public class Dish implements MenuItem {

    private String name;
    private int priceCents;
    private int preparationUnits;

    public Dish(String name, int priceCents, int preparationUnits) {
        this.name = name;
        this.priceCents = priceCents;
        this.preparationUnits = preparationUnits;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPriceCents() {
        return priceCents;
    }

    @Override
    public int getPreparationUnits() {
        return preparationUnits;
    }
}
