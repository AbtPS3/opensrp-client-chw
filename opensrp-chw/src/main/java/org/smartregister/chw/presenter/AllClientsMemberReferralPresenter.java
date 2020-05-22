package org.smartregister.chw.presenter;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.model.AllClientsMemberReferralModel;
import org.smartregister.chw.referral.contract.BaseIssueReferralContract;
import org.smartregister.chw.referral.model.AbstractIssueReferralModel;
import org.smartregister.chw.referral.presenter.BaseIssueReferralPresenter;
import org.smartregister.chw.referral.util.DBConstants;
import org.smartregister.chw.util.Constants;

public class AllClientsMemberReferralPresenter extends BaseIssueReferralPresenter {

    public AllClientsMemberReferralPresenter(String baseEntityId, BaseIssueReferralContract.View view,
                                             Class<? extends AbstractIssueReferralModel> viewModelClass,
                                             BaseIssueReferralContract.Interactor interactor) {
        super(baseEntityId, view, viewModelClass, interactor);
    }

    @NotNull
    @Override
    public Class<? extends AbstractIssueReferralModel> getViewModel() {
        return AllClientsMemberReferralModel.class;
    }

    @NotNull
    @Override
    public String getMainCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.Key.BASE_ENTITY_ID + " = '" + getBaseEntityID() + "'";
    }


    @NotNull
    @Override
    public String getMainTable() {
        return Constants.TABLE_NAME.FAMILY_MEMBER;
    }
}