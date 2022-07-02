package com.example.antariaclient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Permission;
import java.security.Permissions;

public class DataEditingActivity extends AppCompatActivity implements View.OnClickListener,Observer<Config> {
    public final static String MAIN_ADDRESS = "http://192.168.0.101";
    private final String[] tabHeaders = {"Фото", "Группы фото", "Таблицы", "Контакты"};
    private Config oldConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_editing);
        findViewById(R.id.apply).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        ConfigLiveData data = new ViewModelProvider(this).get(ConfigLiveData.class);
        data.getConfig().observe(this,this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onStart() {
        super.onStart();
        hideSystemBars();
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(MAIN_ADDRESS + "/config");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                Thread t = new Thread(() -> {
                        StringBuilder response = readConfigFields(urlConnection);
                        oldConfig = createConfig(response);
                        ConfigLiveData data = new ViewModelProvider(this).get(ConfigLiveData.class);
                        checkAndPostConfigLiveData(data);
                        runOnUiThread(this::configurePager);
                });
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private StringBuilder readConfigFields(HttpURLConnection connection){
        StringBuilder response = new StringBuilder();
        try {
            String line;
            connection.setRequestMethod("GET");
            InputStream input = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return response;
    }

    private Config createConfig(StringBuilder response){
        Config cfg = null;
        try {
            cfg = new ObjectMapper().readValue(response.toString(), Config.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return cfg;
    }

    private void checkAndPostConfigLiveData(ConfigLiveData data){
        if (data.getConfigValue() == null) {
            data.getConfig().postValue(oldConfig);
        } else {//Повторный запуск(После добавления фото в EditPhotosFragment)
            Config mergedCfg = oldConfig;

            //data.getConfig().postValue(mergedCfg);
        }
    }

    private void configurePager() {
        ViewPager2 viewPager = findViewById(R.id.viewPager2);
        TabLayout tabLayout = findViewById(R.id.tab);
        viewPager.setAdapter(new ViewPagerAdapter(this, oldConfig));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(tabHeaders[position])).attach();
        viewPager.setUserInputEnabled(false);
    }

    private void hideSystemBars() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onClick(View view) {
        ConfigLiveData cfgData = new ViewModelProvider(this).get(ConfigLiveData.class);
        if (view.getId() == R.id.apply) {
            oldConfig = cfgData.getConfigValue();
            try {
                sendNewConfigOnServer(cfgData.getConfigValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.cancel) {
            cfgData.setConfig(oldConfig);
        }
    }


    private void sendNewConfigOnServer(Config cfg) throws IOException {
        URL url = new URL(MAIN_ADDRESS);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Thread t = new Thread(() -> {
            try {
                urlConnection.setRequestMethod("POST");
                OutputStream out = urlConnection.getOutputStream();
                BufferedWriter reader = new BufferedWriter(new OutputStreamWriter(out));
                reader.append(cfg.toString());
                reader.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        });
        t.start();
    }

    @Override
    public void onChanged(Config config) {

    }

}