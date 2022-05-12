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
				var that = this;
				var __JsonERModel = null;
				try {
					var __RegERModel = new RegExp("<div[^>]*?>([^<>]*?)</div>");
					var __IsERModel = cell.value ? __RegERModel.test(cell.value) : false;
					var __JsonStrERModel = __IsERModel ? Base64.decode(cell.value.match(__RegERModel)[1]) : null;
					__JsonERModel = __JsonStrERModel ? JSON.parse(__JsonStrERModel) : null;
				} catch (e) { }
				if(evt && __JsonERModel) {
					erModelViewerDialog({ id: 'erModelViewerDialog' },
						this.isEnabled(),
						__JsonERModel,
						function (erDto) {
							var erModelHtml = $.TPLERModel(erDto);
							that.getModel().beginUpdate();
						 	try {
						  		cell.value = erModelHtml.html;
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
		 * @author Renlm
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
     * 通过js触发打开一个ER模型查看器
     * @param opts 需要覆盖的属性
     * @param editable 是否可编辑
     * @param erDto ER模型
     * @param callback 回调函数
     * @returns {*|jQuery|HTMLElement}
     */
	function erModelViewerDialog (opts, editable, erDto, callback) {
        var myDialogId = opts.id || (new Date()).getTime();
		var myDialogIDatagridId = myDialogId + "Content";
        var $myDialog = $("<form id='" + myDialogId + "' class='myui' style='overflow-x: hidden' ></form>");
		var $buttons = [];
		if(editable) {
			$buttons = [
				{
		            text: "添加行",
		            iconCls: "left fa fa-plus",
		            handler: function () {
						var $myDatagrid = $("#" + myDialogIDatagridId);
						var $selectedRow = $myDatagrid.datagrid('getSelected');
						var $selectedRowIndex = $selectedRow == null ? null : $myDatagrid.datagrid('getRowIndex', $selectedRow);
						if($selectedRowIndex == null) {
							$("#" + myDialogIDatagridId).datagrid('addRow',{
									row:{isNullable:true,autoIncrement:false,isPk:false,isFk:false}
								});
						} else {
							$("#" + myDialogIDatagridId).datagrid('addRow',{
									index:($selectedRowIndex+1),
									row:{isNullable:true,autoIncrement:false,isPk:false,isFk:false}
								});
						}
					}
		        },
				{
		            text: "删除行",
		            iconCls: "left fa fa-minus",
		            handler: function () {
						var $myDatagrid = $("#" + myDialogIDatagridId);
						var checkedErFields = $myDatagrid.datagrid('getChecked');
						if(!checkedErFields || checkedErFields.length == 0) {
							$.iMessager.alert('操作提示', '请选择要删除的字段', 'messager-error');
							return;
						}
						var checkedErFieldRowIndexs = [];
				    	checkedErFields.forEach(function(item) { checkedErFieldRowIndexs.push($myDatagrid.datagrid('getRowIndex', item)); });
						$myDatagrid.datagrid('destroyRow', checkedErFieldRowIndexs);
					}
		        },
				{
		            text: "保存",
		            iconCls: "left fa fa-save",
		            handler: function () {
						$("#" + myDialogIDatagridId).datagrid('saveRow');
					}
		        },
				{
		            text: "取消",
		            iconCls: "left fa fa-mail-reply",
		            handler: function () {
						$("#" + myDialogIDatagridId).datagrid('cancelRow');
					}
		        },
				{
		            text: "确定",
		            iconCls: "fa fa-save",
		            handler: function () {
						var $opflag = false;
						var $myDatagrid = $("#" + myDialogIDatagridId);
						if(callback) {
							erDto.fields = $myDatagrid.datagrid('getData').rows;
							erDto.fields.forEach(function(item) { 
								var $rowIndex = $myDatagrid.datagrid('getRowIndex', item);
								var $eds = $myDatagrid.datagrid('getEditors', $rowIndex);
								$opflag = $opflag ? $opflag : ($eds && $eds.length);
							});
							if($opflag) {
								$.iMessager.alert('操作提示', '请先保存编辑的字段', 'messager-error');
								$opflag = false;
							} else {
								var $isValid = $myDialog.iForm("validate");
								if($isValid) {
									erDto.tableName = $('#__eTableName').iTextbox('getValue');
									erDto.comment = $('#__eComment').iTextbox('getValue');
									$opflag = callback(erDto);
								}
							}
						}
						if($opflag) {
							$myDialog.dialog("destroy");
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
			top: 120,
			collapsible: true,
			minimizable: false,
			maximizable: true,
	        closed: false,
	        cache: false,
			maximized: true,
			modal: true,
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
			        			"border:false," +
			        			"bodyCls:'border_right'\">" +
					 		"<table id='" + myDialogIDatagridId + "'></table>" +
					 	"</div>" +
					 "</div>",
            buttons: $buttons,
			onOpen: function() {
				$myDialog.form("load", erDto);
				var $myDatagrid = $("#" + myDialogIDatagridId);
				if(editable) {
					$myDatagrid.datagrid({
						border:false,
						fit:true,
						fitColumns: true,
						pagination: false,
						autoSave: true,
						data: erDto.fields,
						frozenColumns: [[
							{field:'uuid',title:'UUID',checkbox:true},
							{field:'name',title:'列名',width:150,editor:{type:'textbox',options:{required:true}}},
							{field:'comment',title:'注释',width:160,editor:{type:'textbox',options:{required:true}}},
						]],
						columns: [[
							{field:'type',title:'数据类型',width:120,formatter:$.jdbcTypeFormatter,editor:{type:'combobox',options:{required:true,editable:true,textField:'label',valueField:'value',panelHeight:350,data:[]}}},
							{field:'size',title:'精度',width:60,align:'right',editor:{type:'numberspinner',options:{required:false}}},
							{field:'digit',title:'标度',width:60,align:'right',editor:{type:'numberspinner',options:{required:false}}},
							{field:'isNullable',title:'是否可为空',width:80,align:'center',formatter:$.yesNoFormatter,editor:{type:'combobox',options:{required:true,editable:false,textField:'label',valueField:'value',panelHeight:70,data:[{label:'是',value:true},{label:'否',value:false}]}}},
							{field:'autoIncrement',title:'是否自增',width:80,align:'center',formatter:$.yesNoFormatter,editor:{type:'combobox',options:{required:true,editable:false,textField:'label',valueField:'value',panelHeight:70,data:[{label:'是',value:true},{label:'否',value:false}]}}},
							{field:'columnDef',title:'字段默认值',width:80,editor:{type:'textbox',options:{required:false}}},
							{field:'isPk',title:'主键',width:60,align:'center',formatter:$.yesNoFormatter,editor:{type:'combobox',options:{required:true,editable:false,textField:'label',valueField:'value',panelHeight:70,data:[{label:'是',value:true},{label:'否',value:false}]}}},
							{field:'isFk',title:'外键',width:60,align:'center',formatter:$.yesNoFormatter,editor:{type:'combobox',options:{required:true,editable:false,textField:'label',valueField:'value',panelHeight:70,data:[{label:'是',value:true},{label:'否',value:false}]}}},
							{field:'operate',title:'操作',width:60,align:'center',formatter: function(value,row,index) {
								var operateTpl = '<span onclick="moveUpERModelField(' + index + ');" title="上移" class="fa fa-angle-double-up" style="cursor:pointer;padding-right:15px;"></span>';
								operateTpl += '<span onclick="moveDownERModelField(' + index + ');" title="下移" class="fa fa-angle-double-down" style="cursor:pointer;"></span>';
								return operateTpl;
							}}
						]],
						onBeforeSave: function(index) {
							var $saveFlag = true;
							var $datas = $myDatagrid.datagrid('getData');
							var $ed = $myDatagrid.datagrid('getEditor',{index:index,field:'name'});
							var fieldNameValue = $($ed.target).iTextbox('getValue');
							$datas.rows.forEach(function(item) { 
								var $index = $myDatagrid.datagrid('getRowIndex', item);
								if(!($index === index)) {
									if(item.name === fieldNameValue) {
										$saveFlag = false;
										return $saveFlag;
									}
								}
							});
							if(!$saveFlag) {
								$.iMessager.alert('操作提示', '添加的字段重复了', 'messager-error');
							}
							return $saveFlag;
						},
						onSave: function (index, row) {
							row.isNullable = row.isNullable === 'true';
							row.autoIncrement = row.autoIncrement === 'true';
							row.isPk = row.isPk === 'true';
							row.isFk = row.isFk === 'true';
							$myDatagrid.datagrid('updateRow',{index:index,row:row});
						}
					});
					moveUpERModelField = function(index) {
						var $datas = $myDatagrid.datagrid('getData');
						if(index > 0) {
							var $upData = Object.assign({}, $datas.rows[index-1]);
							var $downData = Object.assign({}, $datas.rows[index]);
							$myDatagrid.datagrid('updateRow',{index:(index-1),row:$downData});
							$myDatagrid.datagrid('updateRow',{index:index,row:$upData});
						}
					};
					moveDownERModelField = function(index) {
						var $datas = $myDatagrid.datagrid('getData');
						if(index < $datas.total) {
							var $upData = Object.assign({}, $datas.rows[index]);
							var $downData = Object.assign({}, $datas.rows[index+1]);
							$myDatagrid.datagrid('updateRow',{index:index,row:$downData});
							$myDatagrid.datagrid('updateRow',{index:(index+1),row:$upData});
						}
					};
				} else {
					$('#__eTableName').textbox('readonly');
					$('#__eComment').textbox('readonly');
					$myDatagrid.datagrid({
						border: false,
						fit: true,
						fitColumns: true,
						pagination: false,
						data: erDto.fields,
						frozenColumns: [[
							{field:'id',title:'序号',formatter:function(value,index){return $myDatagrid.datagrid('getRowIndex',index)+1;}},
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
						var opflag = false;
						if(callback) {
							opflag = callback(checkedErs);
						}
						if(opflag) {
							$myDialog.dialog("destroy");
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