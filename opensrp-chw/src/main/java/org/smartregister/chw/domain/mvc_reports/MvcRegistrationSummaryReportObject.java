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

public class MvcRegistrationSummaryReportObject extends ReportObject {

    private final List<String> indicatorCodesWithAgeGroups = new ArrayList<>();

    private final String[] indicatorCodes = new String[]{"gbv-1", "gbv-2", "gbv-3", "gbv-4", "gbv-5", "gbv-6"};

    private final String[] clientSex = new String[]{"F", "M"};

    private final String[] indicatorAgeGroups = new String[]{"0-4", "5-9", "10-14", "15-17", "18-19", "20-24", "25-29", "30-34", "35-above"};

    private final Date reportDate;

    public MvcRegistrationSummaryReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;
        setIndicatorCodesWithAgeGroups(indicatorCodesWithAgeGroups);
    }

    public static int calculateGbvSpecificTotal(HashMap<String, Integer> indicators, String specificKey) {
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
            for (String indicatorKey : indicatorAgeGroups) {
                for (String clientType : clientSex) {
                    indicatorCodesWithAgeGroups.add(indicatorCode + "-" + indicatorKey + "-" + clientType);
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

        // Calculate and add total values for "totals"
        for (String indicatorCode : indicatorCodes) {
            for (String sex : clientSex) {
                if (sex.equalsIgnoreCase("male")) {
                    indicatorDataObject.put(indicatorCode + "-totalMale", calculateGbvSpecificTotal(indicatorsValues, indicatorCode + "-" + sex));
                } else {
                    indicatorDataObject.put(indicatorCode + "-totalFemale", calculateGbvSpecificTotal(indicatorsValues, indicatorCode + "-" + sex));
                }
            }
        }
        for (String indicatorCode : indicatorCodes) {
            try {
                int grandTotal = indicatorDataObject.getInt(indicatorCode + "-totalMale") + indicatorDataObject.getInt(indicatorCode + "-totalFemale");
                indicatorDataObject.put(indicatorCode + "-grandTotal", grandTotal);
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        return indicatorDataObject;
    }

}
