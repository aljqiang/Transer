package com.sunrise.cgb.cc.impl;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sunrise.cgb.cc.ICCQueryHandler;
import com.sunrise.cgb.exception.QueryTaskException;
import com.sunrise.cgb.gateway.CGBConnectorUtil;
import com.sunrise.cgb.queue.QueryTaskEntity;
import com.sunrise.foundation.dbutil.QueryRunner;
import com.sunrise.foundation.dbutil.ResultSetHandler;
import com.sunrise.foundation.utils.StringUtil;
import com.sunrise.framework.commonquery.CommonQueryHelper;
import com.sunrise.framework.commonquery.QueryContext;
import com.sunrise.framework.commonquery.config.QueryConfig;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class ActivityQueryHandler implements ICCQueryHandler {

    private static Log log = LogFactory.getLog(ActivityQueryHandler.class);

    @Override
    public void run(QueryTaskEntity entity)  throws QueryTaskException{
        try {
            QueryContext cxt = QueryContext.create();
//			QueryConfig.initDefault("WebContent/WEB-INF/querys");
//			QueryContext cxt = QueryContext.create(QueryConfig.defaultConfig());
            CommonQueryHelper.executeQuery("Interface/Q_INTERFACE.xml",
                    "Q_ACT_INFO_QUERY", entity.getQueryParams(), cxt,
                    new ActResultSetHandler(this.responseBody(), entity.getQueryParams(),entity.getOutput()));
        } catch (Exception e) {
            throw new QueryTaskException(entity,"查询宣传活动查询结果失败:"+e.getMessage(),ICCQueryHandler.RESP_CODE_QUERY,"query data err");
        }
    }

    private static class ActResultSetHandler implements ResultSetHandler {

        private String ftl;
        private OutputStream out;
        private Map<String,Object> params;

        public ActResultSetHandler(String ftl, Map<String,Object> params,OutputStream out) {
            this.ftl = ftl;
            this.params = params;
            this.out = out;
        }

        @Override
        public Object handle(ResultSet rs) throws Exception {
            List<Map<String,Object>> rowDataList = new ArrayList<Map<String,Object>>();
            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<String, Object>();
                rowData.put("BANK_ID", rs.getString("BANK_ID"));
                rowData.put("CUST_ID", rs.getString("CUST_ID"));
                rowData.put("CARD_NUM", rs.getString("CARD_ID"));
                rowData.put("CAMPAIGN_CD", rs.getString("CAMPAIGN_CD"));
                rowData.put("CAMPAIGN_NM", rs.getString("CAMPAIGN_NM"));
                rowData.put("COMMUNICATION_CD", rs.getString("COMMUNICATION_CD"));
                rowData.put("COMMUNICATION_NM", rs.getString("COMMUNICATION_NM"));
                rowData.put("TREAT_GIFT_CODE", rs.getString("TREAT_GIFT_CODE"));
                rowData.put("OFFER_DESC", rs.getString("OFFER_DESC"));
                rowData.put("OFFER_END_DATE", rs.getString("OFFER_END_DATE"));
                rowData.put("CONTACT_MONTH", rs.getString("CONTACT_MONTH"));
                rowData.put("CELL_NAME", rs.getString("CELL_NAME"));
                rowData.put("RESPTRACKING_CD", rs.getString("RESPTRACKING_CD"));
                rowDataList.add(rowData);
            }
            params.put("qResult", rowDataList);
            params.put(CONSTANT_KEY_RECCNT, rowDataList.size());
            CGBConnectorUtil.writeSoapProto(params, ftl, out);
            return null;
        }

    }

    @Override
    public String responseBody() {
        return "protocol/SoapRespActivityQuery.ftl";
    }

    /**
     * 测试入口
     * @param args
     * @throws QueryTaskException
     */
    public static void main(String[] args) throws QueryTaskException {
        ActivityQueryHandler handler = new ActivityQueryHandler();
        QueryTaskEntity entity = new QueryTaskEntity();
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("", "");
        params.put("", "");
        params.put("", "");
        params.put("", "");
        params.put("", "");
        params.put("", "");
        params.put("", "");
        params.put("", "");
        entity.addQueryParams(params);
        handler.run(entity);
    }

    @Override
    public int getPageSize() {
        return 8;
    }

    @Override
    public int getRecCnt(QueryTaskEntity entity) throws QueryTaskException {
        int cnt = 0;
        try {
            QueryContext cxt = QueryContext.create();
//			QueryConfig.initDefault("WebContent/WEB-INF/querys");
//			QueryContext cxt = QueryContext.create(QueryConfig.defaultConfig());
            cnt = CommonQueryHelper.executeQuery("Interface/Q_INTERFACE.xml","Q_ACT_INFO_CNT", entity.getQueryParams(), cxt,
                    QueryRunner.LONG_RESULT_HANDLER).intValue();
        } catch (Exception e) {
            throw new QueryTaskException(entity,"查询宣传活动总量失败:"+e.getMessage(),ICCQueryHandler.RESP_CODE_QUERY,"query data err");
        }
        return cnt;
    }

    @Override
    public boolean isVali(QueryTaskEntity entity) throws QueryTaskException {
        //客户号不能为空
        String custId = (String)entity.getQueryParams().get("CUST_ID");
        if(StringUtil.isNull(custId))
            throw new QueryTaskException(entity,"查询宣传活动数量失败:持卡人号码不能为空",ICCQueryHandler.RESP_CODE_NOT_VALI,"cust_id is null");
        return true;
    }
}
