package za.co.davidweber.coindispenseclient.webservice.coindispenseservice.beans;

import java.util.List;

/**
 * Created by David on 2015-03-15.
 */
public class PaymentResponse {

    private List<RandCashDenomination> randCashDenominations;
    private double total;

    public PaymentResponse() {
    }

    public List<RandCashDenomination> getRandCashDenominations() {
        return randCashDenominations;
    }

    public double getTotal() {
        return total;
    }
}