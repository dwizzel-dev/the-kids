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
import com.dwizzel.datamodels.InvitationModel;
import com.dwizzel.datamodels.WatcherModel;
import com.dwizzel.objects.ListItems;
import com.dwizzel.objects.UserObject;
import com.dwizzel.thekids.R;
import com.dwizzel.utils.Tracer;

import java.util.ArrayList;

/**
 * Created by Dwizzel on 22/11/2017.
 */

public class WatchOverMeListAdapter extends RecyclerView.Adapter<WatchOverMeListAdapter.ViewHolder>
        implements IRecyclerViewItemClickListener.AdapterClickListener {

    private static final String TAG = "WatchOverMeListAdapter";
    private ArrayList<ListItems.Item> mList;
    private UserObject mUser;
    private Context mContext;
    private IRecyclerViewItemClickListener.ActivityClickListener mClickListener;

    // fill la liste avec les headers
    public WatchOverMeListAdapter(ArrayList<ListItems.Item> list, Context context) {
        Tracer.log(TAG, "WatchOverMeListAdapter");
        mList = list;
        mUser = UserObject.getInstance();
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WatchOverMeListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Tracer.log(TAG, "onCreateViewHolder");
        View mItemView;
        switch(viewType){
            case ListItems.Type.TYPE_WATCHER:
                mItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_list_watcher_item, parent, false);
                return new WatcherViewHolder(mItemView);

            case ListItems.Type.TYPE_INVITATION:
                mItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recycler_list_invitation_item, parent, false);
                return new InvitationViewHolder(mItemView);

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
        Tracer.log(TAG, "onBindViewHolder");
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
        /*
        * on a plusieurs type d'item [title, etxt, watchers, invitations] alors on ne doit pas
        * le mettre a la fin de mList, mais plutot à la fin de son type d'item
        *
        *   Example:
        *   mList[0] = title watchers
        *   mList[1] = watchers items
        *   mList[2] = watchers items
        *   mList[3] = title invitations
        *   mList[4] = invitations items
        *   mList[5] = invitations items
        *
        * 1. Si on rajoute un watchers, il faut le mettre a mList[3] et decaler la liste completement
        * 2. Si on rajoute une invitation on le met simplement a la la fin soi mList[6]
        *
        *
        * */
        mList.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, mList.size());

    }

    public void setClickListener(IRecyclerViewItemClickListener.ActivityClickListener listener){
        mClickListener = listener;
    }

    public void onItemClick(int position, int type){
        if(mClickListener != null){
            mClickListener.onRecycleViewItemClick(position, mList.get(position).getItemValue(), type);
        }
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
        ImageView image, itemMenuOption;
        Context context;
        WatcherViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            email = itemView.findViewById(R.id.email);
            image = itemView.findViewById(R.id.imageView);
            itemMenuOption = itemView.findViewById(R.id.itemMenuOption);
        }
        public void bindToViewHolder(ViewHolder viewholder, final int position) {
            final WatcherViewHolder holder = (WatcherViewHolder) viewholder;
            WatcherModel model = mUser.getWatcher(mList.get(position).getItemValue());
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
                        public void onClick(final View view) {
                            //creating a popup menu
                            PopupMenu popup = new PopupMenu(mContext, holder.itemMenuOption);
                            //inflating menu from xml resource
                            popup.inflate(R.menu.menu_recyclerview_watcher_item);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.deleteItem:
                                            onItemClick(position,
                                                    IRecyclerViewItemClickListener.TYPE_DELETE_ITEM_WATCHER);
                                            break;
                                        case R.id.modifyItem:
                                            onItemClick(position,
                                                    IRecyclerViewItemClickListener.TYPE_MODIFY_ITEM_WATCHER);
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
                    Tracer.log(TAG, "WatcherViewHolder.exception: ", e);
                }
            }
        }
    }

    //-----------------------------------
    public class InvitationViewHolder extends ViewHolder {
        TextView name, phone, code;
        Context context;
        ImageView itemMenuOption;
        InvitationViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            code = itemView.findViewById(R.id.code);
            itemMenuOption = itemView.findViewById(R.id.itemMenuOption);
        }
        public void bindToViewHolder(ViewHolder viewholder, final int position) {
            final InvitationViewHolder holder = (InvitationViewHolder) viewholder;
            InvitationModel model = mUser.getInvitation(mList.get(position).getItemValue());
            if(model != null) {
                try {
                    String n = model.getName();
                    String p = model.getPhone();
                    String c = context.getResources().getString(R.string.code, model.getCode());
                    if (n.equals("")) {
                        n = context.getResources().getString(R.string.empty_name);
                        holder.name.setTypeface(holder.name.getTypeface(), Typeface.ITALIC);
                    }
                    if (p.equals("")) {
                        p = context.getResources().getString(R.string.empty_phone);
                        holder.phone.setTypeface(holder.phone.getTypeface(), Typeface.ITALIC);
                    }

                    holder.name.setText(n);
                    holder.phone.setText(p);
                    holder.code.setText(c);

                    //le option sub menu
                    holder.itemMenuOption.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            //creating a popup menu
                            PopupMenu popup = new PopupMenu(mContext, holder.itemMenuOption);
                            //inflating menu from xml resource
                            popup.inflate(R.menu.menu_recyclerview_invitation_item);
                            //adding click listener
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.deleteItem:
                                            onItemClick(position,
                                                    IRecyclerViewItemClickListener.TYPE_DELETE_ITEM_INVITATION);
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
                    Tracer.log(TAG, "InvitationViewHolder.exception: ", e);
                }
            }
        }
    }


}
