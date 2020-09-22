package com.bytedance.sdk.open.tiktok.utils;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class OpenUtils {
    /**
     *  Remove duplicate strings from an array
     */
    public static String[] arrayUnique(String[] str) {
        try {
            if (str != null && str.length > 0) {
                List<String> list = new ArrayList<>(Arrays.asList(str));
                Set<String> set = new HashSet<>(list);
                return set.toArray(new String[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    public static void setViewVisibility(View v, int visiable) {
        if (v == null || v.getVisibility() == visiable || !visibilityValid(visiable)) {
            return;
        }
        v.setVisibility(visiable);
    }

    private static boolean visibilityValid(int visiable) {
        return visiable == View.VISIBLE || visiable == View.GONE || visiable == View.INVISIBLE;
    }
}
