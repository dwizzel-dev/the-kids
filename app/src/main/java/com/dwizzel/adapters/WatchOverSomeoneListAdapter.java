package com.dwizzel.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dwizzel.Const;
import com.dwizzel.datamodels.WatchingModel;
import com.dwizzel.objects.ListItems;
import com.dwizzel.objects.UserObject;
import com.dwizzel.thekids.R;
import com.dwizzel.utils.Tracer;

import java.util.ArrayList;

/**
 * Created by Dwizzel on 22/11/2017.
  */

public class WatchOverSomeoneListAdapter extends RecyclerView.Adapter<WatchOverSomeoneListAdapter.ViewHolder> {

    private static final String TAG = "WatchOverSomeoneListAdapter";
    private ArrayList<ListItems.Item> mList;
    private UserObject mUser;
    private Context mContext;

    // fill la liste avec les headers
    public WatchOverSomeoneListAdapter(ArrayList<ListItems.Item> list, Context context) {
        Tracer.log(TAG, "WatchOverSomeoneListAdapter");
        mList = list;
        mUser = UserObject.getInstance();
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WatchOverSomeoneListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Tracer.log(TAG, "onCreateViewHolder");
        View mItemView;
        switch(viewType){
            case ListItems.Type.TYPE_WATCHING:
                mItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_list_watching_item, parent, false);
                return new WatchingViewHolder(mItemView);

            case ListItems.Type.TYPE_HEADER:
                mItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_list_header, parent, false);
                return new HeaderViewHolder(mItemView);

            case ListItems.Type.TYPE_TEXT:
                mItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_list_text, parent, false);
                return new TextViewHolder(mItemView);

            default:
                return null;
        }
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

    public void removeItem(int position){
        //remove form the list
        mList.remove(position);
        //notify item remove from the views
        notifyItemRemoved(position);
        //notify the range of list has changed
        notifyItemRangeChanged(position, mList.size());
    }

    public void addItem(int type, ListItems.Item item, int position){
        mList.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mList.size());
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
    public class WatchingViewHolder extends ViewHolder {
        TextView name, phone, email;
        ImageView image, itemMenuOption;
        Context context;
        WatchingViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            image = itemView.findViewById(R.id.imageView);
            itemMenuOption = itemView.findViewById(R.id.itemMenuOption);
        }
        public void bindToViewHolder(ViewHolder viewholder, final int position) {
            final WatchingViewHolder holder = (WatchingViewHolder) viewholder;
            WatchingModel model = mUser.getWatching(mList.get(position).getItemValue());
            if(model != null) {
                try {

                    String n = model.getName();
                    String e = model.getEmail();
                    String p = model.getPhone();
                    int ir;

                    if (n.equals("")) {
                        n = context.getResources().getString(R.string.empty_name);
                        holder.name.setTypeface(holder.name.getTypeface(), Typeface.ITALIC);
                    }
                    if (e.equals("")) {
                        e = context.getResources().getString(R.string.empty_email);
                        holder.email.setTypeface(holder.email.getTypeface(), Typeface.ITALIC);
                    }
                    if (p.equals("")) {
                        p = context.getResources().getString(R.string.empty_phone);
                        holder.phone.setTypeface(holder.phone.getTypeface(), Typeface.ITALIC);
                    }

                    switch (model.getStatus()) {
                        case Const.status.ONLINE:
                            ir = R.drawable.icon_person_watcher;
                            break;
                        case Const.status.OFFLINE:
                            ir = R.drawable.icon_person_offline;
                            break;
                        case Const.status.OCCUPIED:
                            ir = R.drawable.icon_person_occupied;
                            break;
                        default:
                            ir = R.drawable.icon_person_offline;
                            break;
                    }

                    holder.name.setText(n);
                    holder.phone.setText(p);
                    holder.email.setText(e);
                    holder.image.setImageResource(ir);

                    //le option sub menu
                    holder.itemMenuOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //creating a popup menu
                            PopupMenu popup = new PopupMenu(mContext, holder.itemMenuOption);
                            //inflating menu from xml resource
                            popup.inflate(R.menu.menu_recyclerview_watching_item);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.deleteItem:
                                            Tracer.log(TAG, "onClick: menuDelete");
                                            break;
                                        case R.id.modifyItem:
                                            Tracer.log(TAG, "onClick: menuModify");
                                            break;
                                        default:
                                            break;
                                    }
                                    return false;
                                }
                            });
                            //displaying the popup
                            popup.show();
                        }
                    });

                } catch (Exception e) {
                    Tracer.log(TAG, "WatchingViewHolder.exception: ", e);
                }
            }
        }
    }



}
