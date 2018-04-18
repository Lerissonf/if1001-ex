package br.ufpe.cin.if1001.rss.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import br.ufpe.cin.if1001.rss.ui.MainActivity;

/**
 * Created by LERISSON on 18/04/2018.
 */

public class NewReportReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Notificacao quando alguma noticia nova aparecer no feed e o user nao estiver com o app em primeiro plano
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());
        builder
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("RSS Feed App")
                .setContentText("Não fique desatualizado das notícias do momento.");
        Intent openApp = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, openApp, 0);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        NotificationManagerCompat.from(context.getApplicationContext()).notify(0, notification);
    }
}
