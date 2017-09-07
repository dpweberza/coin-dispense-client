package za.co.davidweber.coindispenseclient.util.currency.rand;

/**
 * Created by David on 2015-03-17.
 */
public enum RandCashNoteDenomination {

    ONE_HUNDRED_RANDS(100), FIFTY_RANDS(50), TWENTY_RANDS(20), TEN_RANDS(10);

    private final double amount;

    private RandCashNoteDenomination(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

}