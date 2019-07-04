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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import wang.cutemusic.R;
import wang.cutemusic.activity.BaseActivity;
import wang.cutemusic.activity.MainActivity;
import wang.cutemusic.adpter.MusicListAdapter;
import wang.cutemusic.data.MusicInfo;
import wang.cutemusic.util.MusicUtil;

public class LocalMusicFragment extends Fragment {

	private static final String ARG_POSITION = "position";
	private static final String TAG = "本地音乐的Fragment";

	private int position;
	View mView;
	private ListView locallistview;
	private MusicListAdapter adpter;
	private List<MusicInfo> musics;

	public static LocalMusicFragment newInstance(int position) {
		LocalMusicFragment f = new LocalMusicFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}


	public interface MItemOnclickListener{

		public void MItemOnclick(int  i);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		musics=MusicUtil.getMp3Infos(getActivity());
		adpter=new MusicListAdapter(getActivity(),musics);
		Log.d(TAG, "onCreateView: 准备把得到的音乐集合放到listVIew中");
		if (mView == null) {
			mView = inflater.inflate(R.layout.fragment_localmusic,null);

		}
		locallistview= (ListView) mView.findViewById(R.id.local_listview);
		locallistview.setAdapter(adpter);
		locallistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				if (getActivity() instanceof MItemOnclickListener) {
					Log.d(TAG, "onItemClick: ID:"+i);
					((MItemOnclickListener) getActivity()).MItemOnclick(i);
				}
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
}