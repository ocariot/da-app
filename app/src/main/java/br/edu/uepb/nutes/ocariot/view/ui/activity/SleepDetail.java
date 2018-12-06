package br.edu.uepb.nutes.ocariot.view.ui.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.Sleep;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity Sleep detail.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SleepDetail extends AppCompatActivity {
    private final String LOG_TAG = "PhysicalActivityDetail";

    public static String SLEEP_DETAIL = "sleep_detail";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Sleep sleep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if (getIntent() != null) {
            sleep = getIntent().getParcelableExtra(SLEEP_DETAIL);
        }
        initComponents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        if (sleep == null) return;

        initToobar();
        populateView(sleep);
    }

    private void populateView(Sleep sleepDetail) {
    }

    private void initToobar() {
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar == null) return;

        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);

        if (sleep.getStartTime() != null)
            mActionBar.setTitle(DateUtils.formatDateISO8601(sleep.getStartTime(),
                    getString(R.string.date_time_abb4), null));
    }
}
