package com.example.generatescanningcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    EditText id,nName,generatedId,product,towerId,radioUnitId;
    Button generate;
    Spinner type;
    String message="",typeString="";
    TextView latitude,longitude;
    Location currentLocation;
    boolean flag=false;
    SharedPreferences sp=null;
    private static  final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    FusedLocationProviderClient fusedLocationProviderClient;
    int sid;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_generate);
        initComponent();
        sp=getSharedPreferences("serialNo",MODE_PRIVATE);
        if(sp.getInt("id",-1)==-1)
        {
            SharedPreferences.Editor editor=sp.edit();
            editor.putInt("id",0);
            editor.commit();
        }
        sid=sp.getInt("id",0);
        sid+=1;
        nName.setText(""+sid);
        typeString="QR Code";
        //generatedId.setText(""+System.currentTimeMillis());
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocatin();
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0: typeString = "QR Code";break;
                    case 1: typeString = "Barcode";break;
                    case 2: typeString = "Data Matrix";break;
                    case 3: typeString = "PDF 417";break;
                    case 4: typeString = "Barcode-39";break;
                    case 5: typeString = "Barcode-93";break;
                    case 6: typeString = "AZTEC";break;
                    default: typeString = "QR Code";break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(id.getText().toString().trim().equals("")||nName.getText().toString().trim().equals("")
                ||generatedId.getText().toString().trim().equals("")||product.getText().toString().trim().equals(""))
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("Error");
                    dialog.setMessage("Please Fill All The Details");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.create();
                    dialog.show();
                }
                else if(!flag)
                {
                    Toast.makeText(MainActivity.this, "Please Give Permission To Access Your Current Location", Toast.LENGTH_SHORT).show();
                    if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);

                    }
                }
                else
                {
                    message="";
                    message+="Transaction Id :"+id.getText().toString().trim()+"\n";
                    message+="Serial Number : "+nName.getText().toString().trim()+"\n";
                    message+="Quantity : "+generatedId.getText().toString().trim()+"\n";
                    message+="Asset Id : "+product.getText().toString().trim()+"\n";
                    message+="Tower Id : "+towerId.getText().toString().trim()+"\n";
                    message+="Radio Unit Id : "+radioUnitId.getText().toString();
                //    message+=latitude.getText().toString()+"\n";
                  //  message+=longitude.getText().toString();
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putInt("id",sid);
                    editor.commit();
                    Intent intent=new Intent(MainActivity.this,GeneratedCodeActivity.class);
                    intent.putExtra("message",message);
                    intent.putExtra("type",typeString);
                    startActivity(intent);
                }
            }
        });
    }
    private void fetchLastLocatin() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
            return;
        }

        Task<Location> task=fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    flag=true;
                    currentLocation=location;
                   // latitude.setText("Latitude : "+currentLocation.getLatitude());
                    //longitude.setText("Longitude : "+currentLocation.getLongitude());
                    }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_LOCATION_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    flag=true;
                    fetchLastLocatin();
                }
                break;
        }
    }

    private void initComponent() {
       id=findViewById(R.id.id);
       nName=findViewById(R.id.nName);
       generatedId=findViewById(R.id.generatedId);
       product=findViewById(R.id.product);
       radioUnitId=findViewById(R.id.radioUnitId);
       towerId=findViewById(R.id.towerId);
       generate=findViewById(R.id.generate);
       type=findViewById(R.id.type_spinner);
       latitude=findViewById(R.id.latitude);
       longitude=findViewById(R.id.longitude);
    }
}
