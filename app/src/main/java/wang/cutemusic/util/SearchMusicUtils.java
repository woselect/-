package wang.cutemusic.util;

import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wang.cutemusic.data.OnlineMusicInfo;

public class SearchMusicUtils {
    private  static  final String TAG ="搜索工具类的";
    private  static  final int SIZE =20;


    private volatile static SearchMusicUtils instance;
    private ExecutorService mThreadPool;
    private OnSearchResultListener listener;


    //构造
    private SearchMusicUtils(){
        mThreadPool= Executors.newSingleThreadExecutor();
    }

    //单例
    public static SearchMusicUtils  getInstance(){
        if(instance == null ){
            synchronized (SearchMusicUtils.class){
                if(instance == null ){
                    instance =new SearchMusicUtils();
                }
            }
        }
        return instance ;
    }

    public interface OnSearchResultListener{
        public void onSearchResult(ArrayList<OnlineMusicInfo> result );
    }

    //设置监听
    public SearchMusicUtils setListener(OnSearchResultListener listener ){
        this.listener =listener;
        return  this ;
    }

    //搜索方法
    public void search (final String key ,final  int page){
        final SearchHandler handler =new SearchHandler(listener) ;

        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<OnlineMusicInfo> result = getMusicList(key);
                if(result == null ){
                    handler.sendEmptyMessage(Constant.FAIL);
                    return;
                }
                handler.obtainMessage(Constant.SUCCESS,result).sendToTarget();
            }
        });
    }

    private ArrayList<OnlineMusicInfo> getMusicList (String key){
        String url = Constant.BASE_URL
                +Constant.NORMAL_REQUEST
                +"&method="+Constant.METHOD_SEARCH
                +"&query="+key;
        Log.d(TAG, "getMusicList: 查询的url:"+url);
        try {
            Connection.Response response = Jsoup.connect(url)
                    .userAgent(Constant.makeUA())
                    .timeout(60 * 1000)
                    .ignoreContentType(true)
                    .execute();
            String body=response.body();
            JSONObject json=new JSONObject(body);
            Log.d(TAG, "getMusicList: "+json.toString());

            ArrayList<OnlineMusicInfo> searchResult =new ArrayList<>() ;
            JSONArray jsonArray = json.optJSONArray("song");
            if (jsonArray ==null){
                if(key.contains(" ")){
                    String newkey = key.substring(0, key.indexOf(" "));
                    String newurl = Constant.BASE_URL
                            +Constant.NORMAL_REQUEST
                            +"&method="+Constant.METHOD_SEARCH
                            +"&query="+newkey;
                    Log.d(TAG, "getMusicList: 二次查找的url:"+newurl);
                    Connection.Response newresponse = Jsoup.connect(url)
                            .userAgent(Constant.makeUA())
                            .timeout(60 * 1000)
                            .ignoreContentType(true)
                            .execute();
                    String newbody=newresponse.body();
                    JSONObject newjson=new JSONObject(newbody);
                    Log.d(TAG, "getMusicList: "+newjson.toString());
                    jsonArray = newjson.optJSONArray("song");
                }
                if (jsonArray == null ){
                    return null ;
                }
            }
            Log.d(TAG, "getMusicList: 查到的数据一："+jsonArray.getJSONObject(0).toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                OnlineMusicInfo info =new OnlineMusicInfo() ;

                //本来的key
                info.setSong_id(object.optString("songid"));
                info.setTitle(object.optString("songname"));
                info.setArtist(object.optString("artistname"));




                String songURL=Constant.BASE_URL
                        +"?method="+Constant.METHOD_DOWNLOAD
                        +"&songid="+info.getSong_id();
                Log.d(TAG, "doInBackground: songURL"+songURL);

                org.jsoup.Connection.Response songresponse = Jsoup.connect(songURL).userAgent(Constant.makeUA())
                        .timeout(60 * 1000)
                        .ignoreContentType(true)
                        .execute();
                String songbody=songresponse.body();
                JSONObject songJson = new JSONObject(songbody);
                Log.d(TAG, "getMusicList: 其中一首歌的info："+songJson.toString());

                JSONObject songinfoJson = songJson.optJSONObject("songinfo");
                if (songinfoJson != null ){
                    info.setTing_uid(songinfoJson.optString("ting_uid"));
                    info.setAlbumId(songinfoJson.optString("ablum_id"));
                    info.setArtistid(songinfoJson.optString("artist_id"));

                    info.setPic_90(songinfoJson.optString("pic_small"));
                    info.setPic_150(songinfoJson.optString("pic_big"));
                    info.setPic_300(songinfoJson.optString("pic_radio"));
                    info.setPic_500(songinfoJson.optString("pic_premium"));
                    info.setLrcLink(songinfoJson.optString("lrclink"));

                }
                JSONObject bitrateJson = songJson.optJSONObject("bitrate");
                if(bitrateJson !=null){
                    String musicUrl=bitrateJson.optString("file_link");
//                    Log.d(TAG, "getMusicList: 搜索里面连接："+musicUrl);
                    info.setUrl(musicUrl);
                    info.setDuration(bitrateJson.optLong("file_duration")*1000);
                }

                Log.d(TAG, "getMusicList: 数据："+info.toString());
                searchResult.add(info);
            }
//            Log.d(TAG, "getMusicList: "+searchResult.toString());
            return searchResult;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null ;
    }

    private static class SearchHandler extends android.os.Handler{
        private OnSearchResultListener mlistener;

        public SearchHandler( OnSearchResultListener listener) {
            this.mlistener=listener;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.SUCCESS :
                    if (mlistener !=null){
                        mlistener.onSearchResult((ArrayList<OnlineMusicInfo>) msg.obj);
                    }
                    break;
                case Constant.FAIL :
                    if (mlistener !=null){
                        mlistener.onSearchResult(null);
                    }
                    break;
            }


        }
    }


}
