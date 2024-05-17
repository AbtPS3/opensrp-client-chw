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

public class MvcChildrenRegistrationSummaryReportObject extends ReportObject {

    private final List<String> indicatorCodesWithAgeGroups = new ArrayList<>();

    private final String[] indicatorCodes = new String[]{"mvc-number-registered", "mvc-birth-certificate", "mvc-type-of-vulnerability-disabled",
            "mvc-type-of-vulnerability-orphaned-hiv", "mvc-type-of-vulnerability-orphaned-other",
            "mvc-type-of-vulnerability-living-hiv", "mvc-type-of-vulnerability-malnourished",
            "mvc-type-of-vulnerability-conflict-law", "mvc-type-of-vulnerability-street",
            "mvc-type-of-vulnerability-institutional-care", "mvc-type-of-vulnerability-worst-labor",
            "mvc-type-of-vulnerability-violence", "mvc-type-of-vulnerability-others",
            "mvc-type-of-disability-albinism", "mvc-type-of-disability-mental-disability",
            "mvc-type-of-disability-hearing-disability", "mvc-type-of-disability-visual-disability",
            "mvc-type-of-disability-physical-disability", "mvc-type-of-disability-speech-impairment-1",
            "mvc-type-of-disability-others", "mvc-in-school", "mvc-not-in-school",
            "mvc-level-of-education-early-childhood", "mvc-level-of-education-pre-primary",
            "mvc-level-of-education-primary-school", "mvc-level-of-education-secondary-school",
            "mvc-level-of-education-vocational-training", "mvc-level-of-education-college",
            "mvc-child-primary-caregiver-father-mother-care", "mvc-child-primary-caregiver-grandparent-care",
            "mvc-child-primary-caregiver-aunt-uncle-care", "mvc-child-primary-caregiver-step-parent-care",
            "mvc-child-primary-caregiver-sibling-care", "mvc-child-primary-caregiver-institutional-care",
            "mvc-child-primary-caregiver-cousin-care", "mvc-child-primary-caregiver-family-friend-care",
            "mvc-child-primary-caregiver-foster-parent-care", "mvc-child-primary-caregiver-adoptive-parent-care",
            "mvc-child-primary-caregiver-other", "mvc-child-primary-caregiver-number-cases-opened",
            "mvc-cases-opened-yes"};

    private final String[] clientSex = new String[]{"F", "M"};

    private final String[] indicatorAgeGroups = new String[]{"<1", "1-4", "5-9", "10-14", "15-17", "18+", "total"};

    private final Date reportDate;

    public MvcChildrenRegistrationSummaryReportObject(Date reportDate) {
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
