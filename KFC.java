import java.util.*;

public class KFC implements Restaurant {
    private final String name;
    private Item[] menu = new Item[10];                               // item array (Prog 12)
    private int count = 0, nextOrderId = 1001;
    private final Map<Integer, Order> orders = new LinkedHashMap<>();
    private final Map<String, Item> byName = new HashMap<>();          // Prog 19
    private final Map<String, Integer> salesCount = new HashMap<>();   // Prog 22

    public KFC() {
        this("KFC");
        load(new Item(1, "Zinger Burger", 189, "Burgers", false),
             new Item(2, "Veg Burger", 119, "Burgers", true),
             new Item(3, "Hot Wings (6)", 159, "Chicken", false),
             new Item(4, "Popcorn Chicken", 149, "Chicken", false),
             new Item(5, "Fries", 89, "Sides", true),
             new Item(6, "Coleslaw", 59, "Sides", true),
             new Item(7, "Pepsi", 69, "Drinks", true));
    }

    protected KFC(String name) { this.name = name; }

    protected void load(Item... items) {
        for (Item i : items) {
            try { addItem(i); } catch (InvalidOrderException e) { System.out.println("Skipped: " + e.getMessage()); }
        }
    }

    private static String key(String s) { return s.trim().toLowerCase(); }

    private Item findById(int id) {
        for (int i = 0; i < count; i++) if (menu[i].getId() == id) return menu[i];
        return null;
    }

    @Override public void printName() { System.out.println("Restaurant: " + name); }

    @Override public void addItem(Item item) throws InvalidOrderException {
        if (item == null || item.getName() == null || item.getName().trim().isEmpty())
            throw new InvalidOrderException("Item name cannot be empty.");
        if (item.getPrice() <= 0) throw new InvalidOrderException("Price must be positive.");
        if (findById(item.getId()) != null || byName.containsKey(key(item.getName())))
            throw new InvalidOrderException("Item already exists: " + item.getName());
        if (count == menu.length) menu = Arrays.copyOf(menu, count * 2);
        menu[count++] = item;
        byName.put(key(item.getName()), item);
    }

    @Override public Item[] getMenu() { return Arrays.copyOf(menu, count); }

    @Override public Order placeOrder(Map<Integer, Integer> cart, Discount d) throws InvalidOrderException {
        if (cart == null || cart.isEmpty()) throw new InvalidOrderException("Cart is empty.");
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {          // validate first
            if (findById(e.getKey()) == null) throw new InvalidOrderException("No item with id " + e.getKey());
            if (e.getValue() <= 0) throw new InvalidOrderException("Quantity must be positive for item " + e.getKey());
        }
        Order o = new Order(nextOrderId++, cart, d == null ? new NoDiscount() : d);
        for (Map.Entry<Integer, Integer> e : cart.entrySet())
            salesCount.merge(findById(e.getKey()).getName(), e.getValue(), Integer::sum);
        orders.put(o.getOrderId(), o);
        return o;
    }

    private Order getOrder(int id) throws InvalidOrderException {
        Order o = orders.get(id);
        if (o == null) throw new InvalidOrderException("No order with id " + id);
        return o;
    }

    private double subtotal(Order o) {
        double sum = 0;
        for (Map.Entry<Integer, Integer> e : o.getLines().entrySet())
            sum += findById(e.getKey()).getPrice() * e.getValue();
        return sum;
    }

    @Override public double generateBill(int orderId) throws InvalidOrderException {
        Order o = getOrder(orderId);
        double net = o.getDiscount().apply(subtotal(o));
        return net + net * TAX_RATE;
    }

    @Override public String billText(int orderId) throws InvalidOrderException {
        Order o = getOrder(orderId);
        double sub = subtotal(o), net = o.getDiscount().apply(sub), tax = net * TAX_RATE;
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================\n");
        sb.append(String.format("  %s  |  Order #%d%n", name, orderId));
        sb.append("==============================================\n");
        for (Map.Entry<Integer, Integer> e : o.getLines().entrySet()) {
            Item i = findById(e.getKey());
            sb.append(String.format(Locale.US, "%-18s %2d x %7.2f = %9.2f%n", i.getName(), e.getValue(), i.getPrice(), i.getPrice() * e.getValue()));
        }
        sb.append("----------------------------------------------\n");
        sb.append(String.format(Locale.US, "%-30s %13.2f%n", "Subtotal", sub));
        sb.append(String.format(Locale.US, "%-30s %13.2f%n", "Discount (" + o.getDiscount().label() + ")", -(sub - net)));
        sb.append(String.format(Locale.US, "%-30s %13.2f%n", "Tax (6%)", tax));
        sb.append("----------------------------------------------\n");
        sb.append(String.format(Locale.US, "%-30s %13.2f%n", "TOTAL (Rs.)", net + tax));
        sb.append("==============================================\n");
        return sb.toString();
    }

    @Override public Item findByName(String n) { return byName.get(key(n)); }
    @Override public Map<String, Integer> getSalesCount() { return salesCount; }
}
