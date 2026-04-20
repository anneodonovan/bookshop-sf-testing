package bookshop;

import bookshop.model.Sale;
import bookshop.model.SaleItem;
import bookshop.service.InventoryService;
import bookshop.service.SalesService;
import bookshop.model.Book;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for TC: Correct Sales Statistics and Validate Card Payment Processing (sales recording side).
 */
public class SalesServiceTest {

    @TempDir
    Path tempDir;

    private SalesService salesService;
    private InventoryService inventoryService;

    @BeforeEach
    public void setUp() throws IOException {
        String booksCsv = tempDir.resolve("books.csv").toString();
        String salesCsv = tempDir.resolve("sales.csv").toString();
        inventoryService = new InventoryService(booksCsv);
        salesService = new SalesService(salesCsv, inventoryService);

        // Add some test books to inventory
        inventoryService.addBook(new Book("The Hobbit", "Tolkien",
                "9780261102217", "HOB01", 9.99, 300.0, 20));
        inventoryService.addBook(new Book("1984", "Orwell",
                "9780451524935", "ORW01", 7.50, 200.0, 15));
    }

    @Test
    public void testRecordSale_SavesSaleCorrectly() throws IOException {
        List<SaleItem> items = List.of(new SaleItem("HOB01", "The Hobbit", 1, 9.99));
        Sale sale = salesService.recordSale(items, Sale.PaymentMethod.CARD);

        assertNotNull(sale.getSaleId());
        assertFalse(sale.isReturn());
        assertEquals(9.99, sale.getTotal(), 0.001);
    }

    @Test
    public void testRecordSale_DecreasesStock() throws IOException {
        List<SaleItem> items = List.of(new SaleItem("HOB01", "The Hobbit", 2, 9.99));
        salesService.recordSale(items, Sale.PaymentMethod.CASH);

        int remaining = inventoryService.findByBarcode("HOB01").get().getStockCount();
        assertEquals(18, remaining);
    }

    @Test
    public void testRecordReturn_IncreasesStock() throws IOException {
        List<SaleItem> items = List.of(new SaleItem("ORW01", "1984", 1, 7.50));
        salesService.recordReturn(items, Sale.PaymentMethod.CARD);

        int stock = inventoryService.findByBarcode("ORW01").get().getStockCount();
        assertEquals(16, stock);
    }

    @Test
    public void testGetDailyTotal_SumsCorrectly() throws IOException {
        salesService.recordSale(List.of(new SaleItem("HOB01", "The Hobbit", 1, 9.99)),
                Sale.PaymentMethod.CARD);
        salesService.recordSale(List.of(new SaleItem("ORW01", "1984", 2, 7.50)),
                Sale.PaymentMethod.CASH);

        double total = salesService.getDailyTotal(LocalDate.now());
        assertEquals(9.99 + 15.00, total, 0.01);
    }

    @Test
    public void testGetAllReturns_OnlyReturnsReturnRecords() throws IOException {
        salesService.recordSale(List.of(new SaleItem("HOB01", "The Hobbit", 1, 9.99)),
                Sale.PaymentMethod.CARD);
        salesService.recordReturn(List.of(new SaleItem("ORW01", "1984", 1, 7.50)),
                Sale.PaymentMethod.CARD);

        List<Sale> returns = salesService.getAllReturns();
        assertEquals(1, returns.size());
        assertTrue(returns.get(0).isReturn());
    }

    @Test
    public void testSaleTotal_MultipleItems_CalculatesCorrectly() throws IOException {
        List<SaleItem> items = List.of(
                new SaleItem("HOB01", "The Hobbit", 2, 9.99),
                new SaleItem("ORW01", "1984", 1, 7.50)
        );
        Sale sale = salesService.recordSale(items, Sale.PaymentMethod.CARD);
        assertEquals(27.48, sale.getTotal(), 0.001);
    }
}