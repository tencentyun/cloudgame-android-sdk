package com.tencent.tcr.demo.cloudphone.operator.network;

import java.util.List;

/**
 * 文件上传响应类，用于表示文件上传操作的结果。
 * 包含上传状态码、消息以及上传文件的详细信息。
 */
public class UploadResponse {

    /**
     * 上传操作的状态码。
     * - 0 表示成功。
     * - 非 0 表示失败，具体错误码由业务逻辑定义。
     */
    public int Code;

    /**
     * 上传操作的描述消息。
     * 通常用于在失败时提供错误信息，成功时可能为空或包含提示信息。
     */
    public String Message;

    /**
     * 上传文件的状态列表。
     * 每个元素表示一个文件的上传状态，包含文件名和云端路径等信息。
     */
    public List<FileStatus> FileStatus;

    @Override
    public String toString() {
        return "UploadResponse{" +
                "Code=" + Code +
                ", Message='" + Message + '\'' +
                ", FileStatus=" + FileStatus +
                '}';
    }

    /**
     * 文件状态类，表示单个文件的上传结果。
     */
    public static class FileStatus {

        /**
         * 上传的文件名。
         */
        public String FileName;

        /**
         * 文件在云端的存储路径。
         */
        public String CloudPath;

        @Override
        public String toString() {
            return "FileStatus{" +
                    "FileName='" + FileName + '\'' +
                    ", CloudPath='" + CloudPath + '\'' +
                    '}';
        }
    }
}
