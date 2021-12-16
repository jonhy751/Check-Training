package com.example.CheckTraining1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.CheckTraining.R;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Classes.TipoTreino;

public class CadastrarTreinosActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth auth;

    private DatabaseReference myRef = database.getReference("Treinos");
    EditText treino, data, time, descricao, local, obs;
    private TipoTreino tipoTreino;
    Button cadastrar, voltar;
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_treinos);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String emaill = user.getEmail();
        cadastrar = (Button) findViewById(R.id.bCadastrarT);
        voltar = (Button) findViewById(R.id.bvoltar);
        treino = (EditText) findViewById(R.id.eTipoTreino);
        data = (EditText) findViewById(R.id.eData);


        //Mascaras para o campo Text de horario e data
        SimpleMaskFormatter dt = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(data, dt);
        data.addTextChangedListener(mtw);
        time = (EditText) findViewById(R.id.ehorario);
        SimpleMaskFormatter tm = new SimpleMaskFormatter("NN:NN");
        MaskTextWatcher mask = new MaskTextWatcher(time, tm);
        time.addTextChangedListener(mask);



        descricao = (EditText) findViewById(R.id.edescricao);
        local = (EditText) findViewById(R.id.elocal);
        obs = (EditText) findViewById(R.id.eobs);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        String var = s.child("email").getValue().toString();

                        if (emaill.equals(var)) {
                            flag = (String) s.child("instituição").getValue();


                        } else {

                        }


                    }

                } else {
                    Log.i("MeuLOG", "erro na captura");


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                startActivity(Intent);
                finish();
            }
        });
        tipoTreino = new TipoTreino(null, null, null, null, null, null);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty((data.getText().toString()))) {
                    data.setError("Necessario Data!");
                    return;
                }
                if (TextUtils.isEmpty((time.getText().toString()))) {
                    time.setError("Necessario Horario!");
                    return;
                }
                if (TextUtils.isEmpty((treino.getText().toString()))) {
                    treino.setError("Necessario tipo de treino!");
                    return;
                }


                String[] hora = time.getText().toString().split(":");
                int h = Integer.parseInt(hora[0]);
                String[] dias = data.getText().toString().split("/");
                int dia, mes, ano;
                dia = Integer.parseInt(dias[0]);
                mes = Integer.parseInt(dias[1]);
                ano = Integer.parseInt(dias[2]);
                if (dia > 31) {
                    data.setError("Dia não existe");
                    return;
                }
                if (mes > 12) {
                    data.setError("Mes não existe");
                    return;
                }
                if (ano > 3000) {
                    data.setError("Ano muito alto");
                    return;
                }
                if (h > 24) {
                    time.setError("Horario somente em formato 24h");
                    return;
                }

                CadastrarTreino(treino.getText().toString(), data.getText().toString(), time.getText().toString(), descricao.getText().toString(),
                        local.getText().toString(), obs.getText().toString(), flag);
                treino.setText("");
                data.setText("");
                time.setText("");
                descricao.setText("");
                local.setText("");
                obs.setText("");
            }
        });
    }

    public void CadastrarTreino(String tipo, String dat, String time, String des, String loc, String obs1, final String insti) {

        String key = myRef.child("Treinos").push().getKey();
        tipoTreino.setTipoTreino(tipo);
        tipoTreino.setData(dat);
        tipoTreino.setTime(time);
        tipoTreino.setDescriçao(des);
        tipoTreino.setLocal(loc);
        tipoTreino.setObs(obs1);
        tipoTreino.setInstituicao(insti);
        myRef.child(key).setValue(tipoTreino);
        if (tipoTreino == null) {
            AlertDialog.Builder msg = new AlertDialog.Builder(CadastrarTreinosActivity.this);
            msg.setTitle("Algo deu errado ");
            msg.setMessage("Verifique todos os campos Obrigatorios");
            msg.show();
        } else {
            Toast.makeText(this, "Cadastrado", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.xml.mover_esquerda, R.xml.fade_out);
    }
}
