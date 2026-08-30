import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private static final int MAX_ITEM_COUNT = 10;
    private Map<String, Double> items = new HashMap<>();

    public void addItem(String name, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("item name must not be blank");
        }
        if (!Double.isFinite(price) || price <= 0) {
            throw new IllegalArgumentException("price must be finite and greater than zero");
        }
        if (!items.containsKey(name) && items.size() >= MAX_ITEM_COUNT) {
            throw new IllegalStateException("shopping cart is full");
        }
        items.put(name, price);
    }

    public boolean removeItem(String name) {
        if (items.containsKey(name)) {
            items.remove(name);
            return true;
        }
        return false;
    }

    public double getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (double price : items.values()) {
            total = total.add(BigDecimal.valueOf(price));
        }
        return total.doubleValue();
    }
    public double getTotalWithDiscount() {
        double total = getTotal();
        if (total > 100) {
            return BigDecimal.valueOf(total)
                    .multiply(BigDecimal.valueOf(0.9))
                    .doubleValue();
        }
        return total;
    }

    public int getItemCount() {
        return items.size();
    }

    public void updateItemPrice(String name, double newPrice) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("item name must not be blank");
        }
        if (!Double.isFinite(newPrice) || newPrice <= 0) {
            throw new IllegalArgumentException("new price must be finite and greater than zero");
        }
        items.replace(name, newPrice);
    }

}
