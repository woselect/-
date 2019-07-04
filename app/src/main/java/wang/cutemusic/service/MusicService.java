package wang.cutemusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.LocaleList;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wang.cutemusic.HPMusicApp;
import wang.cutemusic.data.MusicInfo;
import wang.cutemusic.data.PlayMusicInfo;
import wang.cutemusic.util.MusicUtil;

public class MusicService extends Service implements MediaPlayer.OnErrorListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{

    final String TAG="MUSI服务里";
    public MediaPlayer player;
    private boolean hasPrepared;
    int playPosation;
    Random random;

    private OnServiceMusicListener listener;

    public static final int MY_MUSIC_LIST = 1;
    public static final int LIKE_MUSIC_LIST = 2;
    public static final int ONLINE_MUSIC_LIST = 3;
    private int PlayList = 0;

    public int getPlayList() {
        return PlayList;
    }

    public void setPlayList(int playList) {
        PlayList = playList;
    }

    public static final int PLAY_ORDER=10;
    public static final int PLAY_RANDOM=11;
    public static final int PLAY_REPEAT=12;
    public   int PLAY_STYlE;
    public   boolean is_Playing=false;

    public boolean isIs_Playing() {
        if (player != null) {
            Log.d(TAG, "isIs_Playing: 准备返回播放的状态是："+player.isPlaying());
            return is_Playing;
        }
        return false;

    }

    public void setIs_Playing(boolean is_Playing) {
        this.is_Playing = is_Playing;
    }

    public int getPLAY_STYlE() {
        return PLAY_STYlE;
    }

    public void setPLAY_STYlE(int PLAY_STYlE) {
        this.PLAY_STYlE = PLAY_STYlE;
    }

    public static final int Mess_START=0;
    public static final int Mess_STOP=1;
    public static final int Mess_NEXT=2;
    public static final int Mess_SAHNG=3;
    public static final int Mess_PAUSE=4;
    public   int STATUS=33;

    private List<PlayMusicInfo> musicInfo;
    private int id;

    public List<PlayMusicInfo> getMusicInfo() {/*
        if (musicInfo==null || musicInfo.size()==0){
            Log.d(TAG, "getMusicInfo: 重新加载音乐列表");
            List<MusicInfo> info=MusicUtil.getMp3Infos(this);
            for (MusicInfo ms : info) {
                PlayMusicInfo play=new PlayMusicInfo();
                play.setAlbum(ms.getAlbum());
                play.setAlbumId(ms.getAlbumId());
                play.setArtist(ms.getArtist());
                play.setDuration(ms.getDuration());
                play.setId(ms.getId());
                play.setIsMusic(ms.getIsMusic());
                play.setMusicid(ms.getMusicid());
                play.setSize(ms.getSize());
                play.setTitle(ms.getTitle());
                play.setType(ms.getType());
                play.setUrl(ms.getUrl());
                musicInfo.add(play);
            }
        }*/
        return musicInfo;
    }

