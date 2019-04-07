package br.edu.uepb.nutes.ocariot.view.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.view.ui.activity.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeFragment extends Fragment {

    private OnClickWelcomeListener mListener;

    @BindView(R.id.fitbit_button)
    AppCompatButton mFitBitButton;

    @BindView(R.id.do_not_login_fitbit_button)
    AppCompatButton mDoNotLoginFitBitButton;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
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
        ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
        mActionBar.setTitle(R.string.app_name);
        mActionBar.setDisplayHomeAsUpEnabled(false);
    }

    public interface OnClickWelcomeListener {
        void onClickFitBit();

        void onDoNotLoginFitBitClick();
    }
}
