package com.avos.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

public class CreateTodo extends Activity {

  private EditText nameText;
  private EditText priceNumber;
  private EditText contentText;
  private String objectId;
  private AVImageView imgView;



  @Override
  protected void onPause() {
    super.onPause();
    // 页面统计，结束
    AVAnalytics.onPause(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    // 页面统计，开始
    AVAnalytics.onResume(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.create_todo);
    //setTitle(R.string.create_todo);

    setTitle("公布打折消息");


    contentText = (EditText) findViewById(R.id.content);
    nameText = (EditText) findViewById(R.id.name);
    priceNumber = (EditText) findViewById(R.id.price);

    Intent intent = getIntent();
    // 通过搜索结果打开
    if (intent.getAction() == Intent.ACTION_VIEW) {
      // 如果是VIEW action，我们通过getData获取URI
      Uri uri = intent.getData();
      String path = uri.getPath();
      int index = path.lastIndexOf("/");
      if (index > 0) {
        // 获取objectId
        objectId = path.substring(index + 1);
        GetCallback<AVObject> getCallback=new GetCallback<AVObject>() {
          @Override
          public void done(AVObject todo, AVException arg1) {
            if (todo != null) {
              String name = todo.getString("name");
              String price = todo.getString("price");
              String content = todo.getString("content");

              if (content != null) {
                contentText.setText(content);
                priceNumber.setText(price);
                nameText.setText(name);

              }
            }
          }
        };
        AVService.fetchTodoById(objectId, getCallback);
      }
    } else {
      // 通过ListView点击打开
      Bundle extras = getIntent().getExtras();
      if (extras != null) {
        String name = extras.getString("name");
        String price = extras.getString("price");
        String content = extras.getString("content");
        objectId = extras.getString("objectId");

        //Todo product = new Todo();
        //product.setObjectId(objectId);
        // 通过Fetch获取content内容
        //product.fetchIfNeededInBackground();

        //AVFile file = product.getImg();
        //imgView.setAVFile(file);
        //imgView.loadInBackground();


        if (content != null) {
          contentText.setText(content);
          priceNumber.setText(price);
          nameText.setText(name);
        }
      }
    }

    Button confirmButton = (Button) findViewById(R.id.confirm);
    confirmButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        SaveCallback saveCallback=new SaveCallback() {
          @Override
          public void done(AVException e) {
            // done方法一定在UI线程执行
            if (e != null) {
              Log.e("CreateItem", "Update Item failed.", e);
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean("success", e == null);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
          }
        };
        String content = contentText.getText().toString();
        String name = nameText.getText().toString();
        String price = priceNumber.getText().toString();

        AVService.createOrUpdateTodo(objectId, name, price, content, saveCallback);
      }
    });
  }

}
