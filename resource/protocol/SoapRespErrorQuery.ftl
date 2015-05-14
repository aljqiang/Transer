<soapenv:Body>
	<gateway:NoAS400>
		<gateway:field name="ERROR_CODE"><![CDATA[${ERROR_CODE}]]></gateway:field>
		<gateway:field name="ERROR_DESC"><![CDATA[${ERROR_DESC!}]]></gateway:field>
		<gateway:field name="RecordCnt"><![CDATA[${RecordCnt!"0"}]]></gateway:field>
		<gateway:field name="PageSize"><![CDATA[${PageSize!"8"}]]></gateway:field>
		<gateway:field name="PageCnt"><![CDATA[${PageCnt!"0"}]]></gateway:field>
		<gateway:field name="CurruntPageNum"><![CDATA[${CurruntPageNum!"1"}]]></gateway:field>
	</gateway:NoAS400>
</soapenv:Body>
</soapenv:Envelope>