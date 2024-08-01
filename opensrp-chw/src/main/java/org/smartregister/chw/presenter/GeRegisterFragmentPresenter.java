package org.smartregister.chw.presenter;

import org.smartregister.chw.contract.GeRegisterFragmentContract;
import org.smartregister.chw.fragment.GeRegisterFragment;
import org.smartregister.configurableviews.model.View;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.Set;
import java.util.TreeSet;

public class GeRegisterFragmentPresenter implements BaseRegisterFragmentContract.Presenter {
    protected Set<View> visibleColumns = new TreeSet();
    private GeRegisterFragmentContract.Model model;
    private BaseRegisterFragmentContract.View view;

    public GeRegisterFragmentPresenter(BaseRegisterFragmentContract.View view, GeRegisterFragmentContract.Model model) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void processViewConfigurations() {

    }

    public String getMainCondition() {
        return model.mainCondition();
    }

    public String getDefaultSortQuery() {
        return model.defaultSortQuery();
    }

    @Override
    public void initializeQueries(String mainCondition) {
        view.initializeQueryParams(
                model.getTableName(),
                model.countSelect(mainCondition),
                model.mainSelect(mainCondition));


        ((GeRegisterFragment) view).initializeAdapter();
        view.countExecute();
        view.filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }

    public String getTableName(){
        return model.getTableName();
    }
}
