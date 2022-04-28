/*
 * MyUI 1.0.1, a JS UI library.
 */
(function (global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
	typeof define === 'function' && define.amd ? define(['exports'], factory) :
	(factory((global.MyUI = global.MyUI || {})));
}(this, (function (exports) { 'use strict';

var version = '1.0.1';
var ctx = '';
var indexTabsId = 'index_tabs';

/**
 * 全屏操作
 */
var FullScreen = function () {
	var isFullScreen = function () {
		return document.fullscreenElement || 
			document.mozFullScreenElement || 
			document.webkitFullscreenElement;
	};
	
	// 进入全屏
    var setFullScreen = function () {
        var docEle = document.documentElement;
        if (docEle.requestFullscreen) {
            // W3C
            docEle.requestFullscreen();
        } else if (docEle.mozRequestFullScreen) {
            // FireFox
            docEle.mozRequestFullScreen();
        } else if (docEle.webkitRequestFullScreen) {
            // Chrome等
            docEle.webkitRequestFullScreen();
        } else if (docEle.msRequestFullscreen) {
            // IE11
            docEle.msRequestFullscreen();
        } else {
            $.messager.alert('温馨提示', '该浏览器不支持全屏', 'messager-warning');
        }
    };

    // 退出全屏 判断浏览器种类
    var exitFullScreen = function () {
        // 判断各种浏览器，找到正确的方法
        var exitMethod = document.exitFullscreen || // W3C
            document.mozCancelFullScreen || // Chrome等
            document.webkitExitFullscreen || // FireFox
            document.msExitFullscreen; // IE11
        if (exitMethod) {
            exitMethod.call(document);
        }
		// for Internet Explorer
        else if (typeof window.ActiveXObject !== 'undefined') {
            var wscript = new ActiveXObject('WScript.Shell');
            if (wscript !== null) {
                wscript.SendKeys('{F11}');
            }
        }
    };

    return {
		// 全屏切换
        handleFullScreen: function () {
            if (isFullScreen()) {
                exitFullScreen();
            } else {
                setFullScreen();
            }
        }
    };
};

/**
 * 添加首页选项卡（从Iframe内容外层添加）
 */
function addParentTab(options) {
    var src, title, iconCls;
    src = options.href;
    title = options.title;
    iconCls = options.iconCls;

    var tabs = $('#' + indexTabsId);
    if (tabs.tabs('exists', title)) {
        tabs.tabs('select', title);
    } else {
        var iframe = '<iframe src="' + src + '" frameborder="0" style="border:0;width:100%;height:100%;"></iframe>';
        tabs.tabs('add', {
            title: title,
            content: iframe,
			// 加上后才能刷新
            iframe: true, 
            closable: true,
            iconCls: iconCls ? iconCls: 'fa fa-th',
            border: true
        });
    }
}

/**
 * 生成菜单树（数据）
 */
function getMenuTree (data) {
	let subData = []
    let child = []
    data.forEach(e => {
        if (e.levelId === 2) {
            e.children = e.state === 'closed' ? [] : ''
            subData.push(e)
        } else {
            child.push(e)
        }
    });
	return recursionMenuTree(subData, child);
}
function recursionMenuTree(sourceData, children) {
    let arr = []
    if (sourceData) {
        sourceData.forEach((e) => {
            children.forEach((c) => {
                if (e.id == c.pid) {
                    e.key = e.id
                    c.children = c.state === 'closed' ? [] : ''
                    if (e.children) e.children.push(c)
                }
            })
            if (e.children) {
                recursionMenuTree(e.children, children)
            }
        })
        arr = sourceData
    }
    return arr
}

/***
 * 修改密码
 */
function modifyPwd () {
	window.editPwdLayerIndex = layer.open({
		type: 2, 
		title: '修改密码',
		offset: '200px',
		area: ['500px', '250px'],
		skin: 'layui-layer-rim',
		content: [ctx + '/editPwd', 'no']
	});
}

/**
 * 初始化
 */
function init (option) {
	ctx = option.ctx ? option.ctx : ctx;
	indexTabsId = option.indexTabsId ? option.indexTabsId : indexTabsId;
}

exports.version = version;
exports.FullScreen = FullScreen();
exports.addParentTab = addParentTab;
exports.getMenuTree = getMenuTree;
exports.modifyPwd = modifyPwd;
exports.init = init;

Object.defineProperty(exports, '__esModule', { value: true });

})));