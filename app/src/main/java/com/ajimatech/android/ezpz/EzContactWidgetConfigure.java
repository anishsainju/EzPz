package com.ajimatech.android.ezpz;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ajimatech.android.ezpz.model.Contact;

import static com.ajimatech.android.ezpz.EzContactWidgetProvider.updateAppWidget;

public class EzContactWidgetConfigure extends AppCompatActivity {

    public static final int PICK_CONTACT_REQUEST = 1;

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public EzContactWidgetConfigure() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        Intent intent1 = new Intent(Intent.ACTION_PICK);
        intent1.setPackage("com.ajimatech.android.ezpz");
        startActivityForResult(intent1, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Contact contact = data.getParcelableExtra(Contact.SELECTED_CONTACT);
                System.out.println("##onActivityResult " + contact.getFullname());
                final Context context = EzContactWidgetConfigure.this;
                // Push widget update to surface with newly set prefix
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName componentName = new ComponentName(context, EzContactWidgetProvider.class);
//                updateAppWidget(context, appWidgetManager, mAppWidgetId, contact);
                updateAppWidget(context, appWidgetManager, appWidgetManager.getAppWidgetIds(componentName), contact);
                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            }
        }
    }
}
