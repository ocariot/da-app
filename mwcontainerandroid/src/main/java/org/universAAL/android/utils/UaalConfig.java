package org.universAAL.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Properties;

import org.universAAL.android.R;
import org.universAAL.android.services.MiddlewareService;
import org.universAAL.middleware.util.Constants;
import org.universAAL.ontology.profile.AssistedPerson;
import org.universAAL.ontology.profile.Caregiver;
import org.universAAL.ontology.profile.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This class holds a centralized way to access settings from everywhere in the
 * app. It is done this way for a couple of reasons: Settings are stored in
 * Preferences, which cannot be accessed from those parts of the app that do not
 * have access to an Android Context. Also, certain configurations have to be
 * accessed from multiple points in the app, so it is better to have them
 * centralized here rather than have them in one class and let others access it
 * (like before when most were in MiddlewareService). Finally, it may improve
 * slightly access to Preferences, since getDefaultSharedPreferences accesses to
 * a file, and the SharedPreferences instance is unique. <br>
 * Of course this also means that, working as a singleton and dealing with
 * settings that can change at any time, there has to be some kind of
 * synchronization - a moment when the in-memory settings of this class are
 * updated to those in the Preferences file. The method load(Context ctxt) is
 * used for this, which is more manageable than using listeners.
 *
 * @author alfiva
 */
public class UaalConfig {
    private static final String TAG = "UaalConfig";
    private static String LOCAL_STORAGE_PATH = "";
    private static String EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath();

    private static String mServerURL = AppConstants.Defaults.CONNURL;
    private static String mServerUSR = AppConstants.Defaults.CONNUSR;
    private static String mServerPWD = AppConstants.Defaults.CONNPWD;
    private static String mServerGCM = AppConstants.Defaults.CONNGCM;
    private static String mConfigFolder = AppConstants.Defaults.CFOLDER;
    private static String mUAALUser = AppConstants.Defaults.USER;
    private static int mSettingRemoteType = AppConstants.Defaults.CONNTYPE;
    private static int mSettingRemoteMode = AppConstants.Defaults.CONNMODE;
    private static boolean mSettingWifiEnabled = AppConstants.Defaults.CONNWIFI;
    private static boolean mServiceCoord = AppConstants.Defaults.ISCOORD;
    private static boolean mUIHandler = AppConstants.Defaults.UIHANDLER;
    private static String mHomeWifi = AppConstants.NO_WIFI;

