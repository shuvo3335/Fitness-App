

package org.dynamicsoft.caloriescope.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

public class BackgroundService extends Service { //This file is a placeholder and will be worked upon in the future
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //left empty to let sensormanager do it's work
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
