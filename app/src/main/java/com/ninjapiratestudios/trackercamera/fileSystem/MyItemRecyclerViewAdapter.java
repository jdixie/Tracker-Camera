package com.ninjapiratestudios.trackercamera.fileSystem;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ninjapiratestudios.trackercamera.fileSystem.ItemFragment.OnListFragmentInteractionListener;
import com.ninjapiratestudios.trackercamera.R;
import com.ninjapiratestudios.trackercamera.fileSystem.fileContent.FileContent;

import java.io.File;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.ninjapiratestudios.trackercamera.fileSystem.fileContent.FileContent.FileHolder} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List<FileContent.FileHolder> mValues;
    private final OnListFragmentInteractionListener mListener;
    ItemFragment iF;

    public MyItemRecyclerViewAdapter(List<FileContent.FileHolder> items, OnListFragmentInteractionListener listener, ItemFragment iF) {
        mValues = items;
        mListener = listener;
        this.iF = iF;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    public void update()
    {
        mValues = iF.getFileContent(true).getItems();
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(position+1));
        holder.mContentView.setText(mValues.get(position).getVideoFile().getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    Log.i("Action", "A button was just Clicked");
                    //mListener.onListFragmentInteraction(Uri.parse(holder.mItem.getVideoFile().getAbsolutePath()));
                    File f = mValues.get(position).getVideoFile();
                    mListener.onListFragmentInteraction(f);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public FileContent.FileHolder mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
