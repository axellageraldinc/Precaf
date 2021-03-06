package papb.com.presensicafe;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListPegawaiActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth, firebaseAuth2;
    private DatabaseReference databaseReference;

    private FloatingActionButton btnAddPegawai;
    private Dialog dialog;
    private EditText txtFullName, txtEmail, txtPassword;
    private Button btnDaftar;
    private Toolbar toolbar;
    private RecyclerView recyclerViewDaftarPegawai;
    private RecyclerViewDaftarPegawaiAdapter recyclerViewDaftarPegawaiAdapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pegawai);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://precaf-8099d.firebaseio.com")
                .setApiKey("AIzaSyBOr9ydz9nG9aPwBeX0z-1ewDLVZRlALpo")
                .setApplicationId("precaf-8099d").build();
        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "Precaf");
            firebaseAuth2 = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e){
            firebaseAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("Precaf"));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        recyclerViewDaftarPegawai = findViewById(R.id.recyclerViewDaftarPegawai);

        getPegawaiListFromFirebaseDatabase();

        btnAddPegawai = findViewById(R.id.btnAddPegawai);

        dialog = new Dialog(ListPegawaiActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_new_pegawai);
        txtFullName = dialog.findViewById(R.id.tambahNama);
        txtEmail = dialog.findViewById(R.id.tambahEmail);
        txtPassword = dialog.findViewById(R.id.tambahPassword);
        btnDaftar = dialog.findViewById(R.id.buttonTambah);

        btnAddPegawai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPegawaiBaru(txtEmail.getText().toString(), txtPassword.getText().toString(), txtFullName.getText().toString());
                dialog.dismiss();
            }
        });
    }

    private void registerPegawaiBaru(final String email, String password, final String fullName){
        progressDialog.show();
        final DateTime today = new DateTime();
        firebaseAuth2.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    User user = new User();
                    user.setId(task.getResult().getUser().getUid());
                    user.setFullName(fullName);
                    user.setEmail(email);
                    user.setRole("pegawai");
                    user.setJamJaga(0);
                    user.setJamJagaBulanIni(0);
                    user.setBulanJagaTerakhir(today.getMonthOfYear());
                    user.setStatus("aktif");
                    user.setTanggalRegistrasi(today.getDayOfMonth() + "-" + today.getMonthOfYear() + "-" + today.getYear());
                    databaseReference.child("users").child(task.getResult().getUser().getUid()).setValue(user);
                    txtEmail.setText("");
                    txtPassword.setText("");
                    firebaseAuth2.signOut();
                    progressDialog.dismiss();
                } else{
                    Toast.makeText(ListPegawaiActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void getPegawaiListFromFirebaseDatabase(){
        final List<User> pegawaiList = new ArrayList<>();
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pegawaiList.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()
                     ) {
                    User user = data.getValue(User.class);
                    if(user.getRole().equals("pegawai")){
                        pegawaiList.add(user);
                    }
                }
                recyclerViewDaftarPegawai.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerViewDaftarPegawaiAdapter = new RecyclerViewDaftarPegawaiAdapter(pegawaiList, getApplicationContext());
                recyclerViewDaftarPegawai.setAdapter(recyclerViewDaftarPegawaiAdapter);
                recyclerViewDaftarPegawaiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ListPegawaiActivity.this, "Error : " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
