/***
 * 封装一些常用的方法
 */
(function($) {
	$.extend({
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
			return FormatERModel(sidebar, isAddEntry, { tableName: tableName, comment: tableName, fields: fields });
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
			var formatJson = __TPLERModel(erDto);
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