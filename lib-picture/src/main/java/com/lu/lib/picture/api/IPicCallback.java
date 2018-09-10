package com.lu.lib.picture.api;

import java.util.List;

/**
 * @Author: luqihua
 * @Time: 2018/6/12
 * @Description: 选中图片返回
 */

public interface IPicCallback {
    /**
     * 获取多张图片
     *
     * @param paths 图片路径  如果只有一张的时候  取 : paths.get(0)
     */
    void success(List<String> paths);

    /**
     * 获取图片失败
     *
     * @param msg msg
     */
    void error(String msg);
}
