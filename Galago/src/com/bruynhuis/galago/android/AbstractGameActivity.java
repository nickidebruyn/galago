/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.jme3.app.AndroidHarness;
import java.util.Properties;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.ui.listener.EscapeListener;
import com.bruynhuis.galago.listener.KeyboardInputListener;
import com.bruynhuis.galago.listener.RemoteActionListener;
import com.google.android.gms.ads.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.games.Games;
import android.app.Activity;
import android.app.AlertDialog;
import static android.content.Context.SENSOR_SERVICE;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;
import com.bruynhuis.galago.listener.SelectionActionListener;
import com.bruynhuis.galago.sound.AndroidMidiPlayer;
import com.bruynhuis.galago.sound.MidiPlayer;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.jme3.audio.AudioRenderer;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

/**
 * This class the AbstractGameActivity should be extended when ever you choose
 * to use the GalagoLibrary with android. This class will give your game
 * features such as admob support, sensor support, midi music, google play
 * services, etc.
 *
 * @author NideBruyn
 */
public abstract class AbstractGameActivity extends AndroidHarness
        implements KeyboardInputListener, RemoteActionListener, EscapeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener, SelectionActionListener {

    protected SensorManager sensorManager = null;
    protected Sensor accelerometer;
    protected Sensor magnetometer;
    private float[] gravity;
    private float[] geomagnetic;
    private float R[] = new float[9];
    private float I[] = new float[9];
    private float[] orientationVector = new float[3];
    private float[] lastAcc = {0, 0, 0};
    private static final int GOOGLE_RESOLUTION_REQUEST_CODE = 444;
    private static final int SAVED_GAMES_REQUEST_CODE = 555;
    private static final int ACHIEVEMENTS_REQUEST_CODE = 666;
    protected RelativeLayout addViewLayout;
    protected AdView adView;
    protected InterstitialAd interstitialAd;
    protected RewardedVideoAd rewardedVideoAd;
    protected GoogleApiClient googleApiClient;
    protected GoogleAnalytics analytics;
//    private Snapshot openedSnapshot;
//    private byte[] openedLevelData;
    protected Tracker tracker;
    private boolean isAccessingScores = false;
    private int scoreToAdd = 0;
    private String leaderboard = "";
    /*
     * You have access to the properties and they can be specified in preload();
     */
    protected String APP_PATH = "";
    protected String PLAYSTORE_URL = "";
    protected String MOREAPPS_URL = "";
    protected String ADMOB_ID = "";
    protected String ADMOB_INTERSTITIALS_ID = "";
    protected String ADMOB_REWARDS_ID = "";
    protected String ANALYTICS_TRACKER_ID = "";
    protected boolean useAdmob = false;
    protected boolean useAdmobInterstitials = false;
    protected boolean useAdmobRewards = false;
    protected boolean admobBannerBottom = true;
    protected boolean useMidiMusicTracks = false;
    protected boolean useSensor = false;
    protected boolean usePlayServices = false;
    protected boolean useAnalytics = false;
    protected boolean keepAudioActive = false;
    protected boolean useCamera = false;
    private Camera mCamera;

    /*
     * Note that you can ignore the errors displayed in this file,
     * the android project will build regardless.
     * Install the 'Android' plugin under Tools->Plugins->Available Plugins
     * to get error checks and code completion for the Android project files.
     */
    public AbstractGameActivity() {
        preload();
        // Set the application class to run
        appClass = APP_PATH;

        // Set the desired EGL configuration
        eglBitsPerPixel = 24;
        eglAlphaBits = 0;
        eglDepthBits = 16;
        eglSamples = 0;
        eglStencilBits = 0;
        frameRate = 30;

        // Exit Dialog title & message
        exitDialogTitle = "Exit?";
        exitDialogMessage = "Press Yes";

        //The exit will be handled by jME itself
        handleExitHook = false;

        // Enable MouseEvents being generated from TouchEvents (default = true)
        mouseEventsEnabled = true;
        // Set the default logging level (default=Level.INFO, Level.ALL=All Debug Info)
        // Invert the MouseEvents X (default = true)
        mouseEventsInvertX = false;
        // Invert the MouseEvents Y (default = true)
        mouseEventsInvertY = false;

        //Added by me
        screenShowTitle = false;

        init();

    }

    /**
     * Called prior to loading the application activity. Here you must override
     * specific functionallity if needed. one will typical setup your ad id's
     * etc.
     */
    protected abstract void preload();

    /**
     * When the loading happens
     */
    protected abstract void init();

    /**
     * This is called after the activity has been loaded.
     */
    protected abstract void postLoad();

    /**
     * Called when an input box needs to capture date
     *
     * @param prprts
     * @return
     */
    public String doInput(Properties prprts) {
        //TODO: Fire the input keyboard focus
//        System.out.println("Fired some focus input...");

        this.runOnUiThread(new Runnable() {
            public void run() {
//                Toast toast = Toast.makeText(getApplicationContext(), "You fired some input!!", Toast.LENGTH_SHORT);
//                toast.show();

                showTextInputDialog();
            }
        });

        return null;
    }

    /**
     * Show a selection dialog
     *
     * @param prprts
     */
    public void doSelectionOption(final HashMap<Integer, String> items) {

        this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(AbstractGameActivity.this);
                builder.setTitle("Select Options");
                String[] options = {"- None -"};

                if (items != null) {
                    int size = items.values().size();
                    options = new String[size];
                    options = items.values().toArray(options);
                }

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Set the selection
                        if (getJmeApplication() != null) {
                            ((BaseApplication) getJmeApplication()).setDropdownSelectedIndex(which);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

    }

    public void doAddScores(Properties prprts) {
        if (googleApiClient != null) {
            String scoreStr = prprts.getProperty(BaseApplication.SCORE);
            if (scoreStr != null && !scoreStr.equals("")) {
                long score = Long.valueOf(scoreStr);
                if (googleApiClient.isConnected()) {
                    Games.Leaderboards.submitScore(googleApiClient, prprts.getProperty(BaseApplication.LEADERBOARD), score);
                }
            }

        }
    }

    public void doGoogleSignIn(Properties prprts) {
        if (googleApiClient != null) {
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
        }
    }

    public void doGoogleSignOut(Properties prprts) {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                googleApiClient.disconnect();
                if (getJmeApplication() != null) {
                    ((BaseApplication) getJmeApplication()).doGoogleAPIDisconnected("Disconnected from google play services!");
                }
            }
        }
    }

    public void doGetScores(Properties prprts) {
        if (googleApiClient != null) {
            int score = Integer.valueOf(prprts.getProperty(BaseApplication.SCORE));

            if (googleApiClient.isConnected()) {

                if (prprts.getProperty(BaseApplication.LEADERBOARD) == null) {
                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(googleApiClient), GOOGLE_RESOLUTION_REQUEST_CODE); // not caring about the result

                } else {
                    Games.Leaderboards.submitScore(googleApiClient, prprts.getProperty(BaseApplication.LEADERBOARD), score);
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApiClient, prprts.getProperty(BaseApplication.LEADERBOARD)), GOOGLE_RESOLUTION_REQUEST_CODE); // not caring about the result
                }

                if (getJmeApplication() != null) {
                    ((BaseApplication) getJmeApplication()).doGoogleAPIConnected("Connection to google services successful!");
                }

            } else {
                scoreToAdd = score;
                isAccessingScores = true;
                leaderboard = prprts.getProperty(BaseApplication.LEADERBOARD);

                googleApiClient.connect();
            }

        }

    }

    public void doUnlockAchievements(Properties prprts) {
        if (googleApiClient != null) {
            String achievementID = prprts.getProperty(BaseApplication.ID);
            if (googleApiClient.isConnected()) {
                Games.Achievements.unlock(googleApiClient, achievementID);
            }
        }
    }

    public void doIncrementAchievements(Properties prprts) {
        if (googleApiClient != null) {
            String achievementID = prprts.getProperty(BaseApplication.ID);
            if (googleApiClient.isConnected()) {
                Games.Achievements.increment(googleApiClient, achievementID, 1);
            }
        }
    }

    public void doGetAchievements(Properties prprts) {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                startActivityForResult(Games.Achievements.getAchievementsIntent(googleApiClient), ACHIEVEMENTS_REQUEST_CODE);
            }
        }
    }

    /**
     * Load a Snapshot from the Saved Games service based on its unique name.
     * After load, the UI will update to display the Snapshot data and
     * SnapshotMetadata.
     *
     * @param snapshotName the unique name of the Snapshot.
     */
    private void doOpenSavedGame(Properties prprts) {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {

                final String snapshotName = prprts.getProperty(BaseApplication.SAVED_GAME_NAME);

//                showAlert("Prepare Saved Game: " + snapshotName);
                PendingResult<Snapshots.OpenSnapshotResult> pendingResult = Games.Snapshots.open(
                        googleApiClient, snapshotName, true);

//                showAlert("Loading Saved Game");
                ResultCallback<Snapshots.OpenSnapshotResult> callback = new ResultCallback<Snapshots.OpenSnapshotResult>() {
                    @Override
                    public void onResult(Snapshots.OpenSnapshotResult openSnapshotResult) {

                        if (openSnapshotResult.getStatus().isSuccess()) {
//                            showAlert("Successfully loaded snapshot: " + snapshotName);

                            byte[] data = new byte[0];

                            try {
                                data = openSnapshotResult.getSnapshot().getSnapshotContents().readFully();

                            } catch (IOException e) {
//                                showAlert("Exception reading snapshot: " + e.getMessage());
                                if (getJmeApplication() != null) {
//                                    showAlert("Error opening saved game with status: " + e.getMessage());
                                    ((BaseApplication) getJmeApplication()).fireSavedGameErrorListener("Error opening saved game, " + e.getMessage());
                                }
                            }

//                            showAlert("Snapshot data: " + new String(data));
                            if (getJmeApplication() != null) {
//                                showAlert("Goto");
                                ((BaseApplication) getJmeApplication()).fireSavedGameOpenListener(snapshotName, new String(data));
                            }

                        } else //                            showAlert("Failed to load snapshot.");
                        if (getJmeApplication() != null) {
//                                showAlert("Error opening saved game.");
                            ((BaseApplication) getJmeApplication()).fireSavedGameErrorListener("Error opening saved game.");
                        }

                    }
                };
                pendingResult.setResultCallback(callback);

            }
        }

    }

    /**
     * Save game progress to the cloud
     *
     * @param prprts
     */
    private void doCommitSavedGame(Properties prprts) {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                final String savedGameName = prprts.getProperty(BaseApplication.SAVED_GAME_NAME);
//                final String savedGameDesc = prprts.getProperty(BaseApplication.SAVED_GAME_DESCRIPTION);
                final String dataStr = prprts.getProperty(BaseApplication.SAVED_GAME_DATA);
                final boolean createIfMissing = false;

                // Use the data from the EditText as the new Snapshot data.
                final byte[] data = dataStr.getBytes();

                AsyncTask<Void, Void, Boolean> updateTask = new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected void onPreExecute() {
//                        showAlert("Updating Saved Game");
                    }

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        Snapshots.OpenSnapshotResult open = Games.Snapshots.open(
                                googleApiClient, savedGameName, createIfMissing).await();

                        if (!open.getStatus().isSuccess()) {
//                    showAlert("Could not open Snapshot for update.");
                            return false;
                        }

                        // Change data but leave existing metadata
                        Snapshot snapshot = open.getSnapshot();
                        snapshot.getSnapshotContents().writeBytes(data);

                        Snapshots.CommitSnapshotResult commit = Games.Snapshots.commitAndClose(
                                googleApiClient, snapshot, SnapshotMetadataChange.EMPTY_CHANGE).await();

                        if (!commit.getStatus().isSuccess()) {
//                    showAlert("Failed to commit Snapshot.");
                            return false;
                        }

                        // No failures
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
//                            showAlert("Successfully commited Snapshot.");
//                    displayMessage(getString(R.string.saved_games_update_success), false);
                        } else {
//                            showAlert("Failed to commit Snapshot.");
//                    displayMessage(getString(R.string.saved_games_update_failure), true);
                        }

                    }
                };
                updateTask.execute();
            }
        }

    }

    /**
     * Opens a default google UI. Displayes all saved games.
     *
     * @param prprts
     */
    public void doShowSavedGame(Properties prprts) {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                Intent savedGamesIntent = Games.Snapshots.getSelectSnapshotIntent(googleApiClient, "Current saved games",
                        true, true, Games.Snapshots.DISPLAY_LIMIT_NONE);
                startActivityForResult(savedGamesIntent, SAVED_GAMES_REQUEST_CODE);
            }
        }
    }

    /**
     * Gives a soft keyboard
     */
    private void showTextInputDialog() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    /**
     * This method will send an analytics action to play services.
     *
     * @param prprts
     */
    public void doAnalyticsAction(Properties prprts) {
        if (analytics != null && tracker != null) {
            String catagoryStr = prprts.getProperty(BaseApplication.ANALYTICS_CATAGORY);
            String actionStr = prprts.getProperty(BaseApplication.ANALYTICS_ACTION);
            String labelStr = prprts.getProperty(BaseApplication.ANALYTICS_LABEL);

            if (catagoryStr != null && actionStr != null && labelStr != null) {

                tracker.send(new HitBuilders.EventBuilder(catagoryStr, actionStr)
                        .setLabel(labelStr)
                        .build());

            }
        }
    }

    /**
     * Called from the jME side when some action must be performed.
     *
     * @param prprts
     * @return
     */
    public String doAction(final Properties prprts) {
        if (prprts != null) {

            String action = prprts.getProperty(BaseApplication.ACTION);
            if (action != null) {
                if (action.equals(BaseApplication.ACTION_AD)) {
                    doAdd(prprts);

                } else if (action.equals(BaseApplication.ACTION_MORE)) {
                    doMore(prprts);

                } else if (action.equals(BaseApplication.ACTION_LINK)) {
                    doLinkToURL(prprts);

                } else if (action.equals(BaseApplication.ACTION_RATE)) {
                    doRate(prprts);

                } else if (action.equals(BaseApplication.ACTION_SHARE)) {
                    doShare(prprts);

                } else if (action.equals(BaseApplication.ACTION_EMAIL)) {
                    doEmail(prprts);

                } else if (action.equals(BaseApplication.ACTION_ALERT)) {
                    showAlert(prprts.getProperty(BaseApplication.MESSAGE_TEXT));

                } else if (action.equals(BaseApplication.ACTION_VIBRATE)) {
                    doVibrate(prprts);

                } else if (action.equals(BaseApplication.ACTION_ADD_SCORE)) {
                    doAddScores(prprts);

                } else if (action.equals(BaseApplication.ACTION_INIT_ADS)) {
                    doInitAds();

                } else if (action.equals(BaseApplication.ACTION_GET_SCORES)) {
                    doGetScores(prprts);

                } else if (action.equals(BaseApplication.ACTION_UNLOCK_ACHIEVEMENT)) {
                    doUnlockAchievements(prprts);

                } else if (action.equals(BaseApplication.ACTION_INCREMENT_ACHIEVEMENT)) {
                    doIncrementAchievements(prprts);

                } else if (action.equals(BaseApplication.ACTION_GET_ACHIEVEMENTS)) {
                    doGetAchievements(prprts);

                } else if (action.equals(BaseApplication.ACTION_GOOGLE_SIGNIN)) {
                    doGoogleSignIn(prprts);

                } else if (action.equals(BaseApplication.ACTION_GOOGLE_SIGNOUT)) {
                    doGoogleSignOut(prprts);

                } else if (action.equals(BaseApplication.ACTION_ANALYTICS)) {
                    doAnalyticsAction(prprts);

                } else if (action.equals(BaseApplication.ACTION_OPEN_SAVED_GAME)) {
                    doOpenSavedGame(prprts);

                } else if (action.equals(BaseApplication.ACTION_COMMIT_SAVED_GAME)) {
                    doCommitSavedGame(prprts);

                } else if (action.equals(BaseApplication.ACTION_SHOW_SAVED_GAME)) {
                    doShowSavedGame(prprts);
                }

            }
        }

        return null;
    }

    /**
     * Called when the app exits
     *
     * @param bln
     */
    public void doEscape(boolean bln) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (app != null) {
                    app.stop(true);
                }
                app = null;
                finish();
            }
        });
    }

    /**
     * Share this application TODO
     *
     * @param properties
     */
    protected void doShare(Properties properties) {

        //Share the levels
        if (properties != null) {
            //Share the game
            String subject = "Check out this app!!";
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.putExtra(Intent.EXTRA_TEXT, PLAYSTORE_URL);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(sendIntent, "Share via"));

        }

    }

    /**
     * Share this application TODO
     *
     * @param properties
     */
    protected void doEmail(Properties properties) {

        //Share the levels
        if (properties != null) {
            //Share the game
            String subject = properties.getProperty(BaseApplication.EMAIL_SUBJECT);
            String address = properties.getProperty(BaseApplication.EMAIL_ADDRESS);
            String content = properties.getProperty(BaseApplication.EMAIL_CONTENT);

            if (subject != null && address != null && content != null) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, content);
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email:"));
            }

        }

    }

    /**
     * Show an add on the screen.
     *
     * @param prprts
     */
    protected void doAdd(Properties prprts) {
        if (prprts != null) {
            String type = prprts.getProperty(BaseApplication.TYPE);
            String showStr = prprts.getProperty(BaseApplication.SHOW);
            boolean show = Boolean.parseBoolean(showStr);

            if (BaseApplication.ADMOB.equals(type)) {
                if (show) {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (adView != null) {
                                adView.setVisibility(adView.VISIBLE);

                            }
                        }
                    });
                } else {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (adView != null) {
                                adView.setVisibility(adView.GONE);
                            }
                        }
                    });
                }
            }

            if (BaseApplication.ADMOB_INTERSTITIALS.equals(type)) {
                if (show) {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (interstitialAd != null) {
                                if (interstitialAd.isLoaded()) {
                                    interstitialAd.show();
                                }
                            }
                        }
                    });
                } else {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (interstitialAd != null) {
                                if (interstitialAd.isLoaded()) {
//                                    interstitialAd.show();
                                }
                            }
                        }
                    });
                }
            }
            
            if (BaseApplication.ADMOB_REWARDS.equals(type)) {
                if (show) {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (rewardedVideoAd != null) {
                                if (rewardedVideoAd.isLoaded()) {
                                    rewardedVideoAd.show();
                                }
                            }
                        }
                    });
                } else {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (rewardedVideoAd != null) {
                                if (rewardedVideoAd.isLoaded()) {
//                                    rewardedVideoAd.show();
                                }
                            }
                        }
                    });
                }
            }

        } else {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    if (adView != null) {
                        adView.setVisibility(adView.GONE);
                    }
                }
            });
        }
    }

    /**
     * Rate the App. This is called when a control calls the app to be rated. It
     * will link to a play store app.
     *
     * @param prprts
     */
    protected void doRate(Properties prprts) {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
        rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(rateIntent);
    }

    /**
     * Link to more apps under this developer.
     *
     * @param action
     */
    protected void doMore(Properties prprts) {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MOREAPPS_URL));
        rateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(rateIntent);
    }

    /**
     * Link to some url.
     *
     * @param action
     */
    protected void doLinkToURL(Properties prprts) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(prprts.getProperty(BaseApplication.URL)));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(browserIntent);
    }

    /**
     * Vibrate the phone.
     *
     * @param prprts
     */
    protected void doVibrate(Properties prprts) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(200);
    }

    /**
     * Shows an alert message
     *
     * @param text
     */
    protected void showAlert(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (usePlayServices) {
            GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this, this, this);
            builder.addApi(Games.API)
                    .addApi(Plus.API)
                    //                    .addApi(AppStateManager.API)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addScope(Games.SCOPE_GAMES)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    //                    .addScope(AppStateManager.SCOPE_APP_STATE)
                    .setGravityForPopups(Gravity.CENTER);
            googleApiClient = builder.build();
        }

        if (useAnalytics) {
            initAnalytics();
        }

        ((BaseApplication) getJmeApplication()).addKeyboardInputListener(this);
        ((BaseApplication) getJmeApplication()).addRemoteActionListener(this);
        ((BaseApplication) getJmeApplication()).addAndroidEscapeListener(this);
        ((BaseApplication) getJmeApplication()).addSelectionActionListener(this);

        if (useSensor) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        }

        if (useMidiMusicTracks) {
            MidiPlayer midiPlayer = new AndroidMidiPlayer(this);
            ((BaseApplication) getJmeApplication()).setMidiPlayer(midiPlayer);
        }

        if (useCamera) {
            initLiveCamera();
        }

        postLoad();

    }

    /**
     * Called when the usedCamera property is true
     */
    protected void initLiveCamera() {
        // Open the default i.e. the first rear facing camera.
        mCamera = Camera.open();

        // What camera formats are supported?
        for (Integer format : mCamera.getParameters().getSupportedPreviewFormats()) {
            System.out.println("ImageFormat: " + format);
        }

        // What camera preview resolutions are supported?
        for (Camera.Size size : mCamera.getParameters().getSupportedPreviewSizes()) {
            System.out.println("PreviewSize: " + size.width + "," + size.height);
        }

        // camera parameters need to be reset before they will take effect
        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        params.setPreviewSize(480, 320);

        //TODO: 
//        System.out.println("ScreenOrientation NOW: " + screenOrientation);
//        if (screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            System.out.println("##############                   SCENE_MODE_PORTRAIT                ################");
//            params.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
//            params.set("orientation", "portrait");
//            params.set("rotation",90);
//            mCamera.setDisplayOrientation(90);
//            
//        } else {
        params.setSceneMode(Camera.Parameters.SCENE_MODE_LANDSCAPE);
        params.set("orientation", "landscape");
        params.set("rotation", 90);
        mCamera.setDisplayOrientation(0);

//        }        
        System.out.println("SET SCENE MODE TO: " + params.getSceneMode());

        mCamera.setParameters(params);

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

                int width = camera.getParameters().getPreviewSize().width;
                int height = camera.getParameters().getPreviewSize().height;

                YuvImage yuv = new YuvImage(data, camera.getParameters().getPreviewFormat(), width, height, null);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Log.e("JME3", "ByteArrayOutputStream created");
                yuv.compressToJpeg(new Rect(0, 0, width, height), 100, bos);

                ((BaseApplication) getJmeApplication()).fireLiveCameraListener(camera.getParameters().getPreviewFormat(), width, height, bos.toByteArray());

            }
        });
        Log.e("JME3", "Preview Callback Added");

        // "set" the preview display
        try {
            mCamera.setPreviewDisplay(null);

        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();

        Log.e("JME3", "On Create Finished");
    }

    protected void initAnalytics() {

        if (analytics == null) {
            analytics = GoogleAnalytics.getInstance(this);
        }

        if (tracker == null) {
            tracker = analytics.newTracker(ANALYTICS_TRACKER_ID);
            tracker.enableExceptionReporting(true);
            tracker.enableAdvertisingIdCollection(true);
            tracker.enableAutoActivityTracking(true);

        }

    }

    /**
     * Called after everything is loaded and we are running the app.
     */
    protected void doInitAds() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (useAdmob) {
                    initAdMobAds();
                }

                if (useAdmobInterstitials) {
                    initAdMobInterstitialsAds();
                }
                
                if (useAdmobRewards) {
                    initAdMobRewardsAds();
                }

            }
        });

    }

    protected void initAdMobInterstitialsAds() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(ADMOB_INTERSTITIALS_ID);

        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
