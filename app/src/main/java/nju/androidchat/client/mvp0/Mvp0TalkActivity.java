package nju.androidchat.client.mvp0;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemImageReceive;
import nju.androidchat.client.component.ItemImageSend;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.component.OnRecallMessageRequested;

@Log
public class Mvp0TalkActivity extends AppCompatActivity implements Mvp0Contract.View, TextView.OnEditorActionListener, OnRecallMessageRequested {
    private Mvp0Contract.Presenter imagePresenter;
    private Mvp0Contract.Presenter textPresenter;
    //private  Mvp0TalkModel mvp0TalkModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mvp0TalkModel mvp0TalkModel = new Mvp0TalkModel();

        // Create the presenter
        //this.presenter = new Mvp0TalkPresenter(mvp0TalkModel, this, new ArrayList<>());
        //this.presenter = new Mvp0TalkTextPresenter(mvp0TalkModel, this, new ArrayList<>());
        this.imagePresenter = new Mvp0TalkImagePresenter(mvp0TalkModel, this, new ArrayList<>());
        this.textPresenter = new Mvp0TalkTextPresenter(mvp0TalkModel, this, new ArrayList<>());

        mvp0TalkModel.setIMvp0TalkTextPresenter(this.textPresenter);
        mvp0TalkModel.setIMvp0TalkImagePresenter(this.imagePresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        imagePresenter.start();
        textPresenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);

                    // 删除所有已有的ItemText
                    content.removeAllViews();

                    // 增加ItemText
                    for (ClientMessage message : messages) {
                        String text = String.format("%s", message.getMessage());
                        // 如果是自己发的，增加ItemTextSend
                        if (message.getSenderUsername().equals(this.imagePresenter.getUsername()) || message.getSenderUsername().equals(this.textPresenter.getUsername()) ) {
                            content.addView(new ItemTextSend(this, text, message.getMessageId(), this));
                        } else {
                            content.addView(new ItemTextReceive(this, text, message.getMessageId()));
                        }
                    }

                    Utils.scrollListToBottom(this);
                }
        );
    }

    @Override
    public void showSingleText(ClientMessage message) {
        runOnUiThread(() -> {
            LinearLayout content = findViewById(R.id.chat_content);

            String text = String.format("%s", message.getMessage());
            // 如果是自己发的，增加ItemTextSend
            if (message.getSenderUsername().equals(this.imagePresenter.getUsername()) || message.getSenderUsername().equals(this.textPresenter.getUsername())) {
                content.addView(new ItemTextSend(this, text, message.getMessageId(), this));
            } else {
                content.addView(new ItemTextReceive(this, text, message.getMessageId()));
            }
        });
    }

    @Override
    public void showSingleImage(ClientMessage message) {
        runOnUiThread(() -> {
            LinearLayout content = findViewById(R.id.chat_content);

            String url = String.format("%s", message.getMessage());
            // 如果是自己发的，增加ItemTextSend
            if (message.getSenderUsername().equals(this.imagePresenter.getUsername()) || message.getSenderUsername().equals(this.textPresenter.getUsername())) {
                log.info("send");
                content.addView(new ItemImageSend(this, url, message.getMessageId()));
            } else {
                log.info("receive");
                content.addView(new ItemImageReceive(this, url, message.getMessageId()));
            }
        });
    }


    @Override
    public void setPresenter(Mvp0Contract.Presenter presenter) {
        this.imagePresenter = presenter;
        this.textPresenter = presenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        String content = text.getText().toString();
        if(content.length() > 5 && content.charAt(0) == '!' && content.charAt(1) == '(' && content.charAt(2) == ')' && content.charAt(3) == '[' && content.charAt(content.length() - 1) == ']') {

            AsyncTask.execute(() -> {
                this.imagePresenter.sendMessage(content.substring(4, content.length()-1));
            });
        }else {
            AsyncTask.execute(() -> {
                this.textPresenter.sendMessage(content);
            });
        }
        /*AsyncTask.execute(() -> {
            this.presenter.sendMessage(content);
        });*/
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么，MVP-0不实现
    @Override
    public void onRecallMessageRequested(UUID messageId) {

    }
}
