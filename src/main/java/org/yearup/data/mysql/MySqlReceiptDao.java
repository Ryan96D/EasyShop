package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ReceiptDao;
import org.yearup.models.CheckoutItems;
import org.yearup.models.Product;
import org.yearup.models.Receipt;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlReceiptDao extends MySqlDaoBase implements ReceiptDao
{
    public MySqlReceiptDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Receipt createReceipt(int userId, List<CheckoutItems> items)
    {
        BigDecimal total = BigDecimal.ZERO;

        for (CheckoutItems item : items) {
            if (item.getProduct() == null) {
                throw new RuntimeException("Product is null in checkout item.");
            }

            BigDecimal price = item.getProduct().getPrice();
            if (price == null) {
                throw new RuntimeException("Product price is null for productId = " + item.getProduct().getProductId());
            }
            int quantity = item.getQuantity();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
            total = total.add(subtotal);
        }

        String insertReceiptSql = "INSERT INTO receipts (user_id, total, date) VALUES (?, ?, NOW())";
        String insertItemSql = "INSERT INTO checkout_items (receipt_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement receiptStmt = conn.prepareStatement(insertReceiptSql, Statement.RETURN_GENERATED_KEYS)) {
                receiptStmt.setInt(1, userId);
                receiptStmt.setBigDecimal(2, total);
                receiptStmt.executeUpdate();

                ResultSet keys = receiptStmt.getGeneratedKeys();
                if (keys.next()) {
                    int receiptId = keys.getInt(1);

                    try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSql)) {
                        for (CheckoutItems item : items) {
                            itemStmt.setInt(1, receiptId);
                            itemStmt.setInt(2, item.getProduct().getProductId());
                            itemStmt.setInt(3, item.getQuantity());
                            itemStmt.setBigDecimal(4, item.getProduct().getPrice());
                            itemStmt.addBatch();
                        }
                        itemStmt.executeBatch();
                    }

                    conn.commit();

                    return new Receipt(receiptId, userId, total, LocalDateTime.now(), items);
                } else {
                    conn.rollback();
                    throw new SQLException("Failed to retrieve receipt ID.");
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating receipt.", e);
        }
    }

    @Override
    public List<Receipt> getReceiptsByUserId(int userId) {
        List<Receipt> receipts = new ArrayList<>();
        String receiptSql = "SELECT * FROM receipts WHERE user_id = ? ORDER BY date DESC";
        String itemSql = "SELECT ci.*, p.name, p.description, p.price AS product_price, p.category_id, p.color FROM checkout_items ci JOIN products p ON ci.product_id = p.product_id WHERE ci.receipt_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement receiptStmt = conn.prepareStatement(receiptSql)) {

            receiptStmt.setInt(1, userId);
            ResultSet rs = receiptStmt.executeQuery();

            while (rs.next()) {
                int receiptId = rs.getInt("receipt_id");
                BigDecimal total = rs.getBigDecimal("total");
                Timestamp timestamp = rs.getTimestamp("date");
                LocalDateTime date = timestamp.toLocalDateTime();

                List<CheckoutItems> items = new ArrayList<>();
                try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                    itemStmt.setInt(1, receiptId);
                    ResultSet itemRs = itemStmt.executeQuery();

                    while (itemRs.next()) {
                        Product product = new Product();
                        product.setProductId(itemRs.getInt("product_id"));
                        product.setName(itemRs.getString("name"));
                        product.setDescription(itemRs.getString("description"));
                        product.setPrice(itemRs.getBigDecimal("product_price"));
                        product.setCategoryId(itemRs.getInt("category_id"));
                        product.setColor(itemRs.getString("color"));

                        CheckoutItems item = new CheckoutItems(product, itemRs.getInt("quantity"));
                        items.add(item);
                    }
                }

                receipts.add(new Receipt(receiptId, userId, total, date, items));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving receipts.", e);
        }

        return receipts;
    }
}
