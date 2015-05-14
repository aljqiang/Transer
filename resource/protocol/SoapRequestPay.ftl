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
		<gateway:field name="templateCodeName">${templateCodeName}</gateway:field>
		<gateway:field name="G_TRANSCODE">${G_TRANSCODE}</gateway:field>
		<gateway:field name="hostId">${hostId}</gateway:field>
		<gateway:field name="libOrPathName">${libOrPathName}</gateway:field>
		<gateway:field name="docName">${docName}</gateway:field>
		<gateway:field name="memberName">${memberName}</gateway:field>
		<gateway:field name="orSenderId">${orSenderId}</gateway:field>
		<gateway:field name="orSenderSN">${orSenderSN}</gateway:field>
		<gateway:field name="orSenderDate">${orSenderDate}</gateway:field>
		<gateway:field name="orSenderTime">${orSenderTime}</gateway:field>
		<gateway:field name="ooSenderId">${ooSenderId}</gateway:field>
		<gateway:field name="ooSenderSN">${ooSenderSN}</gateway:field>
		<gateway:field name="ooSenderDate">${ooSenderDate}</gateway:field>
		<gateway:field name="ooSenderTime">${ooSenderTime}</gateway:field>
		<gateway:field name="fileCategory">${fileCategory}</gateway:field>
		<gateway:field name="terminalNO">${terminalNO}</gateway:field>
		<gateway:field name="utId">${utID}</gateway:field>
		<gateway:field name="srcFileName">${srcFileName}</gateway:field>
		<gateway:field name="srcFileDes">${srcFileDes}</gateway:field>
		<gateway:field name="synchStatus">${synchStatus}</gateway:field>
		<gateway:field name="reserved1">${reserved1}</gateway:field>
		<gateway:field name="reserved2">${reserved2}</gateway:field>
	</gateway:NoAS400>
</soapenv:Body>
</soapenv:Envelope>