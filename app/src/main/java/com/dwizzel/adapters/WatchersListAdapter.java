package com.dwizzel.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.thekids.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dwizzel on 22/11/2017.
 */

public class WatchersListAdapter extends RecyclerView.Adapter<WatchersListAdapter.ViewHolder> {

    private List<WatcherModel> mWatchersList;
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, email;
        // each data item is just a string in this case
        ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            phone = (TextView) v.findViewById(R.id.phone);
            email = (TextView) v.findViewById(R.id.email);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WatchersListAdapter(ArrayList<WatcherModel> watchersList) {
        try {
            mWatchersList = watchersList;
        }catch(Exception e){
            //
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public WatchersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View mItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watcher_list_item, parent, false);
        //
        return new ViewHolder(mItemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        WatcherModel watcher = mWatchersList.get(position);
        holder.name.setText(watcher.getName());
        holder.phone.setText(watcher.getPhone());
        holder.email.setText(watcher.getEmail());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mWatchersList.size();
    }
}
