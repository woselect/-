package wang.cutemusic.data;

public class MusicInfo {

    private long id;//音乐id
    private long musicid;//应用内存储数据使用的ID
    private String title ;// 音乐标题
    private String artist;//歌手
    private String album;//专辑
    private long albumId ;//专辑ID
    private long duration;//时长
    private long size;//文件大小
    private String url;//文件路径
    private int isMusic;//是否音乐
    private int isFavorite;//是否为喜爱的音乐
    private int type;//音乐类型

    public static final int TYPE_LOCAL=0;
    public static final int TYPE_ONLINE=1;

    public MusicInfo() {
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

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
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
                ", isMusic=" + isMusic +
                ", isFavorite=" + isFavorite +
                ", type=" + type +
                '}';
    }


}
