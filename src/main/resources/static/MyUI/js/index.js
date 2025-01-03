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
	var $tabs = $('#' + indexTabsId).tabs({
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
	}).tabs('tabs');
	// 利用初始类样式，处理display: none与visibility: hidden转换
	$.each($tabs, function ($i, $tab) {
		$tab.panel('options').tab.on('click', function () {
			$('.indexTabBody').eq($i).removeClass('initTab');
		});
	});
	
	// tab右键菜单
    $('#' + mmId).menu({
        onClick: function (item) {
            tabMenuOprate(this, item.name);
        }
    });
    
    $('.nav-group').on('click', 'li', function() {
        if($(this).data('type') === 'more') {
            return false;
        }
        if ($(this).data('type') === 'urlNewWindows') {
			var dataUrl = $(this).data('url');
			if (dataUrl) {
				if (dataUrl.indexOf('http') >= 0) {
					window.open(dataUrl);
				} else if (dataUrl.indexOf('//') >= 0) {
					window.open(dataUrl);
				} else if (dataUrl.indexOf('/') >= 0) {
					window.open(ctx + dataUrl);
				} else {
					window.open(dataUrl);
				}
			}
        }

        // 添加选择样式
        $('.nav-group .selected').removeClass('selected');
        $(this).addClass('selected');

        let target = this;
        if($(this).data('sub-type') === 'moreSub') {
            $('.lastMenu').addClass('selected');
            target = '.lastMenu';
        }

        if ($(this).data('type') === 'urlNewWindows' || 
			$(this).data('type') === 'urlInsidePage') {
            return;
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
		{ 'uuid': menuId },
        function (data) {
			var menuData = data;
            $('#' + rightAccordionId).sidemenu({
                width: 255,
                data: menuData,
                fit: true,
                multiple: false,
                border: false,
                onSelect: function (item) {
					if (item.resourceTypeCode === 'markdown') {
						$.openMarkdownViewer(item.resourceId, item.text);
					} else if (item.resourceTypeCode === 'urlNewWindows') {
						var targetUrl = (item.url.indexOf("http") === 0 || item.url.indexOf("//") === 0) ? item.url : (ctx + item.url);
						window.open(targetUrl);
					} else if (item.resourceTypeCode === 'menu' 
						|| item.resourceTypeCode === 'urlInsidePage') {
						addIndexTab({id: item.id, title: item.text, href: item.url, iconCls: item.iconCls});
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
function addIndexTab(options) {
	var id, src, title, iconCls;
    id = options.id;
    src = ctx + options.href;
    title = options.title;
    iconCls = options.iconCls;

	if(!id) {
		id = 'Base64-' + Base64.encodeURI(src);
	}

	// 判断tab
    var tabsArr = $('#' + indexTabsId).tabs('tabs');
    for (var i = 0; i < tabsArr.length; i++) {
        var tab = tabsArr[i];
        var options = $(tab).panel('options');
        if (id && options.id && id == options.id) {
            $('#' + indexTabsId).tabs('select', i); // 选中
            return;
        }
    }

    // 添加tab
    if(src) {
        $('#' + indexTabsId).tabs('add', {
            id: id,
            title: title,
            iconCls: iconCls ? iconCls: 'fa fa-th',
            closable: true,
            content: '<iframe scrolling="auto" frameborder="0"  src="' + src + '" style="width:100%;height:100%;"></iframe>',
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
        if (e.level === 2) {
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
 * 修改个人信息
 */
function modifyPersonal () {
	var $personalDialog = $('<form id=\'personalDialog\' method=\'get\' class=\'myui\'></form>');
    $personalDialog.myuiDialog({
        title: '个人信息',
        width: 730,
        height: 445,
		collapsible: true,
		minimizable: false,
		maximizable: true,
        closed: false,
        cache: false,
        href: ctx + '/sys/user/personalDialog',
        modal: true,
        buttons: [{
            text: '更新',
            iconCls: 'fa fa-save',
            handler: function () {
				$.messager.progress({'text': '请求中……'});
		        $personalDialog.form('submit', {
		            url: ctx + '/sys/user/doModifyPersonal',
		            onSubmit: function () {
		                var isValid = $(this).form('validate');
		                if (!isValid) {
		                    $.messager.progress('close');
		                }
		                return isValid;
		            },
		            success: function (result) {
						var resultJson = JSON.parse(result);
		                if (resultJson.statusCode == 200) {
							$('#user-nickname').html(resultJson.data.nickname);
							$('#user-username').html(resultJson.data.username);
		                	$.messager.show({title: '我的消息', msg: resultJson.message?resultJson.message:'操作成功', timeout: 5000, showType: 'slide'});
							$personalDialog.dialog('destroy');
		                } else {
		                    $.messager.show({title: '我的消息', msg: resultJson.message?resultJson.message:'出错了', timeout: 5000, showType: 'slide'});
		                }
		                $.messager.progress('close');
		        	}
		 		});
			}
        }, {
            text: '关闭',
            iconCls: 'fa fa-close',
            handler: function () {
                $personalDialog.dialog('destroy');
            }
        }],
        onLoad: function () {
            $.getJSON(ctx + '/sys/user/getCurrent', function (result) {
				// 初始化表单
				$personalDialog.form('load', result.data);
				if (result.data.avatar) {
					$personalDialog.find(".avatar img").attr("src", ctx + '/sys/file/download/' + result.data.avatar + '?inline').show().siblings().hide().parent().parent().css('padding-top', '10px');
				}
				// 绑定头像上传
				layui.upload.render({
					elem: $personalDialog.find(".avatar").get(0),
					url: ctx + "/sys/file/upload",
					accept: "images",
				   	acceptMime: "image/*",
	    			size: 30 * 1024,
					before: function(obj) {
						if (obj) {
							window.uploadeindex = layer.load(2);
						}
					},
					done: function (res, index, upload) {
						if (res && index && upload) {
							layer.close(window.uploadeindex);
							if (res.success) {
								$personalDialog.find(".avatar input[name='avatar']").val(res.data.fileId);
								$personalDialog.find(".avatar img").attr("src", ctx + '/sys/file/download/' + res.data.fileId + '?inline').show().siblings().hide().parent().parent().css('padding-top', '10px');
							} else {
								layer.msg("上传失败", { icon: 5, shift:6 });
							}
						} else {
							layer.msg("出错了", { icon: 5, shift:6 });
						}
				    },
				    error: function (index, upload) {
						if (index && upload) {
							layer.msg("出错了", { icon: 5, shift:6 });
						}
				    }
				});
            });
        },
		onClose: function () {
			$personalDialog.dialog('destroy');
		}
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

		// 主页打开初始化时显示第一个系统的菜单
    	$('.nav-group li.selected').eq('0').trigger('click');
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
exports.getMenuTree = getMenuTree;
exports.addIndexTab = addIndexTab;
exports.modifyPersonal = modifyPersonal;
exports.init = init;

Object.defineProperty(exports, '__esModule', { value: true });

})));