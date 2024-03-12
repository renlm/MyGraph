package cn.renlm.graph.modular.sys.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.renlm.graph.common.TreeState;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.modular.sys.mapper.SysDictMapper;
import cn.renlm.graph.modular.sys.service.ISysDictService;
import cn.renlm.graph.modular.sys.service.ISysFileService;
import cn.renlm.graph.util.TreeExtraUtil;
import cn.renlm.plugins.MyExcelUtil;
import cn.renlm.plugins.MyExcel.handler.DataWriterHandler;
import cn.renlm.plugins.MyExcel.reader.AbstractReader;
import cn.renlm.plugins.MyResponse.Result;
import lombok.SneakyThrows;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author RenLiMing(任黎明)
 * @since 2022-04-29
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

	@Autowired
	private ISysFileService iSysFileService;

	@Override
	public List<SysDict> findListByPid(Long pid) {
		return this.list(Wrappers.<SysDict>lambdaQuery().func(wrapper -> {
			if (pid == null) {
				wrapper.isNull(SysDict::getPid);
				wrapper.eq(SysDict::getLevel, 1);
			} else {
				wrapper.eq(SysDict::getPid, pid);
			}
			wrapper.orderByAsc(SysDict::getSort);
			wrapper.orderByAsc(SysDict::getId);
		}));
	}

	@Override
	public List<SysDict> findFathers(Long id) {
		if (id == null) {
			return CollUtil.newArrayList();
		}
		SysDict sysDict = this.getById(id);
		List<SysDict> list = CollUtil.newArrayList(sysDict);
		int level = sysDict.getLevel();
		while (--level > 0) {
			list.add(this.getById(CollUtil.getLast(list).getPid()));
		}
		return CollUtil.reverse(list);
	}

	@Override
	public List<SysDict> findListByPath(String... codePaths) {
		List<SysDict> list = CollUtil.newArrayList();
		for (String code : codePaths) {
			SysDict node = this.getOne(Wrappers.<SysDict>lambdaQuery().func(wrapper -> {
				wrapper.eq(SysDict::getCode, code);
				if (CollUtil.isEmpty(list)) {
					wrapper.eq(SysDict::getLevel, 1);
				} else {
					wrapper.eq(SysDict::getPid, CollUtil.getLast(list).getId());
				}
			}));
			if (node == null) {
				return CollUtil.newArrayList();
			} else {
				list.add(node);
			}
		}
		return list;
	}

	@Override
	public List<Tree<Long>> getTree(boolean root, String... codePaths) {
		Long pid = null;
		AtomicLong atomic = new AtomicLong();
		if (codePaths.length > 0) {
			List<SysDict> nodes = this.findListByPath(codePaths);
			if (CollUtil.isEmpty(nodes)) {
				return CollUtil.newArrayList();
			} else {
				pid = CollUtil.getLast(nodes).getId();
				atomic.set(pid);
			}
		}
		List<SysDict> list = this.list();
		if (CollUtil.isEmpty(list)) {
			return CollUtil.newArrayList();
		}
		Tree<Long> top = new Tree<>();
		List<Tree<Long>> tree = TreeUtil.build(list, pid, (object, treeNode) -> {
			BeanUtil.copyProperties(object, treeNode);
			treeNode.setId(object.getId());
			treeNode.setName(object.getText());
			treeNode.setWeight(object.getSort());
			treeNode.setParentId(object.getPid());
			if (BooleanUtil.isFalse(root)) {
				return;
			}
			if (atomic.get() == 0) {
				return;
			}
			if (atomic.get() == object.getId()) {
				BeanUtil.copyProperties(treeNode, top);
			}
		});
		if (CollUtil.isEmpty(tree)) {
			return CollUtil.newArrayList();
		}
		if (ObjectUtil.isNotEmpty(top)) {
			top.setChildren(tree);
			return TreeExtraUtil.resetLevel(CollUtil.newArrayList(top), 1);
		} else {
			return TreeExtraUtil.resetLevel(tree, 1);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<SysDict> ajaxSave(SysDict sysDict) {
		if (StrUtil.isBlank(sysDict.getUuid())) {
			sysDict.setUuid(IdUtil.simpleUUID().toUpperCase());
			sysDict.setCreatedAt(new Date());
		} else {
			SysDict Dict = this.getOne(Wrappers.<SysDict>lambdaQuery().eq(SysDict::getUuid, sysDict.getUuid()));
			sysDict.setId(Dict.getId());
			sysDict.setCreatedAt(Dict.getCreatedAt());
			sysDict.setUpdatedAt(new Date());
		}
		// 校验字典编码（同级编码不能重复）
		long cnt = this.count(Wrappers.<SysDict>lambdaQuery().func(wrapper -> {
			wrapper.eq(SysDict::getCode, sysDict.getCode());
			wrapper.eq(SysDict::getLevel, sysDict.getLevel());
			if (sysDict.getId() != null) {
				wrapper.notIn(SysDict::getId, sysDict.getId());
			}
			if (sysDict.getPid() == null) {
				wrapper.isNull(SysDict::getPid);
			} else {
				wrapper.eq(SysDict::getPid, sysDict.getPid());
			}
		}));
		if (cnt > 0) {
			return Result.error("同级目录字典编码不能重复");
		}
		if (sysDict.getPid() == null) {
			sysDict.setLevel(1);
		} else {
			SysDict parent = this.getById(sysDict.getPid());
			parent.setState(TreeState.closed.name());
			sysDict.setLevel(parent.getLevel() + 1);
			List<SysDict> fathers = this.findFathers(parent.getId());
			List<Long> fatherIds = fathers.stream().map(SysDict::getId).collect(Collectors.toList());
			if (fatherIds.contains(sysDict.getId())) {
				return Result.error("不能选择自身或子节点作为父级字典");
			} else {
				parent.setUpdatedAt(new Date());
				this.updateById(parent);
			}
		}
		// 排序
		if (sysDict.getSort() == null) {
			List<SysDict> childs = this.findListByPid(sysDict.getPid());
			OptionalInt max = childs.stream().filter(Objects::nonNull).mapToInt(SysDict::getSort).max();
			if (max.isPresent()) {
				sysDict.setSort(max.getAsInt() + 1);
			}
		}
		sysDict.setSort(ObjectUtil.defaultIfNull(sysDict.getSort(), 1));
		this.saveOrUpdate(sysDict);
		// 子节点更新
		Map<String, Integer> map = new LinkedHashMap<>();
		List<Tree<Long>> roots = this.getTree(false);
		TreeExtraUtil.resetLevel(roots, 1);
		roots.forEach(root -> {
			Tree<Long> node = TreeUtil.getNode(root, sysDict.getId());
			if (ObjectUtil.isNotEmpty(node)) {
				List<Tree<Long>> childs = TreeExtraUtil.getAllNodes(CollUtil.newArrayList(node));
				childs.forEach(child -> {
					SysDict sd = BeanUtil.copyProperties(child, SysDict.class);
					map.put(sd.getUuid(), sd.getLevel());
				});
			}
		});
		map.forEach((uuid, level) -> {
			this.update(Wrappers.<SysDict>lambdaUpdate().func(wrapper -> {
				wrapper.set(SysDict::getLevel, level);
				wrapper.set(SysDict::getUpdatedAt, new Date());
				wrapper.eq(SysDict::getUuid, uuid);
			}));
		});
		return Result.success(sysDict);
	}

	@Override
	public void exportDataToFile(SysFile file) {
		List<Tree<Long>> trees = this.getTree(false);
		AtomicLong id = new AtomicLong(1);
		try (Workbook workbook = MyExcelUtil.createWorkbook("excel/sys/SysDict.excel.xml", false, sheet -> {
			for (Tree<Long> tree : trees) {
				SysDict sysDict = BeanUtil.copyProperties(tree, SysDict.class);
				sysDict.setId(id.getAndIncrement());
				sheet.write(sysDict);
				List<Tree<Long>> childs = tree.getChildren();
				if (CollUtil.isNotEmpty(childs)) {
					for (Tree<Long> child : childs) {
						this.writeExcelSheet(sheet, id, sysDict.getId(), child);
					}
				}
			}
		})) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			workbook.write(stream);
			file.setFileContent(stream.toByteArray());
			file.setSize((long) file.getFileContent().length);
			file.setStatus(5);
			file.setUpdatedAt(new Date());
		} catch (IOException e) {
			e.printStackTrace();
			file.setStatus(4);
			file.setUpdatedAt(new Date());
			file.setMessage(ExceptionUtil.stacktraceToString(e));
		} finally {
			iSysFileService.updateById(file);
		}
	}

	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public Result<List<String>> importDataFromFile(User user, MultipartFile file) {
		SysFile sysFile = iSysFileService.upload(file.getOriginalFilename(), file.getBytes(), entity -> {
			entity.setCreatorUserId(user.getUserId());
			entity.setCreatorNickname(user.getNickname());
		});
		List<String> errors = CollUtil.newArrayList();
		List<SysDict> list = CollUtil.newArrayList();
		String sheetName = "数据字典";
		AbstractReader reader = MyExcelUtil.readBySax("excel/sys/SysDict.excel.xml", IoUtil.toStream(sysFile.getFileContent()),
				sheetName, (data, checkResult) -> {
					// 出错了
					if (checkResult.isError()) {
						// 表头已处理完，进入行数据读取流程中
						if (checkResult.isProcess()) {
							CollUtil.addAll(errors, checkResult.getErrors());
						}
						// 模板表头校验失败
						else {
							CollUtil.addAll(errors, checkResult.getErrors());
						}
					}
					// 成功
					else {
						SysDict item = BeanUtil.toBean(data, SysDict.class);
						item.setUuid(IdUtil.simpleUUID().toUpperCase());
						item.setCreatedAt(new Date());
						CollUtil.addAll(list, item);
					}
				});
		if (reader.getRead(sheetName) == 0) {
			Result<List<String>> result = Result.error();
			return result.setData(CollUtil.newArrayList("未读到表头，模板不符合要求！"));
		}
		if (CollUtil.isNotEmpty(errors)) {
			Result<List<String>> result = Result.error();
			return result.setData(errors);
		}
		this.remove(Wrappers.<SysDict>lambdaQuery().isNotNull(SysDict::getId));
		this.saveBatch(list);
		return Result.success();
	}

	/**
	 * 向文件中写入子节点
	 * 
	 * @param sheet
	 * @param id
	 * @param pid
	 * @param node
	 */
	private void writeExcelSheet(DataWriterHandler sheet, AtomicLong id, Long pid, Tree<Long> node) {
		SysDict sysDict = BeanUtil.copyProperties(node, SysDict.class);
		sysDict.setId(id.getAndIncrement());
		sysDict.setPid(pid);
		sheet.write(sysDict);
		List<Tree<Long>> childs = node.getChildren();
		if (CollUtil.isNotEmpty(childs)) {
			for (Tree<Long> child : childs) {
				this.writeExcelSheet(sheet, id, sysDict.getId(), child);
			}
		}
	}
}
