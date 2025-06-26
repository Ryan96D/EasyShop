package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

public interface ShoppingCartDao
{
    // GET /cart
    ShoppingCart getByUserId(int userId);

    // POST /cart/products/{id}
    void addProductToCart(int userId, int productId);

    // PUT /cart/products/{id}
    void updateQuantity(int userId, int productId, int quantity);

    // DELETE /cart
    void clearCart(int userId);
}