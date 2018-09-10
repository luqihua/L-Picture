package com.lu.lib.picture.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: luqihua
 * @Time: 2018/6/12
 * @Description: 图片
 */

public class PictureItem implements Parcelable {
    private String path;
    private String name;
    private String bucketName;
    private String title;

    public PictureItem(String path, String name, String bucketName, String title) {
        this.path = path;
        this.name = name;
        this.bucketName = bucketName;
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeString(this.bucketName);
        dest.writeString(this.title);
    }

    protected PictureItem(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.bucketName = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<PictureItem> CREATOR = new Parcelable.Creator<PictureItem>() {
        @Override
        public PictureItem createFromParcel(Parcel source) {
            return new PictureItem(source);
        }

        @Override
        public PictureItem[] newArray(int size) {
            return new PictureItem[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PictureItem){
            return this.path.equals(((PictureItem) obj).getPath());
        }
        return false;
    }
}
