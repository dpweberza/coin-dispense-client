package za.co.davidweber.coindispenseclient.activities.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import za.co.davidweber.coindispenseclient.R;
import za.co.davidweber.coindispenseclient.activities.payment.PaymentActivity;
import za.co.davidweber.coindispenseclient.util.ui.ProgressUIAnimator;
import za.co.davidweber.coindispenseclient.webservice.coindispenseservice.CoinDispenseServiceRestClient;
import za.co.davidweber.coindispenseclient.webservice.coindispenseservice.beans.AuthenticateResponse;

import static com.mobsandgeeks.saripaar.Validator.ValidationListener;

/**
 * A login screen that offers login via username / password.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends RoboActivity implements ValidationListener {

    private UserLoginTask mAuthTask;
    private ProgressUIAnimator mProgressUIAnimator;
    private Validator mFormValidator;

    // UI references.
    @InjectView(R.id.username)
    @NotEmpty
    private EditText mUsernameEditText;
    @InjectView(R.id.password)
    @Password
    private EditText mPasswordEditText;
    @InjectView(R.id.login_progress)
    private View mProgressView;
    @InjectView(R.id.login_form)
    private View mLoginFormView;
    @InjectView(R.id.sign_in_button)
    private Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the click listener for the login button
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // Init the progress UI and form validator
        mProgressUIAnimator = new ProgressUIAnimator(mLoginFormView, mProgressView, getResources().getInteger(android.R.integer.config_shortAnimTime));
        mFormValidator = new Validator(this);
        mFormValidator.setValidationListener(this);
    }

    /**
     * Attempts to sign in the user specified by the login form.
     * If there are form errors, the errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mFormValidator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // Show a progress spinner, and kick off a background task to perform the user login attempt.
        mProgressUIAnimator.showProgress(true);
        mAuthTask = new UserLoginTask(username, password);
        mAuthTask.execute((Void) null);
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
     * Represents an asynchronous login/registration task used to authenticate the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private static final String TAG = "UserLoginTask";

        private final String mUsername;
        private final String mPassword;
        private AuthenticateResponse authenticateResponse;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = false;

            try {
                // Make the auth request
                authenticateResponse = new CoinDispenseServiceRestClient().authenticate(mUsername, mPassword);

                success = true;
            } catch (Exception ex) {
                Log.e(TAG, "Exception while attempting to authenticate!", ex);
            }

            return success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mProgressUIAnimator.showProgress(false);

            if (success) {
                // Go to the payment page and pass through the authenticate response
                Intent paymentIntent = new Intent(getApplicationContext(), PaymentActivity.class);
                paymentIntent.putExtra("authenticateResponse", new Gson().toJson(authenticateResponse));
                startActivity(paymentIntent);
                finish();
            } else {
                mPasswordEditText.setError(getString(R.string.error_incorrect_credentials));
                mPasswordEditText.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressUIAnimator.showProgress(false);
        }
    }
}

