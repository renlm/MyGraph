package cn.renlm.graph.mxgraph;

import java.awt.Rectangle;
import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.thoughtworks.xstream.XStream;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONUtil;
import cn.renlm.graph.common.Mxgraph;
import cn.renlm.graph.common.TreeState;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.doc.entity.DocCategory;
import cn.renlm.graph.modular.doc.service.IDocCategoryService;
import cn.renlm.graph.modular.er.dto.ErDto;
import cn.renlm.graph.modular.er.entity.ErField;
import cn.renlm.graph.modular.er.service.IErService;
import cn.renlm.graph.modular.graph.dto.GraphDto;
import cn.renlm.graph.modular.graph.entity.Graph;
import cn.renlm.graph.modular.graph.service.IGraphService;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.mxgraph.model.MxCell;
import cn.renlm.graph.mxgraph.model.MxGeometry;
import cn.renlm.graph.mxgraph.model.MxGraphModel;
import cn.renlm.graph.mxgraph.model.Root;
import cn.renlm.graph.mxgraph.model.UserObject;
import cn.renlm.graph.response.Result;
import cn.renlm.graph.util.FreemarkerUtil;

/**
 * ER图形解析器
 * 
 * @author Renlm
 *
 */
@Component
public class ERModelParser {

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

	@Autowired
	private IErService iErService;

	@Autowired
	private IGraphService iGraphService;

	@Autowired
	private IDocCategoryService iDocCategoryService;

	/**
	 * 获取图形尺寸
	 * 
	 * @param xml
	 * @return
	 */
	public static final Rectangle getRectangle(String xml) {
		Assert.notBlank(xml, "xml内容不能为空");
		XStream xstream = new XStream();
		xstream.processAnnotations(MxGraphModel.class);
		xstream.allowTypeHierarchy(MxGraphModel.class);
		xstream.ignoreUnknownElements();
		MxGraphModel mxGraphModel = (MxGraphModel) xstream.fromXML(xml);
		Root root = mxGraphModel.getRoot();
		Double ix = null;
		Double iy = null;
		Double ex = null;
		Double ey = null;
		List<MxCell> mxCells = CollUtil.newArrayList();
		if (CollUtil.isNotEmpty(root.getMxCells())) {
			mxCells.addAll(root.getMxCells());
		}
		if (CollUtil.isNotEmpty(root.getUserObjects())) {
			for (UserObject userObject : root.getUserObjects()) {
				if (CollUtil.isNotEmpty(userObject.getMxCells())) {
					mxCells.addAll(userObject.getMxCells());
				}
			}
		}
		if (CollUtil.isNotEmpty(mxCells)) {
			double dx = 0;
			double dy = 0;
			for (MxCell mxCell : mxCells) {
				MxGeometry mxGeometry = mxCell.getMxGeometry();
				if (mxGeometry == null) {
					continue;
				} else {
					Double x = mxGeometry.getX();
					Double y = mxGeometry.getY();
					Double w = mxGeometry.getWidth();
					Double h = mxGeometry.getHeight();
					if (x != null) {
						if (ix == null) {
							ix = x - dx;
							ex = w == null ? x : (x + w);
						} else {
							ix = Math.min(ix, x - dx);
							ex = w == null ? Math.max(ex, x) : Math.max(ex, x + w);
						}
						if (dx > 0) {
							dx = 0;
						}
					} else if (w != null) {
						dx = w;
					}
					if (y != null) {
						if (iy == null) {
							iy = y - dy;
							ey = h == null ? y : (y + h);
						} else {
							iy = Math.min(iy, y - dy);
							ey = h == null ? Math.max(ey, y) : Math.max(ey, y + h);
						}
						if (dy > 0) {
							dy = 0;
						}
					} else if (h != null) {
						dy = h;
					}
				}
			}
		}
		int _ix = ix == null ? 0 : Convert.toInt(ix > 0 ? Math.ceil(ix) : Math.floor(ix));
		int _iy = iy == null ? 0 : Convert.toInt(iy > 0 ? Math.ceil(iy) : Math.floor(iy));
		int _ex = ex == null ? 0 : Convert.toInt(ex > 0 ? Math.ceil(ex) : Math.floor(ex));
		int _ey = ey == null ? 0 : Convert.toInt(ey > 0 ? Math.ceil(ey) : Math.floor(ey));
		return new Rectangle(_ix, _iy, _ex - _ix, _ey - _iy);
	}

