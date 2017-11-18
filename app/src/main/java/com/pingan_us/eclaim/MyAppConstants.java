package com.pingan_us.eclaim;

/**
 * Created by yshui on 10/23/17.
 */

public class MyAppConstants {

    public static final String wholeScene_FC2 = "Whole scene";
    public static final String emptyWholeScene_FC2 = "Please upload the picture of whole scene";
    public static final String yourPlate_FC2 = "Your Plate";
    public static final String emptyYourPlate_FC2 = "Please upload the picture of your plate";
    public static final String otherPlate_FC2 = "Other Plate";
    public static final String emptyOtherPlate_FC2 = "Please upload the picture of other's plate";
    public static final String morePic = "More Pictures";

    public static final String tapToRemove = "Tap here to remove";
    public static final String selectTime = "Please select time to continue";

    public static final String regUNEmpty = "user name must not be empyt";
    public static final String regFNEmpty = "first name must not be empty";
    public static final String regLNEmpty = "last name must not be empty";
    public static final String regPhoneEmpty = "phone number must not be empty";
    public static final String regEAEmpty = "email address must not be empty";
    public static final String regPWLength = "length of password must be at least 2";
    public static final String regUPEmpty = "please upload the photo of your driver license";
    public static final String regPWMatch = "Passwordwords do not match";
}

/*asd
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
                    Toast.makeText(getApplicationContext(), "Access Denied, please grant eClaim the access to gallery/camera!", Toast.LENGTH_LONG).show();
                }
                break;
            case MY_CAMERA_REQUEST_CODE:
                cameraIntent();
                break;
        }
    }

    public void addToList(Bitmap resBitmap) {
        String str = titleList.get(titleList.size() - 1);
        titleList.add(str);
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

        Button rmv_btn;

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        userInput.setText(currStr);

        userInput.setFocusable(false);

        if(position > 2) {
            userInput.setFocusable(true);
            rmv_btn = (Button) promptsView.findViewById(R.id.remove_button);
            rmv_btn.setVisibility(View.VISIBLE);
            rmv_btn.setClickable(true);
            rmv_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    titleList.remove(position);
                    picList.remove(position);
                    adapter.notifyDataSetChanged();
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
    } */