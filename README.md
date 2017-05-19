# AX-EDP
安夏电子席卡 电子题词机版本 使用Android Studio 编写</br>

## 说明: </br>

* 项目采用 **小米平板2** 作为蓝本开发，分辨率为 **2048x1536** </br>
* 项目Android 版本使用 **25** 版本 **Android 7.1.1 (Nougat)** </br>


## 工作记录</br>

* 2017.05.18 搭建Android Studio 环境，安装Git，重新编写 **安夏电子席卡题词机项目分支** 程序</br>
<div align=center><img  width="533" height="400" src="https://raw.githubusercontent.com/LeoLiu8023AmyLu/AX-EDP/master/ScreenCapture/device-2017-05-18-164156.png"/></div></br>

主界面主要是显示**时间,日期**，另外两个按钮为**进入文件系统(蓝色)**，和**自动连接网络(第二阶段开发内容，目前无任何操作)**</br>

* 2017.05.19 更新 **Android Studio** 配置，**SDK** 更新;编写`TxtReader`,`ProcessTxt`,`MainPreView`及界面，File 文件输入的问题还是没有解决</br>

<div align=center><img  width="480" height="270" src="https://raw.githubusercontent.com/LeoLiu8023AmyLu/AX-EDP/master/ScreenCapture/device-2017-05-19-193455.png"/></div></br>

目前txt文本读取的是 `\raw\a.txt` 文件夹下的文件</br>
使用的是 `InputStream inputStream = getResources().openRawResource(R.raw.a);` 语句进行读取</br>
然后经过编码返回 `String` 类型字符串</br>
程序如下:</br>

```java
PreView_Text.setText(Main_string.substring(Page_Text_Num*(Text_Page-1),Page_Text_Num*Text_Page));
```