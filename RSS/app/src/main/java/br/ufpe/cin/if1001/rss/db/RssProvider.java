package br.ufpe.cin.if1001.rss.db;

/**
 * Created by LERISSON on 02/05/2018.
 */

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class RssProvider extends ContentProvider {
    private SQLiteRSSHelper db;
    public RssProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        return db.getWritableDatabase().delete(SQLiteRSSHelper.DATABASE_TABLE, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long id = db.getWritableDatabase().insert(SQLiteRSSHelper.DATABASE_TABLE, null, values);
        return Uri.withAppendedPath(RssProviderContract.CONTENT_NEWS_URI, Long.toString(id));


    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        db = SQLiteRSSHelper.getInstance((getContext()));
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Cursor cursor = db.getReadableDatabase().query(SQLiteRSSHelper.DATABASE_TABLE,
                SQLiteRSSHelper.columns,
                selection, selectionArgs, null, null, null);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        return db.getWritableDatabase().update(SQLiteRSSHelper.DATABASE_TABLE, values, selection, selectionArgs);
    }
}
