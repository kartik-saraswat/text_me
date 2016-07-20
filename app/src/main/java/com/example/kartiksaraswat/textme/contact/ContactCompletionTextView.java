package com.example.kartiksaraswat.textme.contact;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kartiksaraswat.textme.R;
import com.tokenautocomplete.TokenCompleteTextView;

/**
 * Created by Kartik Saraswat on 19-07-2016.
 */
public class ContactCompletionTextView extends TokenCompleteTextView<Contact2> {

    public ContactCompletionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        allowDuplicates(false);
        setTokenClickStyle(TokenClickStyle.Delete);
    }

    @Override
    protected View getViewForObject(Contact2 contact2) {
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) l.inflate(R.layout.contact_token_layout, (ViewGroup) getParent(), false);
        view.setText(contact2.toString());
        return view;
    }

    @Override
    protected Contact2 defaultObject(String completionText) {
        return new Contact2(completionText,completionText);
    }
}