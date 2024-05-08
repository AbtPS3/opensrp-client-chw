package org.smartregister.chw.interactor;

import org.smartregister.chw.ovc.contract.OvcProfileContract;
import org.smartregister.chw.ovc.domain.MemberObject;
import org.smartregister.chw.ovc.util.Constants;
import org.smartregister.chw.ovc.interactor.BaseOvcProfileInteractor;

public class OvcProfileInteractor extends BaseOvcProfileInteractor {

    @Override
    public void refreshProfileInfo(MemberObject memberObject, OvcProfileContract.InteractorCallBack callback) {
        Runnable runnable = () -> appExecutors.mainThread().execute(() -> {
            callback.refreshMedicalHistory(getVisit(Constants.EVENT_TYPE.OVC_FOLLOW_UP_VISIT, memberObject) != null);
        });
        appExecutors.diskIO().execute(runnable);
    }
}