	/**
	 * 根据ER模型生成DDL
	 * 
	 * @param graphUuid
	 * @return
	 */
	public SysFile generateDDL(String graphUuid) {
		SysFile sysFile = new SysFile();
		// 获取图形信息
		Graph graph = iGraphService.getOne(Wrappers.<Graph>lambdaQuery().eq(Graph::getUuid, graphUuid));
		String fileName = CollUtil.getLast(StrUtil.splitTrim(graph.getName(), StrUtil.SLASH));
		String date = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MINUTE_PATTERN);
		sysFile.setOriginalFilename(FileNameUtil.cleanInvalid(fileName + StrUtil.DOT + date + ".zip"));
		// 创建临时目录
		File temp = FileUtil.createTempFile("DDL", IdUtil.getSnowflakeNextIdStr(), true);
		temp.delete();
		temp.mkdir();
		String folder = FileUtil.normalize(temp.getAbsolutePath());
		// 生成DDL
		List<ErDto> ers = CollUtil.newArrayList();
		if (StrUtil.isNotBlank(graph.getXml())) {
			String unescape = HtmlUtil.unescape(graph.getXml());
			List<String> strs = ReUtil.findAllGroup1("<div class='ermodel-json'[^>]*?>([^<>]*?)</div>", unescape);
			strs.forEach(str -> {
				String decodeStr = Base64.decodeStr(str);
				if (JSONUtil.isTypeJSONObject(decodeStr)) {
					ers.add(JSONUtil.toBean(decodeStr, ErDto.class));
				}
			});
		}
		// MySQL
		String MySQL = FreemarkerUtil.read("ftl/MySQL.DDL.ftl", "ers", ers);
		FileUtil.writeUtf8String(MySQL, folder + File.separator + "MySQL.sql");
		// 压缩文件夹
		File zip = ZipUtil.zip(temp);
		sysFile.setFileContent(FileUtil.readBytes(zip));
		temp.delete();
		return sysFile;
	}

	/**
	 * 根据ER模型创建图形
	 * 
	 * @param user
	 * @param erUuids
	 * @param form
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Result<?> create(User user, List<String> erUuids, GraphDto form) {
		if (CollUtil.isEmpty(erUuids)) {
			return Result.error("请选择要生成ER图的表");
		}
		if (StrUtil.isBlank(form.getDocProjectUuid())) {
			return Result.error("请选择文档项目");
		}
		if (form.getDocCategoryId() == null) {
			return Result.error("请选择归属分类");
		}
		DocCategory parent = iDocCategoryService.getById(form.getDocCategoryId());
		DocCategory docCategory = new DocCategory();
		docCategory.setPid(parent.getId());
		docCategory.setText(form.getName());
		docCategory.setState(TreeState.open.name());
		docCategory.setRemark(form.getRemark());
		Result<DocCategory> result = iDocCategoryService.ajaxSave(user, form.getDocProjectUuid(), docCategory);
		if (BooleanUtil.isFalse(result.isSuccess())) {
			return result;
		}
		List<ErDto> ers = iErService.findListWithFields(erUuids);
		form.setUuid(docCategory.getUuid());
		MxGraphModel mxGraphModel = this.initMxGraphModel(form, ers);
		form.setXml(Base64.encodeUrlSafe(this.convertMxGraphModelToXml(mxGraphModel)));
		form.setCategoryCode(Mxgraph.ER.name());
		form.setCategoryName(Mxgraph.ER.getText());
		return iGraphService.saveEditor(user, form);
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
		xstream.ignoreUnknownElements();
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
			mxGeometry.setX(Double.valueOf(xy[0] + shift * fixedWidth + shift * interval + interval / 2));
		} else {
			int shift = col - w / 2 - 1;
			mxGeometry.setX(
					Double.valueOf(xy[0] + shift * fixedWidth + fixedWidth / 2 + shift * interval + interval / 2));
		}
		mxGeometry.setY(Double.valueOf(xy[1] + colYMap.get(col)));
		mxGeometry.setWidth(Double.valueOf(fixedWidth));
		mxGeometry.setHeight(Double.valueOf(h));
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