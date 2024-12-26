<?xml version="1.0" encoding="UTF-8"?>
<generator dsName="pg"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:noNamespaceSchemaLocation="https://renlm.github.io/schemas/MyGenerator.xsd"><#list erGroup?keys as key>

	<module name="${key!}" package="com.example.demo">
		<#list erGroup[key] as er>
		<table author="${author!}" name="${er.tableName!}" entity="true" excel="true" />
		</#list>
	</module></#list>

	<url>${url!}</url>
	<username>username</username>
	<password>password</password>

	<config springdoc="false" swagger="false">
		<typeConvert>
			<javaSqlType name="BIGINT" type="BigInteger" pkg="java.math.BigInteger" />
			<javaSqlType name="TINYINT" type="Boolean" />
			<javaSqlType name="DOUBLE" type="Double" />
		</typeConvert>
		<package>
			<controller>controller</controller>
			<serviceImpl>service.impl</serviceImpl>
			<service>service</service>
			<entity>entity</entity>
			<mapper>mapper</mapper>
			<xml>mapper</xml>
		</package>
		<strategy>
			<entity>
				<formatFileName>{entityName}</formatFileName>
				<disableSerialVersionUID>false</disableSerialVersionUID>
				<tableFieldAnnotationEnable>false</tableFieldAnnotationEnable>
			</entity>
			<controller>
				<controller>true</controller>
				<enableRestStyle>true</enableRestStyle>
				<formatFileName>{entityName}Controller</formatFileName>
			</controller>
			<service>
				<formatServiceFileName>I{entityName}Service</formatServiceFileName>
				<formatServiceImplFileName>{entityName}ServiceImpl</formatServiceImplFileName>
			</service>
			<mapper>
				<formatMapperFileName>{entityName}Mapper</formatMapperFileName>
				<formatXmlFileName>{entityName}Mapper</formatXmlFileName>
			</mapper>
		</strategy>
	</config>

</generator>