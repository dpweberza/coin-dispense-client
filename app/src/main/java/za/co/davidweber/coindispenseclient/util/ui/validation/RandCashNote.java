package za.co.davidweber.coindispenseclient.util.ui.validation;

import com.mobsandgeeks.saripaar.annotation.ValidateUsing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by David on 2015-03-17.
 */
@ValidateUsing(RandCashNoteRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RandCashNote {
    public int messageResId() default -1;

    public String message() default "Please specify a valid rand cash note amount.";

    public int sequence() default -1;
}