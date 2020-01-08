package com.androidtutz.bulana.devices.view;

import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidtutz.bulana.devices.R;
import com.androidtutz.bulana.devices.adapter.DeviceAdapter;
import com.androidtutz.bulana.devices.model.Device;
import com.androidtutz.bulana.devices.model.DeviceDBResponse;
import com.androidtutz.bulana.devices.service.DevicesDataService;
import com.androidtutz.bulana.devices.service.RetrofitInstance;
import com.tomer.fadingtextview.FadingTextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Device> devices;
    private RecyclerView recyclerView;
    private DeviceAdapter deviceAdapter;
    private FadingTextView fadingTextView;
    private TextView tvAllDeviceHeader;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getSupportActionBar().hide();
        getSupportActionBar().setTitle(" ");

        getDevices();

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.blue_dark));

        tvAllDeviceHeader = findViewById(R.id.tvAllDeviceHeader);
        fadingTextView = findViewById(R.id.fading_text_view);
        fadeText();
    }

    private void fadeText() {
        String[] questionString = {"Looking For A Zapper Device?", "Look No Further."};
        fadingTextView.setTexts(questionString);
        fadingTextView.setTimeout(2000, TimeUnit.MILLISECONDS);
        fadingTextView.forceRefresh();
    }

    private void showDevices() {
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);
        ViewGroup hiddenPanel = (ViewGroup)findViewById(R.id.llDevices);
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);
    }

    public void getDevices() {
        DevicesDataService devicesDataService = RetrofitInstance.getService();
        Call<DeviceDBResponse> call = devicesDataService.getDevices();
        call.enqueue(new Callback<DeviceDBResponse>() {
            @Override
            public void onResponse(Call<DeviceDBResponse> call, Response<DeviceDBResponse> response) {
                DeviceDBResponse deviceDBResponse = response.body();

                if (deviceDBResponse != null && deviceDBResponse.getDevices() != null) {
                    devices = (ArrayList<Device>) deviceDBResponse.getDevices();
                    showOnRecyclerView();
                    showDevices();
                }
            }
            @Override
            public void onFailure(Call<DeviceDBResponse> call, Throwable t) {
            }
        });
    }

    private void showOnRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.rvDevices);
        deviceAdapter = new DeviceAdapter(this, devices);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(deviceAdapter);
        deviceAdapter.notifyDataSetChanged();
        tvAllDeviceHeader.setText("All Devices");
    }
}