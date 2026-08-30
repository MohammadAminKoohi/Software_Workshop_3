import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShoppingCartConstraintsTest {

    @Test
    void allowsTenDistinctItems() {
        ShoppingCart cart = new ShoppingCart();

        fillToCapacity(cart);

        assertCartState(cart, 10, 10.0, 10.0);
    }

    @Test
    void rejectsEleventhDistinctItemWithoutChangingCart() {
        ShoppingCart cart = fullCart();

        assertThrows(IllegalStateException.class,
                () -> cart.addItem("Overflow", 5.0));

        assertCartState(cart, 10, 10.0, 10.0);
    }

    @Test
    void replacesExistingItemAtCapacity() {
        ShoppingCart cart = fullCart();

        cart.addItem("Item-0", 20.0);

        assertCartState(cart, 10, 29.0, 29.0);
    }

    @Test
    void removalFreesSlotForAnotherDistinctItem() {
        ShoppingCart cart = fullCart();

        assertTrue(cart.removeItem("Item-0"));
        cart.addItem("Replacement", 2.0);

        assertCartState(cart, 10, 11.0, 11.0);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsInvalidNameWithoutChangingCart(String invalidName) {
        ShoppingCart cart = cartWithOneItem();

        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem(invalidName, 5.0));

        assertCartState(cart, 1, 10.0, 10.0);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.01})
    void rejectsNonPositivePriceWithoutChangingCart(double invalidPrice) {
        ShoppingCart cart = cartWithOneItem();

        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("Invalid", invalidPrice));

        assertCartState(cart, 1, 10.0, 10.0);
    }

    @ParameterizedTest
    @ValueSource(doubles = {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
    })
    void rejectsNonFinitePriceWithoutChangingCart(double invalidPrice) {
        ShoppingCart cart = cartWithOneItem();

        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("Invalid", invalidPrice));

        assertCartState(cart, 1, 10.0, 10.0);
    }

    @Test
    void validatesNameBeforeCheckingCapacity() {
        ShoppingCart cart = fullCart();

        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("   ", 5.0));

        assertCartState(cart, 10, 10.0, 10.0);
    }

    @Test
    void validatesPriceBeforeCheckingCapacity() {
        ShoppingCart cart = fullCart();

        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("Overflow", -1.0));

        assertCartState(cart, 10, 10.0, 10.0);
    }

    @Test
    void invalidDuplicatePriceDoesNotReplaceExistingValue() {
        ShoppingCart cart = fullCart();

        assertThrows(IllegalArgumentException.class,
                () -> cart.addItem("Item-0", 0.0));

        assertCartState(cart, 10, 10.0, 10.0);
    }

    private ShoppingCart fullCart() {
        ShoppingCart cart = new ShoppingCart();
        fillToCapacity(cart);
        return cart;
    }

    private ShoppingCart cartWithOneItem() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Book", 10.0);
        return cart;
    }

    private void fillToCapacity(ShoppingCart cart) {
        for (int index = 0; index < 10; index++) {
            cart.addItem("Item-" + index, 1.0);
        }
    }

    private void assertCartState(
            ShoppingCart cart,
            int expectedCount,
            double expectedTotal,
            double expectedDiscountedTotal
    ) {
        assertEquals(expectedCount, cart.getItemCount());
        assertEquals(expectedTotal, cart.getTotal());
        assertEquals(expectedDiscountedTotal, cart.getTotalWithDiscount());
    }
}
