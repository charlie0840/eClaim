package com.pingan_us.eclaim;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class FileClaim2tActivity extends AppCompatActivity implements View.OnClickListener{
    private GridLayout default_grid;
    private View l1, l2, l3;
    private ListView list;
    private LinearLayout p1, p2, p3;
    private List<Bitmap> picList;
    private ArrayList<String> strList;
    private String resStr, userChoosenTask;
    private int change_or_insert, pos;
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, INSERT_IMAGE = 1, CHANGE_IMAGE = 2, MY_CAMERA_REQUEST_CODE = 1;
    private CustomListt adapter;
    private Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileclaim2t);



        default_grid = (GridLayout) findViewById(R.id.default_pic_grid);

        p1 = (LinearLayout) default_grid.findViewById(R.id.whole_scene_section);
        p2 = (LinearLayout) default_grid.findViewById(R.id.your_plate_section);
        p3 = (LinearLayout) default_grid.findViewById(R.id.other_plate_section);

        l1 = findViewById(R.id.whole_scene);
        l2 = findViewById(R.id.your_plate);
        l3 = findViewById(R.id.other_plate);

        ImageView I1 = l1.findViewById(R.id.img);
        ImageView I2 = l2.findViewById(R.id.img);
        ImageView I3 = l3.findViewById(R.id.img);
        TextView t1 = l1.findViewById(R.id.txt);
        TextView t2 = l2.findViewById(R.id.txt);
        TextView t3 = l3.findViewById(R.id.txt);

        I1.setImageDrawable(getResources().getDrawable(R.drawable.addphoto));
        I2.setImageDrawable(getResources().getDrawable(R.drawable.addphoto));
        I3.setImageDrawable(getResources().getDrawable(R.drawable.addphoto));
        t1.setText(MyAppConstants.wholeScene_FC2);
        t2.setText(MyAppConstants.yourPlate_FC2);
        t3.setText(MyAppConstants.otherPlate_FC2);

        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
        l3.setOnClickListener(this);

        next_btn = (Button) findViewById(R.id.start_step3_button);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FileClaim3Activity.class);
                startActivity(intent);
            }
        });

        picList = new ArrayList<Bitmap>();
        strList = new ArrayList<String>();

        picList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));
        picList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));
        picList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));

        adapter = new CustomListt(FileClaim2tActivity.this, picList);
        list=(ListView)findViewById(R.id.more_photo_list);
        list.setAdapter(adapter);
        addToList(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));

        addToList(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));
        addToList(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(position == 0){
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
                    Toast.makeText(getApplicationContext(), "start editing", Toast.LENGTH_LONG).show();
                    editList(position);
                }

            }
        });
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){
          //  case R.id.whole_scene:
           //     break;
           // case R.id.your_plate:
             //   break;
           // case R.id.other_plate:
               // break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(FileClaim2tActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(FileClaim2tActivity.this);
                if(!result)
                    Toast.makeText(getApplicationContext(), "no permission!!!!", Toast.LENGTH_LONG).show();
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                        }
                        else
                            cameraIntent();
                    }
                    else
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

        //MultiImageSelector.create(getApplicationContext()).start(Activity, SELECT_FILE);

        Intent intent = new Intent(getApplicationContext(), MultiImageSelectorActivity.class);
        if(change_or_insert == INSERT_IMAGE) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
            intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, strList);
        }
        else {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        }
        startActivityForResult(intent, SELECT_FILE);
        //Intent intent = new Intent();
        //intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
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


        List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);

        for(int i = 0; i < path.size(); i++) {
            Uri curr = Uri.parse(path.get(i));
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), curr);
                Bitmap resBitmap = Bitmap.createScaledBitmap(bm, bm.getWidth()/2, bm.getHeight()/2, true);
                if(change_or_insert == INSERT_IMAGE)
                    addToList(resBitmap);
                else
                    setList(resBitmap);
            }
            catch(IOException e){
                Toast.makeText(getApplicationContext(), "this is " + path.get(i), Toast.LENGTH_LONG).show();
            }
        }
        //if (data != null) {
        //        Uri selectedImageUri = data.getData();
        //        String[] projection = {MediaStore.MediaColumns.DATA};
        //        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
        //                null);
        //        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        //        cursor.moveToFirst();

        //        String selectedImagePath = cursor.getString(column_index);

        //}
        //Bitmap resBitmap = Bitmap.createScaledBitmap(bm, bm.getWidth()/2, bm.getHeight()/2, true);
        //bm.recycle();
        //if(change_or_insert == INSERT_IMAGE)
        //    addToList(resBitmap);
        //else{
        //    setList(resBitmap);
        //}
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
        Bitmap resBitmap = Bitmap.createScaledBitmap(thumbnail, thumbnail.getWidth()/2, thumbnail.getHeight()/2, true);
        thumbnail.recycle();
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
                    Toast.makeText(getApplicationContext(), "Access Denied, please grant eClaim the access to gallery/camera!", Toast.LENGTH_LONG).show();
                }
                break;
            case MY_CAMERA_REQUEST_CODE:
                cameraIntent();
                break;
        }
    }

    public void addToList(Bitmap resBitmap) {
        picList.add(resBitmap);
        adapter.notifyDataSetChanged();
    }

    public void setList(Bitmap resBitmap) {
        picList.set(pos, resBitmap);
        adapter.notifyDataSetChanged();
    }

    public void editList(final int position) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.popup_window, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        Button rmv_btn;

        alertDialogBuilder.setCancelable(false).setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // get user input and set it to result
                // edit text
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
