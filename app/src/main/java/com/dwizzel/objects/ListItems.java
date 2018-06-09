package com.dwizzel.objects;

/**
 * Created by Dwizzel on 24/11/2017.
 */

public class ListItems {

    //-----------------------------------
    public static class Type {
        public static final int TYPE_HEADER = 1;
        public static final int TYPE_WATCHER = 2;
        public static final int TYPE_INVITATION = 3;
        public static final int TYPE_TEXT = 4;
        public static final int TYPE_WATCHING = 5;
    }

    //-----------------------------------
    public static abstract class Item {
        public abstract int getItemType();
        public abstract String getItemValue();
    }

    //-----------------------------------
    public static class HeaderItem extends Item {
        private String mTitle = "";
        public HeaderItem(String title) {
            super();
            this.mTitle = title;
        }
        @Override
        public int getItemType() {
            return Type.TYPE_HEADER;
        }
        @Override
        public String getItemValue() {
            return mTitle;
        }
    }

    //-----------------------------------
    public static class TextItem extends Item {
        private String mText = "";
        public TextItem(String title) {
            super();
            this.mText = title;
        }
        @Override
        public int getItemType() {
            return Type.TYPE_TEXT;
        }
        @Override
        public String getItemValue() {
            return mText;
        }
    }

    //-----------------------------------
    public static class WatcherItem extends Item {
        private String uid;
        public WatcherItem(String uid) {
            super();
            this.uid = uid;
        }
        @Override
        public int getItemType() {
            return Type.TYPE_WATCHER;
        }
        @Override
        public String getItemValue() {
            return uid;
        }
    }

    //-----------------------------------
    public static class InvitationItem extends Item {
        private String inviteId;
        public InvitationItem(String inviteId) {
            super();
            this.inviteId = inviteId;
        }
        @Override
        public int getItemType() {
            return Type.TYPE_INVITATION;
        }
        @Override
        public String getItemValue() {
            return inviteId;
        }
    }

    //-----------------------------------
    public static class WatchingItem extends Item {
        private String uid;
        public WatchingItem(String uid) {
            super();
            this.uid = uid;
        }
        @Override
        public int getItemType() {
            return Type.TYPE_WATCHING;
        }
        @Override
        public String getItemValue() {
            return uid;
        }
    }

}
