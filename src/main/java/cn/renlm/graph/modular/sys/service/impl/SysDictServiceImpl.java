package cn.renlm.graph.modular.sys.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
import cn.hutool.core.util.IdUtil;
import cn.renlm.graph.dto.User;
import cn.renlm.graph.modular.sys.entity.SysDict;
import cn.renlm.graph.modular.sys.entity.SysFile;
import cn.renlm.graph.modular.sys.mapper.SysDictMapper;
import cn.renlm.graph.modular.sys.service.ISysDictService;
import cn.renlm.graph.modular.sys.service.ISysFileService;
import cn.renlm.graph.response.Result;
import cn.renlm.plugins.MyExcelUtil;
import cn.renlm.plugins.MyExcel.handler.DataWriterHandler;
import lombok.SneakyThrows;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Renlm
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
	public List<Tree<Long>> getTree(String... codePaths) {
		Long pid = null;
		if (codePaths.length > 0) {
			List<SysDict> nodes = this.findListByPath(codePaths);
			if (CollUtil.isEmpty(nodes)) {
				return CollUtil.newArrayList();
			} else {
				pid = CollUtil.getLast(nodes).getId();
			}
		}
		List<SysDict> list = this.list();
		if (CollUtil.isEmpty(list)) {
			return CollUtil.newArrayList();
		}
		List<Tree<Long>> tree = TreeUtil.build(list, pid, (object, treeNode) -> {
			BeanUtil.copyProperties(object, treeNode);
			treeNode.setId(object.getId());
			treeNode.setName(object.getText());
			treeNode.setWeight(object.getSort());
			treeNode.setParentId(object.getPid());
		});
		if (CollUtil.isEmpty(tree)) {
			return CollUtil.newArrayList();
		}
		return tree;
	}

	@Override
	public void exportDataToFile(SysFile file) {
		List<Tree<Long>> trees = this.getTree();
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
		}
		iSysFileService.updateById(file);
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
		int rows = MyExcelUtil.readBySax("excel/sys/SysDict.excel.xml", IoUtil.toStream(sysFile.getFileContent()),
				"数据字典", (data, checkResult) -> {
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
		if (rows == 0) {
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
	 * 获取由上而下的父子集
	 * 
	 * @param codePaths
	 * @return
	 */
	private List<SysDict> findListByPath(String... codePaths) {
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
