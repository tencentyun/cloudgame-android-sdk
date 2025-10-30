package com.tencent.tcr.sdk.demo.cloudphone.ui;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tencent.tcr.sdk.api.AndroidInstance;
import com.tencent.tcr.sdk.api.AndroidInstance.UploadFileItem;
import com.tencent.tcr.sdk.api.AsyncCallback;
import com.tencent.tcr.sdk.api.TcrLogger;
import com.tencent.tcr.sdk.api.TcrSdk;
import com.tencent.tcr.sdk.api.TcrSdk.SdkType;
import com.tencent.tcr.sdk.api.data.BatchTaskResponse;
import com.tencent.tcr.sdk.demo.cloudphone.DemoApp;
import com.tencent.tcr.sdk.demo.cloudphone.R;
import com.tencent.tcr.sdk.demo.cloudphone.network.CreateAndroidInstancesAccessTokenRequest;
import com.tencent.tcr.sdk.demo.cloudphone.network.CreateAndroidInstancesAccessTokenResponse;
import com.tencent.tcr.sdk.demo.cloudphone.network.DescribeAndroidInstancesRequest;
import com.tencent.tcr.sdk.demo.cloudphone.network.DescribeAndroidInstancesResponse;
import com.tencent.tcr.sdk.demo.cloudphone.network.DescribeAndroidInstancesResponse.AndroidInstanceData;
import com.tencent.tcr.sdk.demo.cloudphone.network.ExpServerResponse;
import com.tencent.tcr.sdk.demo.cloudphone.network.UploadResponse;
import com.tencent.tcr.sdk.demo.cloudphone.util.GsonUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FunctionActivity extends AppCompatActivity {

    private static final String TAG = "FunctionActivity";
    private static final int REQUEST_CODE_FILE_SELECT = 1001;
    private static final int REQUEST_CODE_MEDIA_SELECT = 1002;
    private final ArrayList<String> mInstanceIds = new ArrayList<>();//这个页面测试的实例ID列表
    private TextView mResultTextView;
    private final AsyncCallback<BatchTaskResponse> mBatchCallback = new AsyncCallback<BatchTaskResponse>() {
        @Override
        public void onSuccess(BatchTaskResponse batchTaskResponse) {
            StringBuilder resultBuilder = new StringBuilder("批量" + batchTaskResponse.Message + "结果\n");
            // 成功的实例结果
            if (!batchTaskResponse.SuccResult.isEmpty()) {
                resultBuilder.append("成功:\n");
                for (Map.Entry<String, JsonElement> entry : batchTaskResponse.SuccResult.entrySet()) {
                    String instanceId = entry.getKey();
                    JsonElement result = entry.getValue();
                    resultBuilder.append("  实例").append(instanceId).append(":").append(result.toString())
                            .append("\n");
                }
            }
            // 失败的实例结果
            if (!batchTaskResponse.FailResult.isEmpty()) {
                resultBuilder.append("失败:\n");
                for (Map.Entry<String, JsonElement> entry : batchTaskResponse.FailResult.entrySet()) {
                    String instanceId = entry.getKey();
                    JsonElement result = entry.getValue();
                    resultBuilder.append("  实例").append(instanceId).append(":").append(result.toString())
                            .append("\n");
                }
            }
            mResultTextView.setText(resultBuilder.toString());
        }

        @Override
        public void onFailure(int code, String errorMsg) {
            mResultTextView.setText("操作失败: " + code + ", " + errorMsg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        initViews();

        requestAccessToken();
    }

    // 初始化 ui
    private void initViews() {
        mResultTextView = findViewById(R.id.tvResult);

        // 初始化所有按钮的点击事件
        findViewById(R.id.btnModifyResolution).setOnClickListener(v -> modifyResolution());
        findViewById(R.id.btnModifyGps).setOnClickListener(v -> modifyGps());
        findViewById(R.id.btnPasteText).setOnClickListener(v -> pasteText());
        findViewById(R.id.btnModifyClipboard).setOnClickListener(v -> modifyClipboard());
        findViewById(R.id.btnModifySensor).setOnClickListener(v -> setSensor());
        findViewById(R.id.btnShake).setOnClickListener(v -> shake());
        findViewById(R.id.btnBlow).setOnClickListener(v -> blow());
        findViewById(R.id.btnSendMessage).setOnClickListener(v -> sendMessage());
        findViewById(R.id.btnQueryInstanceAttr).setOnClickListener(v -> queryInstanceAttr());
        findViewById(R.id.btnModifyInstanceAttr).setOnClickListener(v -> modifyInstanceProperties());
        findViewById(R.id.btnQueryApps).setOnClickListener(v -> queryApps());
        findViewById(R.id.btnModifyForegroundKeep).setOnClickListener(v -> modifyForegroundKeep());
        findViewById(R.id.btnQueryForegroundKeep).setOnClickListener(v -> queryForegroundKeep());
        findViewById(R.id.btnUninstallApp).setOnClickListener(v -> uninstallApp());
        findViewById(R.id.btnLaunchApp).setOnClickListener(v -> launchApp());
        findViewById(R.id.btnStopApp).setOnClickListener(v -> stopApp());
        findViewById(R.id.btnClearAppData).setOnClickListener(v -> clearAppData());
        findViewById(R.id.btnEnableApp).setOnClickListener(v -> enableApp());
        findViewById(R.id.btnDisableApp).setOnClickListener(v -> disableApp());
        findViewById(R.id.btnPlayMedia).setOnClickListener(v -> playMedia());
        findViewById(R.id.btnStopMedia).setOnClickListener(v -> stopMedia());
        findViewById(R.id.btnQueryMediaStatus).setOnClickListener(v -> queryMediaStatus());
        findViewById(R.id.btnShowImage).setOnClickListener(v -> showImage());
        findViewById(R.id.btnAddBackgroundKeep).setOnClickListener(v -> addBackgroundKeep());
        findViewById(R.id.btnRemoveBackgroundKeep).setOnClickListener(v -> removeBackgroundKeep());
        findViewById(R.id.btnSetBackgroundKeep).setOnClickListener(v -> setBackgroundKeep());
        findViewById(R.id.btnQueryBackgroundKeep).setOnClickListener(v -> queryBackgroundKeep());
        findViewById(R.id.btnClearBackgroundKeep).setOnClickListener(v -> clearBackgroundKeep());
        findViewById(R.id.btnToggleMute).setOnClickListener(v -> toggleMute());
        findViewById(R.id.btnSearchMedia).setOnClickListener(v -> searchMedia());
        findViewById(R.id.btnRebootInstance).setOnClickListener(v -> rebootInstance());
        findViewById(R.id.btnQueryAllApps).setOnClickListener(v -> queryAllApps());
        findViewById(R.id.btnMoveToBackground).setOnClickListener(v -> moveToBackground());
        findViewById(R.id.btnAddInstallBlacklist).setOnClickListener(v -> addInstallBlacklist());
        findViewById(R.id.btnRemoveInstallBlacklist).setOnClickListener(v -> removeInstallBlacklist());
        findViewById(R.id.btnSetInstallBlacklist).setOnClickListener(v -> setInstallBlacklist());
        findViewById(R.id.btnQueryInstallBlacklist).setOnClickListener(v -> queryInstallBlacklist());
        findViewById(R.id.btnClearInstallBlacklist).setOnClickListener(v -> clearInstallBlacklist());
        findViewById(R.id.btnScreenshot).setOnClickListener(v -> getInstanceImage());
        findViewById(R.id.btnUploadFile).setOnClickListener(v -> showUploadFile());
        findViewById(R.id.btnUploadMedia).setOnClickListener(v -> showUploadMedia());
        findViewById(R.id.btnDownloadFile).setOnClickListener(v -> downloadFile());
        findViewById(R.id.btnEnterSingleCloudPhone).setOnClickListener(v -> enterSingleCloudPhone());
        findViewById(R.id.btnEnterGroupCloudPhone).setOnClickListener(v -> enterGroupCloudPhone());
        findViewById(R.id.btnGetFileDownloadUrl).setOnClickListener(v -> getFileDownloadUrl());
        findViewById(R.id.btnGetLogDownloadUrl).setOnClickListener(v -> getLogDownloadUrl());
        findViewById(R.id.btnGetNavigationBarStatus).setOnClickListener(v -> getNavigationBarStatus());
        findViewById(R.id.btnGetMediaVolume).setOnClickListener(v -> getMediaVolume());
    }

    // 请求云手机实例的访问信息AceessInfo和鉴权Token
    private void requestAccessToken() {
        AsyncCallback<ExpServerResponse<CreateAndroidInstancesAccessTokenResponse>> createAndroidInstancesAccessTokenCallback = new AsyncCallback<ExpServerResponse<CreateAndroidInstancesAccessTokenResponse>>() {
            @Override
            public void onSuccess(ExpServerResponse<CreateAndroidInstancesAccessTokenResponse> expServerResponse) {
                if (expServerResponse.Error != null) {
                    Log.e(TAG, "CreateAndroidInstancesAccessToken expServerResponse.Error: " + expServerResponse.Error);
                    return;
                }
                if (expServerResponse.Response == null) {
                    Log.e(TAG, "CreateAndroidInstancesAccessToken Response: null");
                    return;
                }

                // 初始化 TcrSdk
                initTcrSdk(expServerResponse);
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                Log.e(TAG, "CreateAndroidInstancesAccessToken failed: " + code + ", " + errorMsg);
                Toast.makeText(FunctionActivity.this, "获取实例访问信息失败: " + code + ", " + errorMsg,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        };
        AsyncCallback<ExpServerResponse<DescribeAndroidInstancesResponse>> describeAndroidInstancesCallback = new AsyncCallback<>() {
            @Override
            public void onSuccess(ExpServerResponse<DescribeAndroidInstancesResponse> expServerResponse) {
                if (expServerResponse.Error != null) {
                    Log.e(TAG, "DescribeAndroidInstances expServerResponse.Error: " + expServerResponse.Error);
                    return;
                }

                if (expServerResponse.Response == null || expServerResponse.Response.AndroidInstances == null) {
                    Log.e(TAG, "DescribeAndroidInstances Response or AndroidInstances is null");
                    return;
                }

                // 业务处理
                mInstanceIds.clear();
                for (AndroidInstanceData instance : expServerResponse.Response.AndroidInstances) {
                    //Log.v(TAG, instance.toString());
                    mInstanceIds.add(instance.AndroidInstanceId);
                }
                new CreateAndroidInstancesAccessTokenRequest(mInstanceIds,
                        createAndroidInstancesAccessTokenCallback).execute(DemoApp.getQueue());
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                Log.e(TAG, "DescribeAndroidInstances failed: " + code + ", " + errorMsg);
                Toast.makeText(FunctionActivity.this, "获取实例列表失败: " + code + ", " + errorMsg, Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        };
        new DescribeAndroidInstancesRequest(describeAndroidInstancesCallback).execute(
                DemoApp.getQueue());
    }

    // 初始化TcrSdk
    private void initTcrSdk(ExpServerResponse<CreateAndroidInstancesAccessTokenResponse> expServerResponse) {
        TcrSdk.TcrConfig config = new TcrSdk.TcrConfig();
        config.type = SdkType.CloudPhone;
        config.ctx = this;
        // 用于接收 SDK 日志的对象，App 需要将收到的 SDK 日志存储到文件和上报，以便分析定位 SDK 运行时的问题。
        // 如果App没有显示设置 logger，SDK 会将日志打印到系统 logcat 以及写入日志文件（/storage/emulated/0/Android/data/app package name/files/tcrlogs 目录下）。
        // 注意，当你反馈 SDK 问题时，你需要提供相应的 SDK 日志。
        // config.logger = ...
        config.callback = new AsyncCallback<>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    Log.i(TAG, "init TcrSdk success");
                    // 请求成功后隐藏全屏加载指示器，显示内容区域
                    findViewById(R.id.progress_overlay).setVisibility(View.GONE);
                    findViewById(R.id.content_scroll_view).setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                runOnUiThread(() -> {
                    String errorMsg = "init TcrSdk failed:" + code + " msg:" + msg;
                    Toast.makeText(FunctionActivity.this, "初始化TcrSdk失败: " + code + ", " + errorMsg,
                            Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        };
        config.accessInfo = expServerResponse.Response.AccessInfo;
        config.token = expServerResponse.Response.Token;
        TcrSdk.getInstance().init(config);
    }

    private AndroidInstance getAndroidInstance() {
        return TcrSdk.getInstance().getAndroidInstance();
    }

    /// ///////////////////// 单机操作示例 /// /////////////////////

    private void getInstanceImage() {
        Map<String, Object> params = new HashMap<>();
        params.put("instance_id", mInstanceIds.get(0));
        params.put("screenshot_quality", 80);
        params.put("screenshot_width", 1080);
        params.put("screenshot_height", 1920);

        getAndroidInstance().getInstanceImage(params, new AsyncCallback<Bitmap>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                // 显示截图结果
                ImageView imageView = new ImageView(FunctionActivity.this);
                imageView.setImageBitmap(bitmap);
                new AlertDialog.Builder(FunctionActivity.this).setTitle("截图结果").setView(imageView).show();
                mResultTextView.setText("截图成功");
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mResultTextView.setText("截图失败: " + code + ", " + errorMsg);
            }
        });
    }

    private void downloadFile() {
        String localPath = getExternalCacheDir() + "/testDownloadFile";
        String cloudPath = "/sdcard/Movies/testDownloadFile";
        Map<String, Object> params = new HashMap<>();
        params.put("instance_id", mInstanceIds.get(0));
        params.put("path", cloudPath);
        AsyncCallback<String> callback = new AsyncCallback<String>() {
            @Override
            public void onSuccess(String response) {
                mResultTextView.setText("文件下载成功: ");
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mResultTextView.setText("文件下载失败: " + code + ", " + errorMsg);
            }
        };
        getAndroidInstance().downloadFile(params, new File(localPath), callback);
    }


    private void getFileDownloadUrl() {
        String address = getAndroidInstance().getInstanceDownloadAddress(mInstanceIds.get(0), "path");
        mResultTextView.setText("文件下载地址: " + address);
    }

    private void getLogDownloadUrl() {
        String address = getAndroidInstance().getInstanceDownloadLogcatAddress(mInstanceIds.get(0), 3);
        mResultTextView.setText("日志下载地址: " + address);
    }

    private void showUploadFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // 支持所有文件类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 启用多选
        startActivityForResult(intent, REQUEST_CODE_FILE_SELECT);
    }

    private void showUploadMedia() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // 只选择图片类型
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 启用多选
        startActivityForResult(intent, REQUEST_CODE_MEDIA_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_CODE_FILE_SELECT || requestCode == REQUEST_CODE_MEDIA_SELECT)
                && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            ArrayList<Uri> fileList = new ArrayList<>();
            if (clipData != null) {
                // 多选文件
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    fileList.add(uri);
                }
            } else {
                // 单选文件
                Uri uri = data.getData();
                fileList.add(uri);
            }
            boolean isMedia = requestCode == REQUEST_CODE_MEDIA_SELECT;
            upload(fileList, isMedia);
        }
    }

    private void upload(ArrayList<Uri> fileList, boolean isMedia) {
        UploadFileItem[] files = new UploadFileItem[fileList.size()];
        for (int i = 0; i < fileList.size(); i++) {
            files[i] = new UploadFileItem();
            files[i].fileBytes = readFilesFromUris(fileList.get(i));
            files[i].fileName = getFileNameFromUri(fileList.get(i));
            files[i].filePath = "/sdcard/Movies/";
        }

        AsyncCallback<String> callback = new AsyncCallback<String>() {
            @Override
            public void onSuccess(String response) {
                // 示例数据 {Code=0, Message='null', FileStatus=[FileStatus{FileName='testDownloadFile', CloudPath='/sdcard/Download/testDownloadFile'}]}
                UploadResponse uploadResponse = GsonUtils.fromJson(response, UploadResponse.class);
                Log.d(TAG, "get response: " + uploadResponse);
                if (uploadResponse == null) {
                    mResultTextView.setText("文件上传异常: uploadResponse 解析失败");
                    return;
                }
                if (uploadResponse.Code == 0) {
                    StringBuilder sb = new StringBuilder("文件上传成功:\n");
                    if (uploadResponse.FileStatus != null) {
                        for (UploadResponse.FileStatus status : uploadResponse.FileStatus) {
                            sb.append("文件名: ").append(status.FileName).append("\n云端路径: ")
                                    .append(status.CloudPath).append("\n");
                        }
                    }
                    mResultTextView.setText(sb.toString());
                } else {
                    mResultTextView.setText("文件上传失败: " + uploadResponse.Message);
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                mResultTextView.setText("文件上传失败: " + code + ", " + errorMsg);
            }
        };
        if (isMedia) {
            getAndroidInstance().uploadMedia(mInstanceIds.get(0), files, callback);
        } else {
            getAndroidInstance().uploadFile(mInstanceIds.get(0), files, callback);
        }
    }

    private byte[] readFilesFromUris(Uri uri) {
        try {
            // 以二进制方式读取文件内容
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            byte[] fileBytes = byteArrayOutputStream.toByteArray();
            inputStream.close();
            byteArrayOutputStream.close();
            return fileBytes;
        } catch (Exception e) {
            Log.e(TAG, "读取文件失败: " + uri, e);
            return null;
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri == null) {
            return null;
        }

        // 处理content类型的URI
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME},
                    null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to get file name from content URI", e);
            }
        }

        // 如果通过MediaStore未获取到文件名，尝试从URI路径解析
        if (fileName == null) {
            String path = uri.getPath();
            if (path != null) {
                int lastSlash = path.lastIndexOf('/');
                if (lastSlash != -1) {
                    fileName = path.substring(lastSlash + 1);
                }
            }
        }

        return fileName;
    }


    private void enterSingleCloudPhone() {
        // 启动云手机界面
        mResultTextView.setText("进入云手机(单个): " + mInstanceIds.get(0));
        Intent intent = new Intent(FunctionActivity.this, PlayActivity.class);
        intent.putStringArrayListExtra("instanceIds", mInstanceIds);
        intent.putExtra("isGroupControl", false);
        startActivity(intent);
    }

    private void enterGroupCloudPhone() {
        // 检查mInstanceIds大小是否足够
        if (mInstanceIds.size() < 4) {
            mResultTextView.setText("请增加实例数，再进入群控云手机。" + mInstanceIds.size());
            return;
        }

        int size = mInstanceIds.size();
        ArrayList<String> instanceIds = new ArrayList<>();
        instanceIds.add(mInstanceIds.get(size - 1));
        instanceIds.add(mInstanceIds.get(size - 2));
        ArrayList<String> joinLeaveInstanceIds = new ArrayList<>();
        joinLeaveInstanceIds.add(mInstanceIds.get(size - 3));
        joinLeaveInstanceIds.add(mInstanceIds.get(size - 4));
        String instanceIdsText = String.join(", ", instanceIds);

        // 启动云手机界面
        mResultTextView.setText("进入云手机(群控): " + instanceIdsText);
        Intent intent = new Intent(FunctionActivity.this, PlayActivity.class);
        intent.putStringArrayListExtra("instanceIds", instanceIds);
        intent.putStringArrayListExtra("joinLeaveInstanceIds", joinLeaveInstanceIds);
        intent.putExtra("isGroupControl", true);
        startActivity(intent);
    }


    /// ///////////////////// 批量操作示例 /// /////////////////////
    private void modifyResolution() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("Width", 1080);
            param.addProperty("Height", 1920);
            param.addProperty("DPI", 420);
            params.put(instanceId, param);
        }
        getAndroidInstance().setResolution(params, mBatchCallback);
    }

    private void modifyGps() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("Longitude", 121.4737);
            param.addProperty("Latitude", 31.2304);
            params.put(instanceId, param);
        }
        getAndroidInstance().setLocation(params, mBatchCallback);
    }

    private void pasteText() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("Text", "粘贴的文本内容");
            params.put(instanceId, param);
        }
        getAndroidInstance().paste(params, mBatchCallback);
    }

    private void modifyClipboard() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("Text", "剪贴板内容");
            params.put(instanceId, param);
        }
        getAndroidInstance().sendClipboard(params, mBatchCallback);
    }

    private void setSensor() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("Type", "accelerometer");
            param.addProperty("Accuracy", 3);

            JsonArray values = new JsonArray();
            values.add(0.5);
            values.add(-0.3);
            values.add(0.8);
            param.add("Values", values);

            params.put(instanceId, param);
        }
        getAndroidInstance().setSensor(params, mBatchCallback);
    }

    private void shake() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().shake(params, mBatchCallback);
    }

    private void blow() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().blow(params, mBatchCallback);
    }

    private void sendMessage() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("PackageName", "com.example.app");
            param.addProperty("Msg", "消息内容");
            params.put(instanceId, param);
        }
        getAndroidInstance().sendTransMessage(params, mBatchCallback);
    }

    private void queryInstanceAttr() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().describeInstanceProperties(params, mBatchCallback);
    }

    private void modifyInstanceProperties() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("RequestID", UUID.randomUUID().toString());

            JsonObject deviceInfo = new JsonObject();
            deviceInfo.addProperty("Brand", "brand");
            deviceInfo.addProperty("Model", "model");
            param.add("DeviceInfo", deviceInfo);

            JsonObject proxyInfo = new JsonObject();
            proxyInfo.addProperty("Enabled", true);
            proxyInfo.addProperty("Protocol", "socks5");
            proxyInfo.addProperty("Host", "proxy.example.com");
            proxyInfo.addProperty("Port", 1080);
            proxyInfo.addProperty("User", "user");
            proxyInfo.addProperty("Password", "password");
            param.add("ProxyInfo", proxyInfo);

            JsonObject gpsInfo = new JsonObject();
            gpsInfo.addProperty("Longitude", 121.4737);
            gpsInfo.addProperty("Latitude", 31.2304);
            param.add("GPSInfo", gpsInfo);

            JsonObject simInfo = new JsonObject();
            simInfo.addProperty("State", 1);
            simInfo.addProperty("PhoneNumber", "1234567890");
            simInfo.addProperty("IMSI", "imsi");
            simInfo.addProperty("ICCID", "iccid");
            param.add("SIMInfo", simInfo);

            JsonObject localeInfo = new JsonObject();
            localeInfo.addProperty("Timezone", "Asia/Shanghai");
            param.add("LocaleInfo", localeInfo);

            JsonObject languageInfo = new JsonObject();
            languageInfo.addProperty("Language", "zh");
            languageInfo.addProperty("Country", "CN");
            param.add("LanguageInfo", languageInfo);

            JsonArray extraProperties = new JsonArray();
            JsonObject extraProp = new JsonObject();
            extraProp.addProperty("Key", "key");
            extraProp.addProperty("Value", "value");
            extraProperties.add(extraProp);
            param.add("ExtraProperties", extraProperties);

            params.put(instanceId, param);
        }
        getAndroidInstance().modifyInstanceProperties(params, mBatchCallback);
    }

    private void queryApps() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().listUserApps(params, mBatchCallback);
    }

    private void modifyForegroundKeep() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("PackageName", "com.example.app");
            param.addProperty("Enable", true);
            param.addProperty("RestartInterValSeconds", 5);
            params.put(instanceId, param);
        }
        getAndroidInstance().modifyKeepFrontAppStatus(params, mBatchCallback);
    }

    private void queryForegroundKeep() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().describeKeepFrontAppStatus(params, mBatchCallback);
    }

    private void uninstallApp() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("PackageName", "com.example.app");
            params.put(instanceId, param);
        }
        getAndroidInstance().unInstallByPackageName(params, mBatchCallback);
    }

    private void launchApp() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("PackageName", "com.example.app");
            param.addProperty("ActivityName", "com.example.app.MainActivity");
            params.put(instanceId, param);
        }
        getAndroidInstance().startApp(params, mBatchCallback);
    }

    private void stopApp() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("PackageName", "com.example.app");
            params.put(instanceId, param);
        }
        getAndroidInstance().stopApp(params, mBatchCallback);
    }

    private void clearAppData() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("PackageName", "com.example.app");
            params.put(instanceId, param);
        }
        getAndroidInstance().clearAppData(params, mBatchCallback);
    }

    private void enableApp() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("PackageName", "com.example.app");
            params.put(instanceId, param);
        }
        getAndroidInstance().enableApp(params, mBatchCallback);
    }

    private void disableApp() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("PackageName", "com.example.app");
            params.put(instanceId, param);
        }
        getAndroidInstance().disableApp(params, mBatchCallback);
    }

    private void playMedia() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("FilePath", "/sdcard/media.mp4");
            param.addProperty("Loops", 3);
            params.put(instanceId, param);
        }
        getAndroidInstance().startCameraMediaPlay(params, mBatchCallback);
    }

    private void stopMedia() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().stopCameraMediaPlay(params, mBatchCallback);
    }

    private void queryMediaStatus() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().describeCameraMediaPlayStatus(params, mBatchCallback);
    }

    private void showImage() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("FilePath", "/sdcard/image.png");
            params.put(instanceId, param);
        }
        getAndroidInstance().displayCameraImage(params, mBatchCallback);
    }

    private void addBackgroundKeep() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            JsonArray appList = new JsonArray();
            appList.add("com.example.app");
            param.add("AppList", appList);
            params.put(instanceId, param);
        }
        getAndroidInstance().addKeepAliveList(params, mBatchCallback);
    }

    private void removeBackgroundKeep() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            JsonArray appList = new JsonArray();
            appList.add("com.example.app");
            param.add("AppList", appList);
            params.put(instanceId, param);
        }
        getAndroidInstance().removeKeepAliveList(params, mBatchCallback);
    }

    private void setBackgroundKeep() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            JsonArray appList = new JsonArray();
            appList.add("com.example.app");
            param.add("AppList", appList);
            params.put(instanceId, param);
        }
        getAndroidInstance().setKeepAliveList(params, mBatchCallback);
    }

    private void queryBackgroundKeep() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().describeKeepAliveList(params, mBatchCallback);
    }

    private void clearBackgroundKeep() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().clearKeepAliveList(params, mBatchCallback);
    }

    private void toggleMute() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("Mute", true);
            params.put(instanceId, param);
        }
        getAndroidInstance().mute(params, mBatchCallback);
    }

    private void searchMedia() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            param.addProperty("Keyword", "搜索关键词");
            params.put(instanceId, param);
        }
        getAndroidInstance().mediaSearch(params, mBatchCallback);
    }

    private void rebootInstance() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().reboot(params, mBatchCallback);
    }

    private void queryAllApps() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().listAllApps(params, mBatchCallback);
    }

    private void moveToBackground() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().moveAppBackground(params, mBatchCallback);
    }

    private void addInstallBlacklist() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            JsonArray appList = new JsonArray();
            appList.add("com.example.app");
            param.add("AppList", appList);
            params.put(instanceId, param);
        }
        getAndroidInstance().addAppInstallBlackList(params, mBatchCallback);
    }

    private void removeInstallBlacklist() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            JsonArray appList = new JsonArray();
            appList.add("com.example.app");
            param.add("AppList", appList);
            params.put(instanceId, param);
        }
        getAndroidInstance().removeAppInstallBlackList(params, mBatchCallback);
    }

    private void setInstallBlacklist() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            JsonObject param = new JsonObject();
            JsonArray appList = new JsonArray();
            appList.add("com.example.app");
            param.add("AppList", appList);
            params.put(instanceId, param);
        }
        getAndroidInstance().setAppInstallBlackList(params, mBatchCallback);
    }

    private void queryInstallBlacklist() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().describeAppInstallBlackList(params, mBatchCallback);
    }

    private void clearInstallBlacklist() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().clearAppInstallBlackList(params, mBatchCallback);
    }

    private void getNavigationBarStatus() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().getNavVisibleStatus(params, mBatchCallback);
    }

    private void getMediaVolume() {
        Map<String, JsonObject> params = new HashMap<>();
        for (String instanceId : mInstanceIds) {
            params.put(instanceId, new JsonObject());
        }
        getAndroidInstance().getSystemMusicVolume(params, mBatchCallback);
    }
}