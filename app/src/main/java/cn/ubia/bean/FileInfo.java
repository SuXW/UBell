package cn.ubia.bean;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;
/**
 * Created by Steven.lin on 2018/4/19.
 */

public class FileInfo implements Serializable {
    private Bitmap thumbnailImg;
    private String displayName="";
    private String fileName="";
    private String fileType="";
    private long fileSize;
    private String fileCloudPath="";
    private String fileImgCloudPath="";
    private String fileTriggerType = "";
    private Date recordTime;
    public Date getRecordTime() {
        return recordTime;
    }
    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
    public String getFileTriggerType() {
        return fileTriggerType;
    }
    public void setFileTriggerType(String fileTriggerType) {
        this.fileTriggerType = fileTriggerType;
    }

    public String getFileImgCloudPath() {
        return fileImgCloudPath;
    }

    public void setFileImgCloudPath(String fileImgCloudPath) {
        this.fileImgCloudPath = fileImgCloudPath;
    }

    private String fileLocatUri="";
    private String fileLocatPath="";
    private int fileTimeLength = 0;
    private int downLoadState = 0;

    public String getFileLocatUri() {
        return fileLocatUri;
    }

    public void setFileLocatUri(String fileLocatUri) {
        this.fileLocatUri = fileLocatUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 0:No 1:END 2:ING
     *
     * @return
     */
    public int getDownLoadState() {
        return downLoadState;
    }

    public void setDownLoadState(int downLoadState) {
        this.downLoadState = downLoadState;
    }

    public int getFileTimeLength() {
        return fileTimeLength;
    }

    public void setFileTimeLength(int fileTimeLength) {
        this.fileTimeLength = fileTimeLength;
    }

    public Bitmap getThumbnailImg() {
        return thumbnailImg;
    }

    public void setThumbnailImg(Bitmap thumbnailImg) {
        this.thumbnailImg = thumbnailImg;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileCloudPath() {
        return fileCloudPath;
    }

    public void setFileCloudPath(String fileCloudPath) {
        this.fileCloudPath = fileCloudPath;
    }

    public String getFileLocatPath() {
        return fileLocatPath;
    }

    public void setFileLocatPath(String fileLocatPath) {
        this.fileLocatPath = fileLocatPath;
    }

    @Override
    public String toString() {
        return "fileName:" + fileName + ",fileType:" + fileType + ",fileSize:" + fileSize + "\nfileCloudPath:" + fileCloudPath;
    }
}
