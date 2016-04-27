package com.ninjapiratestudios.trackercamera;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;

/**
 * Unit tests for PopupDialog.java.
 *
 * @author John Qualls
 * @version 3/5/2016
 */
@PrepareForTest(PopupDialog.class)
@RunWith(PowerMockRunner.class)
public class PopupDialogTest extends BaseTest {
    private PopupDialog dialog;

    private enum TestName {
        LAYOUT,
        LISTENERS;
    }

    @Before
    public void setup() {
        try {
            dialog = Mockito.spy(new PopupDialog());
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_BEFORE_ERROR + e.getMessage());
        }
    }

    @Test
    public void correctStyleTest() {
        // Test setup
        try {
            PowerMockito.suppress(PowerMockito.methods(DialogFragment.class,
                    "onCreate"));
            Mockito.doNothing().when(dialog).setStyle(anyInt(),
                    anyInt());
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SETUP_ERROR + e.getMessage());
        }

        // Execute SUT
        try {
            dialog.onCreate(null);
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SUT_ERROR + e.getMessage());
        }

        // Run test
        try {
            Mockito.verify(dialog).setStyle(DialogFragment.STYLE_NO_TITLE,
                    android.R.style.Theme_Holo_Dialog);
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_EXECUTE_ERROR + e.getMessage());
        }
    }

    @Test
    public void correctDialogLayoutTest() {
        onCreateViewTests(TestName.LAYOUT);
    }

    @Test
    public void correctListenersSetTest() {
        onCreateViewTests(TestName.LISTENERS);
    }

    /**
     * Helper method to run tests that are within the onCreateView method.
     */
    private void onCreateViewTests(TestName testName) {
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);
        ViewGroup container = Mockito.mock(ViewGroup.class);
        View view = Mockito.mock(View.class);
        PopupDialog.ButtonClick listener = Mockito.mock(PopupDialog.ButtonClick.class);

        // Test setup
        try {
            Mockito.when(inflater.inflate(anyInt(), any(ViewGroup.class),
                    anyBoolean())).thenReturn(view);
            Mockito.when(view.findViewById(R.id.fn_dialog_record_button))
                    .thenReturn(view);
            Mockito.when(view.findViewById(R.id.fn_dialog_cancel_button))
                    .thenReturn(view);
            // Just return dummy EditText for test to work
            Mockito.when(view.findViewById(R.id.fn_dialog_file_name))
                    .thenReturn(Mockito.mock(EditText.class));
            PowerMockito.whenNew(PopupDialog.ButtonClick.class)
                    .withNoArguments().thenReturn(listener);
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SETUP_ERROR + e.getMessage());
        }

        // Execute SUT
        try {
            dialog.onCreateView(inflater, container,
                    Mockito.mock(Bundle.class));
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SUT_ERROR + e.getMessage());
        }

        // Run test
        try {
            if (TestName.LAYOUT == testName) {
                Mockito.verify(inflater).inflate(R.layout
                        .fragment_file_name_dialog, container, false);
            } else if (TestName.LISTENERS == testName) {
                Mockito.verify(view).findViewById(R.id.fn_dialog_record_button);
                Mockito.verify(view).findViewById(R.id.fn_dialog_cancel_button);
                Mockito.verify(view, times(2)).setOnClickListener
                        (listener);
            }
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_EXECUTE_ERROR + e.getMessage());
        }
    }
}
