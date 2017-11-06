package edu.cse4230.schilbe.assignment3;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class EditItem extends AppCompatActivity {
    Intent m_Intent = null;
    EditText txtProductName = null;
    EditText txtProductPrice = null;
    Button btnCancel = null;
    Button btnSave = null;
    TextView pageTitle = null;
    String imagePath = null;
    Button browseButton = null;
    ImageView imageView = null;

    final int PICK_IMAGE_REQUEST = 1;
    public static int picture_number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        m_Intent = getIntent();
        txtProductName = (EditText) findViewById(R.id.txtAddUpdate_ProductName);
        txtProductPrice = (EditText) findViewById(R.id.txtAddUpdate_ProductPrice);
        pageTitle = (TextView) findViewById(R.id.pageTitle);

        if (m_Intent.getStringExtra("OperationType").equals("EDIT")) {
            pageTitle.setText("Edit Product");
            txtProductName.setText(m_Intent.getStringExtra("ProductName"));
            txtProductPrice.setText(m_Intent.getStringExtra("ProductPrice"));
        } else {
            pageTitle.setText("Create Product");
        }

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putExtra("OperationType", m_Intent.getStringExtra("OperationType"));
                intent.putExtra("ProductName", txtProductName.getText().toString());
                intent.putExtra("ProductPrice", txtProductPrice.getText().toString());
                intent.putExtra("ImagePath", imagePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        browseButton = (Button) findViewById(R.id.photoBrowserButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            picture_number = picture_number + 1;
            Uri uri = data.getData();
            String path = this.getFilesDir().getAbsolutePath() + "/temp_image" + picture_number;
            imagePath = path;
            File tempFile = new File(path);

            //Copy URI contents into temporary file.
            try {
                tempFile.createNewFile();
                copyAndClose(this.getContentResolver().openInputStream(data.getData()), new FileOutputStream(tempFile));
            } catch (IOException e) {
                //Log Error
            }

            //Now fetch the new URI
            Uri newUri = Uri.fromFile(tempFile);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                // Log.d(TAG, String.valueOf(bitmap));

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void copyAndClose(InputStream inputStream, FileOutputStream fileOutputStream) {
        try {
            byte[] buffer = new byte[4096]; // To hold file contents
            int bytes_read; // How many bytes in buffer

            // Read a chunk of bytes into the buffer, then write them out,
            // looping until we reach the end of the file (when read() returns
            // -1). Note the combination of assignment and comparison in this
            // while loop. This is a common I/O programming idiom.
            while ((bytes_read = inputStream.read(buffer)) != -1)
                // Read until EOF
                fileOutputStream.write(buffer, 0, bytes_read); // write
        } catch (Exception e) {

        }
        // Always close the streams, even if exceptions were thrown
        finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    ;
                }
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    ;
                }
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public String getPhotoPath(Uri uri) {
        if (isMediaDocument(uri)) {
            final String docId;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                docId = DocumentsContract.getDocumentId(uri);

                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {MediaStore.Images.Media.DATA};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
