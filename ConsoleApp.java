import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ConsoleApp {
    private final Scanner sc;
    private Restaurant r;
    private int lastOrderId = -1;

    public ConsoleApp(Scanner sc) { this.sc = sc; }

    public void run() {
        chooseRestaurant();
        int ch;
        do {
            System.out.println("\n========= RESTAURANT SYSTEM =========");
            r.printName();
            System.out.println("1.View Menu   2.Add Item   3.Place Order   4.Generate Bill");
            System.out.println("5.Reports     6.Search     7.Change Restaurant   0.Exit");
            ch = readInt("Choice: ");
            switch (ch) {
                case 1: showMenu(); break;
                case 2: addItem(); break;
                case 3: placeOrder(); break;
                case 4: bill(); break;
                case 5: reports(); break;
                case 6: search(); break;
                case 7: chooseRestaurant(); break;
                case 0: System.out.println("Thank you!"); break;
                default: System.out.println("Invalid choice.");
            }
        } while (ch != 0);
    }

    private int readInt(String p) {
        while (true) {
            System.out.print(p);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Enter a valid whole number."); }
        }
    }

    private double readDouble(String p) {
        while (true) {
            System.out.print(p);
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Enter a valid number."); }
        }
    }

    private String readLine(String p) { System.out.print(p); return sc.nextLine().trim(); }

    private void chooseRestaurant() {
        int c = readInt("Restaurant (1 = KFC, 2 = Dominos): ");
        r = (c == 2) ? new Dominos() : new KFC();     // late binding
        r.printName();
    }

    private void showMenu() {
        System.out.printf("%-4s %-22s %-10s %-8s %s%n", "ID", "Name", "Category", "Type", "Price");
        for (Item i : r.getMenu()) System.out.println(i);
    }

    private void addItem() {
        String name = readLine("Name: ");
        double price = readDouble("Price: ");
        String cat = readLine("Category: ");
        boolean veg = readLine("Veg? (y/n): ").equalsIgnoreCase("y");
        try {
            r.addItem(new Item(Reports.nextId(r.getMenu()), name, price, cat, veg));
            System.out.println("Item added.");
        } catch (InvalidOrderException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private void placeOrder() {
        showMenu();
        Map<Integer, Integer> cart = new LinkedHashMap<>();
        while (true) {
            int id = readInt("Item id (0 to finish): ");
            if (id == 0) break;
            cart.merge(id, readInt("Quantity: "), Integer::sum);
        }
        int dc = readInt("Discount (1 = None, 2 = Flat Rs.50, 3 = 10%): ");
        Discount d = (dc == 2) ? new FlatDiscount(50) : (dc == 3) ? new PercentDiscount(10) : new NoDiscount();
        try {
            lastOrderId = r.placeOrder(cart, d).getOrderId();
            System.out.println("Order placed. Order id = " + lastOrderId);
        } catch (InvalidOrderException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private void bill() {
        int id = readInt("Order id (0 = last order): ");
        if (id == 0) id = lastOrderId;
        try {
            String text = r.billText(id);
            System.out.println(text);
            Files.createDirectories(Paths.get("bills"));
            Files.write(Paths.get("bills", "bill_" + id + ".txt"), text.getBytes());
            System.out.println("Saved to bills/bill_" + id + ".txt");
        } catch (InvalidOrderException e) { System.out.println("Error: " + e.getMessage()); }
        catch (IOException e) { System.out.println("Could not save bill: " + e.getMessage()); }
    }

    private void reports() {
        Item[] m = r.getMenu();
        System.out.println("-- Veg items --");
        Reports.veg(m).forEach(System.out::println);
        double max = readDouble("Show items priced up to Rs.: ");
        Reports.underPrice(m, max).forEach(System.out::println);
        System.out.println("Top sellers : " + Reports.topSellers(r.getSalesCount(), 3));
        System.out.println("Names (upper): " + Reports.upperNames(m));
    }

    private void search() {
        Item i = r.findByName(readLine("Item name: "));
        System.out.println(i == null ? "Not found." : i.toString());
    }
}
