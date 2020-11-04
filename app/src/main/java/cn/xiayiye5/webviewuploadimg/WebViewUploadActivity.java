package cn.xiayiye5.webviewuploadimg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * @author 上传图片页面
 */
public class WebViewUploadActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 表单的数据信息
     */
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private static final int CAMERA_RESULT_CODE = 121;
    private static final int PHOTO_CHOOSER_RESULT_CODE = 122;
    private String imagePaths;
    private Uri cameraUri;
    private String[] cameraAndStorage = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private int openType = 0;
    private WebView wb;
    private TextView tvWebViewTitle;
    private ProgressBar pbAd;
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        tvWebViewTitle = findViewById(R.id.tvWebViewTitle);
        pbAd = findViewById(R.id.pb_ad);
        tvWebViewTitle.setText("上传图片");
        wb = findViewById(R.id.wb);
        //设置可缩放
        wb.getSettings().setSupportZoom(true);
        wb.getSettings().setBuiltInZoomControls(true);
        //设置WebView自适应屏幕
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setLoadWithOverviewMode(true);
        //加载网页
        wb.setWebViewClient(new MyWebViewClient());
        wb.setWebChromeClient(new MyWebChromeClient());
        //隐藏缩放按钮
        wb.getSettings().setDisplayZoomControls(false);


        WebSettings settings = wb.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        // 是否可访问Content Provider的资源，默认值 true
        settings.setAllowContentAccess(true);
        // 是否可访问本地文件，默认值 true
        settings.setAllowFileAccess(true);
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        settings.setAllowFileAccessFromFileURLs(false);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);

        //设置http和https混合加载
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            wb.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }


        wb.addJavascriptInterface(this, "GfanSdk");
        tvWebViewTitle.setOnClickListener(this);
        wb.loadUrl("file:///android_asset/upload_image.html");
    }

    @Override
    public void onClick(View view) {

    }
    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                pbAd.setVisibility(View.GONE);
            } else {
                if (pbAd.getVisibility() == View.GONE) {
                    pbAd.setVisibility(View.VISIBLE);
                }
                pbAd.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            uploadMessage = uploadMsg;
            openImageChooserActivity();
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            uploadMessage = uploadMsg;
            openImageChooserActivity();
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            uploadMessage = uploadMsg;
            openImageChooserActivity();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    }
    /**
     * 防止点击dialog的取消按钮之后，未选择图片返回后，就不再次响应点击事件了
     */
    public void cancelCallback() {
        if (uploadMessageAboveL != null) {
            uploadMessageAboveL.onReceiveValue(null);
            uploadMessageAboveL = null;
        }
        if (uploadMessage != null) {
            uploadMessage.onReceiveValue(null);
            uploadMessage = null;
        }
    }
    private void openImageChooserActivity() {
        final PhotoChooseDialog photoChooseDialog = new PhotoChooseDialog(this);
        photoChooseDialog.show();
        photoChooseDialog.setOnClickListener(new PhotoChooseDialog.OnClickListener() {
            @Override
            public void onClick(int id) {
                if (id == R.id.txt_cancel) {
                    cancelCallback();
                } else if (id == R.id.txt_camera) {
                    openCapture();
                    /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        openCapture();
                        return;
                    }*/
                  /*  if (checkPermissions(1)) {
                        openCapture();
                    } else {
                        requestPermission();
                    }*/
                } else if (id == R.id.txt_photo) {
                    openPick();
                   /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        openPick();
                        return;
                    }*/
                   /* if (checkPermissions(2)) {
                        openPick();
                    } else {
                        requestPermission();
                    }*/
                }
                photoChooseDialog.dismiss();
            }
        });
    }
    /**
     * 打开相册
     */
    private void openPick() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_CHOOSER_RESULT_CODE);
    }

    /**
     * 打开照相机拍照
     */
    private void openCapture() {
        try {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takeIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            imagePaths = Environment.getExternalStorageDirectory().getAbsolutePath() + "/XiaYiYe5_SDK/xiayiye5_" + System.currentTimeMillis() + ".jpg";
            // 必须确保文件夹路径存在，否则拍照后无法完成回调
            File vFile = new File(imagePaths);
            if (!vFile.exists()) {
                File vDirPath = vFile.getParentFile();
                vDirPath.mkdirs();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cameraUri = FileProvider.getUriForFile(this, getPackageName() + ".xiayiye5", vFile);
            } else {
                cameraUri = Uri.fromFile(vFile);
            }
            Log.e("打印相机路径", cameraUri.toString());
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            startActivityForResult(takeIntent, CAMERA_RESULT_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RESULT_CODE) {
            Uri uri = null;
            File file = new File(imagePaths);
            if (!file.exists()) {
                cameraUri = Uri.parse("");
            }
            Log.e("打印图片地址", imagePaths);
            ImageUtils.afterOpenCamera(imagePaths, this);
            uri = cameraUri;
            if (uploadMessageAboveL != null) {
                Uri[] uris = new Uri[1];
                uris[0] = uri;
                uploadMessageAboveL.onReceiveValue(uris);
                uploadMessageAboveL = null;
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(uri);
                uploadMessage = null;
            }
        } else if (requestCode == PHOTO_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) {
                return;
            }
            Uri result = data == null ? null : data.getData();

            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(resultCode, data);
            } else if (uploadMessage != null) {
                result = ImageUtils.getUri(data, this);
                if (result == null) {
                    return;
                }
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        } else if (resultCode == RESULT_CANCELED) {
            cancelCallback();
        }
    }

    private void onActivityResultAboveL(int resultCode, Intent intent) {
        if (uploadMessageAboveL != null) {
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    String dataString = intent.getDataString();
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            uploadMessageAboveL.onReceiveValue(results);
            uploadMessageAboveL = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (wb.canGoBack()) {
            wb.goBack();
            return;
        }
        super.onBackPressed();
    }
}
