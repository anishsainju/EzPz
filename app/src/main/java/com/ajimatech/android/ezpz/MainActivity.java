/**
 * Resources used:
 */
package com.ajimatech.android.ezpz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ajimatech.android.ezpz.model.Contact;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    private static final String PROTOCOL_FACEBOOK = "Facebook";
    private static final String GVOICE = "Google Voice";
    private static final String VIBER = "Viber";
    private static final int PERMISSIONS_REQUEST_GET_LOCATION = 11;
    private static final int PERMISSIONS_REQUEST_SEND_SMS = 12;
    private static final int PERMISSIONS_REQUEST_SEND_LOCATION_VIA_SMS = 14;

    // UI components
    @BindView(R.id.rv_contacts)
    RecyclerView mRvContacts;

    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.adView)
    AdView mAdView;

    @BindView(R.id.iv_logo)
    ImageView ivLogo;

    // Adapter for RecyclerView
    private LinearLayoutManager mLayoutManager;
    private ContactAdapter mContactAdapter;
    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
            };
    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION = ContactsContract.Contacts.STARRED + "= ?";
    ArrayList<Contact> mContacts = new ArrayList<>();

    // Defines a variable for the search string
    private String mSearchString = "1";
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = {mSearchString};

    private LocationManager locationManager;

    private String addressToSendLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        new LoadImage(this, ivLogo).execute();
        setSupportActionBar(myToolbar);

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

        permitAndShowContacts();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mContactAdapter.setmOnSendLocationClickedListener(new ContactAdapter.OnSendLocationClickedListener() {
            @Override
            public void onSendLocationClicked(String destinationAddress) {
                addressToSendLocation = destinationAddress;
                permitAndSendLocationViaSMS(destinationAddress);
            }
        });

        mContactAdapter.setmContactAdapterOnClickHandler(new ContactAdapter.ContactAdapterOnClickHandler() {
            @Override
            public void onClick(Contact contact) {
                Intent intent = getIntent();
                if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_PICK)) {
                    Intent intentToSend = new Intent();
                    intentToSend.putExtra(Contact.SELECTED_CONTACT, contact);
                    setResult(RESULT_OK, intentToSend);
                    finish();
                }
            }
        });

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    private void permitAndShowContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//            After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
        // Android version is lesser than 6.0 or the permission is already granted.
        // Initializes the loader
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    private void sendLocation(final String destinationAddress) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_GET_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    String message = "http://maps.google.com/maps?f=q&q=(" + location.getLatitude() + "," + location.getLongitude() + ")";
                    // Now send via SMS
                    sendSMS(destinationAddress, message);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }

            });
        }
    }

    private void sendSMS(String destinationAddress, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(destinationAddress, null, message, null, null);
                Toast.makeText(getApplicationContext(), R.string.sms_sent,
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        R.string.sms_failed,
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void sendLocationViaSMS(String destinationAddress) {
        sendLocation(destinationAddress);
    }

    public void permitAndSendLocationViaSMS(String destinationAddress) {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_LOCATION_VIA_SMS);
//            After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            sendLocationViaSMS(destinationAddress);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_SEND_LOCATION_VIA_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    sendLocationViaSMS(addressToSendLocation);
                } else {
                    Toast.makeText(this, R.string.permission_required_send_location_via_sms, Toast.LENGTH_LONG).show();
                }
            break;
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    permitAndShowContacts();
                } else {
                    Toast.makeText(this, R.string.permission_required_read_contacts, Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showErrorMessage() {
        mRvContacts.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(R.string.no_contacts_to_show_msg);
    }

    private void showContactsDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRvContacts.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
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
        if (cursor != null && !cursor.isClosed()) {
            ContentResolver cr = getContentResolver();
            try {
                while (cursor.moveToNext()) {
                    String contactId =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String fullname = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    String photoThumbnailUri = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));

                    // Store the info we collected so far
                    Contact contact = new Contact(fullname);
                    contact.setPhotoUri(photoThumbnailUri);

                    //  Get all phone numbers.
                    Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        switch (type) {
                            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                contact.setNumberMobile(number);
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                                String label = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                                if (label.equalsIgnoreCase(GVOICE)) {
                                    contact.setNumberGVoice(number);
                                } else if (label.equalsIgnoreCase(VIBER)) {
                                    contact.setNumberViber(number);
                                }
                                break;
                            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
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
                        if (protocol == ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM) {
                            String custProtocol = im.getString(im.getColumnIndex(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL));
                            if (custProtocol.equalsIgnoreCase(PROTOCOL_FACEBOOK)) {
                                String facebookId = im.getString(im.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                                contact.setFacebookId(facebookId);
                                break; // no need to look for more of this kind. If more of this kind is present, they will be ignored.
                            }
                        }
                    }
                    mContacts.add(contact);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(EzContactContract.ContactEntry.COLUMN_FULLNAME, contact.getFullname());
                    contentValues.put(EzContactContract.ContactEntry.COLUMN_PHOTOURI, contact.getPhotoUri());
                    contentValues.put(EzContactContract.ContactEntry.COLUMN_NUMBERMOBILE, contact.getNumberMobile());
                    contentValues.put(EzContactContract.ContactEntry.COLUMN_FACEBOOKID, contact.getFacebookId());
                    contentValues.put(EzContactContract.ContactEntry.COLUMN_NUMBERGVOICE, contact.getNumberGVoice());
                    contentValues.put(EzContactContract.ContactEntry.COLUMN_NUMBERVIBER, contact.getNumberViber());
                    getContentResolver().insert(EzContactContract.ContactEntry.CONTENT_URI, contentValues);

                    phones.close();
                    im.close();
                }
            } finally {
                cursor.close();
            }
            mContactAdapter.setContactData(mContacts);
            if (mContacts == null || mContacts.isEmpty()) {
                showErrorMessage();
            } else {
                showContactsDataView();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContactAdapter.setContactData(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.how_to:
                Intent intent = new Intent(this, HowToGuideActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class LoadImage extends AsyncTask<Void, Void, Bitmap> {
        private ImageView imageView;
        private Context context;

        public LoadImage(Context context, ImageView imageView) {
            this.imageView = imageView;
            this.context = context;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ezpz);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
