package com.tencent.tcrdemo.utils;

import static com.tencent.tcrdemo.TcrDemoApplication.getContext;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioRecord.Builder;
import android.media.AudioTimestamp;
import android.media.MediaRecorder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import androidx.core.app.ActivityCompat;

import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcr.sdk.api.utils.CustomAudioBufferUtil;

import java.nio.ByteBuffer;

/**
 * 自定义音频采集类
 */
public class CustomAudioCapturer {
    private TcrSession mSession;
    private final int sampleRateInHz = 48000;
    private final int channelNum = 2;
    private final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private final int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    private AudioRecord audioRecord;
    private volatile boolean running = false;
    private Thread audioThread;

    public void startRecording(TcrSession session) {
        if (running) {
            return;
        }
        mSession = session;
        final int audioSource = MediaRecorder.AudioSource.MIC;
        if (VERSION.SDK_INT >= VERSION_CODES.M) {
            audioRecord = createAudioRecordOnMOrHigher(
                    audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        } else {
            audioRecord = createAudioRecordOnLowerThanM(audioSource, sampleRateInHz, channelConfig, audioFormat,
                    bufferSizeInBytes);
        }
        running = true;

        audioThread = new Thread(() -> {
            // 使用CustomAudioBufferUtil分配ByteBuffer大小
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(CustomAudioBufferUtil
                    .getCustomAudioCaptureDataBufferSize(sampleRateInHz, channelNum == 2));
            audioRecord.startRecording();
            AudioTimestamp audioTimestamp = null;
            if (VERSION.SDK_INT >= 24) {
                audioTimestamp = new AudioTimestamp();
            }
            while (running) {
                // 读取音频数据
                int bytesRead = audioRecord.read(byteBuffer, byteBuffer.capacity());
                if (bytesRead == byteBuffer.capacity()) {
                    long captureTimeNs = 0;
                    if (VERSION.SDK_INT >= 24) {
                        // 获取时间戳
                        if (audioRecord.getTimestamp(audioTimestamp, AudioTimestamp.TIMEBASE_MONOTONIC)
                                == AudioRecord.SUCCESS) {
                            captureTimeNs = audioTimestamp.nanoTime;
                        }
                    }
                    if (mSession != null) {
                        // 发送自定义采集音频
                        mSession.sendCustomAudioData(byteBuffer, captureTimeNs);
                    }
                    byteBuffer.clear();
                }
            }
        });
        audioThread.start();
    }

    public void stopRecording() {
        running = false;
        if(audioRecord!=null)audioRecord.stop();
        audioRecord = null;
        audioThread = null;
        mSession = null;
    }

    private static AudioRecord createAudioRecordOnMOrHigher(
            int audioSource, int sampleRate, int channelConfig, int audioFormat, int bufferSizeInBytes) {
        if (ActivityCompat.checkSelfPermission(getContext(), permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || VERSION.SDK_INT < VERSION_CODES.M) {
            return null;
        }
        return new Builder()
                .setAudioSource(audioSource)
                .setAudioFormat(new AudioFormat.Builder()
                        .setEncoding(audioFormat)
                        .setSampleRate(sampleRate)
                        .setChannelMask(channelConfig)
                        .build())
                .setBufferSizeInBytes(bufferSizeInBytes)
                .build();
    }

    private static AudioRecord createAudioRecordOnLowerThanM(
            int audioSource, int sampleRate, int channelConfig, int audioFormat, int bufferSizeInBytes) {
        if (ActivityCompat.checkSelfPermission(getContext(), permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSizeInBytes);
    }

    public int getSampleRateInHz() {
        return sampleRateInHz;
    }

    public int getChannelNum() {
        return channelNum;
    }

    public int getAudioFormat() {
        return audioFormat;
    }
}