    public void setMusicInfo(ArrayList<PlayMusicInfo> musicInfo) {
        this.musicInfo = musicInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public class MusicBinder extends Binder{
        public MusicService getService(){
            Log.d(TAG, "getService: 准备返回service实例");
            return MusicService.this;
        }
    }



    public interface OnServiceMusicListener {
        void onMucisPublish(int progress);
        void onMucisChanged(int musicid);
        void onMucispauseed(int musicid);
    }

    public void setOnServiceChangedListener(OnServiceMusicListener listener) {
        this.listener = listener;
    }

    //线程池
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    Runnable updateStatusRunnable=new Runnable() {
        @Override
        public void run() {
            while(true ){
                if(listener !=null && player  !=null && player.isPlaying()){
                    listener.onMucisPublish(getCurrentProgress());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    /**********以下是生命周期**************/

    //绑定
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: 开始绑定");
        return new MusicBinder();
    }

    /*//解绑
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: 开始解绑");
        return super.onUnbind(intent);
    }*/

    //创建
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate:开始创建  ");
        HPMusicApp app = (HPMusicApp) getApplication();
        playPosation=app.sharedPreferences.getInt("mposation", 0);
        PLAY_STYlE=app.sharedPreferences.getInt("mplaystyle", PLAY_ORDER);
//        PlayList=app.sharedPreferences.getInt("playlist", MY_MUSIC_LIST);
        if (PlayList ==LIKE_MUSIC_LIST){

        }else{

        }

        musicInfo=new ArrayList<>() ;
        if (player == null){
            player=new MediaPlayer();
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
        }
        executorService.execute(updateStatusRunnable);
         random=new Random();
        super.onCreate();
    }


    //启动方式的运行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:  开始启动 ");
        return super.onStartCommand(intent, flags, startId);
    }

    //销毁
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:  开始销毁");
        super.onDestroy();
        if(executorService !=null && !executorService.isShutdown()&&executorService.isTerminated()){
            executorService.shutdown();
            executorService=null;
        }
        release();

    }


    //播放进度
    public int getCurrentProgress(){
        if (player !=null ){//这里因为进度条的事情没有判断是否在播放状态了，不知道后面会不会出错
            return player.getCurrentPosition();
        }
        return 0;
    }
    public int getcurPosition(){
        if (player!=null){
            return playPosation;
        }
        return 0;
    }

    //准备播放
    public void play(int id) {
        if (id <0 || id >=musicInfo.size()){
            id=0;
        }


        hasPrepared = false; // 开始播放前Flag置为不可操作

        String path =musicInfo.get(id).getUrl();
        Log.d(TAG, "play: 播放的ID："+id);
        try {
            player.reset();
            player.setDataSource(this,Uri.parse(path));
            player.setOnPreparedListener(this);
            player.prepareAsync();//异步准备
            playPosation=id;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void start() {
        // release()会释放player、将player置空，所以这里需要判断一下
        if (null != player && hasPrepared) {
            is_Playing=true;
            player.start();
            listener.onMucisChanged(playPosation);
            Log.d(TAG, "start: 音乐是在播放吗？"+player.isPlaying());
        }
    }

    //暂停
    public void pause() {
        if (null != player && hasPrepared) {
            if(player.isPlaying()){
                player.pause();
                is_Playing=false;
                STATUS=Mess_PAUSE;
            }else{
                player.start();
                is_Playing=true;
                STATUS=Mess_START;
            }
            listener.onMucispauseed(playPosation);
        }
    }

    //上一首
    public void prev() {
        if (null != player && hasPrepared) {
            if(playPosation-1 < 0){
                playPosation=musicInfo.size()-1;
            }else{
                playPosation --;
            }

            play(playPosation);

        }
    }

    //下一首
    public void next() {
        if (null != player && hasPrepared) {
            if(playPosation+1 > musicInfo.size()-1){
                playPosation=0;
            }else{
                playPosation ++;
            }

            play(playPosation);

        }
    }

    public void seekTo(int position) {
        if (null != player && hasPrepared) {
            player.seekTo(position);
        }
    }

    public void setDisplay(SurfaceHolder holder) {
        if (null != player) {
            player.setDisplay(holder);
        }
    }
    //清空缓存
    public void release() {
        hasPrepared = false;
        is_Playing=false;
        player.stop();
        player.release();
        player = null;
    }


    /**
     * 准备完成后回调的
     * @param mediaPlayer
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        hasPrepared =true;
        start();
    }


    /**
     * 播放完成一首歌后回调的
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
//        hasPrepared = false;
        // 通知调用处，调用play()方法进行下一个曲目的播放
        switch (PLAY_STYlE){
            case PLAY_ORDER:
                Log.d(TAG, "onCompletion: 准备下一曲循环播放");
                next();
                break;
            case PLAY_RANDOM:
                Log.d(TAG, "onCompletion: 准备下一曲随机播放");
                play(random.nextInt(musicInfo.size()));
                break;
            case PLAY_REPEAT:
                Log.d(TAG, "onCompletion: 准备单曲循环播放");
                play(playPosation);
                break;
            default:
                break;


        }

    }

    /**
     * 错误
     * @param mediaPlayer
     * @param i
     * @param i1
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        hasPrepared = false;
        return false;
    }




}