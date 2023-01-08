<#list ers as er><#if er_index gt 0>

</#if>-- ${er.comment!}
CREATE TABLE "${er.tableName!}"(
    <#list er.fields as field>
    "${field.name!}" <#if field.jdbcType == "BIGINT" && field.isPk>BIGSERIAL<#elseif field.jdbcType == "BIGINT">BIGINT<#elseif field.jdbcType == "INT">INT<#elseif field.jdbcType == "LONGTEXT">TEXT<#elseif field.jdbcType == "LONGBLOB">BYTEA<#elseif field.jdbcType == "BLOB">BYTEA<#elseif field.jdbcType == "DATE">DATE<#elseif field.jdbcType == "BIT" && field.size == 1>BOOLEAN<#else>${field.jdbcType!}<#if field.size gt 0 || field.digit gt 0><#if field.jdbcType != "TIMESTAMP" && field.jdbcType != "TEXT" && field.jdbcType != "LONGTEXT">(${field.size!}<#if field.digit gt 0>, ${field.digit!}</#if>)</#if></#if></#if><#if field.isPk> PRIMARY KEY</#if><#if field.columnDef!?length gt 0> DEFAULT <#if field.jdbcType == "BIT" && field.size == 1><#if field.columnDef == "1">TRUE<#else>FALSE</#if><#else>${field.columnDef!}</#if></#if><#if !field.isNullable && !field.isPk> NOT NULL</#if><#if field_index lt er.fields?size - 1>,</#if>
    </#list>
)WITH(OIDS=FALSE);
COMMENT ON TABLE "${er.tableName!}" IS '${er.comment!}';
<#list er.fields as field>
COMMENT ON COLUMN "${er.tableName!}"."${field.name!}" IS '${field.comment!}';
</#list>
</#list>