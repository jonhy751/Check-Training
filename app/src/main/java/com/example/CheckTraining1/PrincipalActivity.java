package com.example.CheckTraining1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.CheckTraining.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;

public class PrincipalActivity extends AppCompatActivity {
    private Button c, t, logout;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private final int ID_Home = 1;
    private final int ID_MESSAGE = 2;
    private final int ID_NOTIFICATION = 3;


    private Boolean firebaseOffline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        mAuth = FirebaseAuth.getInstance();

        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutt();
            }
        });


        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_Home, R.drawable.ic_home_black_24dp));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_MESSAGE, R.drawable.ic_perm_contact_calendar_black_24dp));
        bottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.ic_create_black_24dp));
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                Toast.makeText(PrincipalActivity.this, "Clicked item : " + item.getId(), Toast.LENGTH_SHORT).show();

            }
        });
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch (item.getId()) {
                    case ID_Home: {
                        name = "Home";

                    }

                    break;
                    case ID_MESSAGE: {
                        name = "Message";
                        Intent Intent = new Intent(getApplicationContext(), CalendarioActivity.class);
                        startActivity(Intent);
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

        bottomNavigation.show(ID_Home, true);
        updateToken();
    }


    private void updateToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {

        }
    }

    private void logoutt() {
        mAuth.signOut();
        finish();
        Intent Intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(Intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.xml.mover_esquerda, R.xml.fade_out);
    }

}
