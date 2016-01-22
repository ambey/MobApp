package com.extenprise.mapp.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by avinash on 4/11/15.
 */
public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;

    public TextDrawable(String text) {
        this.text = text;
        this.paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(16f);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, 0, 6, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /*Then set the drawable to left of the edittext as

    EditText et = (EditText)findViewById(R.id.editText1);
    String code = "+374";
    et.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code), null, null, null);
    et.setCompoundDrawablePadding(code.length()*10);

*/


    /*

    editText = (EditText) findViewById(R.id.editText1);
    editText.setText("+374");
    Selection.setSelection(editText.getText(), editText.getText().length());
    editText.addTextChangedListener(new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {


        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().startsWith("+374")) {
                editText.setText("+374");
                Selection.setSelection(editText.getText(), editText
                        .getText().length());

            }

        }

    });


    */




    /*@Override
    public void afterTextChanged(Editable s) {
        String prefix = "http://";
        if (!s.toString().startsWith(prefix)) {
            String cleanString;
            String deletedPrefix = prefix.substring(0, prefix.length() - 1);
            if (s.toString().startsWith(deletedPrefix)) {
                cleanString = s.toString().replaceAll(deletedPrefix, "");
            } else {
                cleanString = s.toString().replaceAll(prefix, "");
            }
            editText.setText(prefix + cleanString);
            editText.setSelection(prefix.length());
        }
    }

    */



}
