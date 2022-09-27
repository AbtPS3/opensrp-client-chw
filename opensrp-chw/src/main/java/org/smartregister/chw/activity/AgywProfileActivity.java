package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.agyw.activity.BaseAGYWProfileActivity;
import org.smartregister.chw.agyw.util.Constants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AgywProfileActivity extends BaseAGYWProfileActivity {

    public static void startProfile(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, AgywProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: update options menu with required details
        return false;
    }

    @Override
    protected void startAGYWServices() {
        AGYWServicesActivity.startMe(this, memberObject.getBaseEntityId());
    }

    @Override
    public void startReferralForm() {
        if(BuildConfig.USE_UNIFIED_REFERRAL_APPROACH){
            List<ReferralTypeModel> referralTypeModels = new ArrayList<>();
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.agyw_referral),
                    org.smartregister.chw.util.Constants.JSON_FORM.getAgywReferralForm(), CoreConstants.TASKS_FOCUS.AGYW_REFERRAL));
            referralTypeModels.addAll(Utils.getCommonReferralTypes(this, memberObject.getBaseEntityId()));

            Utils.launchClientReferralActivity(this, referralTypeModels, memberObject.getBaseEntityId());
        }
    }
}
