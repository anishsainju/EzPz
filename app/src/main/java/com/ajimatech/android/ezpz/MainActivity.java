package com.ajimatech.android.ezpz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ajimatech.android.ezpz.model.Contact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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

        Contact c1 = new Contact("Anish Sainju");
        Contact c2 = new Contact("Anisha Sainju");
        List<Contact> clist = new ArrayList<>();
        clist.add(c1);
        clist.add(c2);
        mContactAdapter.setContactData(clist);

    }

    private void showErrorMessage() {
        mRvContacts.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMoviesDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRvContacts.setVisibility(View.VISIBLE);
    }
}
