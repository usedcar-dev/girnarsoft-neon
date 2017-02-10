package com.gaadi.neon.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.gaadi.neon.Enumerations.ResponseCode;
import com.gaadi.neon.interfaces.ICameraParam;
import com.gaadi.neon.interfaces.IGalleryParam;
import com.gaadi.neon.interfaces.INeutralParam;
import com.gaadi.neon.interfaces.IParam;
import com.gaadi.neon.interfaces.OnImageCollectionListener;
import com.gaadi.neon.model.ImageTagModel;
import com.scanlibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author princebatra
 * @version 1.0
 * @since 1/2/17
 */
public class NeonImagesHandler {

    private static NeonImagesHandler singleonInstance;
    private static boolean clearInstance;
    private List<FileInfo> imagesCollection;
    private ICameraParam cameraParam;
    private IGalleryParam galleryParam;
    private boolean neutralEnabled;
    private INeutralParam neutralParam;
    private OnImageCollectionListener imageResultListener;

    private NeonImagesHandler() {
    }

    public synchronized static NeonImagesHandler getSingleonInstance() {
        if (singleonInstance == null || clearInstance) {
            singleonInstance = new NeonImagesHandler();
            clearInstance = false;
        }
        return singleonInstance;
    }

    private void scheduleSinletonClearance() {
        clearInstance = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSingleonInstance();
            }
        }, 10000);
    }

    public OnImageCollectionListener getImageResultListener() {
        return imageResultListener;
    }

    public void setImageResultListener(OnImageCollectionListener imageResultListener) {
        this.imageResultListener = imageResultListener;
    }

    public IParam getGenericParam() {
        if (galleryParam != null)
            return galleryParam;
        else if (cameraParam != null)
            return cameraParam;
        else
            return neutralParam;
    }

    public boolean isNeutralEnabled() {
        return neutralEnabled;
    }

    public void setNeutralEnabled(boolean neutralEnabled) {
        this.neutralEnabled = neutralEnabled;
    }

    public INeutralParam getNeutralParam() {
        return neutralParam;
    }

    public void setNeutralParam(INeutralParam neutralParam) {
        this.neutralParam = neutralParam;
    }

    public List<FileInfo> getImagesCollection() {
        return imagesCollection;
    }

    public void setImagesCollection(List<FileInfo> allreadyAdded) {
        imagesCollection = new ArrayList<>();
        if (allreadyAdded != null && allreadyAdded.size() > 0) {
            for (int i = 0; i < allreadyAdded.size(); i++) {
                FileInfo cloneFile = new FileInfo();
                FileInfo originalFile = allreadyAdded.get(i);
                if (originalFile == null) {
                    continue;
                }
                if (originalFile.getFileTag() != null) {
                    cloneFile.setFileTag(new ImageTagModel(originalFile.getFileTag().getTagName(), originalFile.getFileTag().getTagId(), originalFile.getFileTag().isMandatory()));
                }
                cloneFile.setSelected(originalFile.getSelected());
                cloneFile.setSource(originalFile.getSource());
                cloneFile.setFileName(originalFile.getFileName());
                cloneFile.setDateTimeTaken(originalFile.getDateTimeTaken());
                cloneFile.setDisplayName(originalFile.getDisplayName());
                cloneFile.setFileCount(originalFile.getFileCount());
                cloneFile.setFilePath(originalFile.getFilePath());
                cloneFile.setType(originalFile.getType());
                imagesCollection.add(cloneFile);
            }
        }
    }

    public boolean checkImagesAvailableForTag(ImageTagModel tagModel) {
        if (imagesCollection == null || imagesCollection.size() <= 0) {
            return false;
        }
        for (int i = 0; i < imagesCollection.size(); i++) {
            if (imagesCollection.get(i).getFileTag() != null && imagesCollection.get(i).getFileTag().getTagId().equals(tagModel.getTagId()) &&
                    imagesCollection.get(i).getFileTag().getTagName().equals(tagModel.getTagName())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkImageAvailableForPath(FileInfo fileInfo) {
        if (imagesCollection == null || imagesCollection.size() <= 0) {
            return false;
        }
        for (int i = 0; i < imagesCollection.size(); i++) {
            if (imagesCollection.get(i).getFilePath().equalsIgnoreCase(fileInfo.getFilePath())) {
                return true;
            }
        }
        return false;
    }

    public boolean removeFromCollection(FileInfo fileInfo) {
        if (imagesCollection == null || imagesCollection.size() <= 0) {
            return true;
        }
        for (int i = 0; i < imagesCollection.size(); i++) {
            if (imagesCollection.get(i).getFilePath().equals(fileInfo.getFilePath())) {
                return imagesCollection.remove(i) != null;
            }
        }
        return true;
    }

    public boolean putInImageCollection(FileInfo fileInfo, Context context) {
        if (imagesCollection == null) {
            imagesCollection = new ArrayList<>();
        }
        if (!getGenericParam().getTagEnabled() && getGenericParam().getNumberOfPhotos() > 0 &&
                getImagesCollection() != null &&
                getGenericParam().getNumberOfPhotos() == getImagesCollection().size()) {
            Toast.makeText(context, context.getString(R.string.max_count_error,getGenericParam().getNumberOfPhotos()), Toast.LENGTH_SHORT).show();
            return false;
        }
        return imagesCollection.add(fileInfo);
    }

    public boolean removeFromCollection(int position) {
        return imagesCollection == null || imagesCollection.size() <= 0 || imagesCollection.remove(position) != null;
    }

    private HashMap<String, List<FileInfo>> getFileHashMap() {
        if (imagesCollection == null || imagesCollection.size() <= 0) {
            return null;
        }
        HashMap<String, List<FileInfo>> hashMap = new HashMap<>();

        for (int i = 0; i < imagesCollection.size(); i++) {
            FileInfo singleFile = imagesCollection.get(i);
            if (singleFile.getFileTag() == null) {
                continue;
            }
            if (hashMap.containsKey(singleFile.getFileTag().getTagId())) {
                hashMap.get(singleFile.getFileTag().getTagId()).add(singleFile);
            } else {
                List<FileInfo> singleTagFiles = new ArrayList<>();
                singleTagFiles.add(singleFile);
                hashMap.put(singleFile.getFileTag().getTagId(), singleTagFiles);
            }
        }
        return hashMap;
    }

    public IGalleryParam getGalleryParam() {
        return galleryParam;
    }

    public void setGalleryParam(IGalleryParam params) {
        this.galleryParam = params;
    }

    public ICameraParam getCameraParam() {
        return cameraParam;
    }

    public void setCameraParam(ICameraParam cameraParam) {
        this.cameraParam = cameraParam;
    }

    public void sendImageCollectionAndFinish(Activity activity, ResponseCode responseCode) {
        NeonImagesHandler.getSingleonInstance().getImageResultListener().imageCollection(NeonImagesHandler.getSingleonInstance().getImagesCollection(), responseCode);
        NeonImagesHandler.getSingleonInstance().getImageResultListener().imageCollection(NeonImagesHandler.getSingleonInstance().getFileHashMap(), responseCode);
        NeonImagesHandler.getSingleonInstance().scheduleSinletonClearance();
        activity.finish();
    }

    public void showBackOperationAlertIfNeeded(final Activity activity) {
        //TODO Review Lakshay : Use strings.xml
        new AlertDialog.Builder(activity).setTitle("Are you sure want to go back?")
                .setCancelable(true).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendImageCollectionAndFinish(activity, ResponseCode.Back);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

}