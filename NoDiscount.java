public class NoDiscount extends Discount {
    @Override public double apply(double amount) { return amount; }
    @Override public String label() { return "No discount"; }
}
