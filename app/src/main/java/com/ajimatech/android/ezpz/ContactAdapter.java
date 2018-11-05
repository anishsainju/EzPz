package com.ajimatech.android.ezpz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajimatech.android.ezpz.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactAdapterViewHolder>  {

    private Context context;
    private List<Contact> mContactsData = new ArrayList<>();

    public ContactAdapter(Context context) {
        this.context = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ContactAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView mTextView;
        final ImageView mImageView;
        ContactAdapterViewHolder(View view) {
            super(view);
            //TODO use butterknife
            mImageView = view.findViewById(R.id.iv_contact_photo);
            mTextView = view.findViewById(R.id.tv_contact_name);
        }
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

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ContactAdapterViewHolder contactAdapterViewHolder, int i) {
        Contact contact = mContactsData.get(i);
        GlideApp.with(context)
                .load(contact.getPhotoUri())
                .placeholder(R.drawable.ic_no_image)
                .into(contactAdapterViewHolder.mImageView);
        contactAdapterViewHolder.mTextView.setText(contact.getFullname() + '\n' + contact.getFacebookId());
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
