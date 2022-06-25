<#list ers as er>
-- ${er.comment!}
CREATE TABLE ${er.tableName!}(
    <#list er.fields as field>
	${field.name!}                  	${field.jdbcType!}<#if field.size gt 0 || field.digit gt 0>(${field.size!}<#if field.digit gt 0>, ${field.digit!}</#if>)</#if>      <#if field.isPk>PRIMARY KEY 	<#if field.autoIncrement>AUTO_INCREMENT</#if>	</#if><#if field.columnDef!?length gt 0>DEFAULT ${field.columnDef!}</#if>  		<#if !field.isNullable>NOT NULL</#if>  		COMMENT '${field.comment!}',
    </#list>
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT = '${er.comment!}';
</#list>