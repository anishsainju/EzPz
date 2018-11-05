package com.ajimatech.android.ezpz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ajimatech.android.ezpz.model.Contact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    private static final String PROTOCOL_FACEBOOK = "Facebook";

    // UI components
    @BindView(R.id.rv_contacts)
    RecyclerView mRvContacts;

    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;


    // Adapter for RecyclerView
    private LinearLayoutManager mLayoutManager;
    private ContactAdapter mContactAdapter;
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
            };
    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION = ContactsContract.Contacts.STARRED + "= ?";
    List<Contact> mContacts = new ArrayList<>();
    //            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
//                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
//                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
    // Defines a variable for the search string
    private String mSearchString = "1";
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = {mSearchString};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRvContacts.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRvContacts.setLayoutManager(mLayoutManager);

        /*
         * The ContactAdapter is responsible for linking our contacts data with the Views that
         * will end up displaying our contact data.
         */
        mContactAdapter = new ContactAdapter(this);
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRvContacts.setAdapter(mContactAdapter);

//        Contact c1 = new Contact("Anish Sainju");
//        Contact c2 = new Contact("Anisha Sainju");
//        List<Contact> clist = new ArrayList<>();
//        clist.add(c1);
//        clist.add(c2);
//        mContactAdapter.setContactData(clist);
        showContacts();
    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            // Initializes the loader
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Permission is required for this app to run", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void showErrorMessage() {
        mRvContacts.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMoviesDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRvContacts.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //mSelectionArgs[0] = "%" + mSearchString + "%";
        // Starts the query
        return new CursorLoader(
                this,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ContentResolver cr = getContentResolver();
        try {
            while (cursor.moveToNext()) {
                String contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String fullname = cursor.getString(cursor.getColumnIndex(Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                        ContactsContract.Contacts.DISPLAY_NAME));
                String photoThumbnailUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                System.out.println("Thumbnail uri " + photoThumbnailUri);

                // Store the info we collected so far
                Contact contact = new Contact(fullname);
                contact.setPhotoUri(photoThumbnailUri);

                //  Get all phone numbers.
                //
                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                while (phones.moveToNext()) {
                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    System.out.println("number phone " + number);
                    int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    switch (type) {
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                            System.out.println("HOME phone " + number);
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                            System.out.println("MOBILE phone " + number);
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                            // do something with the Work number here...
                            break;
                    }
                }

                String[] projection = null;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                        + " AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] selectionArgs = {contactId, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                Cursor im = cr.query(ContactsContract.Data.CONTENT_URI, projection, selection, selectionArgs, null);
                while (im.moveToNext()) {

                    int protocol = im.getInt(im.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                    System.out.println("###PROTOCOL " + protocol);
                    if (protocol == ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM) {
                        String custProtocol = im.getString(im.getColumnIndex(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL));
                        System.out.println("###CUSTOMPROTOCOL " + custProtocol);
                        if (custProtocol.equals(PROTOCOL_FACEBOOK)) {
                            String facebookId = im.getString(im.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                            System.out.println("###DATA " + facebookId);
                            contact.setFacebookId(facebookId);
                            break; // no need to look for more of this kind. If more of this kind is present, they will be ignored.
                        }
                    }
//                    //Types are defined in CommonDataKinds.Im.*
//                    int imppType = im
//                            .getInt(im
//                                    .getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
//                    System.out.println("###TYPE " + String.valueOf(imppType));
//                    //Protocols are defined in CommonDataKinds.Im.*
//                    int imppProtocol = im
//                            .getInt(im
//                                    .getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
//                    System.out.println("###PROTOCOL " + String.valueOf(imppProtocol));
//                    //and in this protocol you can check your custom value
                }


                mContacts.add(contact);
                phones.close();
                im.close();
            }
        } finally {
            cursor.close();
        }
        mContactAdapter.setContactData(mContacts);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContactAdapter.setContactData(null);
    }

}
