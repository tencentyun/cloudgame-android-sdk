package com.tencent.tcr.sdk.demo.cloudphone.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Debug utils
 */
public class DebugUtil {

    private static final int DEFAULT_STACK_LINE = 10;

    /**
     * get the stack trace with {@link DebugUtil#DEFAULT_STACK_LINE} line.
     *
     * @return the invoke stack string of this method.
     */
    @NonNull
    public static String getStackTrace() {
        return getStackTrace(DEFAULT_STACK_LINE);
    }

    /**
     * get the stack trace with {@code line} line.
     *
     * @param line constrain the max line count of the return stack. line<=0 will be refined to {@link
     *         DebugUtil#DEFAULT_STACK_LINE}.
     * @return the invoke stack string of this method.
     */
    @NonNull
    public static String getStackTrace(int line) {
        return getStackTrace(Thread.currentThread().getStackTrace(), line);
    }

    /**
     * get the stack trace
     *
     * @param elements StackTraceElement
     * @param line constrain the max line count of the return stack. line<=0 will be refined to {@link
     *         DebugUtil#DEFAULT_STACK_LINE}.
     * @return the stack string of {@code elements}
     */
    @NonNull
    private static String getStackTrace(@Nullable StackTraceElement[] elements, int line) {
        if (elements == null) {
            return "";
        }
        if (line <= 0) {
            line = DEFAULT_STACK_LINE;
        }

        int count = 0;
        StringBuilder sb = new StringBuilder();
        // start from your calling position excluding Thread.getStackTrace() and this method itself.
        for (int i = 3; i < elements.length && count < line; ++i) {
            StackTraceElement stackTraceElement = elements[i];
            if (stackTraceElement != null) {
                if (sb.length() > 0) {
                    sb.append("\n    ");
                }
                sb.append(stackTraceElement);
                count++;
            }
        }
        return sb.toString();
    }
}
