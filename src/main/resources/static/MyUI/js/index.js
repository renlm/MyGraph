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
var menuApi = null;
var loadingId = 'loading';
var mmId = 'mm';
var indexLayoutId = 'indexLayout';
var rightAccordionId = 'rightAccordion';
var indexTabsId = 'indexTabs';

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
 * 初始化首页选项卡
 */
function initIndexTabs () {
	// 选项卡
	$('#' + indexTabsId).tabs({
   		fit: true,
        tools: [{
            iconCls: 'fa fa-home',
            handler: function () {
                $('#' + indexTabsId).tabs('select', 0);
            }
        }, {
            iconCls: 'fa fa-refresh',
            handler: function () {
                var refresh_tab = $('#' + indexTabsId).tabs('getSelected');
                var refresh_iframe = refresh_tab.find('iframe')[0];
                refresh_iframe.contentWindow.location.href = refresh_iframe.src;
            }
        }, {
            iconCls: 'fa fa-close',
            handler: function () {
                var index = $('#' + indexTabsId).tabs('getTabIndex', $('#' + indexTabsId).tabs('getSelected'));
                var tab = $('#' + indexTabsId).tabs('getTab', index);
                if (tab.panel('options').closable) {
                    $('#' + indexTabsId).tabs('close', index);
                }
            }
        }, {
            iconCls: 'fa fa-arrows-alt',
            handler: function () {
                FullScreen().handleFullScreen();
            }
        }],
        onSelect: tabOnSelect,
        // 监听右键事件，创建右键菜单
        onContextMenu: function (e, title, index) {
            e.preventDefault();
            if (index >= 0) {
                $('#' + mmId).menu('show', {
                    left: e.pageX,
                    top: e.pageY
                }).data('tabTitle', title);
            }
        }
	});
	
	// tab右键菜单
    $('#' + mmId).menu({
        onClick: function (item) {
            tabMenuOprate(this, item.name);
        }
    });
    
    $('.nav-group').on('click', 'li', function() {
        if($(this).data('type') === 'lastMenu') {
            return false;
        }
        if ($(this).data('type') === 'lastMenuSub') {
            window.open($(this).data('url'));
        }

        // 添加选择样式
        $('.nav-group .selected').removeClass('selected');
        $(this).addClass('selected');

        let target = this;
        if($(this).data('type') === 'lastMenuSub'){
            $('.lastMenu').addClass('selected');
            target = '.lastMenu';
        }
        navsScrollTo('first', target);

        // 刷新左侧导航菜单
        var id = $(this).attr('id');
        var title = $(this).attr('title');
        if (id && title) {
            generateMenu(id, title);
        }
    });
    var changing = false; //是否正在滚动中
    function navsScrollTo(step, target) {
        if(changing) {
            return;
        }else{
            changing = !changing;
            setTimeout(function () {
                changing = !changing;
            },400);
        }

        var contentWidth,itemWidth,areaWidth,currentLeft,times,$navContentBox;
        $navContentBox = $('.nav-group'); // 拿到导航项的盒子
        // 1.得到滚动内容的总长度 contentWidth；
        contentWidth = parseInt($navContentBox.width());

        // 2.得到滚动元素的宽度 itemWidth；
        // itemWidth = 102;
         itemWidth = $('.nav-center ul li').width()+2;

        // 3.得到当前currentLeft值
        currentLeft = parseInt($navContentBox.position().left);

        // 4.得到滚动区域的总长度 areaWidth;
        areaWidth = parseInt( $navContentBox.parent().width());

        // 5.按钮是否生效  并左右滚动
        if(step == 'next'){ // 滚动到下一页
            if( areaWidth + Math.abs(currentLeft) < contentWidth){ // 不需要向右移动
                // 5.1 移动多少个
                times = Math.floor(areaWidth / itemWidth);
                $navContentBox.css('left',currentLeft - itemWidth * times)
            }
        }else if(step == 'previous'){
            // 滚动到上一页
            if (currentLeft != 0){
                if(Math.abs(currentLeft) <= areaWidth){
                    $navContentBox.css('left',0)
                }else{
                    times = Math.floor(areaWidth / itemWidth);
                    $navContentBox.css('left',-(Math.abs(currentLeft) - itemWidth * times))
                }
            }
        }else if(step == 'first'){ // 将点击的元素滚动到第一个
            var liLeft = $(target).position().left;
            // 获取点击项距离浏览器右侧的距离
            var liRight  =  $('.nav-bar').width() - $(target).offset().left - itemWidth - $('.nav-right').width();
            if(liRight < itemWidth && areaWidth + Math.abs(currentLeft) < contentWidth){
                $navContentBox.css('left',-liLeft)
            }
        }
    }
}
function tabOnSelect () {
	var tabOpts = $('#' + indexTabsId).tabs('getSelected').panel('options');
    var tabId = tabOpts.id, panels = $('#' + rightAccordionId).find('.accordion').accordion('panels');
    if(tabOpts.isNewTab || !tabId){
        tabOpts.isNewTab = !tabOpts.isNewTab;
        return;
    }
    $.each(panels, function (index, item) {
        var $tree = item.find('.tree');
        var node = $tree.tree('find', {id: tabId});
        if (node) {
            $('#' + rightAccordionId).find('.accordion').accordion('select', index) // 选中面板
            $tree.tree('expandTo', node.target).tree('select', node.target); // 选中tree选项
            $tree.tree('scrollTo', node.target); // 选中tree选项
            return false;
        }
    });
}

