package com.pingan_us.eclaim;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yshui on 11/2/17.
 */

public class ClaimBundle implements Parcelable{
    private int mData;

    private boolean injured;
    private boolean drivable;
    private boolean atScene;
    private boolean person;

    private String time = "";
    private String location = "";
    private String vehicleID = "";
    private String vehicleNum = "";
    private String phoneOther = "";
    private String imageListID = "";

    private ParseFile yourPlate;
    private ParseFile otherPlate;
    private ParseFile wholeScene;
    private ParseFile driverLicense;
    private ParseFile otherLicense;
    private ParseFile otherInsurance;

    private List<String> morePictures;

    @TargetApi(26)
    private ClaimBundle(Parcel in) {
        final ClassLoader cl = getClass().getClassLoader();
        boolean[] boolArray = new boolean[4];
        boolean[] nullCheck = new boolean[7];
        in.readBooleanArray(boolArray);
        injured = boolArray[0];
        drivable = boolArray[1];
        atScene = boolArray[2];
        person = boolArray[3];

        time = in.readString();
        location = in.readString();
        vehicleNum = in.readString();
        vehicleID = in.readString();
        phoneOther = in.readString();

        in.readBooleanArray(nullCheck);
        if(nullCheck[0])
            driverLicense = (ParseFile) in.readValue(cl);
        if(nullCheck[1])
            otherLicense = (ParseFile) in.readValue(cl);
        if(nullCheck[2])
            otherInsurance = (ParseFile) in.readValue(cl);
        if(nullCheck[3])
            wholeScene = (ParseFile) in.readValue(cl);
        if(nullCheck[4])
            yourPlate = (ParseFile) in.readValue(cl);
        if(nullCheck[5])
            otherPlate = (ParseFile) in.readValue(cl);
        if(nullCheck[6]) {
            imageListID = in.readString();
        }
    }

    public ClaimBundle(){
        this.driverLicense = null;
        this.otherLicense = null;
        this.otherInsurance = null;
    }

