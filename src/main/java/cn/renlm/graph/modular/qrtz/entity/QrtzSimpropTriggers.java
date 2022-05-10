package cn.renlm.graph.modular.qrtz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
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
@TableName("QRTZ_SIMPROP_TRIGGERS")
public class QrtzSimpropTriggers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    @TableId(value = "TRIGGER_NAME", type = IdType.AUTO)
    private String triggerName;

    @TableId(value = "TRIGGER_GROUP", type = IdType.AUTO)
    private String triggerGroup;

    @TableField("STR_PROP_1")
    private String strProp1;

    @TableField("STR_PROP_2")
    private String strProp2;

    @TableField("STR_PROP_3")
    private String strProp3;

    @TableField("INT_PROP_1")
    private Integer intProp1;

    @TableField("INT_PROP_2")
    private Integer intProp2;

    @TableField("LONG_PROP_1")
    private Long longProp1;

    @TableField("LONG_PROP_2")
    private Long longProp2;

    @TableField("DEC_PROP_1")
    private BigDecimal decProp1;

    @TableField("DEC_PROP_2")
    private BigDecimal decProp2;

    @TableField("BOOL_PROP_1")
    private String boolProp1;

    @TableField("BOOL_PROP_2")
    private String boolProp2;


}
