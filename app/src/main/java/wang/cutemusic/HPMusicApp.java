package wang.cutemusic;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.xutils.DbManager;
import org.xutils.common.util.FileUtil;
import org.xutils.db.table.TableEntity;
import org.xutils.x;

import java.io.File;

import wang.cutemusic.util.Constant;

public class HPMusicApp extends Application {

    public static SharedPreferences sharedPreferences;
    public static DbManager.DaoConfig daoConfig;

    public static Context context;

    private static RequestQueue queue ;


    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences=getSharedPreferences("shuju", Context.MODE_PRIVATE);

        x.Ext.init(this);
//        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

        daoConfig=new DbManager.DaoConfig()
                .setDbName(Constant.DB_NAME)
                .setDbDir(new File(String.valueOf(Environment.getExternalStorageDirectory())+"/Wang"))//这个类没有
                .setDbVersion(1)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                    }
                })
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                        Log.d("HP帮助类", "onTableCreated: "+table.getName());
                    }
                });
        context = getApplicationContext();
        queue = Volley.newRequestQueue(getApplicationContext());
    }

    public static RequestQueue getHttpQueues(){
        return queue;
    }
}
