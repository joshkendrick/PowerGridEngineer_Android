package com.thirtyonetensoftware.android.powergridengineer.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thirtyonetensoftware.android.powergridengineer.R;
import com.thirtyonetensoftware.android.powergridengineer.model.City;
import com.thirtyonetensoftware.android.powergridengineer.model.Country;
import com.thirtyonetensoftware.android.powergridengineer.model.Path;
import com.thirtyonetensoftware.android.powergridengineer.model.Region;
import com.thirtyonetensoftware.android.powergridengineer.util.FileHelper;

/**
 * DBHelper
 * <p/>
 * Power Grid Engineer
 * 31Ten Software
 * <p/>
 * Author: Josh Kendrick
 * <p/>
 * Portions taken from Danny Remington: http://stackoverflow.com/questions/
 * 513084/how-to-ship-an-android-application-with-a-database
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getCanonicalName();

    private static final String ACTIVE = "active";

    private static final String REGION_TABLE = "region";

    private static final String COUNTRY_TABLE = "country";

    private SQLiteDatabase database;

    private String database_path;

    private String database_name;

    private Context context;

    private boolean createDatabase = false;

    private boolean upgradeDatabase = false;

    public DBHelper(Context context) {
        super(context, context.getResources().getString(R.string.database_name), null,
              context.getResources().getInteger(R.integer.database_version));

        database_name = context.getResources().getString(R.string.database_name);

        this.context = context;
        database_path = context.getDatabasePath(database_name).getAbsolutePath();
    }

    public void openReadableDatabase() {
        database = getReadableDatabase();
    }

    public void openWritableDatabase() {
        database = getWritableDatabase();
    }

    @Override
    public void close() {
        database.close();
    }

    public Set<Path> getPathsFromCity(City source) {
        Set<Path> paths = new HashSet<>();

        Cursor cursor = database.rawQuery("SELECT path.id, cost, city.name, destination, country," +
                                              " region FROM path, city, region, country WHERE path.destination = city.id AND " +
                                              "path.source = ? AND city.country = country.id AND city.region = region.id AND " +
                                              "country.active = 1 AND region.active = 1", new String[] {
            String.valueOf(source
                               .getId())
        });
        while ( cursor.moveToNext() ) {
            paths.add(new Path(cursor.getInt(0), cursor.getInt(1), source,
                               new City(cursor.getInt(3), cursor.getString(2), Country.valueOf(cursor.getInt
                                   (4)), Region.valueOf(cursor.getInt(5)))
            ));
        }
        cursor.close();

        return paths;
    }

    public ArrayList<City> getCities() {
        ArrayList<City> cities = new ArrayList<>();

        Cursor cursor = database.rawQuery("SELECT city.id, city.name, region, country FROM city, " +
                                              "region, country WHERE city.country = country.id AND city.region = region.id AND " +
                                              "country.active = 1 AND region.active = 1 ORDER BY city.name ASC", null);
        while ( cursor.moveToNext() ) {
            cities.add(new City(cursor.getInt(0), cursor.getString(1),
                                Country.valueOf(cursor.getInt(3)), Region.valueOf(cursor.getInt(2))));
        }
        cursor.close();

        return cities;
    }

    public void setRegionActive(Region region, boolean active) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ACTIVE, active);
        database.update(REGION_TABLE, contentValues, "id = ?", new String[] {
            String.valueOf(region
                               .getValue())
        });
    }

    public void setCountryActive(Country country) {
        ContentValues contentValues = new ContentValues();

        // set other countries inactive
        contentValues.put(ACTIVE, false);
        database.update(COUNTRY_TABLE, contentValues, "id != ?", new String[] {
            String.valueOf
                (country.getValue())
        });

        // set this country active
        contentValues.put(ACTIVE, true);
        database.update(COUNTRY_TABLE, contentValues, "id = ?", new String[] {
            String.valueOf
                (country.getValue())
        });
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        initializeDataBase();
        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        initializeDataBase();
        return super.getWritableDatabase();
    }

    /**
     * Upgrade the database in internal storage if it exists but is not current. Create a new empty
     * database in internal storage if it does not exist.
     */
    private void initializeDataBase() {
        /*
         * Creates or updates the database in internal storage if it is needed
         * before opening the database. In all cases opening the database copies
         * the database in internal storage to the cache.
         */
        database = super.getWritableDatabase();

        if ( createDatabase ) {
            /*
             * If the database is created by the copy method, then the creation
             * code needs to go here. This method consists of copying the new
             * database from assets into internal storage and then caching it.
             */
            try {
                /*
                 * Write over the empty data that was created in internal
                 * storage with the one in assets and then cache it.
                 */
                copyDataBase();
            }
            catch ( IOException e ) {
                throw new Error("Error copying database");
            }
        }
        else if ( upgradeDatabase ) {
            Log.d(TAG, "upgrade database was true, onUpgrade will be called. Currently doing nothing.");
        }
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled. This is done by transferring
     * bytestream.
     */
    private void copyDataBase() throws IOException {
        /*
         * Close SQLiteOpenHelper so it will commit the created empty database
         * to internal storage.
         */
        close();

        /*
         * Open the database in the assets folder as the input stream.
         */
        InputStream myInput = context.getAssets().open(database_name);

        /*
         * Open the empty db in internal storage as the output stream.
         */
        OutputStream myOutput = new FileOutputStream(database_path);

        /*
         * Copy over the empty db in internal storage with the database in the
         * assets folder.
         */
        FileHelper.copyFile(myInput, myOutput);

        /*
         * Access the copied database so SQLiteHelper will cache it and mark it
         * as created.
         */
        SQLiteDatabase db = super.getWritableDatabase();
        if ( db != null ) {
            db.close();
        }
    }

    /*
     * This is where the creation of tables and the initial population of the
     * tables should happen, if a database is being created from scratch instead
     * of being copied from the application package assets. Copying a database
     * from the application package assets to internal storage inside this
     * method will result in a corrupted database.
     * <P>
     * NOTE: This method is normally only called when a database has not already
     * been created. When the database has been copied, then this method is
     * called the first time a reference to the database is retrieved after the
     * database is copied since the database last cached by SQLiteOpenHelper is
     * different than the database in internal storage.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * Signal that a new database needs to be copied. The copy process must
         * be performed after the database in the cache has been closed causing
         * it to be committed to internal storage. Otherwise the database in
         * internal storage will not have the same creation timestamp as the one
         * in the cache causing the database in internal storage to be marked as
         * corrupted.
         */
        createDatabase = true;
    }

    /**
     * Called only if version number was changed and the database has already been created. Copying
     * a database from the application package assets to the internal data system inside this
     * method
     * will result in a corrupted database in the internal data system.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
         * Signal that the database needs to be upgraded for the copy method of
         * creation. The copy process must be performed after the database has
         * been opened or the database will be corrupted.
         */
        upgradeDatabase = true;
    }
}
