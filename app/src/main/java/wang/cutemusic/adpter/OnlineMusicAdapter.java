package wang.cutemusic.adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;

import javax.net.ssl.ManagerFactoryParameters;

import wang.cutemusic.HPMusicApp;
import wang.cutemusic.R;
import wang.cutemusic.activity.MainActivity;
import wang.cutemusic.data.OnlineMusicInfo;
import wang.cutemusic.fragment.OnlineDialogFragment;

/**
 * 自定义的音乐列表适配器
 * 为了方便扩展，因为之前没有考虑到显示专辑封面
 * @author wwj
 *
 */
public class OnlineMusicAdapter extends BaseAdapter{
    private MainActivity context;		//上下文对象引用
    private ArrayList<OnlineMusicInfo> MusicInfo;	//存放Mp3Info引用的集合
    private OnlineMusicInfo music;		//Mp3Info对象引用
    private int pos = -1;			//列表位置



    /**
     * 构造函数
     * @param context	上下文
     * @param Infos  集合对象
     */
    public OnlineMusicAdapter(MainActivity context, ArrayList<OnlineMusicInfo> Infos) {
        this.context = context;
        this.MusicInfo = Infos;
    }

    public ArrayList<OnlineMusicInfo> getMusicInfo() {
        return MusicInfo;
    }

    public void setMusicInfo(ArrayList<OnlineMusicInfo> musicInfo) {
        MusicInfo = musicInfo;
    }

    @Override
    public int getCount() {
        return MusicInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return MusicInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.onlinemusic_list_item_layout, null);
            viewHolder.albumImage = (ImageView) convertView.findViewById(R.id.online_albumImage);
            viewHolder.musicTitle = (TextView) convertView.findViewById(R.id.online_music_title);
            viewHolder.musicArtist = (TextView) convertView.findViewById(R.id.online_music_Artist);
            viewHolder.xiazia = (ImageView) convertView.findViewById(R.id.online_music_xiazai);
            convertView.setTag(viewHolder);			//表示给View添加一个格外的数据，
        } else {
            viewHolder = (ViewHolder)convertView.getTag();//通过getTag的方法将数据取出来
        }
        music = MusicInfo.get(position);
        final ViewHolder finalViewHolder = viewHolder;
        final ViewHolder finalViewHolder1 = viewHolder;
        ImageRequest iconRequest = new ImageRequest(music.getPic_90(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                finalViewHolder.albumImage.setImageBitmap(bitmap);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //如错误
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.muci);
                finalViewHolder1.albumImage.setImageBitmap(bitmap);
            }
        });
        iconRequest.setTag("网络图标");
        HPMusicApp.getHttpQueues().add(iconRequest);
//        Log.d("网络适配器", "getView: 标题是："+music.getTitle());
        viewHolder.musicTitle.setText(music.getTitle());			//显示标题
        viewHolder.musicArtist.setText(music.getArtist());		//显示艺术家

        viewHolder.xiazia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("下载按钮", "onClick:准备显示Dialog！ ");
                /*DialogFragment dialog = new OnlineDialogFragment();
                dialog.show(context.getFragmentManager(), "aa");*/
                DialogFragment   Dialog= OnlineDialogFragment.newInstance(MusicInfo, position);
                Dialog.show(context.getSupportFragmentManager(), "xiazai");
            }
        });
        return convertView;
    }


    /**
     * 定义一个内部类
     * 声明相应的控件引用
     * @author wwj
     *
     */
    public class ViewHolder {
        //所有控件对象引用
        public ImageView albumImage;	//专辑图片
        public TextView musicTitle;		//音乐标题
        public ImageView xiazia;	//下载按钮
        public TextView musicArtist;	//音乐艺术家
    }
}
