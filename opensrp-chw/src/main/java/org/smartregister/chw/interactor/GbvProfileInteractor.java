package org.smartregister.chw.interactor;

import org.smartregister.chw.gbv.contract.GbvProfileContract;
import org.smartregister.chw.gbv.domain.MemberObject;
import org.smartregister.chw.gbv.interactor.BaseGbvProfileInteractor;
import org.smartregister.chw.gbv.util.Constants;

public class GbvProfileInteractor extends BaseGbvProfileInteractor {

    @Override
    public void refreshProfileInfo(MemberObject memberObject, GbvProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshMedicalHistory(getVisit(Constants.EVENT_TYPE.GBV_HOME_VISIT, memberObject) != null);
        });
        appExecutors.diskIO().execute(runnable);
    }
}
