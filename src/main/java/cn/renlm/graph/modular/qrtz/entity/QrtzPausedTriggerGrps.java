package cn.renlm.graph.modular.qrtz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("QRTZ_PAUSED_TRIGGER_GRPS")
public class QrtzPausedTriggerGrps implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @TableId(value = "TRIGGER_GROUP", type = IdType.AUTO)
    private String triggerGroup;


}
