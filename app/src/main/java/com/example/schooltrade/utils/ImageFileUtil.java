package com.example.schooltrade.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 图片文件保存工具类
 */
public class ImageFileUtil {
    
    /**
     * 保存Bitmap到本地文件
     * @param context 上下文
     * @param bitmap 图片
     * @return 文件路径，失败返回null
     */
    public static String saveImageToStorage(Context context, Bitmap bitmap) {
        // 获取外部存储目录
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        
        if (storageDir == null) {
            storageDir = context.getFilesDir();
        }
        
        // 创建SchoolTrade文件夹
        File appDir = new File(storageDir, "SchoolTrade");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        
        // 生成文件名（使用时间戳）
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";
        
        File imageFile = new File(appDir, fileName);
        
        try {
            // 压缩并保存图片
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
            
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 删除图片文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteImage(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
