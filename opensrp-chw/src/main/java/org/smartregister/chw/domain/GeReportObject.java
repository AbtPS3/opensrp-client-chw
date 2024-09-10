package org.smartregister.chw.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.dao.ReportDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class GeReportObject extends ReportObject {

    private final List<String> indicatorCodesWithAgeGroups = new ArrayList<>();

    private final String[] indicatorCodes = new String[]{"ge-1", "ge-2", "ge-3", "ge-4", "ge-5", "ge-6", "ge-7", "ge-8", "ge-9", "ge-10", "ge-11", "ge-12", "ge-13", "ge-14"};

    private final String[] clientSex = new String[]{"female", "male"};

    private final Date reportDate;

    public GeReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;
        setIndicatorCodesWithAgeGroups(indicatorCodesWithAgeGroups);
    }

    public void setIndicatorCodesWithAgeGroups(List<String> indicatorCodesWithAgeGroups) {
        int i = 0;
        for (String indicatorCode : indicatorCodes) {
            if (i < 5) {
                for (String clientType : clientSex) {
                    indicatorCodesWithAgeGroups.add(indicatorCode + "-" + clientType);
                }
            } else {
                indicatorCodesWithAgeGroups.add(indicatorCode);
            }
            i++;
        }

    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {
        JSONObject indicatorDataObject = new JSONObject();
        for (String indicatorCode : indicatorCodesWithAgeGroups) {
            int value = ReportDao.getReportPerIndicatorCode(indicatorCode, reportDate);
            indicatorDataObject.put(indicatorCode, value);
        }

        // Calculate and add total values for "totals"
        for (int i = 0; i < 5; i++) {
            String indicatorCode = indicatorCodes[i];
            try {
                int grandTotal = indicatorDataObject.getInt(indicatorCode + "-male") + indicatorDataObject.getInt(indicatorCode + "-female");
                indicatorDataObject.put(indicatorCode + "-grandTotal", grandTotal);
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        return indicatorDataObject;
    }

}
