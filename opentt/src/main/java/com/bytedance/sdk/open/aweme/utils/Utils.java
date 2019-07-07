package com.bytedance.sdk.open.aweme.utils;

import android.content.Context;

/**
 * Created by gy on 2019/1/15.
 */
public class Utils {
    public static float dip2Px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }
}
