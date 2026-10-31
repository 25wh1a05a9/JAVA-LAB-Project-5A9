import java.util.Map;

public interface Restaurant {
    double TAX_RATE = 0.06;

    void printName();
    void addItem(Item item) throws InvalidOrderException;
    Item[] getMenu();
    Order placeOrder(Map<Integer, Integer> cart, Discount d) throws InvalidOrderException;
    double generateBill(int orderId) throws InvalidOrderException;   // total incl. 6% tax

    String billText(int orderId) throws InvalidOrderException;
    Item findByName(String name);                                    // HashMap lookup
    Map<String, Integer> getSalesCount();                            // HashMap<String,Integer>
}