    public void uploadStep1Image(String num, final boolean person, List<byte[]> list, final Window w, final Context context, final RelativeLayout background) {
        byte[] file = null;
        file = list.get(0);
        if(!person) {
            if (file != null)
                driverLicense = new ParseFile("image", file);
        }
        if(!num.equals("1")) {
            file = list.get(1);
            if (file != null)
                otherLicense = new ParseFile("image", file);
            file = list.get(2);
            if (file != null)
                otherInsurance = new ParseFile("image", file);
        }

        w.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(!vehicleNum.equals("1")) {
            otherLicense.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        otherInsurance.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    if (driverLicense != null && !person) {
                                        driverLicense.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                                background.setAlpha((float) 0);
                                                Intent intent = new Intent(context, FileClaim2Activity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("ClaimBundle", getThisClaim());
                                                context.startActivity(intent);
                                            }
                                        });
                                    }
                                    else {
                                        w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        background.setAlpha((float) 0);
                                        Intent intent = new Intent(context, FileClaim2Activity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("ClaimBundle", getThisClaim());
                                        context.startActivity(intent);
                                    }
                                }
                                else {
                                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                    w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    background.setAlpha((float) 0);
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        background.setAlpha((float) 0);
                    }

                }

            });
        }
        else {
            if (driverLicense != null && !person) {
                driverLicense.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        background.setAlpha((float) 0);
                        Intent intent = new Intent(context, FileClaim2Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("ClaimBundle", getThisClaim());
                        context.startActivity(intent);
                    }
                });
            }
            else {
                w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                background.setAlpha((float) 0);
                Intent intent = new Intent(context, FileClaim2Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ClaimBundle", getThisClaim());
                context.startActivity(intent);
            }
        }
    }

    public void uploadStep2Image(List<byte[]> list, final Window w, final Context context, final RelativeLayout background) {
        byte[] file = null;
        file = list.get(0);
        if(file != null)
            wholeScene = new ParseFile("image", file);
        file = list.get(1);
        if(file != null)
            yourPlate = new ParseFile("image", file);
        file = list.get(2);
        if(file != null)
            otherPlate = new ParseFile("image", file);
        w.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        wholeScene.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    yourPlate.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                otherPlate.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null) {
                                            background.setAlpha((float) 0);
                                            w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent intent = new Intent(context, FileClaim3Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("ClaimBundle", getThisClaim());
                                            context.startActivity(intent);
                                        }
                                        else {
                                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                            background.setAlpha((float) 0);
                                            w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                background.setAlpha((float) 0);
                                w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    background.setAlpha((float) 0);
                    w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    public void setStep1Bundle(boolean injured, boolean drivable, boolean atScene, boolean person,
                               String time, String location, String vehicleID, String vehicleNum, String phoneOther) {
        this.injured = injured;
        this.drivable = drivable;
        this.atScene = atScene;
        this.person = person;
        this.time = time;
        this.location = location;
        this.vehicleID = vehicleID;
        this.vehicleNum = vehicleNum;
        this.phoneOther = phoneOther;
    }

    public void setStep2Bundle(final List<byte[]> singleList, final Window w, List<String> list, final Context context, final RelativeLayout background) {
        this.morePictures = new ArrayList<String>(list);
        final ParseObject imageList = new ParseObject("ImageList");
        imageList.put("list", list);
        imageList.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    imageListID = imageList.getObjectId();
                    uploadStep2Image(singleList, w, context, background);
                }
                else {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    background.setAlpha((float) 0);
                    w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });

    }

    private void uploadImg(final ParseFile file, final Window w, final int i) {
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                else {
                    uploadImg(file, w, i + 1);
                }
            }
        });
    }

    public void uploadClaimBundle(final Window w, final Context context) {
        final ParseUser currUser = ParseUser.getCurrentUser();
        final ParseObject Claim = new ParseObject("Claim");
        Claim.put("injured", injured);
        Claim.put("drivable", drivable);
        Claim.put("atScene", atScene);
        Claim.put("vehicleNum", vehicleNum);
        Claim.put("time", time);
        Claim.put("person", person);
        Claim.put("location", location);
        Claim.put("vehicleID", vehicleID);
        Claim.put("phoneOther", phoneOther);
        if(driverLicense != null)
            Claim.put("driverLicense", driverLicense);
        if(otherLicense != null) {
            Claim.put("otherLicense", otherLicense);
        }
        if(otherInsurance != null) {
            Claim.put("otherInsurance", otherInsurance);
        }
        else {
        }
        Claim.put("wholeScene", wholeScene);
        Claim.put("yourPlate", yourPlate);
        Claim.put("otherPlate", otherPlate);
        //Claim.put("morePictures", morePictures);
        Claim.put("morePicturesID", imageListID);
        Claim.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    final String objectID = Claim.getObjectId();
                    List<String> claimList = new ArrayList<String>();
                    if(currUser.get("claimID") != null) {
                        try {
                            claimList = new ArrayList<String>((List<String>) currUser.get("claimID"));
                        }
                        catch (ClassCastException e1) {
                        }
                    }
                    claimList.add(objectID);
                    currUser.put("claimID", claimList);
                    currUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Intent intent = new Intent(context, ClaimFinishActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                            else{
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Claim");
                                query.whereEqualTo("objectID", objectID);
                                query.getInBackground(objectID, new GetCallback<ParseObject>() {
                                    public void done(ParseObject object, ParseException e) {
                                        if (e == null) {
                                            w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            object.deleteInBackground();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flag) {
        boolean[] boolArray = new boolean[] {injured, drivable, atScene, person};
        boolean[] nullCheck = new boolean[] {false, false, false, false, false, false, false};
        if(driverLicense != null)
            nullCheck[0] = true;
        if(otherLicense != null)
            nullCheck[1] = true;
        if(otherInsurance != null)
            nullCheck[2] = true;
        if(wholeScene != null)
            nullCheck[3] = true;
        if(yourPlate != null)
            nullCheck[4] = true;
        if(otherPlate != null)
            nullCheck[5] = true;
        if(morePictures != null)
            nullCheck[6] = true;
        out.writeBooleanArray(boolArray);
        out.writeString(time);
        out.writeString(location);
        out.writeString(vehicleNum);
        out.writeString(vehicleID);
        out.writeString(phoneOther);
        out.writeBooleanArray(nullCheck);
        if(nullCheck[0])
            out.writeValue(driverLicense);
        if(nullCheck[1])
            out.writeValue(otherLicense);
        if(nullCheck[2])
            out.writeValue(otherInsurance);
        if(nullCheck[3])
            out.writeValue(wholeScene);
        if(nullCheck[4])
            out.writeValue(yourPlate);
        if(nullCheck[5])
            out.writeValue(otherPlate);
        if(nullCheck[6]) {
            out.writeString(imageListID);
        }
    }

    public static final Parcelable.Creator<ClaimBundle> CREATOR = new Parcelable.Creator<ClaimBundle>() {
        public ClaimBundle createFromParcel(Parcel in) {
            return new ClaimBundle(in);
        }

        public ClaimBundle[] newArray(int size) {
            return new ClaimBundle[size];
        }
    };

    public String returnTime() {
        return time;
    }

    public ClaimBundle getThisClaim() {
        return this;
    }

    public String getImageListID() { return imageListID; }
}
