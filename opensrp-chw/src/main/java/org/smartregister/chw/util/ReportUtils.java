package org.smartregister.chw.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.webkit.WebViewAssetLoader;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.smartregister.chw.domain.GbvReportObject;
import org.smartregister.chw.domain.GeReportObject;
import org.smartregister.chw.domain.agyw_reports.AGYWReportObject;
import org.smartregister.chw.domain.cbhs_reports.CbhsMonthlyReportObject;
import org.smartregister.chw.domain.cdp_reports.CdpIssuingReportObject;
import org.smartregister.chw.domain.cdp_reports.CdpReceivingReportObject;
import org.smartregister.chw.domain.iccm_reports.IccmClientsReportObject;
import org.smartregister.chw.domain.iccm_reports.IccmDispensingSummaryReportObject;
import org.smartregister.chw.domain.iccm_reports.MalariaTestReportObject;
import org.smartregister.chw.domain.mother_champion_report.MotherChampionReportObject;
import org.smartregister.chw.domain.mvc_reports.MvcChildrenRegistrationSummaryReportObject;
import org.smartregister.chw.domain.mvc_reports.MvcHouseholdRegistrationSummaryReportObject;
import org.smartregister.chw.domain.mvc_reports.MvcServicesProvidedToChildrenReportObject;
import org.smartregister.chw.domain.mvc_reports.MvcServicesProvidedToHouseholdsReportObject;
import org.smartregister.chw.domain.sbc_reports.SbcReportObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class ReportUtils {
    private static final int year = Calendar.getInstance().get(Calendar.YEAR);
    private static final int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    public static String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
    private static String printJobName;
    private static String reportPeriod;

    public static String getDefaultReportPeriod() {
        String monthString = String.valueOf(month);
        if (month < 10) {
            monthString = "0" + monthString;
        }
        return monthString + "-" + year;
    }

    public static int getMonth() {
        return month;
    }

    public static int getYear() {
        return year;
    }

    public static String displayMonthAndYear(int month, int year) {
        return monthNames[month] + ", " + year;
    }

    public static String displayMonthAndYear() {
        return monthNames[getMonth() - 1] + ", " + getYear();
    }

    public static String getPrintJobName() {
        return printJobName;
    }

    public static void setPrintJobName(String printJobName) {
        ReportUtils.printJobName = printJobName;
    }

    public static Date getReportDate() {
        if (StringUtils.isNotBlank(reportPeriod)) {
            try {
                return new SimpleDateFormat("MM-yyyy", Locale.getDefault()).parse(reportPeriod);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        return new Date();
    }

    public static String getReportPeriod() {
        return reportPeriod;
    }

    public static void setReportPeriod(String reportPeriod) {
        ReportUtils.reportPeriod = reportPeriod;
    }

    public static void printTheWebPage(WebView webView, Context context) {

        // Creating  PrintManager instance
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(getPrintJobName());

        // Create a print job with name and adapter instance
        assert printManager != null;
        printManager.print(getPrintJobName(), printAdapter, new PrintAttributes.Builder().build());
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void loadReportView(String reportPath, WebView mWebView, Context context, String reportType) {

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder().addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context)).build();
        mWebView.setWebViewClient(new LocalContentWebViewClient(assetLoader));
        mWebView.addJavascriptInterface(new ChwWebAppInterface(context, reportType), "Android");

        if (reportType.equals(Constants.ReportConstants.ReportTypes.CONDOM_DISTRIBUTION_REPORT)) {
            mWebView.loadUrl("https://appassets.androidplatform.net/assets/reports/cdp_reports/" + reportPath + ".html");
        } else {
            mWebView.loadUrl("https://appassets.androidplatform.net/assets/reports/" + reportPath + ".html");
        }

    }


    public static class CBHSReport {
        public static String computeReport(Date now, Context context) {
            String report = "";
            CbhsMonthlyReportObject cbhsMonthlyReportObject = new CbhsMonthlyReportObject(now, context);
            try {
                report = cbhsMonthlyReportObject.getIndicatorDataAsGson(cbhsMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class MotherChampionReport {
        public static String computeReport(Date now) {
            String report = "";
            MotherChampionReportObject motherChampionReportObject = new MotherChampionReportObject(now);
            try {
                report = motherChampionReportObject.getIndicatorDataAsGson(motherChampionReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class CDPReports {
        public static String computeIssuingReports(Date startDate) {
            CdpIssuingReportObject cdpIssuingReportObject = new CdpIssuingReportObject(startDate);
            try {
                return cdpIssuingReportObject.getIndicatorDataAsGson(cdpIssuingReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeReceivingReports(Date now) {
            String report = "";
            CdpReceivingReportObject cdpReceivingReportObject = new CdpReceivingReportObject(now);
            try {
                report = cdpReceivingReportObject.getIndicatorDataAsGson(cdpReceivingReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class AGYWReport {
        public static String computeReport(Date now) {
            String report = "";
            AGYWReportObject agywReportObject = new AGYWReportObject(now);
            try {
                report = agywReportObject.getIndicatorDataAsGson(agywReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class ICCMReports {
        public static String computeClientsReports(Date startDate) {
            IccmClientsReportObject iccmClientsReportObject = new IccmClientsReportObject(startDate);
            try {
                return iccmClientsReportObject.getIndicatorDataAsGson(iccmClientsReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeDispensingSummaryReports(Date startDate) {
            IccmDispensingSummaryReportObject iccmDispensingSummaryReportObject = new IccmDispensingSummaryReportObject(startDate);
            try {
                return iccmDispensingSummaryReportObject.getIndicatorDataAsGson(iccmDispensingSummaryReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeMalariaTestsReports(Date startDate) {
            MalariaTestReportObject malariaTestReportObject = new MalariaTestReportObject(startDate);
            try {
                return malariaTestReportObject.getIndicatorDataAsGson(malariaTestReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }
    }


    public static class SbcReports {
        public static String computeClientsReports(Date startDate) {
            SbcReportObject sbcReportObject = new SbcReportObject(startDate);
            try {
                return sbcReportObject.getIndicatorDataAsGson(sbcReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }
    }


    public static class GbvReports {
        public static String computeClientsReports(Date startDate) {
            GbvReportObject gbvReportObject = new GbvReportObject(startDate);
            try {
                return gbvReportObject.getIndicatorDataAsGson(gbvReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }
    }


    public static class GeReports {
        public static String computeClientsReports(Date startDate) {
            GeReportObject geReportObject = new GeReportObject(startDate);
            try {
                return geReportObject.getIndicatorDataAsGson(geReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }
    }


    public static class MvcReports {
        public static String computeRegistrationSummaryReports(Date startDate) {
            MvcHouseholdRegistrationSummaryReportObject registrationSummaryReportObject = new MvcHouseholdRegistrationSummaryReportObject(startDate);
            try {
                return registrationSummaryReportObject.getIndicatorDataAsGson(registrationSummaryReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeChildrenRegistrationDetailsReports(Date startDate) {
            MvcChildrenRegistrationSummaryReportObject childrenRegistrationSummaryReportObject = new MvcChildrenRegistrationSummaryReportObject(startDate);
            try {
                return childrenRegistrationSummaryReportObject.getIndicatorDataAsGson(childrenRegistrationSummaryReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeServicesProvidedToHouseholdsReports(Date startDate) {
            MvcServicesProvidedToHouseholdsReportObject servicesProvidedToHouseholdsReportObject = new MvcServicesProvidedToHouseholdsReportObject(startDate);
            try {
                return servicesProvidedToHouseholdsReportObject.getIndicatorDataAsGson(servicesProvidedToHouseholdsReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeServicesProvidedToChildrenReports(Date startDate) {
            MvcServicesProvidedToChildrenReportObject servicesProvidedToChildrenReportObject = new MvcServicesProvidedToChildrenReportObject(startDate);
            try {
                return servicesProvidedToChildrenReportObject.getIndicatorDataAsGson(servicesProvidedToChildrenReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }
    }

}