/** 
 * Tab菜单操作
 */
function tabMenuOprate (menu, type) {
    var allTabs = $('#' + indexTabsId).tabs('tabs');
    var allTabtitle = [];
	for (var k = 0; k < allTabs.length; k++) {
		var n = allTabs[k];
     	var opt = $(n).panel('options');
        if (opt.closable) {
			allTabtitle.push(opt.title);
		}   
  	}
    var curTabTitle = $(menu).data('tabTitle');
    var curTabIndex = $('#' + indexTabsId).tabs('getTabIndex', $('#' + indexTabsId).tabs('getTab', curTabTitle));
    switch (type) {
        case '1': // 关闭当前
            if (curTabIndex > 0) {
                $('#' + indexTabsId).tabs('close', curTabTitle);
                return false;
            } else {
                $.messager.show({
                    title: '操作提示',
                    msg: '首页不允许关闭！'
                });
                break;
            }
        case '2': // 全部关闭
            for (var i = 0; i < allTabtitle.length; i++) {
                $('#' + indexTabsId).tabs('close', allTabtitle[i]);
            }
            break;
        case '3': // 除此之外全部关闭
            for (var i = 0; i < allTabtitle.length; i++) {
                if (curTabTitle != allTabtitle[i]) {
					$('#' + indexTabsId).tabs('close', allTabtitle[i]);
				}
            }
            $('#' + indexTabsId).tabs('scrollBy', 10);
            $('#' + indexTabsId).tabs('select', curTabTitle);
            break;
        case '4': // 当前侧面右边
            for (var i = curTabIndex; i < allTabtitle.length; i++) {
                $('#' + indexTabsId).tabs('close', allTabtitle[i]);
            }
            $('#' + indexTabsId).tabs('select', curTabTitle);
            break;
        case '5': // 当前侧面左边
            for (var i = 0; i < curTabIndex - 1; i++) {
                $('#' + indexTabsId).tabs('close', allTabtitle[i]);
            }
            $('#' + indexTabsId).tabs('select', curTabTitle);
            break;
        case '6': // 刷新
            var currentTab = $('#' + indexTabsId).tabs('getSelected');
            var currentIframe = currentTab.find('iframe')[0];
            currentIframe.contentWindow.location.href = currentIframe.src;
            break;
        case '7': // 在新窗口打开
            var refresh_tab = $('#' + indexTabsId).tabs('getSelected');
            var refresh_iframe = refresh_tab.find('iframe')[0];
            window.open(refresh_iframe.src);
            break;
    }

}

/**
 * 生成左侧导航菜单
 */