//                showAlert("AdLoadded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                String message = String.format("onAdFailedToLoad (%s)", getInterstitialsAdErrorReason(errorCode));
//                showAlert(message);
            }

            @Override
            public void onAdClosed() {
                //Load a new ad with the request
                AdRequest adRequest = new AdRequest.Builder().build();
                interstitialAd.loadAd(adRequest);
//                showAlert("Load the next admob ad");
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);

        interstitialAd.loadAd(adRequest);
    }

    /**
     * This will initialize the admob service.
     */
    protected void initAdMobAds() {

        /*
         adView = new AdView(this, AdSize.IAB_BANNER, ADMOB_ID);
         */
        addViewLayout = new RelativeLayout(this);

        adView = new AdView(this);
        adView.setAdUnitId(ADMOB_ID);
        adView.setAdSize(AdSize.SMART_BANNER);

        adView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (adView != null) {
                            adView.setVisibility(adView.GONE);
                            adView.bringToFront();

                        }
                    }
                });

            }
        });

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addContentView(addViewLayout, params);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        addViewLayout.addView(adView);
        if (admobBannerBottom) {
            addViewLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        } else {
            addViewLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        }

        addViewLayout.bringToFront();
        adView.bringToFront();

    }
    
    protected void initAdMobRewardsAds() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);        
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

            @Override
            public void onRewardedVideoAdLoaded() {
                if (getJmeApplication() != null) {
                    ((BaseApplication) getJmeApplication()).setRewardAdLoaded(true);
                    ((BaseApplication) getJmeApplication()).fireRewardAdLoadedListener();
                }

            }

            @Override
            public void onRewardedVideoAdOpened() {
            }

            @Override
            public void onRewardedVideoStarted() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
                if (getJmeApplication() != null) {
                    ((BaseApplication) getJmeApplication()).fireRewardAdClosedListener();
                }
                loadAdmobRewardsAd();
                
            }

            @Override
            public void onRewarded(RewardItem ri) {
                //Call back to the game and fire the reward ad listener
                if (getJmeApplication() != null) {
                    ((BaseApplication) getJmeApplication()).fireRewardAdRewardListener(ri.getAmount(), ri.getType());
                }
                
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                //TODO: Call back to the game and fire the reward ad listener
                
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
//                String message = String.format("onRewardAdFailedToLoad (%s)", getInterstitialsAdErrorReason(errorCode));
//                showAlert(message);
                ((BaseApplication) getJmeApplication()).setRewardAdLoaded(false);
            }
        });
        
        loadAdmobRewardsAd();
        
    }
    
    private void loadAdmobRewardsAd() {
        if (rewardedVideoAd != null && !rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(ADMOB_REWARDS_ID, new AdRequest.Builder().build());
        }
    }

    @Override
    protected void onStop() {

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        super.onStop();

    }

    @Override
    protected void onDestroy() {

        if (adView != null) {
            adView.destroy();
        }

        super.onDestroy();

//        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        // If an interstitial is on screen, close it. Otherwise continue as normal.
        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }

        super.onPause();

        if (keepAudioActive) {
            if (app != null) {
                //resume the audio
                AudioRenderer result = app.getAudioRenderer();
                if (result != null) {
                    result.resumeAll();
                }
            }
        }

        if (getJmeApplication() != null) {
            if (((BaseApplication) getJmeApplication()).hasMidiPlayer()) {
                ((BaseApplication) getJmeApplication()).getMidiPlayer().pause();
            }
            ((BaseApplication) getJmeApplication()).doPauseGame();
        }

        if (useCamera && mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {

        if (adView != null) {
            adView.resume();
        }

        if (((BaseApplication) getJmeApplication()).hasMidiPlayer()) {
            ((BaseApplication) getJmeApplication()).getMidiPlayer().play();
        }

        super.onResume();

        if (sensorManager != null) {
            // Register this class as a listener for the accelerometer sensor
//            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (useCamera && mCamera == null) {
            initLiveCamera();
        }

        if (getJmeApplication() != null) {
            ((BaseApplication) getJmeApplication()).doResumeGame();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (googleApiClient != null) {
            if (requestCode == GOOGLE_RESOLUTION_REQUEST_CODE) {
                if (resultCode != Activity.RESULT_OK) {
                    if (getJmeApplication() != null) {
                        ((BaseApplication) getJmeApplication()).doGoogleAPIError("You are not allowed to perform this action.");
                    }
                } else {
                    googleApiClient.connect();
                    //Games.Leaderboards.submitScore(googleApiClient, leaderboard, scoreToAdd);
                    //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApiClient, leaderboard), 0);
                }

            } else if (requestCode == SAVED_GAMES_REQUEST_CODE) {
                if (resultCode != Activity.RESULT_OK) {
                    if (getJmeApplication() != null) {
                        if (intent != null) {
                            if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_METADATA)) {
                                // Load a snapshot.
                                SnapshotMetadata snapshotMetadata = (SnapshotMetadata) intent.getParcelableExtra(Snapshots.EXTRA_SNAPSHOT_METADATA);
                                Properties properties = new Properties();
                                properties.put(BaseApplication.SAVED_GAME_NAME, snapshotMetadata.getUniqueName());
                                doOpenSavedGame(properties);

                            } else if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_NEW)) {
                                // Create a new snapshot named with a unique string
                                String unique = new BigInteger(281, new Random()).toString(13);
                                String uniqueSaveName = "Saved-" + unique;
                                Properties properties = new Properties();
                                properties.put(BaseApplication.SAVED_GAME_NAME, uniqueSaveName);
                                doOpenSavedGame(properties);
                            }
                        }

                    }
                }
            }
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // if (isAccessingScores) { // KdB - why?

        String errorMessage = "";

        switch (result.getErrorCode()) {
            case ConnectionResult.DEVELOPER_ERROR: {
                errorMessage = "A developer error occurred.";
                break;
            }
            case ConnectionResult.INTERNAL_ERROR: {
                errorMessage = "An internal error occurred.";
                break;
            }
            case ConnectionResult.INVALID_ACCOUNT: {
                errorMessage = "You have an invalid account.";
                break;
            }
            case ConnectionResult.LICENSE_CHECK_FAILED: {
                errorMessage = "The license check failed.";
                break;
            }
            case ConnectionResult.NETWORK_ERROR: {
                errorMessage = "A network error occurred. Please try again.";
                break;
            }
            case ConnectionResult.RESOLUTION_REQUIRED: {
                break;
            }
            case ConnectionResult.SERVICE_DISABLED: {
                errorMessage = "The google play services is currently disabled.";
                break;
            }
            case ConnectionResult.SERVICE_INVALID: {
                errorMessage = "The google play services is invalid.";
                break;
            }
            case ConnectionResult.SERVICE_MISSING: {
                errorMessage = "The google play services is could not be found.";
                break;
            }
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: {
                errorMessage = "The google play services requires an update.";
                break;
            }
            case ConnectionResult.SIGN_IN_REQUIRED: {
                break;
            }
            case ConnectionResult.SUCCESS: {
                break;
            }
            default: {
                break;
            }

        }

        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, GOOGLE_RESOLUTION_REQUEST_CODE);
            } catch (Exception e) {
                if (getJmeApplication() != null) {
                    ((BaseApplication) getJmeApplication()).doGoogleAPIError("An unknown error occurred.");
                }
            }
        } else if (getJmeApplication() != null) {
            ((BaseApplication) getJmeApplication()).doGoogleAPIError(errorMessage);
        }

        //}
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (googleApiClient != null) {
            if (isAccessingScores) {

                if (leaderboard == null) {
                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(googleApiClient), GOOGLE_RESOLUTION_REQUEST_CODE); // not caring about the result

                } else {
                    Games.Leaderboards.submitScore(googleApiClient, leaderboard, scoreToAdd);
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApiClient, leaderboard), GOOGLE_RESOLUTION_REQUEST_CODE);
                }

            }

            if (getJmeApplication() != null) {
                ((BaseApplication) getJmeApplication()).doGoogleAPIConnected("Connection to google services successful!");
            }
        }

    }

    public void onConnectionSuspended(int cause) {
    }

    /**
     * Gets a string error reason from an error code.
     */
    private String getInterstitialsAdErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public final void onSensorChanged(SensorEvent sensorEvent) {
        // wait for app to be up before firing in sensors
        if (getJmeApplication() == null) {
            return;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            lastAcc = sensorEvent.values.clone();
            if (getJmeApplication() != null) {
                ((BaseApplication) getJmeApplication()).fireSensorListener(lastAcc[0], lastAcc[1], lastAcc[2]);
            }

        }

//        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            gravity = sensorEvent.values;
//        }
//        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            geomagnetic = sensorEvent.values;
//        }
//        if (gravity != null && geomagnetic != null) {
//            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
//            if (success) {
//                SensorManager.getOrientation(R, orientationVector);
//                if (getJmeApplication() != null) {
////                    ((BaseApplication) getJmeApplication()).fireSensorListener(orientationVector[2], -orientationVector[1], orientationVector[0]);
////                    ((BaseApplication) getJmeApplication()).fireSensorListener(orientationVector[0], orientationVector[1], orientationVector[2]);
////                    ((BaseApplication) getJmeApplication()).fireSensorListener(orientationVector[2], orientationVector[1], orientationVector[0]);
////                    ((BaseApplication) getJmeApplication()).fireSensorListener(orientationVector[2], orientationVector[0], orientationVector[1]);
//                    ((BaseApplication) getJmeApplication()).fireSensorListener(orientationVector[2], -orientationVector[0], -orientationVector[1]);
//                }
////                tempQuat.fromAngles(orientationVector[2], -orientationVector[1], orientationVector[0]);
////                orientation.slerp(tempQuat, 0.2f);
//            }
//        }
    }
}
