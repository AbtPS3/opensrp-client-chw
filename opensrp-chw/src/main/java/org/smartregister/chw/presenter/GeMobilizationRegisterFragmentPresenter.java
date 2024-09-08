package org.smartregister.chw.presenter;

import org.smartregister.chw.ge.contract.GeRegisterFragmentContract;
import org.smartregister.chw.ge.presenter.BaseGeRegisterFragmentPresenter;
import org.smartregister.chw.ge.util.Constants;

public class GeMobilizationRegisterFragmentPresenter extends BaseGeRegisterFragmentPresenter {
    public GeMobilizationRegisterFragmentPresenter(GeRegisterFragmentContract.View view, GeRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainTable() {
        return Constants.TABLES.GE_MOBILIZATION_SESSIONS;
    }

}
