package com.msamogh.firstapp.admin;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.msamogh.firstapp.R;
import com.msamogh.firstapp.callback.FindCallback2;
import com.msamogh.firstapp.callback.SaveCallback2;
import com.msamogh.firstapp.model.Community;
import com.msamogh.firstapp.util.MyCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class CreatePostActivity extends AppCompatActivity {

    private ParseObject mCommunity;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;
    private String mCommunityId;
    private final ArrayList<Uri> mUris = new ArrayList<>();
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        rootView = findViewById(R.id.mother_of_god);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Bundle b = getIntent().getExtras();
        mCommunityId = b.getString("community");
    }

    private void chooseFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            findViewById(R.id.attached_images_parent).setVisibility(View.VISIBLE);
            final ImageView image = (ImageView) inflater.inflate(R.layout.image_create_post, null).findViewById(R.id.image);

            Picasso.with(this).load(data.getData()).centerCrop().resize(200, 200).into(image);

            LinearLayout.MarginLayoutParams params = new LinearLayout.MarginLayoutParams(200, 200);
            params.setMargins(100, 0, 100, 0);
            image.setLayoutParams(params);

            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new MaterialDialog.Builder(CreatePostActivity.this)
                            .content("Remove image?")
                            .positiveText("Remove")
                            .negativeText("Cancel")
                            .negativeColor(Color.parseColor("#666666"))
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    ((LinearLayout) findViewById(R.id.attached_images)).removeView(image);
                                    mUris.remove(data.getData().toString());
                                }
                            })
                            .show();
                    return true;
                }
            });
            image.setPadding(10, 0, 0, 10);

            mUris.add(data.getData());
            ((LinearLayout) findViewById(R.id.attached_images)).addView(image);
        }
    }

    private void saveFilesAndPublish(final String content, final MyCallback callback) {
        final ParseObject po = new ParseObject("Post");
        po.put("content", content);
        po.put("type", "post");
        new Community(mCommunityId, new FindCallback2<ParseObject>(this) {
            @Override
            public void done(List<ParseObject> list) {
                if (list.isEmpty()) return;
                mCommunity = list.get(0);
                po.put("community", mCommunity);
                if (mUris.isEmpty()) {
                    System.out.println("Empty");
                    po.saveInBackground(new SaveCallback2(CreatePostActivity.this) {
                        @Override
                        public void done() {
                            callback.callback();
                        }
                    });
                    return;
                }


                System.out.println("Uploading");

                final int[] i = {0};
                final boolean[] done = {true};

                final MaterialDialog dialog = new MaterialDialog.Builder(CreatePostActivity.this)
                        .progress(false, 100)
                        .title("Uploading images")
                        .cancelable(false)
                        .show();

                for (i[0] = 0; i[0] < mUris.size() && done[0]; i[0]++) {
                    Uri uri = mUris.get(i[0]);

                    try {
                        InputStream iStream = getContentResolver().openInputStream(uri);
                        byte[] data = getBytes(iStream);

                        final ParseFile file = new ParseFile(data);
                        file.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                dialog.dismiss();
                                if (e == null) {
                                    if (mCommunity != null) {
                                        po.add("images", file);
                                        if (i[0] == mUris.size()) {
                                            po.put("community", mCommunity);
                                            po.saveInBackground(new SaveCallback2(CreatePostActivity.this) {
                                                @Override
                                                public void done() {
                                                    done[0] = false;
                                                    callback.callback();
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }, new ProgressCallback() {
                            @Override
                            public void done(Integer integer) {
                                dialog.setContent(i[0] + " of " + mUris.size());
                                dialog.setProgress(integer);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            final EditText content = (EditText) findViewById(R.id.post_content);
            if (content.getText() != null) {
                if (TextUtils.isEmpty(content.getText().toString().trim())) {
                    Toast.makeText(CreatePostActivity.this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CreatePostActivity.this, "Post cannot be empty", Toast.LENGTH_SHORT).show();
            }

            final MaterialDialog progress = new MaterialDialog.Builder(CreatePostActivity.this)
                    .title("Publishing Post")
                    .content(R.string.please_wait)
                    .cancelable(true)
                    .progress(true, 0)
                    .show();

            saveFilesAndPublish(content.getText().toString(), new MyCallback() {
                @Override
                public void callback() {
                    Snackbar.make(rootView, "Posted successfully", Snackbar.LENGTH_SHORT).show();
                    progress.dismiss();
                    CreatePostActivity.this.finish();
                }
            });

        } else if (id == R.id.action_attach) {
            LinearLayout attachedImages = (LinearLayout) findViewById(R.id.attached_images);
            if (attachedImages.getChildCount() < 4)
                chooseFromGallery();
            else
                Snackbar.make(findViewById(R.id.attached_images_parent), "You can attach a maximum of 4 images.", Snackbar.LENGTH_SHORT).show();
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(CreatePostActivity.this)
                .content("Discard draft?")
                .positiveText("Discard")
                .negativeColor(Color.parseColor("#666666"))
                .negativeText("Cancel")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        CreatePostActivity.super.onBackPressed();
                    }
                })
                .show();
    }
}
