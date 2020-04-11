package info.simplyapps.appengine.storage;

import android.provider.BaseColumns;

public abstract class StorageContract {

    public static abstract class TableConfiguration implements BaseColumns {
        public static final String TABLE_NAME = "configuration";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_VALUE = "value";
    }

    public static abstract class TablePurchases implements BaseColumns {
        public static final String TABLE_NAME = "purchases";
        public static final String COLUMN_NAME = "name";
    }

    public static abstract class TableExtensions implements BaseColumns {
        public static final String TABLE_NAME = "extensions";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AMOUNT = "amount";
    }
}
