package com.ajimatech.android.ezpz;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.ajimatech.android.ezpz.model.Contact;

import static com.ajimatech.android.ezpz.ContactWidgetRemoteViewsService.CONTACT_SELECTED;

/**
 * Implementation of App Widget functionality.
 */
public class EzContactWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int[] appWidgetIds, Contact contact) {
        for (int appWidgetId : appWidgetIds) {
//            CharSequence widgetText = "EzPz Contact";
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.contact_widget_provider);

//            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            Intent contactWidgetRemoteViewsServiceIntent = new Intent(context, ContactWidgetRemoteViewsService.class);
            if (contact != null) {
                contactWidgetRemoteViewsServiceIntent.putExtra(CONTACT_SELECTED, contact);
                contactWidgetRemoteViewsServiceIntent.setData(Uri.fromParts("Widget", String.valueOf(appWidgetId + Math.random()), null));
//                views.setTextViewText(R.id.appwidget_text, widgetText + ": " + contact.getFullname());
                System.out.println("##RemoteVuew uimage " + contact.getPhotoUri());
                views.setImageViewUri(R.id.iv_contact_photo, Uri.parse(contact.getPhotoUri()));

                setIBVisibilityAndOnClickPI(contact.getNumberMobile(), views, R.id.ib_call_phone, "tel:" + contact.getNumberMobile(), Intent.ACTION_DIAL, context, null);
                setIBVisibilityAndOnClickPI(contact.getFacebookId(), views, R.id.ib_messenger, context.getString(R.string.uri_messenger) + contact.getFacebookId(), Intent.ACTION_VIEW, context, null);
                setIBVisibilityAndOnClickPI(contact.getNumberGVoice(), views, R.id.ib_google_voice, "tel:" + contact.getNumberGVoice(), Intent.ACTION_DIAL, context, context.getString(R.string.hangouts_dialer_package));
                setIBVisibilityAndOnClickPI(contact.getNumberViber(), views, R.id.ib_viber, "tel:" + contact.getNumberViber(), Intent.ACTION_DIAL, context, context.getString(R.string.viber_package));
                // Location sharing is not available via Widget
            }
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);
//            views.setOnClickPendingIntent(R.id.iv_contact_photo, pendingIntent);
//            views.setRemoteAdapter(R.id.appwidget_listview, contactWidgetRemoteViewsServiceIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private static void setIBVisibilityAndOnClickPI(String data, RemoteViews views, int viewId, String uriString, String intentAction, Context context, String appPackage) {
        if (data != null && !data.isEmpty()) {
            views.setViewVisibility(viewId, View.VISIBLE);
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(intentAction, uri);
            if (appPackage != null) intent.setPackage(appPackage);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(viewId, pendingIntent);
        } else {
            views.setViewVisibility(viewId, View.INVISIBLE);
        }
    }

    public static void sendRefreshBroadcast(Context context, Contact contact) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(CONTACT_SELECTED, contact);
        intent.setComponent(new ComponentName(context, EzContactWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            Contact contact = intent.getParcelableExtra(CONTACT_SELECTED);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, EzContactWidgetProvider.class);
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.appwidget_listview);
            updateAppWidget(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName), contact);
            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName));
        }
    }
}

