/***
 * 封装一些常用的方法
 */
(function($) {
	$.extend({
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