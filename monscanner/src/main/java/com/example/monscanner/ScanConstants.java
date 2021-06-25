package com.example.monscanner;

import android.content.ContentResolver;
import android.os.Environment;

import java.io.File;

/**
 * Constantes qui sont utilisées dans la librairie
 */
public abstract class ScanConstants {

    public static final int OPEN_GALERIE = 1;
    public static final int OPEN_CAMERA = 2;
    static final String PHOTO_RESULT = "photo_result";
    static final int PICKFILE_REQUEST_CODE = 3;
    static final int START_CAMERA_REQUEST_CODE = 4;
    public static String IMAGE_PATH = null;
    public static final String OPEN_INTENT_PREFERENCE = "open_intent_preference";
    final static String SELECTED_BITMAP = "selectedBitmap";
    final static String SCANNED_RESULT = "scannedResult";
    final static int TAKE_PHOTO_REQUEST_CODE = 5;
    public static final String ORIGINAL_FILE = "original_photo_file";
    public static File PHOTO_FILE = null;
    public static ContentResolver contentResolver = null;
}
