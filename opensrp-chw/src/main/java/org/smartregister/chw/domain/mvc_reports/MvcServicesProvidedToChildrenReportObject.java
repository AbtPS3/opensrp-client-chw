package org.smartregister.chw.domain.mvc_reports;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.dao.ReportDao;
import org.smartregister.chw.domain.ReportObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class MvcServicesProvidedToChildrenReportObject extends ReportObject {

    private final List<String> indicatorCodesWithAgeGroups = new ArrayList<>();

    private final String[] indicatorCodes = new String[]{
            "mvc-number-of-children-enrolled-in-school",
            "mvc-missed-school",
            "mvc-linked-to-education-support",
            "mvc-supported-vocational-training",
            "mvc-received-business-kit",
            "mvc-linked-to-memkwa",
            "mvc-attended-ecd",
            "mvc-attended-childrens-clubs",
            "mvc-attended-teen-clubs",
            "mvc-vaccinated",
            "mvc-not-vaccinated",
            "mvc-severe-malnutrition",
            "mvc-moderate-malnutrition",
            "mvc-not-malnourished",
            "mvc-joined-chf-tika",
            "mvc-hiv-prevention-counseling",
            "mvc-disclosure-support",
            "mvc-linked-hiv-support-group",
            "mvc-art-adherence-counseling",
            "mvc-hiv-risk-assessment",
            "mvc-hiv-prevention-education",
            "mvc-supported-birth-certificates",
            "mvc-emergency-care",
            "mvc-linked-social-welfare",
            "mvc-linked-mvcc",
            "mvc-child-protection-awareness",
            "mvc-referrals-health-services",
            "mvc-referrals-nutrition",
            "mvc-referrals-education",
            "mvc-referrals-child-protection",
            "mvc-referrals-psychosocial",
            "mvc-referrals-economic-strengthening",
            "mvc-referrals-others",
            "mvc-referrals-completed-health-services",
            "mvc-referrals-completed-nutrition",
            "mvc-referrals-completed-education",
            "mvc-referrals-completed-child-protection",
            "mvc-referrals-completed-psychosocial",
            "mvc-referrals-completed-economic-strengthening",
            "mvc-referrals-completed-others",
            "mvc-referral-accompanied-to-services",
            "mvc-referral-assisted-with-transport",
            "mvc-active-MVC",
            "mvc-graduated-MVC",
            "mvc-transferred-MVC",
            "mvc-exited-without-graduation",
            "mvc-died-MVC"};

    private final String[] clientSex = new String[]{"F", "M"};

    private final String[] indicatorAgeGroups = new String[]{"<1", "1-4", "5-9", "10-14", "15-17", "18+", "total"};

    private final Date reportDate;

    public MvcServicesProvidedToChildrenReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;
        setIndicatorCodesWithAgeGroups(indicatorCodesWithAgeGroups);
    }

    public static int calculateMvcSpecificTotal(HashMap<String, Integer> indicators, String specificKey) {
        int total = 0;

        for (Map.Entry<String, Integer> entry : indicators.entrySet()) {
            String key = entry.getKey().toLowerCase();
            Integer value = entry.getValue();

            // Create a Pattern object
            Pattern regexPattern = Pattern.compile(specificKey);

            // Create a Matcher object
            Matcher matcher = regexPattern.matcher(key);

            if (matcher.find()) {
                total += value;
            }
        }

        return total;
    }

    public void setIndicatorCodesWithAgeGroups(List<String> indicatorCodesWithAgeGroups) {
        for (String indicatorCode : indicatorCodes) {
            for (String sex : clientSex) {
                for (String ageGroup : indicatorAgeGroups) {
                    indicatorCodesWithAgeGroups.add(indicatorCode + "-" + sex + "-" + ageGroup);
                }
            }
        }
    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {
        HashMap<String, Integer> indicatorsValues = new HashMap<>();
        JSONObject indicatorDataObject = new JSONObject();
        for (String indicatorCode : indicatorCodesWithAgeGroups) {
            int value = ReportDao.getReportPerIndicatorCode(indicatorCode, reportDate);
            indicatorsValues.put(indicatorCode, value);
            indicatorDataObject.put(indicatorCode, value);
        }
        return indicatorDataObject;
    }

}
