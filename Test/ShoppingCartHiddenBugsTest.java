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
}
