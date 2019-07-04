package wang.cutemusic.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.astuetz.PagerSlidingTabStrip;

import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.List;

import wang.cutemusic.HPMusicApp;
import wang.cutemusic.R;
import wang.cutemusic.data.MusicInfo;
import wang.cutemusic.data.PlayMusicInfo;
import wang.cutemusic.fragment.InternetMusicFragment;
import wang.cutemusic.fragment.LikeMusicFragment;
import wang.cutemusic.fragment.LocalMusicFragment;
import wang.cutemusic.service.MusicService;
import wang.cutemusic.util.BlurUtil;
import wang.cutemusic.util.MusicUtil;

public class MainActivity extends BaseActivity  implements View.OnClickListener,LocalMusicFragment.MItemOnclickListener{


    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private List<Fragment> fragmentListList = new ArrayList<Fragment>();

    private static final String TAG="Main主要的ACtivity";

    private RelativeLayout relativeLayout1;
    private TextView music_title,music_Artist;
    public  ImageView albumImage,im_startorstop,im_next,im_menu,main_backgroud;


    int id =0;
    HPMusicApp hpMusicApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: 准备加载主界面");
        init();
        hpMusicApp=new HPMusicApp();
//        addlocalmusicToPlay();

    }

    private void initbottom(int id) {
        //设置主界面下面的控件

        /*Bitmap tu = BitmapFactory.decodeResource(getResources(), R.drawable.huahua);
        Bitmap bit = BlurUtil.doBlur(tu,  5,true );//将专辑虚化
        main_backgroud.setImageBitmap(bit);*/



        PlayMusicInfo info = service.getMusicInfo().get(id);
        Log.d(TAG, "initbottom: 歌曲标题是："+info.getTitle());
        music_title.setText(info.getTitle());
        music_Artist.setText(info.getArtist());
        Log.d(TAG, "initbottom: 这是完歌曲标题");



        if(info.getType() ==PlayMusicInfo.TYPE_LOCAL){//本地
            //可能有问题3
            Bitmap bitmap = MusicUtil.getArtwork(this, info.getId(),info.getAlbumId(), true, true);
            albumImage.setImageBitmap(bitmap);
        }else {//网络
            ImageRequest iconRequest = new ImageRequest(info.getPic_90(), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    albumImage.setImageBitmap(bitmap);
                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    //如错误
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.muci);
                    albumImage.setImageBitmap(bitmap);
                }
            });
            iconRequest.setTag("网络图标");
            HPMusicApp.getHttpQueues().add(iconRequest);
        }

    }

    //初始化主界面
    private void init() {
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        tabs.setTextColorResource(R.color.colorWhite);


        Log.d(TAG, "init: 初始化两个Fragment");

        LocalMusicFragment local=new LocalMusicFragment();
        InternetMusicFragment internet=new InternetMusicFragment();
        LikeMusicFragment like=new LikeMusicFragment();
        fragmentListList.add(local);
        fragmentListList.add(internet);
        fragmentListList.add(like);

        relativeLayout1= (RelativeLayout) findViewById(R.id.RelativeLayout1);
        relativeLayout1.setOnClickListener(this);

        main_backgroud=findViewById(R.id.main_backgroud);

        //下面是设置背景的
        Bitmap oldBit = BitmapFactory.decodeResource(getResources(), R.drawable.huahua);
        Bitmap newBmp = Bitmap.createBitmap(oldBit.getWidth(), oldBit.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(newBmp);
        c.drawBitmap(oldBit, 0, 0, new Paint());
        Bitmap over = BlurUtil.doBlur(newBmp, 30, true);
        main_backgroud.setImageBitmap(over);


        //下方的控件
        music_title= (TextView) findViewById(R.id.music_title);
        music_Artist= (TextView) findViewById(R.id.music_Artist);
        albumImage= (ImageView) findViewById(R.id.albumImage);
        im_startorstop= (ImageView) findViewById(R.id.im_startorstop);
        im_next= (ImageView) findViewById(R.id.im_next);
        im_menu= (ImageView) findViewById(R.id.im_menu);
        im_startorstop.setOnClickListener(this);
        im_next.setOnClickListener(this);
        im_menu.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor edit = hpMusicApp.sharedPreferences.edit();
        Log.d(TAG, "onDestroy: "+service.getcurPosition());
        edit.putInt("mposation", service.getcurPosition());
        edit.putInt("mplaystyle", service.getPLAY_STYlE());
//        edit.putInt("playlist", service.getPlayList());
        edit.commit();
        stopService(new Intent(this,MusicService.class));

    }

    @Override
    public void startmusic(int id) {
        super.startmusic(id);
    }

    /**
     * 本地Fragmentitem点击的回调方法
     *
     */
    @Override
    public void MItemOnclick(int  i) {
        Log.d(TAG, "MItemOnclick: ID:"+i);
        id=i;
        if (service.getPlayList() != MusicService.MY_MUSIC_LIST){
            addlocalmusicToPlay();
        }
        service.setPlayList(MusicService.MY_MUSIC_LIST);
        initbottom(id);
        startmusic(i);
        im_startorstop.setImageResource(R.drawable.ic_stop);
    }

    private void addlocalmusicToPlay() {

        ArrayList<PlayMusicInfo> playMusic=new ArrayList<>();

            Log.d(TAG, "addlocalmusicToPlay: 开始把本地列表添加到播放列表");
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
                playMusic.add(play);
            }
        Log.d(TAG, "addlocalmusicToPlay: 把本地集合转换为Play集合的大小："+playMusic.size());
        service.setMusicInfo(playMusic);
    }


    public void gotoPlayMusic() {
        if (id ==0 && service.getMusicInfo().size()==0 ){
            Toast.makeText(this, "请播放音乐", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("id", id);
        startActivity(intent) ;
    }

    @Override
    public void pausemusic() {
        super.pausemusic();
    }

    @Override
    public void nextmusic() {
        super.nextmusic();
    }




    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.RelativeLayout1:
                Log.d(TAG, "onClick: 主界面下面控件被点击");
                gotoPlayMusic();
                break;
            case R.id.im_startorstop:
                Log.d(TAG, "onClick: 主界面暂停");
                pausemusic();
                break;
            case R.id.im_next:
                Log.d(TAG, "onClick: 主界面下一首");
                nextmusic();
                break;
            case R.id.im_menu:
                Log.d(TAG, "onClick: 主界面跳到收藏");
                startActivity(new Intent(MainActivity.this,LikeMusicActivity.class));
                break;


        }

    }


    @Override
    public void Publish(int progress) {
        //更新进度的
    }

    @Override
    public void Changed(int musicid) {

        id =musicid;
        Log.d(TAG, "Changed: 传过来的ID是："+musicid);
//        写到更新界面了关于播发网络的
        if(service.getMusicInfo() !=null   && musicid <=service.getMusicInfo().size()) {
            if (musicid ==0 && service.getMusicInfo().size()==0 ){
                return ;
            }
            initbottom(musicid);
        }
        if (service.isIs_Playing()){
            im_startorstop.setImageResource(R.drawable.ic_stop);
        }else {
            im_startorstop.setImageResource(R.drawable.ic_start);
        }

        Log.d(TAG, "Changed: 主界面的音乐改变监听");

    }

    @Override
    public void pauseed(int musicid) {
        Log.d(TAG, "pauseed: 主界面暂停");
        if (service.isIs_Playing()){
            im_startorstop.setImageResource(R.drawable.ic_stop);
        }else {
            im_startorstop.setImageResource(R.drawable.ic_start);
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = { "本地音乐", "网络音乐","收藏音乐"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentListList.get(position);
        }

    }

}