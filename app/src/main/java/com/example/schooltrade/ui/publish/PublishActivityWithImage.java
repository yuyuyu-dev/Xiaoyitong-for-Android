package com.example.schooltrade.ui.publish;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.model.db.GoodsDao;
import com.example.schooltrade.utils.ImageFileUtil;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;

/**
 * 发布商品Activity - 带图片上传功能
 */
public class PublishActivityWithImage extends BaseActivity {
    private EditText etTitle, etContent, etPrice;
    private ImageView ivProductImage;
    private LinearLayout llUploadHint;
    private String selectedImagePath = null;
    
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;
    private static final int REQUEST_IMAGE_CAPTURE = 1003;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_with_image;
    }

    @Override
    protected void initView() {
        try {
            // 返回按钮
            ImageView btnBack = findViewById(R.id.btn_back);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // 初始化控件
            etTitle = findViewById(R.id.et_title);
            etContent = findViewById(R.id.et_content);
            etPrice = findViewById(R.id.et_price);
            ivProductImage = findViewById(R.id.iv_product_image);
            llUploadHint = findViewById(R.id.ll_upload_hint);
            Button btnPublish = findViewById(R.id.btn_publish);

            // 点击图片区域选择图片
            findViewById(R.id.fl_image_container).setOnClickListener(v -> showImagePickerDialog());

            // 发布按钮
            if (btnPublish != null) {
                btnPublish.setOnClickListener(v -> publish());
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "页面加载失败");
            finish();
        }
    }

    /**
     * 显示图片选择对话框
     */
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

    /**
     * 从相册选择图片
     */
    private void pickImageFromGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "打开相册失败");
        }
    }

    /**
     * 使用相机拍照
     */
    private void takePhotoWithCamera() {
        // 检查相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }
        
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "打开相机失败");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == REQUEST_IMAGE_PICK) {
                    // 从相册选择
                    Uri imageUri = data.getData();
                    if (imageUri != null) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        setImagePreview(bitmap);
                    }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    // 相机拍照
                    if (data.getExtras() != null) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        if (bitmap != null) {
                            setImagePreview(bitmap);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.show(this, "图片加载失败");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotoWithCamera();
            } else {
                ToastUtil.show(this, "需要相机权限才能拍照");
            }
        }
    }

    /**
     * 设置图片预览
     */
    private void setImagePreview(Bitmap bitmap) {
        try {
            // 保存图片到本地
            String imagePath = ImageFileUtil.saveImageToStorage(this, bitmap);
            
            if (imagePath != null) {
                selectedImagePath = imagePath;
                
                // 显示预览
                ivProductImage.setImageBitmap(bitmap);
                ivProductImage.setVisibility(android.view.View.VISIBLE);
                llUploadHint.setVisibility(android.view.View.GONE);
                
                ToastUtil.show(this, "图片已选择");
            } else {
                ToastUtil.show(this, "图片保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "图片处理失败");
        }
    }

    /**
     * 发布商品
     */
    private void publish() {
        try {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            
            if (title.isEmpty() || content.isEmpty()) {
                ToastUtil.show(this, "标题和描述不能为空");
                return;
            }

            double price = 0;
            String priceStr = etPrice.getText().toString().trim();
            if (!priceStr.isEmpty()) {
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    ToastUtil.show(this, "价格格式不正确");
                    return;
                }
            }

            if (UserSession.getCurrentUser() == null) {
                ToastUtil.show(this, "请先登录");
                finish();
                return;
            }

            Goods goods = new Goods();
            goods.setUserId(UserSession.getCurrentUser().getUserId());
            goods.setTitle(title);
            goods.setContent(content);
            goods.setPrice(price);
            goods.setPublishType(0); // 固定为出售类型
            goods.setWantGoods(null);
            goods.setImgUrl(selectedImagePath); // 保存图片路径

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
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "发布失败：" + e.getMessage());
        }
    }

    @Override
    protected void initData() {}
}
