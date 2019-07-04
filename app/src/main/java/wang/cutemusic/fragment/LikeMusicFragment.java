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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import wang.cutemusic.HPMusicApp;
import wang.cutemusic.R;
import wang.cutemusic.activity.MainActivity;
import wang.cutemusic.adpter.LikeMusicAdapter;
import wang.cutemusic.adpter.MusicListAdapter;
import wang.cutemusic.data.MusicInfo;
import wang.cutemusic.data.PlayMusicInfo;
import wang.cutemusic.util.MusicUtil;

public class LikeMusicFragment extends Fragment {

	private static final String TAG = "本地音乐的Fragment";

	View mView;


	//以下的
	private ListView listView_like;
	private HPMusicApp app;
	private ArrayList<PlayMusicInfo> likemusicInfos;
	private LikeMusicAdapter adapter;
	private boolean isChange = false;//表示当前播放列表是否为收藏列表

	DbManager db;
	private TextView like_is_kong;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		app = (HPMusicApp) mainActivity.getApplication();
		Log.d(TAG, "onCreateView: 准备把得到的音乐集合放到listVIew中");
		if (mView == null) {
			mView = inflater.inflate(R.layout.activity_like_music,null);

		}
		listView_like= (ListView) mView.findViewById(R.id.listView_like);
		like_is_kong= (TextView) mView.findViewById(R.id.like_is_kong);
		initData();



		listView_like.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if (mainActivity.service.getPlayList() != mainActivity.service.LIKE_MUSIC_LIST){
					mainActivity.service.setMusicInfo(likemusicInfos);//播放列表切换为收藏列表
					mainActivity.service.setPlayList(mainActivity.service.LIKE_MUSIC_LIST);
				}
				mainActivity.service.play(i);
			}
		});
		return mView;
	}

	MainActivity mainActivity;
	@Override
	public void onAttach(Activity activity) {

		super.onAttach(activity);
		mainActivity = (MainActivity) activity;
	}



	private void initData() {
		try {
			db= x.getDb(app.daoConfig);
			likemusicInfos = (ArrayList<PlayMusicInfo>) db.selector(PlayMusicInfo.class).where("isFavorite","=",1).findAll();
			if(likemusicInfos ==null || likemusicInfos.size() ==0){
				Log.d(TAG, "initData: 喜欢的音乐为空");
				like_is_kong.setVisibility(View.VISIBLE);
				return;
			}
			like_is_kong.setVisibility(View.GONE);
			Log.d(TAG, "initData: 喜欢的音乐第一首标题为："+likemusicInfos.get(0).getTitle());
			adapter = new LikeMusicAdapter(mainActivity,likemusicInfos);
			listView_like.setAdapter(adapter);
		} catch (DbException e) {
			e.printStackTrace();
		}


	}


}