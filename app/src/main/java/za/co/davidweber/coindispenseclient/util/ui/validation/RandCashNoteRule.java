package za.co.davidweber.coindispenseclient.util.ui.validation;

import com.mobsandgeeks.saripaar.AnnotationRule;

import za.co.davidweber.coindispenseclient.util.currency.rand.RandCashNoteDenomination;

/**
 * Validation rule to ensure a valid Rand note amount has been specified.
 * <p/>
 * Created by David on 2015-03-17.
 */
public class RandCashNoteRule extends AnnotationRule<RandCashNote, Double> {

    protected RandCashNoteRule(RandCashNote randCashNote) {
        super(randCashNote);
    }

    @Override
    public boolean isValid(Double value) {

        boolean isValid = false;
        for (RandCashNoteDenomination randCashNoteDenomination : RandCashNoteDenomination.values()) {
            if (value == randCashNoteDenomination.getAmount()) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }
}