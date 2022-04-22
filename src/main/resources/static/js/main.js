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
		/***
		 * 简单按钮加载效果
		 */
		simpleLoad: function (btn, state) {
            if (state) {
                btn.children().addClass("fa-spin");
                btn.contents().last().replaceWith(" Loading");
            } else {
                setTimeout(function () {
                    btn.children().removeClass("fa-spin");
                    btn.contents().last().replaceWith(" 刷新");
                }, 300);
            }
        },
		/***
		 * Toastr通知（成功）
		 */
		toastrSuccess: function (message) {
			toastr.options = {
				closeButton: true,
				debug: false,
				progressBar: true,
				positionClass: "toast-top-right",
				onclick: null,
				showDuration: "400",
				hideDuration: "1000",
				timeOut: "7000",
				extendedTimeOut: "1000",
				showEasing: "swing",
				hideEasing: "linear",
				showMethod: "fadeIn",
				hideMethod: "fadeOut"
			};
			toastr.success(message);
		},
		/***
		 * 修改密码
		 */
		editPwd: function() {
			window.editPwdLayerIndex = layer.open({
				type: 2, 
				title: '修改密码',
				offset: '200px',
				area: ['500px', '250px'],
				skin: "layui-layer-rim",
				content: [ctx + '/editPwd', 'no']
			});
		},
		/**
		 * 序号格式化
		 */
		seqFormatter: function(value, row, index) {
			if(value) { } else if(row) { }
			return index + 1;
		},
		/**
		 * 是否状态格式化
		 */
		yesNoFormatter: function(value) {
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
		}
	});
})(jQuery);