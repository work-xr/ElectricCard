ElectricCard  基于9820A-Android4.4  
基本功能测试已完成, 已经考虑到断网再连网, 关机再开机, 关机换卡, 恢复出厂设置, 升级软件等情况

#### 事项:  
* 查询电子保卡是否激活暗码: `*#8888*#`  
* 清除电子保卡激活标志暗码: `*#8888*8888*#`
* 开机联网会根据SIM识别运营商, 然后设定GSM手机或电信手机激活的持续时长和累计时长
* 设置定时服务运行时间间隔: Constant.java->SERVICE_STARTUP_INTERVAL(默认一分钟)
* 累积时长: 一次开机联网时长(默认1小时) Constant.java-> GSM_PHONE_ACCUMULATED_DURATION
* 持续时长: 多次开机联网时长相加(默认1小时) Constant.java-> GSM_PHONE_CONSISTENT_DURATION

#### 需求:  
GMS手机电子保卡需求  
1. 持续识SIM卡24小时和累积识SIM卡6小时(a. 有设置每天22:00关机 08:00开机  b. 实卡待机不到6小时有更换有效SIM卡后两张卡时间相加满6小时)后收到信息, 输入指令`*#8888*#`弹出`****年**月**日**时**分 已激活`, 其中日期, 时间指手机自动识别到SIM卡累计时间超过6小时后收到的第一条信息的运营商系统时间, 如识卡累计6小时收到第一条信息时间是2017年12月22日16时15分, 输入指令`*#8888*#`弹出`2017年12月22日16时15分 已激活`
2. 当手机识别到有SIM卡累积时间不到6小时, 输入指令`*#8888*#`弹出`****年**月**日**时**分 已激活`, 没有任何日期和时间
3. 当累积识卡满6小时后收到信息登记激活时间, 收到短信后输入指令就能查看电子保卡时间,这个时间只要记录后不能改变,记录的激活时间可以用对应的指令或者META工具和清除指令清除,或者用Flashtool全格清除
4. 用清除指令 `*#8888*8888*#`清除电子保卡记录后重新激活,激活条件要跟第一次激活一样
5. 任何时刻这个查询的结果用多路下载工具,采用"不备份/还原校准参数+格式化"下载软件时不能丢失

电信手机电子保卡需求  
1. 持续识SIM卡6小时和累积识SIM卡6小时(有设置每天22:00关机 08:00开机)后收到信息, 输入指令`*#8888*#`弹出`****年**月**日**时**分 已激活`, 其中日期, 时间指手机自动识别到SIM卡累计时间超过6小时后收到的第一条信息的运营商系统时间, 如识卡累计6小时收到第一条信息时间是2017年12月22日16时15分, 输入指令`*#8888*#`弹出`2017年12月22日16时15分 已激活`
2. 当手机识别到有SIM卡累积时间不到6小时, 输入指令`*#8888*#`弹出`****年**月**日**时**分 已激活`, 没有任何日期和时间
3. 电子保卡快速测试指令`* #0809#"`,打开收到信息登记激活时间功能,收到短信后输入指令就能查看激活的电子保卡时间,即收到短信的运营商系统时间
4. 只要激活了电子保卡功能, 记录的激活时间就不能再改变,只能用清除指令 `*#8888*8888*#`清除

#### 步骤:
1. 应用ElectricCardApp启动, onCreate被调用
2. 收到开机广播, 将运营商信息清空, 从文件`/productinfo/electriccard.conf`读取电子保卡激活状态, 第一次开机该文件不存在, 进入3, 如果文件存在, 则说明仅激活标记或激活标记和激活日期都已写入, 如果仅有激活标记, 则停止定时服务并等待接收短信广播, 如果同时有激活日期, 则停止定时服务
3. 开启定时服务, 循环执行以下4-8步骤
4. 运营商信息是否是空, 是则读取其信息,根据移动, 联通, 电信设定累积时长和持续时长
5. 读取电子保卡激活状态, 如果没有激活则进行下一步
6. 判断当前的累计时长+目前联网时长是否大于预定累计时长, 当前的持续时长+目前联网时长是否大于预定持续时长, 是则转下一步
7. 将累计时长和持续时长更新后写入preference, 转13
8. 将激活状态写入文件, 激活日期还是为空
9. 收到联网广播, 将本次联网的开始时间写入preference
10. 如果收到关机广播, 清空本次开机联网时长, 清空读取的运营商信息
11. 如果收到断网广播, 将累计时长和持续时长更新后写入preference, 将联网开始时间初始化为0
12. 如果收到信息广播, 判断电子保卡是否已经激活, 如果激活并且激活日期为空的时候, 读取短信内容, 如果来自运营商短信中心, 则将激活标记和激活日期写入文件, 转下一步
13. 停止定时服务
14. 如果通过暗码清除了激活标记, 则将开始联网时间, 累计时长, 持续时长, 运营商信息清空, 将文件`/productinfo/electriccard.conf`删除, 重新开启定时服务, 转3

#### 问题1: 新建文件失败
```
Android Exception : java.io.IOException: open failed: EACCES (Permission denied)
```
原因: 缺乏系统权限  
解决方案:
除了在AndroidManifest.xml添加权限外, 还要加上`android:sharedUserId="android.uid.system"`
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.hsf1002.sky.electriccard"
    android:sharedUserId="android.uid.system"
    coreApp="true">

<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

#### 问题2: 监听网络状态变化的广播失败
原因: Android各个版本可能有差异  
解决方案: 把静态广播改为动态广播进行监听

#### 问题3: 用SharePreference同时写入两个值偶现失败
需要同时将持续时长和累积时长通过SharePreference写入, 有时候会无法写入成功, 导致这两个数据没有更新  
原因: 两个操作同时写同一个文件, apply是将修改数据原子提交到内存, 而后异步真正提交到硬件磁盘, 而commit是同步的提交到硬件磁盘  
解决方案: 用commit代替apply  
```
public void putLong(String key, long value)
{
    boolean result;
    editor.putLong(key, value);
    //editor.apply();
    result = editor.commit();
    Log.d("electriccard", "putLong result = " + result);
}
```
