package org.smartregister.chw.activity;

import static org.smartregister.chw.util.Utils.getClientGender;
import static org.smartregister.chw.util.Utils.updateAgeAndGender;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.chw.BuildConfig;
import org.smartregister.chw.R;
import org.smartregister.chw.agyw.dao.AGYWDao;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.UpdateDetailsUtil;
import org.smartregister.chw.custom_view.GeFloatingMenu;
import org.smartregister.chw.dataloader.AncMemberDataLoader;
import org.smartregister.chw.dataloader.FamilyMemberDataLoader;
import org.smartregister.chw.ge.activity.BaseGeProfileActivity;
import org.smartregister.chw.ge.domain.MemberObject;
import org.smartregister.chw.ge.util.Constants;
import org.smartregister.chw.ge.util.GeJsonFormUtils;
import org.smartregister.chw.hivst.dao.HivstDao;
import org.smartregister.chw.kvp.dao.KvpDao;
import org.smartregister.chw.malaria.dao.IccmDao;
import org.smartregister.chw.model.ReferralTypeModel;
import org.smartregister.chw.sbc.dao.SbcDao;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class GeProfileActivity extends BaseGeProfileActivity {
    private final FamilyOtherMemberProfileActivity.Flavor flavor = new FamilyOtherMemberProfileActivityFlv();

    private final List<ReferralTypeModel> referralTypeModels = new ArrayList<>();

    @Override
    protected void onCreation() {
        super.onCreation();
        addReferralTypes();
    }


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
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {

    }

    @Override
    public void openMedicalHistory() {
        GeServicesHistoryActivity.startMe(this, memberObject);
    }


    private void addReferralTypes() {
        if (BuildConfig.USE_UNIFIED_REFERRAL_APPROACH) {

            //HIV Testing referrals will only be issued to non positive clients
            if (memberObject.getCtcNumber().isEmpty()) {
                referralTypeModels.add(new ReferralTypeModel(getString(R.string.hts_referral), CoreConstants.JSON_FORM.getHtsReferralForm(), CoreConstants.TASKS_FOCUS.CONVENTIONAL_HIV_TEST));
            } else { //HIV Treatment and care referrals will be issued to HIV Positive clients
                referralTypeModels.add(new ReferralTypeModel(getString(R.string.hiv_referral), CoreConstants.JSON_FORM.getHivReferralForm(), CoreConstants.TASKS_FOCUS.SICK_HIV));
            }

            referralTypeModels.add(new ReferralTypeModel(getString(R.string.tb_referral), CoreConstants.JSON_FORM.getTbReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_TB));

            if (isClientEligibleForAnc(memberObject)) {
                referralTypeModels.add(new ReferralTypeModel(getString(R.string.anc_danger_signs), CoreConstants.JSON_FORM.getAncUnifiedReferralForm(), CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS));
                referralTypeModels.add(new ReferralTypeModel(getString(R.string.pnc_referral), CoreConstants.JSON_FORM.getPncUnifiedReferralForm(), CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS));
                if (!AncDao.isANCMember(memberObject.getBaseEntityId())) {
                    referralTypeModels.add(new ReferralTypeModel(getString(R.string.pregnancy_confirmation), CoreConstants.JSON_FORM.getPregnancyConfirmationReferralForm(), CoreConstants.TASKS_FOCUS.PREGNANCY_CONFIRMATION));
                }
            }
            referralTypeModels.add(new ReferralTypeModel(getString(R.string.gbv_referral), CoreConstants.JSON_FORM.getGbvReferralForm(), CoreConstants.TASKS_FOCUS.SUSPECTED_GBV));
        }

    }

    public List<ReferralTypeModel> getReferralTypeModels() {
        return referralTypeModels;
    }

    @Override
    public void initializeFloatingMenu() {
        baseGeFloatingMenu = new GeFloatingMenu(this, memberObject);
        baseGeFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(baseGeFloatingMenu, linearLayoutParams);


        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            if (viewId == R.id.gbv_fab) {
                checkPhoneNumberProvided();
                ((GeFloatingMenu) baseGeFloatingMenu).animateFAB();
            } else if (viewId == R.id.gbv_call_layout) {
                ((GeFloatingMenu) baseGeFloatingMenu).launchCallWidget();
                ((GeFloatingMenu) baseGeFloatingMenu).animateFAB();
            } else if (viewId == R.id.gbv_refer_to_facility_layout) {
                org.smartregister.chw.util.Utils.launchClientReferralActivity(GeProfileActivity.this, getReferralTypeModels(), memberObject.getBaseEntityId());
                ((GeFloatingMenu) baseGeFloatingMenu).animateFAB();
            } else {
                Timber.d("Unknown fab action");
            }
        };

        ((GeFloatingMenu) baseGeFloatingMenu).setFloatMenuClickListener(onClickFloatingMenu);
    }

    private void checkPhoneNumberProvided() {
        boolean phoneNumberAvailable = (StringUtils.isNotBlank(memberObject.getPhoneNumber()));
        ((GeFloatingMenu) baseGeFloatingMenu).redraw(phoneNumberAvailable);
    }

    protected boolean isClientEligibleForAnc(MemberObject memberObject) {
        if (memberObject.getGender().equalsIgnoreCase("Female")) {
            //Obtaining the clients CommonPersonObjectClient used for checking is the client is Of Reproductive Age
            CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

            final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(memberObject.getBaseEntityId());
            final CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
            client.setColumnmaps(commonPersonObject.getColumnmaps());

            return org.smartregister.chw.core.utils.Utils.isMemberOfReproductiveAge(client, 15, 49);
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem addMember = menu.findItem(org.smartregister.chw.core.R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        getMenuInflater().inflate(org.smartregister.chw.core.R.menu.other_member_menu, menu);


        String dob = memberObject.getDob();
        int age = org.smartregister.chw.util.Utils.getAgeFromDate(dob);

        String gender = memberObject.getGender();
        menu.findItem(R.id.action_location_info).setVisible(true);
        if (ChwApplication.getApplicationFlavor().hasHIV()) {
            menu.findItem(R.id.action_cbhs_registration).setVisible(true);
        }
        menu.findItem(R.id.action_tb_registration).setVisible(false);

        if (ChwApplication.getApplicationFlavor().hasFamilyPlanning() && age >= 10 && age <= 49) {
            flavor.updateFpMenuItems(memberObject.getBaseEntityId(), menu);
        } else {
            menu.findItem(R.id.action_fp_initiation).setVisible(false);
        }

        if (ChwApplication.getApplicationFlavor().hasANC() && !AncDao.isANCMember(memberObject.getBaseEntityId()) && age >= 10 && age <= 49 && gender.equalsIgnoreCase("Female")) {
            flavor.updateFpMenuItems(memberObject.getBaseEntityId(), menu);
            menu.findItem(R.id.action_anc_registration).setVisible(true);
        } else {
            menu.findItem(R.id.action_anc_registration).setVisible(false);
        }
        if (ChwApplication.getApplicationFlavor().hasANC() && age >= 10 && age <= 49 && gender.equalsIgnoreCase("Female")) {
            flavor.updateFpMenuItems(memberObject.getBaseEntityId(), menu);
            menu.findItem(R.id.action_pregnancy_out_come).setVisible(true);
        } else {
            menu.findItem(R.id.action_pregnancy_out_come).setVisible(false);
        }
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        if (ChwApplication.getApplicationFlavor().hasMalaria())
            flavor.updateMalariaMenuItems(memberObject.getBaseEntityId(), menu);

        if (ChwApplication.getApplicationFlavor().hasHIVST()) {
            menu.findItem(R.id.action_hivst_registration).setVisible(!HivstDao.isRegisteredForHivst(memberObject.getBaseEntityId()) && age >= 15);
        }

        if (ChwApplication.getApplicationFlavor().hasAGYW()) {
            if (gender.equalsIgnoreCase("Female") && age >= 10 && age <= 24 && !AGYWDao.isRegisteredForAgyw(memberObject.getBaseEntityId())) {
                menu.findItem(R.id.action_agyw_screening).setVisible(true);
            }
        }

        if (ChwApplication.getApplicationFlavor().hasKvp()) {
            menu.findItem(R.id.action_kvp_prep_registration).setVisible(!KvpDao.isRegisteredForKvpPrEP(memberObject.getBaseEntityId()) && age >= 15);
        }

        if (ChwApplication.getApplicationFlavor().hasICCM() && !IccmDao.isRegisteredForIccm((memberObject.getBaseEntityId()))) {
            menu.findItem(R.id.action_iccm_registration).setVisible(true);
        }

        if (ChwApplication.getApplicationFlavor().hasSbc()) {
            menu.findItem(R.id.action_sbc_registration).setVisible(!SbcDao.isRegisteredForSbc(memberObject.getBaseEntityId()) && age >= 10);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_anc_registration) {
            startAncRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_pregnancy_out_come) {
            startPncRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_fp_initiation) {
            startFpRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_fp_ecp_provision) {
            startFpEcpScreening();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_malaria_registration) {
            startMalariaRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_iccm_registration) {
            startIntegratedCommunityCaseManagementEnrollment();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_vmmc_registration) {
            startVmmcRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_registration) {
            if (UpdateDetailsUtil.isIndependentClient(memberObject.getBaseEntityId())) {
                startFormForEdit(org.smartregister.chw.core.R.string.registration_info,
                        CoreConstants.JSON_FORM.getAllClientUpdateRegistrationInfoForm());
            } else {
                startFormForEdit(org.smartregister.chw.core.R.string.edit_member_form_title,
                        CoreConstants.JSON_FORM.getFamilyMemberRegister());
            }
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_hiv_registration) {
            startHivRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_cbhs_registration) {
            startHivRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_tb_registration) {
            startTbRegister();
        }else if (i == org.smartregister.chw.core.R.id.action_hivst_registration) {
            startHivstRegistration();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_agyw_screening) {
            startAgywScreening();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_kvp_prep_registration) {
            startKvpPrEPRegistration();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_sbc_registration) {
            startSbcRegistration();
        }else if (i == org.smartregister.chw.core.R.id.action_gbv_registration) {
            startGbvRegistration();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void startAncRegister() {
        AncRegisterActivity.startAncRegistrationActivity(GeProfileActivity.this, memberObject.getBaseEntityId(), memberObject.getPhoneNumber(),
                org.smartregister.chw.util.Constants.JSON_FORM.getAncRegistration(), null, memberObject.getFamilyBaseEntityId(), memberObject.getFamilyName());
    }

    protected void startPncRegister() {
        PncRegisterActivity.startPncRegistrationActivity(GeProfileActivity.this, memberObject.getBaseEntityId(), memberObject.getPhoneNumber(),
                CoreConstants.JSON_FORM.getPregnancyOutcome(), null, memberObject.getFamilyBaseEntityId(), memberObject.getFamilyName(), null);
    }

    protected void startMalariaRegister() {
        MalariaRegisterActivity.startMalariaRegistrationActivity(GeProfileActivity.this, memberObject.getBaseEntityId(), memberObject.getFamilyBaseEntityId());
    }

    protected void startVmmcRegister() {
        //implement
    }

    protected void startIntegratedCommunityCaseManagementEnrollment() {
        IccmRegisterActivity.startIccmRegistrationActivity(GeProfileActivity.this, memberObject.getBaseEntityId(), memberObject.getFamilyBaseEntityId());
    }

    protected void startHivRegister() {
        String gender = memberObject.getGender();
        String dob = memberObject.getDob();
        int age = org.smartregister.chw.util.Utils.getAgeFromDate(dob);

        try {
            String formName = org.smartregister.chw.util.Constants.JsonForm.getCbhsRegistrationForm();
            JSONObject formJsonObject = (new FormUtils()).getFormJsonFromRepositoryOrAssets(GeProfileActivity.this, formName);
            JSONArray steps = formJsonObject.getJSONArray("steps");
            JSONObject step = steps.getJSONObject(0);
            JSONArray fields = step.getJSONArray("fields");

            updateAgeAndGender(fields, age, gender);

            HivRegisterActivity.startHIVFormActivity(GeProfileActivity.this, memberObject.getBaseEntityId(), formName, formJsonObject.toString());
        } catch (JSONException e) {
            Timber.e(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startTbRegister() {
        try {
            TbRegisterActivity.startTbFormActivity(GeProfileActivity.this, memberObject.getBaseEntityId(), org.smartregister.chw.util.Constants.JSON_FORM.getTbRegistration(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, org.smartregister.chw.util.Constants.JSON_FORM.getTbRegistration()).toString());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    protected void startFpRegister() {

        String gender = memberObject.getGender();

        FpRegisterActivity.startFpRegistrationActivity(this, memberObject.getBaseEntityId(), CoreConstants.JSON_FORM.getFpRegistrationForm(gender));
    }

    protected void startFpEcpScreening() {
        //NOT Required in CHW
    }

    protected void startHivstRegistration() {
        String gender = memberObject.getGender();
        HivstRegisterActivity.startHivstRegistrationActivity(GeProfileActivity.this, memberObject.getBaseEntityId(), gender);
    }

    protected void startAgywScreening() {
        String dob = memberObject.getDob();
        int age = org.smartregister.chw.util.Utils.getAgeFromDate(dob);
        AgywRegisterActivity.startRegistration(GeProfileActivity.this, memberObject.getBaseEntityId(), age);
    }

    protected void startSbcRegistration() {
        SbcRegisterActivity.startRegistration(GeProfileActivity.this, memberObject.getBaseEntityId());
    }

    protected void startGbvRegistration() {
        GbvRegisterActivity.startRegistration(GeProfileActivity.this, memberObject.getBaseEntityId());
    }

    protected void startKvpPrEPRegistration() {
        String gender = getClientGender(memberObject.getBaseEntityId());
        String dob = memberObject.getDob();
        int age = org.smartregister.chw.util.Utils.getAgeFromDate(dob);
        KvpPrEPRegisterActivity.startRegistration(GeProfileActivity.this, memberObject.getBaseEntityId(), gender, age);
    }

    public void startFormForEdit(Integer title_resource, String formName) {
        try {
            JSONObject form = null;
            boolean isPrimaryCareGiver = memberObject.getPrimaryCareGiver().equals(memberObject.getBaseEntityId());
            String titleString = title_resource != null ? getResources().getString(title_resource) : null;

            if (formName.equals(CoreConstants.JSON_FORM.getAncRegistration())) {

                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new AncMemberDataLoader(titleString));
                form = binder.getPrePopulatedForm(formName);
                if (form != null) {
                    form.put(JsonFormUtils.ENCOUNTER_TYPE, CoreConstants.EventType.UPDATE_ANC_REGISTRATION);
                }

            } else if (formName.equals(CoreConstants.JSON_FORM.getFamilyMemberRegister())) {

                String eventName = org.smartregister.chw.util.Utils.metadata().familyMemberRegister.updateEventType;

                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new FamilyMemberDataLoader(memberObject.getFamilyName(), isPrimaryCareGiver, titleString, eventName, memberObject.getUniqueId()));

                form = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getFamilyMemberRegister());
            } else if (formName.equals(CoreConstants.JSON_FORM.getAllClientUpdateRegistrationInfoForm())) {
                String eventName = org.smartregister.chw.util.Utils.metadata().familyMemberRegister.updateEventType;

                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new FamilyMemberDataLoader(memberObject.getFamilyName(), isPrimaryCareGiver, titleString, eventName, memberObject.getUniqueId()));

                form = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getAllClientUpdateRegistrationInfoForm());
            }

            startActivityForResult(org.smartregister.chw.util.JsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

}
