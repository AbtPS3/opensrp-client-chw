package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.viewpager.widget.ViewPager;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.chw.ge.activity.BaseGeProfileActivity;
import org.smartregister.chw.ge.domain.MemberObject;
import org.smartregister.chw.ge.util.Constants;
import org.smartregister.chw.ge.util.GeJsonFormUtils;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GeProfileActivity extends BaseGeProfileActivity {
    private final FamilyOtherMemberProfileActivity.Flavor flavor = new FamilyOtherMemberProfileActivityFlv();

    private final List<ReferralTypeModel> referralTypeModels = new ArrayList<>();

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, GeProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void recordGe(MemberObject memberObject) {
        JSONObject jsonObject;
        try {
            jsonObject = GeJsonFormUtils.getFormAsJson(Constants.FORMS.GE_INDIVIDUAL_SERVICES);
            String locationId = Context.getInstance().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            GeJsonFormUtils.getRegistrationForm(jsonObject, memberObject.getBaseEntityId(), locationId);
            startFormActivity(jsonObject);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void startFormActivity(JSONObject jsonForm) {
        Form form = new Form();
        form.setWizard(false);

        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.chw.ovc.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, org.smartregister.chw.ovc.util.Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {

    }
}
