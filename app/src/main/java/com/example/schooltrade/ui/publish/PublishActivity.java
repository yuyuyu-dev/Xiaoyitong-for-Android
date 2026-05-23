package com.example.schooltrade.ui.publish;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.schooltrade.R;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.Goods;
import com.example.schooltrade.api.GoodsRequest;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.utils.ImageUtil;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.io.IOException;
import retrofit2.Response;

public class PublishActivity extends BaseActivity {
    private static final String TAG = "PublishActivity";
    private static final String[] CATEGORY_NAMES = {
        "教材书籍", "数码产品", "生活用品", "服饰鞋包", "运动器材", "其他"
    };

    private EditText etTitle, etContent, etPrice;
    private LinearLayout llCategoryRow1, llCategoryRow2;
    private ImageView ivProductImage;
    private View flImageContainer;
    private int category = 0;
    private String selectedImagePath = null;
    private static final int REQUEST_IMAGE_PICK = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;
    private static final int REQUEST_IMAGE_CAPTURE = 1003;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
    }

    @Override
    protected void initView() {
        try {
            // 初始化基本控件
            etTitle = findViewById(R.id.et_title);
            etContent = findViewById(R.id.et_content);
            etPrice = findViewById(R.id.et_price);
            llCategoryRow1 = findViewById(R.id.ll_category_row1);
            llCategoryRow2 = findViewById(R.id.ll_category_row2);
            ivProductImage = findViewById(R.id.iv_product_image);
            flImageContainer = findViewById(R.id.fl_image_container);
            Button btnPublish = findViewById(R.id.btn_publish);

            // 检查必要控件
            if (etTitle == null || etContent == null || etPrice == null || btnPublish == null) {
                ToastUtil.show(this, "页面初始化失败");
                finish();
                return;
            }

            // 分类 Chip 按钮 — 分两行，每行 3 个，默认选中"其他"
            if (llCategoryRow1 != null && llCategoryRow2 != null) {
                initCategoryChips();
                updateCategoryChipSelection(6);
            }

            // 图片点击
            if (flImageContainer != null) {
                flImageContainer.setOnClickListener(v -> showImagePickerDialog());
            }

            // 发布按钮
            btnPublish.setOnClickListener(v -> publish());
            
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "页面加载失败");
            finish();
        }
    }

    // 分类 Chip 按钮 — 两行，每行 3 个
    private void initCategoryChips() {
        for (int i = 0; i < CATEGORY_NAMES.length; i++) {
            TextView chip = new TextView(this);
            chip.setText(CATEGORY_NAMES[i]);
            chip.setId(View.generateViewId());
            chip.setTextSize(13);
            chip.setPadding(20, 12, 20, 12);
            chip.setClickable(true);
            chip.setFocusable(true);
            chip.setBackgroundResource(R.drawable.bg_category_chip);
            chip.setTextColor(Color.parseColor("#666666"));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            if (i % 3 != 0) lp.setMargins(8, 0, 0, 0);

            final int idx = i + 1; // category = 1~6
            chip.setOnClickListener(v -> {
                category = idx;
                updateCategoryChipSelection(idx);
            });

            if (i < 3) llCategoryRow1.addView(chip);
            else llCategoryRow2.addView(chip);
        }
    }

    private void updateCategoryChipSelection(int selected) {
        updateRowSelection(llCategoryRow1, selected);
        updateRowSelection(llCategoryRow2, selected);
    }

    private void updateRowSelection(LinearLayout row, int selected) {
        for (int i = 0; i < row.getChildCount(); i++) {
            TextView chip = (TextView) row.getChildAt(i);
            int chipCategory = (row == llCategoryRow1 ? 0 : 3) + i + 1;
            boolean sel = (chipCategory == selected);
            chip.setSelected(sel);
            chip.setTextColor(sel ? Color.WHITE : Color.parseColor("#666666"));
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
        if (category == 0) {
            category = 6;
        }

        String priceStr = etPrice.getText().toString().trim();
        double price = 0;
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                ToastUtil.show(this, "价格格式不正确");
                return;
            }
        }

        // 有图片时先上传拿到 URL，再发布
        if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
            uploadImageThenPublish(title, content, price);
        } else {
            doPublish(title, content, price, "");
        }
    }

    private void uploadImageThenPublish(String title, String content, double price) {
        showLoading();
        new Thread(() -> {
            try {
                // Base64 → 临时文件 → Multipart 上传
                byte[] bytes = android.util.Base64.decode(selectedImagePath, android.util.Base64.DEFAULT);
                okhttp3.MediaType mediaType = okhttp3.MediaType.parse("image/jpeg");
                okhttp3.RequestBody fileBody = okhttp3.RequestBody.create(mediaType, bytes);
                okhttp3.MultipartBody.Part part = okhttp3.MultipartBody.Part.createFormData(
                    "file", "goods_" + System.currentTimeMillis() + ".jpg", fileBody);

                Response<Result<String>> uploadResp = RetrofitClient.getInstance()
                    .getApiService().uploadImage(part).execute();

                if (uploadResp.isSuccessful() && uploadResp.body() != null && uploadResp.body().isSuccess()) {
                    String imgUrl = uploadResp.body().getData();
                    doPublish(title, content, price, imgUrl != null ? imgUrl : "");
                } else {
                    // 上传失败，仍然发布（不带图）
                    runOnUiThread(() -> ToastUtil.show(PublishActivity.this, "图片上传失败，将发布纯文字商品"));
                    doPublish(title, content, price, "");
                }
            } catch (Exception e) {
                runOnUiThread(() -> ToastUtil.show(PublishActivity.this, "图片上传失败"));
                hideLoading();
            }
        }).start();
    }

    private void doPublish(String title, String content, double price, String imgUrl) {
        GoodsRequest goodsReq = new GoodsRequest();
        goodsReq.setTitle(title);
        goodsReq.setContent(content);
        goodsReq.setPrice(price);
        goodsReq.setPublishType(0);
        goodsReq.setImgUrl(imgUrl);
        goodsReq.setCategory(category);

        new Thread(() -> {
            try {
                Response<Result<Goods>> response = RetrofitClient.getInstance()
                    .getApiService().publishGoods(goodsReq).execute();
                runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        ToastUtil.show(PublishActivity.this, "发布成功");
                        finish();
                    } else if (response.body() != null) {
                        ToastUtil.show(PublishActivity.this, response.body().getMessage());
                    } else {
                        ToastUtil.show(PublishActivity.this, "发布失败: " + response.code());
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(PublishActivity.this, "网络连接失败");
                });
            }
        }).start();
    }

    @Override
    protected void initData() {}
}