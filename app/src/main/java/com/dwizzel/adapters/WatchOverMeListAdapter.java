package com.dwizzel.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.datamodels.InvitationModel;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.objects.ListItems;
import com.dwizzel.objects.UserObject;
import com.dwizzel.thekids.R;
import com.dwizzel.utils.Tracer;

import java.util.ArrayList;

/**
 * Created by Dwizzel on 22/11/2017.
 * TODO: trigger event on status change of the watchers with an observer ou autres
 */

public class WatchOverMeListAdapter extends RecyclerView.Adapter<WatchOverMeListAdapter.ViewHolder> {

    private static final String TAG = "WatchOverMeListAdapter";
    private ArrayList<ListItems.Item> mList;
    private UserObject mUser;

    // fill la liste avec les headers
    public WatchOverMeListAdapter(ArrayList<ListItems.Item> list) {
        Tracer.log(TAG, "WatchOverMeListAdapter");
        mList = list;
        mUser = UserObject.getInstance();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WatchOverMeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Tracer.log(TAG, "onCreateViewHolder");
        View mItemView;
        if (viewType == ListItems.Type.TYPE_WATCHER) {
            mItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_list_watcher_item, parent, false);
            return new WatcherViewHolder(mItemView);
        } else if (viewType == ListItems.Type.TYPE_INVITATION) {
            mItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_list_invitation_item, parent, false);
            return new InvitationViewHolder(mItemView);
        } else if (viewType == ListItems.Type.TYPE_HEADER) {
            mItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_list_header, parent, false);
            return new HeaderViewHolder(mItemView);
        } else if (viewType == ListItems.Type.TYPE_TEXT) {
            mItemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_list_text, parent, false);
            return new TextViewHolder(mItemView);
        }
        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Tracer.log(TAG, "onBindViewHolder");
        holder.bindToViewHolder(holder, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        //Tracer.log(TAG, "getItemCount");
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //Tracer.log(TAG, "getItemViewType");
        return mList.get(position).getItemType();
    }



    //-------------------------------------------------------------------------------------
    //NESTED CLASS

    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
        public abstract void bindToViewHolder(ViewHolder viewholder, int position);
    }


    //-----------------------------------
    public class HeaderViewHolder extends ViewHolder {
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
    public class TextViewHolder extends ViewHolder {
        TextView mText;
        TextViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.txt);
        }
        public void bindToViewHolder(ViewHolder viewholder, int position) {
            TextViewHolder holder = (TextViewHolder) viewholder;
            holder.mText.setText(mList.get(position).getItemValue());
        }
    }

    //-----------------------------------
    public class WatcherViewHolder extends ViewHolder {
        TextView name, phone, email;
        ImageView image;
        Context context;
        WatcherViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            image = itemView.findViewById(R.id.imageView);
        }
        public void bindToViewHolder(ViewHolder viewholder, int position) {
            WatcherViewHolder holder = (WatcherViewHolder) viewholder;
            WatcherModel model = mUser.getWatcher(mList.get(position).getItemValue());
            try {
                String n = model.getName();
                String e = model.getEmail();
                String p = model.getPhone();
                if(n.equals("")){
                    n = context.getResources().getString(R.string.empty_name);
                    holder.name.setTypeface(holder.name.getTypeface(), Typeface.ITALIC);
                }
                if(e.equals("")){
                    e = context.getResources().getString(R.string.empty_email);
                    holder.email.setTypeface(holder.email.getTypeface(), Typeface.ITALIC);
                }
                if(p.equals("")){
                    p = context.getResources().getString(R.string.empty_phone);
                    holder.phone.setTypeface(holder.phone.getTypeface(), Typeface.ITALIC);
                }

                holder.name.setText(n);
                holder.phone.setText(p);
                holder.email.setText(e);

                switch(model.getStatus()){
                    case Const.status.ONLINE:
                        image.setImageResource(R.drawable.icon_person_watcher);
                        break;
                    case Const.status.OFFLINE:
                        image.setImageResource(R.drawable.icon_person_offline);
                        break;
                    case Const.status.OCCUPIED:
                        image.setImageResource(R.drawable.icon_person_occupied);
                        break;
                    default:
                        image.setImageResource(R.drawable.icon_person_offline);
                        break;
                }
            }catch(Exception e){
                Tracer.log(TAG, "WatcherViewHolder.exception: ", e);
            }
        }
    }

    //-----------------------------------
    public class InvitationViewHolder extends ViewHolder {
        TextView name, phone;
        Context context;
        InvitationViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
        }
        public void bindToViewHolder(ViewHolder viewholder, int position) {
            InvitationViewHolder holder = (InvitationViewHolder) viewholder;
            InvitationModel model = mUser.getInvitation(mList.get(position).getItemValue());
            try {
                String n = model.getName();
                String p = model.getPhone();
                if(n.equals("")){
                    n = context.getResources().getString(R.string.empty_name);
                    holder.name.setTypeface(holder.name.getTypeface(), Typeface.ITALIC);
                }
                if(p.equals("")){
                    p = context.getResources().getString(R.string.empty_phone);
                    holder.phone.setTypeface(holder.phone.getTypeface(), Typeface.ITALIC);
                }
                holder.name.setText(n);
                holder.phone.setText(p);

            }catch(Exception e){
                Tracer.log(TAG, "InvitationViewHolder.exception: ", e);
            }
        }
    }


}
