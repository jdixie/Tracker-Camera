package com.ninjapiratestudios.trackercamera.fileStytemTests;
import com.ninjapiratestudios.trackercamera.fileSystem.ItemFragment;
import com.ninjapiratestudios.trackercamera.fileSystem.MyItemRecyclerViewAdapter;
import com.ninjapiratestudios.trackercamera.fileSystem.fileContent.FileContent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Tests The recycler View Adapter Class
 *
 * @author Benjmain Boudra
 * @version 1.0
 * @date 3/20/2016
 */
public class MyItemRecyclerViewAdapterTest extends BaseTest{

    MyItemRecyclerViewAdapter mIRVA;
    @Before
    public void setup()
    {
        ItemFragment.OnListFragmentInteractionListener oLFIL = Mockito.mock(ItemFragment.OnListFragmentInteractionListener.class);
        File[] files = super.generateExternalStorageFileMockObjects();
        FileContent fC = super.generateFileContentLength8(files);
        mIRVA = new MyItemRecyclerViewAdapter(fC.getItems(),oLFIL);
    }


    @Test
    public void getCountShouldReturn8()
    {
        //given

        //when

        //then
        assertEquals(8, mIRVA.getItemCount());
    }

    @Test
    public void onCreateViewShouldcallRecycler()
    {

    }


}
