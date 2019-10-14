package br.edu.uepb.nutes.ocariot.view.ui.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import br.edu.uepb.nutes.ocariot.R;
import br.edu.uepb.nutes.ocariot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.ocariot.view.ui.activity.LoginActivity;

/**
 * SettingsFragment implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsSimpleFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private Context mContext;

    public SettingsSimpleFragment() {
        // Empty constructor required!
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsSimpleFragment newInstance() {
        return new SettingsSimpleFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_simple);

        mContext = getActivity();

        // Sign Out
        findPreference(getString(R.string.key_signout)).setOnPreferenceClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_signout))) {
            openDialogSignOut();
            return true;
        }
        return false;
    }

    /**
     * Show dialog confirm sign out in app.
     */
    private void openDialogSignOut() {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
            mDialog.setMessage(R.string.dialog_confirm_sign_out)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                if (AppPreferencesHelper.getInstance(mContext).removeSession()) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                    ).setNegativeButton(android.R.string.no, null)
                    .create().show();
        });
    }
}
