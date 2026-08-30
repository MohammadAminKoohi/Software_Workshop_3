import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartHiddenBugsTest {

    @Test
    void decimalItemPricesHaveStableTotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.1);
        cart.addItem("Banana", 0.2);

        assertEquals(0.3, cart.getTotal());
    }

    @Test
    void decimalDiscountHasStableResult() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Headphones", 100.22);

        assertEquals(90.198, cart.getTotalWithDiscount());
    }
}
