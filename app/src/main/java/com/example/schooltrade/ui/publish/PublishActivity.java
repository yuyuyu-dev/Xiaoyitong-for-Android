package com.example.schooltrade.ui.publish;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.model.db.GoodsDao;
import com.example.schooltrade.utils.ImageUtil;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;

public class PublishActivity extends BaseActivity {
    private static final String TAG = "PublishActivity";
    private EditText etTitle, etContent, etPrice, etWant;
    private RadioGroup rgType;
    private ImageView ivProductImage;
    private Button btnSelectImage;
    private int type = 0;
    private String selectedImagePath = null;
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;
    private static final int REQUEST_IMAGE_CAPTURE = 1003;

    @Override
    protected int getLayoutId() { 
        // 使用简化布局
        return R.layout.activity_publish_simple; 
    }

    @Override
    protected void initView() {
        try {
            // 初始化基本控件
            etTitle = findViewById(R.id.et_title);
            etContent = findViewById(R.id.et_content);
            etPrice = findViewById(R.id.et_price);
            etWant = findViewById(R.id.et_want);
            rgType = findViewById(R.id.rg_type);
            ivProductImage = findViewById(R.id.iv_product_image);
            btnSelectImage = findViewById(R.id.btn_select_image);
            Button btnPublish = findViewById(R.id.btn_publish);

            // 检查控件
            if (etTitle == null || etContent == null || etPrice == null || 
                etWant == null || rgType == null || btnPublish == null) {
                ToastUtil.show(this, "页面初始化失败");
                finish();
                return;
            }

            // 类型切换
            rgType.setOnCheckedChangeListener((group, id) -> {
                if (id == R.id.rb_sell) {
                    type = 0;
                    etPrice.setVisibility(View.VISIBLE);
                    etWant.setVisibility(View.GONE);
                } else {
                    type = 1;
                    etWant.setVisibility(View.VISIBLE);
                    etPrice.setVisibility(View.GONE);
                }
            });

            // 图片按钮 - 暂时只显示提示
            if (btnSelectImage != null) {
                btnSelectImage.setOnClickListener(v -> {
                    ToastUtil.show(this, "图片功能开发中");
                });
            }

            // 发布按钮
            btnPublish.setOnClickListener(v -> publish());
            
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "页面加载失败");
            finish();
        }
    }

    // 显示图片选择对话框
    private void showImagePickerDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("选择图片来源")
            .setItems(new String[]{"从相册选择", "拍照"}, (dialog, which) -> {
                if (which == 0) {
                    pickImageFromGallery();
                } else {
                    takePhotoWithCamera();
                }
            })
            .show();
    }

    // 从相册选择图片
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    // 使用相机拍照
    private void takePhotoWithCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        ivProductImage.setImageBitmap(bitmap);
                        ivProductImage.setVisibility(View.VISIBLE);
                        findViewById(R.id.ll_upload_hint).setVisibility(View.GONE);
                        selectedImagePath = ImageUtil.bitmapToBase64(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.show(this, "图片加载失败");
                    }
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    ivProductImage.setImageBitmap(bitmap);
                    ivProductImage.setVisibility(View.VISIBLE);
                    findViewById(R.id.ll_upload_hint).setVisibility(View.GONE);
                    selectedImagePath = ImageUtil.bitmapToBase64(bitmap);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerDialog();
            } else {
                ToastUtil.show(this, "需要相机权限才能拍照");
            }
        }
    }

    private void publish() {
        // 检查用户是否登录
        if (!UserSession.isLogin() || UserSession.getCurrentUser() == null) {
            ToastUtil.show(this, "请先登录");
            finish();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        if (title.isEmpty() || content.isEmpty()) {
            ToastUtil.show(this, "标题和描述不能为空");
            return;
        }

        double price = 0;
        if (type == 0) {
            String priceStr = etPrice.getText().toString().trim();
            if (!priceStr.isEmpty()) {
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    ToastUtil.show(this, "价格格式不正确");
                    return;
                }
            }
        }
        String want = type == 1 ? etWant.getText().toString().trim() : null;

        Goods goods = new Goods();
        goods.setUserId(UserSession.getCurrentUser().getUserId());
        goods.setTitle(title);
        goods.setContent(content);
        goods.setPrice(price);
        goods.setPublishType(type);
        goods.setWantGoods(want);
        goods.setImgUrl(selectedImagePath); // 添加图片路径

        showLoading();
        new Thread(() -> {
            try {
                boolean res = GoodsDao.publishGoods(goods);
                runOnUiThread(() -> {
                    hideLoading();
                    if (res) {
                        ToastUtil.show(this, "发布成功");
                        finish();
                    } else {
                        ToastUtil.show(this, "发布失败");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(this, "发布异常：" + e.getMessage());
                });
            }
        }).start();
    }

    @Override
    protected void initData() {}
}