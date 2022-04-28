/***
 * 封装一些常用的方法
 */
(function($) {
	$.extend({
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
		 * 更新日期格式化
		 */
		updatedAtFormatter: function (value) {
			if(!value) {
				return null;
			}
			return layui.util.toDateString(value, "yyyy/MM/dd HH:mm:ss");
		}
	});
})(jQuery);