package wang.cutemusic.util;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.DownloadListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.xutils.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import wang.cutemusic.data.OnlineMusicInfo;


public class DownloadUtils {

    public static final String TAG="下载工具类的";

    public static final int SUCCESS_LRC=1;
    public static final int FAIL_LRC=2;
    public static final int SUCCESS_MP3=3;
    public static final int FAIL_MP3=4;
    public static final int GET_MP3_URL=5;
    public static final int GET_MP3_URL_FAIL=6;
    public static final int MUSIC_EXISTS=7;

    public volatile static DownloadUtils sInstance;
    private ExecutorService mThreadPool;
    private OnDownloadListener listener;

    //构造
    private DownloadUtils (){
        mThreadPool =Executors.newSingleThreadExecutor();
    }

    //单例
    public static DownloadUtils getsInstance() {
        if(sInstance ==null ){
            synchronized (DownloadUtils.class){
                if (sInstance ==null){
                    sInstance =new DownloadUtils();
                }
            }
        }
        return sInstance;
    }

    //成功或失败监听接口
    public interface OnDownloadListener{
        public void onDownloadSucced(String mp3url);

        public void onDownloadFaild(String error);
    }

    //上面监听的set
   public DownloadUtils setDownloadListener(OnDownloadListener liste){
        this.listener =liste;
        return this ;
   }


   //下载
   public void download(final OnlineMusicInfo musicInfo ){
       @SuppressLint({"HandleLeak", "HandlerLeak"}) Handler handler =new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case GET_MP3_URL:
                        Log.d(TAG, "handleMessage: 下载链接："+msg.obj);
                        downloadMusic(musicInfo,(String)msg.obj,this);
                        break;
                    case GET_MP3_URL_FAIL:
                        if(listener !=null){
                            listener.onDownloadFaild("下载失败，无法获取到歌曲链接");
                        }
                        break;
                    case SUCCESS_MP3:
                        Log.d(TAG, "handleMessage: "+msg.obj);
                        if(listener !=null){
                            listener.onDownloadSucced(musicInfo.getTitle()+"一个神秘连接");
                            String lrcLink = musicInfo.getLrcLink();
                            Log.d(TAG, "handleMessage: 歌词下载连接："+lrcLink);
                            downloadLRC(musicInfo,this);
                        }
                        break;
                    case FAIL_MP3:
                        if(listener !=null){
                            listener.onDownloadFaild("歌曲下载失败");
                        }
                        break;
                    case SUCCESS_LRC:
                        if(listener !=null){
                            listener.onDownloadSucced("歌词下载成功");
                        }
                        break;
                    case FAIL_LRC :
                        if(listener !=null){
                            listener.onDownloadFaild("歌词下载失败");
                        }
                        break;
                    case MUSIC_EXISTS:
                        if(listener !=null){
                            listener.onDownloadFaild("音乐已经存在");
                        }
                        break;
                }
            }
        };
       getDownloadURL(musicInfo, handler);
   }
    //1.获取URL链接，用于下载操作
    public void getDownloadURL(final OnlineMusicInfo musicInfo , final Handler handler ){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String url = Constant.BASE_URL
                        +Constant.NORMAL_REQUEST
                        +"&method="+Constant.METHOD_DOWNLOAD
                        +"&songid="+musicInfo.getSong_id();

                try {
                    Connection.Response response = Jsoup.connect(url)
                            .userAgent(Constant.makeUA())
                            .timeout(60 * 1000)
                            .ignoreContentType(true)
                            .execute();
                    String body=response.body();
                    JSONObject json=new JSONObject(body);
                    Log.d(TAG, json.toString());
                    JSONObject infojson = json.optJSONObject("songinfo");
                    if (infojson !=null){
                        String lrcUrl = infojson.optString("lrclink");
                        Log.d(TAG, "run: 歌词链接："+lrcUrl);
                        musicInfo.setLrcLink(lrcUrl);
                    }
                    JSONObject bitrateJson = json.optJSONObject("bitrate");
                    if (bitrateJson != null){
                        String musicUrl = bitrateJson.optString("file_link");
                        Log.d(TAG, "run: 下载链接："+musicUrl);
                        musicInfo.setUrl(musicUrl);
                        handler.obtainMessage(GET_MP3_URL,musicUrl).sendToTarget();
                    }else {
                        handler.obtainMessage(GET_MP3_URL_FAIL).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.obtainMessage(GET_MP3_URL_FAIL).sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.obtainMessage(GET_MP3_URL_FAIL).sendToTarget();
                }
            }
        });

    }
    //2.下载音乐
    public void downloadMusic(final OnlineMusicInfo musicInfo , final String url, final Handler handler ){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                File musicDirFile =new File(String.valueOf(Environment.getExternalStorageDirectory())+"/Wang/Musicdown");//这个是存储的路径
                if (!musicDirFile.exists()){
                    boolean b = musicDirFile.mkdirs();
                    Log.d(TAG, "run: 创建目录："+b);
                }
                //.../WANGDOWN/test-aaa.mp3
                String target=musicDirFile+"/"+musicInfo.getTitle()+"-"+musicInfo.getArtist()+".mp3";
                Log.d(TAG, "run: 歌曲文件路径："+target);
                File targetFile =new File(target);
                if (targetFile.exists()){
                    handler.obtainMessage(MUSIC_EXISTS).sendToTarget();
                }else {
                    OkHttpClient client = new OkHttpClient();
                    Request requst = new Request.Builder()
                            .url(url).build();
                    try {
                        Response response = client.newCall(requst).execute();
                        if (response.isSuccessful()){
                            PrintStream ps=new PrintStream(targetFile) ;
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes,0,bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_MP3).sendToTarget();
                        }else{
                            handler.obtainMessage(FAIL_MP3).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.obtainMessage(FAIL_MP3).sendToTarget();
                    }
                }

            }
        });


    }
    //3.下载歌词
    public void downloadLRC(final OnlineMusicInfo musicInfo , final Handler handler ){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                File lrcDirFile =new File(String.valueOf(Environment.getExternalStorageDirectory())+"/Wang/Musicdown");//这个是存储的路径
                if (!lrcDirFile.exists()){
                    boolean b = lrcDirFile.mkdirs();
                    Log.d(TAG, "run: 创建目录："+b);
                }
                //.../WANGDOWN/test-aaa.mp3
                String target=lrcDirFile+"/"+musicInfo.getTitle()+"-"+musicInfo.getArtist()+".lrc";
                Log.d(TAG, "run: 歌词文件路径："+target);
                File targetFile =new File(target);
                if (targetFile.exists()){
                    handler.obtainMessage(MUSIC_EXISTS).sendToTarget();
                }else {
                    OkHttpClient client = new OkHttpClient();
                    Request requst = new Request.Builder()
                            .url(musicInfo.getLrcLink()).build();
                    try {
                        Response response = client.newCall(requst).execute();
                        if (response.isSuccessful()){
                            PrintStream ps=new PrintStream(targetFile) ;
                            byte[] bytes = response.body().bytes();
                            ps.write(bytes,0,bytes.length);
                            ps.close();
                            handler.obtainMessage(SUCCESS_LRC).sendToTarget();
                        }else{
                            handler.obtainMessage(FAIL_LRC).sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        handler.obtainMessage(FAIL_LRC).sendToTarget();
                    }
                }
            }
        });

    }




}








