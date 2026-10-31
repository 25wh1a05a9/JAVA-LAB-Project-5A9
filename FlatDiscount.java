public class FlatDiscount extends Discount {
    private final double off;
    public FlatDiscount(double off) { this.off = off; }
    @Override public double apply(double amount) { return Math.max(0, amount - off); }
    @Override public String label() { return String.format("Flat Rs.%.0f off", off); }
}
