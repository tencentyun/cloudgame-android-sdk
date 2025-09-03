package com.tencent.tcr.sdk.demo.cloudstream.util;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

/**
 * Gson工具类
 */
public class GsonUtils {

    private static final String TAG = "GsonUtils";

    // 默认的Gson实例。Gson类本身是线程安全的，不需要用ThreadLocal.
    private static final Gson DEFAULT_GSON = new Gson();

    /**
     * 返回默认的Gson对象实例，用于处理Json数据
     *
     * @return Gson对象
     */
    @NonNull
    public static Gson getGson() {
        return DEFAULT_GSON;
    }

    /**
     * 该方法将指定的Json反序列化为指定类的对象，如有异常进行捕获并打印log和调用堆栈。参考 {@link Gson#fromJson(String, Class)} 。
     *
     * @param json 要从中反序列化对象的字符串
     * @param classOfT T的类
     * @param <T> 目标对象的类型
     * @return 来自字符串的 T 类型的对象。如果 json 为 null 或 json 为空 或 无法解析为目标对象，则返回 null。
     * @see Gson#fromJson(String, Class)
     */
    @Nullable
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return fromJson(json, classOfT, true);
    }

    /**
     * 该方法将指定的Json反序列化为指定类的对象，如有异常进行捕获。参考 {@link Gson#fromJson(String, Class)} 。
     *
     * @param json 要从中反序列化对象的字符串
     * @param classOfT T的类
     * @param logError 当捕获异常时，是否打印log和调用堆栈。
     * @param <T> 目标对象的类型
     * @return 来自字符串的 T 类型的对象。如果 json 为 null 或 json 为空 或 无法解析为目标对象，则返回 null。
     * @see Gson#fromJson(String, Class)
     */
    @Nullable
    public static <T> T fromJson(String json, Class<T> classOfT, boolean logError) {
        try {
            return GsonUtils.getGson().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            if (logError) {
                Log.w(TAG,
                        "fromJson() e=" + e.getMessage() + "\njson=" + json + "\nstack=" + DebugUtil.getStackTrace());
            }
            return null;
        }
    }

    /**
     * 该方法将指定的Json反序列化为指定类的对象，如有异常进行捕获并打印log和调用堆栈。参考 {@link Gson#fromJson(JsonElement, Class)} 。
     *
     * @param json 要反序列化对象的 JsonElements 解析树的根
     * @param classOfT T的类
     * @param <T> 目标对象的类型
     * @return 来自 json 的 T 类型的对象。如果 json 为 null 或 无法解析为目标对象，则返回 null。
     * @see Gson#fromJson(JsonElement, Class)
     */
    @Nullable
    public static <T> T fromJson(JsonElement json, Class<T> classOfT) {
        try {
            return GsonUtils.getGson().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            Log.w(TAG, "fromJson() e=" + e.getMessage() + "\njson=" + json + "\nstack=" + DebugUtil.getStackTrace());
            return null;
        }
    }
}
