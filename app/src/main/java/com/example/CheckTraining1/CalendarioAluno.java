package com.example.CheckTraining1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.CheckTraining.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import Classes.TipoTreino;

public class CalendarioAluno extends AppCompatActivity {
    private Button sair,next,retur;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private CalendarView calendario;
    private TextView tv, tv2;
    private long resul;
    private static final int STORAGE_CODE = 1000;
    private DatabaseReference myRef = database.getInstance().getReference("Treinos");
    private ListView li;
    private String Data, obs, Time, TipoTreino, DATA, descricao, local, data2;
    private Bitmap bm, sbm;
    private SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
    private Date data1 = new Date();
    private String dataFormatada = formataData.format(data1);
    private static final int CREATEPDF = 1;
    Bitmap scale, bitmap;
    String Data1, ins;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String emaill = user.getEmail();

    private final int ID_MESSAGE = 2;
    private TextView mess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        marcadores();
        setContentView(R.layout.activity_calendario_aluno);
        sair = (Button) findViewById(R.id.bsair);
        mAuth = FirebaseAuth.getInstance();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moldurapdf);
        tv = (TextView) findViewById(R.id.tv);
        tv2 = (TextView) findViewById(R.id.tvuser);
        message();
        mess = (TextView) findViewById(R.id.mes2);
        next = (Button) findViewById(R.id.buttonnext);
        retur= (Button)findViewById(R.id.ButtonReturn);


        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);


        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.calendarView2);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollLeft();

            }
        });

        retur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollRight();

            }
        });
        SimpleDateFormat getMonth = new SimpleDateFormat("MMMM - YYYY");

        String mth = getMonth.format(data1);

        mth = mth.substring(0, 1).toUpperCase().concat(mth.substring(1));
        mess.setText(mth);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                String dia = dateClicked.toString();
                String[] dias = dia.split(" ");
                dia = dias[2];
                String ano = Integer.toString(dateClicked.getYear());
                int a = Integer.parseInt(ano);
                a += 1900;
                ano = String.valueOf(a);

                String mes = Integer.toString(dateClicked.getMonth() + 1);

                int m = Integer.parseInt(mes);
                if (m < 10) {
                    mes = "0" + mes;
                } else {

                }

                Data1 = dia + "/" + mes + "/" + ano;

                resgatar(Data1);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                SimpleDateFormat getMonth = new SimpleDateFormat("MMMM - YYYY");

                String mth = getMonth.format(firstDayOfNewMonth);
                mth = mth.substring(0, 1).toUpperCase().concat(mth.substring(1));
                mess.setText(mth);
                Log.i("MONTH", mth);
            }
        });


        tv.setText("Selecione uma Data");
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                Intent Intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(Intent);
            }
        });


    }

    private void savepdf(String data) {

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "Aplicativo -" + data);

        startActivityForResult(intent, CREATEPDF);

    }

    @Override
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


                canvas.drawText("Tipo treino: " + TipoTreino, pageInfo.getPageWidth() / 7, 230, paint);
                canvas.drawText("Hora: " + Time, pageInfo.getPageWidth() / 7, 260, paint);
                canvas.drawText("Local: " + local, pageInfo.getPageWidth() / 7, 290, paint);
                canvas.drawText("Observação: " + obs, pageInfo.getPageWidth() / 7, 320, paint);
                canvas.drawText("Descrição do Treino: " + descricao, pageInfo.getPageWidth() / 7, 350, paint);


                pdfDocument.finishPage(page);
                gravarPdf(caminhDoArquivo, pdfDocument);

            }
        }
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

    private void logoutt() {
        mAuth.signOut();
        finish();
        Intent Intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(Intent);
    }

    private void resgatar(String d) {

        DATA = d;
        DatabaseReference refe = FirebaseDatabase.getInstance().getReference();
        refe.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        String var = s.child("email").getValue().toString();

                        if (emaill.equals(var)) {
                            ins = (String) s.child("instituição").getValue();
                        } else {
                            System.out.println("algo errado");
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Treinos").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        Data = (String) s.child("data").getValue(String.class);
                        String flag = (String) s.child("instituicao").getValue(String.class);


                        if (DATA.equals(Data) && ins.equals(flag)) {
                            tv.setText("O treino deste dia é:");
                            Time = s.child("time").getValue().toString();
                            TipoTreino = s.child("tipoTreino").getValue().toString();
                            descricao = s.child("descriçao").getValue().toString();
                            local = s.child("local").getValue().toString();
                            obs = s.child("obs").getValue().toString();

                            String dados = ("Tipo do treino: " + TipoTreino + "\nHorário do Treino: " + Time + "h\nDescrição do treino: " + descricao + "\nLocal:" + local + "\nObservações: " + obs);
                            Intent Intent = new Intent(getApplicationContext(), ViewPdfActivity.class);
                            Intent.putExtra("dados", dados);
                            Intent.putExtra("data", Data1);
                            Intent.putExtra("Tipotreino", TipoTreino);
                            Intent.putExtra("Time", Time);
                            Intent.putExtra("des", descricao);
                            Intent.putExtra("local", local);
                            Intent.putExtra("obs", obs);
                            Intent.putExtra("ins",ins);


                            startActivity(Intent);
                            finish();

                        } else {
                            tv.setText("Não ha Treinos nessa data!");
                        }


                    }

                } else {
                    Log.i("MeuLOG", "erro na captura");
                    System.out.println(DATA);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.xml.mover_esquerda, R.xml.fade_out);
    }

    private long DataMilis(String mes, String dia, String ano) {
        Calendar calendario1 = Calendar.getInstance();


        int m = (int) Double.parseDouble(mes);
        int d = (int) Double.parseDouble(dia);
        int a = (int) Double.parseDouble(ano);

        if (m < 10) {
            String var = String.valueOf(m);
            var = "0" + var;
            m = Integer.valueOf(var);

        }


        calendario1.set(a, m - 1, d);


        long milis = calendario1.getTimeInMillis();
        return milis;


    }

    private void marcadores() {
        DatabaseReference refe = FirebaseDatabase.getInstance().getReference();
        refe.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        String var = s.child("email").getValue().toString();

                        if (emaill.equals(var)) {
                            ins = (String) s.child("instituição").getValue();


                        } else {
                            System.out.println("algo errado");
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


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("Treinos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        String flag = s.child("instituicao").getValue().toString();
                        Data = (String) s.child("data").getValue(String.class);
                        if (ins.equals(flag)) {
                            final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.calendarView2);
                            compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
                            compactCalendarView.setUseThreeLetterAbbreviation(true);


                            String frase = Data.toString();
                            frase = frase.replaceAll("/", "");
                            frase = frase.replaceAll(" ", "");


                            String num = frase.substring(0, 2);
                            String m = frase.substring(2, 4);
                            String ano = frase.substring(4);


                            resul = DataMilis(m, num, ano);

                            Event ev1 = new Event(Color.GREEN, resul, "Some extra data that I want to store.");
                            compactCalendarView.addEvent(ev1);
                            resul = 0;
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


    }

    public void message() {
        DatabaseReference refe = FirebaseDatabase.getInstance().getReference();
        refe.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        String var = s.child("email").getValue().toString();

                        if (emaill.equals(var)) {
                            ins = (String) s.child("nome").getValue();
                            tv2.setText("Olá,  " + ins);


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
    }

}
