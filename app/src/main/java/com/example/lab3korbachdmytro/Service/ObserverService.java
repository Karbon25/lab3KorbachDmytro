package com.example.lab3korbachdmytro.Service;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.example.lab3korbachdmytro.ShowResult.GameResult;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObserverService extends Service {

    private final IBinder binder = new LocalBinder();
    private List<GameResult> listResult;
    private List<IConnectorObserver> listConnectionObserver;

    public ObserverService() {
        listResult = new CopyOnWriteArrayList<>();
        listConnectionObserver = new ArrayList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        ServiceReadWrite.startActionRead(this);
        new ReadDataThread().start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        for(IConnectorObserver client :listConnectionObserver){
            unregisterClientObserver(client);
        }
        super.onDestroy();
    }

    public void regiserClientObserver(IConnectorObserver client) {
        if(Looper.myLooper() == Looper.getMainLooper()){
            Log.e("myTag", " regiserClientObserver MainUI");
        }else{
            Log.e("MyTag", " regiserClientObserver Thread");
        }
        listConnectionObserver.add(client);
        client.onConnectObserver(new ArrayList<>(listResult));
    }

    public void unregisterClientObserver(IConnectorObserver client) {
        listConnectionObserver.remove(client);
        client.onDisconnectObserver();
    }

    public void notificationClientEvent(GameResult result) {
        listResult.add(0, result);
        notifyClients();
        new WriteDataThread().start();
    }

    public void setListResult(ArrayList<GameResult> listResult) {
        if(Looper.myLooper() == Looper.getMainLooper()){
            Log.e("myTag", " setListResult MainUI");
        }else{
            Log.e("MyTag", " setListResult Thread");
        }
        this.listResult = new CopyOnWriteArrayList<>(listResult);
        new WriteDataThread().start();
    }

    private void notifyClients() {
        for (IConnectorObserver client : listConnectionObserver) {
            client.updateEventObserver(listResult.get(0));
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public ObserverService getService() {
            return ObserverService.this;
        }
    }

    private class ReadDataThread extends Thread{
        Handler handler;
        public ReadDataThread(){
            handler = new Handler(Looper.myLooper());
        }
        @Override
        public void run() {
            if(Looper.myLooper() == Looper.getMainLooper()){
                Log.e("myTag", "MainUI");
            }else{
                Log.e("MyTag", "Thread");
            }

            List<GameResult> resultsRead;
            try (FileInputStream fis = openFileInput("database1.dat");
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                resultsRead = (List<GameResult>) ois.readObject();
            } catch (Exception e) {
                resultsRead = new ArrayList<>();
            }
            final List<GameResult> result = resultsRead;
            handler.post(new Thread() {
                @Override
                public void run() {
                    setListResult(new ArrayList<>(result));
                }
            });

        }
    }
    private class WriteDataThread extends Thread{
        @Override
        public void run() {
            try (FileOutputStream fos = openFileOutput("database1.dat", Context.MODE_PRIVATE);
                 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                 oos.writeObject(listResult);
            } catch (IOException e) {
                Log.e(TAG, "Error writing to file", e);
            }
        }
    }

}