package com.ajimatech.android.ezpz;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ajimatech.android.ezpz.model.Contact;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactAdapterViewHolder>  {

    private Context context;
    private List<Contact> mContactsData = new ArrayList<>();
    private OnSendLocationClickedListener mOnSendLocationClickedListener;

    public void setmOnSendLocationClickedListener(OnSendLocationClickedListener mOnSendLocationClickedListener) {
        this.mOnSendLocationClickedListener = mOnSendLocationClickedListener;
    }

    public ContactAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactAdapterViewHolder contactAdapterViewHolder, int i) {
        final Contact contact = mContactsData.get(i);
        GlideApp.with(context)
                .load(contact.getPhotoUri())
                .placeholder(R.drawable.dummy_contact)
                .into(contactAdapterViewHolder.mIvContactPhoto);

        setImageButtonVisibility(contact.getNumberMobile(), contactAdapterViewHolder.mIbCallPhone);
        contactAdapterViewHolder.mIbCallPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + contact.getNumberMobile());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                //gvIntent.setPackage("com.samsung.android.contacts");
//                    gvIntent.setPackage("com.android.phone");
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });

        setImageButtonVisibility(contact.getFacebookId(), contactAdapterViewHolder.mIbMessenger);
        contactAdapterViewHolder.mIbMessenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(context.getString(R.string.uri_messenger) + contact.getFacebookId()); // fb://messaging/" + facebookId);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    context.startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, R.string.install_messenger, Toast.LENGTH_LONG).show();
                }
            }
        });

        setImageButtonVisibility(contact.getNumberGVoice(), contactAdapterViewHolder.mIbGVoice);
        contactAdapterViewHolder.mIbGVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + contact.getNumberGVoice());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                intent.setPackage(context.getString(R.string.hangouts_dialer_package));
                try {
                    context.startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, R.string.install_hangouts_dialer, Toast.LENGTH_LONG).show();
                }
            }
        });

        setImageButtonVisibility(contact.getNumberViber(), contactAdapterViewHolder.mIbViber);
        contactAdapterViewHolder.mIbViber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + contact.getNumberViber());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                intent.setPackage(context.getString(R.string.viber_package));
                try {
                    context.startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, R.string.install_viber, Toast.LENGTH_LONG).show();
                }
            }
        });

        contactAdapterViewHolder.mIbLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mOnSendLocationClickedListener.onSendLocationClicked(contact.getNumberMobile());
                return true;
            }
        });
    }

    public interface OnSendLocationClickedListener {
        void onSendLocationClicked(String destinationAddress);
    }

    @NonNull
    @Override
    public ContactAdapter.ContactAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.contact_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new ContactAdapterViewHolder(view);
    }

    private void setImageButtonVisibility(String data, ImageButton imageButton) {
        if (data != null && !data.isEmpty()) {
            imageButton.setVisibility(View.VISIBLE);
        } else {
            imageButton.setVisibility(View.INVISIBLE);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ContactAdapterViewHolder extends RecyclerView.ViewHolder {

        // UI components
        @BindView(R.id.iv_contact_photo)
        ImageView mIvContactPhoto;
        @BindView(R.id.ib_call_phone)
        ImageButton mIbCallPhone;
        @BindView(R.id.ib_messenger)
        ImageButton mIbMessenger;
        @BindView(R.id.ib_google_voice)
        ImageButton mIbGVoice;
        @BindView(R.id.ib_viber)
        ImageButton mIbViber;
        @BindView(R.id.ib_location)
        ImageButton mIbLocation;

        ContactAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mContactsData)
            return 0;
        return mContactsData.size();
    }

    public void setContactData(List<Contact> contactsData) {
        mContactsData.clear();
        mContactsData.addAll(contactsData);
        notifyDataSetChanged();
    }
}
