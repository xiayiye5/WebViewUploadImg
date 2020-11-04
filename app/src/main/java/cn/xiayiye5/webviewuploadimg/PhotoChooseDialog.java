package cn.xiayiye5.webviewuploadimg;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


/**
 * 选择相机图片的弹框
 */
public class PhotoChooseDialog extends Dialog implements View.OnClickListener {

    private OnClickListener onClickListener;

    private TextView mCameraTxt;
    private TextView mPhotoTxt;
    private TextView mCancelTxt;
    private Context mContext;


    public PhotoChooseDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_choose_dialog);
        Window dialogWindow = this.getWindow();
        //设置dialog的显示位置在屏幕底部
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        //设置dialog的宽度为当前手机屏幕的宽度
        p.width = d.getWidth();
        dialogWindow.setAttributes(p);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);

        // false：按空白处不能关闭弹窗，true：按空白处可以关闭弹窗
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        //初始化界面控件
        initView();

        //初始化界面控件的事件
        initListener();
    }

    private void initListener() {

        mCameraTxt.setOnClickListener(this);
        mPhotoTxt.setOnClickListener(this);
        mCancelTxt.setOnClickListener(this);

    }


    private void initView() {

        mCameraTxt = findViewById(R.id.txt_camera);
        mPhotoTxt = findViewById(R.id.txt_photo);
        mCancelTxt = findViewById(R.id.txt_cancel);

    }

    public void setOnClickListener(OnClickListener onClickListener) {

        this.onClickListener = onClickListener;
    }


    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v.getId());

        }
    }

    /**
     * 设置被点击的接口
     */
    public interface OnClickListener {
        void onClick(int id);
    }


}
