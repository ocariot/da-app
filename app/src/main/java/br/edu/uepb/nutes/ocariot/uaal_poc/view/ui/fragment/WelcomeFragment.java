package br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import br.edu.uepb.nutes.ocariot.uaal_poc.R;
import br.edu.uepb.nutes.ocariot.uaal_poc.view.ui.activity.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeFragment extends Fragment {

    private OnClickWelcomeListener mListener;

    @BindView(R.id.fitbit_button)
    AppCompatButton mFitBitButton;

    public WelcomeFragment() {
        // Required empty public constructor
    }

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mFitBitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) mListener.onClickFitBit();
            }
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
    }
}
