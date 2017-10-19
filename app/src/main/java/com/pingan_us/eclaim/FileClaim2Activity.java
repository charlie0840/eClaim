package com.pingan_us.eclaim;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileClaim2Activity extends AppCompatActivity {
    private ListView list;
    private List<Bitmap> picList;
    private List<String> titleList;
    private String resStr, userChoosenTask;
    private int change_or_insert, pos;
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, INSERT_IMAGE = 1, CHANGE_IMAGE = 2;
    private CustomList adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileclaim2);

        picList = new ArrayList<Bitmap>();
        titleList = new ArrayList<String>();

        picList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));
        picList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));
        picList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));
        picList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));

        titleList.add("Whole Scene");
        titleList.add("Your Vehicle Plate");
        titleList.add("The other vehicle plate");
        titleList.add("More pictures");

        adapter = new
                CustomList(FileClaim2Activity.this, titleList, picList);
        list=(ListView)findViewById(R.id.photo_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(FileClaim2Activity.this, "position " + position + " You Clicked at " +titleList.get(+ position) + " position " + position + " size " + titleList.size(), Toast.LENGTH_SHORT).show();
                if(position+1 == titleList.size()){
                    LayoutInflater li = LayoutInflater.from(parent.getContext());
                    View promptsView = li.inflate(R.layout.popup_window, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parent.getContext());

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);
                    alertDialogBuilder.setCancelable(false).setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // get user input and set it to result
                                            // edit text
                                            resStr = userInput.getText().toString();
                                            change_or_insert = INSERT_IMAGE;
                                            selectImage();
                                        }
                                    })
                            .setNegativeButton("Return",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
                else{
                    if(position + 1 != 4) {
                        editList(position);
                    }
                }

            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(FileClaim2Activity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(FileClaim2Activity.this);
                if(!result)
                    Toast.makeText(getApplicationContext(), "no permission!!!!", Toast.LENGTH_LONG).show();
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Toast.makeText(this, "camera!!!!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager pm = getApplicationContext().getPackageManager();
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            startActivityForResult(intent, REQUEST_CAMERA);
        else
            Toast.makeText(getApplicationContext(), "No camera detected", Toast.LENGTH_LONG).show();
    }

    private void galleryIntent() {
        Toast.makeText(this, "gallery!!!!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bitmap resBitmap = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
        if(change_or_insert == INSERT_IMAGE)
            addToList(resBitmap);
        else{
            setList(resBitmap);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap resBitmap = Bitmap.createScaledBitmap(thumbnail, thumbnail.getWidth(), thumbnail.getHeight(), true);
        if(change_or_insert == INSERT_IMAGE)
            addToList(resBitmap);
        else{
            setList(resBitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    public void addToList(Bitmap resBitmap) {
        titleList.add(resStr);
        titleList.add(titleList.get(titleList.size() - 1));
        Bitmap currBitmap = picList.get(picList.size() - 1);
        picList.add(currBitmap);
        picList.set(picList.size() - 2, resBitmap);
        titleList.set(titleList.size() - 2, resStr);
        adapter.notifyDataSetChanged();
        resStr = "";
    }

    public void setList(Bitmap resBitmap) {
        titleList.set(pos, resStr);
        picList.set(pos, resBitmap);
        adapter.notifyDataSetChanged();
    }

    public void editList(final int position) {
        String currStr = titleList.get(position);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_window, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        Button rmv_btn = (Button) findViewById(R.id.remove_button);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(currStr);

        userInput.setFocusable(false);

        if(position > 3) {
            userInput.setFocusable(true);
            rmv_btn.setVisibility(View.VISIBLE);
            rmv_btn.setClickable(true);
            rmv_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleList.remove(position);
                    picList.remove(position);
                    Toast.makeText(FileClaim2Activity.this, "Picture Removed, press Return to exit", Toast.LENGTH_LONG).show();
                }
            });
        }

        alertDialogBuilder.setCancelable(false).setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // get user input and set it to result
                // edit text
                resStr = userInput.getText().toString();
                change_or_insert = CHANGE_IMAGE;
                pos = position;
                selectImage();
            }
        })
                .setNegativeButton("Return",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
