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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.extenprise.mapp.medico.R;
import com.extenprise.mapp.medico.service.data.ServiceProvider;
import com.extenprise.mapp.medico.service.data.SubscriptionStatus;
import com.extenprise.mapp.medico.util.Utility;
import com.extenprise.mapp.medico.util.Validator;

/**
 * Created by ambey on 12/2/16.
 */
public class SignUpActionDialog extends DialogFragment {
    private Context mContext;
    private EditText mPhoneText;
    private EditText mStdCode;
    private EditText mLandlineText;
    private EditText mFName;
    private EditText mLName;
    private EditText mEmailID;
    private Button mSubmitButton;
    private int mSignupBy;
    private View.OnClickListener mListener;

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setSignupBy(int signupBy) {
        mSignupBy = signupBy;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.layout_phone_text, container);

        mPhoneText = (EditText) view.findViewById(R.id.editTextPhone);
        mStdCode = (EditText) view.findViewById(R.id.editTextStdCode);
        mLandlineText = (EditText) view.findViewById(R.id.editTextLandline);
        mFName = (EditText) view.findViewById(R.id.editTextFName);
        mLName = (EditText) view.findViewById(R.id.editTextLName);
        mEmailID = (EditText) view.findViewById(R.id.editTextEmail);

        View salut = view.findViewById(R.id.textViewSalut);
        View landline = view.findViewById(R.id.layoutLandline);
        if (mSignupBy != R.string.practitioner) {
            landline.setVisibility(View.VISIBLE);
            salut.setVisibility(View.GONE);
            ((LinearLayout.LayoutParams) mFName.getLayoutParams()).weight = 0.5f;
            ((LinearLayout.LayoutParams) mLName.getLayoutParams()).weight = 0.5f;
            mPhoneText.setHint(String.format("%s **", mContext.getString(R.string.mobile_no)));
            mLandlineText.setHint(String.format("%s **", mContext.getString(R.string.landline_no)));
            mStdCode.setHint(String.format("%s *", mContext.getString(R.string.std_code)));
        } else {
            mPhoneText.setHint(String.format("%s *", mContext.getString(R.string.mobile_no)));
        }
        mFName.setHint(String.format("%s *", mContext.getString(R.string.first_name)));
        mLName.setHint(String.format("%s *", mContext.getString(R.string.last_name)));

        mSubmitButton = (Button) view.findViewById(R.id.submitButton);
        mSubmitButton.setOnClickListener(mListener);
        return view;
    }

    public boolean isInputValid() {
        String phone = mPhoneText.getText().toString().trim();
        String stdCode = mStdCode.getText().toString().trim();
        String landline = mLandlineText.getText().toString().trim();
        String emailID = mEmailID.getText().toString().trim();

        View focusView = null;
        boolean cancel = false;

        if (!TextUtils.isEmpty(emailID) && !Validator.isValidEmaillId(emailID)) {
            mEmailID.setError(getString(R.string.error_invalid_email));
            focusView = mEmailID;
            cancel = true;
        }
        if (mSignupBy == R.string.practitioner) {
            if (TextUtils.isEmpty(phone)) {
                mPhoneText.setError(getString(R.string.error_field_required));
                focusView = mPhoneText;
                cancel = true;
            } else if (!Validator.isPhoneValid(phone)) {
                mPhoneText.setError(getString(R.string.error_invalid_phone));
                focusView = mPhoneText;
                cancel = true;
            }
        } else {
            if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(landline)) {
                mPhoneText.setError(getString(R.string.error_mobile_or_landline_req));
                focusView = mPhoneText;
                cancel = true;
            } else if (!TextUtils.isEmpty(landline)) {
                mPhoneText.setError(null);
                if (landline.length() + stdCode.length() != 11 || landline.startsWith("0")) {
                    mLandlineText.setError(getString(R.string.error_invalid_phone));
                    focusView = mLandlineText;
                    cancel = true;
                }
                if (TextUtils.isEmpty(stdCode)) {
                    mStdCode.setError(getString(R.string.error_field_required));
                    focusView = mStdCode;
                    cancel = true;
                } else if (!stdCode.startsWith("0")) {
                    mStdCode.setError(getString(R.string.error_invalid_std_code));
                    focusView = mStdCode;
                    cancel = true;
                }
            }

            if (!TextUtils.isEmpty(phone) && !Validator.isPhoneValid(phone)) {
                mPhoneText.setError(getString(R.string.error_invalid_phone));
                focusView = mPhoneText;
                cancel = true;
            }
        }
            /*if (TextUtils.isEmpty(phone) &&
                    (TextUtils.isEmpty(stdCode) || TextUtils.isEmpty(landline))) {
                mPhoneText.setError(getString(R.string.error_mobile_or_landline_req));
                focusView = mPhoneText;
                cancel = true;
            } else {
                if (!TextUtils.isEmpty(landline)) {
                    mPhoneText.setError(null);
                    if (landline.startsWith("0") || !stdCode.startsWith("0")) {
                        if (landline.startsWith("0")) {
                            mLandlineText.setError(getString(R.string.error_invalid_phone));
                            focusView = mLandlineText;
                            cancel = true;
                        }
                        if (!stdCode.startsWith("0")) {
                            mStdCode.setError(getString(R.string.error_invalid_std_code));
                            focusView = mStdCode;
                            cancel = true;
                        }
                    } else {
                        if (landline.length() + stdCode.length() != 11) {
                            mLandlineText.setError(getString(R.string.error_invalid_phone));
                            focusView = mLandlineText;
                            cancel = true;
                        }
                    }
                }
           */
        if (!Utility.isNameValid(getActivity(), mFName, mLName)) {
            focusView = null;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
        return !cancel;
    }

    public ServiceProvider getInputForm() {
        ServiceProvider sp = new ServiceProvider();
        sp.setfName(mFName.getText().toString().trim());
        sp.setlName(mLName.getText().toString().trim());
        sp.setPhone(mPhoneText.getText().toString().trim());
        sp.setEmailId(mEmailID.getText().toString().trim());
        sp.setStdCode(mStdCode.getText().toString().trim());
        sp.setLandlineNo(mLandlineText.getText().toString().trim());
        sp.setSubscribed(SubscriptionStatus.REQUEST_RECEIVED);
        return sp;
    }

    public void setSubmitListener(View.OnClickListener listener) {
        mListener = listener;
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
