package com.ajimatech.android.ezpz;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines all of the tables and URIs needed for the EzContact ContentProvider
 */
public class EzContactContract {

    /**
     * The Content Authority is a name for the entire content provider, similar to the relationship
     * between a domain name and its website. A convenient string to use for content authority is
     * the package name for the app, since it is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.ajimatech.android.ezpz";
    /**
     * A list of possible paths that will be appended to the base URI for each of the different
     * tables.
     */
    public static final String PATH_CONTACT = "contact";
    /**
     * The content authority is used to create the base of all URIs which apps will use to
     * contact this content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Create one class for each table that handles all information regarding the table schema and
     * the URIs related to it.
     */
    public static final class ContactEntry implements BaseColumns {
        // Content URI represents the base location for the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACT).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_CONTACT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CONTACT;

        // Define the table schema
        public static final String TABLE_NAME = "contactTable";
        public static final String COLUMN_FULLNAME = "fullname";
        public static final String COLUMN_PHOTOURI = "photoUri";
        public static final String COLUMN_NUMBERMOBILE = "numberMobile";
        public static final String COLUMN_FACEBOOKID = "facebookId";
        public static final String COLUMN_NUMBERVIBER = "numberViber";
        public static final String COLUMN_NUMBERGVOICE = "numberGVoice";

        // Define a function to build a URI to find a specific contact by it's identifier
        public static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
