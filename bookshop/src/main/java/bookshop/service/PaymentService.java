package bookshop.service;

import bookshop.model.Sale;

/**
 * Simulates payment processing for card and cash transactions.
 *
 * In a real system this would integrate with a card terminal (e.g. SumUp, Square, Stripe Terminal).
 * For the mockup, card payments always succeed unless the amount is invalid.
 */
public class PaymentService {

    public enum PaymentResult { SUCCESS, DECLINED, INVALID_AMOUNT, TERMINAL_ERROR }

    /**
     * Processes a card payment for the given amount.
     * @param amountEuros the amount to charge
     * @return SUCCESS for valid amounts (mockup always approves)
     */
    public PaymentResult processCardPayment(double amountEuros) {
        if (amountEuros <= 0) return PaymentResult.INVALID_AMOUNT;
        // Real integration with SumUp/Stripe/Square Terminal would go here
        return PaymentResult.SUCCESS;
    }

    /**
     * Records a cash payment. Just validates the amount — no terminal needed.
     */
    public PaymentResult processCashPayment(double amountEuros) {
        if (amountEuros <= 0) return PaymentResult.INVALID_AMOUNT;
        return PaymentResult.SUCCESS;
    }

    /**
     * Calculates the change to give back for a cash transaction.
     * @param amountDue  the price the customer owes
     * @param cashGiven  the cash the customer handed over
     * @return change in euros
     * @throws IllegalArgumentException if cash given is less than amount due
     */
    public double calculateChange(double amountDue, double cashGiven) {
        if (cashGiven < amountDue) {
            throw new IllegalArgumentException(
                    "Cash given (" + cashGiven + ") is less than amount due (" + amountDue + ").");
        }
        // Round to 2 decimal places to avoid floating point issues
        return Math.round((cashGiven - amountDue) * 100.0) / 100.0;
    }
}