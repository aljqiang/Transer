package com.sunrise.cgb.gateway;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.cc.CCQueryFactory;
import com.sunrise.cgb.cc.ICCQueryHandler;
import com.sunrise.cgb.core.TransHelper;
import com.sunrise.cgb.exception.QueryTaskException;
import com.sunrise.cgb.queue.QueryTaskEntity;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class QueryTaskWorker extends Thread{

    private static Log log = LogFactory.getLog(QueryTaskWorker.class);

    private QueryTaskEntity entity;

    /**
     * 创建查询导标任务
     * @param entity
     * @param ip
     * @param port
     */
    public QueryTaskWorker(QueryTaskEntity entity) {
        this.entity = entity;
        this.setName("QueryTaskTranser(type-" + entity.getType() +")");
    }

    /**
     * 查询导标任务逻辑
     */
    @Override
    public void run(){
        try{
            //输出SOAP报文头
            try {
                CGBConnectorUtil.writeSOAPHeader(entity.getQueryParams(), entity.getOutput());
            } catch (Exception e) {
                throw new QueryTaskException(entity,e.getMessage(),ICCQueryHandler.RESP_CODE_INNER,"writeSOAPHeader error.");
            }
            //获取查询类型
            ICCQueryHandler handler = CCQueryFactory.buildQueryHandler(entity.getType());
            if(handler == null)
                throw new QueryTaskException(entity,"无法找到该类型的查询处理器.",ICCQueryHandler.RESP_CODE_INNER,"inner error.no handler");
            //2013-7-25 李嘉伟 添加判断分行号和客户号的查询数据校验
            handler.isVali(entity);
            //设置分页信息
            try{
                String nextPage = (String) entity.getQueryParams().get(ICCQueryHandler.CONSTANT_KEY_NEXTPAGE);
                int nextPageIndex = Integer.parseInt(nextPage);//获取当前分页
                entity.getQueryParams().put(ICCQueryHandler.QUERY_KEY_PAGE_START, (nextPageIndex-1)*handler.getPageSize());
                entity.getQueryParams().put(ICCQueryHandler.QUERY_KEY_PAGE_END,nextPageIndex*handler.getPageSize());
                int totalRec = handler.getRecCnt(entity);//获取记录总数
                entity.getQueryParams().put(ICCQueryHandler.CONSTANT_KEY_CURRPAGE, nextPage);
                entity.getQueryParams().put(ICCQueryHandler.CONSTANT_KEY_ERRCODE,ICCQueryHandler.RESP_CODE_SUCC);
                entity.getQueryParams().put(ICCQueryHandler.CONSTANT_KEY_ERRDESC, "");
                entity.getQueryParams().put(ICCQueryHandler.CONSTANT_KEY_PAGECNT,
                        (totalRec<=handler.getPageSize())?1:(
                                (totalRec%handler.getPageSize() == 0)?totalRec/handler.getPageSize():totalRec/handler.getPageSize()+1));
                entity.getQueryParams().put(ICCQueryHandler.CONSTANT_KEY_PAGESIZE, handler.getPageSize());
                //2013-07-25 李嘉伟 修改当前总记录数为当前分页记录数 CONSTANT_KEY_RECCNT在处理器内维护
                //entity.getQueryParams().put(ICCQueryHandler.CONSTANT_KEY_RECCNT, totalRec);
            }catch(Exception e){
                throw new QueryTaskException(entity, "设置分业信息时出错:"+e.toString(),ICCQueryHandler.RESP_CODE_PAGEQ,"paging data error");
            }
            //执行查询
            handler.run(entity);
            entity.getOutput().flush();
        }catch(QueryTaskException e){
            log.error(e.toString());
            entity.getQueryParams().put(ICCQueryHandler.CONSTANT_KEY_ERRCODE,e.getErrCode());
            entity.getQueryParams().put(ICCQueryHandler.CONSTANT_KEY_ERRDESC, e.getErrMsg());
            try {
                CGBConnectorUtil.writeSOAPQueryErr(entity.getQueryParams(), entity.getOutput());
            } catch (Exception ex) {
                log.error("输出查询失败报文体时出错.",ex);
            }
        } catch (IOException e) {
            log.error("清空查询返回结果IO缓冲区时出现异常.",e);
        }finally{
            TransHelper.transer().showCcWorkerCnt();
            TransHelper.transer().decreaseCcLoad();
            synchronized(this.entity){
                this.entity.notifyAll();
            }
        }
    }
}
