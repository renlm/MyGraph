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
					var __cols = 8;
					var __interval = 10;
					var __colYMap = {};
					var __cell = UI.editor.graph.getSelectionCell();
					var __geometry = UI.editor.graph.getCellGeometry(__cell ? __cell : UI.editor.graph.getChildCells()[UI.editor.graph.getChildCells().length - 1]);
					var __originx = __geometry ? __geometry.x : -555;
					var __x = __originx;
					var __y = __geometry ? (__geometry.y + __geometry.height + __interval) : 0;
					dbTableSelector({ id: 'dbTableSelector', width: 850, height: 512 }, 
						function (checkedErs) {
							var __selectionCells = null;
							checkedErs.reverse();
							checkedErs.forEach(function(item, index) {
								var __col = Math.ceil(index % __cols);
			           			var __demo = $.FormatERModel(UI.sidebar, false, item);
								__x = __col === 0 ? __originx : __x;
								__colYMap[__col] = __colYMap[__col] ? __colYMap[__col] : __y;
								__selectionCells = UI.editor.graph.importCells([__demo.cell], __x, __colYMap[__col]);
								__x = __x + __demo.width + __interval;
								__colYMap[__col] = __colYMap[__col] + __demo.height + __interval;
			           		});
							if(__selectionCells) {
								UI.editor.graph.clearSelection();
								UI.editor.graph.setSelectionCells(__selectionCells);
								UI.editor.graph.scrollCellToVisible(UI.editor.graph.getSelectionCell());
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
			if (UI.editor.graph.isEnabled()) {
				UI.toolbar.addItems(['-', 'save']);
				UI.saveFile = function () {
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
							__settings += "&version=" + GJSON.version;
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
								GJSON.version = resJson.data.version;
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
			}
			
			// 重写双击事件
			var graphDblClick = UI.editor.graph.dblClick;
			UI.editor.graph.dblClick = function(evt, cell)
			{
				// ER模型双击事件
				var that = this;
				var __JsonERModel = null;
				try {
					var __RegERModel = new RegExp("<div[^>]*?>([^<>]*?)</div>");
					var __IsERModel = cell.value ? __RegERModel.test(cell.value) : false;
					var __JsonStrERModel = __IsERModel ? Base64.decode(cell.value.match(__RegERModel)[1]) : null;
					__JsonERModel = __JsonStrERModel ? JSON.parse(__JsonStrERModel) : null;
				} catch (e) { }
				if(evt && __JsonERModel) {
					mxEvent.consume(evt);
					erModelViewerDialog({ id: 'erModelViewerDialog' },
						this.isEnabled(),
						__JsonERModel,
						function (erDto) {
							var erModelHtml = $.TPLERModel(erDto);
							that.getModel().beginUpdate();
						 	try {
						  		cell.value = erModelHtml.html;
						  		cell.geometry.height = erModelHtml.height;
						        that.refresh();
							} finally {
						        that.getModel().endUpdate();
							}
							return true;
						});
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
		 * @author RenLiMing(任黎明)
		 * @param sidebar 
		 * @param isAddEntry 
		 * @return {width,height,html,cell,func}
		 */
		CreateERModelVertexDemo: function(sidebar, isAddEntry) {
			var tableName = "Tablename";
			var fields = [
				{ name: "id", 			comment: "主键ID", 			sqlType: -5,	jdbcType: 'BIGINT',		size: 19,	digit: 0,	isNullable: false,	autoIncrement: true,	columnDef: null,	isPk: true,	 isFk: false },
				{ name: "created_at", 	comment: "创建时间", 			sqlType: 93,	jdbcType: 'TIMESTAMP',	size: 19,	digit: 0,	isNullable: false,	autoIncrement: false,	columnDef: 'now()',	isPk: false, isFk: false },
				{ name: "updated_at", 	comment: "更新时间", 			sqlType: 93,	jdbcType: 'TIMESTAMP',	size: 19,	digit: 0,	isNullable: true,	autoIncrement: false,	columnDef: null,	isPk: false, isFk: false },
				{ name: "deleted", 		comment: "是否删除（默认否）", 	sqlType: -7,	jdbcType: 'BIT',		size: 1,	digit: 0,	isNullable: false,	autoIncrement: false,	columnDef: false,	isPk: false, isFk: false },
				{ name: "remark", 		comment: "备注", 				sqlType: 12,	jdbcType: 'VARCHAR',	size: 255,	digit: 0,	isNullable: true,	autoIncrement: false,	columnDef: null,	isPk: false, isFk: false }
			];
			return $.FormatERModel(sidebar, isAddEntry, { tableName: tableName, comment: tableName, fields: fields });
		},
		/**
		 * 自定义元图-ER模型
		 * @author RenLiMing(任黎明)
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
		 * @author RenLiMing(任黎明)
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
     * 通过js触发打开一个ER模型查看器
     * @param opts 需要覆盖的属性
     * @param editable 是否可编辑
     * @param erDto ER模型
     * @param callback 回调函数
     * @returns {*|jQuery|HTMLElement}
     */
	function erModelViewerDialog (opts, editable, erDto, callback) {
        var myDialogId = opts.id || (new Date()).getTime();
		var myDialogDatagridId = myDialogId + "Content";
        var $myDialog = $("<form id='" + myDialogId + "' class='myui' style='overflow-x: hidden' ></form>");
		var $buttons = [];
		if(editable) {
			// 刷新行序号
			refreshAllRowSeq = function () {
				var $myDatagrid = $("#" + myDialogDatagridId);
				var rowDatas = $myDatagrid.datagrid('getRows');
				$.each(rowDatas, function (index, row) {
					if (row) { }
					$('tr[datagrid-row-index=' + index + ']').find('td[field=id]>div.datagrid-cell').html(index + 1);
				});
			};
			// 检查重复字段
			checkEditorRepeat = function () {
				var isRepeat = false;
				var $myDatagrid = $("#" + myDialogDatagridId);
				var rowDatas = $myDatagrid.datagrid('getRows');
				var rowDataMap = {};
				$.each(rowDatas, function (index, row) {
					if (rowDataMap[row.name]) {
						var existRow = rowDataMap[row.name];
						$.messager.myuiAlert('操作提示', '第 ' + (Math.min(existRow.$index, index) + 1) + ' 行，第 ' + (Math.max(existRow.$index, index) + 1) + ' 行，' + row.name + ' 字段重复！', 'error');
						isRepeat = true;
						return false;
					} else {
						row.$index = index;
						rowDataMap[row.name] = row;
					}
				});
				return isRepeat;
			};
			// 是否编辑状态
			isEditing = function () {
				var $myDatagrid = $("#" + myDialogDatagridId);
				var rowDatas = $myDatagrid.datagrid('getRows');
				var inEditing = false;
				$.each(rowDatas, function (index, row) {
					if (index) { }
					if (row.editing) {
						inEditing = true;
					}
				});
				return inEditing;
			};
			// 编辑行
			editRow = function (target) {
				var tr = $(target).closest('tr.datagrid-row');
      			var index = parseInt(tr.attr('datagrid-row-index'));
				var $myDatagrid = $("#" + myDialogDatagridId);
				var rows = $myDatagrid.datagrid('getRows');
				var row = rows[index];
				row.$id = Math.max(1, index);
				$myDatagrid.datagrid('beginEdit', index);
			};
			// 保存行
			saveRow = function (target) {
				var tr = $(target).closest('tr.datagrid-row');
      			var index = parseInt(tr.attr('datagrid-row-index'));
				var $myDatagrid = $("#" + myDialogDatagridId);
				var isValid = true;
				var rows = $myDatagrid.datagrid('getRows');
				var row = rows[index];
				var editors = $myDatagrid.datagrid('getEditors', index);
				$.each(editors, function (j, e) {
					if (j) {}
					if (isValid && !$(e.target).textbox('isValid')) {
						isValid = false;
						$(e.target).textbox('textbox').focus();
						return;
					}
				});
				if (isValid) {
					$myDatagrid.datagrid('endEdit', index);
					$myDatagrid.datagrid('updateRow', { 
						index: index, 
						row: {
							isNullable: row.isNullable === 'true',
							autoIncrement: row.autoIncrement === 'true',
							isPk: row.isPk === 'true',
							isFk: row.isFk === 'true',
							jdbcType: JDBC_TYPE[row.sqlType]
						}
					});
				}
			};
			// 取消编辑
			cancelRow = function (target) {
				var tr = $(target).closest('tr.datagrid-row');
      			var index = parseInt(tr.attr('datagrid-row-index'));
				var $myDatagrid = $("#" + myDialogDatagridId);
				var rows = $myDatagrid.datagrid('getRows');
				var row = rows[index];
				if (row.$id) {
					$myDatagrid.datagrid('cancelEdit', index);
				}
			};
			// 暂存数据
			var saveTemporaryData = function (isSave) {
				var $myDatagrid = $("#" + myDialogDatagridId);
				var rowDatas = $myDatagrid.datagrid('getRows');
				var isValid = true;
				var selectRow1 = null;
				var selectRow2 = null;
				$.each(rowDatas, function (index, row) {
					if (row.editing) {
						var editors = $myDatagrid.datagrid('getEditors', index);
						$.each(editors, function (j, e) {
							if (j) {}
							if (isValid && !$(e.target).textbox('isValid')) {
								isValid = false;
								selectRow1 = index;
								$(e.target).textbox('textbox').focus();
								return;
							}
						});
						if (isValid && (isSave || isSave == null)) {
							selectRow2 = index;
							$myDatagrid.datagrid('endEdit', index);
							$myDatagrid.datagrid('updateRow', { 
								index: index, 
								row: {
									isNullable: row.isNullable === 'true',
									autoIncrement: row.autoIncrement === 'true',
									isPk: row.isPk === 'true',
									isFk: row.isFk === 'true',
									jdbcType: JDBC_TYPE[row.sqlType]
								}
							});
						}
					}
				});
				if (isValid) {
					if (selectRow2 === 0 || selectRow2) {
						$myDatagrid.datagrid('selectRow', selectRow2);
					}
				} else {
					if (selectRow1 === 0 || selectRow1) {
						$myDatagrid.datagrid('selectRow', selectRow1);
					}
				}
				return isValid;
			};
			$buttons = [
				{
		            text: "添加行",
		            iconCls: "left fa fa-plus",
		            handler: function () {
						if (isEditing() && !saveTemporaryData(false)) {
							return;
						}
						var $myDatagrid = $("#" + myDialogDatagridId);
						var rowDatas = $myDatagrid.datagrid('getRows');
						var $selectedRow = $myDatagrid.datagrid('getSelected');
						var $selectedRowIndex = $selectedRow == null ? rowDatas.length : $myDatagrid.datagrid('getRowIndex', $selectedRow) + 1;
						$myDatagrid.datagrid('insertRow', { index: $selectedRowIndex, row: { isNullable: true, autoIncrement: false, isPk: false, isFk: false } });
						$myDatagrid.datagrid('beginEdit', $selectedRowIndex);
						refreshAllRowSeq();
					}
		        },
				{
		            text: "删除行",
		            iconCls: "left fa fa-minus",
		            handler: function () {
						var $myDatagrid = $("#" + myDialogDatagridId);
						var checkedErFields = $myDatagrid.datagrid('getChecked');
						if(!checkedErFields || checkedErFields.length == 0) {
							$.messager.myuiAlert('操作提示', '请勾选要删除的字段', 'error');
							return;
						}
						$.messager.myuiConfirm('操作提示', '您确定要删除选中的字段吗？', function (r) {
							if (r) {
								checkedErFields.forEach(function (checkedRow) { 
									var index = $myDatagrid.datagrid('getRowIndex', checkedRow);
									$myDatagrid.datagrid('deleteRow', index); 
								});
								refreshAllRowSeq();
							}
						});
					}
		        },
				{
		            text: "添加字段",
		            iconCls: "left fa fa-plus",
		            handler: function () {
						dbFieldLibSelector({ id: 'dbFieldLibSelector', width: 850, height: 512 }, 
						function (checkedErFields) {
							var $myDatagrid = $("#" + myDialogDatagridId);
							var rowDatas = $myDatagrid.datagrid('getRows');
							var $selectedRow = $myDatagrid.datagrid('getSelected');
							var $selectedRowIndex = $selectedRow == null ? rowDatas.length : $myDatagrid.datagrid('getRowIndex', $selectedRow) + 1;
							$.each(checkedErFields, function (index, row) {
								if (index) { }
								$myDatagrid.datagrid('insertRow', { index: $selectedRowIndex, row: row });
								$myDatagrid.datagrid('beginEdit', $selectedRowIndex++);
							});
							refreshAllRowSeq();
							return true;
						});
					}
		        },
				{
		            text: "暂存数据",
		            iconCls: "left fa fa-save",
		            handler: saveTemporaryData
		        },
				{
		            text: "取消编辑",
		            iconCls: "left fa fa-mail-reply",
		            handler: function () {
						var $myDatagrid = $("#" + myDialogDatagridId);
						var rowDatas = $myDatagrid.datagrid('getRows');
						$.each(rowDatas, function (index, row) {
							if (row.editing && row.$id) {
								$myDatagrid.datagrid('cancelEdit', index);
							}
						});
					}
		        },
				{
		            text: "确定",
		            iconCls: "fa fa-save",
		            handler: function () {
						if (isEditing()) {
							$.messager.myuiAlert('操作提示', '请完成字段编辑！', 'error');
							return;
						}
						if (checkEditorRepeat()) {
							return;
						}
						var $opflag = false;
						var $myDatagrid = $("#" + myDialogDatagridId);
						if(callback) {
							var $isValid = $myDialog.form("validate");
							if($isValid && saveTemporaryData()) {
								erDto.tableName = $('#__eTableName').textbox('getValue');
								erDto.comment = $('#__eComment').textbox('getValue');
								erDto.fields = $myDatagrid.datagrid('getRows');
								$opflag = callback(erDto);
								if($opflag) {
									$myDialog.dialog("destroy");
								} else {
									$.messager.myuiAlert('操作提示', '出错了', 'error');
								}
							}
						}
					}
		        },
                {
               		text: "关闭", 
					iconCls: "fa fa-close", 
					handler: function () {
                        $myDialog.dialog("destroy");
                    }
                }
            ];
		} else {
			$buttons = [
                {
               		text: "关闭", 
					iconCls: "fa fa-close", 
					handler: function () {
                        $myDialog.dialog("destroy");
                    }
                }
            ];
		}
        var defaultOptions = {
            title: opts.title ? opts.title : (erDto.comment ? erDto.comment : erDto.tableName),
	        width: 890,
	        height: 483,
			collapsible: false,
			minimizable: false,
			maximizable: false,
			closable: editable ? true : false,
	        closed: false,
	        cache: false,
			maximized: true,
			modal: true,
			cls: "no-border no-border-radius",
            content: "<div class=\"easyui-layout\" data-options=\"fit:true\">" +
						"<div data-options=\"region:'north'," +
								"border:false," +
			        			"height:'51px'," +
			        			"bodyCls:'border_bottom'\">" +
					 		"<div class='myui-fluid mlr25center'>" + 
								"<div class='myui-row'></div>" + 
							 	"<div class='myui-row'>" + 
							    	"<div class='myui-col-sm6'>" + 
							        	"<label class='myui-form-label'>表名</label>" + 
							            "<div class='myui-input-block'>" + 
							                "<input id='__eTableName' type='text' name='tableName' " + 
							                	"class='easyui-textbox'" + 
												"data-options=\"required:true,prompt:'表名',width:'100%'\"" + 
							                	"/>" + 
							            "</div>" + 
							        "</div>" + 
									"<div class='myui-col-sm6'>" + 
							        	"<label class='myui-form-label'>注释</label>" + 
							            "<div class='myui-input-block'>" + 
							                "<input id='__eComment' type='text' name='comment' " + 
							                	"class='easyui-textbox'" + 
												"data-options=\"required:true,prompt:'注释',width:'100%'\"" + 
							                	"/>" + 
							            "</div>" + 
							        "</div>" +
								"</div>" + 
							 "</div>" + 
					 	"</div>" +
					 	"<div data-options=\"region:'center'," +
			        			"fit:false," +
			        			"border:false\">" +
					 		"<table id='" + myDialogDatagridId + "'></table>" +
					 	"</div>" +
					 "</div>",
            buttons: $buttons,
			onOpen: function() {
				$myDialog.form("load", erDto);
				var $myDatagrid = $("#" + myDialogDatagridId);
				if(editable) {
					$myDatagrid.datagrid({
						border: false,
						fit: true,
						fitColumns: true,
						singleSelect: true,
						checkOnSelect: false,
						selectOnCheck: false,
						pagination: false,
						autoSave: true,
						data: erDto.fields,
						frozenColumns: [[
							{field:'uuid',title:'UUID',checkbox:true},
							{field:'id',title:'序号',formatter:function(value,row,index){if(value&&index){}return $myDatagrid.datagrid('getRowIndex',row)+1;}},
							{field:'name',title:'列名',width:150,editor:{type:'textbox',options:{required:true}}},
							{field:'comment',title:'注释',width:160,editor:{type:'textbox',options:{required:true}}},
						]],
						columns: [[
							{field:'sqlType',title:'数据类型',width:120,formatter:function(value,row,index){if(value&&index){}return JDBC_TYPE[row.sqlType];},editor:{type:'combobox',options:{required:true,editable:true,textField:'label',valueField:'value',panelHeight:350,data:(function(){var data=[];$.each(JDBC_TYPE,function(key,text){data.push({label:text,value:key});});return data;})()}}},
							{field:'size',title:'长度',width:60,align:'right',editor:{type:'numberspinner',options:{required:false}}},
							{field:'digit',title:'精度',width:60,align:'right',editor:{type:'numberspinner',options:{required:false}}},
							{field:'isNullable',title:'是否可为空',width:80,align:'center',formatter:$.yesNoFormatter,editor:{type:'combobox',options:{required:true,editable:false,textField:'label',valueField:'value',panelHeight:70,data:[{label:'是',value:true},{label:'否',value:false}]}}},
							{field:'autoIncrement',title:'是否自增',width:80,align:'center',formatter:$.yesNoFormatter,editor:{type:'combobox',options:{required:true,editable:false,textField:'label',valueField:'value',panelHeight:70,data:[{label:'是',value:true},{label:'否',value:false}]}}},
							{field:'columnDef',title:'字段默认值',width:80,editor:{type:'textbox',options:{required:false}}},
							{field:'isPk',title:'主键',width:60,align:'center',formatter:$.yesNoFormatter,editor:{type:'combobox',options:{required:true,editable:false,textField:'label',valueField:'value',panelHeight:70,data:[{label:'是',value:true},{label:'否',value:false}]}}},
							{field:'isFk',title:'外键',width:60,align:'center',formatter:$.yesNoFormatter,editor:{type:'combobox',options:{required:true,editable:false,textField:'label',valueField:'value',panelHeight:70,data:[{label:'是',value:true},{label:'否',value:false}]}}},
							{
								field: 'actions',
	                    		title: '操作',
	                    		width: 100,
	                    		align: 'center',
	                    		formatter: function (value, row, index) {
									if (value && index) { }
									if (row.editing) {
										var s = '<a href=\'javascript:void(0);\' onclick=\'saveRow(this)\' class=\'l-btn myui-btn-blue l-btn-small l-btn-plain\'><span class=\'l-btn-left l-btn-icon-left\'><span class=\'l-btn-text\'>保存</span><span class=\'l-btn-icon fa fa-save\'></span></span></a> ';
										var c = '<a href=\'javascript:void(0);\' onclick=\'cancelRow(this)\' class=\'l-btn myui-btn-brown l-btn-small l-btn-plain\'><span class=\'l-btn-left l-btn-icon-left\'><span class=\'l-btn-text\'>撤销</span><span class=\'l-btn-icon fa fa-mail-reply\'></span></span></a>';
										if (row.$id) {
											return s + c;
										} else {
											return s;
										}
									} else {
										var dnd = '<a href=\'javascript:void(0);\' class=\'l-btn myui-btn-purple l-btn-small l-btn-plain\'><span class=\'l-btn-left l-btn-icon-left\'><span class=\'l-btn-text\'>拖拽</span><span class=\'l-btn-icon fa fa-arrows-v\'></span></span></a> ';
										var e = '<a href=\'javascript:void(0);\' onclick=\'editRow(this)\' class=\'l-btn myui-btn-green l-btn-small l-btn-plain\'><span class=\'l-btn-left l-btn-icon-left\'><span class=\'l-btn-text\'>编辑</span><span class=\'l-btn-icon fa fa-pencil\'></span></span></a>';
										return dnd + e;
									}
								}
							}
						]],
						onLoadSuccess: function() {
							$(this).datagrid('enableDnd');
						},
						onBeforeDrag: function (row) {
							return !row.editing;
						},
						onDrop: function (targetRow, sourceRow, point) {
							if (targetRow && sourceRow && point) {}
							refreshAllRowSeq();
						},
						onDblClickRow: function (index, row) {
							if (!row.editing) {
								row.$id = Math.max(1, index);
								$(this).datagrid('beginEdit', index);
							}
						},
						onBeforeEdit: function (index, row) {
							row.editing = true;
							$(this).datagrid('refreshRow', index);
						},
						onAfterEdit: function (index, row) {
							row.editing = false;
							$(this).datagrid('refreshRow', index);
						},
						onCancelEdit: function (index, row) {
							row.editing = false;
							$(this).datagrid('refreshRow', index);
						}
					});
				} else {
					$('#__eTableName').textbox('readonly');
					$('#__eComment').textbox('readonly');
					$myDatagrid.datagrid({
						border: false,
						fit: true,
						fitColumns: true,
						singleSelect: true,
						checkOnSelect: false,
						selectOnCheck: false,
						pagination: false,
						data: erDto.fields,
						frozenColumns: [[
							{field:'id',title:'序号',formatter:function(value,row,index){if(value&&index){}return $myDatagrid.datagrid('getRowIndex',row)+1;}},
							{field:'name',title:'列名',width:150},
							{field:'comment',title:'注释',width:160},
						]],
						columns: [[
							{field:'jdbcType',title:'数据类型',width:120,formatter:$.jdbcTypeFormatter},
							{field:'size',title:'长度',width:60,align:'right'},
							{field:'digit',title:'精度',width:60,align:'right'},
							{field:'isNullable',title:'是否可为空',width:80,align:'center',formatter:$.yesNoFormatter},
							{field:'autoIncrement',title:'是否自增',width:80,align:'center',formatter:$.yesNoFormatter},
							{field:'columnDef',title:'字段默认值',width:80},
							{field:'isPk',title:'主键',width:60,align:'center',formatter:$.yesNoFormatter},
							{field:'isFk',title:'外键',width:60,align:'center',formatter:$.yesNoFormatter}
						]]
					});
				}
			},
			onClose: function () {
				$myDialog.dialog('destroy');
			}
        };
        $myDialog.myuiDialog($.extend(true, {}, defaultOptions, opts));
        return $myDialog;
    }
	/***
     * 通过js触发打开一个数据库表选择器
     * @param opts 需要覆盖的属性
     * @param callback 回调函数
     * @returns {*|jQuery|HTMLElement}
     */
	function dbTableSelector (opts, callback) {
        var myDialogId = opts.id || (new Date()).getTime();
		var width = opts.width ? opts.width : 850;
		var height = opts.height ? opts.height : 512;
        var $myDialog = $("<form id='" + myDialogId + "' class='myui' style='overflow-x: hidden' ></form>");
        var defaultOptions = {
            title: opts.title ? opts.title : "数据库",
			top: 120,
            width: width,
            height: height,
			collapsible: true,
			minimizable: false,
			maximizable: true,
        	closed: false,
        	cache: false,
            href: ctx + "/graph/dbTableSelector",
			modal: true,
            buttons: [
				{
		            text: "确定",
		            iconCls: "fa fa-save",
		            handler: function () {
						var checkedErs = $('#erDataGrid').datagrid('getChecked');
						if(!checkedErs || checkedErs.length == 0) {
							$.messager.myuiAlert('操作提示', '请选择要添加的数据库表', 'error');
							return;
						}
						var $index = layer.load(2);
						setTimeout(function () {
							var opflag = false;
							if(callback) {
								opflag = callback(checkedErs);
							}
							layer.close($index);
							if(opflag) {
								$myDialog.dialog("destroy");
							}
						}, 100);
					}
		        }, 
                {
               		text: "关闭", 
					iconCls: "fa fa-close", 
					handler: function () {
                        $myDialog.dialog("destroy");
                    }
                }
            ],
			onClose: function () {
				$myDialog.dialog('destroy');
			}
        };
        $myDialog.myuiDialog($.extend(true, {}, defaultOptions, opts));
        return $myDialog;
    }
	/***
     * 通过js触发打开一个字段选择器（我的字段库）
     * @param opts 需要覆盖的属性
     * @param callback 回调函数
     * @returns {*|jQuery|HTMLElement}
     */
	function dbFieldLibSelector (opts, callback) {
        var myDialogId = opts.id || (new Date()).getTime();
		var width = opts.width ? opts.width : 850;
		var height = opts.height ? opts.height : 512;
        var $myDialog = $("<form id='" + myDialogId + "' class='myui' style='overflow-x: hidden' ></form>");
        var defaultOptions = {
            title: opts.title ? opts.title : "我的字段库",
			top: 120,
            width: width,
            height: height,
			collapsible: true,
			minimizable: false,
			maximizable: true,
        	closed: false,
        	cache: false,
            href: ctx + "/graph/dbFieldLibSelector",
			modal: true,
            buttons: [
				{
		            text: "确定",
		            iconCls: "fa fa-save",
		            handler: function () {
						var checkedErFields = $('#erFieldLibDataGrid').datagrid('getChecked');
						if(!checkedErFields || checkedErFields.length == 0) {
							$.messager.myuiAlert('操作提示', '请选择要添加的字段', 'error');
							return;
						}
						var $index = layer.load(2);
						setTimeout(function () {
							var opflag = false;
							if(callback) {
								opflag = callback(checkedErFields);
							}
							layer.close($index);
							if(opflag) {
								$myDialog.dialog("destroy");
							}
						}, 100);
					}
		        }, 
                {
               		text: "关闭", 
					iconCls: "fa fa-close", 
					handler: function () {
                        $myDialog.dialog("destroy");
                    }
                }
            ],
			onClose: function () {
				$myDialog.dialog('destroy');
			}
        };
        $myDialog.myuiDialog($.extend(true, {}, defaultOptions, opts));
        return $myDialog;
    }
	/**
	 * 自定义元图-ER模型
	 * @author RenLiMing(任黎明)
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
	 * @author RenLiMing(任黎明)
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