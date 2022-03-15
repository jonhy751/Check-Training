package com.example.CheckTraining1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.CheckTraining.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Classes.User;

public class cadastrarActivity extends AppCompatActivity {

    private EditText email, senha, rep, nome;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase fb = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = fb.getReference("Usuarios");
    private Button Cadastrar, voltar;
    private Spinner sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
        Cadastrar = (Button) findViewById(R.id.bcadastrar2);
        email = (EditText) findViewById(R.id.editCemail);
        senha = (EditText) findViewById(R.id.editCpass);
        nome = (EditText) findViewById(R.id.editCnome);
        rep = (EditText) findViewById(R.id.editRep);
        sp = (Spinner) findViewById(R.id.spinner);
        voltar = (Button) findViewById(R.id.bvoltar);


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(Intent);
                finish();
            }
        });

        Cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty((email.getText().toString()))) {
                    email.setError("Necessario Email!");
                    return;
                }else if (TextUtils.isEmpty(senha.getText().toString())) {
                    senha.setError("Necessario senha!");
                    return;
                }else if (senha.getText().toString().length() < 6) {
                    senha.setError("Necessario senha de 6 digitos!");
                    return;
                }else if (senha.getText().toString().equals(rep.getText().toString())) {

                } else {
                    rep.setError("Senha diferentes!");
                    return;
                } if (TextUtils.isEmpty((nome.getText().toString()))) {
                    nome.setError("Necessario nome!");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), senha.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String flag = "false";
                            User user = new User(email.getText().toString(), senha.getText().toString(), nome.getText().toString(), flag.toString(), sp.getSelectedItem().toString());
                            fb.getReference("Usuarios")
                                    .child(nome.getText().toString()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                            Toast.makeText(cadastrarActivity.this, "Usuário Cadastrado", Toast.LENGTH_SHORT).show();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                                            if (nome.getText().toString().equals(s.child("nome").getValue(String.class))) {
                                                String flag1 = s.child("flag").getValue(String.class);
                                                if (flag1.equals("true")) {
                                                    Intent Intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                                                    startActivity(Intent);
                                                    finish();
                                                } else {
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
                        } else {
                            Toast.makeText(cadastrarActivity.this, "Erro no cadastro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


}
