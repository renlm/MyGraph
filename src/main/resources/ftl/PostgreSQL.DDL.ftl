<#list ers as er><#if er_index gt 0>

</#if><#if er.comment!?length gt 0>-- ${er.comment!}
</#if>CREATE TABLE "${er.tableName!}"(
    <#list er.fields as field>
    "${field.name!}" <#if field.jdbcType == "BIGINT" && field.isPk && er.pkList?size == 1>BIGSERIAL<#elseif field.jdbcType == "BIGINT">BIGINT<#elseif field.jdbcType == "INT">INT<#elseif field.jdbcType == "LONGTEXT">TEXT<#elseif field.jdbcType == "LONGBLOB">BYTEA<#elseif field.jdbcType == "BLOB">BYTEA<#elseif field.jdbcType == "DATE">DATE<#elseif field.jdbcType == "BIT" && field.size == 1>BOOLEAN<#else>${field.jdbcType!}<#if field.size gt 0 || field.digit gt 0><#if field.jdbcType != "TIMESTAMP" && field.jdbcType != "TEXT" && field.jdbcType != "LONGTEXT" && field.jdbcType != "SMALLINT">(${field.size!?c}<#if field.digit gt 0>, ${field.digit!?c}</#if>)</#if></#if></#if><#if field.isPk && er.pkList?size == 1> PRIMARY KEY</#if><#if field.columnDef!?length gt 0> DEFAULT <#if field.jdbcType == "BIT" && field.size == 1><#if field.columnDef == "1">TRUE<#else>FALSE</#if><#else>${field.columnDef!}</#if></#if><#if !field.isNullable && !field.isPk> NOT NULL</#if><#if field_index lt er.fields?size - 1 || er.pkList?size gt 1>,</#if>
    </#list>
    <#if er.pkList?size gt 1>
    PRIMARY KEY (<#list er.pkList as field>"${field.name!}"<#if field_index lt er.pkList?size - 1>,</#if></#list>)
    </#if>
)WITH(OIDS=FALSE);
<#if er.comment!?length gt 0>COMMENT ON TABLE "${er.tableName!}" IS '${er.comment!}';
</#if><#list er.fields as field><#if field.comment!?length gt 0>
COMMENT ON COLUMN "${er.tableName!}"."${field.name!}" IS '${field.comment!}';
</#if></#list>
</#list>