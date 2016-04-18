package magshimim.newzbay;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

public class ErrorHandler {
    private PopupWindow noConnectionWithServer;
    private View popupWindowView_noConnectionWithServer;
    private String ConnectingClientMsg;
    private String lastMsgToServer;

    public ErrorHandler() {
        noConnectionWithServer = null;
    }

    public PopupWindow getNoConnectionWithServer() {
        return noConnectionWithServer;
    }

    public void setNoConnectionWithServer(PopupWindow noConnectionWithServer) {
        this.noConnectionWithServer = noConnectionWithServer;
    }

    public View getPopupWindowView_noConnectionWithServer() {
        return popupWindowView_noConnectionWithServer;
    }

    public void setPopupWindowView_noConnectionWithServer(View popupWindowView_noConnectionWithServer) {
        this.popupWindowView_noConnectionWithServer = popupWindowView_noConnectionWithServer;
    }

    public void handleNoConnectionToTheServer(final GlobalClass globalClass)
    {
        Button refreshConnection = (Button) globalClass.getErrorHandler().getPopupWindowView_noConnectionWithServer().findViewById(R.id.btn_reconnect);
        refreshConnection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (globalClass.getErrorHandler().getNoConnectionWithServer() != null) {
                    Communication communication = new Communication(globalClass);
                    globalClass.setCommunication(communication);
                    Thread t = new Thread(communication);
                    t.start();
                    globalClass.getErrorHandler().getNoConnectionWithServer().dismiss();
                }
            }
        });
    }

    public String getConnectingClientMsg() {
        return ConnectingClientMsg;
    }

    public void setConnectingClientMsg(String connectingClientMsg) {
        ConnectingClientMsg = connectingClientMsg;
    }

    public String getLastMsgToServer() {
        return lastMsgToServer;
    }

    public void setLastMsgToServer(String lastMsgToServer) {
        this.lastMsgToServer = lastMsgToServer;
    }
}
