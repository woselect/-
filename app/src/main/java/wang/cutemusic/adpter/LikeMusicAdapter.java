package wang.cutemusic.adpter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import wang.cutemusic.R;
import wang.cutemusic.data.MusicInfo;
import wang.cutemusic.data.PlayMusicInfo;
import wang.cutemusic.util.MusicUtil;

/**
 * 自定义的音乐列表适配器
 * 为了方便扩展，因为之前没有考虑到显示专辑封面
 * @author wwj
 *
 */
public class LikeMusicAdapter extends BaseAdapter{
    private Context context;		//上下文对象引用
    private List<PlayMusicInfo> MusicInfo;	//存放Mp3Info引用的集合
    private PlayMusicInfo music;		//Mp3Info对象引用
    private int pos = -1;			//列表位置



    /**
     * 构造函数
     * @param context	上下文
     * @param Infos  集合对象
     */
    public LikeMusicAdapter(Context context, List<PlayMusicInfo> Infos) {
        this.context = context;
        this.MusicInfo = Infos;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.music_list_item_layout, null);
            viewHolder.albumImage = (ImageView) convertView.findViewById(R.id.albumImage);
            viewHolder.musicTitle = (TextView) convertView.findViewById(R.id.music_title);
            viewHolder.musicArtist = (TextView) convertView.findViewById(R.id.music_Artist);
            viewHolder.musicDuration = (TextView) convertView.findViewById(R.id.music_duration);
            convertView.setTag(viewHolder);			//表示给View添加一个格外的数据，
        } else {
            viewHolder = (ViewHolder)convertView.getTag();//通过getTag的方法将数据取出来
        }
        music = MusicInfo.get(position);
        if(position == pos) {
            viewHolder.albumImage.setImageResource(R.drawable.muci);
        } else {
            //可能有问题3
            Bitmap bitmap = MusicUtil.getArtwork(context, music.getId(),music.getAlbumId(), true, true);
            viewHolder.albumImage.setImageBitmap(bitmap);
        }
        viewHolder.musicTitle.setText(music.getTitle());			//显示标题
        viewHolder.musicArtist.setText(music.getArtist());		//显示艺术家
        viewHolder.musicDuration.setText(MusicUtil.formatTime(music.getDuration()));//显示时长

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
        public TextView musicDuration;	//音乐时长
        public TextView musicArtist;	//音乐艺术家
    }
}
