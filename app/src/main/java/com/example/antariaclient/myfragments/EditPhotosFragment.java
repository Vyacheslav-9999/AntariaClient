package com.example.antariaclient.myfragments;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.antariaclient.BuildConfig;
import com.example.antariaclient.CheckingSiteAppearanceActivity;
import com.example.antariaclient.Config;
import com.example.antariaclient.ConfigLiveData;
import com.example.antariaclient.DataEditingActivity;
import com.example.antariaclient.ImageSlider;
import com.example.antariaclient.Photo;
import com.example.antariaclient.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditPhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditPhotosFragment extends Fragment implements View.OnClickListener {
    private ActivityResultLauncher<String[]> galleryActivityLauncher;
    private ConfigLiveData cfgData;
    private ImageSlider image;
    private List<Photo> photos;
    private int current;

    public EditPhotosFragment() {
        // Required empty public constructor
    }

    public static EditPhotosFragment newInstance() {
        EditPhotosFragment fragment = new EditPhotosFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        checkPermissions();
        return inflater.inflate(R.layout.fragment_edit_photos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cfgData = new ViewModelProvider(requireActivity()).get(ConfigLiveData.class);
        current = cfgData.current;
        photos = cfgData.getConfigValue().getPhotos();
        assignListener(this);
        image = initSlider(requireView().findViewById(R.id.slider));
        galleryActivityLauncher =
                registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::onImageSelectResult);
        showImage(current);
    }

    private void checkPermissions(){
        boolean permissonGranted = ContextCompat
                .checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(!(permissonGranted)){
            String[] perms = new String[2];
            perms[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
            perms[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(perms,100);
            }
        }
    }

    public void onImageSelectResult(Uri result) {
        ConfigLiveData cfgData = new ViewModelProvider(requireActivity()).get(ConfigLiveData.class);
        Config cfg = cfgData.getConfigValue();
        if (cfg != null) { // content://com.android.externalstorage.documents/document/primary%3ADCIM%2F2020-06-07%2008-09-36-1.jpg
            cfg.addPhoto(new Photo(result.toString()));
            cfgData.postConfig(cfg);
            cfgData.current = photos.size() - 1;
        }
    }

    private void assignListener(View.OnClickListener listener) {
        View view = requireView();
//        view.findViewById(R.id.next_picture).setOnClickListener(listener);
//        view.findViewById(R.id.prev_picture).setOnClickListener(listener);
        view.findViewById(R.id.delete).setOnClickListener(listener);
        view.findViewById(R.id.add).setOnClickListener(listener);
    }

    private ImageSlider initSlider(ImageSlider image) {
        image.getSettings().setSupportZoom(true);
        image.getSettings().setBuiltInZoomControls(true);
        image.getSettings().setDisplayZoomControls(false);

        image.setPadding(0, 0, 0, 0);
        image.loadUrl(DataEditingActivity.MAIN_ADDRESS + "/" + photos.get(0).getImage());
        image.setInitialScale(1);
        image.getSettings().setLoadWithOverviewMode(true);
        image.getSettings().setUseWideViewPort(true);
        image.getSettings().setAllowFileAccess(true);
        image.getSettings().setAllowFileAccessFromFileURLs(true);
        image.getSettings().setAllowContentAccess(true);
        image.setGestureDetector(new GestureDetector(new CustomGestureDetector()));
        return image;
    }

    public class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
            else {
                try { // right to left
                    if (e1.getX() - e2.getX() > convertDpToPixel(200) && Math.abs(velocityX) > 550) {
                        nextImage();
                        return true;
                    } //left to right
                    else if (e2.getX() - e1.getX() > convertDpToPixel(200) && Math.abs(velocityX) > 550) {
                        prevImage();
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
    }

    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.delete) {
            if (photos != null) {
                deleteImageAt(current);
                cfgData.postConfig(cfgData.getConfigValue());
            }
        } else if (view.getId() == R.id.add) {
            addNewImage();
            cfgData.postConfig(cfgData.getConfigValue());
        }
    }

    private void deleteImageAt(int index) {
        if(photos.size() == 0){
            Toast.makeText(requireContext(), "No photos", Toast.LENGTH_SHORT).show();
        }
        else {
            if (index == photos.size() - 1) {
                current--;
            }
            photos.remove(index);
            showImage(current);
            ConfigLiveData cfgData = new ViewModelProvider(requireActivity()).get(ConfigLiveData.class);
            Config cfg = cfgData.getConfigValue();
            cfg.setPhotos(photos);
        }
    }

    private void addNewImage() {
        galleryActivityLauncher.launch(new String[]{"image/*"});
        image.setVisibility(View.VISIBLE);
    }

    public void nextImage() {
        if (current < photos.size() - 1) {
            current++;
            showImage(current);
        } else {

        }
    }

    public void prevImage() {
        if (current > 0) {
            current--;
            showImage(current);
        } else {

        }
    }

    void showImage(int current) {
        TextView t = requireActivity().findViewById(R.id.photo_name);
        if(current != -1) {
            String photo = photos.get(current).getImage();
            if(t!= null)t.setText(photo);
           // if (photo.contains("http")) {
                try {
                   Uri uri = downloadImage(photo);
                    image.loadUrl(uri.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //} else image.loadUrl(photo);
        }else{
            image.setVisibility(View.INVISIBLE);
            t.setText("-");
        }
    }

    private Uri downloadImage(String name) throws IOException {
        String path = requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/antaria";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DataEditingActivity.MAIN_ADDRESS+ "/" + name))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setDestinationInExternalFilesDir(requireContext(),Environment.DIRECTORY_DCIM,"antaria")
                .setTitle(name)// Title of the Download Notification
                .setDescription("Downloading " + image)// Description of the Download Notification
                //.setRequiresCharging(false)// Set if charging is required to begin the download
                .setAllowedOverMetered(true);// Set if download is allowed on Mobile network
        request.allowScanningByMediaScanner();
        DownloadManager downloadManager= (DownloadManager) requireActivity().getSystemService(DOWNLOAD_SERVICE);
        long downloadID = downloadManager.enqueue(request);
        File f = new File(path + "/" + name);
        //return Uri.fromFile(f);
        Uri uri = null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID + ".provider", f);
        }else uri = Uri.fromFile(f);
        return uri;
    }
}