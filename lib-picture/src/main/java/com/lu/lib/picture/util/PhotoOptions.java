package com.lu.lib.picture.util;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author luzeyan
 * @time 2018/2/8 下午8:19
 * @description 获取图片基本设置
 */

public class PhotoOptions implements Parcelable {

    /**
     * 最多可选图片数量
     */
    public int maxCount;
    /**
     * 当前已选图片数量
     */
    public int currentCount;
    /**
     * 目标图片数量
     */
    public int targetCount;
    /**
     * 图片是否压缩
     */
    public boolean compress;
    /**
     * 相册图片是否可剪裁
     */
    public boolean selectPhotoCrop;
    /**
     * 相机图片是否可剪裁
     */
    public boolean takePhotoCrop;
    /**
     * 裁剪框适应宽高比例
     */
    public boolean fixAspectRatio;
    /**
     * 裁剪宽度
     */
    public int aspectRatioX;
    /**
     * 裁剪高度
     */
    public int aspectRatioY;


    private PhotoOptions(Builder builder) {
        maxCount = builder.maxCount;
        compress = builder.compress;
        currentCount = builder.currentCount;
        selectPhotoCrop = builder.selectPhotoCrop;
        takePhotoCrop = builder.takePhotoCrop;
        fixAspectRatio = builder.fixAspectRatio;
        aspectRatioX = builder.aspectRatioX;
        aspectRatioY = builder.aspectRatioY;
        targetCount = maxCount - currentCount;
    }

    public static class Builder {

        private int maxCount;
        private int currentCount;
        private boolean compress;
        private boolean selectPhotoCrop;
        private boolean takePhotoCrop;
        private boolean fixAspectRatio;
        private int aspectRatioX;
        private int aspectRatioY;

        public Builder() {
            maxCount = 1;
            currentCount = 0;
            compress = true;
            selectPhotoCrop = true;
            takePhotoCrop = true;
            aspectRatioX = 1;
            aspectRatioY = 1;
        }

        /**
         * 最多可选图片数量
         */
        public Builder setMaxCount(int maxCount) {
            this.maxCount = maxCount;
            return this;
        }

        /**
         * 相机图片是否可剪裁
         */
        public Builder setTakePhotoCrop(boolean takePhotoCrop) {
            this.takePhotoCrop = takePhotoCrop;
            return this;
        }

        /**
         * 相册图片是否可剪裁
         */
        public Builder setSelectPhotoCrop(boolean selectPhotoCrop) {
            this.selectPhotoCrop = selectPhotoCrop;
            return this;
        }

        public Builder setCurrentCount(int currentCount) {
            this.currentCount = currentCount;
            return this;
        }

        public Builder setFixAspectRatio(boolean fixAspectRatio) {
            this.fixAspectRatio = fixAspectRatio;
            return this;
        }

        public Builder setAspectRatioX(int aspectRatioX) {
            this.aspectRatioX = aspectRatioX;
            return this;
        }

        public Builder setAspectRatioY(int aspectRatioY) {
            this.aspectRatioY = aspectRatioY;
            return this;
        }

        /**
         * 图片是否压缩
         */
        public Builder setCompress(boolean isCompress) {
            this.compress = isCompress;
            return this;
        }

        /**
         * 裁剪宽高
         */
        public Builder setAspectRatio(int aspectRatioX, int aspectRatioY) {
            this.aspectRatioX = aspectRatioX;
            this.aspectRatioY = aspectRatioY;
            return this;
        }

        public PhotoOptions build() {
            if (aspectRatioX != 1 || aspectRatioY != 1) {
                fixAspectRatio = true;
            }

            return new PhotoOptions(this);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.maxCount);
        dest.writeInt(this.currentCount);
        dest.writeInt(this.targetCount);
        dest.writeByte(this.compress ? (byte) 1 : (byte) 0);
        dest.writeByte(this.selectPhotoCrop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.takePhotoCrop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.fixAspectRatio ? (byte) 1 : (byte) 0);
        dest.writeInt(this.aspectRatioX);
        dest.writeInt(this.aspectRatioY);
    }

    protected PhotoOptions(Parcel in) {
        this.maxCount = in.readInt();
        this.currentCount = in.readInt();
        this.targetCount = in.readInt();
        this.compress = in.readByte() != 0;
        this.selectPhotoCrop = in.readByte() != 0;
        this.takePhotoCrop = in.readByte() != 0;
        this.fixAspectRatio = in.readByte() != 0;
        this.aspectRatioX = in.readInt();
        this.aspectRatioY = in.readInt();
    }

    public static final Creator<PhotoOptions> CREATOR = new Creator<PhotoOptions>() {
        @Override
        public PhotoOptions createFromParcel(Parcel source) {
            return new PhotoOptions(source);
        }

        @Override
        public PhotoOptions[] newArray(int size) {
            return new PhotoOptions[size];
        }
    };
}
