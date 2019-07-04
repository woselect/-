/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wang.cutemusic.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import wang.cutemusic.R;
import wang.cutemusic.activity.MainActivity;
import wang.cutemusic.adpter.OnlineMusicAdapter;
import wang.cutemusic.data.OnlineMusicInfo;
import wang.cutemusic.data.PlayMusicInfo;
import wang.cutemusic.service.MusicService;
import wang.cutemusic.util.BlurUtil;
import wang.cutemusic.util.Constant;
import wang.cutemusic.util.SearchMusicUtils;

public class InternetMusicFragment extends Fragment implements AdapterView.OnItemClickListener, OnRefreshListener, View.OnClickListener {

	private static final String ARG_POSITION = "position";
    private int position;
    View mView;

    private static final String TAG = "网络Fragment";
    private MainActivity  mainActivity;
    private ListView listView ;
    private OnlineMusicAdapter adapter;
    private SmartRefreshLayout refreshLayout;
    private boolean isAuto=false;
    private boolean isAutoFinish=false;
    private ArrayList <OnlineMusicInfo> onlineMusicInfos =new ArrayList<>() ;

    ArrayList<OnlineMusicInfo> sr=new ArrayList<>();
    private int page =1;


    private EditText  searchedit;
    private ImageView  searchenter,online_music_xiazai;
    private RelativeLayout searchempty;

    //饿汉
    private static InternetMusicFragment internetFragment;

    public static InternetMusicFragment  newInstance(){
        if(internetFragment == null){
            internetFragment=new InternetMusicFragment();

        }
        return internetFragment;
    }




