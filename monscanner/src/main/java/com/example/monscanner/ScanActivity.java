package com.example.monscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Cette classe est la seule Activité de la librarie.
 * C'est le cas afin de faciliter le renvoi des données lors de la récupération des données
 * dans le onActivityResult de l'application externe.
 * Cette activité se remplie avec des fragments, cela provoque cependant un ecran vide si l'utilisateur appui plusieurs
 * fois sur la touche "retour" lors du traitement des images.
 */
public class ScanActivity extends AppCompatActivity implements ComponentCallbacks2 {

    private final String TAG = "ScanActivityDebug";
    private Uri fileUri;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private File photoFile;
//    private ImageView photoImageView = null;

    public static final String SCAN_RESULT = "scan_result";
    private boolean onEditionFragment;
    static {
        System.loadLibrary("opencv_java4");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);
//        photoImageView = findViewById(R.id.iv_photo);
        Log.d("SCAN_ACTIVITY_PHOTO","2:"+Uri.fromFile(ScanConstants.PHOTO_FILE).toString());
       // handleIntentPreference();
        try{
            handleFileIntent();
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d("SCAN_ACTIVITY_PHOTO","exception:"+e.getMessage());
        }
        onEditionFragment = false;
    }

    // Ouvre les medias ou la camera en fonction de la demande de l'utilisateur
    private void handleIntentPreference() {
        int preference = getPreferenceContent();
        if (preference == ScanConstants.OPEN_CAMERA) {
            openCamera();
        } else if (preference == ScanConstants.OPEN_GALERIE) {
            openMediaContent();
        }
    }

    private void handleFileIntent() throws  IOException{
        if (getPreferenceContent()==ScanConstants.OPEN_CAMERA){

            Uri fileUri =  FileProvider.getUriForFile(getApplicationContext(),
                    "com.brainx.zytrack_demo.docprovider",
                    ScanConstants.PHOTO_FILE);
            Bitmap bitmap = getBitmap(fileUri);
            postImagePick(bitmap);
          //  Uri photoUri = getUri(bitmap);
            //Bitmap photoBitmap = getBitmap(photoUri);
//            photoImageView.setImageBitmap(bitmap);
            Log.d("SCAN_ACTIVITY_PHOTO","1:"+fileUri.toString());

            //postImagePick(getBitmap(photoUri));
        }
    }

    private Uri checkImageIntent(){
        return (Uri)getIntent().getSerializableExtra(ScanConstants.ORIGINAL_FILE);
    }

    protected int getPreferenceContent() {
        return getIntent().getIntExtra(ScanConstants.OPEN_INTENT_PREFERENCE, 0);
    }

    // Ouvre les médias
    public void openMediaContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE);
    }

    // Ouvre la camera
//    public void openCamera() {
//        Intent intent = new Intent(this, CameraActivity.class);
//        startActivityForResult(intent, ScanConstants.START_CAMERA_REQUEST_CODE);
//    }

    public void openCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    ScanConstants.contentResolver = getContentResolver();
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                            "com.brainx.zytrack_demo.docprovider",
                            photoFile);
                    fileUri = photoURI;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, ScanConstants.TAKE_PHOTO_REQUEST_CODE);
                }
            }

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
//        clearTempImages();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "pic",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        fileUri = Uri.fromFile(image);
        return image;
    }

    // Supprime les fichiers temporaires utiles aux traitements des images
    void clearTempImages() {
        try {
            File tempFolder = new File(ScanConstants.IMAGE_PATH);
            for (File f : tempFolder.listFiles())
                if (!f.delete())
                    Log.d(TAG, "clearTempImages: Impossible de supprimer les fichiers");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {
            try {
                switch (requestCode) {
                    case ScanConstants.TAKE_PHOTO_REQUEST_CODE:
                       // Uri imageUri = Objects.requireNonNull(data.getExtras()).getParcelable(ScanConstants.PHOTO_RESULT);
                        bitmap = getBitmap(fileUri);
                        postImagePick(bitmap);
                        break;

                    case ScanConstants.START_CAMERA_REQUEST_CODE:
                         Uri imageUri = Objects.requireNonNull(data.getExtras()).getParcelable(ScanConstants.PHOTO_RESULT);
                        bitmap = getBitmap(imageUri);
                        postImagePick(bitmap);
                        break;

                    case ScanConstants.PICKFILE_REQUEST_CODE:
                        bitmap = getBitmap(data.getData());
                        postImagePick(bitmap);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            finish();
        }
//        if (bitmap != null) {
//            postImagePick(bitmap);
//        }
    }

    // Traitement de l'image selectionnée
    protected void postImagePick(Bitmap bitmap) {
        Uri uri = getUri(bitmap);
        bitmap.recycle();
        onBitmapSelect(uri);
    }

    // Set le fragment de validation de la forme du document
    public void onBitmapSelect(Uri uri) {
        ShapeValidationFragment fragment = new ShapeValidationFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ScanConstants.SELECTED_BITMAP, uri);
        fragment.setArguments(bundle);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment);
        fragmentTransaction.addToBackStack(ShapeValidationFragment.class.toString());
        fragmentTransaction.commit();
    }

    // Récupère le Bitmap lié à l'Uri passée en paramètre
    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        AssetFileDescriptor fileDescriptor;
        fileDescriptor = getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        assert fileDescriptor != null;
        return BitmapFactory.decodeFileDescriptor(
        fileDescriptor.getFileDescriptor(), null, options);
    }

    // Enregistre le Bitmap et renvoie son Uri
    public static Uri getUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
        Calendar calendar = Calendar.getInstance();
        String filename = calendar.getTime().toString()+"originale.jpg";
        Imgcodecs.imwrite(ScanConstants.IMAGE_PATH+"/originale.jpg",mat);
        return Uri.parse(ScanConstants.IMAGE_PATH+"/originale.jpg");
    }

    // Après la validation de la forme, set le fragment d'édition du document
    public void onScanFinish(Uri uri) {

        try {

            File file = new File(uri.getPath());
            Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
            original = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), new Matrix(), true);
            Utils.bitmapToMat(original,new Mat());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

//            Uri finalUri = getUri(this, original);
            Intent data = new Intent();
            Log.d("SCAN_ACTIVITY_PHOTO_3",uri.toString());
            data.putExtra(ScanActivity.SCAN_RESULT, file);
            setResult(Activity.RESULT_OK, data);
//            clearTempImages();
            System.gc();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SCAN_ACTIVITY_PHOTO_3",e.getMessage());
        }
    }

    private Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    // Renvoie le bitmap redimentionné en fonction de la taille passée en paramètre
    Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    @Override
    public void onTrimMemory(int level) {
        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */
                break;
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:
                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */
                break;
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.peu_de_memoire))
                        .setMessage(getResources().getString(R.string.peu_de_memoire_desc))
                        .create()
                        .show();
                break;
            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }

    @Override
    public void onBackPressed(){
        if (!onEditionFragment)
            finish();
        else {
            super.onBackPressed();
            onEditionFragment = false;
        }
    }
}

