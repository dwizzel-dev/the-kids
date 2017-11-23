package com.dwizzel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dwizzel.datamodels.InvitationModel;
import com.dwizzel.datamodels.InviteModel;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.objects.UserObject;
import com.dwizzel.thekids.R;
import com.dwizzel.utils.Tracer;

import java.util.ArrayList;

/**
 * Created by Dwizzel on 22/11/2017.
 */

public class WatchersListAdapter extends RecyclerView.Adapter<WatchersListAdapter.ViewHolder> {

    private static final String TAG = "WatchersListAdapter";
    private ArrayList<Item> mList;
    private Context mContext;

    // fill la liste avec les headers
    public WatchersListAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<Item>();
        try {
            //on met un header pour les watchers
            mList.add(new HeaderItem(mContext.getResources().getString(R.string.watchers_header)));
            //on fill la list avec les watchers
            for(WatcherModel watcher : UserObject.getInstance().getWatchers().values()){
                mList.add(new WatcherItem(watcher));
            }
            //on met un header pour les invitations
            mList.add(new HeaderItem(mContext.getResources().getString(R.string.invitations_header)));
            //on fill la list avec les watchers
            for(InvitationModel invitation : UserObject.getInstance().getInvitations().values()){
                mList.add(new InvitationItem(invitation));
            }
        }catch(Exception e){
            Tracer.log(TAG, "WatchersListAdapter.exception: ", e);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WatchersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        // each data item is just a string in this case
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
        public abstract Object getItemValue();
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
        public Object getItemValue() {
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
            HeaderItem item = (HeaderItem) mList.get(position);
            holder.headerName.setText((String)item.getItemValue());
        }
    }
    //-----------------------------------
    class WatcherItem extends Item {

        private WatcherModel mModel;

        WatcherItem(WatcherModel watcherModel) {
            super();
            this.mModel = watcherModel;
        }
        @Override
        public int getItemType() {
            return Item.TYPE_WATCHER;
        }
        @Override
        public Object getItemValue() {
            return mModel;
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
            WatcherItem item = (WatcherItem) mList.get(position);
            WatcherModel model = (WatcherModel) item.getItemValue();
            holder.name.setText(model.getName());
            holder.phone.setText(model.getPhone());
            holder.email.setText(model.getEmail());
        }
    }
    //-----------------------------------
    class InvitationItem extends Item {

        private InvitationModel mModel;

        InvitationItem(InvitationModel invitationModel) {
            super();
            this.mModel = invitationModel;
        }
        @Override
        public int getItemType() {
            return Item.TYPE_INVITATION;
        }
        @Override
        public Object getItemValue() {
            return mModel;
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
            InvitationItem item = (InvitationItem) mList.get(position);
            InvitationModel model = (InvitationModel) item.getItemValue();
            holder.name.setText(model.getName());
            holder.phone.setText(model.getPhone());
            holder.email.setText(model.getEmail());
        }
    }




}
