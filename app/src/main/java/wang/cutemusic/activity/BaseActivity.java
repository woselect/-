package wang.cutemusic.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wang.cutemusic.R;
import wang.cutemusic.data.MusicInfo;
import wang.cutemusic.service.MusicService;
import wang.cutemusic.util.MusicUtil;

public abstract class BaseActivity extends FragmentActivity {

    public MusicService service ;
    public boolean isbind=false ;
    static String TAG="BaseActivity";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }




    private ServiceConnection binConn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //服务连接
            Log.d("Bound", "onBind: 服务连接");
            MusicService.MusicBinder binder= (MusicService.MusicBinder) iBinder;
            service =binder.getService();
            service.setOnServiceChangedListener(musicListener);
            musicListener.onMucisChanged(service.getcurPosition());
            Log.v(TAG, "得到的service是空？"+String.valueOf(service==null));
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //服务断开
            Log.d("Bound", "onBind: 服务因故断开");
            service  = null;
            isbind=false  ;
        }
    };

    //绑定服务
    public void bindService() {
        Log.d(TAG, "bindBoundService: 准备绑定0");
        if (!isbind){
            Intent intent = new Intent(this,MusicService.class);
            Log.d(TAG, "bindBoundService: 绑定中1");
            bindService(intent,binConn , BIND_AUTO_CREATE);
            Log.d(TAG, "bindBoundService: 绑定完成2");
            isbind=true ;
        }


    }

    //解绑服务
    public void unbindService() {
        Log.d(TAG, "unbindService: 准备解绑");
        /*if(service  !=null){
            service  = null;
        }*/
        if (isbind){
            unbindService(binConn);
            isbind=false  ;
        }
    }






    public void startmusic(int id) {
        service.play(id);
    }

    public void pausemusic() {
        service.pause();
    }

    public void prevmusic() {
        service.prev();
    }


    public void nextmusic() {
        service.next();
    }


    private MusicService.OnServiceMusicListener musicListener=new MusicService.OnServiceMusicListener() {

        //进度条
        @Override
        public void onMucisPublish(int progress) {
            Publish(progress);
        }

        //音乐改变
        @Override
        public void onMucisChanged(int musicid) {
            Changed(musicid);
        }
        //暂停
        @Override
        public void onMucispauseed(int musicid) {pauseed( musicid); }
    };



    public abstract  void Publish(int progress);
    public abstract  void Changed(int musicid);
    public abstract  void pauseed(int musicid);
}
