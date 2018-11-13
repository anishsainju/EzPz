package com.ajimatech.android.ezpz;

import android.content.Context;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ajimatech.android.ezpz.model.Contact;

public class ContactWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Contact contact;

    public ContactWidgetRemoteViewsFactory(Context context, Contact c) {
        this.mContext = context;
        if (c != null) {
            contact = c;
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
//        if (position == AdapterView.INVALID_POSITION) {
        return null;
//        }
//
//        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_item);
//        remoteViews.setTextViewText(R.id.tv_ingredientName, contact.get(position));
//        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
