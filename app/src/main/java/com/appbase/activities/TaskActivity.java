package com.appbase.activities;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.appbase.activities.util.FirebaseDeak;
import com.appbase.activities.util.ImageUtil;
import com.appbase.activities.util.LiveQueryAdapter;
import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.couchbase.lite.UnsavedRevision;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class TaskActivity extends AppCompatActivity {
    public static final String INTENT_LIST_ID = "list_id";

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private static final int THUMBNAIL_SIZE = 150;
    public static Map<String, String> hm = new HashMap<String, String>();

    private static SimpleDateFormat mDateFormatter =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private String mListId;
    private Database mDatabase;
    private TaskAdapter mAdapter;
    ArrayList<String> tasks = new ArrayList<String>();
    private Map<String, Object> properties;
    private String mImagePathToBeAttached;
    private Document mCurrentTaskToAttachImage;
    private Bitmap mImageToBeAttached;
    private String str = "ppp";
    String strrr;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        Application application = (Application) getApplication();
        mDatabase = application.getDatabase();

        if (savedInstanceState != null)
            mListId = savedInstanceState.getString(INTENT_LIST_ID);
        else
            mListId = getIntent().getStringExtra(INTENT_LIST_ID);

        Query query = getQuery();
        mAdapter = new TaskAdapter(this, query.toLiveQuery());


        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);
        setListHeader(listView);
        setListItemLongClick(listView);

        mDateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(INTENT_LIST_ID, mListId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task, menu);

        // Hide share menu if the current user is not the owner of the list:
        Application application = (Application) getApplication();
        String owner = (String) application.getDatabase().
                getDocument(mListId).getProperties().get("owner");

        boolean sharable = owner != null && owner.equals("p:" + application.getCurrentUserId());
        menu.findItem(R.id.share).setVisible(sharable);

        try {
            work();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public void work() {

            new Thread(new Runnable() {
                public void run(){
                    for (int i = 0; i < tasks.size(); i++) {
                        Log.v(Integer.toString(tasks.size()), "lllllllllllll");
                        strrr = doIntt(tasks.get(i));

                        String category = strrr.split("<ItemCategory>")[1].split("</ItemCategory>")[0];
                        String aisle = strrr.split("<AisleNumber>")[1].split("</AisleNumber>")[0];
                        FirebaseDeak fire = new FirebaseDeak();
                        fire.FirebaseDeakes(category, aisle, tasks.get(i), hm);
                    }}
            }).start();





    }

    public static void parse(String xml) throws Exception {
    }

    protected String doIntt(String item) {
        // Do some validation here
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL("http://www.supermarketapi.com/api.asmx/COMMERCIAL_SearchForItem?APIKEY=8f9ce01e07&StoreId=d7c0f77c1c&ItemName=" + item);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    public String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource();
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.share:
                displayShareActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setListHeader(ListView listView) {
        ViewGroup header = (ViewGroup) getLayoutInflater().inflate(
                R.layout.view_task_create, listView, false);

        /*final ImageView imageView = (ImageView) header.findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAttachImageDialog(null);
            }
        });*/

        final EditText text = (EditText) header.findViewById(R.id.text);
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String inputText = text.getText().toString();
                    if (inputText.length() > 0)
                        try {
                            createTask(inputText, mImageToBeAttached, mListId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    text.setText("");
                    deleteCurrentPhoto();

                    return true;
                }
                return false;
            }
        });

        listView.addHeaderView(header);
    }

    private void setListItemLongClick(ListView listView) {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int pos, long id) {
                PopupMenu popup = new PopupMenu(TaskActivity.this, view);
                popup.inflate(R.menu.task_item);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Document task = (Document) mAdapter.getItem(pos - 1);
                        handleTaskPopupAction(item, task);
                        return true;
                    }
                });
                popup.show();
                return true;
            }
        });
    }

    private void handleTaskPopupAction(MenuItem item, Document task) {
        switch (item.getItemId()) {
            case R.id.update:
                updateTask(task);
                return;
            case R.id.delete:
                deleteTask(task);
                return;
        }

    }

    private void displayShareActivity() {
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra(ShareActivity.INTENT_LIST_ID, mListId);
        startActivity(intent);
    }

    private Query getQuery() {
        Application application = (Application) getApplication();
        Query query = application.getTasksView().createQuery();
        query.setDescending(true);

        List<Object> startKeys = new ArrayList<Object>();
        startKeys.add(mListId);
        startKeys.add(new HashMap<String, Object>());

        List<Object> endKeys = new ArrayList<Object>();
        endKeys.add(mListId);

        query.setStartKey(startKeys);
        query.setEndKey(endKeys);

        return query;
    }

    private void createTask(String title, Bitmap image, String listId) throws Exception {
        String currentTimeString = mDateFormatter.format(new Date());

        properties = new HashMap<String, Object>();
        properties.put("type", "task");
        properties.put("title", title);
        properties.put("checked", Boolean.FALSE);
        properties.put("created_at", currentTimeString);
        properties.put("list_id", listId);

        Document document = mDatabase.createDocument();
        UnsavedRevision revision = document.createRevision();
        revision.setUserProperties(properties);

        if (image != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, out);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            revision.setAttachment("image", "image/jpg", in);
        }

        try {
            revision.save();
        } catch (CouchbaseLiteException e) {
            Log.e(Application.TAG, "Cannot create new task", e);
        }
        work();
    }

    private void updateTask(final Document task) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.title_dialog_update));

        final EditText input = new EditText(this);
        input.setMaxLines(1);
        input.setSingleLine(true);
        String text = (String) task.getProperty("title");
        input.setText(text);
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String currentTimeString = mDateFormatter.format(new Date());

                Map<String, Object> updatedProperties = new HashMap<String, Object>();
                updatedProperties.putAll(task.getProperties());
                updatedProperties.put("title", input.getText().toString());
                updatedProperties.put("updated_at", currentTimeString);

                try {
                    task.putProperties(updatedProperties);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.show();
    }

    private void deleteTask(final Document task) {
        try {
            tasks.remove(task.getProperty("title"));
            task.delete();

        } catch (CouchbaseLiteException e) {
            Log.e(Application.TAG, "Cannot delete a task", e);
        }
    }

    private void attachImage(Document task, Bitmap image) {
        if (task == null || image == null) return;

        UnsavedRevision revision = task.createRevision();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        revision.setAttachment("image", "image/jpg", in);

        try {
            revision.save();
        } catch (CouchbaseLiteException e) {
            Log.e(Application.TAG, "Cannot attach image", e);
        }
    }

    private void updateCheckedStatus(Document task, boolean checked) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.putAll(task.getProperties());
        properties.put("checked", checked);

        try {
            task.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(Application.TAG, "Cannot update checked status", e);
        }
    }

    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.e(Application.TAG, "Cannot create a temp image file", e);
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "TODO_LITE-" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        mImagePathToBeAttached = image.getAbsolutePath();
        return image;
    }

    private void dispatchChoosePhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CHOOSE_PHOTO);
    }

    private void deleteCurrentPhoto() {
        if (mImageToBeAttached != null) {
            mImageToBeAttached.recycle();
            mImageToBeAttached = null;
        }

        ViewGroup view = (ViewGroup) findViewById(R.id.create_task);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
       /// imageView.setImageDrawable(getResources().getDrawable());
    }

    private void displayAttachImageDialog(final Document task) {
        CharSequence[] items;
        if (mImageToBeAttached != null)
            items = new CharSequence[]{"Take photo", "Choose photo", "Delete photo"};
        else
            items = new CharSequence[]{"Take photo", "Choose photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    mCurrentTaskToAttachImage = task;
                    dispatchTakePhotoIntent();
                } else if (item == 1) {
                    mCurrentTaskToAttachImage = task;
                    dispatchChoosePhotoIntent();
                } else {
                    deleteCurrentPhoto();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(tasks.get(1), "gggggggggg");
        Log.v("ggggggggggggg", "gggggggggg");

        if (resultCode != RESULT_OK) {
            if (mCurrentTaskToAttachImage != null)
                mCurrentTaskToAttachImage = null;
            return;
        }

        final int size = THUMBNAIL_SIZE;
        Bitmap thumbnail = null;
        if (requestCode == REQUEST_TAKE_PHOTO) {
            File file = new File(mImagePathToBeAttached);
            if (file.exists()) {
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                options.inJustDecodeBounds = false;
                mImageToBeAttached = BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                if (mCurrentTaskToAttachImage == null) {
                    thumbnail = ThumbnailUtils.extractThumbnail(mImageToBeAttached, size, size);
                }

                // Delete the temporary image file
                file.delete();
            }
            mImagePathToBeAttached = null;
        } else if (requestCode == REQUEST_CHOOSE_PHOTO) {
            try {
                Uri uri = data.getData();
                ContentResolver resolver = getContentResolver();
                mImageToBeAttached = MediaStore.Images.Media.getBitmap(resolver, uri);
                if (mCurrentTaskToAttachImage == null) {
                    AssetFileDescriptor asset = resolver.openAssetFileDescriptor(uri, "r");
                    thumbnail = ImageUtil.thumbnailFromDescriptor(asset.getFileDescriptor(), size, size);
                }
            } catch (IOException e) {
                Log.e(Application.TAG, "Cannot get a selected photo from the gallery.", e);
            }
        }

        if (mImageToBeAttached != null) {
            if (mCurrentTaskToAttachImage != null) {
                attachImage(mCurrentTaskToAttachImage, mImageToBeAttached);
                mImageToBeAttached = null;
            }
        }

        if (thumbnail != null) {
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(thumbnail);
        }

        // Ensure resetting the task to attach an image
        if (mCurrentTaskToAttachImage != null)
            mCurrentTaskToAttachImage = null;
    }

    public void onclick(View v) {
    final AppCompatActivity current = this;
        new Thread(new Runnable() {
            public void run(){
                Intent intent = new Intent(current, Home.class);
                startActivity(intent);
                }
        }).start();





    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */



    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }


    private class TaskAdapter extends LiveQueryAdapter {
        public TaskAdapter(Context context, LiveQuery query) {
            super(context, query);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.view_task, null);
            }

            final Document task = (Document) getItem(position);
            if (task == null || task.getCurrentRevision() == null) {
                return convertView;
            }

            Bitmap thumbnail = getTaskThumbnail(task);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            if (thumbnail != null)
                imageView.setImageBitmap(thumbnail);
            else
                //imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_light));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (task.getCurrentRevision().getAttachment("image") != null) {
                        Intent intent = new Intent(TaskActivity.this, ImageActivity.class);
                        intent.putExtra(ImageActivity.INTENT_TASK_DOC_ID, task.getId());
                        startActivity(intent);
                    } else
                        displayAttachImageDialog(task);
                }
            });

            TextView text = (TextView) convertView.findViewById(R.id.text);
            text.setText((String) task.getProperty("title"));
            if (tasks.contains((String) ((task.getProperty("title")))) == false) {
                tasks.add((String) ((task.getProperty("title"))));
            }

            final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checked);
            Boolean checkedProperty = (Boolean) task.getProperty("checked");
            boolean checked = checkedProperty != null ? checkedProperty.booleanValue() : false;
            checkBox.setChecked(checked);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateCheckedStatus(task, checkBox.isChecked());
                }
            });

            return convertView;
        }

        private Bitmap getTaskThumbnail(Document task) {
            List<Attachment> attachments = task.getCurrentRevision().getAttachments();
            if (attachments.size() == 0)
                return null;

            Bitmap bitmap = null;
            InputStream is = null;
            final int size = THUMBNAIL_SIZE;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                is = attachments.get(0).getContent();
                BitmapFactory.decodeStream(is, null, options);
                options.inSampleSize = ImageUtil.calculateInSampleSize(options, size, size);
                is.close();

                options.inJustDecodeBounds = false;
                is = task.getCurrentRevision().getAttachments().get(0).getContent();
                bitmap = BitmapFactory.decodeStream(is, null, options);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, size, size);
            } catch (Exception e) {
                Log.e(Application.TAG, "Cannot decode the attached image", e);
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException e) {
                }
            }
            return bitmap;
        }
    }
}
