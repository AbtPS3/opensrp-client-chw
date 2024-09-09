package org.smartregister.chw.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.GeMobilizationSessionDetailsActivity;
import org.smartregister.chw.ge.dao.GeDao;

import java.util.List;

import timber.log.Timber;

public class GeMobilizationRegisterAdapter extends RecyclerView.Adapter<GeMobilizationRegisterAdapter.GeMobilizationViewHolder> {
    private static final StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

    private final Context context;

    private final List<GeDao.GeMobilization> geSessionModels;


    public GeMobilizationRegisterAdapter(List<GeDao.GeMobilization> geMobilizations, Context context) {
        this.geSessionModels = geMobilizations;
        this.context = context;
    }

    private static void evaluateView(TextView tv, Context context, String stringValue) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String[] stringValueArray;
        if (stringValue.contains(",")) {
            stringValueArray = stringValue.substring(1, stringValue.length() - 1).split(",");
            for (String value : stringValueArray) {
                spannableStringBuilder.append(getStringResource(context, "ge_", value.trim()) + "\n", new BulletSpan(10), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (stringValue.charAt(0) == '[' && stringValue.charAt(stringValue.length() - 1) == ']') {
            spannableStringBuilder.append(getStringResource(context, "ge_", stringValue.substring(1, stringValue.length() - 1))).append("\n");
        } else {
            spannableStringBuilder.append(getStringResource(context, "ge_", stringValue));
        }
        tv.setText(spannableStringBuilder);
    }

    private static String getStringResource(Context context, String prefix, String resourceName) {
        int resourceId = context.getResources().
                getIdentifier(prefix + resourceName.trim(), "string", context.getPackageName());
        try {
            return context.getString(resourceId);
        } catch (Exception e) {
            Timber.e(e);
            return resourceName;
        }
    }

    @NonNull
    @Override
    public GeMobilizationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View followupLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ge_mobilization_session_card_view, viewGroup, false);
        return new GeMobilizationViewHolder(followupLayout, context);
    }

    @Override
    public void onBindViewHolder(@NonNull GeMobilizationViewHolder holder, int position) {
        GeDao.GeMobilization geMobilizations = geSessionModels.get(position);
        holder.bindData(geMobilizations);
    }

    @Override
    public int getItemCount() {
        return geSessionModels.size();
    }

    protected static class GeMobilizationViewHolder extends RecyclerView.ViewHolder {
        public TextView session_start_date;
        public TextView session_end_date;
        public TextView event_supporter;
        public TextView event_type;

        private Context context;

        public GeMobilizationViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
        }

        public void bindData(GeDao.GeMobilization geMobilization) {
            session_start_date = itemView.findViewById(R.id.session_start_date);
            session_end_date = itemView.findViewById(R.id.session_end_date);
            event_supporter = itemView.findViewById(R.id.event_supporter);
            event_type = itemView.findViewById(R.id.event_type);

            session_start_date.setText(context.getString(R.string.ge_session_start_date, geMobilization.getEventStartDate()));
            session_end_date.setText(context.getString(R.string.ge_session_end_date, geMobilization.getEventEndDate()));
            event_supporter.setText(context.getString(R.string.ge_event_supporter, getStringResource(context, "ge_", geMobilization.getEventSupporter())));
            event_type.setText(geMobilization.getMobilizationEventType());

            evaluateView(event_type, context, geMobilization.getMobilizationEventType());
            itemView.setOnClickListener(view -> GeMobilizationSessionDetailsActivity.startMe(((Activity) context), geMobilization.getBaseEntityID()));
        }
    }
}
