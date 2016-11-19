package com.msamogh.firstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Pattern;


public class SignupActivity extends AppCompatActivity {
    private Button b;
    private boolean showToast = true;
    private final boolean[] valid = new boolean[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        b = (Button) findViewById(R.id.button2);
        b.setEnabled(false);
        initValidateFields();
    }

    private void initValidateFields() {
        final EditText fullName = (EditText) findViewById(R.id.fullName);
        final ImageView fullNameIcon = (ImageView) findViewById(R.id.fullName_icon);
        final EditText username = (EditText) findViewById(R.id.username);
        final ImageView usernameIcon = (ImageView) findViewById(R.id.username_icon);
        final EditText email = (EditText) findViewById(R.id.email);
        final ImageView emailIcon = (ImageView) findViewById(R.id.email_icon);
        final EditText pass = (EditText) findViewById(R.id.pass);
        final ImageView passIcon = (ImageView) findViewById(R.id.pass_icon);
        final EditText confirm = (EditText) findViewById(R.id.confirm);
        final ImageView confirmIcon = (ImageView) findViewById(R.id.confirm_icon);


        fullName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
                String fn = fullName.getText().toString().trim();
                int l = fn.length();
                if (l > 0 && l < 40) {
                    fullNameIcon.setImageResource(R.drawable.tick);
                    showToast = true;
                    valid[0] = true;
                } else if (l == 0) {
                    fullNameIcon.setImageResource(R.drawable.cross);
                    if (showToast) {
                        Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        showToast = false;
                    }
                    valid[0] = false;
                } else {
                    fullNameIcon.setImageResource(R.drawable.cross);
                    if (showToast) {
                        Toast.makeText(getApplicationContext(), "Name has to be less than 40 characters", Toast.LENGTH_SHORT).show();
                        showToast = false;
                    }
                    valid[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
                String un = username.getText().toString();
                int l = username.getText().toString().trim().length();
                if (l > 0 && l < 25 && !(un.contains(" "))) {
                    usernameIcon.setImageResource(R.drawable.tick);
                    valid[1] = true;
                    showToast = true;
                } else if (l == 0) {
                    usernameIcon.setImageResource(R.drawable.cross);
                    if (showToast) {
                        Toast.makeText(getApplicationContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                        showToast = false;
                    }
                    valid[1] = false;
                } else if (un.contains(" ")) {
                    usernameIcon.setImageResource(R.drawable.cross);
                    if (showToast) {
                        Toast.makeText(getApplicationContext(), "Username cannot contain spaces", Toast.LENGTH_SHORT).show();
                        showToast = false;
                    }
                    valid[1] = false;
                } else {
                    usernameIcon.setImageResource(R.drawable.cross);
                    if (showToast) {
                        Toast.makeText(getApplicationContext(), "Username has to be less than 25 characters", Toast.LENGTH_SHORT).show();
                        showToast = false;
                    }
                    valid[1] = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
            }
        });


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.hasFocus()) {
                    b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
                    String text = email.getText().toString();
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches()) {
                        emailIcon.setImageResource(R.drawable.tick);
                        valid[2] = true;
                        showToast = true;
                    } else {
                        emailIcon.setImageResource(R.drawable.cross);
                        if (showToast) {
                            Toast.makeText(getApplicationContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
                            showToast = false;
                        }
                        valid[2] = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
                int l3 = pass.getText().toString().length();
                if (l3 > 5) {
                    passIcon.setImageResource(R.drawable.tick);
                    valid[3] = true;
                    showToast = true;
                } else {
                    passIcon.setImageResource(R.drawable.cross);
                    if (showToast) {
                        Toast.makeText(getApplicationContext(), "Password must have minimum of 6 characters", Toast.LENGTH_SHORT).show();
                        showToast = false;
                    }
                    valid[3] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
            }
        });
        confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
                String text2 = pass.getText().toString();
                int l4 = confirm.getText().toString().length();
                String text3 = confirm.getText().toString();
                if (text2.equals(text3) && (l4 > 5)) {
                    valid[4] = true;
                    showToast = true;
                    confirmIcon.setImageResource(R.drawable.tick);
                } else {
                    confirmIcon.setImageResource(R.drawable.cross);
                    if (showToast) {
                        Toast.makeText(getApplicationContext(), "Passwords not matching", Toast.LENGTH_SHORT).show();
                        showToast = false;
                    }
                    valid[4] = false;
                }
            }


            @Override
            public void afterTextChanged(Editable s) {
                b.setEnabled(valid[0] && valid[1] && valid[2] && valid[3] && valid[4]);
            }
        });

    }

    public void verify(View v) {
        boolean verified = true;
        EditText name = (EditText) findViewById(R.id.fullName);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.pass);
        EditText email = (EditText) findViewById(R.id.email);

        ParseUser user = new ParseUser();
        user.setUsername(username.getText().toString().toLowerCase().trim());
        user.setPassword(password.getText().toString());
        user.setEmail(email.getText().toString().trim());
        user.put("name", name.getText().toString().trim());

        final MaterialDialog progress = new MaterialDialog.Builder(this)
                .title("Signing up")
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                progress.dismiss();
                if (e == null) {
                    Toast.makeText(SignupActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                } else {
                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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

    /**
     * Checks for Internet connection every second
     */
    class InternetConnectionTask extends AsyncTask<Button, Void, Void> {

        private boolean isNetworkDisconnected() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni == null;
        }

        @Override
        protected Void doInBackground(Button... params) {
            if (params.length != 0) {
                final Button loginButton = params[0];

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (isNetworkDisconnected()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginButton.setText("No connection found");
                                        loginButton.setEnabled(false);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginButton.setText("Sign up");
                                        loginButton.setEnabled(true);
                                        loginButton.setTextColor(Color.WHITE);
                                    }
                                });
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                t.start();
            }
            return null;
        }


    }


}