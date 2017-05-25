# AX-EDP
安夏电子席卡 电子题词机版本 使用Android Studio 编写</br>

## 说明: </br>

* 项目采用 **小米平板2** 作为蓝本开发，分辨率为 **2048x1536** </br>
* 项目Android 版本使用 **25** 版本 **Android 7.1.1 (Nougat)** </br>

## 工作记录</br>

* 2017.05.18 搭建**Android Studio**环境，安装**Git**，重新编写 **安夏电子席卡题词机项目分支** 程序</br>
<div align=center><img  width="533" height="400" src="https://raw.githubusercontent.com/LeoLiu8023AmyLu/AX-EDP/master/ScreenCapture/device-2017-05-18-164156.png"/></div></br>

主界面主要是显示**时间,日期**，另外两个按钮为**进入文件系统(蓝色)**，和**自动连接网络(第二阶段开发内容，目前无任何操作)**</br>

* 2017.05.19 更新 **Android Studio** 配置，**SDK** 更新;编写`TxtReader`,`ProcessTxt`,`MainPreView`及界面，File 文件输入的问题还是没有解决</br>

<div align=center><img  width="480" height="270" src="https://raw.githubusercontent.com/LeoLiu8023AmyLu/AX-EDP/master/ScreenCapture/device-2017-05-19-193455.png"/></div></br>

>目前txt文本读取的是 `\raw\a.txt` 文件夹下的文件 </br>
>使用的是 `InputStream inputStream = getResources().openRawResource(R.raw.a);` 语句进行读取</br>
>然后经过编码返回 `String` 类型字符串</br>
>程序如下:</br>

```java
    PreView_Text.setText(Main_string.substring(Page_Text_Num*(Text_Page-1),Page_Text_Num*Text_Page));
```

* 2017.05.20 今天受到了朋友圈的各种伤害，于是我默默的去码代码了，Git一下，来平静一下我复杂的心情</br>
    > 更新程序注释，对比 `Txtreader`与`ProcessTxt`两个不同代码的区别，主要区别在文件的读取方式</br>
    
* 2017.05.21 研究 `FileManager` 的程序文件，学习**文件目录操作函数方法**</br>
* 2017.05.22 测试 `FileManager` 程序，发现读取`emulated`,`sdcard`等目录存在问题;需要查找问题</br>
    > 研究发现:获取**U盘挂载路径**，**U盘、SD卡等外接存储设备**的挂载，大部分都是在`/system/etc/vold.fstab`中指定了挂载路径，这么说就简单了吧；直接读取改文件，就可以知道U盘被挂载到哪个目录了，放心的是，基本上都再`/mnt`目录下</br>
    > 编写文件导入界面`file_manager` 页面文件</br>
* 2017.05.23 学习解决 `Thread` 线程问题，但是遇到了困难，无法控制线程暂停，从而影响资源;文件目录获取到并通过列表显示出来，目前需要优化</br>
    
    > Thread 控制有两种方法，一种是使用 `while(Flag)` 形式, `Flag` 作为标识符来控制 `while` 循环，但是存在问题。</br>
    > 使用列表 `ListView` 显示出来，点击事件可以获取到完整地址</br>

    > <div align=center><img  width="480" height="270" src="https://raw.githubusercontent.com/LeoLiu8023AmyLu/AX-EDP/master/ScreenCapture/device-2017-05-23-210644.png"/></div> </br>
    
    > 获取文件名通过 `HashMap` 的形式，这个列表随着 `path`的更新而更新</br>
    
    > <div align=center><img  width="480" height="270" src="https://raw.githubusercontent.com/LeoLiu8023AmyLu/AX-EDP/master/ScreenCapture/device-2017-05-23-210722.png"/></div> </br>
    
    > 将路径`path`与文件名组合在一起便可以得到完整的路径</br>
* 2017.05.24 加入文件类型判断程序 (判断`txt` 类型); 加入**OTG USB** 识别程序;</br>
    > 尝试解决线程问题，在标识符前加入 `static` 可以停止线程，但是不能有效**重启/继续**线程 </br>
    > OTG USB 研究了**Android**的相关包 </br>
* 2017.05.25 今日主要研究 `OTG` 读取 `USB` 优盘;另外线程部分进行简化，解决一部分问题;</br>
    > 线程问题在`Activity`的周期运行中可以有效控制，但是`Fragment`切换后，`Activity`依旧是运行状态，因此线程`Thread`依旧无法关闭 </br>
    > 添加简单的**txt**文字编码识别程序,以及 `Google` 推荐的字符编码识别程序 </br>
    > 关于`OTG`相关程序,查找到了 [GitHub libaums 方法](https://github.com/magnusja/libaums#using-buffered-streams-for-more-efficency)</br>
    > `Android` 插入**USB**设备自动检测，然后会自动启动**Android**的文件查看程序 = =||| </br>
    > 加入了 `OTG USB` 相关的代码，但是 Activity 之间的调用问题没能解决,**Intent**传递出现问题</br>