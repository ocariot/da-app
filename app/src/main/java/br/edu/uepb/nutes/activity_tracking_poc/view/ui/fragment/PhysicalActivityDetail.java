package br.edu.uepb.nutes.activity_tracking_poc.view.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.data.model.Activity;
import butterknife.ButterKnife;

/**
 * A fragment to see the details of a physical activity.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityDetail extends Fragment {
    private final String LOG_TAG = "PhysicalActivityDetail";

    public static String ACTIVITY_DETAIL = "activity_detail";

    private Activity activity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhysicalActivityDetail() {

    }

    public static PhysicalActivityDetail newInstance() {
        PhysicalActivityDetail fragment = new PhysicalActivityDetail();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().size() != 0) {
            activity = (Activity) getArguments().getParcelable(ACTIVITY_DETAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physical_activity_detail, container, false);
        ButterKnife.bind(this, view);

        getActivity().setTitle(R.string.title_physical_activity);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.w(LOG_TAG, "ACTIVITY SELECTED: " + activity.toString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
