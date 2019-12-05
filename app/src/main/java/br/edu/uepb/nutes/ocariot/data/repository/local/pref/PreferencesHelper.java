package br.edu.uepb.nutes.ocariot.data.repository.local.pref;

import br.edu.uepb.nutes.ocariot.data.model.common.UserAccess;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.Child;
import br.edu.uepb.nutes.ocariot.data.model.ocariot.FitBitAppData;

/**
 * Interface for Preferences Helper.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface PreferencesHelper {

    boolean addOcariotURL(final String utl);

    boolean changedOcariotUrl(final boolean value);

    boolean addUserAccessOcariot(final UserAccess userAccess);

    boolean addLastSelectedChild(final Child user);

    boolean addFitbitAppData(final FitBitAppData fitBitAppData);

    boolean addBoolean(String key, boolean value);

    boolean addInt(String key, int value);

    String getOcariotURL();

    boolean changedOcariotUrl();

    UserAccess getUserAccessOcariot();

    Child getLastSelectedChild();

    FitBitAppData getFitbitAppData();

    boolean getBoolean(String key);

    int getInt(String key);

    boolean removeSession();
}
