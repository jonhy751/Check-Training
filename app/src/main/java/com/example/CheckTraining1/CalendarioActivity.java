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
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CalendarioActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private CalendarView calendario;
    private TextView tv;
    private final int ID_Home = 1;
    private final int ID_MESSAGE = 2;
    private final int ID_NOTIFICATION = 3;
    private static final int STORAGE_CODE = 1000;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private long resul;
    private ListView li;
    private String Data, obs, Time, TipoTreino, DATA, descricao, local, data2, Data1, ins;

    private Bitmap bm, sbm;
    private SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private Date data1 = new Date();
    private String dataFormatada = formataData.format(data1);
    private static final int CREATEPDF = 1;
    Bitmap scale, bitmap;
    private Button deletar, next, retur, voltar;
    private TextView mess;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String emaill = user.getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        marcadores();
        next = (Button) findViewById(R.id.buttonnext);
        retur = (Button) findViewById(R.id.ButtonReturn);
        voltar = (Button) findViewById(R.id.bvoltar);
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.calendarView);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONTH);

        compactCalendarView.setUseThreeLetterAbbreviation(true);
        final SimpleDateFormat getMonth = new SimpleDateFormat("MMMM - YYYY");
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                startActivity(Intent);
                finish();
            }
        });
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
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("Treinos");
        scoresRef.keepSynced(true);


        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_Home, R.drawable.ic_home_black_24dp));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE, R.drawable.ic_perm_contact_calendar_black_24dp));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.ic_create_black_24dp));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
            }
        });
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch (item.getId()) {
                    case ID_Home: {
                        name = "Home";
                        Intent Intent = new Intent(getApplicationContext(), PrincipalActivity.class);
                        startActivity(Intent);
                    }

                    break;
                    case ID_MESSAGE: {
                        name = "Message";

                    }
                    break;
                    case ID_NOTIFICATION: {
                        name = "Notification";
                        Intent Intent = new Intent(getApplicationContext(), CadastrarTreinosActivity.class);
                        startActivity(Intent);

                    }
                    break;
                    default:
                        name = "";
                }

            }
        });

        bottomNavigation.show(ID_MESSAGE, true);


        database = FirebaseDatabase.getInstance();
        myRef = database.getInstance().getReference("Treinos");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moldurapdf);
        mAuth = FirebaseAuth.getInstance();
        mess = (TextView) findViewById(R.id.mes);


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


        tv = (TextView) findViewById(R.id.tv);

        deletar = (Button) findViewById(R.id.bdelet);
        tv.setText("Selecione uma Data");

        deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText delet = new EditText(v.getContext());
                AlertDialog.Builder del = new AlertDialog.Builder(v.getContext());
                del.setTitle("Excluir data de treino");
                del.setMessage("Digite o dia que deseja apagar o treino");
                del.setView(delet);
                del.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("Treinos").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                                        Data = (String) s.child("data").getValue(String.class);

                                        if (delet.getText().toString().equals(Data)) {
                                            myRef.child(s.getKey()).removeValue();

                                            Toast.makeText(CalendarioActivity.this, "Treino excluído!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(CalendarioActivity.this, "Erro! Verifique a data digitada!", Toast.LENGTH_SHORT).show();
                                        }


                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CalendarioActivity.this, "Deu algo de errado :(", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                del.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                del.show();
            }
        });


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);


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

                            Time = s.child("time").getValue().toString();
                            TipoTreino = s.child("tipoTreino").getValue().toString();
                            descricao = s.child("descriçao").getValue().toString();
                            local = s.child("local").getValue().toString();
                            obs = s.child("obs").getValue().toString();
                            tv.setText("O treino deste dia é:");

                            String dados = ("Tipo do treino: " + TipoTreino + "\nHorário do Treino: " + Time + "h\nDescrição do treino: " + descricao + "\nLocal:" + local + "\nObservações: " + obs);
                            Intent Intent = new Intent(getApplicationContext(), ViewPdfActivity.class);
                            Intent.putExtra("dados", dados);
                            Intent.putExtra("data", Data1);
                            Intent.putExtra("Tipotreino", TipoTreino);
                            Intent.putExtra("Time", Time);
                            Intent.putExtra("des", descricao);
                            Intent.putExtra("local", local);
                            Intent.putExtra("obs", obs);
                            Intent.putExtra("ins", ins);

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
        System.out.println(ano + "oi");

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
                            final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.calendarView);
                            compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
                            compactCalendarView.setUseThreeLetterAbbreviation(true);


                            String frase = Data.toString();
                            frase = frase.replaceAll("/", "");
                            frase = frase.replaceAll(" ", "");


                            String num = frase.substring(0, 2);
                            String m = frase.substring(2, 4);
                            String ano = frase.substring(4);
                            System.out.println(ano + "alo");

                            resul = DataMilis(m, num, ano);

                            Event ev1 = new Event(Color.GREEN, resul, "Some extra data that I want to store.");
                            compactCalendarView.addEvent(ev1);
                            resul = 0;
                        } else {
                            System.out.println("DEU ErraDO");
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
}
