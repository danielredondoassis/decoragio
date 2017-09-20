package com.assis.redondo.daniel.appdoikeda.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.assis.redondo.daniel.appdoikeda.api.model.FrameModel;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "decoragioDB";
    private static final int DATABASE_VERSION = 1;

    private Dao<DbFrameModel, Integer> mFramePricesDao;
    private Dao<DbInsumoModel, Integer> mInsumosDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
        try {
            TableUtils.createTable(connectionSource, DbFrameModel.class);
            TableUtils.createTable(connectionSource, DbInsumoModel.class);

        } catch (final SQLException e) {
            Log.e(TAG, "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            TableUtils.dropTable(connectionSource, DbFrameModel.class, true);
            TableUtils.dropTable(connectionSource, DbInsumoModel.class, true);

            onCreate(sqliteDatabase, connectionSource);
        } catch (final SQLException e) {
            Log.e(TAG, "Unable to upgrade database from version " + oldVer + " to new "
                    + newVer, e);
        }
    }

    public Dao<DbFrameModel,Integer> getFramePrices() throws SQLException {
        if (mFramePricesDao == null) {
            mFramePricesDao = getDao(DbFrameModel.class);
        }
        return mFramePricesDao;
    }

    public Dao<DbInsumoModel,Integer> getInsumos() throws SQLException {
        if (mInsumosDao == null) {
            mInsumosDao = getDao(DbInsumoModel.class);
        }
        return mInsumosDao;
    }


}
