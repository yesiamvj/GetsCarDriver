package com.ulgebra.getscardriver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StartRideEntry extends AppCompatActivity {

    AutoCompleteTextView odometr;
    EditText notesTextEt;
    String odometrnum,notestxt,car_no,fromFormat,minstr,fromTime,fromDate,carcondistr,userdocstr,fuellvl,booking_idd;
    Boolean pickTSet=false;
       Switch visible_switch_inp;
    int one_img=0;
    int one_img_sts=-1;
    int fromHour,fromMin,CalendarHour,CalendarMinute;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1,FRONT=1,LEFT=2,RIGHT=3,BACK=4;
    private Button btnSelect;
    private ImageView ivImage,ImgFront,ImgLeft,ImgRight,ImgBack;
    private String userChoosenTask;
    TimePickerDialog timepickerdialog;
    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    Calendar calendar;
    int tot_en=-1;
    Button upload_btn_inp,btn1,btn2,btn3,fromTimeBtn;
    Spinner fueltype,transtype,cartype;
    CheckBox passportt,rationn,corpo,aadhar,drivinglic,stepni,tyres,jaccy,toolss;
    String[] tot_files=new String[30];
    LinearLayout LnFront,LnLeft,LnRight,LnBack;
    ProgressDialog dialog = null;
    HttpEntity resEntity;
    List<String> imagesEncodedList;
    int serverResponseCode = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        booking_idd=intent.getStringExtra("booking_idd");
        setContentView(R.layout.activity_add_new_car);

        fueltype=(Spinner) findViewById(R.id.fuel_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fuel_level, R.layout.single_spin_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fueltype.setAdapter(adapter);





        DatePicker frm_dat_pic=(DatePicker) findViewById(R.id.from_date);
        frm_dat_pic.setMinDate(System.currentTimeMillis() - 1000);

        passportt=(CheckBox) findViewById(R.id.passpo);
        aadhar=(CheckBox) findViewById(R.id.aadahr);
        corpo=(CheckBox) findViewById(R.id.corpor);
        drivinglic=(CheckBox) findViewById(R.id.drilic);
        rationn=(CheckBox) findViewById(R.id.rationc);
        stepni=(CheckBox) findViewById(R.id.stepni);
        toolss=(CheckBox) findViewById(R.id.toolss);
        tyres=(CheckBox) findViewById(R.id.tyress);
        jaccy=(CheckBox) findViewById(R.id.jacky);

        odometr=(AutoCompleteTextView)findViewById(R.id.odomet_inp);
        notesTextEt=(EditText)findViewById(R.id.notesText);


        upload_btn_inp=(Button)findViewById(R.id.upload_btn);


        fromTimeBtn=(Button) findViewById(R.id.BtnSelctFromTime);

        calendar = Calendar.getInstance();
        CalendarHour = calendar.get(Calendar.HOUR_OF_DAY);
        CalendarMinute = calendar.get(Calendar.MINUTE);

        frm_dat_pic.setMinDate(System.currentTimeMillis() - 1000);
        int frm_day = frm_dat_pic.getDayOfMonth();
        int frm_month = frm_dat_pic.getMonth() + 1;
        int frm_year = frm_dat_pic.getYear();

        if(frm_day<10){
            fromDate="0"+frm_day+"-";
        }
        else {
            fromDate=frm_day+"-";
        }
        if(frm_month<10){
            fromDate+="0"+frm_month+"-";
        }
        else {
            fromDate+=frm_month+"-";
        }

            fromDate+=frm_year;



        fromTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timepickerdialog = new TimePickerDialog(StartRideEntry.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                fromHour=hourOfDay;
                                fromMin=minute;
                                pickTSet=true;

                                if (hourOfDay == 0) {

                                    hourOfDay += 12;

                                    fromFormat = "AM";
                                }
                                else if (hourOfDay == 12) {

                                    fromFormat = "PM";

                                }
                                else if (hourOfDay > 12) {

                                    hourOfDay -= 12;

                                    fromFormat = "PM";

                                }
                                else {

                                    fromFormat = "AM";
                                }
                                if(minute<10){
                                    minstr="0"+minute;
                                }else {
                                    minstr=minute+"";
                                }

                                fromTime=hourOfDay + ":" + minstr +" "+ fromFormat;

                                fromTimeBtn.setText(hourOfDay + ":" + minstr +" "+ fromFormat);
                            }
                        }, CalendarHour, CalendarMinute, false);

                timepickerdialog.show();

            }
        });

        LnFront=(LinearLayout) findViewById(R.id.ln_img_front);
        LnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(FRONT);
            }
        });

        LnLeft=(LinearLayout) findViewById(R.id.ln_img_left);
        LnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(LEFT);
            }
        });
        LnRight=(LinearLayout) findViewById(R.id.ln_img_right);
        LnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(RIGHT);
            }
        });
        LnBack=(LinearLayout) findViewById(R.id.ln_img_back);
        LnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(BACK);
            }
        });




        userdocstr="";
        carcondistr="";
        try{


            upload_btn_inp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    odometrnum=odometr.getText().toString();
                    notestxt=notesTextEt.getText().toString();



                    if(passportt.isChecked()){
                        userdocstr+="Passport , ";
                    }

                    if(aadhar.isChecked()){
                        userdocstr+="Aadhar , ";
                    }
                    if(corpo.isChecked()){
                        userdocstr+="Corporate , ";
                    }
                    if(drivinglic.isChecked()){
                        userdocstr+="Driving Licence , ";
                    }
                    if(rationn.isChecked()){
                        userdocstr+="Ration Card , ";
                    }
                    if(jaccy.isChecked()){
                        carcondistr+="Jacky , ";
                    }
                    if(tyres.isChecked()){
                        carcondistr+="Tyres , ";
                    }
                    if(toolss.isChecked()){
                        carcondistr+="Tools , ";
                    }

                    if(stepni.isChecked()){
                        carcondistr+="Stepni , ";
                    }

                   fuellvl=fueltype.getSelectedItem().toString();


                    boolean fill_all=true;

                    if(TextUtils.isEmpty(odometrnum)){
                        fill_all=false;
                        odometr.setError("Required");
                        odometr.requestFocus();
                    }



                    if(fill_all){

                        String otp_nums = "http://luxscar.com/luxscar_app/driver_startRide.php?";


                        final String otp_url = otp_nums;

                        Log.v("add_new_car_desc",booking_idd);

                        new LongOperation().execute(otp_url);
                    }else{
                        Snackbar.make(v,"Please Fill in all fields", Snackbar.LENGTH_LONG).show();
                    }





                }
            });


        }catch (Exception e){
            Log.v("net_err",e.getMessage());
        }


        // ivImage = (ImageView) findViewById(R.id.gy);
    }

    private void selectImage(int pos) {
        final CharSequence[] items = { "Take Photo","Cancel" };
        final int pos2=pos;
//        AlertDialog.Builder builder = new AlertDialog.Builder(StartRideEntry.this);
//        builder.setTitle("Add Photo!");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                boolean result=Utility.checkPermission(StartRideEntry.this);
//
//                String userChoosenTask;
//                if (items[item].equals("Take Photo")) {
//                    userChoosenTask="Take Photo";
//                    if(result)
//                        cameraIntent(pos2);
//
//                } else if (items[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
        cameraIntent(pos2);
    }
    private void cameraIntent(int pos)
    {
        Log.v("cam_err","cam_strt");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, pos);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {


            // When an Image is picked
            if ( resultCode == Activity.RESULT_OK && null != data) {
              onCaptureImageResult(data,requestCode);
            }



        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void onCaptureImageResult(Intent data,int reqCode) {


        try{

            Bundle extras = data.getExtras();

            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Uri urils=getImageUri(this,imageBitmap);
            ImageView yourImage2;
            switch (reqCode){
                case 1:
                     yourImage2 = (ImageView) findViewById(R.id.img_front);
                    break;
                case 2:
                     yourImage2 = (ImageView) findViewById(R.id.img_left);
                    break;
                case 3:
                     yourImage2 = (ImageView) findViewById(R.id.img_right);
                    break;
                case 4:
                     yourImage2 = (ImageView) findViewById(R.id.img_back);
                    break;
                default:
                     yourImage2 = (ImageView) findViewById(R.id.img_back);
                    break;

            }

            yourImage2.setImageURI(urils);
            compressImage(urils.toString());

        }catch (Exception e){
            Log.v("caam_err",e.getMessage());
        }
    }




    public Bitmap compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 40, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(!filename.contains("content://com.")){


            if(one_img==1){
                if(one_img_sts==-1){
                    tot_en=tot_en+1;

                    one_img_sts=tot_en;
                }

                tot_files[one_img_sts]=filename;
                tot_files[one_img_sts]=filename;
            }else {
                tot_en=tot_en+1;

                tot_files[tot_en]=filename;
            }

        }
        Log.v("huijo",tot_en+"");

        return scaledBitmap;

    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public class LongOperation  extends AsyncTask<String, Void, Void> {

        // Required initialization

        // private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(StartRideEntry.this);
        String data ="";
        String otpt="";



        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //Start Progress Dialog (Message)

            Dialog.setMessage("Please wait..");
            Dialog.show();



        }

        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {

            /************ Make Post Call To Web Server ***********/
            BufferedReader reader=null;

            // Send data



            File[] all_fl=new File[50];

            for(int i=0;i<=tot_en;i++){
                Log.v("fl_nme",tot_files[i]);
                all_fl[i]=new File(tot_files[i]);
                if(all_fl[i].isFile()){
                    Log.v("fl_nm","avls"+tot_en);
                }else{
                    Log.v("fl_nm","not_avl");
                }
            }

            String urlString = "http://luxscar.com/luxscar_app/driver_startRide.php";
            try
            {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(urlString);
                FileBody[] fl_bd=new FileBody[50];

                for(int i=0;i<=tot_en;i++){
                    fl_bd[i]=new FileBody(all_fl[i]);
                }


                MultipartEntity reqEntity = new MultipartEntity();
                for(int i=0;i<=tot_en;i++){
                    reqEntity.addPart("uploaded_file"+i, fl_bd[i]);

                }

                reqEntity.addPart("jsk",new StringBody("ndjfhfjghfs"));
                reqEntity.addPart("drv_from_date",new StringBody(fromDate));
                reqEntity.addPart("drv_from_time",new StringBody(fromTime));
                reqEntity.addPart("car_condi",new StringBody(carcondistr));
                reqEntity.addPart("user_docs",new StringBody(userdocstr));
                reqEntity.addPart("odometr",new StringBody(odometrnum));
                reqEntity.addPart("fuel_levl",new StringBody(fuellvl));
                reqEntity.addPart("booking_id",new StringBody(booking_idd));
                reqEntity.addPart("extra_notes",new StringBody(notestxt));

                post.setEntity(reqEntity);

                HttpResponse response = client.execute(post);
                resEntity = response.getEntity();
                final String response_str = EntityUtils.toString(resEntity);
                if (resEntity != null) {
                    Log.i("RESPONSE_NET",response_str);
                    otpt=response_str;
                    runOnUiThread(new Runnable(){
                        public void run() {
                            try {
                                 Toast.makeText(getApplicationContext(),response_str, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            catch (Exception ex){
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            }


            /*****************************************************/
            return null;
        }


        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.


            Dialog.dismiss();



            if(otpt.hashCode()==0){
                Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_LONG).show();

            }else{
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",1);
                setResult(RESULT_OK,returnIntent);
                finish();
            }



        }

    }



}

/*else {
                    one_img=0;
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            LinearLayout layout = (LinearLayout) findViewById(R.id.ji);

                            for (int i = 0; i < mClipData.getItemCount(); i++) {


                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                Log.v("LOG_TAG", "Selected Image URi" + uri);

                                mArrayUri.add(uri);
                                LayoutInflater li = LayoutInflater.from(StartRideEntry.this);
                                View customView = li.inflate(R.layout.image_cont,null);
                                ImageView yourImage = (ImageView) customView.findViewById(R.id.fi);
                                //compressImage(uri.toString());
                                yourImage.setImageBitmap(compressImage(uri.toString()));


                                int t = i + 1;



                                if(t<mClipData.getItemCount()){
                                    ClipData.Item item2 = mClipData.getItemAt(t);
                                    Uri uri2 = item2.getUri();
                                    ImageView yourImage2 = (ImageView) customView.findViewById(R.id.fi2);

                                    yourImage2.setImageBitmap(compressImage(uri2.toString()));
                                }

                                layout.addView(customView);




                                // Get the cursor
                                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imageEncoded  = cursor.getString(columnIndex);
                                imagesEncodedList.add(imageEncoded);
                                //Log.v("LOG_TAG", "Selected Image URi" + cursor.getColumnIndex(filePathColumn[0]));
                                //ivImage.setImageURI(cursor);
                                cursor.close();
                                if(t<mClipData.getItemCount()){
                                    i=i+1;
                                }

                            }
                            Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        }
                    }

                    */
