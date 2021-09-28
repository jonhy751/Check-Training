package com.example.CheckTraining1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.CheckTraining.R;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class ViewPdfActivity extends AppCompatActivity {
    TextView tv;
    Button salvar;
    private static final int CREATEPDF = 1;
    Bitmap scale, bitmap;
    String dados;
    private String Data, obs, Time, TipoTreino, DATA, descricao, local, data2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moldurapdf);
        tv = (TextView) findViewById(R.id.Dadostv);
        salvar = (Button) findViewById(R.id.bsalvarr);
        dados = getIntent().getStringExtra("dados");
        tv.setText(getIntent().getStringExtra("dados"));
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = getIntent().getStringExtra("data");
                savepdf(data);
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

                TipoTreino = getIntent().getStringExtra("Tipotreino");
                Time = getIntent().getStringExtra("Time");
                local = getIntent().getStringExtra("local");
                obs = getIntent().getStringExtra("obs");
                descricao = getIntent().getStringExtra("des");

                canvas.drawText("Nesse dia haverá..", pageInfo.getPageWidth() / 3, 100, paint);
                canvas.drawText("Tipo treino: " + TipoTreino, pageInfo.getPageWidth() / 7, 230, paint);
                canvas.drawText("Hora: " + Time, pageInfo.getPageWidth() / 7, 280, paint);
                canvas.drawText("Local: " + local, pageInfo.getPageWidth() / 7, 330, paint);
                canvas.drawText("Observação: " + obs, pageInfo.getPageWidth() / 7, 380, paint);
                canvas.drawText("Descrição do Treino: " + descricao, pageInfo.getPageWidth() / 7, 430, paint);


                pdfDocument.finishPage(page);
                gravarPdf(caminhDoArquivo, pdfDocument);

            }
        }
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.xml.mover_esquerda,R.xml.fade_out);
    }
}
