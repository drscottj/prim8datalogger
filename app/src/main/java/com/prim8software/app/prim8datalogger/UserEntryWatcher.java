package com.prim8software.app.prim8datalogger;

// Part of Prim8 Data Logger
// Copyright 2016, Scott Johnson, Prim8 Software (scott.johnson@prim8software.com)

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

///@cond DEV
public class UserEntryWatcher implements TextWatcher {

    private Activity activity;
    private Integer timestampFirstLetterTyped;

    public UserEntryWatcher(Activity activity) {
        this.activity = activity;
        this.timestampFirstLetterTyped = -1;
    }
    ///@endcond

    /** @cond USER
     * @page pg_recording Recording Input Data
     * @section sec_recording_input Behavior when you enter text
     *
     * When you are entering text:
     * - When the first character is typed in the text box, the timestamp is recorded
     * - Whenever there is nothing in the text box, the timestamp in unset
     * - When the enter button is pressed and there is text in the edit text box, the data is recorded
     *
     * This is important when understanding what will and will not be captured
     *
     * @page pg_export Exporting Data
     * @section sec_recording_notes1 Exporting basics
     *
     * Please see @ref sec_recording_notes1 to understand the basics of exporting.
     */

    ///@cond DEV

    @Override
    public void afterTextChanged(Editable s)
    {
        String ss = s.toString();
        if(!ss.contains("\n"))
        {
            if(ss.length() == 0)
            {
                this.timestampFirstLetterTyped = -1;
            }
            else
            {
                if(this.timestampFirstLetterTyped < 0)
                {
                    Log.i("UserEntryTextWatcher", "timestamp set as " + this.timestampFirstLetterTyped);
                    this.timestampFirstLetterTyped = Common.CurrentTime();
                }
            }
            return;
        }

        if(this.timestampFirstLetterTyped < 0)
        {
            Log.i("UserEntryTextWatcher", "strange: the timestamp of the first letter typed has not been set");
            this.timestampFirstLetterTyped = Common.CurrentTime();
        }

        Log.i("UserEntryTextWatcher","Reading behavior line: " + ss);

        ss = ss.replace("\n", "");
        if(ss.length() > 0) {
            ((MainActivity) this.activity).UpdateListView(ss, timestampFirstLetterTyped);
        }
        s.clear();
        this.timestampFirstLetterTyped = -1;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
///@endcond