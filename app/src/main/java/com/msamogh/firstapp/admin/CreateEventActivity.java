package com.msamogh.firstapp.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.msamogh.firstapp.R;
import com.msamogh.firstapp.callback.FindCallback2;
import com.msamogh.firstapp.callback.SaveCallback2;
import com.msamogh.firstapp.model.Community;
import com.msamogh.firstapp.model.Event;
import com.msamogh.firstapp.util.DatePickerFragment;
import com.msamogh.firstapp.util.TimePickerFragment;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Date mDate = new Date();

    private Button dateButton, timeButton;
    private String mCommunity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mCommunity = getIntent().getExtras().getString("community");

        dateButton = (Button) findViewById(R.id.date);
        timeButton = (Button) findViewById(R.id.time);

        findViewById(R.id.discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(CreateEventActivity.this)
                        .content("Discard event?")
                        .positiveText("Discard")
                        .negativeText("Cancel")
                        .negativeColor(Color.parseColor("#666666"))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                CreateEventActivity.this.finish();
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView name = (TextView) findViewById(R.id.name);
                final TextView location = (TextView) findViewById(R.id.location);
                final TextView contact = (TextView) findViewById(R.id.contact);
                final Switch rsvp = (Switch) findViewById(R.id.rsvp);
                final TextView description = (TextView) findViewById(R.id.description);
                final MaterialDialog dialog = new MaterialDialog.Builder(CreateEventActivity.this)
                        .progress(true, 0)
                        .title("Creating event")
                        .content(R.string.please_wait)
                        .show();

                new Community(mCommunity, new FindCallback2<ParseObject>(CreateEventActivity.this) {
                    @Override
                    public void done(List<ParseObject> list) {
                        if (!list.isEmpty()) {
                            new Event(list.get(0), name.getText().toString(), mDate, location.getText().toString(), contact.getText().toString(), rsvp.isChecked(), description.getText().toString(), new SaveCallback2(CreateEventActivity.this) {
                                @Override
                                public void done() {
                                    dialog.dismiss();
                                    CreateEventActivity.this.finish();
                                    Toast.makeText(CreateEventActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
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

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(CreateEventActivity.this)
                .content("Discard event?")
                .positiveText("Discard")
                .negativeText("Cancel")
                .negativeColor(Color.parseColor("#666666"))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        CreateEventActivity.super.onBackPressed();
                    }
                })
                .show();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = TimePickerFragment.newInstance(mDate.getHours(), mDate.getMinutes());
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(mDate.getDate(), mDate.getMonth(), mDate.getYear() + 1900);
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (mDate == null) mDate = new Date();
        mDate.setYear(year);
        mDate.setMonth(monthOfYear);
        mDate.setDate(dayOfMonth);
        dateButton.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + (year));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mDate == null) mDate = new Date();
        mDate.setHours(hourOfDay);
        mDate.setMinutes(minute);
        timeButton.setText((hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay) + ":" + (minute < 10 ? ("0" + minute) : minute));
    }
}