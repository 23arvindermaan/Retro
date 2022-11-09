package com.example.retrofitcode;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.retrofitcode.client.Api;
import com.example.retrofitcode.client.ApiInterface;
import com.example.retrofitcode.model.DataModal;
import com.example.retrofitcode.model.Imagedata;
import com.example.retrofitcode.model.Serial;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity
{
    int ID=0;
    int PDF_REQ_CODE=1;
    ApiInterface apiInterface;
    Button  buttonChoose,buttonUpload;
    TextView editTextName;

    File file=null;
    static File pictureFile=null;
    ProgressBar progress;

    String  currentPhotoPath="";
    String BASE_URL ="https://app.novagems.io:8012/";
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int TAKE_PICTURE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());

        if (checkPermission()) {}


        apiInterface = Api.getInstance().getMyApi();
        editTextName = findViewById(R.id.editTextName);
        buttonChoose = findViewById(R.id.buttonChoose);
        buttonUpload = findViewById(R.id.buttonUpload);
        progress = findViewById(R.id.progress);
        buttonChoose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("application/pdf");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PDF_REQ_CODE);
            }
        });


        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("dataaaaa_send_","--------"+pictureFile);

                if(isNetworkAvailable(getApplicationContext()))
                {
                 if(pictureFile!=null)
                {
                    Log.e("dataaaaa__",""+pictureFile.getAbsolutePath());

                    MultipartBody.Part body;
                        try
                        {


                            RequestBody Title=RequestBody.create(MediaType.parse("text/plain"),""+editTextName.getText().toString());


                            RequestBody Pdf=RequestBody.create(MediaType.parse("application/pdf"), pictureFile);
                            body = MultipartBody.Part.createFormData("files",currentPhotoPath , Pdf);
                            Log.e("dataaaaa_vvvvvv_",""+body);

                            progress.setVisibility(View.VISIBLE);
                            uploadFile(Title,body);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Pick Photo",Toast.LENGTH_SHORT).show();
                }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



    private void uploadFile(RequestBody title, MultipartBody.Part requestFile) {

        //The gson builder
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        //creating retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        ApiInterface myApi = retrofit.create(ApiInterface.class);
        //creating a call and calling the upload image method
        Call<Imagedata> call = myApi.uploadImage(title,requestFile);

        //finally performing the call
        call.enqueue(new Callback<Imagedata>()
        {
            @Override
            public void onResponse(Call<Imagedata> call, Response<Imagedata> response)
            {
                progress.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<Imagedata> call, @NonNull Throwable t)
            {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("dataaaaa","---"+t.getMessage());
            }
        });
    }

    private void SaveImage(Bitmap finalBitmap)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "asmapp" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try
        {
            pictureFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".pdf",         /* suffix */
                    storageDir      /* directory */
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
         currentPhotoPath = pictureFile.getAbsolutePath();

        Log.e("patttttttt",""+currentPhotoPath);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        Log.e("dataaaaaxxxc",""+requestCode+"---"+resultCode+"--"+data.getData());

        if (requestCode == PDF_REQ_CODE) {
            if (resultCode == MainActivity.RESULT_OK)
            {
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                file = new File(selectedImage.getPath());
                editTextName.setText("" + file);
                SaveImage(bitmap);
            }
        }
    }

    private boolean checkPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED ||   grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                // main logic
            }
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("You need to allow access permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PDF_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".pdf",         /* suffix */
                storageDir      /* directory */
        );
    }
}