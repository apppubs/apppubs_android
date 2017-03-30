package com.orm;

import static com.orm.util.ManifestHelper.getDatabaseVersion;
import static com.orm.util.ManifestHelper.getDebugEnabled;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.orm.util.ManifestHelper;
import com.orm.util.SugarCursorFactory;

public class SugarDb extends SQLiteOpenHelper {

	private final SchemaGenerator schemaGenerator;
	private SQLiteDatabase sqLiteDatabase;

	public SugarDb(Context context) {
		super(context, ManifestHelper.getDatabaseName(context), new SugarCursorFactory(getDebugEnabled(context)),
				getDatabaseVersion(context));
		schemaGenerator = new SchemaGenerator(context);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		schemaGenerator.createDatabase(sqLiteDatabase);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		schemaGenerator.doUpgrade(sqLiteDatabase, oldVersion, newVersion);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public synchronized SQLiteDatabase getDB() {
		if (this.sqLiteDatabase == null) {
			try{
				this.sqLiteDatabase = getWritableDatabase();
			}catch (SQLiteDatabaseLockedException e){
				e.printStackTrace();
			}
		}

		return this.sqLiteDatabase;
	}

}
