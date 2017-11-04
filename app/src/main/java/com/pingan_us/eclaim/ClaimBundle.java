package com.pingan_us.eclaim;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
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
import java.util.Base64;
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

    private List<byte[]> morePictures;

    @TargetApi(26)
    private ClaimBundle(Parcel in) {
        final ClassLoader cl = getClass().getClassLoader();
        boolean[] boolArray = new boolean[4];
        boolean[] nullCheck = new boolean[7];
        in.readBooleanArray(boolArray);
        Log.d("Debuging!!!!!!!!!!!", "start to get list");

        time = in.readString();
        location = in.readString();
        vehicleNum = in.readString();
        vehicleID = in.readString();
        phoneOther = in.readString();
        Log.d("Debuging!!!!!!!!!!!", "start to get list");

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
//            Log.d("Debuging!!!!!!!!!!!", "start to get list");
//
//            int size = in.readInt();
//            morePictures = new ArrayList<byte[]>();
//            for(int i = 0; i < size; i++) {
//                Log.d("Debuging!!!!!!!!!!!", "list");
//
//                int length = in.readInt();
//                byte[] array = new byte[length];
//                in.readByteArray(array);
//                morePictures.add(array);
//            }
//            Log.d("Debuging!!!!!!!!!!!", "start to get list");
            imageListID = in.readString();
        }

    }

    public ClaimBundle(){
        this.driverLicense = null;
        this.otherLicense = null;
        this.otherInsurance = null;

    }

//    public void uploadStepImage(byte[] file, Window w, int id) {
//        if(id == 1) {
//            wholeScene = new ParseFile("image", file);
//            uploadImg(wholeScene, w, 0);
//        }
//        if(id == 2) {
//            yourPlate = new ParseFile("image", file);
//            uploadImg(yourPlate, w, 0);
//        }
//        if(id == 3) {
//            otherPlate = new ParseFile("image", file);
//            uploadImg(otherPlate, w, 0);
//        }
//        if(id == 4) {
//            driverLicense = new ParseFile("image", file);
//            uploadImg(driverLicense, w, 0);
//        }
//        if(id == 5) {
//            otherLicense = new ParseFile("image", file);
//            uploadImg(otherLicense, w, 0);
//        }
//        if(id == 6) {
//            otherInsurance = new ParseFile("image", file);
//            uploadImg(otherInsurance, w, 0);
//        }
//    }

    public void uploadStep1Image(List<byte[]> list, final Window w, final Context context, RelativeLayout background) {
        byte[] file = null;
        file = list.get(0);
        if(file != null)
            driverLicense = new ParseFile("image", file);
        file = list.get(1);
        if(file != null)
            otherLicense = new ParseFile("image", file);
        file = list.get(2);
        if(file != null)
            otherInsurance = new ParseFile("image", file);

        w.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        otherLicense.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    otherInsurance.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if(driverLicense != null) {
                                    driverLicense.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent intent = new Intent(context, FileClaim2Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("ClaimBundle", getThisClaim());
                                            context.startActivity(intent);
                                        }
                                    });
                                }
                                else {
                                    w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Intent intent = new Intent(context, FileClaim2Activity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("ClaimBundle", getThisClaim());
                                    context.startActivity(intent);
                                }
                            } else {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                Log.d("error!!!!!!!!!!!", e.toString());
                                w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    Log.d("error!!!!!!!!!!!", e.toString());
                    w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });
        //if(otherLicense != null) {
//            otherLicense.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        otherInsurance.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(ParseException e) {
//                                if (e == null) {
//                                    if(driverLicense != null) {
//                                        driverLicense.saveInBackground(new SaveCallback() {
//                                            @Override
//                                            public void done(ParseException e) {
//                                                w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                                Intent intent = new Intent(context, FileClaim2Activity.class);
//                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                intent.putExtra("ClaimBundle", getThisClaim());
//                                                context.startActivity(intent);
//                                            }
//                                        });
//                                    }
//                                    else {
//                                        w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                        Intent intent = new Intent(context, FileClaim2Activity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        intent.putExtra("ClaimBundle", getThisClaim());
//                                        context.startActivity(intent);
//                                    }
//                                } else {
//                                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
//                                    Log.d("error!!!!!!!!!!!", e.toString());
//                                    w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                }
//                            }
//                        });
//                    } else {
//                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
//                        Log.d("error!!!!!!!!!!!", e.toString());
//                        w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    }
//                }
//            });


    }

    public void uploadStep2Image(List<byte[]> list, final Window w, final Context context) {
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
                                            w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Intent intent = new Intent(context, FileClaim3Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("ClaimBundle", getThisClaim());
                                            context.startActivity(intent);
                                        }
                                        else {
                                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                            Log.d("error!!!!!!!!!!!", e.toString());
                                            w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                Log.d("error!!!!!!!!!!!", e.toString());
                                w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    Log.d("error!!!!!!!!!!!", e.toString());
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

    public void setStep2Bundle(final List<byte[]> singleList, final Window w, List<byte[]> list, final Context context) {
        this.morePictures = new ArrayList<byte[]>(list);
        if(list.size() == 0)
            return;
        final ParseObject imageList = new ParseObject("ImageList");
        imageList.put("list", list);
        imageList.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    imageListID = imageList.getObjectId();
                    uploadStep2Image(singleList, w, context);
                }
            }
        });

    }

    private void uploadImg(final ParseFile file, final Window w, final int i) {
        if(i == 3) {
            Log.d("error", "several failed");
        }
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    w.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                else {
                    Log.d("error", e.toString());
                    uploadImg(file, w, i + 1);
                }
            }
        });
    }

    public void uploadClaimBundle(final Window w, final Context context) {
        final ParseUser currUser = ParseUser.getCurrentUser();
        final ParseObject Claim = new ParseObject("Claim");
        Log.d("names", vehicleNum + " !!!!!!!!" + time + " !!!!!!!!!" + location + " !!!!!!!!" + vehicleID + " !!!!!!!" + phoneOther);
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
            Log.d("error", "otherInsurance is null");
        }
        Claim.put("wholeScene", wholeScene);
        Claim.put("yourPlate", yourPlate);
        Claim.put("otherPlate", otherPlate);
        //Claim.put("morePictures", morePictures);
        Claim.put("morePicturesID", imageListID);
        Toast.makeText(context, "start to upload claim", Toast.LENGTH_LONG).show();
        Claim.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.d("done", "claim upload done");
                    Toast.makeText(context, "claim built", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(context, "user updated", Toast.LENGTH_LONG).show();
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
                    Log.d("error", "error uploading claim " + e.toString());
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
        else
            Log.d("error!!!!!!!!!!!!!!!!!!", "otherInsurance null!!!!!!!!!!!!!!!!!\n\n\n\n\n\n\n");
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
        //    Log.d("Debuging!!!!!!!!!!!", "start to take care of list");
        ///    out.writeInt(morePictures.size());
        //    for(byte[] array: morePictures) {
        //        Log.d("Debuging!!!!!!!!!!!", "seqencing!!!!!!!!!!!!");
        //        out.writeInt(array.length);
        //        out.writeByteArray(array);
        //    }
        //    Log.d("Debuging!!!!!!!!!!!", "succeed!!!!!!!!!!!!!!!!!!!");
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
}
