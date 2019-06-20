package nju.androidchat.client.hw1;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nju.androidchat.client.ClientMessage;

@AllArgsConstructor
public class Mvp0TalkImagePresenter implements Mvp0Contract.Presenter {

    private Mvp0Contract.Model mvp0TalkModel;
    private Mvp0Contract.View iMvp0TalkView;

    @Getter
    private List<ClientMessage> clientMessages;

    @Override
    public void sendMessage(String content) {
        ClientMessage clientMessage = mvp0TalkModel.sendInformation(content);
        refreshMessageList(clientMessage);
    }

    private void refreshMessageList(ClientMessage clientMessage) {
        clientMessages.add(clientMessage);
        iMvp0TalkView.showSingleImage(clientMessage);
    }

    @Override
    public void receiveMessage(ClientMessage content) {
        refreshMessageList(content);
    }

    @Override
    public String getUsername() {
        return mvp0TalkModel.getUsername();
    }

    @Override
    public void recallMessage(int index0) {

    }

    @Override
    public void start() {

    }
}


