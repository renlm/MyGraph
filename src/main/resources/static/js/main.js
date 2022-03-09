/***
 * 封装一些常用的方法
 */
(function($) {
	$.extend({
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
		}
	});
})(jQuery);