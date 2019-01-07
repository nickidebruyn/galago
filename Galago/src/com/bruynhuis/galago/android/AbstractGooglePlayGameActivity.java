/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import java.util.Properties;
import com.bruynhuis.galago.app.BaseApplication;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.games.Games;
import android.app.Activity;
import android.os.AsyncTask;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

/**
 * This class the AbstractGooglePlayGameActivity should be extended when ever you choose
 * to use the google play services in you game.
 *
 * @author NideBruyn
 */
public abstract class AbstractGooglePlayGameActivity extends AbstractAdsGameActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final int GOOGLE_RESOLUTION_REQUEST_CODE = 444;
    protected static final int SAVED_GAMES_REQUEST_CODE = 555;
    protected static final int ACHIEVEMENTS_REQUEST_CODE = 666;
    protected GoogleApiClient googleApiClient;

    private boolean isAccessingScores = false;
    private int scoreToAdd = 0;
    private String leaderboard = "";
    
    /*
     * You have access to the properties and they can be specified in preload();
     */    
    protected boolean usePlayServices = false;

    
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
                        {
                            if (getJmeApplication() != null) {
//                                showAlert("Error opening saved game.");
                                ((BaseApplication) getJmeApplication()).fireSavedGameErrorListener("Error opening saved game.");
                            }
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
     * Called from the jME side when some action must be performed.
     *
     * @param prprts
     * @return
     */
    public String doAction(final Properties prprts) {
        
        super.doAction(prprts);
        
        if (prprts != null) {

            String action = prprts.getProperty(BaseApplication.ACTION);
            if (action != null) {
                
                if (action.equals(BaseApplication.ACTION_ADD_SCORE)) {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (usePlayServices) {
            GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this, this, this);
            builder.addApi(Games.API)
                    .addScope(Games.SCOPE_GAMES)
                    .setGravityForPopups(Gravity.CENTER);
            googleApiClient = builder.build();
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

}
