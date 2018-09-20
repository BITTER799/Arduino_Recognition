package com.example.lenovo.arduinoforandroid;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private GridView mgridView;
    private Uri imageUri;
    private int[] pictures=new int[]{R.drawable.takephoto,R.drawable.album,R.drawable.book,R.drawable.help};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mgridView = super.findViewById(R.id.mgridview);
        mgridView.setAdapter(new ImageAdapter(this));

        mgridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                switch (arg2){
                    case 0:
                        //创建file对象，用于存储拍照后的图片；
                        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");

                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(Build.VERSION.SDK_INT>=23){
                            imageUri = FileProvider.getUriForFile(MainActivity.this,
                                    "com.example.lenovo.arduinoforandroid", outputImage);
                        }
                        else {
                            imageUri = Uri.fromFile(outputImage);
                        }

                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                        }else{
                            Intent predict=new Intent(MainActivity.this,ArudinoJudge.class);
                            predict.putExtra("uri",imageUri.toString());
                            predict.putExtra("type",1);
                            startActivity(predict);
                            //openCamera();
                        }
                        break;
                    case 1:
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            Intent predict=new Intent(MainActivity.this,ArudinoJudge.class);
                            predict.putExtra("uri","");
                            predict.putExtra("type",2);
                            startActivity(predict);
                            //openAlbum();
                        }
                        break;
                    case 2:
                        Intent intent1=new Intent(MainActivity.this,Encyclopedia.class);
                        startActivityForResult(intent1,101);
                        break;
                    case 3:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle("使用说明");
                        dialog.setMessage("1.程序提供模块的识别功能，请点击按钮直接拍摄或从相簿选择想要检测的器件照片。拍摄照片时，请尽量将模块置于正中央充满屏幕；\n" +
                                "2.程序提供mcookie器件的介绍，请点击器件百科了解相关信息");
                        dialog.setPositiveButton("我已了解", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        dialog.show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public class ImageAdapter extends BaseAdapter        //设置图片适配器，继承自BaseAdapter
    {

        private Context mContext;
        public ImageAdapter(Context c)
        {
            mContext = c;
        }

        @Override
        public int getCount() {
            return pictures.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {     //在这个方法中设置一个新的ImageView，用来显示图片，
            // 并且通过适配器将图片加载到新的ImageView中
            ImageView imageView;
            if (convertView == null)
            {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(550, 550));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);        //设置图片的缩放方式（CENTER_DROP：通过图片的纵横比来设置图片并且覆盖住整个ImageView）
            }
            else
            {
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(pictures[position]);
            return imageView;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                    Intent predict=new Intent(MainActivity.this,ArudinoJudge.class);
                    predict.putExtra("uri",imageUri.toString());
                    predict.putExtra("type",1);
                    startActivity(predict);
                    //openCamera();
                }
                else {
                    // 判断用户是否点击了不再提醒。(检测该权限是否还可以申请)
                    //boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    // 提示用户去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent predict=new Intent(MainActivity.this,ArudinoJudge.class);
                    predict.putExtra("uri",imageUri.toString());
                    predict.putExtra("type",2);
                    startActivity(predict);
                    //openAlbum();
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void showDialogTipUserGoToAppSettting() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("摄像头权限不可用");
        dialog.setMessage("请在-应用设置-权限-中，允许程序使用摄像头和存储权限");
        dialog.setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                // 跳转到应用设置界面
                goToAppSetting();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if(Build.VERSION.SDK_INT>=23) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        showDialogTipUserGoToAppSettting();
                    } else {
                        Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    //获取屏幕高度宽度
    public int[] onMeasure() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;
        int locate[]=new int[2];
        locate[0] = screenWidth / 2;
        locate[1] = screenHeigh / 2;
        return locate;
    }
}
