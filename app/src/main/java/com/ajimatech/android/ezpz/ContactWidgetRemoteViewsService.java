package com.ajimatech.android.ezpz;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.ajimatech.android.ezpz.model.Contact;

public class ContactWidgetRemoteViewsService extends RemoteViewsService {

    public static final String CONTACT_SELECTED = "contact_selected";
    public static final String CONTACTS_LIST = "contacts_list";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Contact contact = intent.getParcelableExtra(CONTACT_SELECTED);
        return new ContactWidgetRemoteViewsFactory(this.getApplicationContext(), contact);
    }
}
