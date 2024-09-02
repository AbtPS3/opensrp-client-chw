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

public class MvcServicesProvidedToHouseholdsReportObject extends ReportObject {

    private final List<String> indicatorCodesWithAgeGroups = new ArrayList<>();

    private final String[] indicatorCodes = new String[]{
            "mvc-ccd-education-home",
            "mvc-ecd-education-cc",
            "mvc-parenting-groups",
            "mvc-parenting-messages",
            "mvc-infant-feeding",
            "mvc-severe-malnutrition",
            "mvc-moderate-malnutrition",
            "mvc-not-malnourished",
            "mvc-nutrition-counseling",
            "mvc-food-support",
            "mvc-linked-services",
            "mvc-hiv-risk",
            "mvc-hiv-prevention",
            "mvc-disclosure-support",
            "mvc-hiv-support-group",
            "mvc-art-adherence",
            "mvc-join-chf-tika",
            "mvc-gbv-support",
            "mvc-slg-member",
            "mvc-consumption-support",
            "mvc-cash-transfer",
            "mvc-entrepreneurship-training",
            "mvc-iga-establishment",
            "mvc-market-link",
            "mvc-succession-plan",
            "mvc-agricultural-extension",
            "mvc-referral-health-services",
            "mvc-referral-nutrition",
            "mvc-referral-education",
            "mvc-referral-child-protection",
            "mvc-referral-psychosocial-support",
            "mvc-referral-economic-strengthening",
            "mvc-referral-others",
            "mvc-health-related-services",
            "mvc-nutrition",
            "mvc-education",
            "mvc-child-protection",
            "mvc-psycho-social-support",
            "mvc-economic-strengthening",
            "mvc-others",
            "mvc-caregiver-referrals-accompanied-to-services",
            "mvc-caregiver-referrals-assisted-with-transport",
            "mvc-house-renovation-support",
            "mvc-house-construction-support",
            "mvc-wash-education",
            "mvc-clothing-support"};

    private final String[] clientSex = new String[]{"F", "M"};

    private final String[] indicatorAgeGroups = new String[]{"17", "18-24", "25-59", "60-and-above"};

    private final Date reportDate;

    public MvcServicesProvidedToHouseholdsReportObject(Date reportDate) {
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
