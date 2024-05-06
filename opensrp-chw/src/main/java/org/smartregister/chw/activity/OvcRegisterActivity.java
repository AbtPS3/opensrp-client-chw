package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.vijay.jsonwizard.domain.Form;

import org.smartregister.chw.R;
import org.smartregister.chw.core.activity.CoreOvcRegisterActivity;
import org.smartregister.chw.fragment.OvcRegisterFragment;
import org.smartregister.chw.ovc.util.Constants;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class OvcRegisterActivity extends CoreOvcRegisterActivity {

    public static void startRegistration(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, OvcRegisterActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.OVC_FORM_NAME, Constants.FORMS.GBV_SCREENING);

        activity.startActivity(intent);
    }

    @Override
    public Form getFormConfig() {
        Form form = new Form();
        form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
        form.setWizard(true);
        form.setName(getString(R.string.ovc_enrollment));
        form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);
        form.setNextLabel(this.getResources().getString(org.smartregister.chw.core.R.string.next));
        form.setPreviousLabel(this.getResources().getString(org.smartregister.chw.core.R.string.back));
        form.setSaveLabel(this.getResources().getString(org.smartregister.chw.core.R.string.save));
        return form;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new OvcRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[]{};
    }
}
