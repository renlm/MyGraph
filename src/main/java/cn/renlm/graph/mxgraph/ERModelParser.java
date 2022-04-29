package cn.renlm.graph.mxgraph;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.common.GraphCategory;
import cn.renlm.graph.modular.er.dto.ErDto;
import cn.renlm.graph.modular.er.entity.ErField;
import cn.renlm.graph.modular.graph.dto.GraphDto;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.service.IGraphService;
import cn.renlm.graph.mxgraph.model.MxCell;
import cn.renlm.graph.mxgraph.model.MxGeometry;
import cn.renlm.graph.mxgraph.model.MxGraphModel;
import cn.renlm.graph.mxgraph.model.Root;
import cn.renlm.graph.security.User;

/**
 * ER图形解析器
 * 
 * @author Renlm
 *
 */
@Component
public class ERModelParser {

	@Autowired
	private IGraphService iGraphService;

	/**
	 * 固定宽度
	 */
	public static final int fixedWidth = 180;

	/**
	 * 最小高度
	 */
	public static final int minHeight = 125;

	/**
	 * 间距
	 */
	public static final int interval = 10;

	/**
	 * 每行列数
	 */
	public static final int cols = 8;

	/**
	 * 元点位置
	 */
	public static final int[] xy = { 200, -80 };

	/**
	 * 根据ER模型创建图形
	 * 
	 * @param user
	 * @param name
	 * @param ers
	 * @return
	 */
	public Graph create(User user, String name, List<ErDto> ers) {
		Graph graph = new Graph();
		graph.setUuid(IdUtil.simpleUUID().toUpperCase());
		MxGraphModel mxGraphModel = this.initMxGraphModel(graph, ers);
		graph.setXml(this.convertMxGraphModelToXml(mxGraphModel));
		graph.setName(name);
		graph.setCategoryCode(GraphCategory.ER.name());
		graph.setCategoryName(GraphCategory.ER.getText());
		GraphDto.fillDefault(graph);
		graph.setCreatedAt(new Date());
		graph.setCreatorUserId(user.getUserId());
		graph.setCreatorNickname(user.getNickname());
		graph.setUpdatedAt(graph.getCreatedAt());
		graph.setDeleted(false);
		iGraphService.save(graph);
		return graph;
	}

	/**
	 * 转换模型为Xml
	 * 
	 * @param mxGraphModel
	 * @return
	 */
	private String convertMxGraphModelToXml(MxGraphModel mxGraphModel) {
		XStream xstream = new XStream();
		xstream.processAnnotations(MxGraphModel.class);
		xstream.allowTypeHierarchy(MxGraphModel.class);
		return xstream.toXML(mxGraphModel);
	}

	/**
	 * 初始化模型
	 * 
	 * @param graph
	 * @param ers
	 * @return
	 */
	private MxGraphModel initMxGraphModel(Graph graph, List<ErDto> ers) {
		AtomicInteger index = new AtomicInteger(0);
		Map<Integer, Integer> colYMap = new LinkedHashMap<>();
		List<MxCell> mxCells = CollUtil.newArrayList();
		MxGraphModel mxGraphModel = new MxGraphModel();
		Root root = new Root();
		mxGraphModel.setRoot(root);
		root.setMxCells(mxCells);

		// 根级父节点
		MxCell parent = new MxCell();
		parent.setId(graph.getUuid());
		mxCells.add(parent);

		// 顶部节点（全选功能）
		MxCell top = new MxCell();
		top.setId(IdUtil.simpleUUID().toUpperCase());
		top.setParent(parent.getId());
		mxCells.add(top);

		// 分列平铺
		List<List<ErDto>> rows = CollUtil.split(ers, cols);
		rows.forEach(row -> {
			row.forEach(er -> {
				int col = index.getAndIncrement() % cols;
				AtomicInteger height = new AtomicInteger(er.getFields().size() * 18 + 21 + 6);
				height.set(NumberUtil.max(minHeight, height.get()));
				if (colYMap.containsKey(col)) {
					colYMap.put(col, colYMap.get(col) + interval);
				} else {
					colYMap.put(col, 0);
				}
				this.createMxCell(mxCells, top, er, colYMap, col, height);
			});
		});
		return mxGraphModel;
	}

	/**
	 * 创建ER元图
	 * 
	 * @param mxCells
	 * @param parent
	 * @param er
	 * @param colYMap
	 * @param col
	 * @param height
	 * @return
	 */
	private MxCell createMxCell(List<MxCell> mxCells, MxCell parent, ErDto er, Map<Integer, Integer> colYMap, int col,
			AtomicInteger height) {
		int h = height.get();
		MxCell mxCell = new MxCell();
		mxCell.setId(er.getUuid());
		mxCell.setParent(parent.getId());
		mxCell.setVertex(1);
		mxCell.setStyle("verticalAlign=top;align=left;overflow=fill;html=1;shadow=1;");
		mxCell.setMxGeometry(this.createMxGeometry(colYMap, col, h));
		this.assembleMxCellValue(mxCell, er);
		mxCells.add(mxCell);
		return mxCell;
	}

	/**
	 * 生成几何形状
	 * 
	 * @param colYMap
	 * @param col
	 * @param h
	 * @return
	 */
	private MxGeometry createMxGeometry(Map<Integer, Integer> colYMap, int col, int h) {
		int w = cols;
		MxGeometry mxGeometry = new MxGeometry();
		if (w % 2 == 0) {
			int shift = col - w / 2;
			mxGeometry.setX(xy[0] + shift * fixedWidth + shift * interval + interval / 2);
		} else {
			int shift = col - w / 2 - 1;
			mxGeometry.setX(xy[0] + shift * fixedWidth + fixedWidth / 2 + shift * interval + interval / 2);
		}
		mxGeometry.setY(xy[1] + colYMap.get(col));
		mxGeometry.setWidth(fixedWidth);
		mxGeometry.setHeight(h);
		colYMap.put(col, colYMap.get(col) + h);
		return mxGeometry;
	}

	/**
	 * 组装元图内容
	 * 
	 * @param mxCell
	 * @param er
	 */
	private void assembleMxCellValue(MxCell mxCell, ErDto er) {
		String template = "<div class='ermodel-json'>" + Base64.encodeUrlSafe(JSONUtil.toJsonStr(er)) + "</div>";
		template += "<div class='ermodel-name'>";
		template += "<p>" + ObjectUtil.defaultIfBlank(er.getComment(), er.getTableName()) + "</p>";
		template += "</div>";
		template += "<table class='ermodel-fields'>";
		template += "<tbody>";
		for (int i = 0; i < er.getFields().size(); i++) {
			ErField item = er.getFields().get(i);
			if (item.getIsPk()) {
				template += "<tr data-index='" + i + "' class='ermodel-pk'>";
			} else if (item.getIsFk()) {
				template += "<tr data-index='" + i + "' class='ermodel-fk'>";
			} else {
				template += "<tr data-index='" + i + "' class='ermodel-col'>";
			}
			template += "<td class='ermodel-key'></td>";
			template += "<td>" + ObjectUtil.defaultIfBlank(item.getComment(), item.getName()) + "</td>";
			template += "</tr>";
		}
		template += "</tbody>";
		template += "</table>";
		template += "</div>";
		mxCell.setValue(template);
	}
}