package com.prim8software.prim8datalogger;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class UserEntryWatcher implements TextWatcher {
    private Activity activity;
    private Integer timestampFirstLetterTyped;

    public UserEntryWatcher(Activity activity) {
        this.activity = activity;
        this.timestampFirstLetterTyped = -1;
    }

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
        ((MainActivity) this.activity).UpdateListView(ss, timestampFirstLetterTyped);
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
