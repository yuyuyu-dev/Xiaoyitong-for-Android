package com.example.schooltrade.ui.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.schooltrade.R;
import com.example.schooltrade.api.Result;
import com.example.schooltrade.api.RetrofitClient;
import com.example.schooltrade.base.BaseActivity;
import com.example.schooltrade.entity.User;
import com.example.schooltrade.utils.ToastUtil;
import com.example.schooltrade.utils.UserSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

public class EditProfileActivity extends BaseActivity {
    private ImageView ivAvatar;
    private EditText etAccount, etRealname, etPhone, etDormitory;
    private String selectedAvatarUrl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void initView() {
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        ivAvatar = findViewById(R.id.iv_avatar);
        etAccount = findViewById(R.id.et_account);
        etRealname = findViewById(R.id.et_realname);
        etPhone = findViewById(R.id.et_phone);
        etDormitory = findViewById(R.id.et_dormitory);
        Button btnSave = findViewById(R.id.btn_save);

        ivAvatar.setOnClickListener(v -> pickImage());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    @Override
    protected void initData() {
        new Thread(() -> {
            try {
                Response<Result<User>> response = RetrofitClient.getInstance()
                    .getApiService().getProfile().execute();
                runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        User user = response.body().getData();
                        etAccount.setText(user.getAccount());
                        etRealname.setText(user.getRealName());
                        etPhone.setText(user.getPhone());
                        etDormitory.setText(user.getDormitory());
                        selectedAvatarUrl = user.getAvatarUrl();
                        loadAvatar(user.getAvatarUrl());
                    } else {
                        ToastUtil.show(EditProfileActivity.this, "加载用户信息失败");
                        finish();
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    ToastUtil.show(EditProfileActivity.this, "网络连接失败");
                    finish();
                });
            }
        }).start();
    }

    private void loadAvatar(String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(this).load(RetrofitClient.fullUrl(url))
                .placeholder(R.drawable.bg_image_placeholder)
                .error(R.drawable.bg_image_placeholder)
                .circleCrop()
                .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.bg_image_placeholder);
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                if (uri != null) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ivAvatar.setImageBitmap(bitmap);
                    // 先保存到本地再上传
                    String path = com.example.schooltrade.utils.ImageFileUtil
                        .saveImageToStorage(this, bitmap);
                    if (path != null) {
                        uploadAvatar(path);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.show(this, "图片加载失败");
            }
        }
    }

    private void uploadAvatar(String filePath) {
        // 检查文件是否存在
        java.io.File imageFile = new java.io.File(filePath);
        if (!imageFile.exists()) {
            ToastUtil.show(EditProfileActivity.this, "图片文件不存在");
            return;
        }

        showLoading();
        new Thread(() -> {
            try {
                RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/jpeg"), imageFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "file", imageFile.getName(), requestFile);
                Response<Result<String>> response = RetrofitClient.getInstance()
                    .getApiService().uploadImage(body).execute();
                runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        selectedAvatarUrl = RetrofitClient.fullUrl(response.body().getData());
                        ToastUtil.show(EditProfileActivity.this, "头像上传成功");
                        loadAvatar(selectedAvatarUrl);
                    } else {
                        String err = (response.body() != null)
                            ? response.body().getMessage() : "上传失败";
                        ToastUtil.show(EditProfileActivity.this, err);
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(EditProfileActivity.this, "网络连接失败: " + e.getMessage());
                });
            }
        }).start();
    }

    private void saveProfile() {
        String realName = etRealname.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String dormitory = etDormitory.getText().toString().trim();

        Map<String, String> body = new HashMap<>();
        body.put("realName", realName);
        body.put("phone", phone);
        body.put("dormitory", dormitory);
        if (selectedAvatarUrl != null && !selectedAvatarUrl.isEmpty()) {
            body.put("avatarUrl", selectedAvatarUrl);
        }

        showLoading();
        new Thread(() -> {
            try {
                Response<Result<User>> response = RetrofitClient.getInstance()
                    .getApiService().updateProfile(body).execute();
                runOnUiThread(() -> {
                    hideLoading();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().isSuccess()) {
                        User updatedUser = response.body().getData();
                        UserSession.setCurrentUser(updatedUser);
                        String token = UserSession.getToken();
                        if (token != null && !token.isEmpty()) {
                            UserSession.saveLogin(updatedUser, token);
                        }
                        ToastUtil.show(EditProfileActivity.this, "保存成功");
                        finish();
                    } else {
                        String msg = response.body() != null
                            ? response.body().getMessage() : "保存失败";
                        ToastUtil.show(EditProfileActivity.this, msg);
                    }
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    hideLoading();
                    ToastUtil.show(EditProfileActivity.this, "网络连接失败");
                });
            }
        }).start();
    }
}
