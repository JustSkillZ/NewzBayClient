package magshimim.newzbay;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.squareup.picasso.Picasso;

public class ErrorHandler {
    private PopupWindow noConnectionWithServer;
    private View popupWindowView_noConnectionWithServer;
    private String ConnectingClientMsg;
    private String lastMsgToServer;
    private GlobalClass globalClass;

    public ErrorHandler(GlobalClass globalClass) {
        noConnectionWithServer = null;
        this.globalClass = globalClass;
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

    public void handleNoConnectionToTheServer()
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

    public void reConnect()
    {
        ((Activity)globalClass.getCurrentActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final PopupWindow noConnection = new PopupWindow(
                        globalClass.getErrorHandler().getPopupWindowView_noConnectionWithServer(),
                        800,
                        800,
                        true);
                noConnection.setAnimationStyle(R.style.AnimationFade);
                noConnection.showAtLocation(((Activity) globalClass.getCurrentActivity()).findViewById(globalClass.getCurrentLayout()), Gravity.CENTER, 0, 0);
                globalClass.getErrorHandler().setNoConnectionWithServer(noConnection);
            }
        });
    }
}
