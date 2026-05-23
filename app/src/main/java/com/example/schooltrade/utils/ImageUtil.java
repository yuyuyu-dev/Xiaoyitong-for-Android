package com.example.schooltrade.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import android.widget.ImageView;
import com.example.schooltrade.R;
import java.io.ByteArrayOutputStream;

public class ImageUtil {
    public static void loadImage(ImageView iv, String url) {
        iv.setImageResource(R.drawable.bg_image_placeholder);
    }

    /**
     * 将Bitmap转换为Base64字符串
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}