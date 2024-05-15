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

    private final String[] indicatorCodes = new String[]{"mvc-number-registered", "mvc-birth-certificate", "mvc-disabled-mvc", "mvc-orphaned-hiv", "mvc-orphaned-other", "mvc-living-hiv", "mvc-malnourished", "mvc-conflict-law", "mvc-street", "mvc-institutional-care", "mvc-worst-labor", "mvc-violence", "mvc-others", "mvc-albinism", "mvc-mental-disability", "mvc-hearing-disability", "mvc-visual-disability", "mvc-physical-disability", "mvc-speech-impairment-1", "mvc-speech-impairment-2", "mvc-others-2", "mvc-in-school", "mvc-not-in-school", "mvc-early-childhood", "mvc-pre-primary", "mvc-primary-school", "mvc-secondary-school", "mvc-vocational-training", "mvc-college", "mvc-father-mother-care", "mvc-grandparent-care", "mvc-aunt-uncle-care", "mvc-aunt-uncle-care", "mvc-step-parent-care", "mvc-sibling-care", "mvc-institutional-care", "mvc-cousin-care", "mvc-family-friend-care", "mvc-foster-parent-care", "mvc-adoptive-parent", "mvc-other", "mvc-cases-opened"};

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
