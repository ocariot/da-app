package br.edu.uepb.nutes.ocariot.view.ui.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.User;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.utils.DateUtils;
import br.edu.uepb.nutes.ocariot.view.ui.activity.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class WelcomeFragment extends Fragment {
    @BindView(R.id.fitbit_button)
    AppCompatButton mFitBitButton;

    @BindView(R.id.do_not_login_fitbit_button)
    AppCompatButton mDoNotLoginFitBitButton;

    @BindView(R.id.instructions_tv)
    TextView mInstructions;

    @BindView(R.id.child_username_tv)
    TextView mChildUsername;

    @BindView(R.id.last_sync_tv)
    TextView mChildLastSync;

    private Child child;
    private OnClickWelcomeListener mListener;
    private AppPreferencesHelper mAppPref;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppPref = AppPreferencesHelper.getInstance();
        child = mAppPref.getLastSelectedChild();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initComponents();

        if (mAppPref.getUserAccessOcariot().getSubjectType().equalsIgnoreCase(User.Type.CHILD)) {
            mChildUsername.setText(child.getUsername());
            mInstructions.setText(R.string.message_welcome_child);
        } else {
            mChildUsername.setText(getResources().getString(
                    R.string.message_selected_child,
                    child.getUsername())
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mInstructions.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }

        if (child.getLastSync() == null) {
            mChildLastSync.setVisibility(View.GONE);
        } else {
            mChildLastSync.setText(getResources().getString(
                    R.string.last_sync_date_time,
                    DateUtils.convertDateTimeUTCToLocale(child.getLastSync(),
                            getResources().getString(R.string.date_time_abb5), null)
            ));
            mChildLastSync.setVisibility(View.VISIBLE);
        }

        mFitBitButton.setOnClickListener(view -> {
            if (mListener != null) mListener.onClickFitBit();
        });

        mDoNotLoginFitBitButton.setOnClickListener(v -> {
            if (mListener != null) mListener.onDoNotLoginFitBitClick();
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClickWelcomeListener) {
            mListener = (OnClickWelcomeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClickWelcomeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        initToolBar();
    }

    private void initToolBar() {
        ActionBar mActionBar = ((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        Objects.requireNonNull(mActionBar).setTitle(R.string.app_name);
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    public interface OnClickWelcomeListener {
        void onClickFitBit();

        void onDoNotLoginFitBitClick();
    }
}
