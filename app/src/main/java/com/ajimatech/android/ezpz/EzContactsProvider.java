package com.ajimatech.android.ezpz;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class EzContactsProvider extends ContentProvider {
    private static final int CONTACT = 200;
    private static final int CONTACT_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ContactDBHelper mContactDBHelper;

    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher() {
        String content = EzContactContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, EzContactContract.PATH_CONTACT, CONTACT);
        matcher.addURI(content, EzContactContract.PATH_CONTACT + "/#", CONTACT_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mContactDBHelper = new ContactDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CONTACT:
                return EzContactContract.ContactEntry.CONTENT_TYPE;
            case CONTACT_ID:
                return EzContactContract.ContactEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] mSelectionArgs, String order) {
        final SQLiteDatabase db = mContactDBHelper.getWritableDatabase();
        Cursor retCursor = db.query(uri.toString(), projection, selection, mSelectionArgs, null, null, order);
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mContactDBHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case CONTACT:
                _id = db.insert(EzContactContract.ContactEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = EzContactContract.ContactEntry.buildContactUri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
