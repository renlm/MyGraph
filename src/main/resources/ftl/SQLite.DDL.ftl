<#list ers as er><#if er_index gt 0>

</#if>-- ${er.comment!}
CREATE TABLE `${er.tableName!}`(
	-- ${er.comment!}
    <#list er.fields as field>
    [${field.name!}] <#if field.jdbcType == "BIT" && field.size == 1>TINYINT<#else>${field.jdbcType!}</#if><#if field.size gt 0 || field.digit gt 0><#if field.jdbcType != "TIMESTAMP" && field.jdbcType != "TEXT" && field.jdbcType != "LONGTEXT" && field.jdbcType != "BLOB" && field.jdbcType != "LONGBLOB">(${field.size!?c}<#if field.digit gt 0>, ${field.digit!?c}</#if>)</#if></#if><#if field.isPk> PRIMARY KEY</#if><#if !field.isNullable && !field.isPk> NOT NULL</#if><#if field.columnDef!?length gt 0> DEFAULT <#if field.columnDef! == "now()">CURRENT_TIMESTAMP<#else>${field.columnDef!}</#if></#if><#if field_index lt er.fields?size - 1>,</#if><#if field.comment!?length gt 0> -- '${field.comment!}'</#if>
    </#list>
);
</#list>