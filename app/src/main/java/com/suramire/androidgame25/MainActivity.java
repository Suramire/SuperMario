package com.suramire.androidgame25;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.suramire.androidgame25.util.L;

public class MainActivity extends Activity {

    private MyView2 view;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        view = new MyView2(this);
        setContentView(view);

	}

	@Override
	public void onBackPressed() {
        //响应返回按钮事件
        view.setPause(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示")
                .setMessage("是否退出游戏")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	setContentView(new View(MainActivity.this));
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        view.setPause(false);
                    }
                })
                .setCancelable(false)
                .show();
//		super.onBackPressed();
	}
}
