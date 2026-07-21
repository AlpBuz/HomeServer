package com.homeserver.util;
public class Util{
    // class will just have a bunch of helper functions to use
    public static long formatBytes(long bytes) {
        long gb = bytes / (1024 * 1024 * 1024);
        return gb;
    }
}