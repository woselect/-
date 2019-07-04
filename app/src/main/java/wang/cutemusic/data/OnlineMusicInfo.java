package wang.cutemusic.data;

public class OnlineMusicInfo {

    private String song_id;//音乐id
    private String ting_uid;//的ID
    private String title ;// 音乐标题
    private String artist;//歌手
    private String artistid;//歌手id
    private String albumId ;//专辑ID
    private long size;//文件大小
    private String url;//文件路径
    private String lrcLink;//歌词连接
    private long duration;//时长

    private String pic_90;
    private String pic_150;
    private String pic_300;
    private String pic_500;



    public OnlineMusicInfo() {
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


    public String  getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String  albumId) {
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


    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getTing_uid() {
        return ting_uid;
    }

    public void setTing_uid(String ting_uid) {
        this.ting_uid = ting_uid;
    }

    public String getArtistid() {
        return artistid;
    }

    public void setArtistid(String artistid) {
        this.artistid = artistid;
    }

    public String getLrcLink() {
        return lrcLink;
    }

    public void setLrcLink(String lrcLink) {
        this.lrcLink = lrcLink;
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
        return "OnlineMusicInfo{" +
                "song_id='" + song_id + '\'' +
                ", ting_uid='" + ting_uid + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", artistid='" + artistid + '\'' +
                ", albumId='" + albumId + '\'' +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", lrcLink='" + lrcLink + '\'' +
                ", duration=" + duration +
                ", pic_90='" + pic_90 + '\'' +
                ", pic_150='" + pic_150 + '\'' +
                ", pic_300='" + pic_300 + '\'' +
                ", pic_500='" + pic_500 + '\'' +
                '}';
    }
}
