package com.ninjapiratestudios.trackercamera;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Handles all of the functionality for the DialogFragment used to get the user
 * specified name for the video to record.
 *
 * @author John Qualls
 * @version 3/5/2016
 */
public class PopupDialog extends DialogFragment {
    // Fragment tag name For FragmentManager
    public final static String LOG_TAG = "FILE_NAME_DIALOG";
    public final static int MAX_FILE_NAME_SIZE = 251;
    protected final static String FRAGMENT_TAG = "DIALOG_FRAGMENT";
    private EditText editText;
    private CameraRecorder cameraRecorder;
    private VideoActivity videoActivity;
    private Setup setupActivity;
    public enum DialogType {
        FILE_NAME_DIALOG,
        BLUETOOTH_ALERT
    }
    private DialogType type;
    /**
     * Static factory method that is required for fragments to create new
     * objects.
     *
     * @param cameraRecorder The object responsible for all camera operations.
     * @return A new instance of this class.
     */
    public static PopupDialog newFileNameDialog(CameraRecorder cameraRecorder) {
        PopupDialog popupDialog = new PopupDialog();
        popupDialog.type = DialogType.FILE_NAME_DIALOG;
        popupDialog.cameraRecorder = cameraRecorder;
        return popupDialog;
    }

    public static PopupDialog newBluetoothAlertDialog(){
        PopupDialog popupDialog = new PopupDialog();
        popupDialog.type = DialogType.BLUETOOTH_ALERT;
        return popupDialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(type == DialogType.FILE_NAME_DIALOG)
            this.videoActivity = (VideoActivity)activity;
        else if(type == DialogType.BLUETOOTH_ALERT)
            this.setupActivity = (Setup)activity;
    }

    /**
     * Sets the style for the DialogFragment.
     *
     * @param savedInstanceState Irrelevant to this dialog, Required to
     *                           override the super class method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set dialog style
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style
                .Theme_Holo_Dialog);
    }

    /**
     * Creates a View for the dialog specified in fragment_file_name_dialog.xml
     * and sets the button listeners.
     *
     * @param inflater           Creates a View out of the xml file.
     * @param container          Parent View container needed to create View.
     * @param savedInstanceState Irrelevant to this dialog, Required to
     *                           override the super class method.
     * @return The created view specified in the xml file.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate xml view
        if(type == DialogType.FILE_NAME_DIALOG) {
            View v = inflater.inflate(R.layout.fragment_file_name_dialog,
                    container, false);

            // Set button listeners
            v.findViewById(R.id.fn_dialog_record_button).setOnClickListener
                    (new ButtonClick());
            v.findViewById(R.id.fn_dialog_cancel_button).setOnClickListener
                    (new ButtonClick());

            // Get reference to EditText
            editText = (EditText) v.findViewById(R.id.fn_dialog_file_name);

            return v;
        }else{
            View v = inflater.inflate(R.layout.bluetooth_alert,
                    container, false);
            v.findViewById(R.id.btError_Button).setOnClickListener
                    (new ButtonClick());
            return v;
        }
    }

    /**
     * Validates a file name for the Android filesystem.
     *
     * @param fileName The file name to validate.
     * @return Whether or not the file name is valid.
     */
    private boolean fileNameValidation(String fileName) {
        // Validate max size
        if (fileName.length() > MAX_FILE_NAME_SIZE) {
            Toast.makeText(getActivity(), "File Name Too Long.", Toast
                    .LENGTH_LONG).show();
            return false;
        }

        // Validate empty string
        if (fileName.isEmpty()) {
            Toast.makeText(getActivity(), "Empty File Name.", Toast
                    .LENGTH_LONG).show();
            return false;
        }

        // Validate / character
        if (fileName.contains("/")) {
            Toast.makeText(getActivity(), "/ is an invalid character", Toast
                    .LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /**
     * Handles all the save and cancel button actions for this dialog
     */
    protected class ButtonClick implements View.OnClickListener {
        /**
         * Handles Handles all the save and cancel button actions for this
         * dialog
         *
         * @param v The button view that was clicked by the user.
         */
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.fn_dialog_record_button) {
                // Save was clicked
                if (!cameraRecorder.isCameraRecording()) {
                    // Validate filename
                    String fileName = editText.getText().toString();
                    if (fileNameValidation(fileName)) {
                        // Begin recording and dismiss dialog
                        videoActivity.toggleRecordButton();
                        cameraRecorder.setCameraRecording(true);
                        cameraRecorder.setFileName(fileName);
                        cameraRecorder.startRecording();
                        dismiss();
                    }
                }
            } else if(v.getId() == R.id.fn_dialog_cancel_button) { // Cancel was clicked
                // Dismiss Dialog
                dismiss();
            }else if(v.getId() == R.id.btError_Button){
                setupActivity.finish();
            }
        }
    }
}
