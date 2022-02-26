/***
 * 修改Mxgraph
 */
(function($) {
	$.extend({
		editMxgraph: function(ui) {
			ui.sidebar.palettes.flowchart[0].innerHTML = mxResources.get('flowchart');
		}
	});
})(jQuery);