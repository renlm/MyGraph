package cn.renlm.graph.modular.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文件
 * </p>
 *
 * @author Renlm
 * @since 2022-07-21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("sys_file")
public class SysFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件ID
     */
    @TableField("file_id")
    private String fileId;

    /**
     * 文件名
     */
    @TableField("original_filename")
    private String originalFilename;

    /**
     * 是否公开（默认否）
     */
    @TableField("is_public")
    private Boolean isPublic;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件内容
     */
    @TableField(value = "file_content", typeHandler = org.apache.ibatis.type.BlobTypeHandler.class)
    private byte[] fileContent;

    /**
     * 文件大小
     */
    @TableField("size")
    private Long size;

    /**
     * 执行器
     */
    @TableField("actuator")
    private String actuator;

    /**
     * 执行参数（Json格式）
     */
    @TableField("param_json")
    private String paramJson;

    /**
     * 状态，1：正常，2：任务初始化，3：任务执行中，4：任务异常，5：任务已完成
     */
    @TableField("status")
    private Integer status;

    /**
     * 消息内容（异常信息）
     */
    @TableField("message")
    private String message;

    /**
     * 创建人（用户ID）
     */
    @TableField("creator_user_id")
    private String creatorUserId;

    /**
     * 创建人（昵称）
     */
    @TableField("creator_nickname")
    private String creatorNickname;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private Date updatedAt;

    /**
     * 是否删除（默认否）
     */
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
