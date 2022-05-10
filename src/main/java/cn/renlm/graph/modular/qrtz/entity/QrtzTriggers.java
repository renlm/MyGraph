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
@TableName("QRTZ_TRIGGERS")
public class QrtzTriggers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @TableId(value = "TRIGGER_NAME", type = IdType.AUTO)
    private String triggerName;

    @TableId(value = "TRIGGER_GROUP", type = IdType.AUTO)
    private String triggerGroup;

    @TableField("JOB_NAME")
    private String jobName;

    @TableField("JOB_GROUP")
    private String jobGroup;

    @TableField("DESCRIPTION")
    private String description;

    @TableField("NEXT_FIRE_TIME")
    private Long nextFireTime;

    @TableField("PREV_FIRE_TIME")
    private Long prevFireTime;

    @TableField("PRIORITY")
    private Integer priority;

    @TableField("TRIGGER_STATE")
    private String triggerState;

    @TableField("TRIGGER_TYPE")
    private String triggerType;

    @TableField("START_TIME")
    private Long startTime;

    @TableField("END_TIME")
    private Long endTime;

    @TableField("CALENDAR_NAME")
    private String calendarName;

    @TableField("MISFIRE_INSTR")
    private Integer misfireInstr;

    @TableField(value = "JOB_DATA", typeHandler = org.apache.ibatis.type.BlobTypeHandler.class)
    private byte[] jobData;


}
