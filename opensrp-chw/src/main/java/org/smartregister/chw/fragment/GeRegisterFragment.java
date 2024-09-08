package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.GeProfileActivity;
import org.smartregister.chw.core.fragment.CoreGeRegisterFragment;
import org.smartregister.chw.ge.presenter.BaseGeRegisterFragmentPresenter;
import org.smartregister.chw.model.GeRegisterFragmentModel;

public class GeRegisterFragment extends CoreGeRegisterFragment {
    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new BaseGeRegisterFragmentPresenter(this, new GeRegisterFragmentModel(), null);
    }

    @Override
    protected void openProfile(String baseEntityId) {
        GeProfileActivity.startMe(getActivity(), baseEntityId);
    }
}
