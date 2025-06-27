package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao)
    {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    // GET /cart
    @GetMapping("")
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            return shoppingCartDao.getByUserId(user.getId());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve cart.", e);
        }
    }

    // POST /cart/products/{id}
    @PostMapping("/products/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToCart(@PathVariable int id, Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            shoppingCartDao.addProductToCart(user.getId(), id);
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to add product to cart.", e);
        }
    }

    // PUT /cart/products/{id}
    @PutMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateQuantity(@PathVariable int id,
                               @RequestBody Map<String, Integer> body,
                               Principal principal)
    {
        try
        {
            int quantity = body.getOrDefault("quantity", 1);
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            shoppingCartDao.updateQuantity(user.getId(), id, quantity);
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to update cart item.", e);
        }
    }

    // DELETE /cart
    @DeleteMapping
    public ShoppingCart clearCart(Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);

            shoppingCartDao.clearCart(user.getId());

            // Return empty cart for the test to parse
            return new ShoppingCart();
        }
        catch (Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to clear cart.", e);
        }
    }
}