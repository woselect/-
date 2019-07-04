package wang.cutemusic.util;

import android.os.Build;

public class Constant {
    public static final String DB_NAME ="SHUJU";
    public static final String USER_AGENT ="SHUJU";
    public static final int SUCCESS =1;
    public static final int FAIL =2;


    //闪屏页面图片资源接口
    public static final String SPLASH_URL ="SHUJU";


    public static final String BASE_URL ="http://tingapi.ting.baidu.com/v1/restserver/ting";
    //获取列表：
    public static final String METHOD_ORDER_LIST ="baidu.ting.billboard.billList";
    public static final String METHOD_SEARCH ="baidu.ting.search.catalogSug";
    public static final String METHOD_LRC ="baidu.ting.song.lry";
    public static final String METHOD_RECOMMEND ="baidu.ting.song.getRecommandSongList";
    public static final String METHOD_DOWNLOAD ="baidu.ting.song.play";




    public static final String TYPE_HOT ="2";//热歌榜


    public static final String NORMAL_REQUEST ="?format=json&calback=&from=webapp_music" ;

    public static String makeUA(){
        String userAgent= Build.BRAND+"/"+Build.MODEL+"/"+Build.VERSION.RELEASE;
        return userAgent;
    }


}
