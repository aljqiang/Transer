<soapenv:Body>
	<gateway:NoAS400>
		<gateway:field name="ERROR_CODE"><![CDATA[${ERROR_CODE!}]]></gateway:field>
		<gateway:field name="ERROR_DESC"><![CDATA[${ERROR_DESC!}]]></gateway:field>
		<gateway:field name="RecordCnt"><![CDATA[${RecordCnt!}]]></gateway:field>
		<gateway:field name="PageSize"><![CDATA[${PageSize!}]]></gateway:field>
		<gateway:field name="PageCnt"><![CDATA[${PageCnt!}]]></gateway:field>
		<gateway:field name="CurruntPageNum"><![CDATA[${CurruntPageNum!}]]></gateway:field>
		<#list qResult as l>
		<gateway:field name="BANK_ID"><![CDATA[${l.BANK_ID!}]]></gateway:field>
		<gateway:field name="CUST_ID"><![CDATA[${l.CUST_ID!}]]></gateway:field>
		<gateway:field name="CARD_NUM"><![CDATA[${l.CARD_NUM!}]]></gateway:field>
		<gateway:field name="CAMPAIGN_CD"><![CDATA[${l.CAMPAIGN_CD!}]]></gateway:field>
		<gateway:field name="CAMPAIGN_NM"><![CDATA[${l.CAMPAIGN_NM!}]]></gateway:field>
		<gateway:field name="COMMUNICATION_CD"><![CDATA[${l.COMMUNICATION_CD!}]]></gateway:field>
		<gateway:field name="COMMUNICATION_NM"><![CDATA[${l.COMMUNICATION_NM!}]]></gateway:field>
		<gateway:field name="TREAT_GIFT_CODE"><![CDATA[${l.TREAT_GIFT_CODE!}]]></gateway:field>
		<gateway:field name="OFFER_DESC"><![CDATA[${l.OFFER_DESC!}]]></gateway:field>
		<gateway:field name="OFFER_END_DATE"><![CDATA[${l.OFFER_END_DATE!}]]></gateway:field>
		<gateway:field name="CONTACT_MONTH"><![CDATA[${l.CONTACT_MONTH!}]]></gateway:field>
		<gateway:field name="CELL_NAME"><![CDATA[${l.CELL_NAME!}]]></gateway:field>
		<gateway:field name="RESPTRACKING_CD"><![CDATA[${l.RESPTRACKING_CD!}]]></gateway:field>
		</#list>
	</gateway:NoAS400>
</soapenv:Body>
</soapenv:Envelope>