package com.juergenkleck.android.appengine.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.juergenkleck.android.appengine.storage.dto.BasicTable;
import com.juergenkleck.android.appengine.storage.dto.Configuration;
import com.juergenkleck.android.appengine.storage.dto.Extensions;

/**
 * Android library - AppEngine
 *
 * Copyright 2022 by Juergen Kleck <develop@juergenkleck.com>
 */
public abstract class DBDriver extends SQLiteOpenHelper {

    protected static final String TYPE_TEXT = " TEXT";
    protected static final String TYPE_INT = " INTEGER";
    protected static final String COMMA_SEP = ",";

    protected static final String SQL_CREATE_EXTENSIONS =
            "CREATE TABLE " + StorageContract.TableExtensions.TABLE_NAME + " (" +
                    StorageContract.TableExtensions._ID + " INTEGER PRIMARY KEY," +
                    StorageContract.TableExtensions.COLUMN_NAME + TYPE_TEXT + COMMA_SEP +
                    StorageContract.TableExtensions.COLUMN_AMOUNT + TYPE_INT +
                    " );";
    protected static final String SQL_CREATE_CONFIGURATION =
            "CREATE TABLE " + StorageContract.TableConfiguration.TABLE_NAME + " (" +
                    StorageContract.TableConfiguration._ID + " INTEGER PRIMARY KEY," +
                    StorageContract.TableConfiguration.COLUMN_NAME + TYPE_TEXT + COMMA_SEP +
                    StorageContract.TableConfiguration.COLUMN_VALUE + TYPE_TEXT +
                    " );";

    protected static final String SQL_DELETE_EXTENSIONS =
            "DROP TABLE IF EXISTS " + StorageContract.TableExtensions.TABLE_NAME;
    protected static final String SQL_DELETE_CONFIGURATION =
            "DROP TABLE IF EXISTS " + StorageContract.TableConfiguration.TABLE_NAME;

    private static DBDriver self;

    public static void createInstance(DBDriver obj) {
        self = obj;
    }

    public static DBDriver getInstance() {
        return self;
    }

    protected static SQLiteDatabase getDBInstance() {
        return self.getWritableDatabase();
    }

    /**
     * Constructor
     *
     * @param dataBaseName
     * @param dataBaseVersion
     * @param context
     */
    public DBDriver(String dataBaseName, int dataBaseVersion, Context context) {
        super(context, dataBaseName, null, dataBaseVersion);
    }

    public abstract void createTables(SQLiteDatabase db);

    public abstract void upgradeTables(SQLiteDatabase db, int oldVersion, int newVersion);

    public abstract String getExtendedTable(BasicTable data);

    public abstract void storeExtended(StoreData data);

    public abstract void readExtended(StoreData data, SQLiteDatabase db);

