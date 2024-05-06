package org.smartregister.chw.fragment;

import org.smartregister.chw.activity.GbvMemberProfileActivity;
import org.smartregister.chw.core.fragment.CoreGbvRegisterFragment;
import org.smartregister.chw.core.fragment.CoreOvcRegisterFragment;
import org.smartregister.chw.gbv.presenter.BaseGbvRegisterFragmentPresenter;
import org.smartregister.chw.model.GbvRegisterFragmentModel;
import org.smartregister.chw.model.OvcRegisterFragmentModel;
import org.smartregister.chw.ovc.presenter.BaseOvcRegisterFragmentPresenter;

public class OvcRegisterFragment extends CoreOvcRegisterFragment {
    @Override
    protected void openProfile(String baseEntityId) {
        GbvMemberProfileActivity.startMe(getActivity(), baseEntityId);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new BaseOvcRegisterFragmentPresenter(this, new OvcRegisterFragmentModel(), null);
    }

}
