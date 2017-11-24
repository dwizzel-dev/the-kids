package com.dwizzel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.datamodels.InvitationModel;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.objects.ObserverNotifObject;
import com.dwizzel.objects.UserObject;
import com.dwizzel.thekids.R;
import com.dwizzel.utils.Tracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dwizzel on 22/11/2017.
 * https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Recycler.html#getViewForPosition(int)
 * TODO: trigger event on status change of the watchers with an observer ou autres
 */

public class WatchOverMeListAdapter extends RecyclerView.Adapter<WatchOverMeListAdapter.ViewHolder> {

    private static final String TAG = "WatchOverMeListAdapter";
    private ArrayList<Item> mList;
    //keep la liste des uid et inviteid et leur position
    //pour le observer/observable sur les notifs
    private HashMap<String, Integer> mKeyList;
    private UserObject mUser;

    // fill la liste avec les headers
    public WatchOverMeListAdapter(Context context) {
        mUser = UserObject.getInstance();
        //on met un observer sur les possible modifs de watchers et invitations
        mUser.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                ObserverNotifObject observerNotifObject = (ObserverNotifObject)o;
                if(observerNotifObject != null){
                    Tracer.log(TAG, String.format("mUser.update: %s = %s",
                            observerNotifObject.getProp(),
                            observerNotifObject.getValue()));
                    //test case
                    switch (observerNotifObject.getProp()){
                        case Const.notif.WATCHER_STATUS:
                            break;
                        case Const.notif.WATCHER_POSITION:
                            break;
                        case Const.notif.WATCHER_UPDATE_TIME:
                            break;
                        case Const.notif.INVITATION_STATE:
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        mList = new ArrayList<>();
        mKeyList = new HashMap<>();
        try {
            int pos = 0;
            //on met un header pour les watchers
            mList.add(new HeaderItem(context.getResources().getString(R.string.watchers_header)));
            pos++;
            //on fill la list avec les watchers
            for(String uid : mUser.getWatchers().keySet()){
                mList.add(new WatcherItem(uid));
                mKeyList.put(uid, pos);
                pos++;
            }
            //on met un header pour les invitations
            mList.add(new HeaderItem(context.getResources().getString(R.string.invitations_header)));
            pos++;
            //on fill la list avec les watchers
            for(String inviteId : mUser.getInvitations().keySet()){
                mList.add(new InvitationItem(inviteId));
                mKeyList.put(inviteId, pos);
                pos++;
            }
        }catch(Exception e){
            Tracer.log(TAG, "WatchOverMeListAdapter.exception: ", e);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WatchOverMeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView;
        if (viewType == Item.TYPE_WATCHER) {
            mItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_list_watcher_item, parent, false);
            return new WatcherViewHolder(mItemView);
        } else if (viewType == Item.TYPE_INVITATION) {
            mItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_list_invitation_item, parent, false);
            return new InvitationViewHolder(mItemView);
        } else if (viewType == Item.TYPE_HEADER) {
            mItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_list_header, parent, false);
            return new HeaderViewHolder(mItemView);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindToViewHolder(holder, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getItemType();
    }



    //-------------------------------------------------------------------------------------
    //NESTED CLASS

    abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
        public abstract void bindToViewHolder(ViewHolder viewholder, int position);
    }

    //-----------------------------------
    abstract class Item {
        static final int TYPE_HEADER = 1;
        static final int TYPE_WATCHER = 2;
        static final int TYPE_INVITATION = 3;
        public abstract int getItemType();
        public abstract String getItemValue();
    }

    //-----------------------------------
    class HeaderItem extends Item {
        private String mTitle = "";
        HeaderItem(String title) {
            super();
            this.mTitle = title;
        }
        @Override
        public int getItemType() {
            return Item.TYPE_HEADER;
        }
        @Override
        public String getItemValue() {
            return mTitle;
        }
    }
    //-----------------------------------
    class HeaderViewHolder extends ViewHolder {
        TextView headerName;
        HeaderViewHolder(View itemView) {
            super(itemView);
            headerName = itemView.findViewById(R.id.headerName);
        }
        public void bindToViewHolder(ViewHolder viewholder, int position) {
            HeaderViewHolder holder = (HeaderViewHolder) viewholder;
            holder.headerName.setText(mList.get(position).getItemValue());
        }
    }
    //-----------------------------------
    class WatcherItem extends Item {
        private String uid;
        WatcherItem(String uid) {
            super();
            this.uid = uid;
        }
        @Override
        public int getItemType() {
            return Item.TYPE_WATCHER;
        }
        @Override
        public String getItemValue() {
            return uid;
        }
    }
    //-----------------------------------
    class WatcherViewHolder extends ViewHolder {
        TextView name, phone, email;
        WatcherViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
        }
        public void bindToViewHolder(ViewHolder viewholder, int position) {
            WatcherViewHolder holder = (WatcherViewHolder) viewholder;
            WatcherModel model = mUser.getWatcher(mList.get(position).getItemValue());
            try {
                holder.name.setText(model.getName());
                holder.phone.setText(model.getPhone());
                holder.email.setText(model.getEmail());
            }catch(Exception e){
                Tracer.log(TAG, "WatcherViewHolder.exception: ", e);
            }
        }
    }
    //-----------------------------------
    class InvitationItem extends Item {
        private String uid;
        InvitationItem(String uid) {
            super();
            this.uid = uid;
        }
        @Override
        public int getItemType() {
            return Item.TYPE_INVITATION;
        }
        @Override
        public String getItemValue() {
            return uid;
        }
    }
    //-----------------------------------
    class InvitationViewHolder extends ViewHolder {
        TextView name, phone, email;
        InvitationViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
        }
        public void bindToViewHolder(ViewHolder viewholder, int position) {
            InvitationViewHolder holder = (InvitationViewHolder) viewholder;
            InvitationModel model = mUser.getInvitation(mList.get(position).getItemValue());
            try {
                holder.name.setText(model.getName());
                holder.phone.setText(model.getPhone());
                holder.email.setText(model.getEmail());
            }catch(Exception e){
                Tracer.log(TAG, "InvitationViewHolder.exception: ", e);
            }
        }
    }


}
