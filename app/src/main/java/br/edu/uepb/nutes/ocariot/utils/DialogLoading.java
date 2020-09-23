package br.edu.uepb.nutes.ocariot.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    private FragmentManager fragmentManager;

    /**
     * Empty constructor is required for DialogFragment
     */
    public DialogLoading() {
        // not implemented!
    }

    /**
     * Initializes the AlertDialog.
     *
     * @param id      The id
     * @param title   The id string of title
     * @param message The id string of message
     * @return The DialogFragment
     */
    public static DialogLoading newDialog(int id, String title, String message) {
        Bundle bundle = new Bundle();
        bundle.putInt(ID, id);
        bundle.putString(TITLE, title);
        bundle.putString(MESSAGE, message);

        DialogLoading dialogFragment = new DialogLoading();
        dialogFragment.setArguments(bundle);

        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container, false);
        TextView message = view.findViewById(R.id.loading_message);

        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(false);
        getDialog().setCancelable(false);
        getDialog().setTitle(getArguments().getString(TITLE));
        message.setText(getArguments().getString(MESSAGE));

        return view;
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
            super.show(fragmentManager, DIALOG_TAG);
        }
    }

    public void close() {
        if (fragmentManager == null) return;
        Fragment dialogFragment = fragmentManager.findFragmentByTag(DIALOG_TAG);

        if (dialogFragment != null && super.isVisible()) {
            super.dismissAllowingStateLoss();
        }
    }
}
