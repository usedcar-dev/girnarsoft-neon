package com.gaadi.neon.activity.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gaadi.neon.Enumerations.CameraFacing;
import com.gaadi.neon.Enumerations.CameraOrientation;
import com.gaadi.neon.Enumerations.CameraType;
import com.gaadi.neon.PhotosLibrary;
import com.gaadi.neon.activity.ImageShow;
import com.gaadi.neon.adapter.GridFilesAdapter;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.model.ImageTagModel;
import com.gaadi.neon.model.PhotosMode;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.NeonException;
import com.gaadi.neon.util.SingletonClass;
import com.scanlibrary.R;
import com.scanlibrary.databinding.ActivityGridFilesBinding;

import java.util.List;

public class GridFilesActivity extends NeonBaseGalleryActivity {

    MenuItem textViewDone,menuItemCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bindXml();
        String title = getIntent().getStringExtra(Constants.BucketName);
        if (title == null || title.length() <= 0) {
            title = "Files";
        }
        setTitle(title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done_file, menu);
        textViewDone = menu.findItem(R.id.menu_next);
        menuItemCamera = menu.findItem(R.id.menuCamera);
        menuItemCamera.setVisible(SingletonClass.getSingleonInstance().getGalleryParam().galleryToCameraSwitchEnabled());
        textViewDone.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
           onBackPressed();
            return true;
        } else if (id == R.id.menu_next) {
            if (SingletonClass.getSingleonInstance().getImagesCollection() == null ||
                    SingletonClass.getSingleonInstance().getImagesCollection().size() <= 0) {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }else {
                /*if(SingletonClass.getSingleonInstance().getImagesCollection() == null ||
                        SingletonClass.getSingleonInstance().getImagesCollection().size()<=0){
                    Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT).show();
                    return super.onOptionsItemSelected(item);
                }*/
                if (!SingletonClass.getSingleonInstance().isNeutralEnabled()) {
                    Intent intent = new Intent(this, ImageShow.class);
                    startActivity(intent);
                    setResult(Constants.destroyPreviousActivity);
                    finish();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }else if (id == R.id.menuCamera) {
            performCameraOperation();
            setResult(Constants.destroyPreviousActivity);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(SingletonClass.getSingleonInstance().isNeutralEnabled()){
            super.onBackPressed();
        }else{
            if(!SingletonClass.getSingleonInstance().getGalleryParam().enableFolderStructure()){
                if (SingletonClass.getSingleonInstance().getImagesCollection() != null &&
                        SingletonClass.getSingleonInstance().getImagesCollection().size() > 0) {
                    new AlertDialog.Builder(this).setTitle("All Images will be lost. Do you sure want to go back?")
                            .setCancelable(true).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SingletonClass.getSingleonInstance().scheduleSinletonClearance();
                            finish();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }else{
                    super.onBackPressed();
                }
            }else{
                super.onBackPressed();
            }

        }
    }

    private void performCameraOperation() {

        ICameraParam cameraParam = SingletonClass.getSingleonInstance().getCameraParam();
        if(cameraParam == null){
            cameraParam = new ICameraParam() {
                @Override
                public CameraFacing getCameraFacing() {
                    return CameraFacing.front;
                }

                @Override
                public CameraOrientation getCameraOrientation() {
                    return CameraOrientation.portrait;
                }

                @Override
                public boolean getFlashEnabled() {
                    return true;
                }

                @Override
                public boolean getCameraSwitchingEnabled() {
                    return true;
                }

                @Override
                public boolean getVideoCaptureEnabled() {
                    return false;
                }

                @Override
                public CameraType getCameraViewType() {
                    return CameraType.normal_camera;
                }

                @Override
                public boolean cameraToGallerySwitchEnabled() {
                    return true;
                }

                @Override
                public int getNumberOfPhotos() {
                    return SingletonClass.getSingleonInstance().getGalleryParam().getNumberOfPhotos();
                }

                @Override
                public boolean getTagEnabled() {
                    return SingletonClass.getSingleonInstance().getGalleryParam().getTagEnabled();
                }

                @Override
                public List<ImageTagModel> getImageTagsModel() {
                    return SingletonClass.getSingleonInstance().getGalleryParam().getImageTagsModel();
                }

            };
        }
        try {
            PhotosLibrary.collectPhotos(this, PhotosMode.setCameraMode().setParams(cameraParam),SingletonClass.getSingleonInstance().getImageResultListener());
        } catch (NeonException e) {
            e.printStackTrace();
        }
    }


    private void bindXml() {
        ActivityGridFilesBinding binder = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_grid_files,frameLayout,true);
        //ActivityGridFilesBinding binder = DataBindingUtil.setContentView(this, R.layout.activity_grid_files);
        GridFilesAdapter adapter = new GridFilesAdapter(this, getFileFromBucketId(getIntent().getStringExtra(Constants.BucketId)));
        binder.gvFolderPhotos.setAdapter(adapter);
    }


}
