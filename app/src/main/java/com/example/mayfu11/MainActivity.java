package com.example.mayfu11;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnHome, btnMenu, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi tombol
        btnHome = findViewById(R.id.btnHome);
        btnMenu = findViewById(R.id.btnMenu);
        btnProfile = findViewById(R.id.btnProfile);

        // Memuat HomeFragment sebagai default
        if (savedInstanceState == null) {
            loadFragment(new Home());
        }

        // Menangani klik pada tombol Home
        btnHome.setOnClickListener(v -> loadFragment(new Home()));

        // Menangani klik pada tombol Menu
        btnMenu.setOnClickListener(v -> loadFragment(new Menu()));

        // Menangani klik pada tombol Profile
        btnProfile.setOnClickListener(v -> loadFragment(new Profile()));
    }

    // Fungsi untuk mengganti fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);  // Ganti fragment di container
        transaction.addToBackStack(null);  // Menambahkan ke back stack agar bisa navigasi mundur
        transaction.commit();
    }
}
