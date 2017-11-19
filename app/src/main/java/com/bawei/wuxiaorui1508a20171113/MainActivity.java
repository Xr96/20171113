package com.bawei.wuxiaorui1508a20171113;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bawei.wuxiaorui1508a20171113.adapter.ListAdapter;
import com.bawei.wuxiaorui1508a20171113.fu.DownLoadManager;
import com.bawei.wuxiaorui1508a20171113.fu.DownLoadService;
import com.bawei.wuxiaorui1508a20171113.fu.TaskInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.center)
    TextView center;
    @BindView(R.id.button)
    Button addbutton;
    @BindView(R.id.userbutton)
    Button userbutton;
    @BindView(R.id.listView)
    ListView listView;
    private ListAdapter adapter;
    /*使用DownLoadManager时只能通过DownLoadService.getDownLoadManager()的方式来获取下载管理器，不能通过new DownLoadManager()的方式创建下载管理器*/
    private DownLoadManager manager;
    private EditText nameText;
    private EditText urlText;
    /*
    * 在项目中要用到ButterKnife:先导依赖包与配置
    * 导入断点续传的依赖包与配置
    *
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //下载管理器需要启动一个Service,在刚启动应用的时候需要等Service启动起来后才能获取下载管理器，所以稍微延时获取下载管理器
        handler.sendEmptyMessageDelayed(1, 50);
        /*初始化service服务*/
        this.startService(new Intent(this, DownLoadService.class));
    }


    @OnClick({R.id.button, R.id.userbutton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:

                View showview = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
                nameText = (EditText) showview.findViewById(R.id.file_name);
                urlText = (EditText) showview.findViewById(R.id.file_url);
                new AlertDialog.Builder(MainActivity.this).setView(showview).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ("".equals(nameText.getText().toString()) || "".equals(urlText.getText().toString())) {
                            Toast.makeText(MainActivity.this, "请输入文件名和下载路径", Toast.LENGTH_SHORT).show();
                        } else {
                            TaskInfo info = new TaskInfo();
                            info.setFileName(nameText.getText().toString());
                            /*服务器一般会有个区分不同文件的唯一ID，用以处理文件重名的情况*/
                            info.setTaskID(nameText.getText().toString());
                            info.setOnDownloading(true);
                            /*将任务添加到下载队列，下载器会自动开始下载*/
                            manager.addTask(nameText.getText().toString(), urlText.getText().toString(), nameText.getText().toString());
                            adapter.addItem(info);
                        }
                    }
                }).setNegativeButton("取消", null).show();
                break;
            case R.id.userbutton:
                new AlertDialog.Builder(MainActivity.this).setTitle("切换用户")
                        .setPositiveButton("fee", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.changeUser("fee");
                                userbutton.setText("用户: fee");
                                adapter.setListdata(manager.getAllTask());

                            }
                        }).setNegativeButton("fe", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        manager.changeUser("fe");
                        userbutton.setText("用户 : fe");
                        adapter.setListdata(manager.getAllTask());
                    }
                }).show();
                break;
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //*获取下载管理器*//*
            manager = DownLoadService.getDownLoadManager();
            //*设置用户ID，客户端切换用户时可以显示相应用户的下载任务*//*
            manager.changeUser("fe");
            //*断点续传需要服务器的支持，设置该项时要先确保服务器支持断点续传功能*//*
            manager.setSupportBreakpoint(true);
            adapter = new ListAdapter(MainActivity.this,manager);
            listView.setAdapter(adapter);
            userbutton.setText("用户 : " + manager.getUserID());
        }
    };
}
