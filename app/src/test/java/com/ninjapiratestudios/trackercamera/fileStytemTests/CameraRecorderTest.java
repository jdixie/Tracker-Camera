package com.ninjapiratestudios.trackercamera.fileStytemTests;

import android.app.FragmentManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;

import com.ninjapiratestudios.trackercamera.FileNameDialog;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

/**
 * Unit tests for CameraRecorder.java.
 *
 * @author John Qualls
 * @version 3/21/2016
 */
@PrepareForTest({CameraRecorder.class, Camera.class, FileNameDialog.class,
        Log.class})
@RunWith(PowerMockRunner.class)
public class CameraRecorderTest extends BaseTest {
    private enum TestName {
        GET_CAMERA,
        GET_CAMERA_NULL,
        SET_PREVIEW;
    }

    private CameraRecorder cameraRecorder;

    @Before
    public void setup() {
        cameraRecorder = PowerMockito.spy(new CameraRecorder());
    }

    @Test
    public void getCameraInstanceSuccessTest() {
        staticFactoryMethodTestHelper(TestName.GET_CAMERA);
    }

    @Test
    public void getCameraInstanceFailureTest() {
        staticFactoryMethodTestHelper(TestName.GET_CAMERA_NULL);
    }

    @Test
    public void setPreviewTest() {
        staticFactoryMethodTestHelper(TestName.SET_PREVIEW);
    }

    @Test
    public void displayFileNameDialogTest() {
        VideoActivity activityMock = Mockito.mock(VideoActivity.class);
        FragmentManager expectedArg1 = Mockito.mock(FragmentManager.class);
        FileNameDialog mockDialog = Mockito.mock(FileNameDialog.class);
        // Test setup
        try {
            PowerMockito.mockStatic(FileNameDialog.class);
            PowerMockito.when(FileNameDialog.newInstance(cameraRecorder))
                    .thenReturn(mockDialog);
            Whitebox.setInternalState(cameraRecorder, "activity", activityMock);
            Mockito.when(activityMock.getFragmentManager()).thenReturn
                    (expectedArg1);
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SETUP_ERROR + e.getMessage());
        }

        // Execute SUT
        try {
            cameraRecorder.displayFileNameDialog();
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SUT_ERROR + e.getMessage());
        }

        // Execute test
        try {
            Mockito.verify(mockDialog).show(expectedArg1, FileNameDialog
                    .FRAGMENT_TAG);
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_EXECUTE_ERROR + e.getMessage());
        }
    }

    @Test
    public void startRecordingTest() {
        MediaRecorder mrMock = Mockito.mock(MediaRecorder.class);
        Camera cameraMock = Mockito.mock(Camera.class);
        // Test setup
        try {
            Whitebox.setInternalState(cameraRecorder, "camera", cameraMock);
            PowerMockito.doNothing().when(cameraRecorder, "setupCamera");
            Whitebox.setInternalState(cameraRecorder, "mediaRecorder", mrMock);
            PowerMockito.mockStatic(Log.class);
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SETUP_ERROR + e.getMessage());
        }

        // Execute SUT
        try {
            cameraRecorder.startRecording();
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SUT_ERROR + e.getMessage());
        }

        // Execute test
        try {
            Mockito.verify(cameraMock).unlock();
            PowerMockito.verifyPrivate(cameraRecorder).invoke
                    ("setupCamera");
            Mockito.verify(mrMock).start();
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_EXECUTE_ERROR + e.getMessage());
        }
    }


    private void staticFactoryMethodTestHelper(TestName testName) {
        CameraRecorder actualInstance = null;
        Camera camera = Mockito.mock(Camera.class);

        // Test setup
        try {
            PowerMockito.whenNew(CameraRecorder.class).withNoArguments()
                    .thenReturn(cameraRecorder);
            PowerMockito.doNothing().when(cameraRecorder, "cameraPreviewSetup");
            PowerMockito.mockStatic(Camera.class);
            if (TestName.GET_CAMERA_NULL != testName) {
                PowerMockito.when(Camera.open()).thenReturn(camera);
            }
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SETUP_ERROR + e.getMessage());
        }

        // Execute SUT
        try {
            actualInstance = CameraRecorder.newInstance(Mockito.mock
                    (VideoActivity.class));
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_SUT_ERROR + e.getMessage());
        }

        // Execute test
        try {
            if (TestName.GET_CAMERA == testName) {
                Assert.assertEquals(cameraRecorder, actualInstance);
            } else if (TestName.GET_CAMERA_NULL == testName) {
                Assert.assertNull(actualInstance);
            } else if (TestName.SET_PREVIEW == testName) {
                PowerMockito.verifyPrivate(cameraRecorder).invoke
                        ("cameraPreviewSetup");
            }
        } catch (Exception e) {
            Assert.fail(UNIT_TEST_EXECUTE_ERROR + e.getMessage());
        }
    }
}
