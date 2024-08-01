package org.smartregister.chw.activity;

import android.content.Intent;

import androidx.viewpager.widget.ViewPager;

import org.smartregister.chw.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

public class GeProfileActivity extends BaseProfileActivity {
    private CommonPersonObjectClient client;

    @Override
    protected void setupViews() {
        super.setupViews();

        Intent intent = getIntent();
        client =
                (CommonPersonObjectClient) intent.getSerializableExtra("client_details");

        if (client != null) {
            CustomFontTextView clientNameCtv = findViewById(R.id.textview_name);

            String firstName = client.getDetails().get("first_name");
            String middleName = client.getDetails().get("middle_name");
            String lastName = client.getDetails().get("last_name");

            String fullName = firstName+" "+middleName+" "+lastName;


            clientNameCtv.setText(fullName);
        }


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
