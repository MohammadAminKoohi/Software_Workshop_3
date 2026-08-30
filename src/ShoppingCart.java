import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private Map<String, Double> items = new HashMap<>();

    public void addItem(String name, double price) {
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

    public void updateItemPrice(String name, int newPrice) {}

}
