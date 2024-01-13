// 定义休眠函数
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

// 页面加载完成后执行 JavaScript 脚本
let interval = setInterval(async function executeScript() {
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
    var fullscreenBtn = document.querySelector('#player_pagefullscreen_yes_player');
    fullscreenBtn.click();
    clearInterval(interval);
}, 3000);