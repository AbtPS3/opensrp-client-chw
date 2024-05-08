package org.smartregister.chw.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.smartregister.chw.R;
import org.smartregister.chw.anc.activity.BaseAncMemberProfileActivity;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.activity.CoreAboveFiveChildProfileActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileMenuActivity;
import org.smartregister.chw.core.activity.CoreFamilyRemoveMemberActivity;
import org.smartregister.chw.core.utils.ChildDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.fragment.FamilyProfileMemberFragment;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.model.FamilyProfileModel;
import org.smartregister.chw.pnc.activity.BasePncMemberProfileActivity;
import org.smartregister.chw.presenter.FamilyProfilePresenter;
import org.smartregister.chw.tb.dao.TbDao;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Objects;

public class OvcFamilyProfileActivity extends CoreFamilyProfileActivity {
    private TextView tvEventDate;
    private TextView tvInterpunct;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        tvEventDate = findViewById(R.id.textview_event_date);
        tvInterpunct = findViewById(R.id.interpunct);
    }

    @Override
    public void setEventDate(String eventDate) {
        if (ChwApplication.getApplicationFlavor().hasEventDateOnFamilyProfile()) {
            tvEventDate.setVisibility(View.VISIBLE);
            tvInterpunct.setVisibility(View.VISIBLE);
            tvEventDate.setText(String.format(this.getString(R.string.created), eventDate));
        }
    }

    @Override
    protected void refreshPresenter() {
        this.presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName),
                familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    protected void refreshList(Fragment fragment) {
        if (fragment instanceof BaseRegisterFragment) {
            if (fragment instanceof FamilyProfileMemberFragment) {
                FamilyProfileMemberFragment familyProfileMemberFragment = ((FamilyProfileMemberFragment) fragment);
                if (familyProfileMemberFragment.presenter() != null) {
                    familyProfileMemberFragment.refreshListView();
                }
            }
        }
    }

    @Override
    protected Class<? extends CoreFamilyRemoveMemberActivity> getFamilyRemoveMemberClass() {
        return FamilyRemoveMemberActivity.class;
    }

    @Override
    protected Class<? extends CoreFamilyProfileMenuActivity> getFamilyProfileMenuClass() {
        return FamilyProfileMenuActivity.class;
    }

    @Override
    protected void initializePresenter() {
        super.initializePresenter();
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    public FamilyProfilePresenter getFamilyProfilePresenter() {
        return (FamilyProfilePresenter) presenter;
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        FamilyProfileMemberFragment profileMemberFragment = (FamilyProfileMemberFragment) FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras());
        adapter.addFragment(profileMemberFragment, this.getString(org.smartregister.family.R.string.member).toUpperCase());
        viewPager.setAdapter(adapter);

        if (getIntent().getBooleanExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, false) || getIntent().getBooleanExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false)) {
            viewPager.setCurrentItem(1);
        }

        return viewPager;
    }

    @Override
    protected Class<?> getFamilyOtherMemberProfileActivityClass() {
        return OvcProfileActivity.class;
    }

    @Override
    protected Class<? extends CoreAboveFiveChildProfileActivity> getAboveFiveChildProfileActivityClass() {
        return AboveFiveChildProfileActivity.class;
    }

    @Override
    protected Class<? extends CoreChildProfileActivity> getChildProfileActivityClass() {
        return ChildProfileActivity.class;
    }

    @Override
    protected Class<? extends BaseAncMemberProfileActivity> getAncMemberProfileActivityClass() {
        return AncMemberProfileActivity.class;
    }

    @Override
    public void goToAncProfileActivity(CommonPersonObjectClient patient, Bundle bundle) {
        AncMemberProfileActivity.startMe(this, patient.getCaseId());
    }

    @Override
    protected Class<? extends BasePncMemberProfileActivity> getPncMemberProfileActivityClass() {
        return PncMemberProfileActivity.class;
    }

    @Override
    protected void goToFpProfile(String baseEntityId, Activity activity) {
        FPMemberProfileActivity.startFpMemberProfileActivity(activity, baseEntityId);
    }

    @Override
    protected void goToHivProfile(String baseEntityId, Activity activity) {
        HivProfileActivity.startHivProfileActivity(this, Objects.requireNonNull(HivDao.getMember(baseEntityId)));
    }

    @Override
    protected void goToTbProfile(String baseEntityId, Activity activity) {
        TbProfileActivity.startTbProfileActivity(this, Objects.requireNonNull(TbDao.getMember(baseEntityId)));
    }


    @Override
    protected boolean isAncMember(String baseEntityId) {
        return ChwApplication.getApplicationFlavor().hasANC() && getFamilyProfilePresenter().isAncMember(baseEntityId);
    }

    @Override
    protected HashMap<String, String> getAncFamilyHeadNameAndPhone(String baseEntityId) {
        return getFamilyProfilePresenter().getAncFamilyHeadNameAndPhone(baseEntityId);
    }

    @Override
    protected CommonPersonObject getAncCommonPersonObject(String baseEntityId) {
        return getFamilyProfilePresenter().getAncCommonPersonObject(baseEntityId);
    }

    @Override
    protected boolean isPncMember(String baseEntityId) {
        return ChwApplication.getApplicationFlavor().hasPNC() && getFamilyProfilePresenter().isPncMember(baseEntityId);
    }

    @Override
    protected CommonPersonObject getPncCommonPersonObject(String baseEntityId) {
        return getFamilyProfilePresenter().getPncCommonPersonObject(baseEntityId);
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    public void goToProfileActivity(View view, Bundle fragmentArguments) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) view.getTag();
            OvcProfileActivity.startMe(this, commonPersonObjectClient.getCaseId());
        }
    }
}
