import java.util.*;
import java.util.stream.*;

public class Reports {
    public static List<Item> veg(Item[] m) {
        return Arrays.stream(m).filter(Item::isVeg).collect(Collectors.toList());
    }
    public static List<Item> underPrice(Item[] m, double max) {
        return Arrays.stream(m).filter(i -> i.getPrice() <= max).collect(Collectors.toList());
    }
    public static List<String> upperNames(Item[] m) {
        return Arrays.stream(m).map(i -> i.getName().toUpperCase()).collect(Collectors.toList());
    }
    public static List<String> topSellers(Map<String, Integer> sales, int n) {
        return sales.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n).map(e -> e.getKey() + " x" + e.getValue()).collect(Collectors.toList());
    }
    public static int nextId(Item[] m) {
        return Arrays.stream(m).mapToInt(Item::getId).max().orElse(0) + 1;
    }
}
