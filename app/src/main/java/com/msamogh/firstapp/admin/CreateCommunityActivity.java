package com.msamogh.firstapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.msamogh.firstapp.R;
import com.msamogh.firstapp.callback.SaveCallback2;
import com.msamogh.firstapp.model.Community;
import com.msamogh.firstapp.model.ParseModelException;
import com.parse.ParseUser;


public class CreateCommunityActivity extends AppCompatActivity {

    private TextView communityId;
    private TextView communityName;
    private TextView communityDescription;
    private Switch communityPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New community");

        communityId = (TextView) findViewById(R.id.c_id);
        communityName = (TextView) findViewById(R.id.c_name);
        communityDescription = (TextView) findViewById(R.id.c_description);
        communityPrivate = (Switch) findViewById(R.id.c_private);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_community, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create) {

            if (TextUtils.isEmpty(communityId.getText()) || communityId.getText().toString().trim().isEmpty()) {
                Toast.makeText(CreateCommunityActivity.this, "Community ID cannot be empty", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (TextUtils.isEmpty(communityName.getText()) || communityName.getText().toString().trim().isEmpty()) {
                Toast.makeText(CreateCommunityActivity.this, "Community name cannot be empty", Toast.LENGTH_SHORT).show();
                return true;
            }

            final MaterialDialog dialog = new MaterialDialog.Builder(CreateCommunityActivity.this)
                    .title("Creating community")
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();
            try {
                new Community(CreateCommunityActivity.this, ParseUser.getCurrentUser(), communityId.getText().toString(), communityName.getText().toString(),
                        communityDescription.getText().toString(), communityPrivate.isChecked(), new SaveCallback2(CreateCommunityActivity.this) {
                    @Override
                    public void done() {
                        dialog.dismiss();
                        Toast.makeText(CreateCommunityActivity.this, communityId.getText().toString() + " created successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreateCommunityActivity.this, ManageCommunitiesActivity.class));
                        CreateCommunityActivity.this.finish();
                    }
                });
            } catch (ParseModelException e) {
                Toast.makeText(CreateCommunityActivity.this, communityId.getText().toString() + " is already taken. Choose another ID.", Toast.LENGTH_SHORT).show();
                communityId.requestFocus();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