function generateMenu(menuId, systemName) {
	$('.panel-header .panel-title:first').html(systemName);
	var panelE = $('#' + rightAccordionId).find('.accordion');
	if(panelE && panelE.length && panelE.length > 0) {
	    var allPanel = panelE.accordion('panels');
	    var size = allPanel.length;
	    if (size > 0) {
	        for (var i = 0; i < size; i++) {
	            $('#' + rightAccordionId).find('.accordion').accordion('getPanelIndex', allPanel[i]);
	            $('#' + rightAccordionId).find('.accordion').accordion('remove', 0);
	        }
	    }
	}
    $.get(ctx + menuApi,
		{ 'menuId': menuId },
        function (data) {
        	var menuData = getMenuTree(data);
            $('#' + rightAccordionId).sidemenu({
                width: 255,
                data: menuData,
                fit: true,
                multiple: false,
                border: false,
                onSelect: function (item) {
				    // 判断是否已经打开tab
				    var tabsArr = $('#' + indexTabsId).tabs('tabs');
				    for (var i = 0; i < tabsArr.length; i++) {
				        var tab = tabsArr[i];
				        var options = $(tab).panel('options');
				        if (item.id && options.id && item.id == options.id) {
				            $('#' + indexTabsId).tabs('select', i); // 选中
				            return;
				        }
				    }
				    // 不存在则添加菜单
				    if(item.url){
				        $('#' + indexTabsId).tabs('add', {
				            title: item.text,
				            id: item.id,
				            isNewTab: true,
				            bodyCls: 'spacing-0', // 去除间隔
				            iconCls: item.iconCls,
				            closable: true,
				            content: '<iframe scrolling="auto" frameborder="0"  src="' + item.url + '" style="width:100%;height:100%;"></iframe>',
				        });
				    }
				}
            });
            // 初始化accordion
            $('#' + rightAccordionId).find('.accordion').accordion({
                fit: true,
                multiple: false,
                border: false
            });
			// 菜单拖拽拉伸是调整大小
            $('#west').panel({ onResize: function () {
			    $('#' + rightAccordionId).sidemenu('resize');
			}});
        }, 'json');
}

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
	menuApi = option.menuApi ? option.menuApi : menuApi;
	loadingId = option.loadingId ? option.loadingId : loadingId;
	mmId = option.mmId ? option.mmId : mmId;
	indexLayoutId = option.indexLayoutId ? option.indexLayoutId : indexLayoutId;
	rightAccordionId = option.rightAccordionId ? option.rightAccordionId : rightAccordionId;
	indexTabsId = option.indexTabsId ? option.indexTabsId : indexTabsId;
	
	// 首页加载完后，取消加载中状态
	$(window).on('load', function () {
	    $('#' + loadingId).fadeOut();
	});
	
	$(function () {
		generateMenu();
		
		$('.collapseMenu').on('click', function () {
	        var opts = $('#' + rightAccordionId).sidemenu('options');

	        // 改变面板布局
	        $('#' + indexLayoutId).layout('panel','west').panel('resize', { width: opts.collapsed ? 260 : 60 })
	        $('#' + indexLayoutId).layout();

	        // 改变slidemenu样式
	        $('#' + rightAccordionId).sidemenu(opts.collapsed ? 'expand' : 'collapse')
	        $('#' + rightAccordionId).sidemenu('resize', { width: opts.collapsed ? 60 : 260 })

	        // 改变图标样式
	        $('#menuCollapseIcon').addClass(opts.collapsed ? 'fa-indent' : 'fa-outdent')
	        $('#menuCollapseIcon').removeClass(opts.collapsed ? 'fa-outdent' : 'fa-indent')

	        // 选中菜单
	        if (!opts.collapsed) {
	            tabOnSelect();
	        }

	        // 改变菜单查询框
	        $('.systemname').css('display', opts.collapsed ? 'none' : 'inline-block');
	        $('.banner .nav-bar .webname').css('width', opts.collapsed ? 55 : 255);
	        $('.nav-bar .nav-center').css('marginLeft', opts.collapsed ? 0 : 255);
	        hideNavBtn();
	    });
		
		// 首页tabs选项卡
	    initIndexTabs();
	});
	
    function hideNavBtn() {
        var contentWidth,areaWidth;
        var $navContentBox = $('.nav-group');
        // 1.得到滚动内容的总长度 contentWidth；
        contentWidth = parseInt($navContentBox.width());

        // 2.得到滚动区域的总长度 areaWidth;
        areaWidth = parseInt( $navContentBox.parent().width());

        if(contentWidth < areaWidth){
            $('.nav-btn').addClass('hide');
            $navContentBox.css('left',0)
        }else{
            $('.nav-btn').removeClass('hide')
        }
    }
}

exports.version = version;
exports.addParentTab = addParentTab;
exports.modifyPwd = modifyPwd;
exports.init = init;

Object.defineProperty(exports, '__esModule', { value: true });

})));