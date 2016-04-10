package com.ninjapiratestudios.trackercamera.fileStytemTests;

import com.ninjapiratestudios.trackercamera.fileSystem.ItemFragment;
import com.ninjapiratestudios.trackercamera.fileSystem.fileContent.FileContent;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;

/**
 * Provides Unit tests for the File Content Class and the FileHolder Inner Class
 */
@PrepareForTest(ItemFragment.class)
@RunWith(PowerMockRunner.class)
public class FileContentTest extends BaseTest {

    private FileContent fileContent;

    @Before
    public void setup()
    {
        fileContent = new FileContent();
    }

    @Test
    public void ShouldCreateEmptyFileHolderObject()
    {
        //Given

        //When
        FileContent fC = new FileContent();
        //Then
        assertEquals(0,fC.size());
    }

    @Test
    public void ShouldCreateFileContentClassWith1FileHolderObject()
    {
        //Given
        File[] files = super.generateExternalStorageFileMockObjects();
        //When
        FileContent fileContent = super.generateFileContentLength1(files);
        //Then
        assertEquals(1,fileContent.size());
    }

    @Test
    public void ShouldCreateFileContentClassWith8FileHolderObjects()
    {
        //Give
        File[] files = super.generateExternalStorageFileMockObjects();
        //When
        FileContent fileContent = super.generateFileContentLength8(files);
        //Then
        assertEquals(8,fileContent.size());
    }

    @Test
    public void ShouldAddFileHolderObjectToFileContentClass()
    {
        //Given
        FileContent fCTo = new FileContent();
        File[] files = generateExternalStorageFileMockObjects();
        FileContent fCFrom = super.generateFileContentLength8(files);
        FileContent.FileHolder fH= fCFrom.getItem(1);

        //When
        fCTo.addItem(fH);

        //Then
        assertEquals(1,fCTo.size());
        assertEquals(fH,fCTo.getItem(0));
    }

    @Test
    public void ShouldGetItemFromFileContentObject()
    {
        //Given
        File fileMock = Mockito.mock(File.class);
        fileContent = new FileContent();
        FileContent.FileHolder fH = fileContent.new FileHolder(fileMock);
        fileContent.addItem(fH);

        //When
        FileContent.FileHolder fHReturned = fileContent.getItem(0);

        //Then
        assertEquals(1,fileContent.size());
        assertEquals(fH,fHReturned);
    }

    @Test
    public void fileContentSizeShouldEqual19()
    {
        //given
        File[] files = generateExternalStorageFileMockObjects();
        File f = Mockito.mock(File.class);
        given(f.getName()).willReturn("zzzz.mp4");
        FileContent.FileHolder fH = fileContent.new FileHolder(f);

        //when
        fileContent = super.generateFileContentLength8(files);
        fileContent.addItem(fH);

        //then
        assertEquals(9, fileContent.size());
    }

    @Test
    public void itemsShouldBeSortedUponBeingAddedToFileContent()
    {
        //given
        File[] files = super.generateExternalStorageFileMockObjects();

        //when
        fileContent = super.generateFileContentLength8(files);

        //then
        assertEquals(files[1], fileContent.getItem(0).getVideoFile());
        assertEquals(files[2], fileContent.getItem(1).getVideoFile());
        assertEquals(files[3], fileContent.getItem(2).getVideoFile());
        assertEquals(files[4], fileContent.getItem(3).getVideoFile());
        assertEquals(files[5], fileContent.getItem(4).getVideoFile());
        assertEquals(files[0], fileContent.getItem(5).getVideoFile());
        assertEquals(files[7], fileContent.getItem(6).getVideoFile());
        assertEquals(files[6], fileContent.getItem(7).getVideoFile());
    }
}
