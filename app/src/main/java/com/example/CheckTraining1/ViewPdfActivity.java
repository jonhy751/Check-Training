package com.example.CheckTraining1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.CheckTraining.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ViewPdfActivity extends AppCompatActivity {
    TextView tv, tvdata;
    Button salvar,voltar;
    private static final int CREATEPDF = 1;
    Bitmap scale, bitmap;
    String dados, d;
    ListView List;
    private String Data, obs, Time, TipoTreino, DATA, descricao, local, data2;
    ArrayList<String> Dados = new ArrayList<String>();
    ArrayList<String> dados1 = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    int var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moldurapdf);

        salvar = (Button) findViewById(R.id.bsalvarr);
        List = (ListView) findViewById(R.id.list);
        tvdata = (TextView) findViewById(R.id.tvdata);
        tvdata.setText("Na data " + getIntent().getStringExtra("data") + " haverá:");
        dados = getIntent().getStringExtra("dados");
        Dados = preenherDados();


        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = getIntent().getStringExtra("data");
                savepdf(data);
            }
        });


    }

    private ArrayList<String> preenherDados() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Treinos").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        Data = (String) s.child("data").getValue(String.class);
                        String flag = (String) s.child("instituicao").getValue(String.class);
                        String data = getIntent().getStringExtra("data");
                        String instituicao = getIntent().getStringExtra("ins");

                        if (data.equals(Data) && instituicao.equals(flag)) {

                            Time = s.child("time").getValue().toString();
                            TipoTreino = s.child("tipoTreino").getValue().toString();
                            descricao = s.child("descriçao").getValue().toString();
                            local = s.child("local").getValue().toString();
                            obs = s.child("obs").getValue().toString();
                            d = ("\n  Tipo do treino: " + TipoTreino + "\n  Horário do Treino: " + Time
                                    + "\n  Descrição do treino: " + descricao + "\n  Local:" + local + "\n  Observações: " + obs );


                            dados1.add(d);
                            arrayAdapter = new ArrayAdapter<String>(ViewPdfActivity.this, android.R.layout.simple_list_item_1, dados1);
                            List.setAdapter(arrayAdapter);

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


        return dados1;
    }
    private ArrayList<String> listarPDF() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Treinos").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        Data = (String) s.child("data").getValue(String.class);
                        String flag = (String) s.child("instituicao").getValue(String.class);
                        String data = getIntent().getStringExtra("data");
                        String instituicao = getIntent().getStringExtra("ins");

                        if (data.equals(Data) && instituicao.equals(flag)) {

                            Time = s.child("time").getValue().toString();
                            TipoTreino = s.child("tipoTreino").getValue().toString();
                            descricao = s.child("descriçao").getValue().toString();
                            local = s.child("local").getValue().toString();
                            obs = s.child("obs").getValue().toString();
                            d = ("\n  Tipo do treino: " + TipoTreino + "\n  Horário do Treino: " + Time
                                    + "\n  Descrição do treino: " + descricao + "\n  Local:" + local + "\n  Observações: " + obs );


                            dados1.add(d);


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


        return dados1;
    }


    private void savepdf(String data) {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "Aplicativo -" + data);

        startActivityForResult(intent, CREATEPDF);

    }

    private void gravarPdf(Uri caminhDoArquivo, PdfDocument pdfDocument) {
        try {
            BufferedOutputStream stream = new BufferedOutputStream(Objects.requireNonNull(getContentResolver().openOutputStream(caminhDoArquivo)));
            pdfDocument.writeTo(stream);
            pdfDocument.close();
            stream.flush();
            Toast.makeText(this, "PDF Gravado Com Sucesso", Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Erro de arquivo não encontrado", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Erro de entrada e saída", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Erro desconhecido" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATEPDF) {
            if (data.getData() != null) {
                Uri caminhDoArquivo = data.getData();
                PdfDocument pdfDocument = new PdfDocument();
                Paint paint = new Paint();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1240, 1754, 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(36f);
                paint.setFakeBoldText(true);


                scale = Bitmap.createScaledBitmap(bitmap, 1240, 1754, true);
                canvas.drawBitmap(scale, 0, 0, paint);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setTextSize(35f);
                paint.setFakeBoldText(false);


                ArrayList<String> array = new ArrayList<String>();
                array=listarPDF();

                var = 230;

                String[] lis = array.toString().split("  ");

                canvas.drawText("Nesse dia haverá..", pageInfo.getPageWidth() / 3, 150, paint);
                int count =0;
                for (int i = 1; i < lis.length-1; i++) {

                    String var2 = lis[i];
                    canvas.drawText(lis[i]+"\n\n", pageInfo.getPageWidth() / 7, var, paint);

                    var = var + 50;

                    count++;
                    if (count==5){
                        var=var+100;
                        count=0;
                    }

                }
                pdfDocument.finishPage(page);
                gravarPdf(caminhDoArquivo, pdfDocument);

            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.xml.mover_esquerda, R.xml.fade_out);
    }
}
