package com.ninjapiratestudios.trackercamera.ColorSelection;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninjapiratestudios.trackercamera.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSelectColorsListener} interface
 * to handle interaction events.
 * Use the {@link ColorSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ColorSelectionFragment extends Fragment {

    OnSelectColorsListener mListener;

    public ColorSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ColorSelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ColorSelectionFragment newInstance(String param1, String param2) {
        ColorSelectionFragment fragment = new ColorSelectionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color_selection, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void selectNewColors() {
        if (mListener != null) {
            mListener.selectNewColors();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectColorsListener) {
            mListener = (OnSelectColorsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSelectColorsListener {
        void selectNewColors();
    }
}
