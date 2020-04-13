package com.yozhik.apartremotecontroller.data.repository;

import android.content.Context;

import com.yozhik.apartremotecontroller.Global;

public class SharedPreferencesRepository {

    private static final String DEFAULT_ZONE_NAME = "Зона ";
    private static final String DEFAULT_SOURCE_NAME = "Источник ";

    private static final String OPTIONS = "options";
    private static final String HOST_PREFERENCES = "host_preferences";

    private static final String IS_FIRST_LAUNCH = "is_first_launch";
    private static final String IP_VALUE = "ip_value";
    private static final String PORT_VALUE = "port_value";
    private static final String ZONE_NAME = "zone_name";
    private static final String SOURCE_NAME = "source_name";

    public static boolean isFirstLaunch(Context context) {
        return context.getSharedPreferences(OPTIONS, Context.MODE_PRIVATE)
                .getBoolean(IS_FIRST_LAUNCH, true);
    }

    public static void setFirstLaunch(Context context, boolean isFirstLaunch) {
        context.getSharedPreferences(OPTIONS, Context.MODE_PRIVATE)
                .edit().putBoolean(IS_FIRST_LAUNCH, isFirstLaunch).apply();
    }

    public static void setHost(Context context, String ip, Integer port) {
        context.getSharedPreferences(HOST_PREFERENCES, Context.MODE_PRIVATE)
                .edit().putString(IP_VALUE, ip).apply();
        context.getSharedPreferences(HOST_PREFERENCES, Context.MODE_PRIVATE)
                .edit().putInt(PORT_VALUE, port).apply();
    }

    public static String getIp(Context context) {
        return context.getSharedPreferences(HOST_PREFERENCES, Context.MODE_PRIVATE)
                .getString(IP_VALUE, Global.DEFAULT_IP);
    }

    public static Integer getPort(Context context) {
        return context.getSharedPreferences(HOST_PREFERENCES, Context.MODE_PRIVATE)
                .getInt(PORT_VALUE, Global.DEFAULT_PORT);
    }

    public static void setZoneName(Context context, Integer index, String name) {
        context.getSharedPreferences(OPTIONS, Context.MODE_PRIVATE)
                .edit().putString(ZONE_NAME + index, name).apply();
    }

    public static String getZoneName(Context context, Integer index) {
        return context.getSharedPreferences(OPTIONS, Context.MODE_PRIVATE)
                .getString(ZONE_NAME + index, DEFAULT_ZONE_NAME + index);
    }

    public static void setSourceName(Context context, Integer index, String name) {
        context.getSharedPreferences(OPTIONS, Context.MODE_PRIVATE)
                .edit().putString(SOURCE_NAME + index, name).apply();
    }

    public static String getSourceName(Context context, Integer index) {
        return context.getSharedPreferences(OPTIONS, Context.MODE_PRIVATE)
                .getString(SOURCE_NAME + index, DEFAULT_SOURCE_NAME + index);
    }
}
