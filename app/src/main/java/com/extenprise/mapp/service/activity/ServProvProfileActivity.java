package com.extenprise.mapp.service.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.extenprise.mapp.LoginHolder;
import com.extenprise.mapp.R;
import com.extenprise.mapp.service.data.WorkPlaceListItem;
import com.extenprise.mapp.db.MappContract;
import com.extenprise.mapp.db.MappDbHelper;
import com.extenprise.mapp.net.MappService;
import com.extenprise.mapp.service.data.ServProvHasServPt;
import com.extenprise.mapp.service.data.ServicePoint;
import com.extenprise.mapp.service.data.ServiceProvider;
import com.extenprise.mapp.service.ui.WorkPlaceListAdapter;
import com.extenprise.mapp.util.DBUtil;
import com.extenprise.mapp.util.SearchServProv;
import com.extenprise.mapp.util.Utility;
import com.extenprise.mapp.util.Validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class ServProvProfileActivity extends Activity {

    private int mServiceAction;
    private UpdateHandler mResponseHandler = new UpdateHandler(this);
    private ArrayList<WorkPlaceListItem> mWorkPlaceList;
    private WorkPlaceListItem mSelectedItem;

    private EditText mMobNo, mEmailID, mRegNo;
    private TextView mDocName, workhourLBL;
    private EditText mFname, mLname;
    private RadioGroup mGender;
    private RadioButton mMale, mFemale, mGenderBtn;
    private RelativeLayout mPersonalInfo, mWorkPlaceInfo;
    private ListView listView;
    private View mFormView;
    private View mProgressView;
    private ImageView mImgView;

    private static int RESULT_LOAD_IMG = 1;
    private static int REQUEST_CAMERA = 2;
    private String imgDecodableString;
    private Bitmap mImgCopy;

    private EditText mName;
    private EditText mLoc;
    private Spinner mCity;
    private EditText mPhone1;
    private EditText mPhone2;
    private EditText mEmailIdwork;
    private EditText mConsultFee;
    private Spinner mServPtType;
    private Spinner mServCatagory;
    private Button mStartTime;
    private Button mEndTime;
    private Spinner mSpeciality;
    private EditText mExperience;
    private EditText mQualification;

    private Button mMultiSpinnerDays;
    protected CharSequence[] options = {"All Days", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    protected boolean[] selections = new boolean[options.length];
    //String []selectedDays = new String[_options.length];
    private String selectedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serv_prov_profile_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mFormView = findViewById(R.id.updateServProvform);
        mProgressView = findViewById(R.id.progressView);
        mPersonalInfo = (RelativeLayout) findViewById(R.id.personalInfo);
        mWorkPlaceInfo = (RelativeLayout) findViewById(R.id.workPlaceInfo);

        mMobNo = (EditText) findViewById(R.id.editTextMobNum);
        mEmailID = (EditText) findViewById(R.id.editTextEmail);
        mGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        mRegNo = (EditText) findViewById(R.id.editTextRegNum);
        mDocName = (TextView) findViewById(R.id.textviewDocname);
        mFname = (EditText) findViewById(R.id.textViewFName);
        mLname = (EditText) findViewById(R.id.textViewLName);
        mMale = (RadioButton) findViewById(R.id.radioButtonMale);
        mFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
        mImgView = (ImageView) findViewById(R.id.imageViewDoctor);
        listView = (ListView) findViewById(R.id.workDetailListView);
        //workhourLBL = (TextView) findViewById(R.id.viewWorkHrsLbl);
        //workhourLBL.setClickable(false);

        viewProfile();

        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            mImgView.setImageBitmap(bitmap);
        } else {
            mImgCopy = (Bitmap) getLastNonConfigurationInstance();
            if (mImgCopy != null) {
                mImgView.setImageBitmap(mImgCopy);
            }
        }

        mRegNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkRegNoExistence();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serv_prove_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewProfile() {
        mDocName.setText(LoginHolder.servLoginRef.getfName() + " " +
                LoginHolder.servLoginRef.getlName());
        if(LoginHolder.servLoginRef.getImg() != null) {
            mImgView.setImageBitmap(Utility.getBitmapFromBytes(LoginHolder.servLoginRef.getImg()));
        }
        mFname.setText(LoginHolder.servLoginRef.getfName());
        mLname.setText(LoginHolder.servLoginRef.getlName());
        mMobNo.setText(LoginHolder.servLoginRef.getPhone());
        mEmailID.setText(LoginHolder.servLoginRef.getEmailId());
        mRegNo.setText(LoginHolder.servLoginRef.getRegNo());

        if (LoginHolder.servLoginRef.getGender().equals("Male")) {
            mMale.setSelected(true);
        } else {
            mFemale.setSelected(true);
        }

        Intent intent = getIntent();
        //mWorkPlaceList = intent.getParcelableArrayListExtra("workPlaceList");
        ArrayList<ServProvHasServPt> services = LoginHolder.servLoginRef.getServices();
        for(ServProvHasServPt s : services) {
            WorkPlaceListItem wp = new WorkPlaceListItem();

            wp.setPincode(s.getServicePoint().getPincode());
            wp.setSpeciality(s.getService().getSpeciality());
            wp.setLocation(s.getServicePoint().getLocation());
            wp.setQualification(LoginHolder.servLoginRef.getQualification());
            wp.setExperience(s.getExperience());
            wp.setServCatagory(s.getService().getCategory());
            wp.setAltPhone(s.getServicePoint().getAltPhone());
            wp.setCity(s.getServicePoint().getCity().getCity());
            wp.setConsultFee(s.getConsultFee());
            wp.setEmailId(s.getServicePoint().getEmailId());
            wp.setEndTime(s.getEndTime());
            wp.setStartTime(s.getStartTime());
            wp.setServPointType(s.getServPointType());
            wp.setName(s.getServicePoint().getName());
            wp.setPhone(s.getServicePoint().getPhone());
            wp.setWorkingDays(s.getWorkingDays());
            wp.setSignInData(LoginHolder.servLoginRef.getSignInData());

            mWorkPlaceList.add(wp);
        }
        //mWorkPlaceList = LoginHolder.servLoginRef.getServices();
        //TODO
        ArrayAdapter<WorkPlaceListItem> adapter = new WorkPlaceListAdapter(this,
                R.layout.activity_servprov_wrkdetail_list, mWorkPlaceList);
        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    private void showWorkPlaceList() {

        final Cursor cursor = SearchServProv.getCursor();

        String[] values = new String[]{
                MappContract.Service.COLUMN_NAME_SERVICE_CATAGORY,
                MappContract.Service.COLUMN_NAME_SERVICE_NAME,
                MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION,
                MappContract.ServProvHasServPt.COLUMN_NAME_EXP,
                MappContract.ServicePoint.COLUMN_NAME_NAME,
                MappContract.ServProvHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE,
                MappContract.ServicePoint.COLUMN_NAME_LOCATION,
                MappContract.ServicePoint.COLUMN_NAME_ID_CITY,
                MappContract.ServicePoint.COLUMN_NAME_PHONE,
                MappContract.ServicePoint.COLUMN_NAME_ALT_PHONE,
                MappContract.ServicePoint.COLUMN_NAME_EMAIL_ID,
                MappContract.ServProvHasServPt.COLUMN_NAME_START_TIME,
                MappContract.ServProvHasServPt.COLUMN_NAME_END_TIME,
                MappContract.ServProvHasServPt.COLUMN_NAME_WORKING_DAYS,
                MappContract.ServProvHasServPt.COLUMN_NAME_CONSULTATION_FEE
        };

        int[] viewIds = new int[]{
                R.id.spinServiceProvCategory,
                R.id.editTextSpeciality,
                R.id.editTextQualification,
                R.id.editTextExperience,
                R.id.editTextName,
                R.id.viewWorkPlaceType,
                R.id.editTextLoc,
                R.id.editTextCity,
                R.id.editTextPhone1,
                R.id.editTextPhone2,
                R.id.editTextEmail,
                R.id.buttonStartTime,
                R.id.buttonEndTime,
                R.id.editTextWeeklyOff,
                R.id.editTextConsultationFees
        };

        //ArrayAdapter arrayAdapter = new ArrayAdapter();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.activity_servprov_wrkdetail_list,
                cursor,
                values,
                viewIds, 0) {
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                cursor.moveToPosition(position);

                TextView mTextViewEdit = (TextView) findViewById(R.id.textViewEditWrkDetail);
                TextView mViewEdit = (TextView) findViewById(R.id.viewEditWrkDetail);

                mTextViewEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editWorkPlaceInfo(v);
                    }
                });

                return view;
            }
        };


        listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }

        });*/
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.workDetailListView) {
            menu.setHeaderTitle("Item Operations");
            menu.add(0, v.getId(), 0, "Edit");
            menu.add(0, v.getId(), 0, "Remove");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle() == "Edit") {
            editWorkPlaceInfo(item.getActionView());
            return true;
        } else if (item.getTitle() == "Remove") {
            //ArrayAdapter adapter = (ArrayAdapter) listView.getAdapter();
            //this.resultsList.remove((int) info.id);
            //adapter.removeItem(adapter.getItem(position));
            //adapter.remove(item);
            //item.getActionView().setVisibility(View.GONE);
            mServiceAction = MappService.REMOVE_WORK_PLACE;
            mSelectedItem = mWorkPlaceList.get(item.getItemId());
            performAction();
            //adapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }


    public void showtimeFields(View view) {
        int[] buttonIds = new int[]{
                R.id.buttonStartTime,
                R.id.buttonEndTime,
        };
        for (int buttonId : buttonIds) {
            Button btn = (Button) findViewById(buttonId);
            if (btn.getVisibility() == View.GONE) {
                btn.setVisibility(View.VISIBLE);
            } else {
                btn.setVisibility(View.GONE);
            }
        }
    }

    public void editPersonalInfo(View view) {
        boolean set = false;
        if (!mFname.isEnabled()) {
            set = true;
        }
        mFname.setEnabled(set);
        mLname.setEnabled(set);
        //mMobNo.setEnabled(true);
        mEmailID.setEnabled(set);
        mRegNo.setEnabled(set);
        mMale.setEnabled(set);
        mFemale.setEnabled(set);

        openPersonalInfo(view);
    }

    public void editWorkPlaceInfo(View v) {
        boolean set = true;
        EditText nm = (EditText) v.findViewById(R.id.editTextName);
        if(nm.isEnabled()) {
            set = false;
        }
        int[] editTxtIds = new int[]{
                R.id.editTextQualification,
                R.id.editTextExperience,
                R.id.editTextName,
                R.id.editTextLoc,
                R.id.editTextPhone1,
                R.id.editTextPhone2,
                R.id.editTextEmail,
                R.id.editTextConsultationFees
        };
        for (int editTxtId : editTxtIds) {
            EditText txt = (EditText) v.findViewById(editTxtId);
            txt.setEnabled(set);
        }

        int[] buttonIds = new int[]{
                R.id.buttonStartTime,
                R.id.buttonEndTime,
                R.id.editTextWeeklyOff
        };
        for (int buttonId : buttonIds) {
            Button btn = (Button) v.findViewById(buttonId);
            btn.setEnabled(set);
        }

        int[] spinnerIds = new int[]{
                R.id.spinServiceProvCategory,
                R.id.editTextSpeciality,
                R.id.viewWorkPlaceType,
                R.id.editTextCity
        };
        for (int spinnerId : spinnerIds) {
            Button btn = (Button) v.findViewById(spinnerId);
            btn.setEnabled(set);
        }
    }

    public void openPersonalInfo(View view) {
        if (mPersonalInfo.getVisibility() == View.VISIBLE) {
            mPersonalInfo.setVisibility(View.GONE);
        } else {
            mPersonalInfo.setVisibility(View.VISIBLE);
            if (mWorkPlaceInfo.getVisibility() == View.VISIBLE) {
                mWorkPlaceInfo.setVisibility(View.GONE);
            }
        }
    }

    public void openWorkPlaceInfo(View view) {
        if (mWorkPlaceInfo.getVisibility() == View.VISIBLE) {
            mWorkPlaceInfo.setVisibility(View.GONE);
        } else {
            mWorkPlaceInfo.setVisibility(View.VISIBLE);
            if (mPersonalInfo.getVisibility() == View.VISIBLE) {
                mPersonalInfo.setVisibility(View.GONE);
            }
        }
    }

    public void removeWorkPlace(View view) {
    }

    public void timePicker(View view, final Button button) {
        // Process to get Current Time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        // Display Selected time in textbox
                        button.setText(String.format("%02d:%02d", hourOfDay, minute));
                    }
                }, hour, minute, false);
        tpd.show();
    }

    private AlertDialog openSpecDialog() {
        final EditText txtSpec = new EditText(this);
        txtSpec.setHint("Add Speciality");

        return new AlertDialog.Builder(this)
                .setTitle("Add Speciality")
                .setView(txtSpec)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newSpec = txtSpec.getText().toString();
                        ArrayList<String> specs = new ArrayList<String>();
                        specs.add(newSpec);
                        DBUtil.setNewSpec(getApplicationContext(), specs, mSpeciality);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }



    ///////////////////////////Multi spinner..../////////////////////////////

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick(View view) {
            if (!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
                setupSelection();
            }
            showDialog(0);
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (!mMultiSpinnerDays.getText().equals(getString(R.string.select_days))) {
            setupSelection();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return new AlertDialog.Builder(this)
                .setTitle("Available Days")
                .setMultiChoiceItems(options, selections, new DialogSelectionClickHandler())
                .setPositiveButton("OK", new DialogButtonClickHandler())
                .create();
    }

    public class DialogSelectionClickHandler implements DialogInterface.OnMultiChoiceClickListener {
        public void onClick(DialogInterface dialog, int clicked, boolean selected) {
            if (options[clicked].toString().equalsIgnoreCase("All Days")) {
                for (CharSequence option : options) {
                    Log.i("ME", option + " selected: " + selected);
                }
            } else {
                Log.i("ME", options[clicked] + " selected: " + selected);
            }
        }
    }

    public class DialogButtonClickHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int clicked) {
            switch (clicked) {
                case DialogInterface.BUTTON_POSITIVE:
                    printSelectedDays();
                    mMultiSpinnerDays.setText(selectedDays);
                    break;
            }
        }
    }

    protected void printSelectedDays() {
        if (selections[0]) {
            setupAllDaysSelected();
            return;
        }
        int i = 1;
        selectedDays = getString(R.string.select_days);
        for (; i < options.length; i++) {
            Log.i("ME", options[i] + " selected: " + selections[i]);

            if (selections[i]) {
                selectedDays = options[i++].toString();
                break;
            }
        }
        for (; i < options.length; i++) {
            Log.i("ME", options[i] + " selected: " + selections[i]);

            if (selections[i]) {
                selectedDays += "," + options[i].toString();
            }
        }
    }

    private void setupSelection() {
        String[] selectedDays = mMultiSpinnerDays.getText().toString().split(",");
        selections[0] = false;
        for (String d : selectedDays) {
            selections[getDayIndex(d)] = true;
        }
    }

    private int getDayIndex(String day) {
        for (int i = 0; i < options.length; i++) {
            if (day.equals(options[i])) {
                return i;
            }
        }
        return 0;
    }

    private void setupAllDaysSelected() {
        selections[0] = false;
        selectedDays = options[1].toString();
        for (int i = 2; i < options.length; i++) {
            selectedDays += "," + options[i];
        }
    }

    ////////////////////////////////////////////////////////////////////////////




    /////////////////////////////Image Upload/////////////////////////////

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        BitmapDrawable drawable = (BitmapDrawable) mImgView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            outState.putParcelable("image", bitmap);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return mImgCopy;
    }

    public void changeImage(View view) {

        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Upload Image ");
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(intent, REQUEST_CAMERA);
                        //startImageCapture();
                        break;

                    case 1:
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
                        break;

                    case 2:
                        dialog.dismiss();
                        break;
                }
            }

        });
        dialogBuilder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == RESULT_OK) {
                if (requestCode == RESULT_LOAD_IMG
                        && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                    // Set the Image in ImageView after decoding the String
                    mImgView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));

                } else if (requestCode == REQUEST_CAMERA) {
                    File f = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    try {
                        Bitmap bm;
                        BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                        bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                                btmapOptions);

                        // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
                        mImgView.setImageBitmap(bm);

                        String path = android.os.Environment
                                .getExternalStorageDirectory()
                                + File.separator
                                + "Phoenix" + File.separator + "default";
                        f.delete();
                        OutputStream fOut = null;
                        File file = new File(path, String.valueOf(System
                                .currentTimeMillis()) + ".jpg");
                        try {
                            fOut = new FileOutputStream(file);
                            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                            fOut.flush();
                            fOut.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    ////////////////////////////////////////////////////////////////////////





    ///////////////////////////////Add New Work Place Details/////////////////////////////////

    public void addNewWorkPlace(View view) {
        /*SimpleCursorAdapter adapter = (SimpleCursorAdapter) listView.getAdapter();
        adapter.changeCursorAndColumns( SearchServProv.getCursor(), new String[]{}, new int[]{});

        *//*MatrixCursor extras = new MatrixCursor(new String[] { "_id", "title" });
        extras.addRow(new String[] { "-1", "New Template" });
        extras.addRow(new String[] { "-2", "Empty Template" });
        Cursor[] cursors = { extras, SearchServProv.getCursor() };
        Cursor extendedCursor = new MergeCursor(cursors);
        adapter.notifyDataSetChanged();
*/
        addWorkPlaceScreen();
    }

    private AlertDialog addWorkPlaceScreen() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_servprov_wrkdetail_list, null);

        mName = (EditText) dialogView.findViewById(R.id.editTextName);
        mLoc = (EditText) dialogView.findViewById(R.id.editTextLoc);
        mPhone1 = (EditText) dialogView.findViewById(R.id.editTextPhone1);
        mPhone2 = (EditText) dialogView.findViewById(R.id.editTextPhone2);
        mEmailIdwork = (EditText) dialogView.findViewById(R.id.editTextEmail);
        mConsultFee = (EditText) dialogView.findViewById(R.id.editTextConsultationFees);
        mServPtType = (Spinner) dialogView.findViewById(R.id.viewWorkPlaceType);
        mCity = (Spinner) dialogView.findViewById(R.id.editTextCity);
        mStartTime = (Button) dialogView.findViewById(R.id.buttonStartTime);
        mEndTime = (Button) dialogView.findViewById(R.id.buttonEndTime);
        mSpeciality = (Spinner) dialogView.findViewById(R.id.editTextSpeciality);
        mExperience = (EditText) dialogView.findViewById(R.id.editTextExperience);
        mQualification = (EditText) dialogView.findViewById(R.id.editTextQualification);
        mMultiSpinnerDays = (Button) dialogView.findViewById(R.id.editTextWeeklyOff);
        mServCatagory = (Spinner) dialogView.findViewById(R.id.spinServiceProvCategory);
        workhourLBL = (TextView) dialogView.findViewById(R.id.viewWorkHrsLbl);

        workhourLBL.setClickable(false);
        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(v, mStartTime);
            }
        });
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(v, mEndTime);
            }
        });
        mMultiSpinnerDays.setOnClickListener(new ButtonClickHandler());
        mServCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String servCategory = mServCatagory.getSelectedItem().toString();
                MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
                DBUtil.setSpecOfCategory(getApplicationContext(), dbHelper, servCategory, mSpeciality);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        mSpeciality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String spec = mSpeciality.getSelectedItem().toString();
                if (spec.equals("Other")) {
                    openSpecDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        return new AlertDialog.Builder(this)
                .setTitle("Add New Work Place")
                .setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (isValidWorkPlace()) {
                            addWorkPlace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private boolean isValidWorkPlace() {
        boolean valid = true;
        View focusView = null;

        String category = mServCatagory.getSelectedItem().toString();
        if (category.equalsIgnoreCase("Select Category")) {
            //Utility.showAlert(this, "", "Please select service category.");
            View selectedView = mServCatagory.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                String errorString = selectedTextView.getResources().getString(R.string.error_field_required);
                selectedTextView.setError(errorString);
            }
            focusView = mServCatagory;
            valid = false;
        }

        String spec = mSpeciality.getSelectedItem().toString();
        if (spec.equalsIgnoreCase("Select Speciality") || spec.equals("Other")) {
            //Utility.showAlert(this, "", "Please select speciality.");
            View selectedView = mSpeciality.getSelectedView();
            if (selectedView != null && selectedView instanceof TextView) {
                TextView selectedTextView = (TextView) selectedView;
                String errorString = selectedTextView.getResources().getString(R.string.error_field_required);
                selectedTextView.setError(errorString);
            }
            focusView = mSpeciality;
            valid = false;
        }

        String exp = mExperience.getText().toString().trim();
        if (TextUtils.isEmpty(exp)) {
            mExperience.setError(getString(R.string.error_field_required));
            focusView = mExperience;
            valid = false;
        } else {
            double exp2 = Double.parseDouble(mExperience.getText().toString());
            if (exp2 < 0 || exp2 > 99) {
                mExperience.setError(getString(R.string.error_invalid_experience));
                focusView = mExperience;
                valid = false;
            }
        }
        String qualification = mQualification.getText().toString().trim();
        if (TextUtils.isEmpty(qualification)) {
            mQualification.setError(getString(R.string.error_field_required));
            focusView = mQualification;
            valid = false;
        }

        String name = mName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            mName.setError(getString(R.string.error_field_required));
            focusView = mName;
            valid = false;
        }
        String location = mLoc.getText().toString().trim();
        if (TextUtils.isEmpty(location)) {
            mLoc.setError(getString(R.string.error_field_required));
            focusView = mLoc;
            valid = false;
        }
        String phone1 = mPhone1.getText().toString().trim();
        if (TextUtils.isEmpty(phone1)) {
            mPhone1.setError(getString(R.string.error_field_required));
            focusView = mPhone1;
            valid = false;
        } else if (!Validator.isPhoneValid(phone1)) {
            mPhone1.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone1;
            valid = false;
        }
        String phone2 = mPhone2.getText().toString().trim();
        if (!TextUtils.isEmpty(phone2) && !Validator.isPhoneValid(phone2)) {
            mPhone2.setError(getString(R.string.error_invalid_phone));
            focusView = mPhone2;
            valid = false;
        }
        String email = mEmailIdwork.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !Validator.isEmailValid(mEmailIdwork.getText().toString())) {
            mEmailIdwork.setError(getString(R.string.error_invalid_email));
            focusView = mEmailIdwork;
            valid = false;
        }
        if (mEndTime.getText().toString().equals(getString(R.string.end_time))) {
            mEndTime.setError(getString(R.string.error_field_required));
            focusView = mEndTime;
            valid = false;
        }
        if (mStartTime.getText().toString().equals(getString(R.string.start_time))) {
            mStartTime.setError(getString(R.string.error_field_required));
            focusView = mStartTime;
            valid = false;
        }
        if (!(mEndTime.getText().toString().equals(getString(R.string.end_time))) &&
                !(mStartTime.getText().toString().equals(getString(R.string.start_time)))) {
            if (Utility.getMinutes(mStartTime.getText().toString()) >= Utility.getMinutes(mEndTime.getText().toString())) {
                mEndTime.setError(getString(R.string.error_endtime));
                focusView = mEndTime;
                valid = false;
            }
        }
        String days = mMultiSpinnerDays.getText().toString();
        if (days.equalsIgnoreCase("Select Days")) {
            mMultiSpinnerDays.setError(getString(R.string.error_field_required));
            focusView = mMultiSpinnerDays;
            valid = false;
        }
        String cosultFee = mConsultFee.getText().toString().trim();
        if (TextUtils.isEmpty(cosultFee)) {
            mConsultFee.setError(getString(R.string.error_field_required));
            focusView = mConsultFee;
            valid = false;
        }


        if (focusView != null) {
            focusView.requestFocus();
        }
        return valid;
    }

    private void addWorkPlace() {
        WorkPlaceListItem wpt = new WorkPlaceListItem();

        wpt.setName(mName.getText().toString().trim());
        wpt.setLocation(mLoc.getText().toString().trim());
        wpt.setCity(mCity.getSelectedItem().toString().trim());
        wpt.setPhone(mPhone1.getText().toString().trim());
        wpt.setAltPhone(mPhone2.getText().toString().trim());
        wpt.setEmailId(mEmailIdwork.getText().toString().trim());
        wpt.setStartTime(Utility.getMinutes(mStartTime.getText().toString()));
        wpt.setEndTime(Utility.getMinutes(mEndTime.getText().toString()));
        wpt.setWorkingDays(mMultiSpinnerDays.getText().toString());
        wpt.setConsultFee(Float.parseFloat(mConsultFee.getText().toString().trim()));
        wpt.setServCatagory(mServCatagory.getSelectedItem().toString());
        wpt.setSpeciality(mSpeciality.getSelectedItem().toString());
        wpt.setServPointType(mServPtType.getSelectedItem().toString());
        wpt.setConsultFee(Float.parseFloat(mConsultFee.getText().toString().trim()));
        wpt.setExperience(Float.parseFloat(mExperience.getText().toString().trim()));
        wpt.setQualification(mQualification.getText().toString().trim());

        mSelectedItem = wpt;
        mServiceAction = MappService.ADD_WORK_PLACE;
        performAction();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////





    //////////////////////////////////////////Update Profile///////////////////////////////////

    public void updateProfile(View view) {
        if(!isValidInput()) {
            return;
        }
        ServiceProvider sp = new ServiceProvider();
        sp.setImg(Utility.getBytesFromBitmap(mImgView.getDrawingCache()));
        sp.setfName(mFname.getText().toString());
        sp.setlName(mLname.getText().toString());
        sp.setPhone(mEmailID.getText().toString());
        sp.setGender(mGenderBtn.getText().toString());
        sp.setRegNo(mRegNo.getText().toString());

        for(WorkPlaceListItem wp: mWorkPlaceList) {
            LoginHolder.servLoginRef.setQualification(wp.getQualification());
            //LoginHolder.servLoginRef.setGender(mGender.getSelectedItem().toString());

            ServicePoint spt = new ServicePoint();
            ServProvHasServPt spsspt = new ServProvHasServPt();

            spt.setName(wp.getName());
            spt.setLocation(wp.getLocation());
            spt.getCity().setCity(wp.getCity());
            spt.setPhone(wp.getPhone());
            spt.setAltPhone(wp.getAltPhone());
            spt.setEmailId(wp.getEmailId());

            spsspt.getService().setSpeciality(wp.getSpeciality());
            spsspt.setExperience(wp.getExperience());
            spsspt.setServPointType(wp.getServPointType());
            spsspt.setStartTime(wp.getStartTime());
            spsspt.setEndTime(wp.getEndTime());
            spsspt.setWorkingDays(wp.getWorkingDays());
            spsspt.setConsultFee(wp.getConsultFee());
            spsspt.setServicePoint(spt);

            LoginHolder.servLoginRef.addServProvHasServPt(spsspt);
            int count = LoginHolder.servLoginRef.getServiceCount() + 1;
        }

        mServiceAction = MappService.DO_UPDATE;
        LoginHolder.servLoginRef = sp;
        performAction();
        /*SaveServiceData task = new SaveServiceData(this);
        task.execute((Void) null);*/
    }

    public boolean isValidInput() {
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mLname.getText().toString())) {
            mLname.setError(getString(R.string.error_field_required));
            focusView = mLname;
            cancel = true;
        }
        if (TextUtils.isEmpty(mFname.getText().toString())) {
            mFname.setError(getString(R.string.error_field_required));
            focusView = mFname;
            cancel = true;
        }

        if (TextUtils.isEmpty(mEmailID.getText().toString())) {
            mEmailID.setError(getString(R.string.error_field_required));
            focusView = mEmailID;
            cancel = true;
        }

        if(Validator.isEmailValid(mEmailID.getText().toString().trim())) {
            mEmailID.setError(getString(R.string.error_invalid_email));
            focusView = mEmailID;
            cancel = true;
        }

        int genderID = mGender.getCheckedRadioButtonId();
        if (genderID == -1) {
            mFemale.setError("Please select Gender.");
            focusView = mFemale;
            cancel = true;
        } else {
            mGenderBtn = (RadioButton)findViewById(genderID);
        }

        String regNo = mRegNo.getText().toString();
        if (TextUtils.isEmpty(regNo)) {
            mRegNo.setError(getString(R.string.error_field_required));
            focusView = mRegNo;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    public void checkRegNoExistence() {
        mServiceAction = MappService.DO_REG_NO_CHECK;
        performAction();
    }

    class SaveServiceData extends AsyncTask<Void, Void, Void> {

        private Activity myActivity;

        public SaveServiceData(Activity activity) {
            myActivity = activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServiceProvider sp = LoginHolder.servLoginRef; // TO DO
            ArrayList<ServProvHasServPt> spsList = sp.getServices();

            if (spsList == null) {
                return null;
            }
            String where = MappContract.ServiceProvider.COLUMN_NAME_CELLPHONE + " = ? ";
            String[] selectionArgs = {
                    "" + LoginHolder.servLoginRef.getPhone()
            };

            MappDbHelper dbHelper = new MappDbHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(MappContract.ServiceProvider.COLUMN_NAME_FNAME, mFname.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_LNAME, mLname.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_QUALIFICATION, mQualification.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_GENDER, mGenderBtn.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_REGISTRATION_NUMBER, mRegNo.getText().toString());
            values.put(MappContract.ServiceProvider.COLUMN_NAME_EMAIL_ID, mEmailID.getText().toString());

            db.update(MappContract.ServiceProvider.TABLE_NAME, values, where, selectionArgs);

            for (ServProvHasServPt sps : spsList) {
                ServicePoint spt = sps.getServicePoint();

                values = new ContentValues();
                values.put(MappContract.ServicePoint.COLUMN_NAME_NAME, spt.getName());
                values.put(MappContract.ServicePoint.COLUMN_NAME_LOCATION, spt.getLocation());
                values.put(MappContract.ServicePoint.COLUMN_NAME_PHONE, spt.getPhone());
                values.put(MappContract.ServicePoint.COLUMN_NAME_ID_CITY, spt.getCity().getIdCity());

                long sptId = db.insert(MappContract.ServicePoint.TABLE_NAME, null, values);


                values = new ContentValues();
                values.put(MappContract.Service.COLUMN_NAME_SERVICE_NAME, sps.getService().getSpeciality());
                values.put(MappContract.Service.COLUMN_NAME_SERVICE_CATAGORY, sps.getService().getCategory());
                long idService = db.insert(MappContract.Service.TABLE_NAME, null, values);

                values = new ContentValues();
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_ID_SERVICE, idService);
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_EXP, sps.getExperience());

                try {
                    values.put(MappContract.ServProvHasServPt.COLUMN_NAME_START_TIME, sps.getStartTime());
                    values.put(MappContract.ServProvHasServPt.COLUMN_NAME_END_TIME, sps.getEndTime());
                } catch (Exception x) {
                    x.printStackTrace();
                }
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_SERVICE_POINT_TYPE, sps.getServPointType());
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_CONSULTATION_FEE, sps.getConsultFee());
                values.put(MappContract.ServProvHasServPt.COLUMN_NAME_WORKING_DAYS, sps.getWorkingDays());

                where = MappContract.ServProvHasServPt.COLUMN_NAME_SERV_PROV_PHONE + " = ? and " +
                        MappContract.ServProvHasServPt.COLUMN_NAME_ID_SERV_PT + " = ? ";
                String[] args = {
                        "" + sp.getPhone(), "" + sptId
                };
                db.update(MappContract.ServProvHasServPt.TABLE_NAME, values, where, args);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utility.showRegistrationAlert(myActivity, "", "Profile updated.");
            Utility.showProgress(myActivity, mFormView, mProgressView, false);
            //Intent intent = new Intent(myActivity, LoginActivity.class);
            //startActivity(intent);
        }

        @Override
        protected void onCancelled() {
            Utility.showProgress(myActivity, mFormView, mProgressView, false);
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////






    //////////////////////////////////////////Connection & handler////////////////////////////////////
    public void performAction() {
        Utility.showProgress(this, mFormView, mProgressView, true);
        Intent intent = new Intent(this, MappService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        private Messenger mService;
        private boolean mBound;

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mSelectedItem.setSignInData(LoginHolder.servLoginRef.getSignInData());
            mService = new Messenger(service);
            mBound = true;
            Message msg = null;
            Bundle bundle = new Bundle();
            bundle.putInt("loginType", MappService.SERVICE_LOGIN);
            if(mServiceAction == MappService.DO_UPDATE || mServiceAction == MappService.DO_REG_NO_CHECK) {
                bundle.putString("regno", mRegNo.getText().toString().trim());
                bundle.putParcelable("service", LoginHolder.servLoginRef);
            } else {
                bundle.putParcelable("service", mSelectedItem);
            }
            msg = Message.obtain(null, mServiceAction);
            msg.replyTo = new Messenger(mResponseHandler);
            msg.setData(bundle);

            try {
                mService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;

            mBound = false;
        }
    };

    private static class UpdateHandler extends Handler {
        private ServProvProfileActivity mActivity;

        public UpdateHandler(ServProvProfileActivity activity) {
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MappService.ADD_WORK_PLACE:
                    mActivity.addWorkPlaceDone(msg.getData());
                    break;
                case MappService.REMOVE_WORK_PLACE:
                    mActivity.removeWorkPlaceDone(msg.getData());
                    break;
                case MappService.DO_UPDATE:
                    mActivity.updateDone(msg.getData());
                    break;
                case MappService.DO_REG_NO_CHECK:
                    mActivity.regNoCheckDone(msg.getData());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public void regNoCheckDone(Bundle data) {
        if(data.getBoolean("exists")) {
            mRegNo.setError("This Registration Number is already Registered.");
            mRegNo.requestFocus();
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
        unbindService(mConnection);
    }

    private void updateDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showRegistrationAlert(this, "", "Profile Updated Successfully.");
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
        unbindService(mConnection);
    }

    private void addWorkPlaceDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showRegistrationAlert(this, "", "Work Place Added Successfully.");
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
        unbindService(mConnection);
    }

    private void removeWorkPlaceDone(Bundle data) {
        if (data.getBoolean("status")) {
            Utility.showRegistrationAlert(this, "", "Work Place Removed Successfully.");
        }
        Utility.showProgress(this, mFormView, mProgressView, false);
        unbindService(mConnection);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

}