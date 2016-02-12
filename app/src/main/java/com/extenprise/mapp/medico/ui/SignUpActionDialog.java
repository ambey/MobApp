package com.extenprise.mapp.medico.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
        dialog.setTitle(R.string.sort_options);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.layout_phone_text, container);

        final EditText phoneText = (EditText) view.findViewById(R.id.phoneText);
        final Button submitButton = (Button) view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneText.getText().toString().trim();
                boolean cancel = false;
                if (TextUtils.isEmpty(phone)) {
                    phoneText.setError(getString(R.string.error_field_required));
                    cancel = true;
                } else if (!Validator.isPhoneValid(phone)) {
                    phoneText.setError(getString(R.string.error_invalid_phone));
                    cancel = true;
                }
                if (cancel) {
                    phoneText.requestFocus();
                    return;
                }

                onDismiss(getDialog());
                Utility.showMessage(context, R.string.msg_sign_up);
            }
        });
        return view;
    }

}
