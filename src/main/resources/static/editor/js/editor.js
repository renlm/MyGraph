/***
 * 封装一些常用的方法
 */
(function($) {
	$.extend({
		EditMxgraph: function (UI, GJSON) {
			// 左侧边栏
			UI.hsplitClickEnabled = true;
			
			// 隐藏顶部菜单栏
			UI.menubarHeight = 0;
			UI.menubarContainer.style.display = "none";
			UI.toolbarContainer.firstChild.style.borderTop = "none";
			
			// 隐藏页脚
			UI.footerHeight = 0;
			
			// 网格大小
			UI.editor.graph.gridEnabled = GJSON.gridEnabled;
			UI.editor.graph.gridSize = GJSON.gridSize;
			if(GJSON.gridColor) { UI.editor.graph.view.gridColor = GJSON.gridColor; }
			// 页面视图
			UI.editor.graph.pageVisible = GJSON.pageVisible;
			// 背景色
			UI.editor.graph.background = GJSON.background;
			// 连接箭头
			UI.editor.graph.connectionArrowsEnabled = GJSON.connectionArrowsEnabled;
			// 连接点
			UI.editor.graph.setConnectable(GJSON.connectable);
			// 参考线
			UI.editor.graph.graphHandler.guidesEnabled = GJSON.guidesEnabled;
			// 分页分割线
			UI.editor.graph.pageBreaksVisible = false;
			// 分割线为虚线
			UI.editor.graph.pageBreakDashed = true;
			
			// 国际化
			UI.sidebar.palettes.bpmn[0].innerHTML = mxResources.get('bpmn');
			UI.sidebar.palettes.flowchart[0].innerHTML = mxResources.get('flowchart');
			
			// 自定义Action（插入ER模型）
			UI.actions.put('insertERModel', new Action(mxResources.get('ERModel'), function()
			{
				if (UI.editor.graph.isEnabled() && !UI.editor.graph.isCellLocked(UI.editor.graph.getDefaultParent()))
				{
					var __demo = $.CreateERModelVertexDemo(UI.sidebar, false);
					var __cell = UI.editor.graph.getSelectionCell();
					var __geometry = UI.editor.graph.getCellGeometry(__cell ? __cell : UI.editor.graph.getChildCells()[UI.editor.graph.getChildCells().length - 1]);
					var __cells = UI.editor.graph.importCells([__demo.cell], __geometry ? __geometry.x : 0, __geometry ? (__geometry.y + __geometry.height + 10) : 0);
					UI.editor.graph.clearSelection();
					UI.editor.graph.setSelectionCells(__cells);
					UI.editor.graph.scrollCellToVisible(UI.editor.graph.getSelectionCell());
				}
			})).isEnabled = UI.actions.actions.save.isEnabled;
			
			// 自定义Action（数据库表选择器）
			UI.actions.put('insertDbTable', new Action(mxResources.get('DbTable'), function()
			{
				if (UI.editor.graph.isEnabled() && !UI.editor.graph.isCellLocked(UI.editor.graph.getDefaultParent()))
				{
					dbTableSelector({ id: 'dbTableSelector', width: 850, height: 495 }, 
						function (checkedErs) {
							var __selectionCells = null;
							checkedErs.forEach(function(item, index) {
								var __col = Math.ceil(index % __cols);
			           			var __demo = $.FormatERModel(ui.sidebar, false, item);
								__x = __col === 0 ? __originx : __x;
								__colYMap[__col] = __colYMap[__col] ? __colYMap[__col] : __y;
								__selectionCells = graph.importCells([__demo.cell], __x, __colYMap[__col]);
								__x = __x + __demo.width + __interval;
								__colYMap[__col] = __colYMap[__col] + __demo.height + __interval;
			           		});
							if(__selectionCells) {
								graph.clearSelection();
								graph.setSelectionCells(__selectionCells);
								graph.scrollCellToVisible(graph.getSelectionCell());
							}
							return true;
						});
				}
			})).isEnabled = UI.actions.actions.save.isEnabled;
			
			// 自定义工具栏（插入）
			UI.menus.menus.insert.funct = function(menu, parent)
			{
				UI.menus.addMenuItems(menu, ['insertERModel', 'insertDbTable', 'insertLink', 'insertImage'], parent);
				UI.menus.addSubmenu('layout', menu, parent);
			};
			
			// 重写保存方法
			UI.actions.actions.save.funct = function () {
				if (UI.editor.graph.isEditing()) 
				{
					UI.editor.graph.stopEditing();
				}
				
				try 
				{
					var layerIndex = layer.load(2);
					var xml = mxUtils.getXml(UI.editor.getGraphXml());
					if (xml.length < MAX_REQUEST_SIZE) 
					{
						var __settings = "&uuid=" + GJSON.uuid;
						__settings += "&zoom=" + UI.editor.graph.view.getScale();
						__settings += "&dx=" + UI.editor.graph.view.getTranslate().x;
						__settings += "&dy=" + UI.editor.graph.view.getTranslate().y;
						__settings += "&gridEnabled=" + UI.editor.graph.gridEnabled;
						__settings += "&gridSize=" + UI.editor.graph.gridSize;
						__settings += "&gridColor=" + UI.editor.graph.view.gridColor;
						__settings += "&pageVisible=" + UI.editor.graph.pageVisible;
						__settings += "&background=" + UI.editor.graph.background;
						__settings += "&connectable=" + UI.editor.graph.isConnectable();
						__settings += "&guidesEnabled=" + UI.editor.graph.graphHandler.guidesEnabled;
						__settings += "&connectionArrowsEnabled=" + UI.editor.graph.connectionArrowsEnabled;
						var req = new mxXmlRequest(SAVE_URL, UI.__CSRF + __settings + '&xml=' + Base64.encodeURI(xml), 'POST', false);
						req.send();
						var resJson = JSON.parse(req.request.responseText);
						if(resJson.statusCode == 200) {
							layer.msg(resJson.message?resJson.message:"已保存", { icon: 1, time: 1500 });
						} else {
							layer.msg(resJson.message?resJson.message:"出错了", { icon: 5, shift:6 });
						}
					} 
					else 
					{
						mxUtils.alert(mxResources.get('drawingTooLarge'));
						mxUtils.popup(xml);
						return;
					}
			
					UI.editor.setModified(false);
				} 
				catch (e) 
				{
					UI.editor.setStatus(mxUtils.htmlEntities(mxResources.get('errorSaving')));
				} 
				finally 
				{
					layer.close(layerIndex);
				}
			};
			
			// 重写双击事件
			var graphDblClick = UI.editor.graph.dblClick;
			UI.editor.graph.dblClick = function(evt, cell)
			{
				// ER模型双击事件
				var __JsonERModel = null;
				try {
					var __RegERModel = new RegExp("<div[^>]*?>([^<>]*?)</div>");
					var __IsERModel = cell.value ? __RegERModel.test(cell.value) : false;
					var __JsonStrERModel = __IsERModel ? Base64.decode(cell.value.match(__RegERModel)[1]) : null;
					__JsonERModel = __JsonStrERModel ? JSON.parse(__JsonStrERModel) : null;
				} catch (e) { }
				if(evt && __JsonERModel) {
					console.log(__JsonERModel);
				}
				// 其它
				else if (this.isEnabled())
				{
					graphDblClick.apply(this, arguments);
				}
			};
		},
		/**
		 * 自定义元图Demo-ER模型
		 * @author Renlm
		 * @param sidebar 
		 * @param isAddEntry 
		 * @return {width,height,html,cell,func}
		 */
		CreateERModelVertexDemo: function(sidebar, isAddEntry) {
			var tableName = "Tablename";
			var fields = [
				{ name: "id", 			comment: "主键ID", 			type: -5,	typeName: 'bigserial',	size: 19,	digit: 0,	isNullable: false,	autoIncrement: true,	columnDef: null,	isPk: true,	 isFk: false },
				{ name: "created_at", 	comment: "创建时间", 			type: 93,	typeName: 'timestamp',	size: 29,	digit: 6,	isNullable: false,	autoIncrement: false,	columnDef: 'now()',	isPk: false, isFk: false },
				{ name: "updated_at", 	comment: "更新时间", 			type: 93,	typeName: 'timestamp',	size: 29,	digit: 6,	isNullable: true,	autoIncrement: false,	columnDef: null,	isPk: false, isFk: false },
				{ name: "deleted", 		comment: "是否删除（默认否）", 	type: -7,	typeName: 'bool',		size: 1,	digit: 0,	isNullable: false,	autoIncrement: false,	columnDef: false,	isPk: false, isFk: false },
				{ name: "remark", 		comment: "备注", 				type: 12,	typeName: 'varchar',	size: 255,	digit: 0,	isNullable: true,	autoIncrement: false,	columnDef: null,	isPk: false, isFk: false }
			];
			return $.FormatERModel(sidebar, isAddEntry, { tableName: tableName, comment: tableName, fields: fields });
		},
		/**
		 * 自定义元图-ER模型
		 * @author Renlm
		 * @param sidebar 
		 * @param isAddEntry 
		 * @param erDto {tableName,comment,fields:[{name,comment,isPk,isFk}]}
		 * @return {width,height,html,cell,func}
		 */
		FormatERModel: function(sidebar, isAddEntry, erDto) {
			var formatJson = $.TPLERModel(erDto);
			var resJson = FormatERModelVertexTemplateEntry(sidebar,
				isAddEntry,
				'verticalAlign=top;align=left;overflow=fill;html=1;shadow=1;',
				formatJson.width,
				formatJson.height,
				formatJson.html,
				'ER模型',
				null,
				null,
				'ER Table 模型'
			);
			formatJson.cell = resJson.cell;
			formatJson.func = resJson.func;
			return formatJson;
		},
		/**
		 * 生成Html模板-ER模型
		 * @author Renlm
		 * @param erDto {tableName,comment,fields:[{name,comment,isPk,isFk}]}
		 * @return {width,height,html}
		 */
		TPLERModel: function(erDto) {
			var nums = 8;
			var lines = Array.isArray(erDto.fields) ? erDto.fields.length : 1;
			var template = "<div class='ermodel-json'>" + Base64.encodeURI(JSON.stringify(erDto)) + "</div>";
			template += "<div class='ermodel-name'><p>" + (erDto.comment ? erDto.comment : erDto.tableName) + "</p></div>";
			if (Array.isArray(erDto.fields)) {
				template += "<table class='ermodel-fields'>";
				template += "<tbody>";
				erDto.fields.forEach(function(item, index) {
					if (item.isPk) {
						template += "<tr data-index='" + index + "' class='ermodel-pk'>";
					} else if (item.isFk) {
						template += "<tr data-index='" + index + "' class='ermodel-fk'>";
					} else {
						template += "<tr data-index='" + index + "' class='ermodel-col'>";
					}
					var showName = item.comment ? item.comment : item.name;
					template += "<td class='ermodel-key'></td>";
					template += "<td>" + showName + "</td>";
					template += "</tr>";
					nums = Math.max(nums, showName.length);
				});
				template += "</tbody>";
				template += "</table>";
			}
			template += "</div>";
			var formatJson = {
				width: 180,
				height: Math.max(2, lines) * 18 + 21 + 6
			};
			formatJson.html = template;
			return formatJson;
		}
	});
	/***
     * 通过js触发打开一个数据库表选择器
     * @param opts 需要覆盖的属性
     * @param callback 回调函数
     * @returns {*|jQuery|HTMLElement}
     */
	function dbTableSelector (opts, callback) {
        var myDialogId = opts.id || (new Date()).getTime();
		var width = opts.width ? opts.width : 850;
		var height = opts.height ? opts.height : 495;
        var $myDialog = $("<form id='" + myDialogId + "' style='overflow-x: hidden' ></form>");
        var defaultOptions = {
            id: myDialogId,
            title: opts.title ? opts.title : "数据库",
            closed: false,
			cache: false,
			top: 120,
            width: width,
            height: height,
            href: ctx + "/mxgraph/dbTableSelector",
            buttons: [
				{
		            text: "确定",
		            iconCls: "fa fa-save",
		            btnCls: "topjui-btn-blue",
		            handler: function () {
						var checkedErs = $('#erDatagrid').iDatagrid('getChecked');
						if(!checkedErs || checkedErs.length == 0) {
							$.iMessager.alert('操作提示', '请选择要添加的数据库表', 'messager-error');
							return;
						}
						var opflag = false;
						if(callback) {
							opflag = callback(checkedErs);
						}
						if(opflag) {
							$myDialog.iDialog("destroy");
						}
					}
		        }, 
                {
               		text: "关闭", 
					iconCls: "fa fa-close", 
					btnCls: "topjui-btn-red", 
					handler: function () {
                        $myDialog.iDialog("destroy");
                    }
                }
            ]
        };
        $myDialog.iDialog($.extend(true, {}, defaultOptions, opts));
        return $myDialog;
    }
	/**
	 * 自定义元图-ER模型
	 * @author Renlm
	 * @param sidebar 
	 * @param isAddEntry 
	 * @param style
	 * @param width
	 * @param height
	 * @param value
	 * @param title
	 * @param showLabel
	 * @param showTitle
	 * @param tags
	 * @return {cell,func}
	 */
	function FormatERModelVertexTemplateEntry(sidebar, isAddEntry, style, width, height, value, title, showLabel, showTitle, tags) {
		tags = (tags != null && tags.length > 0) ? tags : title.toLowerCase();
		var cell = new mxCell(value ? value : "", new mxGeometry(0, 0, width, height), style);
		cell.vertex = true;
		var addEntryFunc = function() {
			return FormatERModelVertexTemplate(sidebar, cell, width, height, title, showLabel, showTitle);
		};
		if (isAddEntry || isAddEntry == null) {
			return {
				cell: cell,
				func: sidebar.addEntry(tags, mxUtils.bind(sidebar, addEntryFunc))
			};
		}
		return {
			cell: cell,
			func: addEntryFunc
		};
	}
	/**
	 * 自定义元图-ER模型
	 * @author Renlm
	 * @param sidebar 
	 * @param cell
	 * @param width
	 * @param height
	 * @param title
	 * @param showLabel
	 * @param showTitle
	 * @param allowCellsInserted
	 * @return string
	 */
	function FormatERModelVertexTemplate(sidebar, cell, width, height, title, showLabel, showTitle, allowCellsInserted) {
		return sidebar.createVertexTemplateFromCells([cell], width, height, title, showLabel, showTitle, allowCellsInserted);
	}
})(jQuery);