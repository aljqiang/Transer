<?xml version="1.0" encoding="GBK"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
xmlns:gateway="http://www.agree.com.cn/GDBGateway" 
xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<soapenv:Header>
	<gateway:HeadType soapenv:actor="http://schemas.xmlsoap.org/soap/actor/next" soapenv:mustUnderstand="0">
		<gateway:versionNo>${header_version_code}</gateway:versionNo>
		<gateway:toEncrypt>${header_encrypt}</gateway:toEncrypt>
		<gateway:commCode>${header_trans}</gateway:commCode>
		<gateway:commType>${header_ct}</gateway:commType>
		<gateway:receiverId>${server_code}</gateway:receiverId>
		<gateway:senderId>${header_send_sid}</gateway:senderId>
		<gateway:senderSN>${seqNum}</gateway:senderSN>
		<gateway:senderDate>${seqDate}</gateway:senderDate>
		<gateway:senderTime>${seqTime}</gateway:senderTime>
		<gateway:tradeCode>${header_trade_code}</gateway:tradeCode>
		<gateway:gwErrorCode/>
		<gateway:gwErrorMessage/>
	</gateway:HeadType>
</soapenv:Header>
<soapenv:Body>
	<gateway:NoAS400>
		<gateway:field name="TransCode">${TransCode}</gateway:field>
		<gateway:field name="SENDID">${SENDID}</gateway:field>
		<gateway:field name="SENDDATE">${SENDDATE}</gateway:field>
		<gateway:field name="SENDSN">${SENDSN}</gateway:field>
		<gateway:field name="CHANNELCODE">${CHANNELCODE}</gateway:field>
		<gateway:field name="FILENAME">${FILENAME}</gateway:field>
		<gateway:field name="Branch">${Branch}</gateway:field>
		<gateway:field name="SubBranch">${SubBranch}</gateway:field>
		<gateway:field name="OperatorId">${OperatorId}</gateway:field>
	</gateway:NoAS400>
</soapenv:Body>
</soapenv:Envelope>