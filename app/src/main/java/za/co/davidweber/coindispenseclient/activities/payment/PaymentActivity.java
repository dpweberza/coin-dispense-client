package za.co.davidweber.coindispenseclient.activities.payment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import za.co.davidweber.coindispenseclient.R;
import za.co.davidweber.coindispenseclient.util.ui.ProgressUIAnimator;
import za.co.davidweber.coindispenseclient.util.ui.validation.RandCashNote;
import za.co.davidweber.coindispenseclient.webservice.coindispenseservice.CoinDispenseServiceRestClient;
import za.co.davidweber.coindispenseclient.webservice.coindispenseservice.beans.AuthenticateResponse;
import za.co.davidweber.coindispenseclient.webservice.coindispenseservice.beans.PaymentResponse;

/**
 * A payment screen that captures a payment amount to pay for an account.
 */
@ContentView(R.layout.activity_payment)
public class PaymentActivity extends RoboActivity implements Validator.ValidationListener {

    private PaymentTask mPaymentTask;
    private ProgressUIAnimator mProgressUIAnimator;
    private Validator mFormValidator;
    private AuthenticateResponse mAuthenticateResponse;

    // UI references.
    @InjectView(R.id.payment_amount)
    @NotEmpty
    @RandCashNote
    private EditText mPaymentAmount;
    @InjectView(R.id.payment_progress)
    private View mProgressView;
    @InjectView(R.id.payment_form)
    private View mPaymentFormView;
    @InjectView(R.id.amount_due)
    private TextView mAmountDueTextView;
    @InjectView(R.id.payment_button)
    private Button mPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the authentication result from the previous activity
        Intent intent = getIntent();
        mAuthenticateResponse = new Gson().fromJson(intent.getStringExtra("authenticateResponse"), AuthenticateResponse.class);

        // Update the label with the amount due for the account
        mAmountDueTextView.setText(String.format(getResources().getString(R.string.label_amount_due), mAuthenticateResponse.getUser().getAccountBalance()));

        // Register the click listener for the submit payment button
        mPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPayment();
            }
        });

        // Init the progress UI and form validator
        mProgressUIAnimator = new ProgressUIAnimator(mPaymentFormView, mProgressView, getResources().getInteger(android.R.integer.config_shortAnimTime));
        Validator.registerAnnotation(RandCashNote.class);
        mFormValidator = new Validator(this);
        mFormValidator.setValidationListener(this);
    }

    /**
     * Attempts to make a payment for the user with an amount specified by the payment form.
     * If there are form errors, the errors are presented and no actual payment is made.
     */
    private void submitPayment() {
        if (mPaymentTask != null) {
            return;
        }

        mFormValidator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        double paymentAmount = Double.parseDouble(mPaymentAmount.getText().toString());

        // Show a progress spinner, and kick off a background task to perform the payment.
        mProgressUIAnimator.showProgress(true);
        mPaymentTask = new PaymentTask(paymentAmount);
        mPaymentTask.execute((Void) null);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Represents an asynchronous payment task to pay a user's account.
     */
    public class PaymentTask extends AsyncTask<Void, Void, Boolean> {

        private static final String TAG = "PaymentTask";

        private final double mPaymentAmount;
        private PaymentResponse mPaymentResponse;

        PaymentTask(double mPaymentAmount) {
            this.mPaymentAmount = mPaymentAmount;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = false;

            try {
                // Make the auth request
                mPaymentResponse = new CoinDispenseServiceRestClient().payment(mAuthenticateResponse.getToken(), mPaymentAmount);

                success = true;
            } catch (Exception ex) {
                Log.e(TAG, "Exception while attempting to make payment!", ex);
            }

            return success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mPaymentTask = null;
            mProgressUIAnimator.showProgress(false);

            if (success) {
                // Go to the payment result page and pass through the payment response
                Intent paymentResultIntent = new Intent(getApplicationContext(), PaymentResultActivity.class);
                paymentResultIntent.putExtra("paymentResponse", new Gson().toJson(mPaymentResponse));
                startActivity(paymentResultIntent);
                finish();
            } else {
                PaymentActivity.this.mPaymentAmount.setError(getString(R.string.error_invalid_amount));
                PaymentActivity.this.mPaymentAmount.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mPaymentTask = null;
            mProgressUIAnimator.showProgress(false);
        }
    }

}