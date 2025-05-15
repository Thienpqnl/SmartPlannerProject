package com.thien.smart_planner_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisActivity extends AppCompatActivity {
    FirebaseAuth mAuth ;
    EditText name, mail, pass;
    Button btnDone;
    FirebaseUser user;
    TextView signIn;
    @Override
    protected void onCreate(Bundle bundle) {

        super.onCreate(bundle);

        setContentView(R.layout.regis_layout);

        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.regisName);
        mail = findViewById(R.id.regisEmail);
         pass = findViewById(R.id.regisPasseword);
         signIn = findViewById(R.id.signIn);

         signIn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(RegisActivity.this, LoginActivity.class);

                 startActivity(intent);
             }
         });
        EditText repass = findViewById(R.id.regisRePass);

        Button btn = findViewById(R.id.regisBtn);
         btnDone = findViewById(R.id.btnDone);
        btn.setOnClickListener(v -> {
            String passString = pass.getText().toString();
            String rePassString = repass.getText().toString();
            if (!passString.equals(rePassString)) {

                Toast.makeText(RegisActivity.this,
                        "mat khau nhap lai khong chinh xac", Toast.LENGTH_SHORT).show();

                 return;
            }

            registerUser(mail.getText().toString(), passString);
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneAction();
            }
        });
    }

    private void doneAction() {
        // Khi nhấn nút "Tôi đã xác thực"
            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (user.isEmailVerified()) {
                        // Đã xác thực => chuyển role
                        Intent intent = new Intent(RegisActivity.this, RoleSelectorActivity.class);
                        intent.putExtra("regisEmail", mail.getText().toString());
                        intent.putExtra("regisName", name.getText().toString());
                        intent.putExtra("regisPass", pass.getText().toString());
                        intent.putExtra("uid", user.getUid());
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Email chua duoc xac thuc", Toast.LENGTH_SHORT).show();
                    }
                });
            }
    }

    private void registerUser(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();

                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verifyTask -> {
                                        if (verifyTask.isSuccessful()) {
                                            String uid = user.getUid();
                                            Toast.makeText(this, "Dang ky thanh cong. Vui long xac thuc email truoc khi dang nhap uid: " + uid, Toast.LENGTH_LONG).show();

                                            btnDone.setVisibility(View.VISIBLE);
                                        }
                                         else {
                                            Toast.makeText(this, "Khong gui duoc email xac thuc: " + verifyTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }});
                        }

                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "Email da duoc dang ky", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Dang ky that bai: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
