package org.smartregister.chw.fragment;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import org.smartregister.chw.R;
import org.smartregister.chw.activity.GeProfileActivity;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.model.GeRegisterFragmentModel;
import org.smartregister.chw.presenter.GeRegisterFragmentPresenter;
import org.smartregister.chw.provider.OpdRegisterProvider;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

public class GeRegisterFragment extends BaseRegisterFragment {

    public void initializeAdapter() {
        OpdRegisterProvider childRegisterProvider = new OpdRegisterProvider(getActivity(), registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        // Update top left icon
        View qrCodeView = view.findViewById(R.id.scanQrCode);
        qrCodeView.setVisibility(View.GONE);

        // Update title name
        view.findViewById(R.id.opensrp_logo_image_view).setVisibility(View.GONE);
        CustomFontTextView title = view.findViewById(R.id.txt_title_label);
        title.setVisibility(View.VISIBLE);
        title.setText(R.string.menu_ge);

        Toolbar toolbar = view.findViewById(R.id.register_toolbar);

        //Obtain an instance of the Navigation menu within our activity
        NavigationMenu.getInstance(getActivity(), view, toolbar);

    }

    @Override
    protected void initializePresenter() {
        presenter = new GeRegisterFragmentPresenter(GeRegisterFragment.this, new GeRegisterFragmentModel());
    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return ((GeRegisterFragmentPresenter) presenter).getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return ((GeRegisterFragmentPresenter) presenter).getDefaultSortQuery();
    }


    @Override
    public String getTablename() {
        return ((GeRegisterFragmentPresenter) presenter).getTableName();
    }

    @Override
    protected void startRegistration() {

    }

    //This method is called once an item in the register list is clicked
    @Override
    protected void onViewClicked(View view) {
        CommonPersonObjectClient client =
                (CommonPersonObjectClient) view.getTag(R.id.VIEW_CLIENT);

        Intent intent = new Intent(getActivity(), GeProfileActivity.class);
        intent.putExtra("client_details",client);
        startActivity(intent);
    }

    @Override
    public void showNotFoundPopup(String s) {

    }
}
