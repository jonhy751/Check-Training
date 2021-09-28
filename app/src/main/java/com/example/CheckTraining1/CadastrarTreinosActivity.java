package com.example.CheckTraining1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.CheckTraining.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Classes.TipoTreino;

public class CadastrarTreinosActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Treinos");
    EditText treino, data, time, descricao, local, obs;
    private TipoTreino t;
    Button c,voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_treinos);
        c = (Button) findViewById(R.id.bCadastrarT);
        voltar = (Button) findViewById(R.id.bvoltar);
        treino = (EditText) findViewById(R.id.eTipoTreino);
        data = (EditText) findViewById(R.id.eData);
        time = (EditText) findViewById(R.id.ehorario);
        descricao = (EditText) findViewById(R.id.edescricao);
        local = (EditText) findViewById(R.id.elocal);
        obs = (EditText) findViewById(R.id.eobs);


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                startActivity(Intent);
                finish();
            }
        });
        t = new TipoTreino();
        c.setOnClickListener(new View.OnClickListener() {
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

                String[] dias = data.getText().toString().split("/");
                int dia,mes,ano;
                dia= Integer.parseInt(dias[0]);
                mes= Integer.parseInt(dias[1]);
                ano= Integer.parseInt(dias[2]);
               if(dia>31){
                   data.setError("Dia não existe");
                   return;
               }
               if(mes>12){
                   data.setError("Mes não existe");
                   return;
               }
               if(ano>3000){
                   data.setError("Ano muito alto");
                   return;
               }

                CadastrarTreino(treino.getText().toString(), data.getText().toString(), time.getText().toString(), descricao.getText().toString(), local.getText().toString(), obs.getText().toString());
                treino.setText("");
                data.setText("");
                time.setText("");
                descricao.setText("");
                local.setText("");
                obs.setText("");
            }
        });
    }

    public void CadastrarTreino(String tipo, String dat, String time, String des, String loc, String obs1) {

        String key = myRef.child("Treinos").push().getKey();
        t.setTipoTreino(tipo);
        t.setData(dat);
        t.setTime(time);
        t.setDescriçao(des);
        t.setLocal(loc);
        t.setObs(obs1);

        myRef.child(key).setValue(t);
        if (t == null) {
            AlertDialog.Builder msg = new AlertDialog.Builder(CadastrarTreinosActivity.this);
            msg.setTitle("Algo deu errado ");
            msg.setMessage("Verifique todos os campos Obrigatorios");
            msg.show();
        } else {
            Toast.makeText(this, "Cadastrado", Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.xml.mover_esquerda,R.xml.fade_out);
    }
}
