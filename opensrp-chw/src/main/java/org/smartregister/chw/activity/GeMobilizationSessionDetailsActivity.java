package org.smartregister.chw.activity;

import static com.vijay.jsonwizard.constants.JsonFormConstants.COUNT;
import static org.smartregister.chw.core.utils.CoreJsonFormUtils.getEditEvent;
import static org.smartregister.chw.core.utils.CoreJsonFormUtils.getFormWithMetaData;
import static org.smartregister.chw.core.utils.CoreJsonFormUtils.updateValues;
import static org.smartregister.chw.sbc.util.Constants.EVENT_TYPE.SBC_HEALTH_EDUCATION_MOBILIZATION;
import static org.smartregister.chw.sbc.util.Constants.FORMS.SBC_MOBILIZATION_SESSION;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.VISIT_ID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.presenter.BaseAncMedicalHistoryPresenter;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.activity.CoreAncMedicalHistoryActivity;
import org.smartregister.chw.core.activity.DefaultAncMedicalHistoryActivityFlv;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.chw.interactor.GeMobilizationSessionDetailsInteractor;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.chw.sbc.util.VisitUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class GeMobilizationSessionDetailsActivity extends CoreAncMedicalHistoryActivity {
    private static String baseEntityId;

    private final Flavor flavor = new GeMobilizationSessionDetailsActivityFlv();

    private ProgressBar progressBar;

    public static void startMe(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, GeMobilizationSessionDetailsActivity.class);
        activity.startActivity(intent);
        GeMobilizationSessionDetailsActivity.baseEntityId = baseEntityId;
    }

    @Override
    public void initializePresenter() {
        presenter = new BaseAncMedicalHistoryPresenter(new GeMobilizationSessionDetailsInteractor(), this, baseEntityId);
    }

    @Override
    public void setUpView() {
        linearLayout = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.linearLayoutMedicalHistory);
        progressBar = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.progressBarMedicalHistory);

        TextView tvTitle = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.tvTitle);
        tvTitle.setText(getString(R.string.sbc_back_to_all_mobilization_sessions));

        ((TextView) findViewById(R.id.medical_history)).setText(getString(R.string.ge_mobilization));
    }

    @Override
    public View renderView(List<Visit> visits) {
        super.renderView(visits);
        View view = flavor.bindViews(this);
        displayLoadingState(true);
        flavor.processViewData(visits, this);
        displayLoadingState(false);
        TextView sbcVisitTitle = view.findViewById(org.smartregister.chw.core.R.id.customFontTextViewHealthFacilityVisitTitle);
        sbcVisitTitle.setText(R.string.ge_mobilization);
        return view;
    }

    @Override
    public void displayLoadingState(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            AllSharedPreferences allSharedPreferences = Utils.getAllSharedPreferences();
            try {
                String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                if (encounterType.equals(org.smartregister.chw.ge.util.Constants.EVENT_TYPE.GE_MOBILIZATION)) {
                    if (form.has(VISIT_ID)) {
                        String deletedVisitId = form.getString(VISIT_ID);
                        form.remove(VISIT_ID);
                        VisitUtils.deleteProcessedVisit(deletedVisitId, baseEntityId);
                    }

                    Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, CoreReferralUtils.setEntityId(jsonString, baseEntityId), Constants.TABLES.SBC_MOBILIZATION_SESSIONS);
                    org.smartregister.chw.anc.util.JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
                    NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
                    finish();
                }
            } catch (Exception e) {
                Timber.e(e, "GeMobilizationSessionDetailsActivity -- > onActivityResult");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    private static class GeMobilizationSessionDetailsActivityFlv extends DefaultAncMedicalHistoryActivityFlv {
        private final StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        @Override
        protected void processAncCard(String has_card, Context context) {
            // super.processAncCard(has_card, context);
            linearLayoutAncCard.setVisibility(View.GONE);
        }

        @Override
        protected void processHealthFacilityVisit(List<Map<String, String>> hf_visits, Context context) {
            //super.processHealthFacilityVisit(hf_visits, context);
        }

        @Override
        public void processViewData(List<Visit> visits, Context context) {

            if (!visits.isEmpty()) {
                int days = 0;
                List<LinkedHashMap<String, String>> hf_visits = new ArrayList<>();

                int x = 0;
                while (x < visits.size()) {
                    LinkedHashMap<String, String> visitDetails = new LinkedHashMap<>();
                    // the first object in this list is the days difference
                    if (x == 0) {
                        days = Days.daysBetween(new DateTime(visits.get(visits.size() - 1).getDate()), new DateTime()).getDays();
                    }

                    String[] healthEducationMobilizationLocation = {
                            "event_start_date",
                            "event_end_date",
                            "event_type",
                            "select_campaign_type",
                            "specify",
                            "number_of_male_participants",
                            "number_of_female_participants",
                            "select_campaign_name",
                            "specify_name",
                            "number_of_male_participants_campaign",
                            "number_of_female_participants_campaign",
                            "select_the_program_name",
                            "specify_program",
                            "number_of_male_participants_program",
                            "number_of_female_participants_program",
                            "ward_executive_officer_first_name",
                            "ward_executive_officer_middle_name",
                            "ward_executive_officer_surname",
                            "village",
                            "total_number_of_male_citizens_reached_by_the_campaign",
                            "total_number_of_female_citizens_reached_by_the_campaign",
                            "total_number_of_cases_on_ownership_of_property_by_male_clients_received_after_the_campaign",
                            "total_number_of_cases_on_ownership_of_property_by_female_clients_received_after_the_campaign",
                            "total_number_of_referrals_cases_after_receiving_cases_on_ownership_of_property",
                            "total_number_of_closed_cases_after_receiving_cases_on_ownership_of_property",
                            "total_number_of_on-going_cases_after_receiving_cases_on_ownership_of_property",
                            "total_number_of_referral_to_ward_land_and_housing_tribunal",
                            "total_number_of_referral_to_district_land_and_housing_tribunal",
                            "total_number_of_referral_to_court",
                            "total_number_of_referral_to_police",
                            "total_number_of_referral_to_rganization_Institution",
                            "total_number_of_referral_to_social_welfare",
                            "total_number_of_referral_to_others_referral",
                            "total_number_of_male_citizens_who_own_land",
                            "total_number_of_female_citizens_who_own_land",
                            "total_number_of_male_citizens_who_own_buildings",
                            "total_number_of_female_citizens_who_own_buildings",
                            "total_number_of_male_citizens_who_own_vehicles",
                            "total_number_of_female_citizens_who_own_vehicles",
                            "total_number_of_male_citizens_who_own_livestock",
                            "total_number_of_female_citizens_who_own_livestock",
                            "reasons_for_differences_in_ratio_of_ownership_between_male_and_female",
                            "specify_reason",
                            "number_of_individual_loan_recepient",
                            "number_of_group_loan_recepient",
                            "registration_date",
                            "group_type",
                            "village_loan",
                            "number_of_male_members_loan",
                            "number_of_female_members_loan",
                            "has_the_group_been_accredited",
                            "has_the_group_individual_received_the_loan",
                            "loan_receiving_date",
                            "loan_type",
                            "loan_deadline_date",
                            "loan_amount",
                            "received_financial_training",
                            "training_type",
                            "specify_training",
                            "training_date",
                            "select_campaign_topic",
                            "number_of_male_less_25_years_reached_by_the_campaign",
                            "number_of_female_less_25_years_reached_by_the_campaign",
                            "number_of_male_25_plus_years_reached_by_the_campaign",
                            "number_of_female_25_plus_years_reached_by_the_campaign",
                            "number_of_male_less_25_years_screened_for_harmful_norms",
                            "number_of_female_less_25_years_screened_for_harmful_norms",
                            "number_of_male_25_plus_years_screened_for_harmful_norms",
                            "number_of_female_25_plus_years_screened_for_harmful_norms",
                            "number_of_male_less_25_years_screened_positive",
                            "number_of_female_less_25_years_screened_positive",
                            "number_of_male_25_plus_years_screened_positive",
                            "number_of_female_25_plus_years_screened_positive",
                            "number_of_male_less_25_years_who_received_services_based_on_issues_raised",
                            "number_of_female_less_25_years_who_received_services_based_on_issues_raised",
                            "number_of_male_25_plus_years_who_received_services_based_on_issues_raised",
                            "number_of_female_25_plus_years_who_received_services_based_on_issues_raised",
                            "event_supporter",
                            "specify_supporter"
                    };
                    extractVisitDetails(visits, healthEducationMobilizationLocation, visitDetails, x, context);

                    hf_visits.add(visitDetails);

                    x++;
                }

                processLastVisit(days, context);
                processVisit(hf_visits, context, visits);
            }
        }

        private void extractVisitDetails(List<Visit> sourceVisits, String[] hf_params, LinkedHashMap<String, String> visitDetailsMap, int iteration, Context context) {
            // get the hf details
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (String param : hf_params) {
                try {
                    List<VisitDetail> details = sourceVisits.get(iteration).getVisitDetails().get(param);
                    map.put(param, getTexts(context, details));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            visitDetailsMap.putAll(map);
        }


        private void processLastVisit(int days, Context context) {
            linearLayoutLastVisit.setVisibility(View.GONE);
            if (days < 1) {
                customFontTextViewLastVisit.setText(org.smartregister.chw.core.R.string.less_than_twenty_four);
            } else {
                customFontTextViewLastVisit.setText(StringUtils.capitalize(MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.days_ago), String.valueOf(days))));
            }
        }


        protected void processVisit(List<LinkedHashMap<String, String>> community_visits, Context context, List<Visit> visits) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            if (community_visits != null && community_visits.size() > 0) {
                linearLayoutHealthFacilityVisit.setVisibility(View.VISIBLE);

                int x = 0;
                for (LinkedHashMap<String, String> vals : community_visits) {
                    View view = inflater.inflate(R.layout.medical_history_visit, null);
                    view.findViewById(R.id.title).setVisibility(View.GONE);
                    TextView tvTypeOfService = view.findViewById(R.id.type_of_service);
                    LinearLayout visitDetailsLayout = view.findViewById(R.id.visit_details_layout);
                    TextView tvEdit = view.findViewById(R.id.textview_edit);

                    // Updating visibility of EDIT button if the visit is the last visit
                    if ((x == visits.size() - 1)) tvEdit.setVisibility(View.VISIBLE);
                    else tvEdit.setVisibility(View.GONE);

                    tvEdit.setOnClickListener(view1 -> {
                        Visit visit = visits.get(0);

                        if (visit.getBaseEntityId() != null) {
                            startFormForEdit(R.string.ge_mobilization_session, org.smartregister.chw.ge.util.Constants.FORMS.GE_MOBILIZATION_SESSION, visit.getBaseEntityId(), visit.getVisitId(), context);

                        }
                    });

                    String visitType;

                    if (SBC_HEALTH_EDUCATION_MOBILIZATION.equals(visits.get(x).getVisitType())) {
                        visitType = context.getString(R.string.sbc_mobilization_session);
                    } else {
                        visitType = visits.get(x).getVisitType();
                    }
                    tvTypeOfService.setText(visitType + " - " + simpleDateFormat.format(visits.get(x).getDate()));


                    for (LinkedHashMap.Entry<String, String> entry : vals.entrySet()) {
                        TextView visitDetailTv = new TextView(context);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        visitDetailTv.setLayoutParams(params);
                        float scale = context.getResources().getDisplayMetrics().density;
                        int dpAsPixels = (int) (10 * scale + 0.5f);
                        visitDetailTv.setPadding(dpAsPixels, 0, 0, 0);
                        visitDetailsLayout.addView(visitDetailTv);


                        try {
                            int resource = context.getResources().getIdentifier("ge_" + entry.getKey(), "string", context.getPackageName());
                            evaluateView(context, vals, visitDetailTv, entry.getKey(), resource, "ge_");
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                    linearLayoutHealthFacilityVisitDetails.addView(view, 0);

                    x++;
                }
            }
        }

        private void evaluateView(Context context, Map<String, String> vals, TextView tv, String valueKey, int viewTitleStringResource, String valuePrefixInStringResources) {
            if (StringUtils.isNotBlank(getMapValue(vals, valueKey))) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(context.getString(viewTitleStringResource), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n");

                String stringValue = getMapValue(vals, valueKey);
                String[] stringValueArray;
                if (stringValue.contains(",")) {
                    stringValueArray = stringValue.split(",");
                    for (String value : stringValueArray) {
                        spannableStringBuilder.append(getStringResource(context, valuePrefixInStringResources, value.trim()) + "\n", new BulletSpan(10), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else if (stringValue.charAt(0) == '[' && stringValue.charAt(stringValue.length() - 1) == ']') {
                    spannableStringBuilder.append(getStringResource(context, valuePrefixInStringResources, stringValue.substring(1, stringValue.length() - 1))).append("\n");
                } else {
                    spannableStringBuilder.append(getStringResource(context, valuePrefixInStringResources, stringValue)).append("\n");
                }
                tv.setText(spannableStringBuilder);
            } else {
                tv.setVisibility(View.GONE);
            }
        }


        private String getMapValue(Map<String, String> map, String key) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            return "";
        }

        private String getStringResource(Context context, String prefix, String resourceName) {
            int resourceId = context.getResources().getIdentifier(prefix + resourceName.trim(), "string", context.getPackageName());
            try {
                return context.getString(resourceId);
            } catch (Exception e) {
                Timber.e(e);
                return resourceName;
            }
        }

        public void startFormForEdit(Integer title_resource, String formName, String baseEntityId, String deletedVisitId, Context context) {
            try {

                Event event = getEditEvent(baseEntityId, org.smartregister.chw.ge.util.Constants.EVENT_TYPE.GE_MOBILIZATION);

                final List<Obs> observations = event.getObs();
                JSONObject form = getFormWithMetaData(baseEntityId, context, formName, org.smartregister.chw.ge.util.Constants.EVENT_TYPE.GE_MOBILIZATION);

                if (form != null) {
                    JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                    JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                    updateValues(jsonArray, observations);

                    //Checking if the form has multiple steps and prefiling them if they exist
                    if (form.getInt("count") > 1) {
                        for (int i = 2; i <= form.getInt("count"); i++) {
                            JSONArray stepFields = form.getJSONObject("step" + i).getJSONArray(JsonFormUtils.FIELDS);
                            updateValues(stepFields, observations);
                        }
                    }
                }

                form.put(VISIT_ID, deletedVisitId);

                ((Activity) context).startActivityForResult(getStartEditFormIntent(form, context.getString(title_resource), context), JsonFormUtils.REQUEST_CODE_GET_JSON);
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        public Intent getStartEditFormIntent(JSONObject jsonForm, String title, Context context) {
            Intent intent = FormUtils.getStartFormActivity(jsonForm, null, context);
            intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

            Form form = new Form();
            form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
            form.setName(title);
            form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);

            try {
                form.setWizard(jsonForm.getInt(COUNT) > 1);
            } catch (JSONException e) {
                Timber.e(e);
                form.setWizard(false);
            }
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
            return intent;
        }


    }
}
