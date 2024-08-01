package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.GeRegisterFragment;
import org.smartregister.chw.fragment.OvcRegisterFragment;
import org.smartregister.chw.ge.activity.BaseGeRegisterActivity;
import org.smartregister.chw.ovc.util.Constants;
import org.smartregister.chw.presenter.GeRegisterActivityPresenter;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class GeRegisterActivity extends BaseGeRegisterActivity {

    public static void startRegistration(Activity activity, String baseEntityId, String formName) {
        Intent intent = new Intent(activity, MvcRegisterActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.OVC_FORM_NAME, formName);

        activity.startActivity(intent);
    }

    @Override
    public Form getFormConfig() {
        Form form = new Form();
        form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
        form.setWizard(true);
        form.setName(getString(R.string.ge_enrollment));
        form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);
        form.setNextLabel(this.getResources().getString(org.smartregister.chw.core.R.string.next));
        form.setPreviousLabel(this.getResources().getString(org.smartregister.chw.core.R.string.back));
        form.setSaveLabel(this.getResources().getString(org.smartregister.chw.core.R.string.save));
        return form;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new GeRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[]{};
    }
}
