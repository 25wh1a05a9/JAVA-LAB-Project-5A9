import java.util.*;

public class Order {
    private final int orderId;
    private final Map<Integer, Integer> lines;   // itemId -> quantity
    private final Discount discount;

    public Order(int orderId, Map<Integer, Integer> lines, Discount discount) {
        this.orderId = orderId;
        this.lines = new LinkedHashMap<>(lines);
        this.discount = discount;
    }
    public int getOrderId() { return orderId; }
    public Map<Integer, Integer> getLines() { return Collections.unmodifiableMap(lines); }
    public Discount getDiscount() { return discount; }
}
