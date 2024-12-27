<#list ers as er><#if er_index gt 0>

</#if><#if er.comment!?length gt 0>-- ${er.comment!}
</#if>CREATE TABLE `${er.tableName!}`(
    <#list er.fields as field>
    `${field.name!}` <#if field.jdbcType == "BIT" && field.size == 1>TINYINT<#else>${field.jdbcType!}</#if><#if field.size gt 0 || field.digit gt 0><#if field.jdbcType != "TIMESTAMP" && field.jdbcType != "TEXT" && field.jdbcType != "LONGTEXT" && field.jdbcType != "BLOB" && field.jdbcType != "LONGBLOB">(${field.size!?c}<#if field.digit gt 0>, ${field.digit!?c}</#if>)</#if></#if><#if field.isPk && er.pkList?size eq 1> PRIMARY KEY<#if field.autoIncrement> AUTO_INCREMENT</#if></#if><#if field.columnDef!?length gt 0> DEFAULT <#if field.columnDef! == "now()">CURRENT_TIMESTAMP<#else>${field.columnDef!}</#if></#if><#if !field.isNullable && !field.isPk> NOT NULL</#if> COMMENT '${field.comment!}'<#if field_index lt er.fields?size - 1>,</#if>
    </#list>
    <#if er.pkList?size gt 1>
    PRIMARY KEY (<#list er.pkList as field>${field.name!}<#if field_index lt er.pkList?size - 1>,</#if></#list>)
    </#if>
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '${er.comment!}';
</#list>