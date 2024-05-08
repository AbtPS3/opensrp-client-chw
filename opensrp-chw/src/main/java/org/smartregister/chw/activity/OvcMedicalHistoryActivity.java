package org.smartregister.chw.activity;

import static com.vijay.jsonwizard.constants.JsonFormConstants.COUNT;
import static org.smartregister.chw.core.utils.CoreJsonFormUtils.updateValues;
import static org.smartregister.chw.util.PmtctVisitUtils.deleteProcessedVisit;
import static org.smartregister.opd.utils.OpdConstants.JSON_FORM_KEY.VISIT_ID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
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
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.interactor.GbvMedicalHistoryInteractor;
import org.smartregister.chw.ovc.domain.MemberObject;
import org.smartregister.chw.ovc.util.Constants;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class OvcMedicalHistoryActivity extends CoreAncMedicalHistoryActivity {
    private static MemberObject gbvMemberObject;

    private final Flavor flavor = new FpMedicalHistoryActivityFlv();

    private ProgressBar progressBar;

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, OvcMedicalHistoryActivity.class);
        gbvMemberObject = memberObject;
        activity.startActivity(intent);
    }

    @Override
    public void initializePresenter() {
        presenter = new BaseAncMedicalHistoryPresenter(new GbvMedicalHistoryInteractor(), this, gbvMemberObject.getBaseEntityId());
    }

    @Override
    public void setUpView() {
        linearLayout = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.linearLayoutMedicalHistory);
        progressBar = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.progressBarMedicalHistory);

        TextView tvTitle = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.tvTitle);
        tvTitle.setText(getString(org.smartregister.chw.opensrp_chw_anc.R.string.back_to, gbvMemberObject.getFullName()));

        ((TextView) findViewById(R.id.medical_history)).setText(getString(R.string.visits_history));
    }

    @Override
    public View renderView(List<Visit> visits) {
        super.renderView(visits);
        View view = flavor.bindViews(this);
        displayLoadingState(true);
        flavor.processViewData(visits, this);
        displayLoadingState(false);
        TextView agywVisitTitle = view.findViewById(org.smartregister.chw.core.R.id.customFontTextViewHealthFacilityVisitTitle);
        agywVisitTitle.setText(R.string.visits_history);
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
            AllSharedPreferences allSharedPreferences = org.smartregister.util.Utils.getAllSharedPreferences();
            try {
                String jsonString = data.getStringExtra(org.smartregister.chw.hivst.util.Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                if (encounterType.equals(Constants.EVENT_TYPE.OVC_FOLLOW_UP_VISIT)) {
                    if (form.has(VISIT_ID)) {
                        String deletedVisitId = form.getString(VISIT_ID);
                        form.remove(VISIT_ID);
                        deleteProcessedVisit(deletedVisitId, memberObject.getBaseEntityId());
                    }

                    Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, CoreReferralUtils.setEntityId(jsonString, memberObject.getBaseEntityId()), null);
                    org.smartregister.chw.anc.util.JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
                    NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
                    finish();
                }
            } catch (Exception e) {
                Timber.e(e, "OvcMedicalHistoryActivity -- > onActivityResult");
            }
        }
    }

    private class FpMedicalHistoryActivityFlv extends DefaultAncMedicalHistoryActivityFlv {
        private final StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);

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

                    String[] params = {
                            "type_of_violence_experienced",
                            "indication_of_sexual_violence",
                            "indication_of_physical_violence",
                            "indication_of_neglect",
                            "indication_of_exploitation",
                            "indication_of_emotional_violence",
                            "time_since_incident",
                            "location_where_the_incident_occurred",
                            "was_education_provided",
                            "emergency_services_offered",
                            "referral_to_other_services"
                    };
                    extractVisitDetails(visits, params, visitDetails, x, context);


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
            linearLayoutLastVisit.setVisibility(View.VISIBLE);
            if (days < 1) {
                customFontTextViewLastVisit.setText(org.smartregister.chw.core.R.string.less_than_twenty_four);
            } else {
                customFontTextViewLastVisit.setText(StringUtils.capitalize(MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.days_ago), String.valueOf(days))));
            }
        }

        public void startFormActivity(JSONObject jsonForm, Context context) {
            Intent intent = new Intent(context, Utils.metadata().familyMemberFormActivity);
            intent.putExtra(FamilyPlanningConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

            startActivityForResult(intent, FamilyPlanningConstants.REQUEST_CODE_GET_JSON);
        }


        protected void processVisit(List<LinkedHashMap<String, String>> community_visits, Context context, List<Visit> visits) {
            if (community_visits != null && community_visits.size() > 0) {
                linearLayoutHealthFacilityVisit.setVisibility(View.VISIBLE);

                int x = 0;
                for (LinkedHashMap<String, String> vals : community_visits) {
                    View view = inflater.inflate(R.layout.medical_history_visit, null);
                    TextView tvTitle = view.findViewById(R.id.title);
                    View edit = view.findViewById(R.id.textview_edit);
                    LinearLayout visitDetailsLayout = view.findViewById(R.id.visit_details_layout);

                    tvTitle.setText(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(visits.get(x).getDate()));

                    if (x == visits.size() - 1 && Days.daysBetween(new DateTime(visits.get(x).getDate()), new DateTime()).getDays() < 7) {
                        int position = x;
                        edit.setVisibility(View.VISIBLE);
                        edit.setOnClickListener(view1 -> {
                            Visit visit = visits.get(position);
                            try {
                                //TODO Finalize this implementation
//                                startFormForEdit(R.string.ovc_edit_home_visit, Constants.FORMS.GBV_HOME_VISIT, visit.getBaseEntityId(), visit.getVisitId(), context);
                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        });
                    }


                    for (LinkedHashMap.Entry<String, String> entry : vals.entrySet()) {
                        TextView visitDetailTv = new TextView(context);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                ((int) LinearLayout.LayoutParams.MATCH_PARENT, (int) LinearLayout.LayoutParams.WRAP_CONTENT);

                        visitDetailTv.setLayoutParams(params);
                        float scale = context.getResources().getDisplayMetrics().density;
                        int dpAsPixels = (int) (10 * scale + 0.5f);
                        visitDetailTv.setPadding(dpAsPixels, 0, 0, 0);
                        visitDetailsLayout.addView(visitDetailTv);


                        try {
                            int resource = context.getResources().getIdentifier("gbv_" + entry.getKey(), "string", context.getPackageName());
                            evaluateView(context, vals, visitDetailTv, entry.getKey(), resource);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                    linearLayoutHealthFacilityVisitDetails.addView(view, 0);

                    x++;
                }
            }
        }

        public void startFormForEdit(Integer title_resource, String formName, String baseEntityId, String deletedVisitId, Context context) {
            try {
//                Finalize this implementation
//                Event event = getEditEvent(baseEntityId, Constants.EVENT_TYPE.GBV_HOME_VISIT);
                Event event = null;

                final List<Obs> observations = event.getObs();
//                Finalize this implementation
//                JSONObject form = getFormWithMetaData(baseEntityId, context, formName, Constants.EVENT_TYPE.GBV_HOME_VISIT);
                JSONObject form = null;

                if (form != null) {
                    JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                    JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                    updateValues(jsonArray, observations);

                    //Checking if the form has multiple steps and prefilling them if they exist
                    if (form.getInt("count") > 1) {
                        for (int i = 2; i <= form.getInt("count"); i++) {
                            JSONArray stepFields = form.getJSONObject("step" + i).getJSONArray(JsonFormUtils.FIELDS);
                            updateValues(stepFields, observations);
                        }
                    }
                    form.put(VISIT_ID, deletedVisitId);
                }

                ((Activity) context).startActivityForResult(getStartEditFormIntent(form, context.getString(title_resource), context), JsonFormUtils.REQUEST_CODE_GET_JSON);
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        public Intent getStartEditFormIntent(JSONObject jsonForm, String title, Context context) {
            Intent intent = FormUtils.getStartFormActivity(jsonForm, null, context);
            intent.putExtra(org.smartregister.chw.hivst.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

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

        private void evaluateView(Context context, Map<String, String> vals, TextView tv, String valueKey, int viewTitleStringResource) {
            if (StringUtils.isNotBlank(getMapValue(vals, valueKey))) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(context.getString(viewTitleStringResource), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n");

                String stringValue = getMapValue(vals, valueKey);
                String[] stringValueArray;
                if (stringValue.contains(",")) {
                    stringValueArray = stringValue.split(",");
                    for (String value : stringValueArray) {
                        spannableStringBuilder.append(getStringResource(context, value.trim()) + "\n", new BulletSpan(10), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    spannableStringBuilder.append(getStringResource(context, stringValue)).append("\n");
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

        private String getStringResource(Context context, String resourceName) {
            int resourceId = context.getResources().
                    getIdentifier("gbv_" + resourceName.trim(), "string", context.getPackageName());
            try {
                return context.getString(resourceId);
            } catch (Exception e) {
                Timber.e(e);
                return resourceName;
            }
        }
    }
}