	public static InternetMusicFragment newInstance(int position) {
		InternetMusicFragment f = new InternetMusicFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

    @Override
    public void onAttach(Context  context) {

        super.onAttach(context);
        mainActivity = (MainActivity) context;
        Log.d(TAG, "onAttach:不是的什么时候能用到：");
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mView == null) {
			mView = inflater.inflate(R.layout.fragment_internetmusic,null);
		}

		listView = mView.findViewById(R.id.internet_listview);
		listView.setOnItemClickListener(this);
        adapter =new OnlineMusicAdapter(mainActivity,onlineMusicInfos);
        listView.setAdapter(adapter);
//        listView.addFooterView(LayoutInflater.from(mainActivity).inflate(R.layout.music_mian_bottom_layout, null));
        listView.addHeaderView(LayoutInflater.from(mainActivity).inflate(R.layout.search_de, null));
        refreshLayout =mView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mainActivity));
        refreshLayout.setOnRefreshListener(this);

        searchedit=mView.findViewById(R.id.search_edit);
        searchenter=mView.findViewById(R.id.search_enter);
        searchempty=mView.findViewById(R.id.search_empty);
        searchedit.setOnClickListener(this);
        searchenter.setOnClickListener(this);

        /*online_music_xiazai=mView.findViewById(R.id.online_music_xiazai);*/

        loadOnlineData();
		return mView;
	}

    private void loadOnlineData() {
        String requedtBody = Constant.NORMAL_REQUEST
                +"&method=" +Constant.METHOD_ORDER_LIST
                +"&type="+Constant.TYPE_HOT
                +"&size=20"+"&offset=0";
        new LoadOnlineMusicTask().execute(Constant.BASE_URL+requedtBody);


    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemClick: 被点击："+i);
//        写到播放，准备把搜索到的东西拿出来
        if (i==0){
            playMusic(i);
        }else {
            playMusic(i-1);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        Log.d(TAG, "onRefresh: isAuto"+isAuto);
        if(isAuto){
            if(isAutoFinish){
                isAuto =false;
                refreshLayout.finishRefresh();
            }
            isAuto =false;
        }else{
            loadOnlineData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.search_edit :
                searchedit.setCursorVisible(true);
                break;
             case  R.id.search_enter :
                 searchMusic();
                break;
             case  R.id.online_music_xiazai :
//                 Toast.makeText(mainActivity, "准备显示Dialog！",Toast.LENGTH_SHORT ).show();
//                 OnlineDialogFragment dialogFragment = new OnlineDialogFragment();
                 break;


        }

    }


    /***
     * 本来的20条数据都是好的，应该都有，搜索的感觉只有一个标题有内容
     */
    private void searchMusic() {
        String key = searchedit.getText().toString();
        Log.d(TAG, "searchMusic: 搜索的内容："+key);
        if(TextUtils.isEmpty(key)){
            Toast.makeText(mainActivity, "请输入关键词", Toast.LENGTH_SHORT).show();
            return;
        }
        isAuto =true;
        refreshLayout.autoRefresh();
        SearchMusicUtils.getInstance().setListener(new SearchMusicUtils.OnSearchResultListener() {
            @Override
            public void onSearchResult(ArrayList<OnlineMusicInfo> result) {
                if (result !=null){
                    sr = adapter.getMusicInfo();
                    sr.clear();
                    sr.addAll(result);
                    adapter.notifyDataSetChanged();
//                    searchempty.setVisibility(View.GONE);
                }else{
//                    searchempty.setVisibility(View.VISIBLE);
                }
                Log.d(TAG, "onSearchResult: 完成刷新");
                isAutoFinish =true;
                refreshLayout.finishRefresh();
            }
        }).search(key, page);




    }

    //播放
    public  void playMusic(int curPosation) {
        addOnlineToPlay();
        mainActivity.service.setPlayList(MusicService.ONLINE_MUSIC_LIST);
        if (mainActivity.service ==null)
            Log.d(TAG, "playMusic: 得到的服务为空");
        else {
            Log.d(TAG, "playMusic: 得到的服务为有");
            mainActivity.service.play(curPosation);
        }
        mainActivity.im_startorstop.setImageResource(R.drawable.ic_stop);
    }

    private void addOnlineToPlay() {

        ArrayList<OnlineMusicInfo> ready=new ArrayList<>();
        if (sr.size() ==0){
            Log.d(TAG, "addOnlineToPlay: 添加本来的");
            ready.addAll(onlineMusicInfos);
        }else {
            Log.d(TAG, "addOnlineToPlay: 添加搜索的");
            ready.addAll(sr);
        }

        ArrayList<PlayMusicInfo> info= new ArrayList<>() ;
        for (OnlineMusicInfo ms : ready) {
            PlayMusicInfo play=new PlayMusicInfo();
            play.setId(Long.parseLong(ms.getSong_id()));
//            play.setOnLineid(Long.parseLong(ms.getTing_uid()));
            play.setArtist(ms.getArtist());
            play.setArtistid(ms.getArtistid());
            play.setDuration(ms.getDuration());
//                play.setAlbumId(Long.parseLong(ms.getAlbumId()));
            play.setSize(ms.getSize());
            play.setTitle(ms.getTitle());
//            Log.d(TAG, "addOnlineToPlay: 播放的标题："+play.getTitle());
            play.setType(PlayMusicInfo.TYPE_ONLINE);
            play.setUrl(ms.getUrl());
//            Log.d(TAG, "addOnlineToPlay: 播放的路径："+play.getUrl());
            play.setPic_90(ms.getPic_90());
            play.setPic_150(ms.getPic_150());
            play.setPic_300(ms.getPic_300());
            play.setPic_500(ms.getPic_500());
            info.add(play);
        }
        Log.d(TAG, "addOnlineToPlay: 把网络集合转换为Play集合的大小："+info.size());
        mainActivity.service.setMusicInfo(info);

    }



    class LoadOnlineMusicTask extends AsyncTask<String,Integer ,Integer >{

		private static final String TAG="异步任务里面";

		private View footer_view;

        public LoadOnlineMusicTask() {
        }

        @Override
		protected void onPreExecute() {
			super.onPreExecute();
			isAuto = true;
			refreshLayout.autoRefresh();
            onlineMusicInfos.clear();

		}

		@Override
		protected Integer doInBackground(String... strings) {


          /*  Bitmap tu = BitmapFactory.decodeResource(getResources(), R.drawable.huahua);
            Bitmap bit = BlurUtil.doBlur(tu, 20,10,mainActivity.main_backgroud  );//将专辑虚化
            */

            String url =strings[0];
            Log.d(TAG, "doInBackground: url :"+url);
            try {
                org.jsoup.Connection.Response response = Jsoup.connect(url).userAgent(Constant.makeUA())
                        .timeout(20 * 1000)
                        .ignoreContentType(true)
                        .execute();
                String body=response.body();
                JSONObject json=new JSONObject(body);
                Log.d(TAG, "doInBackground: "+json.toString());

                JSONArray jsonArray = json.optJSONArray("song_list");
                Log.d(TAG, "doInBackground: "+jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    OnlineMusicInfo Info =new OnlineMusicInfo();

                    Info.setSong_id(object.optString("song_id"));
                    Info.setTing_uid(object.optString("ting_uid"));

                    Info.setAlbumId(object.optString("ablum_id"));
                    Info.setArtistid(object.optString("artist_id"));
                    Info.setTitle(object.optString("title"));
                    Info.setDuration(object.optLong("file_duration")*1000);
                    Info.setPic_90(object.optString("pic_small"));
                    Info.setPic_150(object.optString("pic_big"));
                    Info.setPic_300(object.optString("pic_radio"));
                    Info.setPic_500(object.optString("pic_premium"));
                    Info.setArtist(object.optString("artist_name"));
                    Info.setLrcLink(object.optString("lrclink"));

//                    Log.d(TAG, "doInBackground: 两次的歌词连接是一样的："+Info.getLrcLink());
                    String songURL=Constant.BASE_URL
                            +"?method="+Constant.METHOD_DOWNLOAD
                            +"&songid="+Info.getSong_id();
                    Log.d(TAG, "doInBackground: songURL"+songURL);

                    org.jsoup.Connection.Response songresponse = Jsoup.connect(songURL).userAgent(Constant.makeUA())
                            .timeout(60 * 1000)
                            .ignoreContentType(true)
                            .execute();
                    String songbody=songresponse.body();
                    JSONObject songJson = new JSONObject(songbody);
                    Log.d(TAG, "doInBackground: "+songJson.toString());
                    JSONObject songinfo = songJson.optJSONObject("songinfo");
                    if(songinfo!=null){
                        String lrclink = songinfo.optString("lrclink");

                        Info.setLrcLink(lrclink);
                    }
                    JSONObject bitrateJson = songJson.optJSONObject("bitrate");
                    if(bitrateJson !=null){
                        String musicUrl=bitrateJson.optString("file_link");
                        Log.d(TAG, "doInBackground: 下载连接："+musicUrl);
                        Info.setUrl(musicUrl);
                    }
                    onlineMusicInfos.add(Info);
                }

            } catch (IOException |JSONException |UncheckedIOException e) {
                e.printStackTrace();
                return -1;
            }
            return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result ==1){
			    adapter.setMusicInfo(onlineMusicInfos);
			    adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(mainActivity, "网络超时，请检查网络",Toast.LENGTH_SHORT ).show();
            }
            Log.d(TAG, "onPostExecute: 刷新");
			refreshLayout.finishRefresh();
			if(onlineMusicInfos !=null && onlineMusicInfos.size()>0){
			   listView.setVisibility(View.VISIBLE);
			   //再把搜索控件隐藏起来
            }else{
                listView.setVisibility(View.GONE);
                //再把搜索控件显示出来
            }

		}
	}





}