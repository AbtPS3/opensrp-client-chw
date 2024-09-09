package org.smartregister.chw.presenter;

import static org.smartregister.chw.util.Utils.getClientName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.activity.AllClientsMemberProfileActivity;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.activity.CoreAllClientsMemberProfileActivity;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.presenter.CoreAllClientsMemberPresenter;
import org.smartregister.chw.dao.FamilyDao;
import org.smartregister.chw.ge.contract.GeRegisterFragmentContract;
import org.smartregister.chw.ge.presenter.BaseGeRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.text.MessageFormat;

public class GeRegisterFragmentPresenter extends BaseGeRegisterFragmentPresenter {

    public GeRegisterFragmentPresenter(GeRegisterFragmentContract.View view, GeRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }


    @Override
    public String getMainCondition() {
        return getMainTable() + "." + "is_closed is 0 AND consent_given = 'yes' ";
    }
}
