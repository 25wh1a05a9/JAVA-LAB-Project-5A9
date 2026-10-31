public class Dominos extends KFC {
    public Dominos() {
        super("Domino's");
        load(new Item(1, "Margherita Pizza", 199, "Pizza", true),
             new Item(2, "Peppy Paneer Pizza", 289, "Pizza", true),
             new Item(3, "Chicken Dominator", 399, "Pizza", false),
             new Item(4, "Garlic Bread", 129, "Sides", true),
             new Item(5, "Choco Lava Cake", 109, "Dessert", true),
             new Item(6, "Coke", 69, "Drinks", true));
    }
}
