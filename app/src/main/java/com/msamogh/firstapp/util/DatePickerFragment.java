package com.msamogh.firstapp.util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by user on 06-Jul-15.
 */
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener callback;

    private static final String ARG_DATE = "date";
    private static final String ARG_MONTH = "month";
    private static final String ARG_YEAR = "year";

    public DatePickerFragment() {
    }

    public static DatePickerFragment newInstance(int date, int month, int year) {
        DatePickerFragment frag = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("date", date);
        args.putInt("month", month);
        args.putInt("year", year);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = getArguments().getInt(ARG_YEAR);
        int month = getArguments().getInt(ARG_MONTH);
        int day = getArguments().getInt(ARG_DATE);
        return new DatePickerDialog(getActivity(), callback, year, month, day);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (DatePickerDialog.OnDateSetListener) activity;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }
    }
}