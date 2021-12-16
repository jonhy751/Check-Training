package com.example.CheckTraining1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.CheckTraining.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText user;
    private EditText senha;
    private Button entrar;
    private FirebaseAuth mAuth;
    private TextView recuperar, cadastrar2;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Treinos");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("Usuarios");
        scoresRef.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();

        entrar = (Button) findViewById(R.id.b);
        recuperar = findViewById(R.id.RS);
        user = (EditText) findViewById(R.id.editUser);
        senha = (EditText) findViewById(R.id.editpass);
        cadastrar2 = (Button) findViewById(R.id.bcadastrar);
        cadastrar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), cadastrarActivity.class);

                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.xml.fade_in, R.xml.mover_direita);
                ActivityCompat.startActivity(MainActivity.this, Intent, activityOptionsCompat.toBundle());
            }
        });
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(senha.getText().toString())) {
                    senha.setError("Necessario senha!");
                    return;
                }
                if (TextUtils.isEmpty(user.getText().toString())) {
                    user.setError("Necessario Usuario!");
                    return;
                }


                Login(user.getText().toString(), senha.getText().toString());


            }
        });

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder pass = new AlertDialog.Builder(v.getContext());
                pass.setTitle("Email de recuperção de senha");
                pass.setMessage("Coloque seu email");
                pass.setView(resetMail);

                pass.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(resetMail.getText().toString())) {
                            resetMail.setError("Campo Vazio");
                            return;
                        }
                        String email = resetMail.getText().toString();

                        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Recupere sua senha no seu email", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Email não existe", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                pass.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                pass.show();
            }
        });


    }


    private void Login(final String user, String passw) {


        mAuth.signInWithEmailAndPassword(user, passw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot s : dataSnapshot.getChildren()) {

                                    System.out.println(s.child("flag").getValue().toString());
                                    if (user.equals(s.child("email").getValue().toString())) {
                                        String flag1 = s.child("flag").getValue().toString();

                                        if (flag1.equals("true")) {
                                            openPrincipalActivity();
                                        }else {
                                            Intent Intent = new Intent(getApplicationContext(), CalendarioAluno.class);
                                            startActivity(Intent);
                                            finish();
                                        }

                                    }


                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    Toast.makeText(MainActivity.this, "Bem vindo!", Toast.LENGTH_SHORT).show();


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Autenticação falhou.",
                            Toast.LENGTH_SHORT).show();

                }

                // ...
            }
        });
    }

    private Boolean UsuarioConectado() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String uid = mAuth.getUid();


        if (UsuarioConectado()) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot s : dataSnapshot.getChildren()) {

                            if (mAuth.getCurrentUser().getEmail().equals(s.child("email").getValue(String.class))) {


                                System.out.println(s.child("flag").getValue(String.class));
                                if (s.child("flag").getValue(String.class).equals("true")) {
                                    openPrincipalActivity();

                                } else {
                                    Intent Intent = new Intent(getApplicationContext(), CalendarioAluno.class);
                                    startActivity(Intent);
                                    finish();
                                    System.out.println("NÂO ERA PRA tA AQII");
                                }
                            }


                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    private void openPrincipalActivity() {
        Intent Intent = new Intent(getApplicationContext(), PrincipalActivity.class);

        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(), R.xml.fade_in, R.xml.mover_direita);
        ActivityCompat.startActivity(MainActivity.this, Intent, activityOptionsCompat.toBundle());
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.xml.mover_esquerda, R.xml.fade_out);
    }
}
