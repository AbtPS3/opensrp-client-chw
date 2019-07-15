package org.smartregister.chw.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.malaria.contract.MalariaRegisterFragmentContract;
import org.smartregister.chw.malaria.util.DBConstants;

public class MalariaRegisterFragmentPresenterTest extends BaseUnitTest {

    private MalariaRegisterFragmentPresenter presenter;

    @Mock
    private MalariaRegisterFragmentContract.View view;

    @Mock
    private MalariaRegisterFragmentContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new MalariaRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void testMainTable() {
        String table_name = "ec_malaria_confirmation";
        Assert.assertEquals(table_name, presenter.getMainTable());
    }

    @Test
    public void testMainCondition() {
        String main_condition = " " + DBConstants.TABLE_NAME + "." + DBConstants.KEY.DATE_REMOVED + " is null " + " COLLATE NOCASE WHERE ec_malaria_confirmation.malaria = 1";
        Assert.assertEquals(main_condition, presenter.getMainCondition());

    }

}
