import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShoppingCartAdvancedScenariosTest {

    @Test
    void emptyCartHasZeroState() {
        ShoppingCart cart = new ShoppingCart();

        assertCartState(cart, 0, 0.0, 0.0);
    }

    @Test
    void removingMissingItemReturnsFalseWithoutChangingCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Book", 10.0);

        assertFalse(cart.removeItem("Pen"));

        assertCartState(cart, 1, 10.0, 10.0);
    }

    @Test
    void removingSameItemTwiceReportsSecondRemovalAsMissing() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Book", 10.0);

        assertTrue(cart.removeItem("Book"));
        assertFalse(cart.removeItem("Book"));

        assertCartState(cart, 0, 0.0, 0.0);
    }

    @Test
    void readingDiscountRepeatedlyDoesNotCompoundOrChangeBaseTotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Bag", 120.0);

        assertEquals(108.0, cart.getTotalWithDiscount());
        assertEquals(108.0, cart.getTotalWithDiscount());

        assertEquals(120.0, cart.getTotal());
        assertEquals(1, cart.getItemCount());
    }

    @ParameterizedTest
    @CsvSource({
            "99.99, 99.99",
            "100.0, 100.0",
            "100.01, 90.009"
    })
    void discountBehaviorAroundThreshold(double total, double expectedResult) {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Order", total);

        assertEquals(expectedResult, cart.getTotalWithDiscount());
    }

    @Test
    void addUpdateAndDiscountSequenceUsesLatestState() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("A", 60.0);
        cart.addItem("B", 50.0);

        assertEquals(99.0, cart.getTotalWithDiscount());

        cart.updateItemPrice("A", 40.0);

        assertCartState(cart, 2, 90.0, 90.0);
    }

    @Test
    void removedItemIsNotRecreatedByUpdate() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Book", 10.0);

        assertTrue(cart.removeItem("Book"));
        cart.updateItemPrice("Book", 20.0);

        assertCartState(cart, 0, 0.0, 0.0);
    }

    @Test
    void duplicateAddUpdateAndRemoveFollowSingleEntryLifecycle() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Book", 10.0);
        cart.addItem("Book", 15.0);
        cart.updateItemPrice("Book", 20.0);

        assertCartState(cart, 1, 20.0, 20.0);

        assertTrue(cart.removeItem("Book"));
        assertCartState(cart, 0, 0.0, 0.0);
    }

    @Test
    void decimalValuesRemainStableAcrossUpdateAndRemoval() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.1);
        cart.addItem("Banana", 1.0);

        cart.updateItemPrice("Banana", 0.2);
        assertEquals(0.3, cart.getTotal());

        assertTrue(cart.removeItem("Apple"));
        assertCartState(cart, 1, 0.2, 0.2);
    }

    @Test
    void largeFinitePriceKeepsFiniteDiscount() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Server", Double.MAX_VALUE);

        double discounted = cart.getTotalWithDiscount();

        assertEquals(Double.MAX_VALUE, cart.getTotal());
        assertTrue(Double.isFinite(discounted));
        assertTrue(discounted < cart.getTotal());
    }

    @Test
    void failedRemovalDoesNotFreeCapacity() {
        ShoppingCart cart = new ShoppingCart();
        for (int index = 0; index < 10; index++) {
            cart.addItem("Item-" + index, 1.0);
        }

        assertFalse(cart.removeItem("Missing"));
        assertThrows(IllegalStateException.class,
                () -> cart.addItem("Overflow", 1.0));

        assertCartState(cart, 10, 10.0, 10.0);
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
