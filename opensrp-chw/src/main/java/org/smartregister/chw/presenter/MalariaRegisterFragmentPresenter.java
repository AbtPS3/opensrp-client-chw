package org.smartregister.chw.presenter;

import org.smartregister.chw.R;
import org.smartregister.chw.malaria.contract.MalariaRegisterFragmentContract;
import org.smartregister.chw.malaria.presenter.BaseMalariaRegisterFragmentPresenter;
import org.smartregister.chw.malaria.util.DBConstants;
import org.smartregister.chw.util.Constants;

public class MalariaRegisterFragmentPresenter extends BaseMalariaRegisterFragmentPresenter {

    public MalariaRegisterFragmentPresenter(MalariaRegisterFragmentContract.View view, MalariaRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public String getMainTable() {
        return Constants.TABLE_NAME.MALARIA_CONFIRMATION;
    }

    @Override
    public String getMainCondition() {
        return " " + DBConstants.TABLE_NAME + "." + DBConstants.KEY.DATE_REMOVED + " is null " + " COLLATE NOCASE WHERE ec_malaria_confirmation.malaria = 1";
    }
}
