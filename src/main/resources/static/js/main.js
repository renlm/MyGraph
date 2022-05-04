/***
 * 封装一些常用的方法
 */
(function($) {
	$.extend({
		/**
		 * 打开图形
		 */
		openGraphEditorFormatter: function (value, row, index) {
			return '<a href=\'javascript:void(0);\' style=\'text-decoration:underline;color:blue;\' data-value="' + value + '" data-index="' + index + '" onclick="$.openGraphEditor(\'' + row.uuid + '\', \'' + row.name + '\')">' + row.name + '</a>';
		},
		/**
		 * 打开图形预览
		 * @param params uuid 唯一标识
		 * @param params title 标题
		 * @param params openNew 是否打开新网页（默认否）
		 * @param params iconCls 导航栏图标
		 */
		openGraphViewer: function (uuid, title, openNew, iconCls) {
			if(openNew) {
				top.window.open(ctx + '/graph/viewer?uuid=' + uuid);
			} else {
				try {
					top.MyUI.addIndexTab({
						id : "GraphViewer-" + uuid,
						title : title,
						href : '/graph/viewer?uuid=' + uuid,
						iconCls : iconCls ? iconCls : 'fa fa-eye'
					});
				} catch(e) {
					top.window.open(ctx + '/graph/viewer?uuid=' + uuid);
				}
			}
		},
		/**
		 * 打开图形编辑器
		 * @param params uuid 唯一标识
		 * @param params title 标题
		 * @param params openNew 是否打开新网页（默认否）
		 * @param params iconCls 导航栏图标
		 */
		openGraphEditor: function (uuid, title, openNew, iconCls) {
			if(openNew) {
				top.window.open(ctx + '/graph/editor?uuid=' + uuid);
			} else {
				try {
					top.MyUI.addIndexTab({
						id : "GraphEditor-" + uuid,
						title : title,
						href : '/graph/editor?uuid=' + uuid,
						iconCls : iconCls ? iconCls : 'fa fa-edit'
					});
				} catch(e) {
					top.window.open(ctx + '/graph/editor?uuid=' + uuid);
				}
			}
		},
		/**
		 * 表格分页参数处理
		 */
		pageOnBeforeLoad: function (param) {
			param.keywords = param.q;
			param.current = param.page;
			param.size = param.rows;
			delete param.q;
			delete param.page;
			delete param.rows;
		},
		/**
		 * 禁用状态格式化
		 */
		disabledFormatter: function (value) {
			if(value) {
				return '<span style=\'color:red;\'>禁用</span>';
			} else {
				return '<span style=\'color:green;\'>启用</span>';
			}
		},
		/**
		 * 字节大小格式化
		 */
		formatBytes: function (value) {
			if(value === 0 || value === 1) {
				return value + " byte";
			} else if(value) {
				if(value >= 1024*1024*1024*1024) {
					return (Math.floor(value/1024/1024/1024/1024 * 100)/100) + " TiB"; 
				} else if(value >= 1024*1024*1024) {
					return (Math.floor(value/1024/1024/1024 * 100)/100) + " GiB"; 
				} else if(value >= 1024*1024) {
					return (Math.floor(value/1024/1024 * 100)/100) + " MiB"; 
				} else if(value >= 1024) {
					return (Math.floor(value/1024 * 100)/100) + " KiB"; 
				} else {
					return value + " bytes";
				}
			} else {
				return null;
			}
		},
		/**
		 * 序号格式化
		 */
		seqFormatter: function (value, row, index) {
			if(value) { } else if(row) { }
			return index + 1;
		},
		/**
		 * 是否状态格式化
		 */
		yesNoFormatter: function (value) {
			var yes = value ? true : false;
			if(value === null) {
				return '-';
			} else if(value === 'false') {
				yes = false;
			} else if(value === '0') {
				yes = false;
			} else if(value === 0) {
				yes = false;
			}
			if(yes) {
				return '是';
			} else {
				return '否';
			}
		},
		/**
		 * 创建时间格式化
		 */
		createdAtFormatter: function (value) {
			if(!value) {
				return null;
			}
			return layui.util.toDateString(value, "yyyy/MM/dd HH:mm:ss");
		},
		/**
		 * 更新时间格式化
		 */
		updatedAtFormatter: function (value) {
			if(!value) {
				return null;
			}
			return layui.util.toDateString(value, "yyyy/MM/dd HH:mm:ss");
		}
	});
})(jQuery);
(function($) {
	if (!$.fn 
		|| !$.fn.validatebox 
		|| !$.fn.validatebox.defaults 
		|| !$.fn.validatebox.defaults.rules) {
		return;
	}
	$.extend($.fn.validatebox.defaults.rules, {
        equals: {
            validator: function (b, c) {
                return b == a(c[0]).val()
            }, message: "两次输入的内容不一致"
        }, cellphone: {
            validator: function (a) {
                return /^1(3|4|5|7|8|9)\d{9}$/.test(a)
            }, message: "请输入有效的手机号码"
        }, telephone: {
            validator: function (a) {
                return /(^(0[0-9]{2,3}\-)?([2-9][0-9]{6,7})+(\-[0-9]{1,4})?$)|(^((\d3)|(\d{3}\-))?(1[345789]\d{9})$)/.test(a)
            }, message: "请输入有效的电话号码"
        }, maxLength: {
            validator: function (a, b) {
                return a.length < b[0]
            }, message: "输入内容长度必须小于{0}"
        }, minLength: {
            validator: function (a, b) {
                return a.length > b[0]
            }, message: "输入内容长度必须大于{0}"
        }, idCard: {
            validator: function (a) {
                return /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/.test(a)
            }, message: "请输入正确的身份证号"
        }, postcode: {
            validator: function (a) {
                return /^[1-9]\d{5}(?!\d)$/.test(a)
            }, message: "请输入正确的邮政编码"
        }, date: {
            validator: function (a) {
                return /^[1-2][0-9][0-9][0-9]-[0-1]{0,1}[0-9]-[0-3]{0,1}[0-9]$/.test(a)
            }, message: "请输入正确的日期"
        }, alphaDash: {
            validator: function (a) {
                return /^[\w-]+$/.test(a)
            }, message: "输入内容只能是数字、字母、下划线或横线"
        }, alphaNum: {
            validator: function (a) {
                return /^[a-z0-9]+$/i.test(a)
            }, message: "输入内容只能是数字和字母"
        }, number: {
            validator: function (a) {
                return /^\d+$/.test(a)
            }, message: "输入内容只能是数字"
        }
    });
})(jQuery);