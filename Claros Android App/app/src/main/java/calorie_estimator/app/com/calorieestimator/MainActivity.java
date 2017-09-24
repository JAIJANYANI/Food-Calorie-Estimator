package calorie_estimator.app.com.calorieestimator;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Random;

import static calorie_estimator.app.com.calorieestimator.R.id.my_text_id;
import static calorie_estimator.app.com.calorieestimator.R.id.textView2;
import static java.lang.System.out;

//import static calorie_estimator.app.com.calorieestimator.R.id.textView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSubmit, btnCamera;
    private ImageView ivImage;
    private ConnectionDetector cd;
    private Boolean upflag = false;
    private Uri selectedImage = null;
    private Bitmap bitmap, bitmapRotate;
    private ProgressDialog pDialog;
    String imagepath = "";
    String buffer = "";
    String fname;
    String s3;

    File file;
//    TextView newtext;
 //   TextView newtext = (TextView) findViewById(my_text_id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //        Object initialization
        cd = new ConnectionDetector(MainActivity.this);
        TextView newtext = (TextView) findViewById(my_text_id);
        //TextView newtext2 = (TextView) findViewById(textView2);
        newtext.setText("Response");
        //newtext2.setText("Response2");


        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        //TextView textView1 = (TextView)findViewById(textView);
        //textView = (TextView) findViewById(textView);

        cd = new ConnectionDetector(getApplicationContext());

        btnSubmit.setOnClickListener(this);
        btnCamera.setOnClickListener(this);
        Intent cameraintent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraintent, 101);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCamera:

                Intent cameraintent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraintent, 101);


                break;
            case R.id.btnSubmit:
                if (cd.isConnectingToInternet()) {
                    if (!upflag) {
                        Toast.makeText(MainActivity.this, "Image Not Captured..!", Toast.LENGTH_LONG).show();
                    } else {
                        saveFile(bitmapRotate, file);

/*

                        // Creating HTTP client
                        HttpClient httpClient = new DefaultHttpClient();
                        // Creating HTTP Post
                        HttpPost httpPost = new HttpPost(
                                "http://35.184.99.106:5000/");

                        // Building post parameters
                        // key and value pair
                        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                        nameValuePair.add(new BasicNameValuePair("key" , "./image.jpg"));


                        // Url Encoding the POST parameters
                        try {
                            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                        } catch (UnsupportedEncodingException e) {
                            // writing error to Log
                            e.printStackTrace();
                        }

                        // Making HTTP Request
                        try {
                            HttpResponse response = httpClient.execute(httpPost);

                            // writing response to log
                            Log.d("Http Response:", response.toString());
                        } catch (ClientProtocolException e) {
                            // writing exception to log
                            e.printStackTrace();
                        } catch (IOException e) {
                            // writing exception to log
                            e.printStackTrace();

                        }




*/


                    }
                } else {
                    Toast.makeText(MainActivity.this, "No Internet Connection !", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode) {
                case 101:
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            selectedImage = data.getData(); // the uri of the image taken
                            if (String.valueOf((Bitmap) data.getExtras().get("data")).equals("null")) {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            } else {
                                bitmap = (Bitmap) data.getExtras().get("data");
                            }
                            if (Float.valueOf(getImageOrientation()) >= 0) {
                                bitmapRotate = rotateImage(bitmap, Float.valueOf(getImageOrientation()));
                            } else {
                                bitmapRotate = bitmap;
                                bitmap.recycle();
                            }

                            ivImage.setVisibility(View.VISIBLE);
                            ivImage.setImageBitmap(bitmapRotate);
                            //TextView newtext = (TextView) findViewById(my_text_id);

                            //newtext.setText("Hello Again");



//                            TextView newtext = (TextView) findViewById(my_text_id);
//                            newtext.setVisibility(View.VISIBLE);
//                            newtext.setText("Hello Again");




//                            Saving image to mobile internal memory for sometime
                            String root = getApplicationContext().getFilesDir().toString();
                            File myDir = new File(root + "/androidlift");
                            myDir.mkdirs();

                            Random generator = new Random();
                            int n = 10000;
                            n = generator.nextInt(n);

//                            Give the file name that u want
                            fname = "Image" + n + ".jpg";

                            imagepath = root + "/androidlift/" + fname;
                            file = new File(myDir, fname);
                            upflag = true;
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    //    In some mobiles image will get rotate so to correting that this code will help us
    private int getImageOrientation() {
        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns, null, null, imageOrderBy);

        if (cursor.moveToFirst()) {
            int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            out.println("orientation===" + orientation);
            cursor.close();
            return orientation;
        } else {
            return 0;
        }
    }

    //    Saving file to the mobile internal memory
    private void saveFile(Bitmap sourceUri, File destination) {
        if (destination.exists()) destination.delete();
        try {
            FileOutputStream out = new FileOutputStream(destination);
            sourceUri.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();
            if (cd.isConnectingToInternet()) {
                new DoFileUpload().execute();
            } else {
                Toast.makeText(MainActivity.this, "No Internet Connection..", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DoFileUpload extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("wait uploading Image..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(imagepath);
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload("http://35.184.99.106/file_upload.php", "ftitle", "fdescription", fname);

                upflag = hfu.Send_Now(fstrm);


                    s3 = Global.s2;
                    System.out.println(s3);
                    //TextView newtext = (TextView) findViewById(my_text_id);
                    //newtext.setText(s3);



/*

                String url = "http://35.184.99.106/file_upload.php?data=./image1.jpg";
                //String url = "http://35.184.99.106:5001/";
                HttpClient client = new DefaultHttpClient();

                try {
                    HttpResponse resp =  client.execute(new HttpGet(url));

                    InputStream is = resp.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while((line = reader.readLine()) != null){
                        str.append(line + "n");
                    }
                    System.out.println("Hello");
                    System.out.println(str);
                    System.out.println(fname);
                    is.close();

//                    str.append(buffer);
//                    System.out.print(buffer);
//
//                    str = str.toString();
//                    TextView t = (TextView) findViewById(R.id.textView);
//                    t.setVisibility(View.VISIBLE);
//                    t.setText(str.toString());
//                    t.setText("Hello Jai!");


                } catch(IOException e) {
                    //do something here
                }


*/





            } catch (FileNotFoundException e) {
                // Error: File not found
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {

            TextView newtext = (TextView) findViewById(my_text_id);
//            newtext.setVisibility(View.VISIBLE);
//            newtext.setText(s3);
//            startActivity(new Intent(MainActivity.this, Main2Activity.class));
//            setContentView(R.layout.activity_main2);
//            TextView newtext2 = (TextView) findViewById(textView2);
//            newtext2.setText(s3);
            Intent intent=new Intent(MainActivity.this, Main2Activity.class);
            intent.putExtra("TextView","Button 3 selected");
            TextView newtext2 = (TextView) findViewById(textView2);
            newtext.setText((s3));
            startActivity(intent);




            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (upflag) {
                Toast.makeText(getApplicationContext(), "Uploading Complete", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Unfortunately file is not Uploaded..", Toast.LENGTH_LONG).show();
            }
        }
    }
}