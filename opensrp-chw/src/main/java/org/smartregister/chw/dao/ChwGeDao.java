package org.smartregister.chw.dao;

import org.smartregister.chw.ge.dao.GeDao;
import org.smartregister.chw.ge.util.Constants;

import java.util.List;

public class ChwGeDao extends GeDao {

    public static List<GeMobilization> getGeMobilizationSessions() {
        String sql = "SELECT * FROM " + Constants.TABLES.GE_MOBILIZATION_SESSIONS;

        DataMap<GeDao.GeMobilization> dataMap = cursor -> {
            GeMobilization geMobilization = new GeMobilization();
            geMobilization.setBaseEntityID(cursor.getString(cursor.getColumnIndex("id")));

            geMobilization.setEventStartDate(cursor.getString(cursor.getColumnIndex("event_start_date")));
            geMobilization.setEventEndDate(cursor.getString(cursor.getColumnIndex("event_end_date")));
            geMobilization.setMobilizationEventType(cursor.getString(cursor.getColumnIndex("event_type")));
            geMobilization.setEventSupporter(cursor.getString(cursor.getColumnIndex("event_supporter")));

            return geMobilization;
        };

        List<GeMobilization> res = readData(sql, dataMap);
        if (res == null || res.isEmpty()) return null;
        return res;
    }

}
