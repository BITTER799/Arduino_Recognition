package com.example.lenovo.arduinoforandroid;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;

public class ArudinoJudge extends AppCompatActivity {
    private ImageButton mCheck;
    private ImageButton mTakePhoto, mChoosePhoto;
    private ImageButton dictionary, help;
    private TextView Result;
    private ImageView picture;
    private Uri imageUri;
    TensorFlowInferenceInterface inferenceInterface1,inferenceInterface2,inferenceInterface3;
    private Bitmap testphoto;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private static final String TAG = "ArudinoJudge";
    private static final String MODEL_FILE_1 = "file:///android_asset/weights_2class.h5.pb";
    private static final String MODEL_FILE_2 = "file:///android_asset/weights_mcookie_12.h5.pb";
    private static final String MODEL_FILE_3 = "file:///android_asset/weights_7Class.h5.pb";
    private static final String INPUT_NAME="conv2d_1_input";
    //private static final String OUTPUT_NAME="dense_2/Softmax";
    private static final String OUTPUT_NAME="output_node0";

    static {
        //加载libtensorflow_inference.so库文件
        System.loadLibrary("tensorflow_inference");
        //System.loadLibrary("native-lib");//可以去掉
        Log.e(TAG,"libtensorflow_inference.so库加载成功");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.judge);
        mCheck=(ImageButton)findViewById(R.id.check) ;
        mTakePhoto = (ImageButton) findViewById(R.id.btn_take_photo);
        mChoosePhoto = (ImageButton) findViewById(R.id.choose_from_album);
        dictionary=(ImageButton)findViewById(R.id.dict);
        help=(ImageButton)findViewById(R.id.help);
        Result=(TextView)findViewById(R.id.result);
        Result.setText("\n 点击下方按钮开始识别");
        picture = (ImageView) findViewById(R.id.iv_picture);
        String StrUri=getIntent().getStringExtra("uri");
        imageUri=Uri.parse(StrUri);
        int type=getIntent().getIntExtra("type",1);

        inferenceInterface1 = new TensorFlowInferenceInterface(getAssets(),MODEL_FILE_1);
        Log.e(TAG,"TensoFlow模型文件加载成功");

