package com.apppubs.ui.activity;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.apppubs.d20.R;
import com.apppubs.ui.fragment.ServiceNOArticlesFragment;

public class ServiceNOArticlesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_common_container);
        setNeedTitleBar(false);
        ServiceNOArticlesFragment frg = new ServiceNOArticlesFragment();
        frg.setNeedBack(true);
        frg.setTitle(getString(R.string.service_no));
        frg.setArguments(getIntent().getExtras());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container_fg, frg);
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.commit();
    }

}
