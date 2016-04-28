# StackLayout
一个自定义层叠layout
#一.效果

1.层叠显示，通过xml属性可控制Y轴偏移量，X轴偏移量，缩放比例。
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="StackLayout">
        //Y轴方向的偏移量，负数向上偏移，正数向下偏移
        <attr name="offsetY" format="dimension"/>
        //X轴方向的偏移量，负数向左偏移，正数向右偏移
        <attr name="offseetScale" format="integer"/>
        //缩放比的偏移量，范围为0-100，
        <attr name="offsetX" format="dimension"/>
    </declare-styleable>
</resources>
```

2.可拖动，自动复位。拖动时有动画效果。

![这里写图片描述](http://img.blog.csdn.net/20160428150553361)

3.支持通过调用函数的方式飞出，且方向可以自定义。

```
//函数声明
   /**
     * 自动飞出
     * @param  left true表示从左边飞出，否则从右边
     * @param up true表示从上边放飞出，否则从下边飞出
     */
    public void takeOff(boolean left,boolean up){
        if(getChildCount()!=0){
            mSelectIndex=getChildCount()-1;
            autoDismissOrRestore(left?-2000:2000,up?-2000:2000);
        }

    }
  //使用
  @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                gallery.takeOff(true,true);
                break;
            case R.id.btn2:
                gallery.takeOff(true,false);
                break;
            case R.id.btn3:
                gallery.takeOff(false,true);
                break;
            case R.id.btn4:
                gallery.takeOff(false,false);
                break;
        }

    }
```
效果

![这里写图片描述](http://img.blog.csdn.net/20160428151237940)

4。支持以adapter的方式使用，也支持直接布局
apdater方式的布局文件：
```
    <com.zhuguohui.learn.StackLayout
        android:id="@+id/gallery"
        app:offsetY="-20dp"
        app:offsetX="-20dp"
        app:offseetScale="5"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_centerInParent="true">
    </com.zhuguohui.learn.StackLayout>
```
代码：
```
    gallery= (StackLayout) findViewById(R.id.gallery);
    adapter=new ImageAdapter(this,rid);
    gallery.setAdapter(adapter);
```
直接布局方式

```
  <com.zhuguohui.learn.StackLayout
        android:id="@+id/gallery"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_centerInParent="true"
        app:offseetScale="5"
        app:offsetX="-20dp"
        app:offsetY="-20dp">
        <ImageView
            android:layout_width="300dp"
            android:layout_height="200dp"
            android:src="@drawable/image1" />
        <ImageView
            android:layout_width="300dp"
            android:layout_height="200dp"
            android:src="@drawable/image2" />
        <ImageView
            android:layout_width="300dp"
            android:layout_height="200dp"
            android:src="@drawable/image3" />

        <ImageView
            android:layout_width="300dp"
            android:layout_height="200dp"
            android:src="@drawable/image4" />
    </com.zhuguohui.learn.StackLayout>
```