    public abstract StoreData createStoreData();

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EXTENSIONS);
        db.execSQL(SQL_CREATE_CONFIGURATION);
        createTables(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        upgradeTables(db, oldVersion, newVersion);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean store(Configuration data) {
        ContentValues values = new ContentValues();
        values.put(StorageContract.TableConfiguration.COLUMN_NAME, data.name);
        values.put(StorageContract.TableConfiguration.COLUMN_VALUE, data.value);
        return persist(data, values, StorageContract.TableConfiguration.TABLE_NAME);
    }

    public boolean store(Extensions data) {
        ContentValues values = new ContentValues();
        values.put(StorageContract.TableExtensions.COLUMN_NAME, data.name);
        values.put(StorageContract.TableExtensions.COLUMN_AMOUNT, data.amount);
        return persist(data, values, StorageContract.TableExtensions.TABLE_NAME);
    }

    public boolean deleteAll(BasicTable data) {
        SQLiteDatabase db = null;
        try {
            db = getDBInstance();
            String table = Configuration.class.isInstance(data) ? StorageContract.TableConfiguration.TABLE_NAME :
                    Extensions.class.isInstance(data) ? StorageContract.TableExtensions.TABLE_NAME :
                            getExtendedTable(data);
            String[] whereArgs = new String[]{"0"};
            return db.delete(table, BaseColumns._ID + ">= ?", whereArgs) > 0;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public boolean delete(BasicTable data) {
        SQLiteDatabase db = null;
        try {
            db = getDBInstance();
            String table = Configuration.class.isInstance(data) ? StorageContract.TableConfiguration.TABLE_NAME :
                    Extensions.class.isInstance(data) ? StorageContract.TableExtensions.TABLE_NAME :
                            getExtendedTable(data);
            String[] whereArgs = new String[]{Long.toString(data.id)};
            return db.delete(table, BaseColumns._ID + "=?", whereArgs) > 0;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Store the data
     *
     * @param data
     * @param values
     * @param tableName
     */
    protected boolean persist(BasicTable data, ContentValues values, String tableName) {
        SQLiteDatabase db = null;
        try {
            db = getDBInstance();
            boolean isUpdate = data.id > 0;
            if (isUpdate) {
                String[] whereArgs = new String[]{Long.toString(data.id)};
                return db.update(tableName, values, BaseColumns._ID + "=?", whereArgs) > 0;
            } else {
                data.id = db.insert(tableName, null, values);
                return data.id > -1;
            }
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * Store the data
     *
     * @param data
     * @param values
     * @param tableName
     */
    protected boolean persist(SQLiteDatabase db, BasicTable data, ContentValues values, String tableName, boolean close) {
        try {
            boolean isUpdate = data.id > 0;
            if (isUpdate) {
                String[] whereArgs = new String[]{Long.toString(data.id)};
                return db.update(tableName, values, BaseColumns._ID + "=?", whereArgs) > 0;
            } else {
                data.id = db.insert(tableName, null, values);
                return data.id > -1;
            }
        } finally {
            if (db != null && close) {
                db.close();
            }
        }
    }

    public void write(StoreData data) {
        // create configuration values
        for (Configuration c : data.configuration) {
            store(c);
        }
        for (Extensions c : data.extensions) {
            store(c);
        }
        storeExtended(data);
    }

    public StoreData read() {
        SQLiteDatabase db = null;
        try {
            StoreData data = createStoreData();
            db = getDBInstance();

            readConfiguration(data, db);
            readExtensions(data, db);
            readExtended(data, db);

            return data;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private void readConfiguration(StoreData data, SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                StorageContract.TableConfiguration._ID,
                StorageContract.TableConfiguration.COLUMN_NAME,
                StorageContract.TableConfiguration.COLUMN_VALUE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = StorageContract.TableConfiguration.COLUMN_NAME + " ASC";
        Cursor c = null;
        try {
            c = openCursor(db, projection, sortOrder, StorageContract.TableConfiguration.TABLE_NAME);

            boolean hasResults = c.moveToFirst();
            while (hasResults) {
                Configuration i = new Configuration();
                i.id = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableConfiguration._ID));
                i.name = c.getString(c.getColumnIndexOrThrow(StorageContract.TableConfiguration.COLUMN_NAME));
                i.value = c.getString(c.getColumnIndexOrThrow(StorageContract.TableConfiguration.COLUMN_VALUE));
                data.configuration.add(i);
                hasResults = c.moveToNext();
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private void readExtensions(StoreData data, SQLiteDatabase db) {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                StorageContract.TableExtensions._ID,
                StorageContract.TableExtensions.COLUMN_NAME,
                StorageContract.TableExtensions.COLUMN_AMOUNT
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = StorageContract.TableExtensions.COLUMN_NAME + " ASC";

        Cursor c = null;
        try {
            c = openCursor(db, projection, sortOrder, StorageContract.TableExtensions.TABLE_NAME);

            boolean hasResults = c.moveToFirst();
            while (hasResults) {
                Extensions i = new Extensions();
                i.id = c.getLong(c.getColumnIndexOrThrow(StorageContract.TableExtensions._ID));
                i.name = c.getString(c.getColumnIndexOrThrow(StorageContract.TableExtensions.COLUMN_NAME));
                i.amount = c.getInt(c.getColumnIndexOrThrow(StorageContract.TableExtensions.COLUMN_AMOUNT));
                data.extensions.add(i);
                hasResults = c.moveToNext();
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    /**
     * Open the cursor to do read and write activity
     *
     * @param db
     * @param projection
     * @param sortOrder
     * @param tableName
     * @return
     */
    protected Cursor openCursor(SQLiteDatabase db, String[] projection, String sortOrder, String tableName) {
        String selection = null;
        String[] selectionArgs = null;
        Cursor c = db.query(
                tableName,      // The table to query
                projection,     // The columns to return
                selection,      // The columns for the WHERE clause
                selectionArgs,  // The values for the WHERE clause
                null,           // don't group the rows
                null,           // don't filter by row groups
                sortOrder       // The sort order
        );
        return c;
    }

    protected String longToString(long[][] value) {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            for (long[] i : value) {
                for (long j : i) {
                    sb.append(Long.toString(j));
                    sb.append(",");
                }
                sb.append(";");
            }
        }
        return sb.toString();
    }

    protected String intToString(int[][] value) {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            for (int[] i : value) {
                for (int j : i) {
                    sb.append(Integer.toString(j));
                    sb.append(",");
                }
                sb.append(";");
            }
        }
        return sb.toString();
    }

    protected String intToString(int[] value) {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            for (int i : value) {
                sb.append(Integer.toString(i));
                sb.append(",");
            }
        }
        return sb.toString();
    }

    protected String boolToString(boolean[][] value) {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            for (boolean[] i : value) {
                for (boolean j : i) {
                    sb.append(Boolean.toString(j));
                    sb.append(",");
                }
                sb.append(";");
            }
        }
        return sb.toString();
    }

    protected String boolToString(boolean[] value) {
        StringBuilder sb = new StringBuilder();
        if (value != null) {
            for (boolean i : value) {
                sb.append(Boolean.toString(i));
                sb.append(",");
            }
        }
        return sb.toString();
    }

    protected int[] stringToInt(String value) {
        String[] vals = value.split(",");
        int[] is = new int[vals.length];
        int j = 0;
        for (String val : vals) {
            if (val.length() > 0) {
                is[j] = Integer.valueOf(val);
            }
            j++;
        }
        return is;
    }

    protected boolean[][] stringToBool(String value) {
        String[] vals = value.split(";");
        boolean[][] is = new boolean[vals.length][];
        int j = 0;
        for (String val : vals) {
            if (val.length() > 0) {
                String[] vls = val.split(",");
                int i = 0;
                is[j] = new boolean[vls.length];
                for (String vl : vls) {
                    is[j][i] = Boolean.valueOf(vl);
                    i++;
                }
            }
            j++;
        }
        return is;
    }

    protected boolean[] stringToSingleBool(String value) {
        String[] vals = value.split(",");
        boolean[] is = new boolean[vals.length];
        int j = 0;
        for (String val : vals) {
            if (val.length() > 0) {
                is[j] = Boolean.valueOf(val);
            }
            j++;
        }
        return is;
    }

    protected int[][] stringToIntInt(String value) {
        String[] vals = value.split(";");
        int[][] is = new int[vals.length][];
        int j = 0;
        for (String val : vals) {
            if (val.length() > 0) {
                String[] vls = val.split(",");
                int i = 0;
                is[j] = new int[vls.length];
                for (String vl : vls) {
                    is[j][i] = Integer.valueOf(vl);
                    i++;
                }
            }
            j++;
        }
        return is;
    }

    protected long[][] stringToLongLong(String value) {
        String[] vals = value.split(";");
        long[][] is = new long[vals.length][];
        int j = 0;
        for (String val : vals) {
            if (val.length() > 0) {
                String[] vls = val.split(",");
                int i = 0;
                is[j] = new long[vls.length];
                for (String vl : vls) {
                    is[j][i] = Long.valueOf(vl);
                    i++;
                }
            }
            j++;
        }
        return is;
    }
}
