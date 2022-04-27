/*
 * MyUI 1.0.1, a JS UI library.
 */
(function (global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
	typeof define === 'function' && define.amd ? define(['exports'], factory) :
	(factory((global.MyUI = global.MyUI || {})));
}(this, (function (exports) { 'use strict';

var version = "1.0.1";
var ctx = null;

/**
 * 生成菜单树
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
		skin: "layui-layer-rim",
		content: [ctx + '/editPwd', 'no']
	});
}

/**
 * 初始化
 */
function init (option) {
	ctx = option.ctx;
}

exports.version = version;
exports.getMenuTree = getMenuTree;
exports.modifyPwd = modifyPwd;
exports.init = init;

Object.defineProperty(exports, '__esModule', { value: true });

})));