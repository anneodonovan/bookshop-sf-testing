package bookshop.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a completed sale transaction.
 */
public class Sale {

    public enum PaymentMethod { CARD, CASH }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String saleId;
    private LocalDateTime dateTime;
    private List<SaleItem> items;
    private PaymentMethod paymentMethod;
    private boolean isReturn; // true if this record is a return/refund

    public Sale(String saleId, LocalDateTime dateTime,
                List<SaleItem> items, PaymentMethod paymentMethod, boolean isReturn) {
        this.saleId = saleId;
        this.dateTime = dateTime;
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.isReturn = isReturn;
    }

    public String getSaleId()               { return saleId; }
    public LocalDateTime getDateTime()      { return dateTime; }
    public List<SaleItem> getItems()        { return items; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public boolean isReturn()               { return isReturn; }

    public double getTotal() {
        return items.stream().mapToDouble(SaleItem::getLineTotal).sum();
    }

    /**
     * CSV format:
     * saleId,dateTime,paymentMethod,isReturn,item1Serialised;item2Serialised;...
     */
    public String toCsvRow() {
        StringBuilder itemsStr = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) itemsStr.append(";");
            itemsStr.append(items.get(i).serialise());
        }
        return String.join(",",
                saleId,
                dateTime.format(FMT),
                paymentMethod.name(),
                String.valueOf(isReturn),
                "\"" + itemsStr + "\"");
    }

    public static Sale fromCsvRow(String csvRow) {
        // Split carefully: last field is quoted and may contain commas
        int lastQuoteOpen  = csvRow.lastIndexOf(",\"");
        String prefix      = csvRow.substring(0, lastQuoteOpen);
        String itemsPart   = csvRow.substring(lastQuoteOpen + 2, csvRow.length() - 1);

        String[] p = prefix.split(",", -1);
        String saleId          = p[0].trim();
        LocalDateTime dt       = LocalDateTime.parse(p[1].trim(), FMT);
        PaymentMethod method   = PaymentMethod.valueOf(p[2].trim());
        boolean isReturn       = Boolean.parseBoolean(p[3].trim());

        List<SaleItem> items = new ArrayList<>();
        for (String itemStr : itemsPart.split(";")) {
            if (!itemStr.isBlank()) items.add(SaleItem.deserialise(itemStr));
        }
        return new Sale(saleId, dt, items, method, isReturn);
    }

    @Override
    public String toString() {
        return "Sale{id='" + saleId + "', total=" + getTotal()
                + ", method=" + paymentMethod + ", return=" + isReturn + "}";
    }
}