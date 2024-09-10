package org.smartregister.chw.util;

import static org.smartregister.util.Utils.getAllSharedPreferences;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class ChwWebAppInterface {
    Context mContext;

    String reportType;


    public ChwWebAppInterface(Context c, String reportType) {
        mContext = c;
        this.reportType = reportType;
    }

    @JavascriptInterface
    public String getDataForReport() {
        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.CBHS_REPORT)) {
            ReportUtils.setPrintJobName("cbhs_monthly_summary-" + ReportUtils.getReportPeriod() + ".pdf");
            return ReportUtils.CBHSReport.computeReport(ReportUtils.getReportDate(), mContext);
        }
        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.MOTHER_CHAMPION_REPORT)) {
            ReportUtils.setPrintJobName("mother_champion_report-" + ReportUtils.getReportPeriod() + ".pdf");
            return ReportUtils.MotherChampionReport.computeReport(ReportUtils.getReportDate());
        }


        return "";
    }

    @JavascriptInterface
    public String getData(String key) {
        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.AGYW_REPORT)) {
            ReportUtils.setPrintJobName("AGYW_report_ya_mwezi-" + ReportUtils.getReportPeriod() + ".pdf");
            return ReportUtils.AGYWReport.computeReport(ReportUtils.getReportDate());
        }
        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.CONDOM_DISTRIBUTION_REPORT)) {
            switch (key) {
                case Constants.ReportConstants.CDPReportKeys.ISSUING_REPORTS:
                    ReportUtils.setPrintJobName("CDP_issuing_report_ya_mwezi-" + ReportUtils.getReportPeriod() + ".pdf");
                    return ReportUtils.CDPReports.computeIssuingReports(ReportUtils.getReportDate());
                case Constants.ReportConstants.CDPReportKeys.RECEIVING_REPORTS:
                    ReportUtils.setPrintJobName("CDP_receiving_report_ya_mwezi-" + ReportUtils.getReportPeriod() + ".pdf");
                    return ReportUtils.CDPReports.computeReceivingReports(ReportUtils.getReportDate());
                default:
                    return "";
            }
        }
        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.ICCM_REPORT)){
            switch (key) {
                case Constants.ReportConstants.ICCMReportKeys.CLIENTS_MONTHLY_REPORT:
                    ReportUtils.setPrintJobName("ICCM_clients_report_ya_mwezi-" + ReportUtils.getReportPeriod() + ".pdf");
                    return ReportUtils.ICCMReports.computeClientsReports(ReportUtils.getReportDate());
                case Constants.ReportConstants.ICCMReportKeys.DISPENSING_SUMMARY:
                    ReportUtils.setPrintJobName("ICCM_dispensing_summary_ya_mwezi-" + ReportUtils.getReportPeriod() + ".pdf");
                    return ReportUtils.ICCMReports.computeDispensingSummaryReports(ReportUtils.getReportDate());
                case Constants.ReportConstants.ICCMReportKeys.MALARIA_MONTHLY_REPORT:
                    ReportUtils.setPrintJobName("ICCM_malaria_report_ya_mwezi-" + ReportUtils.getReportPeriod() + ".pdf");
                    return ReportUtils.ICCMReports.computeMalariaTestsReports(ReportUtils.getReportDate());
                default:
                    return "";
            }
        }

        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.SBC_REPORT)) {
            ReportUtils.setPrintJobName("SBC_report_ya_mwezi-" + ReportUtils.getReportPeriod() + ".pdf");
            return ReportUtils.SbcReports.computeClientsReports(ReportUtils.getReportDate());
        }

        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.GBV_REPORT)) {
            ReportUtils.setPrintJobName("GBV_report_ya_mwezi-" + ReportUtils.getReportPeriod() + ".pdf");
            return ReportUtils.GbvReports.computeClientsReports(ReportUtils.getReportDate());
        }

        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.GE_REPORT)) {
            ReportUtils.setPrintJobName("GE_report_ya_mwezi-" + ReportUtils.getReportPeriod() + ".pdf");
            return ReportUtils.GeReports.computeClientsReports(ReportUtils.getReportDate());
        }

        if (reportType.equalsIgnoreCase(Constants.ReportConstants.ReportTypes.MVC_REPORT)){
            switch (key) {
                case Constants.ReportConstants.MvcReportKeys.HOUSEHOLD_REGISTRATION_DETAILS_REPORT:
                    ReportUtils.setPrintJobName("MVC Registration Summary - " + ReportUtils.getReportPeriod() + ".pdf");
                    return ReportUtils.MvcReports.computeRegistrationSummaryReports(ReportUtils.getReportDate());
                case Constants.ReportConstants.MvcReportKeys.CHILDREN_REGISTRATION_DETAILS_REPORT:
                    ReportUtils.setPrintJobName("MVC Children Registration Details - " + ReportUtils.getReportPeriod() + ".pdf");
                    return ReportUtils.MvcReports.computeChildrenRegistrationDetailsReports(ReportUtils.getReportDate());
                case Constants.ReportConstants.MvcReportKeys.SERVICES_PROVIDED_TO_HOUSEHOLDS_REPORT:
                    ReportUtils.setPrintJobName("MVC Services Provided To Households - " + ReportUtils.getReportPeriod() + ".pdf");
                    return ReportUtils.MvcReports.computeServicesProvidedToHouseholdsReports(ReportUtils.getReportDate());
                case Constants.ReportConstants.MvcReportKeys.SERVICES_PROVIDED_TO_CHILDREN_REPORT:
                    ReportUtils.setPrintJobName("MVC Services Provided To Children -" + ReportUtils.getReportPeriod() + ".pdf");
                    return ReportUtils.MvcReports.computeServicesProvidedToChildrenReports(ReportUtils.getReportDate());
                default:
                    return "";
            }
        }

        return "";
    }


    @JavascriptInterface
    public String getDataPeriod() {
        return ReportUtils.getReportPeriod();
    }

    @JavascriptInterface
    public String getDataPeriod(String reportKey) {
        return ReportUtils.getReportPeriod();
    }

    @JavascriptInterface
    public String getReportingFacility() {
        return getAllSharedPreferences().fetchCurrentLocality();
    }
}