        if(type==1) {
            openCamera();
        }
        else if (type==2){
            openAlbum();
        }


        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Result.setText(" \n 识别中……");
                    predict(testphoto);
                }
                catch (Exception e){

                }
            }
        });

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    imageUri = FileProvider.getUriForFile(ArudinoJudge.this,
                            "com.example.lenovo.arduinoforandroid", outputImage);
                }
                else {
                    imageUri = Uri.fromFile(outputImage);
                }

                openCamera();
            }
        });

        mChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
        });

        dictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ArudinoJudge.this,Encyclopedia.class);
                startActivityForResult(intent1,101);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ArudinoJudge.this);
                dialog.setTitle("使用说明");
                dialog.setMessage("请点击左侧两个按钮拍摄或选择想要检测的器件照片。拍摄照片时，请尽量将模块置于正中央充满屏幕；点击第三个按钮可前往器件百科了解器件详细信息");
                dialog.setPositiveButton("我已了解", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    //打开相册
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void openCamera(){
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        testphoto = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        testphoto = resizeBitmap(testphoto);
                        picture.setImageBitmap(testphoto);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {  //4.4及以上的系统使用这个方法处理图片；
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);  //4.4及以下的系统使用这个方法处理图片
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    /**
     * 4.4及以上的系统使用这个方法处理图片
     *
     * @param data
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果document类型的Uri,则通过document来处理
            String docID = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docID.split(":")[1];     //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;

                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/piblic_downloads"), Long.valueOf(docID));

                imagePath = getImagePath(contentUri, null);

            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则使用普通方式使用
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，直接获取路径即可
            imagePath = uri.getPath();

        }
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            testphoto = BitmapFactory.decodeFile(imagePath);
            testphoto=resizeBitmap(testphoto);
            picture.setImageBitmap(testphoto);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) 640) / width;
        float scaleHeight = ((float) 640) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        int wh = width > height ? height : width;// 裁切后所取的正方形区域边长
        int retX = width > height ? (width - height) / 2 : 0;// 基于原图，取正方形左上角x坐标
        int retY = width > height ? 0 : (height - width) / 2;
        //int centre[]=onMeasure();

        //Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, retX, retY, wh, wh,null,false);
        return resizedBitmap;
    }

    @SuppressLint("StaticFieldLeak")
    public void predict(final Bitmap bitmap){
        //Runs inference in background thread
        new AsyncTask<Integer,Integer,Integer>(){
            @Override
            protected Integer doInBackground(Integer ...params){

                //Resize the image into 128 x 128
                Bitmap resized_image = ImageUtils.processBitmap(bitmap,128);

                //Normalize the pixels
                float[] floatValues = ImageUtils.normalizeBitmap(resized_image, 128, 127.5f, 1.0f);

                //Pass input into the tensorflow
                inferenceInterface1.feed(INPUT_NAME,floatValues,1,128,128,3);

                //compute predictions0
                inferenceInterface1.run(new String[]{OUTPUT_NAME});

                //copy the output into the PREDICTIONS array
                //float[] PREDICTIONS = new float[33];
                float[] PREDICTIONS1 = new float[2];
                inferenceInterface1.fetch(OUTPUT_NAME,PREDICTIONS1);

                //Obtained highest prediction
                Object[] results = argmax(PREDICTIONS1);

                int class_index = (Integer) results[0];
                float confidence;
                int class_final;

                if(class_index==0){
                    inferenceInterface2 = new TensorFlowInferenceInterface(getAssets(),MODEL_FILE_2);
                    Log.e(TAG,"TensoFlow模型文件加载成功");
                    inferenceInterface2.feed(INPUT_NAME,floatValues,1,128,128,3);
                    inferenceInterface2.run(new String[]{OUTPUT_NAME});
                    float[] PREDICTIONS2 = new float[12];
                    inferenceInterface2.fetch(OUTPUT_NAME,PREDICTIONS2);
                    results = argmax(PREDICTIONS2);

                    class_final = (Integer) results[0];
                    confidence = (Float) results[1];
                    //Log.e(TAG, String.valueOf(class_final)+String.valueOf(confidence));
                }
                else{
                    inferenceInterface3 = new TensorFlowInferenceInterface(getAssets(),MODEL_FILE_3);
                    Log.e(TAG,"TensoFlow模型文件加载成功");
                    inferenceInterface3.feed(INPUT_NAME,floatValues,1,128,128,3);
                    inferenceInterface3.run(new String[]{OUTPUT_NAME});
                    float[] PREDICTIONS3 = new float[7];
                    inferenceInterface3.fetch(OUTPUT_NAME,PREDICTIONS3);
                    results = argmax(PREDICTIONS3);

                    class_final = (Integer) results[0];
                    confidence = (Float) results[1];
                }
                try{
                    final String conf = String.valueOf(confidence * 100).substring(0,5);
                    //Convert predicted class index into actual label name
                    //final String label = ImageUtils.getLabel(getAssets().open("labels.json"),class_index);
                    //Display result on UI
                    final String label = getlabel(class_final+1, class_index);
                    final int index=class_index;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Result.setText(" 识别结果："+index+'\n'+" "+label + "," + conf + "%"+'\n'+ " 进入器件百科了解更多");
                        }
                    });
                }
                catch (Exception e){

                }
                return 0;
            }
        }.execute(0);
    }


    //FUNCTION TO COMPUTE THE MAXIMUM PREDICTION AND ITS CONFIDENCE
    public Object[] argmax(float[] array){
        int best = -1;
        float best_confidence = 0.0f;

        for(int i = 0;i < array.length;i++){
            float value = array[i];
            if (value > best_confidence){
                best_confidence = value;
                best = i;
            }
        }
        return new Object[]{best,best_confidence};
    }

    private String getlabel(int number, int type){
        String label= "";
        if(type==0){
           switch(number){
               case 1:
                  label="Amplifier";
                  break;
               case 2:
                  label="Audio Pro";
                  break;
               case 3:
                  label="Audio Shield";
                  break;
               case 4:
                  label="Base";
                  break;
               case 5:
                  label="Buletooth";
                  break;
               case 6:
                  label="Core";
                  break;
               case 7:
                  label="Motor";
                  break;
               case 8:
                  label="OLED";
                  break;
               case 9:
                  label="RTC";
                  break;
               case 10:
                  label="USB-TTLC";
                  break;
               case 11:
                  label="WIFI";
                  break;
               case 12:
                  label="ZigBee";
                  break;
               default:
                  label="识别失败";
                  break;
           }
        }
        else {
            switch (number) {
                case 1:
                    label = "电机1";
                    break;
                case 2:
                    label = "电机2";
                    break;
                case 3:
                    label = "锂离子电池";
                    break;
                case 4:
                    label = "遥控器";
                    break;
                case 5:
                    label = "传感器";
                    break;
                case 6:
                    label = "Micro Servo";
                    break;
                case 7:
                    label = "Sensor Cable";
                    break;
                default:
                    label = "识别失败";
                    break;
            }
        }
        return label;
    }
}


