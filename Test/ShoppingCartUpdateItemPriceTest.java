import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShoppingCartUpdateItemPriceTest {

    @Test
    void updatesExistingItemTotal() {
        ShoppingCart cart = cartWithItem("Book", 50.0);

        cart.updateItemPrice("Book", 80.0);

        assertEquals(80.0, cart.getTotal());
    }

    @Test
    void keepsItemCountUnchanged() {
        ShoppingCart cart = cartWithItem("Pen", 10.0);

        cart.updateItemPrice("Pen", 20.0);

        assertEquals(1, cart.getItemCount());
    }

    @Test
    void recalculatesDiscountAfterCrossingAboveThreshold() {
        ShoppingCart cart = cartWithItem("Bag", 90.0);

        cart.updateItemPrice("Bag", 120.0);

        assertEquals(108.0, cart.getTotalWithDiscount());
    }

    @Test
    void removesDiscountAfterDroppingToBoundary() {
        ShoppingCart cart = cartWithItem("Bag", 120.0);

        cart.updateItemPrice("Bag", 100.0);

        assertEquals(100.0, cart.getTotalWithDiscount());
    }

    @Test
    void missingItemIsANoOp() {
        ShoppingCart cart = cartWithItem("Notebook", 30.0);

        cart.updateItemPrice("Eraser", 5.0);

        assertCartState(cart, 1, 30.0);
    }

    @Test
    void rejectsNullNameWithoutChangingCart() {
        ShoppingCart cart = cartWithItem("Book", 50.0);

        assertThrows(IllegalArgumentException.class,
                () -> cart.updateItemPrice(null, 80.0));

        assertCartState(cart, 1, 50.0);
    }

    @Test
    void rejectsEmptyNameWithoutChangingCart() {
        ShoppingCart cart = cartWithItem("Book", 50.0);

        assertThrows(IllegalArgumentException.class,
                () -> cart.updateItemPrice("", 80.0));

        assertCartState(cart, 1, 50.0);
    }

    @Test
    void rejectsWhitespaceOnlyNameWithoutChangingCart() {
        ShoppingCart cart = cartWithItem("Book", 50.0);

        assertThrows(IllegalArgumentException.class,
                () -> cart.updateItemPrice("   ", 80.0));

        assertCartState(cart, 1, 50.0);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.01})
    void rejectsNonPositivePriceWithoutChangingCart(double invalidPrice) {
        ShoppingCart cart = cartWithItem("Book", 50.0);

        assertThrows(IllegalArgumentException.class,
                () -> cart.updateItemPrice("Book", invalidPrice));

        assertCartState(cart, 1, 50.0);
    }

    @ParameterizedTest
    @ValueSource(doubles = {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
    })
    void rejectsNonFinitePriceWithoutChangingCart(double invalidPrice) {
        ShoppingCart cart = cartWithItem("Book", 50.0);

        assertThrows(IllegalArgumentException.class,
                () -> cart.updateItemPrice("Book", invalidPrice));

        assertCartState(cart, 1, 50.0);
    }

    @Test
    void validatesPriceBeforeMissingItemLookup() {
        ShoppingCart cart = cartWithItem("Book", 50.0);

        assertThrows(IllegalArgumentException.class,
                () -> cart.updateItemPrice("Missing", -1.0));

        assertCartState(cart, 1, 50.0);
    }

    @Test
    void updatesTheSingleEntryUsedForDuplicateNames() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Book", 10.0);
        cart.addItem("Book", 15.0);

        cart.updateItemPrice("Book", 20.0);

        assertCartState(cart, 1, 20.0);
    }

    @Test
    void itemLookupIsCaseSensitive() {
        ShoppingCart cart = cartWithItem("Book", 50.0);

        cart.updateItemPrice("book", 80.0);

        assertCartState(cart, 1, 50.0);
    }

    @Test
    void acceptsDecimalPrice() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem("Apple", 0.1);
        cart.addItem("Banana", 1.0);

        cart.updateItemPrice("Banana", 0.2);

        assertEquals(0.3, cart.getTotal());
    }

    @Test
    void acceptsLargeFinitePrice() {
        ShoppingCart cart = cartWithItem("Server", 1.0);

        cart.updateItemPrice("Server", Double.MAX_VALUE);

        assertCartState(cart, 1, Double.MAX_VALUE);
    }

    private ShoppingCart cartWithItem(String name, double price) {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(name, price);
        return cart;
    }

    private void assertCartState(ShoppingCart cart, int expectedCount, double expectedTotal) {
        assertEquals(expectedCount, cart.getItemCount());
        assertEquals(expectedTotal, cart.getTotal());
    }
}
