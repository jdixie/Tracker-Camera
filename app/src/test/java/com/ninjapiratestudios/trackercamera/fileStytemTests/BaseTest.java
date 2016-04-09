package com.ninjapiratestudios.trackercamera.fileStytemTests;

import android.app.DialogFragment;
import android.app.Fragment;

import com.ninjapiratestudios.trackercamera.fileContent.FileContent;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;

/**
 * This class should contain common methods and fields used for different
 * test classes
 */
public class BaseTest {
    protected final String UNIT_TEST_SETUP_ERROR = "There was an error while " +
            "setting up the test: ";
    protected final String UNIT_TEST_SUT_ERROR = "There was an error " +
            "while " +
            "executing the method under test: ";
    protected final String UNIT_TEST_EXECUTE_ERROR = "The test either failed," +
            " or there was an error while executing the test: ";
    protected final String UNIT_TEST_BEFORE_ERROR = "Error in @Before setup " +
            "method: ";


    /**
     * Generates A series of Mock Objects for testing Purposes and Return them to the caller.
     *
     * @return The Mock File Objects.
     */
    public File[] generateExternalStorageFileMockObjects() {
        File mockFile0 = Mockito.mock(File.class);
        given(mockFile0.getAbsolutePath()).willReturn("/storage/emulated/0/Pictures/Tracker_Camera/Video0.mp4");
        given(mockFile0.getName()).willReturn("Video0.mp4");

        File mockFile1 = Mockito.mock(File.class);
        given(mockFile1.getAbsolutePath()).willReturn("/storage/emulated/0/Pictures/Tracker_Camera/AVideo1.mp4");
        given(mockFile1.getName()).willReturn("AVideo1.mp4");

        File mockFile2 = Mockito.mock(File.class);
        given(mockFile2.getAbsolutePath()).willReturn("/storage/emulated/0/Pictures/Tracker_Camera/aVideo2.mp4");
        given(mockFile2.getName()).willReturn("aVideo2.mp4");

        File mockFile3 = Mockito.mock(File.class);
        given(mockFile3.getAbsolutePath()).willReturn("/storage/emulated/0/Pictures/Tracker_Camera/busStop3.mp4");
        given(mockFile3.getName()).willReturn("busStop3.mp4");

        File mockFile4 = Mockito.mock(File.class);
        given(mockFile4.getAbsolutePath()).willReturn("/storage/emulated/0/Pictures/Tracker_Camera/BusStop4.mp4");
        given(mockFile4.getName()).willReturn("BusStop4.mp4");

        File mockFile5 = Mockito.mock(File.class);
        given(mockFile5.getAbsolutePath()).willReturn("/storage/emulated/0/Pictures/Tracker_Camera/DogVideo5.mp4");
        given(mockFile5.getName()).willReturn("DogVideo5.mp4");

        File mockFile6 = Mockito.mock(File.class);
        given(mockFile6.getAbsolutePath()).willReturn("/storage/emulated/0/Pictures/Tracker_Camera/z6.mp4");
        given(mockFile6.getName()).willReturn("z6.mp4");

        File mockFile7 = Mockito.mock(File.class);
        given(mockFile7.getAbsolutePath()).willReturn("/storage/emulated/0/Pictures/Tracker_Camera/Video7.mp4");
        given(mockFile7.getName()).willReturn("Video7.mp4");

        File[] files = new File[8];
        files[0] = mockFile0;
        files[1] = mockFile1;
        files[2] = mockFile2;
        files[3] = mockFile3;
        files[4] = mockFile4;
        files[5] = mockFile5;
        files[6] = mockFile6;
        files[7] = mockFile7;
        return files;
    }

    /**
     * Takes a takes a list of files, creates a FileContent object from the first
     * file, and returns that objects to the caller.
     *
     * @param files - list of files.
     * @return the FileContent object.
     */
    protected FileContent generateFileContentLength1(File[] files) {
        FileContent fileContent = new FileContent(Arrays.copyOfRange(files, 0, 1));
        return fileContent;
    }

    /**
     * Takes a takes a list of files, creates a FileContent object from the list,
     * and returns that objects to the caller.
     *
     * @param files - list of files.
     * @return the FileContent object.
     */
    protected FileContent generateFileContentLength8(File[] files) {
        FileContent fileContent = new FileContent(Arrays.copyOfRange(files, 0, 8));
        return fileContent;
    }
}
