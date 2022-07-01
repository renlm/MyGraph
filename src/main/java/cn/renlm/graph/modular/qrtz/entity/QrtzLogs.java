package cn.renlm.graph.modular.qrtz.entity;

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
 * 定时任务日志
 * </p>
 *
 * @author Renlm
 * @since 2022-07-01
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("QRTZ_LOGS")
public class QrtzLogs implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 机器ip
     */
    @TableField("machine")
    private String machine;

    /**
     * 触发器
     */
    @TableField("trigger_name")
    private String triggerName;

    /**
     * 任务描述
     */
    @TableField("job_name")
    private String jobName;

    /**
     * 执行类
     */
    @TableField("job_class_name")
    private String jobClassName;

    /**
     * 任务参数
     */
    @TableField("job_data_map_json")
    private String jobDataMapJson;

    /**
     * 批次
     */
    @TableField("batch")
    private String batch;

    /**
     * 序列
     */
    @TableField("seq")
    private Integer seq;

    /**
     * 日志级别
     */
    @TableField("level")
    private String level;

    /**
     * 日志内容
     */
    @TableField("text")
    private String text;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;


}
