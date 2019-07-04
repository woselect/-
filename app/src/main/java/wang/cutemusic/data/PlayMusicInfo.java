package wang.cutemusic.data;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "playMusic")
public class PlayMusicInfo {

    @Column(name = "id")
    private long id;//音乐id

    @Column(name = "musicid" ,isId = true)
    private long musicid;//应用内存储数据使用的ID

    @Column(name = "onLineid")
    private long onLineid;//网络ID


    @Column(name = "title")
    private String title ;// 音乐标题

    @Column(name = "artistid")
    private String artistid;//歌手id

    @Column(name = "artist")
    private String artist;//歌手


    @Column(name = "album")
    private String album;//专辑图片

    @Column(name = "albumId")
    private long albumId ;//专辑ID

    @Column(name = "lrcLink")
    private long lrcLink ;//歌词连接


    @Column(name = "duration")
    private long duration;//时长

    @Column(name = "size")
    private long size;//文件大小

    @Column(name = "url")
    private String url;//文件路径



    @Column(name = "isFavorite")
    private int isFavorite;//是否为喜爱的音乐

    @Column(name = "musicid")
    private int type;//音乐类型0本地，1网络


    @Column(name = "pic_90")
    private String  pic_90;


    @Column(name = "pic_150")
    private String pic_150;


    @Column(name = "pic_300")
    private String pic_300;

    @Column(name = "pic_500")
    private String pic_500;

    @Column(name = "isMusic")
    private int isMusic;//是否音乐




    public static final int TYPE_LOCAL=0;
    public static final int TYPE_ONLINE=1;

    public PlayMusicInfo() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMusicid() {
        return musicid;
    }

    public void setMusicid(long musicid) {
        this.musicid = musicid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getOnLineid() {
        return onLineid;
    }

    public void setOnLineid(long onLineid) {
        this.onLineid = onLineid;
    }

    public String getArtistid() {
        return artistid;
    }

    public void setArtistid(String artistid) {
        this.artistid = artistid;
    }

    public long getLrcLink() {
        return lrcLink;
    }

    public void setLrcLink(long lrcLink) {
        this.lrcLink = lrcLink;
    }


    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    public String getPic_90() {
        return pic_90;
    }

    public void setPic_90(String pic_90) {
        this.pic_90 = pic_90;
    }

    public String getPic_150() {
        return pic_150;
    }

    public void setPic_150(String pic_150) {
        this.pic_150 = pic_150;
    }

    public String getPic_300() {
        return pic_300;
    }

    public void setPic_300(String pic_300) {
        this.pic_300 = pic_300;
    }

    public String getPic_500() {
        return pic_500;
    }

    public void setPic_500(String pic_500) {
        this.pic_500 = pic_500;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "id=" + id +
                ", musicid=" + musicid +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", isFavorite=" + isFavorite +
                ", type=" + type +
                '}';
    }


}
