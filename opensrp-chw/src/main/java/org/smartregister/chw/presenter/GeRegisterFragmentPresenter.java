package org.smartregister.chw.presenter;

import org.smartregister.chw.ge.contract.GeRegisterFragmentContract;
import org.smartregister.chw.ge.presenter.BaseGeRegisterFragmentPresenter;

public class GeRegisterFragmentPresenter extends BaseGeRegisterFragmentPresenter {

    public GeRegisterFragmentPresenter(GeRegisterFragmentContract.View view, GeRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }


    @Override
    public String getMainCondition() {
        return getMainTable() + "." + "is_closed is 0 AND consent_given = 'yes' ";
    }
}
