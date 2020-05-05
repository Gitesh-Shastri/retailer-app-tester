package com.medicento.retailerappmedi;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Otp extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText e1,e2;
    public static String verification_code;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        e1 = findViewById(R.id.phone);
        e2 = findViewById(R.id.otp);

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verification_code = s;
                Toast.makeText(Otp.this, "Code sent", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void sendSms1(View view) {
        String number = e1.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60,TimeUnit.SECONDS, this,mCallbacks
        );
    }

    public void signInWithPhone(PhoneAuthCredential credential) {

        /*mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Otp.this, "Login Succes ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Otp.this, SignUpActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                */
    }

    public void verifySms1(View view) {
        String input_code = e2.getText().toString();
        verifyPhone(verification_code, input_code);
    }

    private void verifyPhone(String verification_code, String input_code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code, input_code);
        signInWithPhone(credential);
    }
    
}
