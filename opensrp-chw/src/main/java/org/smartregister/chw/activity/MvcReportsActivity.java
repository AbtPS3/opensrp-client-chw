package org.smartregister.chw.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.smartregister.chw.R;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.ReportUtils;
import org.smartregister.view.activity.SecuredActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class MvcReportsActivity extends SecuredActivity implements View.OnClickListener {
    protected ConstraintLayout mvcRegistrationSummaryReport;

    protected ConstraintLayout mvcChildrenRegistrationReport;

    protected ConstraintLayout mvcServicesProvidedToHouseholdsReport;

    protected ConstraintLayout mvcServicesProvidedToChildrenReport;

    protected AppBarLayout appBarLayout;

    Menu menu;

    private String reportPeriod = ReportUtils.getDefaultReportPeriod();

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_mvc_reports);
        setUpToolbar();
        setupViews();
    }

    public void setupViews() {
        mvcRegistrationSummaryReport = findViewById(R.id.mvc_registration_summary);

        mvcChildrenRegistrationReport = findViewById(R.id.mvc_child_registration_details);

        mvcServicesProvidedToHouseholdsReport = findViewById(R.id.mvc_services_provided_to_households);

        mvcServicesProvidedToChildrenReport = findViewById(R.id.mvc_services_provided_to_children);

        mvcRegistrationSummaryReport.setOnClickListener(this);

        mvcChildrenRegistrationReport.setOnClickListener(this);

        mvcServicesProvidedToHouseholdsReport.setOnClickListener(this);

        mvcServicesProvidedToChildrenReport.setOnClickListener(this);
    }

    public void setUpToolbar() {
        Toolbar toolbar = findViewById(org.smartregister.chw.core.R.id.back_to_nav_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(org.smartregister.chw.core.R.drawable.ic_arrow_back_white_24dp);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        appBarLayout = findViewById(org.smartregister.chw.core.R.id.app_bar);
        appBarLayout.setOutlineProvider(null);
    }

    @Override
    protected void onResumption() {
        //overridden
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reports_menu, menu);
        this.menu = menu;
        this.menu.findItem(R.id.action_select_month).setTitle(ReportUtils.displayMonthAndYear());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_select_month) {
            showMonthPicker(this, menu);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.mvc_registration_summary) {
            MvcReportsViewActivity.startMe(this, Constants.ReportConstants.ReportPaths.MVC_REGISTRATION_SUMMARY_REPORT, R.string.mvc_registrations_summary_title, reportPeriod);
        } else if (id == R.id.mvc_child_registration_details) {
            MvcReportsViewActivity.startMe(this, Constants.ReportConstants.ReportPaths.MVC_CHILDREN_REGISTRATION_DETAILS_REPORT, R.string.mvc_children_registration_details_title, reportPeriod);
        } else if (id == R.id.mvc_services_provided_to_households) {
            MvcReportsViewActivity.startMe(this, Constants.ReportConstants.ReportPaths.MVC_SERVICES_PROVIDED_TO_HOUSEHOLDS_REPORT, R.string.mvc_services_provided_to_the_households_title, reportPeriod);
        } else if (id == R.id.mvc_services_provided_to_children) {
            MvcReportsViewActivity.startMe(this, Constants.ReportConstants.ReportPaths.MVC_SERVICES_PROVIDED_TO_CHILDREN_REPORT, R.string.mvc_services_provided_to_the_children_title, reportPeriod);
        } else {
            Toast.makeText(this, "Action Not Defined", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMonthPicker(Context context, Menu menu) {
        //shows the month picker and returns selected period and updated the menu
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(context, (selectedMonth, selectedYear) -> {
            int month = selectedMonth + 1;
            String monthString = String.valueOf(month);
            if (month < 10) {
                monthString = "0" + monthString;
            }
            String yearString = String.valueOf(selectedYear);
            reportPeriod = monthString + "-" + yearString;
            menu.findItem(R.id.action_select_month).setTitle(ReportUtils.displayMonthAndYear(selectedMonth, selectedYear));

        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));
        try {
            Date reportDate = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).parse(reportPeriod);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(reportDate);
            builder.setActivatedMonth(calendar.get(Calendar.MONTH));
            builder.setMinYear(2021);
            builder.setActivatedYear(calendar.get(Calendar.YEAR));
            builder.setMaxYear(Calendar.getInstance().get(Calendar.YEAR));
            builder.setMinMonth(Calendar.JANUARY);
            builder.setMaxMonth(Calendar.DECEMBER);
            builder.setTitle("Select Month 0");
            builder.build().show();
        } catch (ParseException e) {
            Timber.e(e);
        }
    }
}