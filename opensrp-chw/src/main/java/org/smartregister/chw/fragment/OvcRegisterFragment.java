package org.smartregister.chw.fragment;

import android.content.Intent;

import org.smartregister.chw.activity.MvcFamilyProfileActivity;
import org.smartregister.chw.core.fragment.CoreOvcRegisterFragment;
import org.smartregister.chw.core.provider.CoreRegisterProvider;
import org.smartregister.chw.model.OvcRegisterFragmentModel;
import org.smartregister.chw.ovc.presenter.BaseOvcRegisterFragmentPresenter;
import org.smartregister.chw.provider.FamilyRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.util.Set;

public class OvcRegisterFragment extends CoreOvcRegisterFragment {
    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        CoreRegisterProvider chwRegisterProvider = new FamilyRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, chwRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new BaseOvcRegisterFragmentPresenter(this, new OvcRegisterFragmentModel(), null);
    }


    @Override
    protected void onViewClicked(android.view.View view) {

        if (getActivity() == null) {
            return;
        }

        if (view.getTag() != null && view.getTag(org.smartregister.family.R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
            goToPatientDetailActivity((CommonPersonObjectClient) view.getTag(), false);
        }
    }

    protected void goToPatientDetailActivity(CommonPersonObjectClient patient,
                                             boolean goToDuePage) {
        Intent intent = new Intent(getActivity(), MvcFamilyProfileActivity.class);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false));
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.FAMILY_HEAD, false));
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.PRIMARY_CAREGIVER, false));
        intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, false));
        intent.putExtra(Constants.INTENT_KEY.FAMILY_NAME, Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.FIRST_NAME, false));
        intent.putExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, goToDuePage);

        startActivity(intent);
    }

}
