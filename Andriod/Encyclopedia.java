package com.example.lenovo.arduinoforandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Encyclopedia extends AppCompatActivity {
    private GridView gridView;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dictlayout);
        gridView = (GridView) findViewById(R.id.gridview);

        //初始化数据
        initData();

        String[] from={"img","text"};

        int[] to={R.id.img,R.id.text};

        adapter=new SimpleAdapter(this, dataList, R.layout.toollist, from, to);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                AlertDialog.Builder builder= new AlertDialog.Builder(Encyclopedia.this);
                builder.setTitle("器件说明");
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.tooldialog,null);
                TextView text = (TextView) layout.findViewById(R.id.text);
                TextView name =(TextView)layout.findViewById(R.id.toolname);
                text.setText(GetIntro(arg2+1));
                name.setText(Getname(arg2));
                ImageView image = (ImageView) layout.findViewById(R.id.image);
                //image.setImageResource(R.mipmap.ic_launcher);
                image.setImageResource(Getpic(arg2+1));
                builder.setView(layout);
                builder.setPositiveButton("我已了解", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    void initData() {
        //图标
        int icno[] = { R.drawable.tob1,R.drawable.tob2,R.drawable.tob3,R.drawable.tob4,R.drawable.tob5,R.drawable.tob6,R.drawable.tob7,R.drawable.tob8,
                       R.drawable.tob9,R.drawable.tob10,R.drawable.tob11,R.drawable.tob12,R.drawable.tob13,R.drawable.tob14,R.drawable.tob15,R.drawable.tob16,
                       R.drawable.tob17,R.drawable.tob18,R.drawable.tob19,R.drawable.tob20,R.drawable.tob21,R.drawable.tob22,R.drawable.tob23,R.drawable.tob24,
                       R.drawable.tob25,R.drawable.tob26,R.drawable.tob27,R.drawable.tob28,R.drawable.tob29,R.drawable.tob30,R.drawable.tob31,R.drawable.tob32,
                       R.drawable.tob33 };
        //图标下的文字
        String name[]={"电机1","电机2","锂离子电池","遥控器","Amplifier","Audio Pro","Audio Shield","Base","Buletooth","BUZZER",
                       "COLOR.LED","Core","CRASH","IO Split","IR.RECIEVOR","IR.SENSOR","JOYSTICK","LIGHT-A1","LINE.FINDER","MIC",
                       "Micro Servo","Motor","OLED","PIR","RTC","Sensor Cable1","Sensor Cable2","TEM&HUM-S2","TOUCH.BUT","USB开关",
                       "USBTTLC","WIFI","ZigBee"};
        dataList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i <icno.length; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("img", icno[i]);
            map.put("text",name[i]);
            dataList.add(map);
        }
    }

    private String Getname(int i){
        String label= "";
        switch (i){
            case 0:
                label="电机1";
                break;
            case 1:
                label="电机2";
                break;
            case 2:
                label="锂离子电池";
                break;
            case 3:
                label="遥控器";
                break;
            case 4:
                label="Amplifier";
                break;
            case 5:
                label="Audio Pro";
                break;
            case 6:
                label="Audio Shield";
                break;
            case 7:
                label="Base";
                break;
            case 8:
                label="Buletooth";
                break;
            case 9:
                label="BUZZER";
                break;
            case 10:
                label="COLOR.LED";
                break;
            case 11:
                label="Core";
                break;
            case 12:
                label="CRASH";
                break;
            case 13:
                label="IO Split";
                break;
            case 14:
                label="IR.RECIEVOR";
                break;
            case 15:
                label="IR.SENSOR";
                break;
            case 16:
                label="JOYSTICK";
                break;
            case 17:
                label="LIGHT-A1";
                break;
            case 18:
                label="LINE.FINDER";
                break;
            case 19:
                label="MIC";
                break;
            case 20:
                label="Micro Servo";
                break;
            case 21:
                label="Motor";
                break;
            case 22:
                label="OLED";
                break;
            case 23:
                label="PIR";
                break;
            case 24:
                label="RTC";
                break;
            case 25:
                label="Sensor Cable1";
                break;
            case 26:
                label="Sensor Cable2";
                break;
            case 27:
                label="TEM&HUM-S2";
                break;
            case 28:
                label="TOUCH.BUT";
                break;
            case 29:
                label="USB开关";
                break;
            case 30:
                label="USBTTLC";
                break;
            case 31:
                label="WIFI";
                break;
            case 32:
                label="ZigBee";
                break;
            default:
                break;
        }
        return label;
    }
    private String GetIntro(int i){
        String introdution="";
        switch (i){
            case 1:
                introdution = "电机1用于控制机械的旋转";break;
            case 2:
                introdution = "电机2配有扇叶，可组装一个小电扇";break;
            case 3:
                introdution = "在不通过USB连接电脑时，电池可为模块提供运行所需要的电力"; break;
            case 4:
                introdution = "遥控器可远程控制模块运行"; break;
            case 5:
                introdution = "模块为双声道音频放大模块，相当于一个小音箱";break;
            case 6:
                introdution = "以VS1053为解码器，集成2.2W的立体声功放，并且支持2.5mm立体声耳机接口的多功能音频模块"; break;
            case 7:
                introdution = "音频播放模块,采用串口通讯，利用存储卡将音乐放在Audio shield上"; break;
            case 8:
                introdution = "一款单节锂电池升压到5V输出、LDO到3.3V输出的放电管理模块，同时集成了Hub传感器接口"; break;
            case 9:
                introdution = "蓝牙低能耗 (BLE) 的串口传输模块。可与其他蓝牙设备进行连接通讯"; break;
            case 10:
                introdution = "无源蜂鸣器，和电磁扬声器一样，需要高低变化不同频率的电压才能发声"; break;
            case 11:
                introdution = "全彩LED灯，采用单线串行级联协议；只需一个I/O口就可以控制线路上每个彩灯的RGB颜色；供电支持的情况下，最多支持1024个彩灯的级联"; break;
            case 12:
                introdution = "以Atmel ATmega328P系列为核心的8位单片机开发核心板，是一个开源的控制器模块"; break;
            case 13:
                introdution = "碰撞传感器，用来检测是否发生碰撞，也可称为碰撞信号传感器，可做按键开关、限位开关用"; break;
            case 14:
                introdution = "IO分线模块，可将一个4PIN接口分成2个4PIN接口";break;
            case 15:
                introdution = "红外接收传感器模块，它主要包含一个将红外线光信号变成电信号的半导体器件"; break;
            case 16:
                introdution = "红外发射传感器模块，发射红外信号"; break;
            case 17:
                introdution = "旋转电位器模块，通过旋钮控制给模块提供不同的电压";break;
            case 18:
                introdution = "光传感器是利用光敏电阻将光信号转换为电信号的传感器，它的敏感波长在可见光波长附近，包括红外线波长和紫外线波长"; break;
            case 19:
                introdution = "红外反射传感器模块，可用于循迹"; break;
            case 20:
                introdution = "声音检测传感器模块，即麦克风"; break;
            case 21:
                introdution = "舵机旋钮可进行180度旋转，进行位置控制，易损坏"; break;
            case 22:
                introdution = "一款直流电机（马达）控制器，具有脉宽调制，可通过PWM控制电机速度"; break;
            case 23:
                introdution = "显示模块，屏幕0.96寸，分辨率128*64像素点。采用IIC驱动，可以直接叠加在mCookie的大多数模块上与核心连接通讯。"; break;
            case 24:
                introdution = "红外热释电运动传感器能检测运动的人或动物身上发出的红外线，输出开关信号，可以应用于各种需要检测运动人体的场合"; break;
            case 25:
                introdution = "时钟模块，可获取时间，采用IIC接口通信。 超级电容提供了一定的掉电计时能力，断电后时钟芯片还可运行"; break;
            case 26:
                introdution = "该连接线提供传感器与Base板之间的连接"; break;
            case 27:
                introdution = "该连接线提供面包板与传感器或Base板之间的连接"; break;
            case 28:
                introdution = "数字温湿度传感器模块，能同时检测温度，湿度。采用IIC通信。适用于环境温度检测，可作为温控、湿控开关等设备"; break;
            case 29:
                introdution = "电容式触摸按键感器模块，用来检测是否触摸，传感器的金色区域为触摸点，可做按键开关开关用"; break;
            case 30:
                introdution = "USB开关可作为电源开关使用，决定是否为模块提供运行的电力"; break;
            case 31:
                introdution = "基于CH340的USB总线的转接芯片，可以直接与 mCookie-Core相连，实现与计算机通讯，可以用来下载程序，也可用来串口调试"; break;
            case 32:
                introdution = "WiFi通讯模块，通过串口与核心模块通讯，可连接其他WiFi设备"; break;
            case 33:
                introdution = "采用CC2530芯片，主要用于距离短、功耗低且传输速率不高的各种电子设备之间进行数据传输以及典型的有周期性数据、间歇性数据和低反应时间数据传输"; break;
            case 0:
            default:
                introdution = "";	break;
        }

        return introdution;
    }

    private int Getpic(int i){
        int id=0;
        switch (i){
            case 1:
                id = R.drawable.to1;break;
            case 2:
                id = R.drawable.to2;break;
            case 3:
                id = R.drawable.to3; break;
            case 4:
                id = R.drawable.to4; break;
            case 5:
                id = R.drawable.to5;break;
            case 6:
                id = R.drawable.to6; break;
            case 7:
                id = R.drawable.to7; break;
            case 8:
                id = R.drawable.to8; break;
            case 9:
                id = R.drawable.to9; break;
            case 10:
                id = R.drawable.to10; break;
            case 11:
                id = R.drawable.to11; break;
            case 12:
                id = R.drawable.to12; break;
            case 13:
                id = R.drawable.to13; break;
            case 14:
                id = R.drawable.to14;break;
            case 15:
                id = R.drawable.to15; break;
            case 16:
                id = R.drawable.to16; break;
            case 17:
                id = R.drawable.to17;break;
            case 18:
                id = R.drawable.to18; break;
            case 19:
                id = R.drawable.to19; break;
            case 20:
                id = R.drawable.to20; break;
            case 21:
                id = R.drawable.to21; break;
            case 22:
                id = R.drawable.to22; break;
            case 23:
                id = R.drawable.to23; break;
            case 24:
                id = R.drawable.to24; break;
            case 25:
                id = R.drawable.to25; break;
            case 26:
                id = R.drawable.to26; break;
            case 27:
                id = R.drawable.to27; break;
            case 28:
                id = R.drawable.to28; break;
            case 29:
                id = R.drawable.to29; break;
            case 30:
                id = R.drawable.to30; break;
            case 31:
                id = R.drawable.to31; break;
            case 32:
                id = R.drawable.to32; break;
            case 33:
                id = R.drawable.to33; break;
            case 0:
            default:
                id = 0;	break;
        }
        return id;
    }
}
