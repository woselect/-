package wang.cutemusic.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wang.cutemusic.R;
import wang.cutemusic.activity.MainActivity;
import wang.cutemusic.data.MusicInfo;
import wang.cutemusic.data.OnlineMusicInfo;
import wang.cutemusic.data.PlayMusicInfo;
import wang.cutemusic.service.MusicService;
import wang.cutemusic.util.DownloadUtils;
import wang.cutemusic.util.MusicUtil;

public class OnlineDialogFragment extends DialogFragment {
    private static final String TAG ="dialog下载页面的";

    private ArrayList<OnlineMusicInfo> onlineMusicInfos ;
    private int curPosation;
    private MainActivity mainActivity;

    public static OnlineDialogFragment newInstance(ArrayList<OnlineMusicInfo> infos ,int posation){
        OnlineDialogFragment dialogFragment =new OnlineDialogFragment() ;
        dialogFragment.onlineMusicInfos = infos;
        dialogFragment.curPosation =posation;
        return dialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: 了");
        mainActivity  = (MainActivity) getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog builder = new AlertDialog.Builder(mainActivity)
                .setTitle("下载")
                .setMessage("您想要下载这首歌吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        downloadMusic();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).create();
                /*.setItems(menuItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0://播放
                                playMusic();
                                break;
                            case 1://下载
                                downloadMusic();
                                break;
                            case 2://取消
                                dialog.dismiss();
                                break;
                        }
                    }
                });*/
        return builder;
    }

    //下载
    private void downloadMusic() {
        DownloadUtils.getsInstance().setDownloadListener(new DownloadUtils.OnDownloadListener() {
            @Override
            public void onDownloadSucced(String mp3url) {
                Toast.makeText(mainActivity, "下载成功！"+mp3url,Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void onDownloadFaild(String error) {
                Toast.makeText(mainActivity, "下载失败！"+error,Toast.LENGTH_SHORT ).show();
            }
        }).download(onlineMusicInfos.get(curPosation));
    }



}
