package br.edu.uepb.nutes.activity_tracking_poc.view.ui.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.StringRes;
import android.util.Log;

import br.edu.uepb.nutes.activity_tracking_poc.R;

/**
 * SettingsFragment implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES/UEPB
 */
public class SettingsFragment extends PreferenceFragment {
    public final String LOG_TAG = "SettingsFragment";
    private OnClickSettingsListener mListener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // FitBit
        Preference prefFitbit = findPreference(getString(R.string.key_fitibit));
        prefFitbit.setOnPreferenceClickListener(preference -> {
            mListener.onClickItemPref(prefFitbit, R.string.key_fitibit);

            SwitchPreference switchPreference = (SwitchPreference) prefFitbit;
            switchPreference.setChecked(!switchPreference.isChecked());
            return true;
        });

        // Sign Out
        Preference prefSignout = findPreference(getString(R.string.key_signout));
        prefSignout.setOnPreferenceClickListener(preference -> {
            mListener.onClickItemPref(prefSignout, R.string.key_signout);
            return true;
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnClickSettingsListener) {
            mListener = (OnClickSettingsListener) context;
        } else {
            throw new ClassCastException("You must implement the OnClickSettingsListener!");
        }
    }

    public interface OnClickSettingsListener {
        void onClickItemPref(Preference preference, @StringRes int key);
    }
}
