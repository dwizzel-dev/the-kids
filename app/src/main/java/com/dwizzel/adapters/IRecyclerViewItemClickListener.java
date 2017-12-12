package com.dwizzel.adapters;

/**
 * Created by Dwizzel on 12/12/2017.
 */


public interface IRecyclerViewItemClickListener {

    int TYPE_DELETE_ITEM_WATCHING = 1;
    int TYPE_MODIFY_ITEM_WATCHING = 2;
    int TYPE_DELETE_ITEM_WATCHER = 3;
    int TYPE_MODIFY_ITEM_WATCHER = 4;
    int TYPE_DELETE_ITEM_INVITATION = 5;

    interface AdapterClickListener {

        void onItemClick(int position, int type);

        void setClickListener(IRecyclerViewItemClickListener.ActivityClickListener listener);

    }

    interface  ActivityClickListener {

        void onRecycleViewItemClick(int position, String uid, int type);

        void setRecycleViewClickListener();

    }
}