package com.example.generatescanningcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    EditText id,nName,generatedId,product;
    Button generate;
    Spinner type;
    TextView latitude,longitude;
    String message="",typeString="";
    Location currentLocation;
    private static  final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        typeString="QR Code";
        generatedId.setText(""+System.currentTimeMillis());
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
                else
                {
                    message="";
                    message+="Id :"+id.getText().toString().trim()+"\n";
                    message+="NName : "+nName.getText().toString().trim()+"\n";
                    message+="Generated ID : "+generatedId.getText().toString().trim()+"\n";
                    message+="Product : "+product.getText().toString().trim();
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
                    currentLocation=location;
                   // Log.d("curent","current"+currentLocation);
                    Toast.makeText(MainActivity.this, ""+currentLocation.getLatitude()+" "+currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    latitude.setText("Latitude : "+currentLocation.getLatitude());
                    longitude.setText("Longitude : "+currentLocation.getLongitude());
                    //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    //mapFragment.getMapAsync(MapActivity.this);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE_LOCATION_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
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
       generate=findViewById(R.id.generate);
       type=findViewById(R.id.type_spinner);
       latitude=findViewById(R.id.latitude);
       longitude=findViewById(R.id.longitude);
    }
}
