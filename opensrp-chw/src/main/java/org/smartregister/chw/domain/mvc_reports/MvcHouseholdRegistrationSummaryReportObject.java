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

public class MvcHouseholdRegistrationSummaryReportObject extends ReportObject {

    private final List<String> indicatorCodesWithAgeGroups = new ArrayList<>();

    private final String[] indicatorCodes = new String[]{"mvc-child-headed", "mvc-adult-headed", "mvc-elderly-headed", "mvc-employed", "mvc-unemployed", "mvc-insured","mvc-uninsured","mvc-disabled","mvc-hiv","mvc-chronic"};

    private final String[] clientSex = new String[]{"F", "M"};

    private final String[] indicatorAgeGroups = new String[]{"17", "18-24", "25-59", "60-and-above"};

    private final Date reportDate;

    public MvcHouseholdRegistrationSummaryReportObject(Date reportDate) {
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
