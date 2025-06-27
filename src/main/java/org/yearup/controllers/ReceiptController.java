package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ReceiptDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.CheckoutItems;
import org.yearup.models.Receipt;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ReceiptController
{
    private final ReceiptDao receiptDao;
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    public ReceiptController(ReceiptDao receiptDao, ShoppingCartDao shoppingCartDao, UserDao userDao)
    {
        this.receiptDao = receiptDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Receipt checkout(Principal principal)
    {
        try
        {
            // Step 1: Get current user ID
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            int userId = user.getId();

            // Step 2: Get cart
            ShoppingCart cart = shoppingCartDao.getByUserId(userId);

            // Step 3: Convert cart items to CheckoutItems
            List<CheckoutItems> checkoutItems = new ArrayList<>();
            for (ShoppingCartItem item : cart.getItems().values())
            {
                checkoutItems.add(new CheckoutItems(item.getProduct(), item.getQuantity()));
            }

            // Step 4: Create and store receipt
            Receipt receipt = receiptDao.createReceipt(userId, checkoutItems);

            // Step 5: Clear cart
            shoppingCartDao.clearCart(userId);

            return receipt;
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Checkout failed.", e);
        }
    }

    @GetMapping
    public List<Receipt> getPastReceipts(Principal principal)
    {
        try
        {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            return receiptDao.getReceiptsByUserId(user.getId());
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve receipts.", e);
        }
    }
}