    /**
     * Synchronize the UaalConfig utility class with the actual saved preferences.
     * This must be called when initializing or after a change has happened in
     * the Preferences. Since changes in Preferences are most relevant during MW
     * initialization, calling this method should match restarts of the MW
     * service too.
     *
     * @param ctxt Android Context to have access to the Preferences
     */
    public static void load(Context ctxt) {
        LOCAL_STORAGE_PATH = ctxt.getFilesDir().getPath();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ctxt);
        mServerURL = prefs.getString(AppConstants.Keys.CONNURL, AppConstants.Defaults.CONNURL);
        mServerUSR = prefs.getString(AppConstants.Keys.CONNUSR, AppConstants.Defaults.CONNUSR);
        mServerPWD = prefs.getString(AppConstants.Keys.CONNPWD, AppConstants.Defaults.CONNPWD);
        mServiceCoord = prefs.getBoolean(AppConstants.Keys.ISCOORD, AppConstants.Defaults.ISCOORD);
        mUIHandler = prefs.getBoolean(AppConstants.Keys.UIHANDLER, AppConstants.Defaults.UIHANDLER);
        mSettingWifiEnabled = prefs.getBoolean(AppConstants.Keys.CONNWIFI, AppConstants.Defaults.CONNWIFI);
        mSettingRemoteMode = Integer.parseInt(prefs.getString(AppConstants.Keys.CONNMODE, Integer.toString(AppConstants.Defaults.CONNMODE)));
        mSettingRemoteType = Integer.parseInt(prefs.getString(AppConstants.Keys.CONNTYPE, Integer.toString(AppConstants.Defaults.CONNTYPE)));
        mConfigFolder = prefs.getString(AppConstants.Keys.CFOLDER, AppConstants.Defaults.CFOLDER);
        mUAALUser = prefs.getString(AppConstants.Keys.USER, AppConstants.Defaults.USER);
        mHomeWifi = prefs.getString(AppConstants.Keys.MYWIFI, AppConstants.NO_WIFI);
    }

    public static void setmServerURL(Context ctxt, String mServerURL) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putString(AppConstants.Keys.CONNURL, mServerURL)
                .apply();
        UaalConfig.mServerURL = mServerURL;
    }

    public static void setmServerUSR(Context ctxt, String mServerUSR) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putString(AppConstants.Keys.CONNUSR, mServerUSR)
                .apply();
        UaalConfig.mServerUSR = mServerUSR;
    }

    public static void setmServerPWD(Context ctxt, String mServerPWD) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putString(AppConstants.Keys.CONNPWD, mServerPWD)
                .apply();
        UaalConfig.mServerPWD = mServerPWD;
    }

    public static void setmServerGCM(Context ctxt, String mServerGCM) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putString(AppConstants.Keys.CONNGCM, mServerGCM)
                .apply();

        UaalConfig.mServerGCM = mServerGCM;
    }

    public static void setmConfigFolder(Context ctxt, String mConfigFolder) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putString(AppConstants.Keys.CFOLDER, mConfigFolder)
                .apply();
        UaalConfig.mConfigFolder = mConfigFolder;
    }

    public static void setmUAALUser(Context ctxt, String mUAALUser) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putString(AppConstants.Keys.USER, mUAALUser)
                .apply();
        UaalConfig.mUAALUser = mUAALUser;
    }

    public static void setmSettingRemoteType(Context ctxt, int mSettingRemoteType) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putString(AppConstants.Keys.CONNTYPE, Integer.toString(mSettingRemoteType))
                .apply();
        UaalConfig.mSettingRemoteType = mSettingRemoteType;
    }

    public static void setmSettingRemoteMode(Context ctxt, int mSettingRemoteMode) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putString(AppConstants.Keys.CONNMODE, Integer.toString(mSettingRemoteMode))
                .apply();
        UaalConfig.mSettingRemoteMode = mSettingRemoteMode;
    }

    public static void setmSettingWifiEnabled(Context ctxt, boolean mSettingWifiEnabled) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putBoolean(AppConstants.Keys.CONNWIFI, mSettingWifiEnabled)
                .apply();
        UaalConfig.mSettingWifiEnabled = mSettingWifiEnabled;
    }

    public static void setmServiceCoord(Context ctxt, boolean mServiceCoord) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putBoolean(AppConstants.Keys.ISCOORD, mServiceCoord)
                .apply();
        UaalConfig.mServiceCoord = mServiceCoord;
    }

    public static void setmUIHandler(Context ctxt, boolean mUIHandler) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putBoolean(AppConstants.Keys.UIHANDLER, mUIHandler)
                .apply();
        UaalConfig.mUIHandler = mUIHandler;
    }

    public static void setmHomeWifi(Context ctxt, String mHomeWifi) {
        PreferenceManager.getDefaultSharedPreferences(ctxt)
                .edit()
                .putString(AppConstants.Keys.MYWIFI, mHomeWifi)
                .apply();
        UaalConfig.mHomeWifi = mHomeWifi;
    }

    public static String printUaalConfig()
    {
        return String.format(
                "Connection URL: %s\n" +
                "Connection User: %s\n" +
                "Connection Password: %s\n" +
                "Is Coordinator: %b\n" +
                "UI Handler: %b\n" +
                "Wifi Enabled: %b\n" +
                "Connection Mode: %d\n" +
                "Connection Type: %d\n" +
                "Configuration folder: %s\n" +
                "UAAL user: %s\n" +
                "Home Wifi: %s\n",
                mServerURL, mServerUSR, mServerPWD,
                mServiceCoord, mUIHandler,  mSettingWifiEnabled,
                mSettingRemoteMode, mSettingRemoteType, mConfigFolder,
                mUAALUser, mHomeWifi);
    }

    /**
     * Get the URL/IP of the server to establish the remote connection
     *
     * @return URL of the server
     */
    public static String getServerURL() {
        return mServerURL;
    }

    /**
     * Get the identification of this local node for the authentication of
     * remote connection when using Remote API
     *
     * @return the id to use in the user authentication
     */
    public static String getServerUSR() {
        return mServerUSR;
    }

    /**
     * Get the password for the authentication of remote connection when using
     * Remote API
     *
     * @return the password
     */
    public static String getServerPWD() {
        return mServerPWD;
    }

    public static String getmServerGCM() {
        return mServerGCM;
    }

    /**
     * Check if this instance of the MW should run as Bus Service Coordinator
     *
     * @return true if it is set as coordinator
     */
    public static boolean isServiceCoord() {
        return mServiceCoord;
    }

    /**
     * Check if this application should work as a UI Handler
     *
     * @return true if it is set as UI Handler
     */
    public static boolean isUIHandler() {
        return mUIHandler;
    }

    /**
     * Get the remote connection mode
     *
     * @return One of the REMOTE_MODE_* constants
     */
    public static int getRemoteMode() {
        return mSettingRemoteMode;
    }

    /**
     * Get the remote connection type
     *
     * @return One of the REMOTE_TYPE_* constants
     */
    public static int getRemoteType() {
        return mSettingRemoteType;
    }

    /**
     * Check if WiFi discovery has been enabled
     *
     * @return true if it is enabled
     */
    public static boolean isWifiAllowed() {
        return mSettingWifiEnabled;
    }

    /**
     * Get the folder where the configuration files for uAAL modules are placed.
     *
     * @return path to the configuration folder
     */
    public static String getConfigDir() {
        return mConfigFolder;
    }

    /**
     * Get the suffix to be used to create the User URI to be used in UI
     * framework, which represents the User resource handling uAAL.
     *
     * @return the user id suffix of the User URI
     */
    public static String getUAALUser() {
        return mUAALUser;
    }

    public static String getmHomeWifi() {
        return mHomeWifi;
    }

    /**
     * Gets the properties of a property file located in the config folder.
     *
     * @param file The name of the file (without path nor extension).
     * @return The Properties
     */
    public static Dictionary getProperties(String file) {
        Properties prop = new Properties();
        try {
            Log.e(TAG, " Loading Uaal properties file in folder:"
                    + EXTERNAL_STORAGE_PATH
                    + getConfigDir() + file + ".properties");
            File conf = new File(EXTERNAL_STORAGE_PATH
                    , getConfigDir() + file + ".properties");

//			Log.e(TAG, " Loading Uaal properties file in folder:"
//					+ Environment.getExternalStorageDirectory()
//					.getPath() + getConfigDir() + file + ".properties");
//			File conf = new File(Environment.getExternalStorageDirectory()
//					.getPath(), getConfigDir() + file + ".properties");
            InputStream in = new FileInputStream(conf);
            prop.load(in);
            in.close();
        } catch (java.io.FileNotFoundException e) {
            Log.w(TAG, "Properties file does not exist: "
                    + file);
        } catch (IOException e) {
            Log.w(TAG, "Error reading props file: " + file);
        }
        return prop;
    }

    /**
     * Creates all the configuration files needed for running uAAL if they dont
     * exist already.
     *
     * @param ctxt Application context
     */
    public static void createFiles(Context ctxt) {
        String basepath = EXTERNAL_STORAGE_PATH + UaalConfig.getConfigDir();
        String ontpath = EXTERNAL_STORAGE_PATH + PreferenceManager.getDefaultSharedPreferences(ctxt)
                .getString(AppConstants.Keys.OFOLDER, AppConstants.Defaults.OFOLDER);

//		String basepath = Environment.getExternalStorageDirectory().getPath() + UaalConfig.getConfigDir();
//		String ontpath = Environment.getExternalStorageDirectory().getPath()
//				+ PreferenceManager.getDefaultSharedPreferences(ctxt)
//						.getString(AppConstants.Keys.OFOLDER,
//								AppConstants.Defaults.OFOLDER);
        Log.d(TAG, "Creating default configuration files");
        try {
            createFile(ctxt, R.raw.jgroups, basepath, "mw.connectors.communication.jgroups.core.properties");
            createFile(ctxt, R.raw.slp, basepath, "mw.connectors.discovery.slp.core.properties");
            createFile(ctxt, R.raw.managersaalspace, basepath, "mw.managers.aalspace.core.properties");
            createFile(ctxt, R.raw.modulesaalspace, basepath, "mw.modules.aalspace.core.properties");
            createFile(ctxt, R.raw.client, basepath + "ri.gateway.multitenant/", "client.properties");
            createFile(ctxt, R.raw.udp, basepath, "udp.xml");
            createFile(ctxt, R.raw.aalspace, basepath, "aalspace.xsd");
            createFile(ctxt, R.raw.home, basepath, "Home.space");
//            createFile(ctxt, R.raw.ontologies, ontpath, "ontologies.cfg");
//            createFile(ctxt, R.raw.activators, ontpath, "activators.cfg");
//            createFile(ctxt, R.raw.ontlighting301, ontpath, "ont.lighting-3.0.1-SNAPSHOT.jar");
//            createFile(ctxt, R.raw.ontcontinuahealthmanager301, ontpath, "ont.continuahealthmanager-3.0.1-SNAPSHOT.jar");
//            createFile(ctxt, R.raw.ontdevice341, ontpath, "ont.device-3.4.1-SNAPSHOT.jar");
//            createFile(ctxt, R.raw.ontpersonalhealthdevice301, ontpath, "ont.personalhealthdevice-3.0.1-SNAPSHOT.jar");
            File folder = new File(basepath + "mw.managers.aalspace.osgi/");
            folder.mkdirs(); //This is so that AALSpace manager can place peers.id
        } catch (IOException e) {
            Log.e(TAG, "Could not create one or more default configuarion files."
                    + "You will have to place them manually: " + e);
        }
    }

    /**
     * Writes a new file into the sdcard if it doesnt exist already
     *
     * @param ctxt     Application context
     * @param fileID   ID of the file in R.raw
     * @param path     Path in the sdcard without the file name
     * @param filename Name of the file
     * @throws IOException If an error occurs during writing
     */
    private static void createFile(Context ctxt, int fileID, String path,
                                   String filename) throws IOException {
        File file = new File(path, filename);
        if (file.exists()) {
            return; // Do not overwrite existing files
        }
        File folder = new File(path);
        folder.mkdirs(); // Create folder if it did not exist, the file is created in FileOutputStream
        InputStream in = ctxt.getResources().openRawResource(fileID);
        FileOutputStream out = new FileOutputStream(file);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static User makeUser() {
        switch (MiddlewareService.mUserType) {
            case AppConstants.USER_TYPE_AP:
                return new AssistedPerson(Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX + getUAALUser());
            case AppConstants.USER_TYPE_CARE:
                return new Caregiver(Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX + getUAALUser());
            default:
                return new User(Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX + getUAALUser());
        }
    }
}
