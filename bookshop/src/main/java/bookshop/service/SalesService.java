package bookshop.service;

import bookshop.model.Sale;
import bookshop.model.SaleItem;
import bookshop.util.CsvUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Records sales and returns, and provides sales statistics.
 * Reads/writes to sales.csv.
 *
 * sales.csv header: saleId,dateTime,paymentMethod,isReturn,items
 */
public class SalesService {

    static final String HEADER = "saleId,dateTime,paymentMethod,isReturn,items";
    private final String csvPath;
    private final InventoryService inventoryService;

    public SalesService(String csvPath, InventoryService inventoryService) {
        this.csvPath = csvPath;
        this.inventoryService = inventoryService;
    }

    /**
     * Records a new sale and decreases stock for each item.
     * @return the saved Sale object with its generated ID
     */
    public Sale recordSale(List<SaleItem> items, Sale.PaymentMethod paymentMethod) throws IOException {
        // Decrease stock for each item sold
        for (SaleItem item : items) {
            inventoryService.decreaseStock(item.getBarcode(), item.getQuantity());
        }

        Sale sale = new Sale(
                generateId(),
                LocalDateTime.now(),
                items,
                paymentMethod,
                false
        );
        CsvUtil.appendLine(csvPath, HEADER, sale.toCsvRow());
        return sale;
    }

    /**
     * Records a return and increases stock for each returned item.
     * @return the saved return Sale object
     */
    public Sale recordReturn(List<SaleItem> items, Sale.PaymentMethod originalPaymentMethod) throws IOException {
        // Increase stock for each returned item
        for (SaleItem item : items) {
            inventoryService.increaseStock(item.getBarcode(), item.getQuantity());
        }

        Sale returnSale = new Sale(
                generateId(),
                LocalDateTime.now(),
                items,
                originalPaymentMethod,
                true
        );
        CsvUtil.appendLine(csvPath, HEADER, returnSale.toCsvRow());
        return returnSale;
    }

    /** Returns all sales (not returns) recorded in the system. */
    public List<Sale> getAllSales() throws IOException {
        List<Sale> sales = new ArrayList<>();
        for (String line : CsvUtil.readLines(csvPath)) {
            Sale s = Sale.fromCsvRow(line);
            if (!s.isReturn()) sales.add(s);
        }
        return sales;
    }

    /** Returns all return records. */
    public List<Sale> getAllReturns() throws IOException {
        List<Sale> returns = new ArrayList<>();
        for (String line : CsvUtil.readLines(csvPath)) {
            Sale s = Sale.fromCsvRow(line);
            if (s.isReturn()) returns.add(s);
        }
        return returns;
    }

    /** Returns total sales revenue for a specific date. */
    public double getDailyTotal(LocalDate date) throws IOException {
        return getAllSales().stream()
                .filter(s -> s.getDateTime().toLocalDate().equals(date))
                .mapToDouble(Sale::getTotal)
                .sum();
    }

    /** Returns total sales revenue for the current week (Mon–Sun). */
    public double getWeeklyTotal() throws IOException {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate sunday = monday.plusDays(6);
        return getAllSales().stream()
                .filter(s -> {
                    LocalDate d = s.getDateTime().toLocalDate();
                    return !d.isBefore(monday) && !d.isAfter(sunday);
                })
                .mapToDouble(Sale::getTotal)
                .sum();
    }

    /** Returns total sales revenue for a given month and year. */
    public double getMonthlyTotal(int year, int month) throws IOException {
        return getAllSales().stream()
                .filter(s -> s.getDateTime().getYear() == year
                          && s.getDateTime().getMonthValue() == month)
                .mapToDouble(Sale::getTotal)
                .sum();
    }

    private String generateId() {
        return "SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}