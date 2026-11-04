package entity;

public class ComboMeal implements MenuItem {
    private final Dish firstMenuItem;
    private final Dish secondMenuItem;

    public ComboMeal(Dish firstMenuItem, Dish secondMenuItem){
        this.firstMenuItem = firstMenuItem;
        this.secondMenuItem = secondMenuItem;
    }

    @Override 
    public String getName(){
        return firstMenuItem.getName() + "+" + secondMenuItem.getName();
    }

    @Override 
    public int getPriceCents(){
        int total = firstMenuItem.getPriceCents() + secondMenuItem.getPriceCents();
        return (int) Math.round(total * 0.9);
    }

    @Override 
    public int getPreparationUnits(){
        return firstMenuItem.getPreparationUnits() + secondMenuItem.getPreparationUnits();
    }
}
