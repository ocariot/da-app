package br.edu.uepb.nutes.ocariot.utils;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import br.edu.uepb.nutes.ocariot.R;

/**
 * Dialog Loading.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class DialogLoading extends DialogFragment {
    private static final String DIALOG_TAG = "DialogLoading";
    private static final String ID = "id";
    private static final String MESSAGE = "message";
    private static final String TITLE = "title";
    private View view;
    private FragmentManager fragmentManager;

    /**
     * Empty constructor is required for DialogFragment
     */
    public DialogLoading() {
    }

    /**
     * Initializes the AlertDialog.
     *
     * @param id      The id
     * @param title   The id string of title
     * @param message The id string of message
     * @return The DialogFragment
     */
    public static DialogLoading newDialog(int id, int title, int message) {
        Bundle bundle = new Bundle();
        bundle.putInt(ID, id);
        bundle.putInt(TITLE, title);
        bundle.putInt(MESSAGE, message);

        DialogLoading dialogFragment = new DialogLoading();
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_fragment, container, false);
        TextView message = view.findViewById(R.id.loading_message);

        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        getDialog().setTitle(Objects.requireNonNull(getArguments()).getInt(TITLE));
        message.setText(getArguments().getInt(MESSAGE));

        return view;
    }

    public void setMessage(@StringRes int id) {
        if (view == null) return;
        TextView message = view.findViewById(R.id.loading_message);
        message.setText(id);
    }

    /**
     * Show Dialog
     *
     * @param fragmentManager The FragmentManager
     */
    public void show(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        Fragment dialogFragment = fragmentManager.findFragmentByTag(DIALOG_TAG);
        if (dialogFragment == null) {
            this.show(fragmentManager, DIALOG_TAG);
        }
    }

    public void close() {
        if(fragmentManager == null) return;
        Fragment dialogFragment = fragmentManager.findFragmentByTag(DIALOG_TAG);

        if (dialogFragment != null && this.isVisible()) {
            this.dismiss();
        }
    }
}
