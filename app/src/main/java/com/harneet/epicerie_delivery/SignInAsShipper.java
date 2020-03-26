package com.harneet.epicerie_delivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import com.harneet.epicerie_delivery.Common.Common;
import com.harneet.epicerie_delivery.Model.Shipper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignInAsShipper extends AppCompatActivity {

    FButton btn_signIn;
    MaterialEditText Phone, Password;

    FirebaseDatabase database;
    DatabaseReference shippers;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/restaurant_font.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_signin);


        btn_signIn = (FButton)findViewById(R.id.btnSignIn);
        Phone = (MaterialEditText)findViewById(R.id.edtPhone);
        Password = (MaterialEditText)findViewById(R.id.edtPassword);
        Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Password.setTransformationMethod(new PasswordTransformationMethod());

        //Init firebase
        database = FirebaseDatabase.getInstance();
        shippers = database.getReference(Common.SHIPPER_TABLE);

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(Phone.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void login(String phone, final String password) {

        final ProgressDialog mDialog = new ProgressDialog(SignInAsShipper.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        shippers.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    mDialog.dismiss();
                    Shipper shipper = dataSnapshot.getValue(Shipper.class);
                    if(shipper.getPassword().equals(password))
                    {
                        //Login success

                        startActivity(new Intent(SignInAsShipper.this, HomeActivity.class));
                        Common.currentShipper = shipper;
                        finish();
                    }
                    else if (shipper.getPhone() == null){
                        Toast.makeText(SignInAsShipper.this, "Your Phone Number is Empty!", Toast.LENGTH_SHORT).show();
                    }
                    else if (shipper.getPassword() == null){
                        Toast.makeText(SignInAsShipper.this, "Your Password is Empty!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(SignInAsShipper.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(SignInAsShipper.this, "User not exists in Database!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
