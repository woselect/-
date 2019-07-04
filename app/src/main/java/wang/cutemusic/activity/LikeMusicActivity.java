package wang.cutemusic.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;

import wang.cutemusic.HPMusicApp;
import wang.cutemusic.R;
import wang.cutemusic.adpter.LikeMusicAdapter;
import wang.cutemusic.data.MusicInfo;
import wang.cutemusic.data.PlayMusicInfo;

public class LikeMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener {


    private ListView listView_like;
    private HPMusicApp app;
    private ArrayList<PlayMusicInfo> likemusicInfos;
    private LikeMusicAdapter adapter;
    private boolean isChange = false;//表示当前播放列表是否为收藏列表

    DbManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_music);

        app = (HPMusicApp) getApplication();
        listView_like = (ListView) findViewById(R.id.listView_like);//实例化布局
        listView_like.setOnItemClickListener(this);
        initData();//初始化数据

    }

    private void initData() {
        try {
            db= x.getDb(app.daoConfig);
            likemusicInfos = (ArrayList<PlayMusicInfo>) db.selector(PlayMusicInfo.class).where("isFavorite","=",1).findAll();
            if(likemusicInfos ==null || likemusicInfos.size() ==0){
                Log.d(TAG, "initData: 喜欢的音乐为空");
                return;
            }
            Log.d(TAG, "initData: 喜欢的音乐第一首标题为："+likemusicInfos.get(0).getTitle());
            adapter = new LikeMusicAdapter(this,likemusicInfos);
            listView_like.setAdapter(adapter);
        } catch (DbException e) {
            e.printStackTrace();
        }


    }


    //把播放服务的绑定和解绑放在onResume,onPause里,好处是,每次回到当前Activity就获取一次播放状态
    @Override
    protected void onResume() {
        super.onResume();
        bindService();//绑定服务
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();//解绑服务
    }



    @Override
    public void Publish(int progress) {

    }

    @Override
    public void Changed(int musicid) {

    }

    @Override
    public void pauseed(int musicid) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        if (service.getPlayList() != service.LIKE_MUSIC_LIST){
            service.setMusicInfo(likemusicInfos);//播放列表切换为收藏列表
            service.setPlayList(service.LIKE_MUSIC_LIST);
        }
        service.play(position);

    }
}
