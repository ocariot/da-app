package br.edu.uepb.nutes.ocariot.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.tapadoo.alerter.Alerter;

import br.edu.uepb.nutes.ocariot.R;
import retrofit2.HttpException;

public class AlertMessage {
    private Context mContext;

    public AlertMessage(Context context) {
        this.mContext = context;
    }

    public void show(String title, String text, int backgroundColor, int icon, int duration,
                     boolean enableVibration, AlertMessageListener listener) {
        Alerter alerter = Alerter.create((Activity) mContext)
                .setDuration(duration)
                .enableVibration(enableVibration)
                .enableSwipeToDismiss()
                .setTitle(title)
                .setText(text)
                .setBackgroundColorRes(backgroundColor)
                .setIcon(icon);

        if (listener != null) {
            alerter.setOnClickListener(v -> listener.onClickListener())
                    .setOnHideListener(listener::onHideListener);
        }
        alerter.show();
    }

    public void show(int title, int text, int backgroundColor, int icon, int duration,
                     boolean enableVibration, AlertMessageListener listener) {
        show(mContext.getResources().getString(title), mContext.getResources().getString(text),
                backgroundColor, icon, duration, enableVibration, listener);
    }

    public void show(int title, int text, int backgroundColor, int icon) {
        show(mContext.getResources().getString(title), mContext.getResources().getString(text),
                backgroundColor, icon, 15000, false, null);
    }

    public void handleError(Throwable error) {
        if (error instanceof HttpException) {
            HttpException httpEx = ((HttpException) error);
            switch (httpEx.code()) {
                case 400:
                    showError(R.string.title_error, R.string.error_400);
                    return;
                case 403:
                    showError(R.string.title_error, R.string.error_403);
                    return;
                case 500:
                    showError(R.string.title_error, R.string.error_500);
                    return;
                default:
                    return;
            }
        }
        showError(R.string.title_connection_error, R.string.error_connection);
    }

    private void showError(int title, int text) {
        show(title, text, R.color.colorDanger, R.drawable.ic_warning_dark);
    }

    public interface AlertMessageListener {
        void onHideListener();

        void onClickListener();
    }
}
