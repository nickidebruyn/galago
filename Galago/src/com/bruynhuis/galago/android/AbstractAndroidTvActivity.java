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
import android.widget.Toast;
import com.jme3.app.AndroidHarness;
import java.util.Properties;
import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.ui.listener.EscapeListener;
import com.bruynhuis.galago.listener.KeyboardInputListener;
import com.bruynhuis.galago.listener.RemoteActionListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.EditText;
import com.bruynhuis.galago.listener.AndroidInputEvent;
import com.bruynhuis.galago.listener.SelectionActionListener;
import com.bruynhuis.galago.sound.AndroidMidiPlayer;
import com.bruynhuis.galago.sound.MidiPlayer;
import com.bruynhuis.galago.ui.field.InputType;
import com.jme3.audio.AudioRenderer;
import java.util.HashMap;

/**
 * This class the AbstractGameActivity should be extended when ever you choose
 * to use the GalagoLibrary with android. This class will give your game
 * features such as admob support, sensor support, midi music, google play
 * services, etc.
 *
 * @author NideBruyn
 */
public abstract class AbstractAndroidTvActivity extends AndroidHarness
        implements KeyboardInputListener, RemoteActionListener, EscapeListener, SelectionActionListener {

    /*
     * You have access to the properties and they can be specified in preload();
     */
    protected String APP_PATH = "";
    protected String PLAYSTORE_URL = "";
    protected String MOREAPPS_URL = "";

    protected boolean useMidiMusicTracks = false;
    protected boolean keepAudioActive = false;
    private AndroidInputEvent androidInputEvent;

    /*
     * Note that you can ignore the errors displayed in this file,
     * the android project will build regardless.
     * Install the 'Android' plugin under Tools->Plugins->Available Plugins
     * to get error checks and code completion for the Android project files.
     */
    public AbstractAndroidTvActivity() {
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

        androidInputEvent = new AndroidInputEvent();

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
    public String doInput(Properties prprts, InputType inputType) {
        this.runOnUiThread(new Runnable() {
            public void run() {

                final EditText txtUrl = new EditText(AbstractAndroidTvActivity.this);
                txtUrl.setText(inputType.getText());
                new AlertDialog.Builder(AbstractAndroidTvActivity.this)
                        .setTitle("Enter text:")
                        .setView(txtUrl)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String txt = txtUrl.getText().toString();
                                inputType.updateText(txt);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(AbstractAndroidTvActivity.this);
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
//    protected void showTextInputDialog(String text) {
//
//
//    }
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
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(AbstractAndroidTvActivity.this, text, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BaseApplication) getJmeApplication()).addKeyboardInputListener(this);
        ((BaseApplication) getJmeApplication()).addRemoteActionListener(this);
        ((BaseApplication) getJmeApplication()).addAndroidEscapeListener(this);
        ((BaseApplication) getJmeApplication()).addSelectionActionListener(this);

        if (useMidiMusicTracks) {
            MidiPlayer midiPlayer = new AndroidMidiPlayer(this);
            ((BaseApplication) getJmeApplication()).setMidiPlayer(midiPlayer);
        }

        postLoad();

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

    }

    @Override
    protected void onResume() {

        if (((BaseApplication) getJmeApplication()).hasMidiPlayer()) {
            ((BaseApplication) getJmeApplication()).getMidiPlayer().play();
        }

        super.onResume();

        if (getJmeApplication() != null) {
            ((BaseApplication) getJmeApplication()).doResumeGame();
        }
    }

    @Override
    public void onBackPressed() {
        // If an interstitial is on screen, close it. Otherwise continue as normal.
        ((BaseApplication) getJmeApplication()).fireAllEscapeListeners(true);

    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {

        // Check that the event came from a game controller
        if ((ev.getSource() & InputDevice.SOURCE_JOYSTICK)
                == InputDevice.SOURCE_JOYSTICK
                && ev.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = ev.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(ev, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(ev, -1);
            return true;
        }

        return super.dispatchGenericMotionEvent(ev); //To change body of generated methods, choose Tools | Templates.
    }

    private float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range
                = device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value
                    = historyPos < 0 ? event.getAxisValue(axis)
                            : event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private void processJoystickInput(MotionEvent event, int historyPos) {

        InputDevice inputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
        }
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_Z, historyPos);
        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
        }
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_RZ, historyPos);
        }

        // Update the ship object based on the new x and y values
        androidInputEvent.clear();
        androidInputEvent.setMotionEvent(true);
        androidInputEvent.setDeviceId(event.getDeviceId());
        androidInputEvent.setAction(event.getAction());
        androidInputEvent.setActionButton(event.getActionButton());
        androidInputEvent.setActionIndex(event.getActionIndex());
        androidInputEvent.setX(x);
        androidInputEvent.setY(y);
        androidInputEvent.setDownTime(event.getDownTime());
        androidInputEvent.setEventTime(event.getEventTime());

        runOnUiThread(new Runnable() {
            public void run() {
                ((BaseApplication) getJmeApplication()).fireAndroidInputEvents(androidInputEvent);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent ev) {

        androidInputEvent.clear();
        androidInputEvent.setMotionEvent(false);
        androidInputEvent.setDeviceId(ev.getDeviceId());
        if (ev.getDevice() != null) {
            androidInputEvent.setDeviceName(ev.getDevice().getName());
        }
        androidInputEvent.setAction(ev.getAction());
        androidInputEvent.setKeyCode(ev.getKeyCode());
        androidInputEvent.setCharacters(ev.getCharacters());
        androidInputEvent.setDownTime(ev.getDownTime());
        androidInputEvent.setEventTime(ev.getEventTime());

        runOnUiThread(new Runnable() {
            public void run() {
                ((BaseApplication) getJmeApplication()).fireAndroidInputEvents(androidInputEvent);
            }
        });

        return super.dispatchKeyEvent(ev); //To change body of generated methods, choose Tools | Templates.
    }

}
