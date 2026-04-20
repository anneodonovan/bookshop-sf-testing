package bookshop.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import bookshop.model.Book;
import bookshop.util.CsvUtil;
import bookshop.util.IsbnValidator;

/**
 * Manages the book inventory. Reads/writes to books.csv.
 *
 * books.csv header: title,author,isbn,barcode,price,weightGrams,stockCount
 */
public class InventoryService {

    static final String HEADER = "title,author,isbn,barcode,price,weightGrams,stockCount";
    private final String csvPath;

    public InventoryService(String csvPath) {
        this.csvPath = csvPath;
    }

    /** Returns all books currently in inventory. */
    public List<Book> getAllBooks() throws IOException {
        List<Book> books = new ArrayList<>();
        for (String line : CsvUtil.readLines(csvPath)) {
            books.add(Book.fromCsvRow(line));
        }
        return books;
    }

    /** Get total stock count across all books. */
    public int getStockCount() throws IOException {
        int total = 0;
        for (String line : CsvUtil.readLines(csvPath)) {
            Book b = Book.fromCsvRow(line);
            total += b.getStockCount();
        }
        return total;
    }

    /** Finds a book by its barcode. Returns empty if not found. */
    public Optional<Book> findByBarcode(String barcode) throws IOException {
        return getAllBooks().stream()
                .filter(b -> b.getBarcode().equalsIgnoreCase(barcode))
                .findFirst();
    }

    /** Finds a book by title (case-insensitive, partial match). */
    public List<Book> searchByTitle(String titleQuery) throws IOException {
        List<Book> results = new ArrayList<>();
        for (Book b : getAllBooks()) {
            if (b.getTitle().toLowerCase().contains(titleQuery.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    /**
     * Adds a new book to inventory.
     * @throws IllegalArgumentException if ISBN is invalid or barcode already exists.
     */
    public void addBook(Book book) throws IOException {
        if (!IsbnValidator.isValid(book.getIsbn())) {
            throw new IllegalArgumentException("Invalid ISBN: " + book.getIsbn());
        }
        if (findByBarcode(book.getBarcode()).isPresent()) {
            throw new IllegalArgumentException("Barcode already exists: " + book.getBarcode());
        }
        CsvUtil.appendLine(csvPath, HEADER, book.toCsvRow());
    }

    /**
     * Decreases stock by the given quantity after a sale.
     * @throws IllegalStateException if stock is insufficient.
     */
    public void decreaseStock(String barcode, int quantity) throws IOException {
        List<Book> books = getAllBooks();
        boolean found = false;
        for (Book b : books) {
            if (b.getBarcode().equalsIgnoreCase(barcode)) {
                if (b.getStockCount() < quantity) {
                    throw new IllegalStateException(
                            "Insufficient stock for '" + b.getTitle() + "': " +
                            b.getStockCount() + " available, " + quantity + " requested.");
                }
                b.setStockCount(b.getStockCount() - quantity);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Book not found with barcode: " + barcode);
        saveAll(books);
    }

    /**
     * Increases stock by the given quantity (e.g. after a return or new delivery).
     */
    public void increaseStock(String barcode, int quantity) throws IOException {
        List<Book> books = getAllBooks();
        boolean found = false;
        for (Book b : books) {
            if (b.getBarcode().equalsIgnoreCase(barcode)) {
                b.setStockCount(b.getStockCount() + quantity);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Book not found with barcode: " + barcode);
        saveAll(books);
    }

    /**
     * Returns all books with stock count below the given threshold.
     */
    public List<Book> getLowStockBooks(int threshold) throws IOException {
        List<Book> lowStock = new ArrayList<>();
        for (Book b : getAllBooks()) {
            if (b.getStockCount() <= threshold) lowStock.add(b);
        }
        return lowStock;
    }

    /** Overwrites the CSV with the current list of books. */
    private void saveAll(List<Book> books) throws IOException {
        List<String> rows = new ArrayList<>();
        for (Book b : books) rows.add(b.toCsvRow());
        CsvUtil.writeLines(csvPath, HEADER, rows);
    }
}