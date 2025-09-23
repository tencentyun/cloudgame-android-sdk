package com.tencent.tcr.sdk.demo.cloudstream.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.tencent.tcr.sdk.api.AndroidInstance;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.demo.cloudstream.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceScreenshotActivity extends AppCompatActivity {

    private static final String TAG = "InstanceScreenshotActivity";
    private RecyclerView recyclerView;
    private ScreenshotAdapter adapter;
    private List<ScreenshotItem> screenshotItems = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable pollRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instance_screenshots);

        String instanceIds = getIntent().getStringExtra("INSTANCE_IDS");
        Log.d(TAG, "Received instanceIds: " + instanceIds);

        List<String> instanceIdsList = Arrays.asList(instanceIds.split(","));
        for (String instanceId : instanceIdsList) {
            screenshotItems.add(new ScreenshotItem(instanceId, null, false));
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScreenshotAdapter(screenshotItems);
        recyclerView.setAdapter(adapter);

        Button startControlButton = findViewById(R.id.startControlButton);
        startControlButton.setOnClickListener(v -> {
            ArrayList<String> selectedInstanceIds = new ArrayList<>();
            ArrayList<String> pending_join_InstanceIds = new ArrayList<>();
            for (ScreenshotItem item : screenshotItems) {
                if (item.isSelected) {
                    selectedInstanceIds.add(item.instanceId);
                } else {
                    pending_join_InstanceIds.add(item.instanceId);
                }
            }
            if (!selectedInstanceIds.isEmpty()) {
                Intent intent = new Intent(InstanceScreenshotActivity.this, PlayActivity.class);
                intent.putStringArrayListExtra("instanceIds", selectedInstanceIds);
                intent.putStringArrayListExtra("pending_join_instanceIds", pending_join_InstanceIds);
                intent.putExtra("isGroupControl", true);
                startActivity(intent);
            }
        });

        startPolling();
    }

    private void startPolling() {
        pollRunnable = new Runnable() {
            @Override
            public void run() {
                updateScreenshots();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(pollRunnable);
    }

    private void updateScreenshots() {
        for (ScreenshotItem item : screenshotItems) {
            Map<String, Object> params = new HashMap<>();
            params.put("instance_id", item.instanceId);

            AndroidInstance instance = TcrSdk.getInstance().getAndroidInstance();
            if (instance == null) {
                Log.e(TAG, "instance is null");
                return;
            }
            String imageUrl = instance.getInstanceImageAddress(params);
            if (TextUtils.isEmpty(imageUrl)) {
                Log.e(TAG, "imageUrl is empty");
                return;
            }
            item.imageUrl = imageUrl;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(pollRunnable);
        Glide.get(this).clearMemory();
        recyclerView.setAdapter(null);
    }
}