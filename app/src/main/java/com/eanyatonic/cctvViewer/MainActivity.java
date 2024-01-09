package com.eanyatonic.cctvViewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private View mCustomView;
    private FrameLayout mFrameLayout;
    private IX5WebChromeClient.CustomViewCallback mCustomViewCallback;
    private com.tencent.smtt.sdk.WebView webView; // 导入 X5 WebView

    private String[] liveUrls = {
            "https://tv.cctv.com/live/cctv1/",
            "https://tv.cctv.com/live/cctv2/",
            "https://tv.cctv.com/live/cctv3/",
            "https://tv.cctv.com/live/cctv4/",
            "https://tv.cctv.com/live/cctv5/",
            "https://tv.cctv.com/live/cctv6/",
            "https://tv.cctv.com/live/cctv7/",
            "https://tv.cctv.com/live/cctv8/",
            "https://tv.cctv.com/live/cctvjilu",
            "https://tv.cctv.com/live/cctv10/",
            "https://tv.cctv.com/live/cctv11/",
            "https://tv.cctv.com/live/cctv12/",
            "https://tv.cctv.com/live/cctv13/",
            "https://tv.cctv.com/live/cctvchild",
            "https://tv.cctv.com/live/cctv15/",
            "https://tv.cctv.com/live/cctv16/",
            "https://tv.cctv.com/live/cctv17/",
            "https://tv.cctv.com/live/cctv5plus/",
            "https://tv.cctv.com/live/cctveurope",
            "https://tv.cctv.com/live/cctvamerica/",
    };

    private String backwardScript =
            """
                    function simulate(element, eventName) {
                        var options = extend(defaultOptions, arguments[2] || {});
                        var oEvent, eventType = null;
                                
                        for (var name in eventMatchers) {
                            if (eventMatchers[name].test(eventName)) {
                                eventType = name;
                                break;
                            }
                        }
                                
                        if (!eventType) throw new SyntaxError('Only HTMLEvents and MouseEvents interfaces are supported');
                                
                        if (document.createEvent) {
                            oEvent = document.createEvent(eventType);
                            if (eventType == 'HTMLEvents') {
                                oEvent.initEvent(eventName, options.bubbles, options.cancelable);
                            } else {
                                oEvent.initMouseEvent(eventName, options.bubbles, options.cancelable, document.defaultView, options.button, options.pointerX, options.pointerY, options.pointerX, options.pointerY, options.ctrlKey, options.altKey, options.shiftKey, options.metaKey, options.button, element);
                            }
                            element.dispatchEvent(oEvent);
                        } else {
                            options.clientX = options.pointerX;
                            options.clientY = options.pointerY;
                            var evt = document.createEventObject();
                            oEvent = extend(evt, options);
                            element.fireEvent('on' + eventName, oEvent);
                        }
                        return element;
                    }
                                
                    function extend(destination, source) {
                        for (var property in source) destination[property] = source[property];
                        return destination;
                    }
                                
                    var eventMatchers = {
                        'HTMLEvents': /^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,
                        'MouseEvents': /^(?:click|dblclick|mouse(?:down|up|over|move|out))$/
                    }
                    var defaultOptions = {
                        pointerX: 0,
                        pointerY: 0,
                        button: 0,
                        ctrlKey: false,
                        altKey: false,
                        shiftKey: false,
                        metaKey: false,
                        bubbles: true,
                        cancelable: true
                    }
                                
                    function triggerMouseEvent (node, eventType) {
                        var clickEvent = document.createEvent ('MouseEvents');
                        clickEvent.initEvent (eventType, true, true);
                        node.dispatchEvent (clickEvent);
                    };
                                
                    function mouseDragStart(node) {
                        console.log("Starting drag...");
                        console.log(node.offsetTop);
                        console.log(node.offsetLeft);
                        triggerMouseEvent(node, "mousedown")
                    }
                                
                    function mouseDragEnd(node){
                        console.log("Ending drag...");
                        const rect = node.getBoundingClientRect();
                        document.querySelector("#epg_right_shift_player").click()
                        simulate(node, "mousemove" , {pointerX: rect.x-3 , pointerY: node.offsetTop})
                        simulate(node, "mouseup" , {pointerX:  rect.x-3, pointerY: node.offsetTop})
                    }
                    function sleep1(time) {
                        time*=1000
                        return new Promise(resolve => {
                            setTimeout(() => {
                                resolve();
                            }, time);
                        });
                    }
                                
                    async function playback(){
                        if(document.querySelector("#play_or_pause_play_player").style.display==="none"){
                            document.querySelector("#play_or_plause_player").click()
                        }
                        await sleep1(3)
                        const targetElement = document.querySelector("#timeshift_pointer_player")
                        mouseDragStart(targetElement);
                        mouseDragEnd(targetElement);
                    };

                    playback()
                    console.log('执行了回退');
                    executeScript();
                    """;

    private String forwardScript =
            """
                    function simulate(element, eventName) {
                        var options = extend(defaultOptions, arguments[2] || {});
                        var oEvent, eventType = null;
                                
                        for (var name in eventMatchers) {
                            if (eventMatchers[name].test(eventName)) {
                                eventType = name;
                                break;
                            }
                        }
                                
                        if (!eventType) throw new SyntaxError('Only HTMLEvents and MouseEvents interfaces are supported');
                                
                        if (document.createEvent) {
                            oEvent = document.createEvent(eventType);
                            if (eventType == 'HTMLEvents') {
                                oEvent.initEvent(eventName, options.bubbles, options.cancelable);
                            } else {
                                oEvent.initMouseEvent(eventName, options.bubbles, options.cancelable, document.defaultView, options.button, options.pointerX, options.pointerY, options.pointerX, options.pointerY, options.ctrlKey, options.altKey, options.shiftKey, options.metaKey, options.button, element);
                            }
                            element.dispatchEvent(oEvent);
                        } else {
                            options.clientX = options.pointerX;
                            options.clientY = options.pointerY;
                            var evt = document.createEventObject();
                            oEvent = extend(evt, options);
                            element.fireEvent('on' + eventName, oEvent);
                        }
                        return element;
                    }
                                
                    function extend(destination, source) {
                        for (var property in source) destination[property] = source[property];
                        return destination;
                    }
                                
                    var eventMatchers = {
                        'HTMLEvents': /^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,
                        'MouseEvents': /^(?:click|dblclick|mouse(?:down|up|over|move|out))$/
                    }
                    var defaultOptions = {
                        pointerX: 0,
                        pointerY: 0,
                        button: 0,
                        ctrlKey: false,
                        altKey: false,
                        shiftKey: false,
                        metaKey: false,
                        bubbles: true,
                        cancelable: true
                    }
                                
                    function triggerMouseEvent (node, eventType) {
                        var clickEvent = document.createEvent ('MouseEvents');
                        clickEvent.initEvent (eventType, true, true);
                        node.dispatchEvent (clickEvent);
                    };
                                
                    function mouseDragStart(node) {
                        console.log("Starting drag...");
                        console.log(node.offsetTop);
                        console.log(node.offsetLeft);
                        triggerMouseEvent(node, "mousedown")
                    }
                                
                    function mouseDragEnd(node){
                        console.log("Ending drag...");
                        const rect = node.getBoundingClientRect();
                        document.querySelector("#epg_right_shift_player").click()
                        simulate(node, "mousemove" , {pointerX: rect.x+20 , pointerY: node.offsetTop})
                        simulate(node, "mouseup" , {pointerX:  rect.x+20, pointerY: node.offsetTop})
                    }
                    function sleep1(time) {
                        time*=1000
                        return new Promise(resolve => {
                            setTimeout(() => {
                                resolve();
                            }, time);
                        });
                    }
                                
                    async function playback(){
                        if(document.querySelector("#play_or_pause_play_player").style.display==="none"){
                            document.querySelector("#play_or_plause_player").click()
                        }
                        await sleep1(3)
                        const targetElement = document.querySelector("#timeshift_pointer_player")
                        mouseDragStart(targetElement);
                        mouseDragEnd(targetElement);
                    };

                    playback()
                    console.log('执行了前进');
                    executeScript();
                    """;


    private String[] channelNames = {
            "CCTV-1 综合",
            "CCTV-2 财经",
            "CCTV-3 综艺",
            "CCTV-4 中文国际",
            "CCTV-5 体育",
            "CCTV-6 电影",
            "CCTV-7 军事农业",
            "CCTV-8 电视剧",
            "CCTV-9 纪录",
            "CCTV-10 科教",
            "CCTV-11 戏曲",
            "CCTV-12 社会与法",
            "CCTV-13 新闻",
            "CCTV-14 少儿",
            "CCTV-15 音乐",
            "CCTV-16 奥林匹克",
            "CCTV-17 农业农村",
            "CCTV-5+ 体育赛事",
            "CCTV Europe",
            "CCTV America"
    };

    private int currentLiveIndex;

    private static final String PREF_NAME = "MyPreferences";
    private static final String PREF_KEY_LIVE_INDEX = "currentLiveIndex";

    private boolean doubleBackToExitPressedOnce = false;

    private StringBuilder digitBuffer = new StringBuilder(); // 用于缓存按下的数字键
    private static final long DIGIT_TIMEOUT = 3000; // 超时时间（毫秒）

    private TextView inputTextView; // 用于显示正在输入的数字的 TextView

    // 初始化透明的View
    private View loadingOverlay;

    // 频道显示view
    private TextView overlayTextView;

    private String info = "";

    private com.tencent.smtt.sdk.WebView cctvView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = findViewById(R.id.flVideoContainer);
        // X5WebView初始化
        initX5WebView();

        // 初始化 WebView
        webView = findViewById(R.id.webView);

        // 初始化显示正在输入的数字的 TextView
        inputTextView = findViewById(R.id.inputTextView);

        // 初始化 loadingOverlay
        loadingOverlay = findViewById(R.id.loadingOverlay);

        // 初始化 overlayTextView
        overlayTextView = findViewById(R.id.overlayTextView);

        // 加载上次保存的位置
        loadLastLiveIndex();
        // 添加自动化调试
