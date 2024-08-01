package org.smartregister.chw.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.fragment.GeRegisterFragment;
import org.smartregister.chw.ge.activity.BaseGeRegisterActivity;
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
    @Override
    protected void initializePresenter() {
        presenter = new GeRegisterActivityPresenter();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new GeRegisterFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtain an instance of the Navigation menu within our activity
        NavigationMenu.getInstance(this, null, null);


        String baseEntityId = getIntent().getStringExtra("BASE_ENTITY_ID_KEY");

        if (baseEntityId != null) {
            startFormActivity("ge_enrollment", baseEntityId, null);
        }


    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            JSONObject jsonObject = (new FormUtils()).getFormJsonFromRepositoryOrAssets(GeRegisterActivity.this, formName);
            String locationId = Context.getInstance().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            org.smartregister.chw.anc.util.JsonFormUtils.getRegistrationForm(jsonObject, entityId, locationId);
            startFormActivity(jsonObject);
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {
        Intent intent = new Intent(this, JsonFormActivity.class);
        intent.putExtra("json", jsonObject.toString());
        startActivityForResult(intent, 700);
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultsCode, Intent intent) {
        if (requestCode == 700 && resultsCode == RESULT_OK) {
            String filledForm = intent.getStringExtra("json");

            AllSharedPreferences allSharedPreferences = org.smartregister.chw.util.Utils.getAllSharedPreferences();

            //1. Convert the received filled form into an EVENT
            Event event = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, filledForm, "ec_gender_equality");

            Gson gson = org.smartregister.chw.anc.util.JsonFormUtils.gson;
            String jsonString = gson.toJson(event);

            try {
                JSONObject form = new JSONObject(jsonString);

                //2. Save and Process the created EVENT
                NCUtils.processEvent(event.getBaseEntityId(), form);
            }catch (Exception e){
                Timber.e(e);
            }

        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Collections.emptyList();
    }

    @Override
    public void startRegistration() {

    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
    }
}
