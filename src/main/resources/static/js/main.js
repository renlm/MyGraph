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