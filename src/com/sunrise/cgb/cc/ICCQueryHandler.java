package com.sunrise.cgb.cc;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.sunrise.cgb.exception.QueryTaskException;
import com.sunrise.cgb.queue.QueryTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public interface ICCQueryHandler {
	/**
	 * 查询参数
	 */
	public static final String QUERY_KEY_PAGE_START = "startPage";
	public static final String QUERY_KEY_PAGE_END = "endPage";
	/**
	 * 报文体参数
	 */
	public static final String CONSTANT_KEY_NEXTPAGE = "NextPageId";
	
	public static final String CONSTANT_KEY_ERRCODE = "ERROR_CODE";
	
	public static final String CONSTANT_KEY_ERRDESC = "ERROR_DESC";
	
	public static final String CONSTANT_KEY_RECCNT = "RecordCnt";
	
	public static final String CONSTANT_KEY_PAGESIZE = "PageSize";
	
	public static final String CONSTANT_KEY_PAGECNT = "PageCnt";
	
	public static final String CONSTANT_KEY_CURRPAGE = "CurruntPageNum";
	/**
	 * 错误码
	 */
	public static final String RESP_CODE_SUCC = "0000";
	
	public static final String RESP_CODE_INNER = "1010";
	
	public static final String RESP_CODE_PAGEQ = "1020";
	
	public static final String RESP_CODE_QUERY = "1021";
	
	public static final String RESP_CODE_NOT_VALI = "1022";
	/**
	 * 导出查询结果
	 * @param params
	 * @param out
	 */
	public void run(QueryTaskEntity entity)  throws QueryTaskException;
	/**
	 * 查询结果报文体路径
	 * @return
	 */
	public String responseBody();
	/**
	 * 获取分页大小
	 * @return
	 */
	public int getPageSize();
	/**
	 * 获取总记录数
	 * @return
	 */
	public int getRecCnt(QueryTaskEntity entity) throws QueryTaskException;
	/**
	 * 查询数据校验
	 * @param entity
	 * @return
	 * @throws QueryTaskException
	 */
	public boolean isVali(QueryTaskEntity entity) throws QueryTaskException;
}
