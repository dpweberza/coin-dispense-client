package za.co.davidweber.coindispenseclient.activities.payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import za.co.davidweber.coindispenseclient.R;
import za.co.davidweber.coindispenseclient.activities.login.LoginActivity;
import za.co.davidweber.coindispenseclient.webservice.coindispenseservice.beans.PaymentResponse;
import za.co.davidweber.coindispenseclient.webservice.coindispenseservice.beans.RandCashDenomination;

/**
 * A payment result screen that lists any cash denominations to dispense as change.
 */
@ContentView(R.layout.activity_payment_result)
public class PaymentResultActivity extends RoboActivity {

    private PaymentResponse mPaymentResponse;

    // UI references
    @InjectView(R.id.cash_denominations)
    private TextView mCashDenominationsTextView;
    @InjectView(R.id.total_change)
    private TextView mTotalChangeTextView;
    @InjectView(R.id.reset_button)
    private Button mPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the payment result from the previous activity
        Intent intent = getIntent();
        mPaymentResponse = new Gson().fromJson(intent.getStringExtra("paymentResponse"), PaymentResponse.class);

        // Update the text listing of cash denominations to dispense
        StringBuilder cashDenominationListing = new StringBuilder();
        List<RandCashDenomination> randCashDenominations = mPaymentResponse.getRandCashDenominations();
        if (randCashDenominations != null) {
            for (RandCashDenomination randCashDenomination : randCashDenominations) {
                cashDenominationListing.append(randCashDenomination.getQuantity()).append(" x ").append(randCashDenomination.getName()).append("\n");
            }
        }
        mCashDenominationsTextView.setText(cashDenominationListing.toString());
        mTotalChangeTextView.setText(String.format(getResources().getString(R.string.label_total), mPaymentResponse.getTotal()));

        // Register the click listener for the submit payment button
        mPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
    }

    private void reset() {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginIntent);
    }

}