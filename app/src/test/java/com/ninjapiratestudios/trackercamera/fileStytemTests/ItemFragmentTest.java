package com.ninjapiratestudios.trackercamera.fileStytemTests;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;

import com.ninjapiratestudios.trackercamera.ItemFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Tests the Item Frament Class
 *
 * @author Benjmain Boudra
 * @version 1.0
 * @date 3/20/2016
 */
@PrepareForTest(ItemFragment.class)
@RunWith(PowerMockRunner.class)
public class ItemFragmentTest extends BaseTest{
    private ItemFragment itemFragment;

    @Before
    public void setup()
    {
        itemFragment = PowerMockito.spy(new ItemFragment());
    }

    public void columnCountShouldEqual1(){
        //Given
        int mColumnCount = 1;
        fragmentOnCreateMock();
        Bundle itemFragmentBundle = Mockito.mock(Bundle.class);
        //When
        Mockito.when(itemFragment.getArguments()).thenReturn(itemFragmentBundle);
        //Then
        Mockito.verify(itemFragmentBundle).getInt("column-count");
    } //NOTHING IS HAPPENING HERE THAT CAN BE VERIFIED. STILL WANT TO KEEP AS REFERENCE.


    @Test
    public void shouldCallPublicExternalDirectory()
    {
        //Given
        PowerMockito.mockStatic(Environment.class);
    }

    //HELPER METHODS

    /**
     * Creates a mock of the OnCreate Class
     *
     */
    public void fragmentOnCreateMock() {
        PowerMockito.suppress(PowerMockito.methods(Fragment.class,
                "onCreate"));
    }

}
