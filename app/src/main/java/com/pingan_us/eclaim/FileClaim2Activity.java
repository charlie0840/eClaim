package com.pingan_us.eclaim;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class FileClaim2Activity extends AppCompatActivity implements View.OnClickListener{
    private ClaimBundle claim;
    private Window w;

    private ListView list;
    private List<Bitmap> picList;
    private List<String> titleList;
    private List<byte[]> singleByteList = new ArrayList<byte[]>();
    private List<ParseFile> fileList = new ArrayList<ParseFile>();
    private List<byte[]> byteList = new ArrayList<byte[]>();
    private ArrayList<String> strList;


    private ParseFile f1 = null, f2 = null, f3 = null;
    private int change_or_insert, pos, which_loc;
    private String resStr, userChoosenTask, claim_id;
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, INSERT_IMAGE = 1, CHANGE_IMAGE = 2,
            MY_CAMERA_REQUEST_CODE = 1, WHOLE = 3, YOUR = 4, OTHER = 5, MORE = 6;

    private View l1, l2, l3;
    private CustomList adapter;
    private GridLayout default_grid;
    private LinearLayout p1, p2, p3;
    private RelativeLayout background;
    private Button next_btn, cancel_btn;
    private ImageView whole_scene, your_plate, other_plate;
    private static FileClaim2Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileclaim2);

        claim_id = getIntent().getStringExtra("claimID");

        claim = (ClaimBundle) getIntent().getParcelableExtra("ClaimBundle");

        Toast.makeText(getApplicationContext(), "after " + claim.returnTime(), Toast.LENGTH_LONG).show();


        w = getWindow();

        activity = this;

        next_btn = (Button) findViewById(R.id.start_step3_button);
        cancel_btn = (Button) findViewById(R.id.step3_cancel_button);

        default_grid = (GridLayout) findViewById(R.id.default_pic_grid);

        background = (RelativeLayout) findViewById(R.id.fc2_background);

        p1 = (LinearLayout) default_grid.findViewById(R.id.whole_scene_section);
        p2 = (LinearLayout) default_grid.findViewById(R.id.your_plate_section);
        p3 = (LinearLayout) default_grid.findViewById(R.id.other_plate_section);

        l1 = findViewById(R.id.whole_scene);
        l2 = findViewById(R.id.your_plate);
        l3 = findViewById(R.id.other_plate);

        whole_scene = l1.findViewById(R.id.img);
        your_plate = l2.findViewById(R.id.img);
        other_plate = l3.findViewById(R.id.img);
        TextView t1 = l1.findViewById(R.id.txt);
        TextView t2 = l2.findViewById(R.id.txt);
        TextView t3 = l3.findViewById(R.id.txt);

        whole_scene.setImageDrawable(getResources().getDrawable(R.drawable.addphoto));
        your_plate.setImageDrawable(getResources().getDrawable(R.drawable.addphoto));
        other_plate.setImageDrawable(getResources().getDrawable(R.drawable.addphoto));
        t1.setText(MyAppConstants.wholeScene_FC2);
        t2.setText(MyAppConstants.yourPlate_FC2);
        t3.setText(MyAppConstants.otherPlate_FC2);

        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
        l3.setOnClickListener(this);
        next_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

        picList = new ArrayList<Bitmap>();
        titleList = new ArrayList<String>();
        strList = new ArrayList<String>();

        for(int i = 0; i < 3; i++) {
            singleByteList.add(null);
        }

        picList.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.addphoto));

        titleList.add(MyAppConstants.morePic);

        adapter = new CustomList(FileClaim2Activity.this, titleList, picList);
        list=(ListView)findViewById(R.id.photo_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Toast.makeText(FileClaim2Activity.this, "position " + position + " You Clicked at " +titleList.get(+ position) + " position " + position + " size " + titleList.size(), Toast.LENGTH_SHORT).show();
                pos = position;
                if(position == 0){
                    if(picList.size() < 10) {
                        change_or_insert = INSERT_IMAGE;
                        selectImage();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "You can upload at most 9 extra pictures", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    change_or_insert = CHANGE_IMAGE;
                    selectImage();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case 1:
                break;
            case R.id.whole_scene:
                change_or_insert = CHANGE_IMAGE;
                which_loc = WHOLE;
                selectImage();
                break;
            case R.id.your_plate:
                which_loc = YOUR;
                change_or_insert = CHANGE_IMAGE;
                selectImage();
                break;
            case R.id.other_plate:
                which_loc = OTHER;
                change_or_insert = CHANGE_IMAGE;
                selectImage();
                break;
            case R.id.start_step3_button:
                if(f1 == null) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.emptyWholeScene_FC2, Toast.LENGTH_LONG).show();
                    break;
                }
                if(f2 == null) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.emptyYourPlate_FC2, Toast.LENGTH_LONG).show();
                    break;
                }
                if(f3 == null) {
                    Toast.makeText(getApplicationContext(), MyAppConstants.emptyOtherPlate_FC2, Toast.LENGTH_LONG).show();
                    break;
                }
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                background.setAlpha((float) 0.5);
                uploadImageGroup();
                //claim.uploadStep2Image(singleByteList, w, getApplicationContext());
