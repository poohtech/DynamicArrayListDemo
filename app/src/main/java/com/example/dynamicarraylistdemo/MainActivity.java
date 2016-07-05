package com.example.dynamicarraylistdemo;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgProfile;
    private EditText edtText;
    private TextView btnAdd;
    private RecyclerView recyclerViewUser;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<UserBean> arrayList;
    private UserAdapter userAdapter;
    private UserBean userBean;


    /* Upload Pic */
    private static String oldFileName;
    private static Uri imgUri;
    private int CAMERA_CAPTURE = 1000;
    private int GET_FROM_GALLERY = 2000;
    private static String newImgName;
    String filename = "";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        edtText = (EditText) findViewById(R.id.edtText);
        btnAdd = (TextView) findViewById(R.id.btnAdd);
        recyclerViewUser = (RecyclerView) findViewById(R.id.recyclerViewUser);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerViewUser.setLayoutManager(linearLayoutManager);
        arrayList = new ArrayList<UserBean>();

        imgProfile.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        System.out.println("********onCreate*******");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgProfile:
                showImgAlert(MainActivity.this);
                break;

            case R.id.btnAdd:
                System.out.println("*********btnAdd : filename*********" + filename);
                userBean = new UserBean();
                userBean.text = edtText.getText().toString().trim();
                userBean.image = filename;
                arrayList.add(userBean);

                userAdapter = new UserAdapter(MainActivity.this);
                userAdapter.setMoreItem(arrayList);
                recyclerViewUser.setAdapter(userAdapter);
                userAdapter.notifyDataSetChanged();

                edtText.setText("");
                break;
        }
    }

    private void showImgAlert(Context context) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.popup_dialog_upload_photo);
        dialog.setCancelable(false);

        final LinearLayout btn_gallerylft = (LinearLayout) dialog
                .findViewById(R.id.lay_1);
        final LinearLayout btn_camerargt = (LinearLayout) dialog
                .findViewById(R.id.lay_2);
        final Button cancel = (Button) dialog.findViewById(R.id.btn_cancle);

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }

        });

        btn_camerargt.setOnClickListener(new View.OnClickListener() {
            // Camera
            @Override
            public void onClick(View v) {
                try {
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getPath();
                    oldFileName = Environment.getExternalStorageDirectory()
                            .getPath() + "/DynamicData/" + System.currentTimeMillis() + ".jpeg";

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, oldFileName);
                    imgUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);

                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                    startActivityForResult(intent, CAMERA_CAPTURE);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

            }
        });

        btn_gallerylft.setOnClickListener(new View.OnClickListener() {

            // Galley
            @Override
            public void onClick(View v) {
                intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, GET_FROM_GALLERY);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) { // for camera
                try {

                    System.out
                            .println(" -----------ShareExp Activity img uri :::: "
                                    + imgUri);

                    filename = Util.compressImage(String.valueOf(imgUri),
                            MainActivity.this);

                    System.out
                            .println("::::::::::::::::ShareExp Activity filename ::: "
                                    + filename);
                    imgProfile.setImageBitmap(BitmapFactory
                            .decodeFile(filename));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GET_FROM_GALLERY) {
                // Pick From Gallery
                try {

                    if (data != null) {

                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null,
                                        null, null);
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            int columnIndex = cursor
                                    .getColumnIndex(filePathColumn[0]);
                            newImgName = cursor.getString(columnIndex);
                            cursor.close();
                            System.out.println("==========filePath :: "
                                    + newImgName);
                            if (newImgName != null
                                    && new File(newImgName).exists()) {

                                filename = Util.compressImage(
                                        String.valueOf(newImgName),
                                        MainActivity.this);
                            }
                        }

                        imgProfile.setImageBitmap(BitmapFactory
                                .decodeFile(filename));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
