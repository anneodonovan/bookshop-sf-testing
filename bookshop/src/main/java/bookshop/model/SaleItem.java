package bookshop.model;

/**
 * Represents a single line item within a sale (one book, a quantity, and its price at time of sale).
 */
public class SaleItem {

    private String barcode;
    private String bookTitle;
    private int quantity;
    private double unitPrice;

    public SaleItem(String barcode, String bookTitle, int quantity, double unitPrice) {
        this.barcode = barcode;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getBarcode()      { return barcode; }
    public String getBookTitle()    { return bookTitle; }
    public int getQuantity()        { return quantity; }
    public double getUnitPrice()    { return unitPrice; }
    public double getLineTotal()    { return unitPrice * quantity; }

    /** Format: barcode|bookTitle|quantity|unitPrice */
    public String serialise() {
        return barcode + "|" + bookTitle + "|" + quantity + "|" + unitPrice;
    }

    public static SaleItem deserialise(String s) {
        String[] p = s.split("\\|", -1);
        if (p.length < 4) throw new IllegalArgumentException("Invalid SaleItem: " + s);
        return new SaleItem(p[0], p[1], Integer.parseInt(p[2]), Double.parseDouble(p[3]));
    }
}