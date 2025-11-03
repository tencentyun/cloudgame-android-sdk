package com.tencent.tcr.demo.cloudphone.common.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.tencent.tcr.demo.cloudphone.R;
import com.tencent.tcr.demo.cloudphone.operator.ui.FunctionActivity;
import com.tencent.tcr.sdk.api.AndroidInstance;
import com.tencent.tcr.sdk.api.TcrSdk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceListActivity extends AppCompatActivity implements InstanceListAdapter.OnItemCheckedChangeListener {

    private static final String TAG = "InstanceListActivity";
    private RecyclerView recyclerView;
    private InstanceListAdapter adapter;
    private final List<InstanceListItem> instanceListItems = new ArrayList<>();
    private final Handler handler = new Handler();
    private Runnable pollRunnable;
    private Button batchPlayBtn, batchOperatorBtn, singlePlayBtn, singleOperatorBtn;
    private final ArrayList<String> selectedInstanceIds = new ArrayList<>();
    private final ArrayList<String> unselectedInstanceIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instance_list);

        String instanceIds = getIntent().getStringExtra("INSTANCE_IDS");
        boolean isSFU = getIntent().getBooleanExtra("IS_SFU", false);
        Log.d(TAG, "Received instanceIds: " + instanceIds);

        String[] instanceIdsList = instanceIds.split(",");
        for (String instanceId : instanceIdsList) {
            instanceListItems.add(new InstanceListItem(instanceId, null, false));
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InstanceListAdapter(instanceListItems);
        adapter.setOnItemCheckedChangeListener(this);
        recyclerView.setAdapter(adapter);

        singlePlayBtn = findViewById(R.id.singlePlayBtn);
        batchPlayBtn = findViewById(R.id.batchPlayBtn);
        singleOperatorBtn = findViewById(R.id.singleOperatorBtn);
        batchOperatorBtn = findViewById(R.id.batchOperatorBtn);
        // sfu 和 operator 两者的功能，后台还不通用，所以终端 demo暂时分开
        if (isSFU) {
            singleOperatorBtn.setVisibility(View.INVISIBLE);
            batchOperatorBtn.setVisibility(View.INVISIBLE);
        } else {
            singlePlayBtn.setVisibility(View.INVISIBLE);
            batchPlayBtn.setVisibility(View.INVISIBLE);
        }
        updateButtonStates(0);// 初始状态下禁用所有按钮
        singlePlayBtn.setOnClickListener(v -> {
            updateSelectedInstanceIds();
            Intent intent = new Intent(InstanceListActivity.this, PlayActivity.class);
            intent.putStringArrayListExtra("instanceIds", selectedInstanceIds);
            intent.putExtra("isGroupControl", false);
            startActivity(intent);
        });
        singleOperatorBtn.setOnClickListener(v -> {
            updateSelectedInstanceIds();
            Intent intent = new Intent(InstanceListActivity.this, FunctionActivity.class);
            intent.putStringArrayListExtra("instanceIds", selectedInstanceIds);
            intent.putExtra("isBatchOperator", false);
            startActivity(intent);
        });
        batchPlayBtn.setOnClickListener(v -> {
            updateSelectedInstanceIds();
            Intent intent = new Intent(InstanceListActivity.this, PlayActivity.class);
            intent.putStringArrayListExtra("instanceIds", selectedInstanceIds);
            intent.putStringArrayListExtra("joinLeaveInstanceIds", unselectedInstanceIds);
            intent.putExtra("isGroupControl", true);
            startActivity(intent);
        });
        batchOperatorBtn.setOnClickListener(v -> {
            updateSelectedInstanceIds();
            Intent intent = new Intent(InstanceListActivity.this, FunctionActivity.class);
            intent.putStringArrayListExtra("instanceIds", selectedInstanceIds);
            intent.putStringArrayListExtra("joinLeaveInstanceIds", unselectedInstanceIds);
            intent.putExtra("isBatchOperator", true);
            startActivity(intent);
        });

    }

    private void updateSelectedInstanceIds() {
        selectedInstanceIds.clear();
        unselectedInstanceIds.clear();
        for (InstanceListItem item : instanceListItems) {
            if (item.isSelected) {
                selectedInstanceIds.add(item.instanceId);
            } else {
                unselectedInstanceIds.add(item.instanceId);
            }
        }
    }

    @Override
    public void onItemCheckedChanged() {
        int count = 0;
        for (InstanceListItem item : instanceListItems) {
            if (item.isSelected) {
                count++;
            }
        }
        updateButtonStates(count);
    }

    private void updateButtonStates(int selectedCount) {
        if (selectedCount == 1) {
            // 选中一个实例时，启用单实例按钮，禁用批量按钮
            singlePlayBtn.setEnabled(true);
            singleOperatorBtn.setEnabled(true);
            batchPlayBtn.setEnabled(false);
            batchOperatorBtn.setEnabled(false);
        } else if (selectedCount > 1) {
            // 选中多个实例时，启用批量按钮，禁用单实例按钮
            singlePlayBtn.setEnabled(false);
            singleOperatorBtn.setEnabled(false);
            batchPlayBtn.setEnabled(true);
            batchOperatorBtn.setEnabled(true);
        } else {
            // 没有选中实例时，禁用所有按钮
            singlePlayBtn.setEnabled(false);
            singleOperatorBtn.setEnabled(false);
            batchPlayBtn.setEnabled(false);
            batchOperatorBtn.setEnabled(false);
        }
    }

    // 新方案下没有可用的图片 url 了
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
        for (InstanceListItem item : instanceListItems) {
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