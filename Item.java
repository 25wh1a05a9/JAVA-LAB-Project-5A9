public class Item {
    private final int id;
    private final String name, category;
    private final double price;
    private final boolean veg;

    public Item(int id, String name, double price, String category, boolean veg) {
        this.id = id; this.name = name; this.price = price; this.category = category; this.veg = veg;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public boolean isVeg() { return veg; }

    @Override public String toString() {
        return String.format("%-4d %-22s %-10s %-8s Rs.%.2f", id, name, category, veg ? "Veg" : "Non-veg", price);
    }
}
