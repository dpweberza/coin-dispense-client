package za.co.davidweber.coindispenseclient.webservice.coindispenseservice.beans;

/**
 * Created by David on 2015-03-15.
 */
public class AuthenticateResponse {

    private String token;
    private User user;

    public AuthenticateResponse() {
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}
