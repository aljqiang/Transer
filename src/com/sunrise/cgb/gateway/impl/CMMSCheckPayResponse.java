package com.sunrise.cgb.gateway.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.common.ProgramConfig;
import com.sunrise.cgb.gateway.IConnectorResponse;
import com.sunrise.cgb.gateway.ISoapConnectorResponse;
import com.sunrise.cgb.gateway.xmlparser.ProtoParser;
import com.sunrise.cgb.gateway.xmlparser.ProtoParser.ProtoContentParseResult;
import com.sunrise.cgb.gateway.xmlparser.impl.CheckPayResponseParser;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CMMSCheckPayResponse extends ConnectorResponseImpl implements ISoapConnectorResponse{

	private static Log log = LogFactory.getLog(CMMSCheckPayResponse.class);
	
	@Override
	public int getType() {
		return ProgramConfig.IO_TYPE_CHECK_UPLOAD_PAY;
	}

	@Override
	public void read(String xml) throws Exception {
		//解析报文体
		ProtoContentParseResult parseResult = null;
		try {
			parseResult = ProtoParser.parse(xml);
		} catch (Exception e) {
			log.error("解析柜面网关转发检查调帐文服的反馈XML出错.",e);
			setResult(IConnectorResponse.RESULT_FAIL);
			setCode("response xml:"+xml+" stack:"+e.getMessage());
			return;
		}
		//抽取有用信息
		if(parseResult.isSuccess()){
			CheckPayResponseParser content = (CheckPayResponseParser)parseResult.protoContent().getBody();
			log.info("文服回复主机通讯状态:"+content.getErrorCode()+",status:"+content.getFileTransStatus());
			if("0004".equals(content.getErrorCode()) || 
			   "0003".equals(content.getErrorCode()) ||
			   "0002".equals(content.getErrorCode())){
				setResult(IConnectorResponse.RESULT_FAIL);
				setCode(content.getErrorCode()+":"+content.getErrorMsg());
			}else if("0000".equals(content.getErrorCode())){
//				 fileTransStatus	文件传输状态	char	2	‘‘01’表示上传主机成功，但移档结果尚未明确
//				 ‘02’.表示主机接收文件后，移档成功
//				 ‘03’,表示主机后成功接收文件，移档失败。
//				  ‘04‘，表示文件服务器解析上传文件失败。
//				 ‘05‘表示文件服务器上传处理内部错误
//				 ‘06‘表示上传文件需要授权但没有得到授权信息
//				 ‘07‘表示上传 文件使用fileCategory不在文件服务器支持之列
//				 ‘08‘表示此类文件需要按域转换但是没有找到格式描述文件。
//				 ‘09’表示文件上传重试失败
//				 ‘0A’表示上传主机通讯报文错误
//				 ‘0C‘表示上传主机排队失败
//				 ‘0D’表示上传与主机通讯出错
//
//				 ‘0E‘表示等待放入队列’
//				 ‘0F’表示文件服务正在预处理文件
//				 ‘10’表示文件记录长度没有维护
//				 ‘11’表示上传的文件为空
//				 '12'表示准备上传到主机
//				 ‘20‘表示接收主机文件成功
//				 ‘21‘表示接收主机文件通讯出错
//				 ‘22‘表示接收主报文有错误。
//				 ‘23’表示接收主机的文件数据有错误。
//				 ‘24‘表示接收主机文件时找不到文件对应的基本信息(例如是否需要按域码等
//				 ‘25’表示接收文件时重试失败
//				 ‘26‘表示接收内部处理出错
//				 ‘27’表示正在接收主机文件
//				 ‘28’表示下载的文件为空
//				 ‘29’表示下载请求已经发送　
//				 '30'表示主机已经收 到下载请求，但是主机内部处理出错
//				 ‘。注意上传可能有两次通知。一次通知上传主机状态。如果主机通知了文件服务器移档状态，文件服务器也会通知到发起方的。
				if("03".equals(content.getFileTransStatus()) ||
				   "04".equals(content.getFileTransStatus()) ||
				   "05".equals(content.getFileTransStatus()) ||
				   "06".equals(content.getFileTransStatus()) ||
				   "07".equals(content.getFileTransStatus()) ||
				   "09".equals(content.getFileTransStatus()) ||
				   "0A".equals(content.getFileTransStatus()) ||
				   "0C".equals(content.getFileTransStatus()) ||
				   "0D".equals(content.getFileTransStatus()) ||
				   "11".equals(content.getFileTransStatus()) ||
				   "21".equals(content.getFileTransStatus()) ||
				   "22".equals(content.getFileTransStatus()) ||
				   "23".equals(content.getFileTransStatus()) ||
				   "24".equals(content.getFileTransStatus()) ||
				   "25".equals(content.getFileTransStatus()) ||
				   "26".equals(content.getFileTransStatus()) ||
				   "30".equals(content.getFileTransStatus()) ||
				   "28".equals(content.getFileTransStatus()) ||
				   "08".equals(content.getFileTransStatus())){
					setResult(IConnectorResponse.RESULT_FAIL);
					setCode("statusDesc:"+content.getFileTransStatus()+
							     ",errorCode:"+content.getErrorCode()+
							     ",errMsg:"+content.getErrorMsg());
				}else if("02".equals(content.getFileTransStatus())){
					setResult(IConnectorResponse.RESULT_SUCCESS);
				}else
					setResult(IConnectorResponse.RESULT_NONE);
			}
		}else{
			setResult(IConnectorResponse.RESULT_FAIL);
			setCode(parseResult.getFault()+",response xml:"+xml);
		}
	}

}
