package com.msamogh.firstapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.msamogh.firstapp.callback.FindCallback2;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private View shakeView;
    private InternetConnectionTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        shakeView = findViewById(R.id.shake_view);
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        checkLoggedIn();
    }

    private void login() {
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);

        if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
            Toast.makeText(this, "Username or password field cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }

        final MaterialDialog progress = new MaterialDialog.Builder(this)
                .title("Signing in")
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();

        ParseUser.logInInBackground(username.getText().toString().trim(), password.getText().toString().trim(), new LogInCallback() {
            @Override
            public void done(final ParseUser user, ParseException e) {
                progress.dismiss();
                if (user != null) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    YoYo.with(Techniques.Shake)
                            .duration(1000)
                            .playOn(shakeView);
                }
            }
        });

    }
    public void forget(View v) {
        new MaterialDialog.Builder(this)
                .title("Forgot Password")
                .content("Enter Your Username")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        ParseQuery<ParseUser> query = ParseUser.getQuery().whereEqualTo("username", input.toString());
                        query.findInBackground(new FindCallback2<ParseUser>(LoginActivity.this) {
                            @Override
                            public void done(final List<ParseUser> list) {
                                if (!list.isEmpty()) {
                                    ParseUser.requestPasswordResetInBackground(list.get(0).getString("email"), new RequestPasswordResetCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Toast.makeText(getApplicationContext(), "An email was successfully sent to " + list.get(0).getString("email") + " with reset instructions", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "This username is not registered in our database", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "This username is not registered in our database", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                }).show();
    }

    private void checkLoggedIn() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            task = new InternetConnectionTask();
            task.execute(loginButton);
        }
    }

    public void signup(final View v) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        if (ParseUser.getCurrentUser() == null)
            finish();
        else
            super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if (task != null)
            task.cancel(true);
        super.onPause();
    }

    /**
     * Checks for Internet connection every second
     */
    class InternetConnectionTask extends AsyncTask<Button, Void, Void> {

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
                                        loginButton.setText("Sign in");
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

        private boolean isNetworkDisconnected() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni == null;
        }


    }

}
