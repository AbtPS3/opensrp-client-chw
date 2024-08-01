package org.smartregister.chw.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.dao.HomeVisitDao;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.ovc.OvcLibrary;
import org.smartregister.chw.ovc.domain.Visit;
import org.smartregister.chw.ovc.repository.VisitDetailsRepository;
import org.smartregister.chw.ovc.repository.VisitRepository;
import org.smartregister.chw.ovc.util.VisitUtils;
import org.smartregister.chw.vmmc.util.TimeUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class MvcVisitUtils extends VisitUtils {
    public static void processVisits() throws Exception {
        processVisits(OvcLibrary.getInstance().visitRepository(), OvcLibrary.getInstance().visitDetailsRepository());
    }

    public static void processVisits(VisitRepository visitRepository, VisitDetailsRepository visitDetailsRepository) throws Exception {
        List<Visit> visits = visitRepository.getAllUnSynced();
        List<Visit> gbvVisits = new ArrayList<>();

        for (Visit v : visits) {
            Date visitDate = new Date(v.getDate().getTime());
            int daysDiff = TimeUtils.getElapsedDays(visitDate);
            if (daysDiff >= 1 && v.getVisitType().equalsIgnoreCase(org.smartregister.chw.gbv.util.Constants.EVENT_TYPE.GBV_FOLLOW_UP_VISIT) && isVisitComplete(v)) {
                gbvVisits.add(v);
            }
        }

        if (!gbvVisits.isEmpty()) {
            processVisits(gbvVisits, visitRepository, visitDetailsRepository);
        }
    }

    public static boolean isVisitComplete(Visit v) {
        try {
            JSONObject jsonObject = new JSONObject(v.getJson());
            JSONArray obs = jsonObject.getJSONArray("obs");
            List<Boolean> checks = new ArrayList<Boolean>();

            boolean isVisitTypeComplete = computeCompletionStatus(obs, "visit_type");
            checks.add(isVisitTypeComplete);

            boolean isNeedAssessmentComplete = computeCompletionStatus(obs, "has_need_assessment_been_conducted");
            checks.add(isNeedAssessmentComplete);


            boolean isPsychosocialSupportComplete = computeCompletionStatus(obs, "ecd_psychosocial_support");
            checks.add(isPsychosocialSupportComplete);

            boolean isHealthCareServiceProvidedComplete = computeCompletionStatus(obs, "health_care_service_provided");
            checks.add(isHealthCareServiceProvidedComplete);


            boolean isChildProtectionServiceComplete = computeCompletionStatus(obs, "child_protection_service");
            checks.add(isChildProtectionServiceComplete);


            boolean isReferralsProvidedComplete = computeCompletionStatus(obs, "referrals_provided");
            checks.add(isReferralsProvidedComplete);

            if (hasHivRiskAssessment(v)) {
                boolean isHivRiskAssessmentComplete = computeCompletionStatus(obs, "child_ever_tested_for_hiv");
                checks.add(isHivRiskAssessmentComplete);
            }

            if (!checks.contains(false)) {
                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return false;
    }

    public static boolean hasHivRiskAssessment(Visit visit) {
        boolean canManageCase = false;
        try {
            JSONObject jsonObject = new JSONObject(visit.getJson());
            JSONArray obs = jsonObject.getJSONArray("obs");
            int size = obs.length();
            for (int i = 0; i < size; i++) {
                JSONObject checkObj = obs.getJSONObject(i);
                if (checkObj.getString("fieldCode").equalsIgnoreCase("health_care_service_provided")) {
                    JSONArray values = checkObj.getJSONArray("values");
                    if ((values.getString(0).contains("hiv_risk_assessment"))) {
                        canManageCase = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return canManageCase;
    }

    public static boolean shouldProceedWithOtherChecks(Visit visit) {
        boolean canManageCase = false;
        try {
            JSONObject jsonObject = new JSONObject(visit.getJson());
            JSONArray obs = jsonObject.getJSONArray("obs");
            int size = obs.length();
            for (int i = 0; i < size; i++) {
                JSONObject checkObj = obs.getJSONObject(i);
                if (checkObj.getString("fieldCode").equalsIgnoreCase("client_consent_after_counseling")) {
                    JSONArray values = checkObj.getJSONArray("values");
                    if ((values.getString(0).equalsIgnoreCase("yes"))) {
                        canManageCase = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return canManageCase;
    }

    public static boolean shouldProvideLabInvestigation(Visit visit) {
        boolean canManageCase = false;
        try {
            JSONObject jsonObject = new JSONObject(visit.getJson());
            JSONArray obs = jsonObject.getJSONArray("obs");
            int size = obs.length();
            for (int i = 0; i < size; i++) {
                JSONObject checkObj = obs.getJSONObject(i);
                if (checkObj.getString("fieldCode").equalsIgnoreCase("does_the_client_need_lab_investigation")) {
                    JSONArray values = checkObj.getJSONArray("values");
                    if ((values.getString(0).equalsIgnoreCase("yes"))) {
                        canManageCase = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return canManageCase;
    }


    public static void manualProcessVisit(Visit visit) throws Exception {
        List<Visit> manualProcessedVisits = new ArrayList<>();
        VisitDetailsRepository visitDetailsRepository = OvcLibrary.getInstance().visitDetailsRepository();
        VisitRepository visitRepository = OvcLibrary.getInstance().visitRepository();

        if (isVisitComplete(visit)) {
            manualProcessedVisits.add(visit);
        }

        if (!manualProcessedVisits.isEmpty()) {
            processVisits(manualProcessedVisits, visitRepository, visitDetailsRepository);
        }
    }

    public static boolean computeCompletionStatus(JSONArray obs, String checkString) throws JSONException {
        int size = obs.length();
        for (int i = 0; i < size; i++) {
            JSONObject checkObj = obs.getJSONObject(i);
            if (checkObj.getString("fieldCode").equalsIgnoreCase(checkString)) {
                return true;
            }
        }
        return false;
    }

    public static void deleteSavedEvent(AllSharedPreferences allSharedPreferences, String baseEntityId, String eventId, String formSubmissionId, String type) {
        Event event = (Event) new Event()
                .withBaseEntityId(baseEntityId)
                .withEventDate(new Date())
                .withEventType(org.smartregister.chw.anc.util.Constants.EVENT_TYPE.DELETE_EVENT)
                .withLocationId(org.smartregister.chw.anc.util.JsonFormUtils.locationId(allSharedPreferences))
                .withProviderId(allSharedPreferences.fetchRegisteredANM())
                .withEntityType(type)
                .withFormSubmissionId(UUID.randomUUID().toString())
                .withDateCreated(new Date());

        event.addDetails(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.DELETE_EVENT_ID, eventId);
        event.addDetails(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.DELETE_FORM_SUBMISSION_ID, formSubmissionId);

        try {
            NCUtils.processEvent(event.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(event)));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void deleteProcessedVisit(String visitID, String baseEntityId) {
        // check if the event
        AllSharedPreferences allSharedPreferences = ImmunizationLibrary.getInstance().context().allSharedPreferences();
        org.smartregister.chw.anc.domain.Visit visit = AncLibrary.getInstance().visitRepository().getVisitByVisitId(visitID);
        if (visit == null || !visit.getProcessed()) return;

        Event processedEvent = HomeVisitDao.getEventByFormSubmissionId(visit.getFormSubmissionId());
        if (processedEvent == null) return;

        MvcVisitUtils.deleteSavedEvent(allSharedPreferences, baseEntityId, processedEvent.getEventId(), processedEvent.getFormSubmissionId(), "event");
        AncLibrary.getInstance().visitRepository().deleteVisit(visitID);
    }

}
