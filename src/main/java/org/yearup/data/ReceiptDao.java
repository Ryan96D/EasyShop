package org.yearup.data;

import org.yearup.models.Receipt;
import org.yearup.models.CheckoutItems;

import java.util.List;

public interface ReceiptDao
{
    Receipt createReceipt(int userId, List<CheckoutItems> items);
    
    List<Receipt> getReceiptsByUserId(int userId);
}
