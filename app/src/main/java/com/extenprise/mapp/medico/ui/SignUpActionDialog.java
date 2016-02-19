package com.extenprise.mapp.medico.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

/**
 * Created by ambey on 12/2/16.
 */
public class SignUpActionDialog extends DialogFragment {
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.layout_phone_text, container);

        final EditText phoneText = (EditText) view.findViewById(R.id.editTextPhone);
        final EditText fname = (EditText) view.findViewById(R.id.editTextFName);
        final EditText lname = (EditText) view.findViewById(R.id.editTextLName);

        phoneText.setHint(String.format("%s *", context.getString(R.string.mobile_no)));
        fname.setHint(String.format("%s *", context.getString(R.string.first_name)));
        lname.setHint(String.format("%s *", context.getString(R.string.last_name)));

        final Button submitButton = (Button) view.findViewById(R.id.submitButton);
        final EditText firstNameTxt = (EditText) view.findViewById(R.id.editTextFName);
        final EditText lastNameTxt = (EditText) view.findViewById(R.id.editTextLName);
        final EditText emailIDtxt = (EditText) view.findViewById(R.id.editTextEmail);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneText.getText().toString().trim();
                boolean cancel = false;
                View focusView = null;

                EditText[] fields = {firstNameTxt, lastNameTxt, phoneText};
                if (Utility.areEditFieldsEmpty(getActivity(), fields)) {
                    cancel = true;
                }

                if (!Validator.isValidEmaillId(emailIDtxt.getText().toString().trim())) {
                    emailIDtxt.setError(getString(R.string.error_invalid_email));
                    focusView = emailIDtxt;
                    cancel = true;
                }
                if (!Validator.isOnlyAlpha(lastNameTxt.getText().toString().trim())) {
                    lastNameTxt.setError(getString(R.string.error_only_alpha));
                    focusView = lastNameTxt;
                    cancel = true;
                }
                if (!Validator.isOnlyAlpha(firstNameTxt.getText().toString().trim())) {
                    firstNameTxt.setError(getString(R.string.error_only_alpha));
                    focusView = firstNameTxt;
                    cancel = true;
                }
                if (!Validator.isPhoneValid(phone)) {
                    phoneText.setError(getString(R.string.error_invalid_phone));
                    focusView = phoneText;
                    cancel = true;
                }
                if (cancel) {
                    if (focusView != null) {
                        focusView.requestFocus();
                    }
                    return;
                }

                onDismiss(getDialog());
                Utility.showMessage(context, R.string.msg_sign_up);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }
}
