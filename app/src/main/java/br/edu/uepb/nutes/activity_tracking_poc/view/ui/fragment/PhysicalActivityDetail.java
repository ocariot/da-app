package br.edu.uepb.nutes.activity_tracking_poc.view.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.uepb.nutes.activity_tracking_poc.R;
import br.edu.uepb.nutes.activity_tracking_poc.data.repository.remote.fitbit.FitBitNetRepository;
import butterknife.ButterKnife;

/**
 * A fragment to see the details of a physical activity.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class PhysicalActivityDetail extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_physical_activity_list, container, false);
        ButterKnife.bind(this, view);

        getActivity().setTitle(R.string.title_physical_activity);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
