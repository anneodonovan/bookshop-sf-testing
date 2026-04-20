package bookshop.service;

/**
 * Calculates postage costs based on book weight.
 *
 * Simple tiered pricing (An Post-inspired rates, adjust as needed):
 *   0–100g     : €1.25
 *   101–250g   : €2.00
 *   251–500g   : €3.50
 *   501–1000g  : €5.00
 *   1001–2000g : €8.00
 *   > 2000g    : €12.00
 */
public class PostageService {

    /**
     * Returns the postage cost in euros for a given weight in grams.
     * @throws IllegalArgumentException if weight is zero, negative, or missing.
     */
    public double calculatePostage(double weightGrams) {
        if (weightGrams <= 0) {
            throw new IllegalArgumentException(
                    "Weight must be greater than zero. Got: " + weightGrams);
        }
        if (weightGrams <= 100)  return 1.25;
        if (weightGrams <= 250)  return 2.00;
        if (weightGrams <= 500)  return 3.50;
        if (weightGrams <= 1000) return 5.00;
        if (weightGrams <= 2000) return 8.00;
        return 12.00;
    }

    /**
     * Calculates combined postage for multiple books (total weight).
     */
    public double calculatePostageForMultiple(double[] weightsGrams) {
        double total = 0;
        for (double w : weightsGrams) total += w;
        return calculatePostage(total);
    }
}