//                Intent intent = new Intent(getApplicationContext(), FileClaim3Activity.class);
//                intent.putExtra("ClaimBundle", claim);
//                startActivity(intent);
                break;
            case R.id.step3_cancel_button:
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
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
        //Toast.makeText(this, "camera!!!!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager pm = getApplicationContext().getPackageManager();
        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
            startActivityForResult(intent, REQUEST_CAMERA);
        else
            Toast.makeText(getApplicationContext(), "No camera detected", Toast.LENGTH_LONG).show();
    }

    private void galleryIntent() {
        Intent intent = new Intent(getApplicationContext(), MultiImageSelectorActivity.class);
        if(change_or_insert == INSERT_IMAGE) {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9 - picList.size() + 1);
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
            intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, strList);
        }
        else {
            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        }
        startActivityForResult(intent, SELECT_FILE);
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
            File image = new File(path.get(i));
            final Uri currUri = Uri.fromFile(image);

            //bm = Bitmap.createBitmap(Utility.compressImageUri(currUri, 1024, 768, getApplicationContext()));

            //decode bitmap from uri
            ReceiverThread thread = new ReceiverThread(currUri, 900, 700, getApplicationContext());
            Thread th = new Thread(thread);
            th.start();
            try {
                th.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bm = Bitmap.createBitmap(thread.getBitmap());

                        //BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));

            Bitmap bitmap = Bitmap.createScaledBitmap(bm, bm.getWidth() / 2, bm.getHeight() / 2, true);
            if(which_loc != MORE)
                uploadImg(bm, which_loc);
            if(change_or_insert == CHANGE_IMAGE)
                setList(bitmap);
            else {
                addToList(bitmap);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(destination);
            thumbnail.compress(Bitmap.CompressFormat.PNG, 70, fout);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri uri=Uri.fromFile(destination);

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
        Bitmap bmp = Bitmap.createBitmap(Utility.compressImage(uri, thumbnail, 900, 700, getApplicationContext(), true));
        if(which_loc != MORE)
            uploadImg(bmp, which_loc);
        if(change_or_insert == INSERT_IMAGE)
            addToList(resBitmap);
        else
            setList(resBitmap);
        //thumbnail.recycle();
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
        titleList.add("Tap to remove");
        adapter.notifyDataSetChanged();
    }

    public void setList(Bitmap resBitmap) {
        switch(which_loc) {
            case MORE:
                picList.set(pos, resBitmap);
                adapter.notifyDataSetChanged();
                break;
            case WHOLE:
                whole_scene.setImageBitmap(resBitmap);
                break;
            case YOUR:
                your_plate.setImageBitmap(resBitmap);
                break;
            case OTHER:
                other_plate.setImageBitmap(resBitmap);
                break;
        }
    }

    private void uploadImg(final Bitmap bmp, final int action) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        final ParseFile file = new ParseFile("imageID", byteArray);
        if(action == WHOLE) {
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            //claim.uploadStepImage(byteArray, w, 1);
            singleByteList.set(0, byteArray);
            f1 = file;
        }
        else if(action == YOUR) {
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            //claim.uploadStepImage(byteArray, w, 2);
            singleByteList.set(1, byteArray);
            f2 = file;
        }
        else if(action == OTHER) {
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            //claim.uploadStepImage(byteArray, w, 3);
            singleByteList.set(2,byteArray);
            f3 = file;
        }
//        file.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if(e == null) {
//                    bmp.recycle();
//                    Toast.makeText(getApplicationContext(), "All good! start to upload data!!!", Toast.LENGTH_LONG).show();
//                }
//                else {
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    Toast.makeText(getApplicationContext(), "pic uploading went wrong!!! " + e.toString(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    private void uploadImageGroup() {
        byteList = new ArrayList<byte[]>();
        List<String> strList = new ArrayList<>();
        int i = 0;
        for(Bitmap bmp:picList) {
            i++;
            if(i == 1)
                continue;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Log.d("DEBUG!!!!!!!!!!!", "" + byteArray);
            strList.add(Base64.encodeToString(byteArray, Base64.DEFAULT));
            byteList.add(byteArray);
            //bmp.recycle();
        }
        claim.setStep2Bundle(singleByteList, w, strList, byteList.get(0), getApplicationContext(), background);
        //claim.setStep2Bundle(byteList);
    }

//    public void uploadData() {
//        Toast.makeText(getApplicationContext(), "uploading Data!!!", Toast.LENGTH_LONG).show();
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Claim");
//        query.getInBackground(claim_id, new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if(e == null) {
//                    object.put("wholeScene", f1);
//                    object.put("yourPlate", f2);
//                    object.put("otherPlate", f3);
//                    object.put("morePictures", byteList);
//                    object.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if(e == null) {
//                                Toast.makeText(getApplicationContext(), "claim uploading all good!!!!!!!", Toast.LENGTH_LONG).show();
//                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                Intent intent = new Intent(getApplicationContext(), FileClaim3Activity.class);
//                                intent.putExtra("claimID", claim_id);
//                                startActivity(intent);
//                            }
//                            else {
//                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                Toast.makeText(getApplicationContext(), "claim uploading went wrong!!! " + e.toString(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//                }
//                else {
//                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }

    public static FileClaim2Activity getInstance() {
        return activity;
    }

    @Override
    public void onBackPressed() {}
}
