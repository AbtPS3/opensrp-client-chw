package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONObject;
import org.smartregister.chw.ovc.activity.BaseOvcChildVisitActivity;
import org.smartregister.chw.ovc.util.Constants;
import org.smartregister.family.util.Utils;

public class MvcVisitActivity extends BaseOvcChildVisitActivity {
    public static void startMe(Activity activity, String baseEntityID, Boolean isEditMode) {
        Intent intent = new Intent(activity, MvcVisitActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.EDIT_MODE, isEditMode);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }
    

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);
        intent.putExtra(org.smartregister.chw.ovc.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        if (getFormConfig(jsonForm) != null) {
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, getFormConfig(jsonForm));
        }

        startActivityForResult(intent, org.smartregister.chw.ovc.util.Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == org.smartregister.chw.ovc.util.Constants.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            submitVisit(org.smartregister.chw.ovc.util.Constants.SaveType.AUTO_SUBMIT);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == org.smartregister.chw.ovc.R.id.close) {
            close();
        } else if (v.getId() == org.smartregister.chw.ovc.R.id.customFontTextViewSubmit) {
            submitVisit(org.smartregister.chw.ovc.util.Constants.SaveType.SUBMIT_AND_CLOSE);
        }
    }
    
}
