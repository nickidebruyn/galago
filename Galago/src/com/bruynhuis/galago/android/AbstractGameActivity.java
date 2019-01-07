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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.jme3.app.AndroidHarness;
import java.util.Properties;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.ui.listener.EscapeListener;
import com.bruynhuis.galago.listener.KeyboardInputListener;
import com.bruynhuis.galago.listener.RemoteActionListener;
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
import android.util.Log;
import com.bruynhuis.galago.listener.SelectionActionListener;
import com.bruynhuis.galago.sound.AndroidMidiPlayer;
import com.bruynhuis.galago.sound.MidiPlayer;
import com.jme3.audio.AudioRenderer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class the AbstractGameActivity should be extended when ever you choose
 * to use the GalagoLibrary with android. This class will give your game
 * features such as admob support, sensor support, midi music, google play
 * services, etc.
 *
 * @author NideBruyn
 */
public abstract class AbstractGameActivity extends AndroidHarness
        implements KeyboardInputListener, RemoteActionListener, EscapeListener, SensorEventListener, SelectionActionListener {

    protected SensorManager sensorManager = null;
    protected Sensor accelerometer;
    protected Sensor magnetometer;
    private float[] gravity;
    private float[] geomagnetic;
    private float R[] = new float[9];
    private float I[] = new float[9];
    private float[] orientationVector = new float[3];
    private float[] lastAcc = {0, 0, 0};
    /*
     * You have access to the properties and they can be specified in preload();
     */
    protected String APP_PATH = "";
    protected String PLAYSTORE_URL = "";
    protected String MOREAPPS_URL = "";

    protected boolean useMidiMusicTracks = false;
    protected boolean useSensor = false;
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
        frameRate = 60;

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
     * Called from the jME side when some action must be performed.
     *
     * @param prprts
     * @return
     */
    public String doAction(final Properties prprts) {
        if (prprts != null) {

            String action = prprts.getProperty(BaseApplication.ACTION);
            if (action != null) {
                if (action.equals(BaseApplication.ACTION_MORE)) {
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

    @Override
    protected void onStop() {

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        super.onStop();

    }

    @Override
    protected void onPause() {
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
