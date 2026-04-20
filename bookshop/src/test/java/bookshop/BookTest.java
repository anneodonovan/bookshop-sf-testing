package bookshop;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import bookshop.model.Book;
import bookshop.service.InventoryService;

public class BookTest {
    //test case 1: book creation
    @Test
    public void testBookAddToStock() throws IOException {
        InventoryService inventory = new InventoryService("books.csv");
        Book testBook = new Book("Java How to Program, Tenth Edition - Early Objects", "Harvey Deitel", "978-0-13-380780-6", "1234567890", 37.5f, 135f, 1);

        int before = inventory.getStockCount(); //taking stock count before change
        inventory.addBook(testBook);
        int after = inventory.getStockCount(); //taking stock count after change

        assertEquals(before + testBook.getStockCount(), after, "Stock count should increase by 1");
    }
}
