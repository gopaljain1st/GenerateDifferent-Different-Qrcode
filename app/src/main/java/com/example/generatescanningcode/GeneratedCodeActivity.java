package com.example.generatescanningcode;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class GeneratedCodeActivity extends AppCompatActivity
{
    private String message = "";
    private String type = "";
    private Button button_generate;

    private int size = 660;
    private int size_width = 660;
    private int size_height = 264;

    private TextView success_text;
    private ImageView success_imageview;

    private String time;

    private Bitmap myBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generated_code_activity);
        success_imageview=findViewById(R.id.code);
        button_generate=findViewById(R.id.saveImage);
        Intent intent=getIntent();
        message=intent.getStringExtra("message");
        type=intent.getStringExtra("type");
        BitMatrix bitMatrix = null;
        // BitMatrix bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);
        try {
            switch (type)
            {
                case "QR Code": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);break;
                case "Barcode": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_128, size_width, size_height);break;
                case "Data Matrix": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.DATA_MATRIX, size, size);break;
                case "PDF 417": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.PDF_417, size_width, size_height);break;
                case "Barcode-39":bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_39, size_width, size_height);break;
                case "Barcode-93":bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.CODE_93, size_width, size_height);break;
                case "AZTEC": bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.AZTEC, size, size);break;
                default: bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, size, size);break;
            }
        }catch (WriterException execption)
        {

        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int [] pixels = new int [width * height];
        for (int i = 0 ; i < height ; i++)
        {
            for (int j = 0 ; j < width ; j++)
            {
                if (bitMatrix.get(j, i))
                {
                    pixels[i * width + j] = 0xff000000;
                }
                else
                {
                    pixels[i * width + j] = 0xffffffff;
                }
            }
        }
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        if(bitmap!=null)
        {
          success_imageview.setImageBitmap(bitmap);
        }
        //myBitmap=bitmap;
        button_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                saveBitmap(bitmap,message,".jpg");
                LayoutInflater layoutInflater = LayoutInflater.from(GeneratedCodeActivity.this);
                View view = layoutInflater.inflate(R.layout.success_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(GeneratedCodeActivity.this);
                builder.setTitle("Summary");
                builder.setCancelable(false);
                builder.setView(view);
                success_text = (TextView) view.findViewById(R.id.success_text);
                success_text.setText(message + "\n\n" + time);
                success_imageview = (ImageView) view.findViewById(R.id.success_imageview);
                success_imageview.setImageBitmap(bitmap);
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                builder.create();
                builder.show();
            }
        });
    }
    public void saveBitmap (Bitmap bitmap, String message, String bitName)
    {
        //String fileLocation;
        String[] PERMISSIONS = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE" };
        int permission = ContextCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS,1);
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);

        String fileName = "code" + "_at_" + String.valueOf(year) + "_" + String.valueOf(month) + "_" + String.valueOf(day) + "_" + String.valueOf(hour) + "_" + String.valueOf(minute) + "_" + String.valueOf(second) + "_"  + String.valueOf(millisecond);
        time = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day) + " " + String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second) + "." + String.valueOf(millisecond);
        File file;

        String fileLocation;

        String folderLocation;

        if(Build.BRAND.equals("Xiaomi") ){
            fileLocation = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/ScanningCode/" + fileName + bitName ;
            folderLocation = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/ScanningCode/";
        }else{
            fileLocation = Environment.getExternalStorageDirectory().getPath()+"/DCIM/ScanningCode/" + fileName + bitName ;
            folderLocation = Environment.getExternalStorageDirectory().getPath()+"/DCIM/ScanningCode/";
        }

        Log.d("file_location", fileLocation);

        file = new File(fileLocation);

        File folder = new File(folderLocation);
        if (!folder.exists())
        {
            folder.mkdirs();
        }

        if (file.exists())
        {
            file.delete();
        }


        FileOutputStream out;

        try
        {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

        /*
        *
        *
        * String fileName = ""+System.currentTimeMillis();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, fileName + ".jpg");
        if (!file.exists()) {
            Log.d("path", file.toString());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        *
        *
        *
        * */
       /* String folderLocation;

        if(Build.BRAND.equals("Xiaomi") ){
            fileLocation = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/AndroidBarcodeGenerator/" + fileName + bitName ;
            folderLocation = Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/AndroidBarcodeGenerator/";
        }else{
            fileLocation = Environment.getExternalStorageDirectory().getPath()+"/DCIM/AndroidBarcodeGenerator/" + fileName + bitName ;
            folderLocation = Environment.getExternalStorageDirectory().getPath()+"/DCIM/AndroidBarcodeGenerator/";
        }
        file = new File(fileLocation);

        File folder = new File(folderLocation);
        if (!folder.exists())
        {
            folder.mkdirs();
        }

        if (file.exists())
        {
            file.delete();
        }


        FileOutputStream out;

        try
        {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

         File file;




        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("GeneratedImages", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File mypath = new File(directory, fileName+".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
        //Log.d("file_location", fileLocation);



        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
*/
        //time = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day) + " " + String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(second) + "." + String.valueOf(millisecond);
    }
}
