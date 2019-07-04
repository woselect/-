package wang.cutemusic.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.IOException;
import java.util.List;

import wang.cutemusic.HPMusicApp;
import wang.cutemusic.R;
import wang.cutemusic.data.PlayMusicInfo;
import wang.cutemusic.service.MusicService;
import wang.cutemusic.util.BlurUtil;
import wang.cutemusic.util.MergeImage;
import wang.cutemusic.util.MusicUtil;

public class PlayActivity extends BaseActivity implements View.OnClickListener,SeekBar.OnSeekBarChangeListener{


    private final static String TAG="播放界面的";
    private final static int UPDATEPROGRESS=11;

    private SeekBar music_seekbar;
    private TextView music_title_tv,music_artist_tv;
    private TextView music_total_tv,music_current_tv;
    private ImageView music_bg_imgv,music_style;
    private ImageView music_needle_imag,music_disc_imagv;
    private ImageView music_pause_imgv,music_prev_imgv,music_next_imgv,music_like_imgv,music_down_imgv;

    int id;

    HPMusicApp musicApp;
    private ObjectAnimator objectAnimator = null;
    private RotateAnimation rotateAnimation = null;
    private RotateAnimation rotateAnimation2 = null;
    PlayMusicInfo playMusicInfo;

    /**
     * seekBar的监听啊
     * @param seekBar
     * @param i
     * @param fromuser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromuser) {
        if (fromuser){
            Log.d(TAG, "onProgressChanged: 进度点击改变了");
            if(service.player.isPlaying()){
//                service.pause();
                service.seekTo(i);
                music_current_tv.setText(MusicUtil.formatTime(i));
                service.player.start();
            }else if (service.STATUS==service.Mess_PAUSE){
                service.seekTo(i);
                music_current_tv.setText(MusicUtil.formatTime(i));
            }
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStartTrackingTouch: 按下");

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStopTrackingTouch: 手指抬起");
    }


    public MyHandler myHandler=new MyHandler(this);
    static class MyHandler extends Handler {
        private PlayActivity playActivity;

        public MyHandler(PlayActivity playActivity) {
            this.playActivity = playActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(playActivity !=null){
                switch (msg.what){
                    case UPDATEPROGRESS:
                        //更新时间
                        playActivity.music_current_tv.setText(MusicUtil.formatTime(msg.arg1));
                        playActivity.music_seekbar.setProgress(msg.arg1);
                }

            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_layout);
        bindService();
        Log.d(TAG, "onCreate: 准备加载播放界面");
        init();


        initAnimator();



    }

    private void initAnimator() {
        //实例化，设置旋转对象
        objectAnimator = ObjectAnimator.ofFloat(music_disc_imagv, "rotation", 0f, 360f);
        //设置转一圈要多长时间
        objectAnimator.setDuration(8000);
        //设置旋转速率
        objectAnimator.setInterpolator(new LinearInterpolator());
        //设置循环次数 -1为一直循环
        objectAnimator.setRepeatCount(-1);
        //设置转一圈后怎么转
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.start();

        rotateAnimation = new RotateAnimation(-25f, 0f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(0);
        rotateAnimation.setFillAfter(true);
//        music_needle_imag.setAnimation(rotateAnimation);
        //rotateAnimation.start();


        rotateAnimation2 = new RotateAnimation(0f, -25f, Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF, 0.1f);
        rotateAnimation2.setDuration(500);
        rotateAnimation2.setInterpolator(new LinearInterpolator());
        rotateAnimation2.setRepeatCount(0);
        rotateAnimation2.setFillAfter(true);
//        music_needle_imag.setAnimation(rotateAnimation2);
        //rotateAnimation2.cancel();


    }


    private void initview(int playid) {

        //设置播放界面控件的值

        if(service  == null ){
            Log.d(TAG, "initview: service是空的");
        }else {
            Log.d(TAG, "initview: service是有的");
            PlayMusicInfo playMusicInfo = service.getMusicInfo().get(playid);
            music_title_tv.setText(playMusicInfo.getTitle());
            music_artist_tv.setText(playMusicInfo.getArtist()+" - "+playMusicInfo.getAlbum());
            music_seekbar.setMax((int) playMusicInfo.getDuration());
            //可能有问题3
            music_total_tv.setText(String.valueOf( MusicUtil.formatTime(playMusicInfo.getDuration())));


        }
          //设置圆盘的图片
        if (playMusicInfo.getPic_500() != null) {
            Log.d(TAG, "initview: 设置有网络的圆盘图片");

            ImageRequest iconRequest = new ImageRequest(playMusicInfo.getPic_500(), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.play_page_disc);//BitmapFactory.decodeResource用于根据给定的资源ID从指定的资源文件中解析、创建Bitmap对象。
                    Bitmap bm = MergeImage.mergeThumbnailBitmap(bitmap1, bitmap);//将专辑图片放到圆盘中
                    music_disc_imagv.setImageBitmap(bm);
                    Bitmap bgbm = BlurUtil.doBlur(bitmap,25, 10,music_bg_imgv);//将专辑虚化
                    music_bg_imgv.setImageBitmap(bgbm);

                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    //如错误
                    Log.d(TAG, "onErrorResponse: 设置播放界面的圆盘图出现错误");
                }
            });
            iconRequest.setTag("网络图标");
            HPMusicApp.getHttpQueues().add(iconRequest);

        } else {
            Log.d(TAG, "initview: 设置没有网络圆盘图片");

            Bitmap bitmap = MusicUtil.getArtwork(PlayActivity.this, playMusicInfo.getId(),playMusicInfo.getAlbumId(), true, true);
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.play_page_disc);//BitmapFactory.decodeResource用于根据给定的资源ID从指定的资源文件中解析、创建Bitmap对象。
            Bitmap bm = MergeImage.mergeThumbnailBitmap(bitmap1, bitmap);//将专辑图片放到圆盘中

            Bitmap bgbm = BlurUtil.doBlur(bitmap,25, 10,music_bg_imgv);//将专辑虚化
            music_bg_imgv.setImageBitmap(bgbm);
            music_disc_imagv.setImageBitmap(bm);
        }

    }

    //网络
    private void updateOnlineCoverAndBg(PlayMusicInfo playMusicInfo) {
        ImageRequest iconRequest = new ImageRequest(playMusicInfo.getPic_150(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                //07:40
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //如错误
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.muci);
            }
        });
    }

    //本地
    private void updatePlayCoverAndBg(PlayMusicInfo playMusicInfo) {
    }

    private void init() {

//        id = getIntent().getIntExtra("id", 0);
        music_title_tv= (TextView) findViewById(R.id.music_title_tv);//歌曲标题
        music_artist_tv= (TextView) findViewById(R.id.music_artist_tv);//歌手
        music_bg_imgv= (ImageView) findViewById(R.id.music_bg_imgv);//背景
        music_needle_imag= (ImageView) findViewById(R.id.music_needle_imag);//白色的针
        music_disc_imagv= (ImageView) findViewById(R.id.music_disc_imagv);//黑胶转盘

        music_pause_imgv= (ImageView) findViewById(R.id.music_pause_imgv);
        music_prev_imgv= (ImageView) findViewById(R.id.music_prev_imgv);
        music_next_imgv= (ImageView) findViewById(R.id.music_next_imgv);
        music_style= (ImageView) findViewById(R.id.music_play_btn_loop_img);
        music_like_imgv= (ImageView) findViewById(R.id.music_like_imgv);
        music_down_imgv= (ImageView) findViewById(R.id.music_down_imgv);


        music_current_tv= (TextView) findViewById(R.id.music_current_tv);
        music_total_tv= (TextView) findViewById(R.id.music_total_tv);
        music_seekbar= (SeekBar) findViewById(R.id.music_seekbar);


        music_seekbar.setOnSeekBarChangeListener(this);
        music_pause_imgv.setOnClickListener(this);
        music_prev_imgv.setOnClickListener(this);
        music_next_imgv.setOnClickListener(this);
        music_style.setOnClickListener(this);
        music_like_imgv.setOnClickListener(this);
        music_down_imgv.setOnClickListener(this);

        music_style.setTag(MusicService.PLAY_REPEAT);

    }




    @Override
    public void pausemusic() {
        super.pausemusic();
    }

    @Override
    public void prevmusic() {
        super.prevmusic();
    }

    @Override
    public void nextmusic() {
        super.nextmusic();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.music_down_imgv:
                Log.d(TAG, "onClick: 点击了返回");
                this.finish();
                break;
             case R.id.music_pause_imgv:
                Log.d(TAG, "onClick: 点击了暂停");
                pausemusic();
                break;

            case R.id.music_prev_imgv:
                Log.d(TAG, "onClick: 上一曲");
                music_needle_imag.startAnimation(rotateAnimation2);
                prevmusic();
                Log.d(TAG, "onClick: 准备开始旋转动画");
                objectAnimator.start();
                music_needle_imag.startAnimation(rotateAnimation);
                break;
            case R.id.music_next_imgv:
                Log.d(TAG, "onClick: 下一曲");
                music_needle_imag.startAnimation(rotateAnimation2);
                nextmusic();
                Log.d(TAG, "onClick: 准备开始旋转动画");
                objectAnimator.resume();
                music_needle_imag.startAnimation(rotateAnimation);
                break;
            case R.id.music_play_btn_loop_img:
                Log.d(TAG, "onClick: 切换播放模式");

                int style= (int) music_style.getTag();
                switch (style){
                    case MusicService.PLAY_ORDER:
                        music_style.setImageResource(R.drawable.ic_suiji);
                        music_style.setTag(MusicService.PLAY_RANDOM);
                        service.setPLAY_STYlE(MusicService.PLAY_RANDOM);
                        Toast.makeText(this, "切换到了随机播放", Toast.LENGTH_SHORT).show();
                        break;
                     case MusicService.PLAY_RANDOM:
                        music_style.setImageResource(R.drawable.ic_danquxunhuan);
                        music_style.setTag(MusicService.PLAY_REPEAT);
                        service.setPLAY_STYlE(MusicService.PLAY_REPEAT);
                        Toast.makeText(this, "切换到了单曲循环", Toast.LENGTH_SHORT).show();
                        break;
                     case MusicService.PLAY_REPEAT:
                        music_style.setImageResource(R.drawable.ic_xunhuan);
                        music_style.setTag(MusicService.PLAY_ORDER);
                        service.setPLAY_STYlE(MusicService.PLAY_ORDER);
                        Toast.makeText(this, "切换到了顺序播放", Toast.LENGTH_SHORT).show();
                        break;
                      default:
                          break;
                }
                break;
            case R.id.music_like_imgv:
                PlayMusicInfo playInfo=service.getMusicInfo().get(service.getcurPosition());
                Log.d(TAG, "onClick: 当前操作喜爱音乐的歌曲是："+playInfo.getTitle()+"id是："+playInfo.getId()+"musicid是："+playInfo.getMusicid());
                Log.d(TAG, "onClick: "+musicApp.daoConfig.toString());
                DbManager db = x.getDb(musicApp.daoConfig);


                try {
                    List<PlayMusicInfo> selector = db.selector(PlayMusicInfo.class).findAll();
                    if(selector !=null){
                        for (PlayMusicInfo info : selector) {
                            Log.d(TAG, "onClick: 喜欢这个数据库里面的音乐："+info.getTitle()+"ID:"+info.getId()+"msicid是："+info.getMusicid());

                        }
                    }else {
                        Log.d(TAG, "onClick: 喜欢数据库里面是空的"+playInfo.getId());
                    }
                    PlayMusicInfo likeinfo = db.selector(PlayMusicInfo.class).where("id","=",playInfo.getId()).findFirst();
                    if (likeinfo == null){//不在音乐收藏数据库中
                        likeinfo=playInfo;
                        likeinfo.setIsFavorite(1);
                        db.save(likeinfo);
                        Log.d(TAG, "onClick: 喜欢音乐保存完毕");
                        music_like_imgv.setImageResource(R.drawable.ic_xin_0);
                        Toast.makeText(PlayActivity.this,"已收藏", Toast.LENGTH_SHORT).show();
                    }else {//在音乐收藏数据库中
                       Log.d(TAG, "onClick: 准备取消这个音乐的喜欢:"+likeinfo.getTitle());
                        //在音乐收藏数据库 删除音乐
                        int isFavorite = likeinfo.getIsFavorite();
                        if (isFavorite ==1 ){
                            likeinfo.setIsFavorite(0);
                            music_like_imgv.setImageResource(R.drawable.xin_default);
                            Toast.makeText(PlayActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();

                        }else{
                            likeinfo.setIsFavorite(1);
                            music_like_imgv.setImageResource(R.drawable.ic_xin_0);
                            Toast.makeText(PlayActivity.this, "已收藏", Toast.LENGTH_SHORT).show();
                        }
                        db.update(likeinfo,"isFavorite");
                    }

                } catch (DbException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        db.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                break;
        }
    }




    //进度条改变
    @Override
    public void Publish(int progress) {
        //发生改变就发送消息
//        Log.d(TAG, "Publish: 发生改变就发送消息进度值是："+progress);
        Message message =myHandler.obtainMessage(UPDATEPROGRESS);
        message.arg1=progress;
        myHandler.sendMessage(message);
    }

    //歌曲改变
    @Override
    public void Changed(int musicid) {


        playMusicInfo = service.getMusicInfo().get(musicid);
        id =musicid;
        //这是判断是否在暂停状态的，但是我又另外写了个暂停的监听，搞不赢啊
        if (service.is_Playing){
            music_pause_imgv.setImageResource(R.drawable.ic_stop);
            objectAnimator.resume();
            music_needle_imag.startAnimation(rotateAnimation);
        }else {
            music_pause_imgv.setImageResource(R.drawable.ic_start);
            objectAnimator.pause();
            music_needle_imag.startAnimation(rotateAnimation2);
        }

        Log.d(TAG, "Changed: 播放界面的音乐改变监听");
        
        initview(id);

        //         写到更新界面 设置进度条的时间
        Log.d(TAG, "Changed: 进度为："+service.getCurrentProgress());
        music_seekbar.setProgress(service.getCurrentProgress());//后面加的，不知道有没有问题
        music_current_tv.setText(MusicUtil.formatTime(service.getCurrentProgress()));

        switch (service.getPLAY_STYlE()){
            case MusicService.PLAY_ORDER:
                music_style.setImageResource(R.drawable.ic_xunhuan);
                music_style.setTag(MusicService.PLAY_ORDER);
                break;
            case MusicService.PLAY_RANDOM:
                music_style.setImageResource(R.drawable.ic_suiji);
                music_style.setTag(MusicService.PLAY_RANDOM);
                break;
            case MusicService.PLAY_REPEAT:
                music_style.setImageResource(R.drawable.ic_danquxunhuan);
                music_style.setTag(MusicService.PLAY_REPEAT);
                break;
                default:
                    Log.d(TAG, "Changed: 歌曲改变但是什么模式都不是");
                    break;
        }
        DbManager db=null;
        try {
             db = x.getDb(musicApp.daoConfig);
            PlayMusicInfo likeinfo = db.selector(PlayMusicInfo.class).where("id","=",playMusicInfo.getId()).findFirst();
            if(likeinfo ==null || likeinfo.getIsFavorite() == 0){
                music_like_imgv.setImageResource(R.drawable.xin_default);
            }else{
                music_like_imgv.setImageResource(R.drawable.ic_xin_0);
            }
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            try {
                db.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        new LoadingPhotoTask().execute(musicid);
        id=musicid;

    }


    @Override
    public void pauseed(int musicid) {
        Log.d(TAG, "pauseed: 播放页面暂停");

        if (service.isIs_Playing()) {
            music_pause_imgv.setImageResource(R.drawable.ic_stop);
            objectAnimator.resume();
            music_needle_imag.startAnimation(rotateAnimation);
            Log.d(TAG, "rotateAnimation: 执行完成往左的");
        } else {
            music_pause_imgv.setImageResource(R.drawable.ic_start);
            objectAnimator.pause();
            music_needle_imag.startAnimation(rotateAnimation2);
            Log.d(TAG, "rotateAnimation2: 执行完成往右的");
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    public  class LoadingPhotoTask extends AsyncTask<Integer,Integer ,Integer > {

        private static final String TAG="图片异步任务里面";

        Bitmap bgbm;
        Bitmap bm;
        Bitmap bbbb;

        @Override
        protected Integer doInBackground(Integer... va) {
            int playid =va[0];
                //好奇怪 iconRequest 是最后执行的12.21最后出的问题在这里

                //设置圆盘的图片
                if (playMusicInfo.getPic_500() != null) {
                    Log.d(TAG, "initview: 设置有网络的背景图片");

                    ImageRequest iconRequest = new ImageRequest(playMusicInfo.getPic_500(), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {

                            bbbb =bitmap;
                            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.play_page_disc);//BitmapFactory.decodeResource用于根据给定的资源ID从指定的资源文件中解析、创建Bitmap对象。
                             bm = MergeImage.mergeThumbnailBitmap(bitmap1, bitmap);//将专辑图片放到圆盘中

                            //30,50 就是底色太单一会10,40会有马赛克的感觉
                            bgbm = BlurUtil.doBlur(bitmap,25, 10,music_bg_imgv);//将专辑虚化
                            if (bgbm == null)
                                Log.d(TAG, "doInBackground: 专辑图片虚化完成之后为空");
                            else
                                Log.d(TAG, "onResponse: 专辑图片虚化完成之后有的宽是："+bgbm.getWidth()+"getByteCount:"+bgbm.getByteCount());
                        }
                    }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            //如错误
                            Log.d(TAG, "onErrorResponse: 设置播放界面的背景图出现错误");
                        }
                    });
                    iconRequest.setTag("网络背景");
                    HPMusicApp.getHttpQueues().add(iconRequest);
                    Log.d(TAG, "doInBackground: 网络背景图提交也许？");

                } else {
                    Log.d(TAG, "initview: 设置没有网络背景图片");

                    Bitmap bitmap = MusicUtil.getArtwork(PlayActivity.this, playMusicInfo.getId(),playMusicInfo.getAlbumId(), true, true);
                    //本地的 10,1 刚好合适，不知道是不是因为本来图片模糊的原因
                    bgbm = BlurUtil.doBlur(bitmap, 20, 5,music_bg_imgv);//将专辑虚化
                    if (bgbm == null)
                        Log.d(TAG, "doInBackground: 专辑图片虚化完成之后为空");
                    else
                        Log.d(TAG, "onResponse: 专辑图片虚化完成之后有的宽是："+bgbm.getWidth()+"getByteCount:"+bgbm.getByteCount());
                }

            
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }





        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            music_disc_imagv.setImageBitmap(bm);
            music_bg_imgv.setImageBitmap(bbbb);
            Log.d(TAG, "onPostExecute: 网络背景图片设置完成");

        }
    }

    




}
