package com.bytedance.sdk.open.aweme.common.constants;

/**
 * 常量
 * Created by gouyang on 209/1/20.
 */
public interface Constants {
    /**
     * 客户端错误码
     */
    interface ErrorCode {
        int OP_ERROR_NO_CONNECTION = -12;
        int OP_ERROR_CONNECT_TIMEOUT = -13;
        int OP_ERROR_NETWORK_ERROR = -15;
    }
}