//        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT) {
//            WebView.setWebContentsDebuggingEnabled(true);
//        }
        // 配置 WebView 设置
        com.tencent.smtt.sdk.WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        // 添加自动播放视频
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setDatabaseEnabled(true);
        webSettings.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
        webSettings.setUseWideViewPort(true);
        // 启用 JavaScript 自动点击功能
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //添加js android双向调用 必须大于api19（4.4）才可以使用
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String jsonParams = "123456";
            //String method = "jsMethod()";//不拼接参数，直接调用js的jsMethod函数
            String method = "jsMethod(" + jsonParams + ")";//拼接参数，就可以把数据传递给js
            webView.evaluateJavascript(method, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Log.i("qcl0228", "js返回的数据" + value);
                }
            });
        }*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(com.tencent.smtt.sdk.WebSettings.LOAD_NORMAL);
        }

        // 设置 WebViewClient 和 WebChromeClient
        webView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public void onReceivedSslError(com.tencent.smtt.sdk.WebView webView, com.tencent.smtt.export.external.interfaces.SslErrorHandler handler, com.tencent.smtt.export.external.interfaces.SslError error) {
                handler.proceed(); // 忽略 SSL 错误
            }

            // 设置 WebViewClient，监听页面加载完成事件
            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
                cctvView = view;
                // 页面加载完成后执行 JavaScript 脚本
                // 清空info
                info = "";

                // 获取节目预告和当前节目
                view.evaluateJavascript("document.querySelector('#jiemu > li.cur.act').innerText", value -> {
                    // 处理获取到的元素值
                    if (!value.equals("null") && !value.isEmpty()) {
                        String elementValueNow = value.replace("\"", ""); // 去掉可能的引号
                        info += elementValueNow + "\n";
                    }
                });
                view.evaluateJavascript("document.querySelector('#jiemu > li:nth-child(4)').innerText", value -> {
                    // 处理获取到的元素值
                    if (!value.equals("null") && !value.isEmpty()) {
                        String elementValueNext = value.replace("\"", ""); // 去掉可能的引号
                        info += elementValueNext;
                    }
                });

                String script =
                        """
                                                            // 定义休眠函数
                                                            function sleep(ms) {
                                                                return new Promise(resolve => setTimeout(resolve, ms));
                                                            }
                                                
                                                            // 页面加载完成后执行 JavaScript 脚本
                                                            let interval=setInterval(async function executeScript() {
                                                                console.log('页面加载完成！');
                                                
                                                                // 休眠 1000 毫秒（1秒）
                                                                await sleep(1000);
                                                
                                                                // 休眠 50 毫秒
                                                                await sleep(50);
                                                
                                                                console.log('点击分辨率按钮');
                                                                var elem = document.querySelector('#resolution_item_720_player');
                                                                elem.click();
                                                
                                                                // 休眠 50 毫秒
                                                                await sleep(50);
                                                
                                                                console.log('设置音量并点击音量按钮');
                                                                var btn = document.querySelector('#player_sound_btn_player');
                                                                btn.setAttribute('volume', 100);
                                                                btn.click();
                                                                btn.click();
                                                                btn.click();
                                                
                                                                // 休眠 50 毫秒
                                                                await sleep(50);
                                                
                                                                console.log('点击全屏按钮');
                                //                                var fullscreenBtn = document.querySelector('#player_pagefullscreen_yes_player');
                                //                                fullscreenBtn.click();
                                                                clearInterval(interval);
                                                            }, 3000);
                                                            
                                                            
                                                
                                                            executeScript();
                                                            """;
                view.evaluateJavascript(script, null);

                new Handler().postDelayed(() -> {
                    // 模拟触摸
                    // simulateTouch(view, 0.5f, 0.5f);

                    // 隐藏加载的 View
                    loadingOverlay.setVisibility(View.GONE);

                    // 显示覆盖层，传入当前频道信息
                        showOverlay(channelNames[currentLiveIndex] + "\n" + info);
                }, 5000);

                // 在 WebView 加载完成后加载 RecyclerView
                new Handler().post(() -> {
                    recyclerView = findViewById(R.id.recyclerView);
                    List<Channel> channelList = createChannelList();
                    ChannelAdapter channelAdapter = new ChannelAdapter(channelList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(channelAdapter);
                });
            }
        });


        // 禁用缩放
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        // 在 Android TV 上，需要禁用焦点自动导航
        webView.setFocusable(false);

        // 设置 WebView 客户端
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                super.onShowCustomView(view, callback);
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mCustomView = view;
                mFrameLayout.addView(mCustomView);
                mCustomViewCallback = callback;
                webView.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
           }

            @Override
            public void onHideCustomView() {
                webView.setVisibility(View.VISIBLE);
                if (mCustomView == null) {
                    return;
                }
                mCustomView.setVisibility(View.GONE);
                mFrameLayout.removeView(mCustomView);
                mCustomViewCallback.onCustomViewHidden();
                mCustomView = null;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                super.onHideCustomView();
            }


        });

        // 加载初始网页
        loadLiveUrl();
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

    }



    private List<Channel> createChannelList() {
        List<Channel> channels = new ArrayList<>();
        // 添加您的频道数据
        channels.add(new Channel("CCTV-1 综合"));
        channels.add(new Channel("CCTV-2 财经"));
        // 添加其他频道...
        return channels;
    }

    private void initX5WebView() {
        // 搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("X5CORE", "onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {

            }
        };
        // x5内核初始化接口
        QbSdk.initX5Environment(this, cb);

        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT || event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    // 执行上一个直播地址的操作
                    navigateToPreviousLive();
                    return true;  // 返回 true 表示事件已处理，不传递给 WebView
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    // 执行下一个直播地址的操作
                    navigateToNextLive();
                    return true;  // 返回 true 表示事件已处理，不传递给 WebView
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                    // 执行暂停操作
                    simulateTouch(webView, 0.5f, 0.5f);
                    return true;  // 返回 true 表示事件已处理，不传递给 WebView
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.requestFocus(); // 将焦点设置到 RecyclerView
                    return true;  // 返回 true 表示事件已处理，不传递给 WebView
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    cctvView.evaluateJavascript(backwardScript, null);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    cctvView.evaluateJavascript(forwardScript, null);
                }
                return true;  // 返回 true 表示事件已处理，不传递给 WebView
            } else if (event.getKeyCode() >= KeyEvent.KEYCODE_0 && event.getKeyCode() <= KeyEvent.KEYCODE_9) {
                int numericKey = event.getKeyCode() - KeyEvent.KEYCODE_0;

                // 将按下的数字键追加到缓冲区
                digitBuffer.append(numericKey);

                // 使用 Handler 来在超时后处理输入的数字
                new Handler().postDelayed(() -> handleNumericInput(), DIGIT_TIMEOUT);

                // 更新显示正在输入的数字的 TextView
                updateInputTextView();

                return true;  // 事件已处理，不传递给 WebView
            }
        }

        return super.dispatchKeyEvent(event);  // 如果不处理，调用父类的方法继续传递事件
    }

    private void handleNumericInput() {
        // 将缓冲区中的数字转换为整数
        if (digitBuffer.length() > 0) {
            int numericValue = Integer.parseInt(digitBuffer.toString());

            // 检查数字是否在有效范围内
            if (numericValue > 0 && numericValue <= liveUrls.length) {
                currentLiveIndex = numericValue - 1;
                loadLiveUrl();
                saveCurrentLiveIndex(); // 保存当前位置
            }

            // 重置缓冲区
            digitBuffer.setLength(0);

            // 取消显示正在输入的数字
            inputTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateInputTextView() {
        // 在 TextView 中显示当前正在输入的数字
        inputTextView.setVisibility(View.VISIBLE);
        inputTextView.setText("换台：" + digitBuffer.toString());
    }

    private void loadLastLiveIndex() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        currentLiveIndex = preferences.getInt(PREF_KEY_LIVE_INDEX, 0); // 默认值为0
        loadLiveUrl(); // 加载上次保存的位置的直播地址
    }

    private void saveCurrentLiveIndex() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_KEY_LIVE_INDEX, currentLiveIndex);
        editor.apply();
    }


    private void loadLiveUrl() {
        if (currentLiveIndex >= 0 && currentLiveIndex < liveUrls.length) {
            // 显示加载的View
//            loadingOverlay.setVisibility(View.VISIBLE);

            webView.setInitialScale(getMinimumScale());
            webView.loadUrl(liveUrls[currentLiveIndex]);
        }
    }

    private void navigateToPreviousLive() {
        currentLiveIndex = (currentLiveIndex - 1 + liveUrls.length) % liveUrls.length;
        loadLiveUrl();
        saveCurrentLiveIndex(); // 保存当前位置
    }

    private void navigateToNextLive() {
        currentLiveIndex = (currentLiveIndex + 1) % liveUrls.length;
        loadLiveUrl();
        saveCurrentLiveIndex(); // 保存当前位置
    }

    private int getMinimumScale() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // 计算缩放比例，使用 double 类型进行计算
        double scale = Math.min((double) screenWidth / 3840.0, (double) screenHeight / 2160.0) * 100;

        // 四舍五入并转为整数
        return (int) Math.round(scale);
    }

    // 在需要模拟触摸的地方调用该方法
    public void simulateTouch(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;

        // 构造 ACTION_DOWN 事件
        MotionEvent downEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);
        view.dispatchTouchEvent(downEvent);

        // 构造 ACTION_UP 事件
        MotionEvent upEvent = MotionEvent.obtain(downTime, eventTime + 100, MotionEvent.ACTION_UP, x, y, 0);
        view.dispatchTouchEvent(upEvent);

        // 释放事件对象
        downEvent.recycle();
        upEvent.recycle();
    }

    @Override
    public void onBackPressed() {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            // 如果 RecyclerView 可见，按下返回键时隐藏它
            recyclerView.setVisibility(View.GONE);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "再按一次返回键退出应用", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        // 如果两秒内再次按返回键，则退出应用
    }

    private void showOverlay(String channelInfo) {
        // 设置覆盖层内容
        overlayTextView.setText(channelInfo);

        findViewById(R.id.overlayTextView).setVisibility(View.VISIBLE);

        // 使用 Handler 延时隐藏覆盖层
        new Handler().postDelayed(() -> {
            findViewById(R.id.overlayTextView).setVisibility(View.GONE);
        }, 8000);
    }

    @Override
    protected void onDestroy() {
        // 在销毁活动时，释放 WebView 资源
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}

