package com.eanyatonic.cctvViewer.tools;

import android.os.Build;
import android.util.Log;

public class SysTool {
    public static String showSysAach(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS[0];
        } else {
            return Build.CPU_ABI;
        }
    }
}
