/*
 * MyUI 1.0.1, a JS UI library.
 */
(function (global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
	typeof define === 'function' && define.amd ? define(['exports'], factory) :
	(factory((global.MyUI = global.MyUI || {})));
}(this, (function (exports) { 'use strict';

var version = "1.0.1";

exports.version = version;

Object.defineProperty(exports, '__esModule', { value: true });

})));