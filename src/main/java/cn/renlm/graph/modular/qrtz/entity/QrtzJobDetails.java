package cn.renlm.graph.modular.qrtz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Renlm
 * @since 2022-05-10
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("QRTZ_JOB_DETAILS")
public class QrtzJobDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @TableId(value = "JOB_NAME", type = IdType.AUTO)
    private String jobName;

    @TableId(value = "JOB_GROUP", type = IdType.AUTO)
    private String jobGroup;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("JOB_CLASS_NAME")
    private String jobClassName;

    @TableField("IS_DURABLE")
    private String isDurable;

    @TableField("IS_NONCONCURRENT")
    private String isNonconcurrent;

    @TableField("IS_UPDATE_DATA")
    private String isUpdateData;

    @TableField("REQUESTS_RECOVERY")
    private String requestsRecovery;

    @TableField(value = "JOB_DATA", typeHandler = org.apache.ibatis.type.BlobTypeHandler.class)
    private byte[] jobData;


}
