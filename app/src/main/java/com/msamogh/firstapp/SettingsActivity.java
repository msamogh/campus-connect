package com.msamogh.firstapp;

import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

    }

    public void editName(View v) {
        new MaterialDialog.Builder(this)
                .title("Change your Name")
                .content("Enter new Name")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        ParseUser user = ParseUser.getCurrentUser();
                        if (input.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Name Cannot be Empty", Toast.LENGTH_SHORT).show();
                        } else if (input.length() > 40) {
                            Toast.makeText(getApplicationContext(), "Name Cannot Exceed 40 Characters", Toast.LENGTH_SHORT).show();
                        } else {
                            user.put("name", input.toString());
                            user.saveInBackground();
                            Toast.makeText(getApplicationContext(), "Name Changed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
    }

    public void editPass(View v) {
        View v1 = LayoutInflater.from(this).inflate(R.layout.change_password, null);
        final Button b = (Button) v1.findViewById(R.id.butt);
        final EditText curr = (EditText) v1.findViewById(R.id.curr);
        final EditText new_pass = (EditText) v1.findViewById(R.id.new_pass);
        final EditText repass = (EditText) v1.findViewById(R.id.repass);
        final ParseUser CurrentUser = ParseUser.getCurrentUser();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logInInBackground(CurrentUser.getString("username"), curr.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(final ParseUser user, ParseException e) {
                        if (user != null) {
                            if (new_pass.getText().toString().length() > 5 && repass.getText().toString().equals(new_pass.getText().toString())) {
                                user.put("password", repass.getText().toString());
                                user.saveInBackground();
                                Toast.makeText(getApplicationContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (new_pass.getText().toString().length() < 5 || repass.getText().toString().length() < 5) {
                                Toast.makeText(getApplicationContext(), "Minimum Length of Password should be 6 characters", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Your current password is wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        new MaterialDialog.Builder(this)
                .title("Change Password")
                .customView(v1, true)
                .show();
    }


    public void invite(View v) {
        //Toast.makeText(getApplicationContext(),"Standard Voice Charges will Apply",Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, RESULT_OK);
    }

    public void usage(View v) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(
                    "com.android.settings",
                    "com.android.settings.Settings$DataUsageSummaryActivity"));
            startActivity(intent);
        } catch (Exception e) {
            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
        }
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.PhoneLookup.NUMBER));
                        }
                    }
                }
                break;
        }
    }


    public void about(View v) {
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    public void source(View v) {
        new MaterialDialog.Builder(this)
                .title("Libraries Used:")
                .customView(R.layout.source_libraries, true)
                .show();
    }

    public void terms(View v) {
        new MaterialDialog.Builder(this)
                .title("Terms of Service")
                .customView(R.layout.terms, true)
                .show();
    }

    public void resendEmail(View v) {

        ParseUser user1 = ParseUser.getCurrentUser();
        if (!user1.getBoolean("emailVerified")) {
            String un = user1.getEmail();
            user1.setEmail("");
            user1.saveInBackground();
            user1.setEmail(un);
            user1.saveInBackground();
            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
            Toast.makeText(getApplicationContext(), "We have resent the validation email to " + un + ". Please check your email!", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Kindly Verify our email before logging in again", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Email already verified!", Toast.LENGTH_SHORT).show();
        }


    }

    public void faq(View v) {
        new MaterialDialog.Builder(this)
                .title("Frequently Asked Questions: ")
                .customView(R.layout.faq, true)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action b  ar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}