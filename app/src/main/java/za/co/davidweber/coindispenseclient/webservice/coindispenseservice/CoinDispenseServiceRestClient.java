package za.co.davidweber.coindispenseclient.webservice.coindispenseservice;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import za.co.davidweber.coindispenseclient.webservice.coindispenseservice.beans.AuthenticateResponse;
import za.co.davidweber.coindispenseclient.webservice.coindispenseservice.beans.PaymentResponse;

/**
 * Coin Dispense Service REST API client.
 * <p/>
 * Created by David on 2015-03-15.
 */
public class CoinDispenseServiceRestClient {

    private static final MediaType JSON_CONTENT_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "http://192.168.1.104:4567/api/v1/";

    private OkHttpClient client;

    /**
     * Constructor
     */
    public CoinDispenseServiceRestClient() {
        client = new OkHttpClient();
        client.setConnectTimeout(15, TimeUnit.SECONDS);
        client.setReadTimeout(15, TimeUnit.SECONDS);
    }

    /**
     * Authenticates the user against the API and returns an auth token along with the user account information.
     *
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public AuthenticateResponse authenticate(String username, String password) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", password);
        String response = post("/public/authenticate", jsonObject.toString(), "");
        return new Gson().fromJson(response, AuthenticateResponse.class);
    }

    /**
     * Makes a payment against the user's account and returns any change to be dispensed.
     *
     * @param amount
     * @return
     * @throws Exception
     */
    public PaymentResponse payment(String authToken, double amount) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount", amount);
        String response = post("/authenticated/payment", jsonObject.toString(), authToken);
        return new Gson().fromJson(response, PaymentResponse.class);
    }

    private String post(String relativeUrl, String json, String authToken) throws Exception {
        RequestBody body = RequestBody.create(JSON_CONTENT_TYPE, json);
        Request request = new Request.Builder()
                .url(getAbsoluteUrl(relativeUrl))
                .post(body)
                .header("X-Auth", authToken)
                .build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful())
            throw new Exception("Failed to authenticate!");

        return response.body().string();
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}