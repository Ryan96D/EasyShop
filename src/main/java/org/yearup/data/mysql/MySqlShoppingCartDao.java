package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{
    public MySqlShoppingCartDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId)
    {
        ShoppingCart cart = new ShoppingCart();

        String sql = """
            SELECT sc.product_id, sc.quantity,
                   p.name, p.description, p.price, p.category_id, p.color, p.stock, p.image_url, p.featured
            FROM shopping_cart sc
            JOIN products p ON sc.product_id = p.product_id
            WHERE sc.user_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setColor(rs.getString("color"));
                product.setStock(rs.getInt("stock"));
                product.setImageUrl(rs.getString("image_url"));
                product.setFeatured(rs.getBoolean("featured"));

                ShoppingCartItem item = new ShoppingCartItem();
                item.setProduct(product);
                item.setQuantity(rs.getInt("quantity"));

                cart.add(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving shopping cart.", e);
        }

        return cart;
    }

    @Override
    public void addProductToCart(int userId, int productId)
    {
        String sql = """
            INSERT INTO shopping_cart (user_id, product_id, quantity)
            VALUES (?, ?, 1)
            ON DUPLICATE KEY UPDATE quantity = quantity + 1
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, productId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding to cart.", e);
        }
    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity)
    {
        String sql = """
            UPDATE shopping_cart
            SET quantity = ?
            WHERE user_id = ? AND product_id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, userId);
            stmt.setInt(3, productId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating quantity.", e);
        }
    }

    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error clearing shopping cart.", e);
        }
    }
}