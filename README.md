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
* 2017.05.26 今晚要回北京了~ 主要研究Activity Intent 之间传递问题 </br>
    > 如果需要调用 OTG 相关操作函数，需要引入库文件:</br>
    ``` java
    import com.github.mjdev.libaums.UsbMassStorageDevice;
    import com.github.mjdev.libaums.fs.FileSystem;
    import com.github.mjdev.libaums.fs.UsbFile;
    import com.github.mjdev.libaums.fs.UsbFileInputStream;
    import com.github.mjdev.libaums.fs.UsbFileOutputStream;
    import com.github.mjdev.libaums.partition.Partition;
    ```
    > Intent </br>
    > 研究 OTG USB 处理过程</br>
* 2017.05.29 今天要着重研究一下 `Activity` 与 `Intent` ;<br>
    > Intent 报错:**"Intent Error :Attempt to invoke virtual method 'android.app.ActivityThread$ApplicationThread android.app.ActivityThread.getApplicationThread()' on a null object reference"**<br>
    > 应该与线程相关<br>
* 2017.06.01 儿童节快乐~ 依旧研究 `Activity` 与 `Intent` ; 调试程序总是错在`startActivity(intent);`<br>
    > 解决方法1: 以`Fragment` 重新编写程序 仿照`USBTest`编写;<br>
    > 解决方法2: 研究线程以及`Intent`,争取调用USB程序<br>
* 2017.06.02 今日研究通Inetnt调用的问题了，原因是`Fragment`与`Activity` 调用 `Intent` 的方法不同<br>
    > 目前采用的方法是由主程序获取本机`SD`目录中的`documents`中的文件信息，由`USBManActivity`获取USB设备中的文件信息<br>
    > 文件浏览界面需要优化 `SeekBar` 部分需要优化<br>
    > 尝试直接从 OTG 中打开 txt 文件<br>
    
    > <div align=center><img  width="480" height="270" src="https://raw.githubusercontent.com/LeoLiu8023AmyLu/AX-EDP/master/ScreenCapture/device-2017-06-02-165211.png"/></div> </br>
    
* 2017.06.03 主要优先解决复制文件的操作问题<br>
    > `USBMainActivity` 中 设置复制路径为 `String filePath = sdPath + "/documents/" + uFile.getName();`<br>
    > 目前采用独立的`USBMainActivity`来完成优盘文件的复制，然后再从本机读取<br>
    > 完善文件类型判断程序<br>
    > `Intent` 传递 `USBFilePath` <br>
* 2017.06.05 完善显示界面SeekBar的细节问题;Txt编码还是不能全部识别<br>
    > `SeekBar`与上下翻页按钮 联动<br>
    > 更改 **txt** 的编码程序，采用`juniversalchardet.jar` **Google**提供的工具程序<br>
    > 点击 `TXT` 文件 自动打开浏览<br>
    > 文件主浏览界面 `SeekBar` UI调整为底部对齐,文字部分设置为'match_parent'<br>
* 2017.06.06 `Raspberry`  **RTAndroid** 测试不成功，原因未找到;三星运行没有问题<br>
    > 点击`Txt`文件 不能完成自动跳转，已禁用`Fragment`跳转<br>
* 2017.06.07 诊断程序完善细节<br>
    > 解决点击 `txt` 直接显示问题 <br>
    > 完善 `txt` 文件长度过短不足一页的问题 <br>
    > 分析程序中间报错信息，查找原因: IntentFilter 部分存在问题<br>
    > `Txt` 路径返回为空 情况的判断处理<br>
* 2017.06.08 继续诊断并完善程序细节<br>
    > 增加主显示页面 页码显示，删除`Toast`提示<br>
    > 尝试控制 `Thread` 进程<br>
    > 还原 `documents` 目录的更改，还原为`0` 根目录<br>
    > Android 7 需要为应用打开权限，因此权限提醒需要加入这个功能<br>
* 2017.06.09 今日主要任务为完善程序 <br>
    > 加入权限检测模块，在程序运行前检查 **SD及外部储存权限** ，并让用户选择权限<br>
    > 昨日的文件路径修改存在问题，今日修复问题： 路径后需要加入"\"<br>
    > 文件读取部分，加入 `try` `cathe` 结构，来让文件无法读取时可以不报错<br>
    > 录制视频<br>
* 2017.06.12 完善`FileManager`程序，为文件类型做好接口<br>
* 2017.06.14 完善 Intent 部分，加入相关的文件类型过滤器 <br>