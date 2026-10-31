public class PercentDiscount extends Discount {
    private final double pct;
    public PercentDiscount(double pct) { this.pct = pct; }
    @Override public double apply(double amount) { return amount * (1 - pct / 100); }
    @Override public String label() { return String.format("%.0f%% off", pct); }
}
