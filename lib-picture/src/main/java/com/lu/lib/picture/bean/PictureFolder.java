package com.lu.lib.picture.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Author: luqihua
 * @Time: 2018/6/12
 * @Description: 图片文件夹
 */

public class PictureFolder implements Parcelable {
    private String name;
    private String path;
    private int count;
    private String firstImagePath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public PictureFolder() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeInt(this.count);
        dest.writeString(this.firstImagePath);
    }

    protected PictureFolder(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.count = in.readInt();
        this.firstImagePath = in.readString();
    }

    public static final Creator<PictureFolder> CREATOR = new Creator<PictureFolder>() {
        @Override
        public PictureFolder createFromParcel(Parcel source) {
            return new PictureFolder(source);
        }

        @Override
        public PictureFolder[] newArray(int size) {
            return new PictureFolder[size];
        }
    };
}
