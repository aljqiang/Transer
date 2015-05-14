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
public class RewardQueryHandler implements ICCQueryHandler {

    private static Log log = LogFactory.getLog(RewardQueryHandler.class);

    @Override
    public void run(QueryTaskEntity entity)  throws QueryTaskException{
        try {

/*			//将活动截止时间区间化为数字
			String startFlag = (String)entity.getQueryParams().get("OFFER_END_DATE_START");
			String endFlag =  (String)entity.getQueryParams().get("OFFER_END_DATE_END");
			if(!StringUtil.isNull(startFlag))
				entity.getQueryParams().put("OFFER_END_DATE_START", startFlag.substring(0,6));
			if(!StringUtil.isNull(endFlag))
				entity.getQueryParams().put("OFFER_END_DATE_END", endFlag.substring(0,6));*/

            QueryContext cxt = QueryContext.create();
//			QueryConfig.initDefault("WebContent/WEB-INF/querys");
//			QueryContext cxt = QueryContext.create(QueryConfig.defaultConfig());
            CommonQueryHelper.executeQuery("Interface/Q_INTERFACE.xml",
                    "Q_REWARD_INFO_QUERY", entity.getQueryParams(), cxt,
                    new RewardResultSetHandler(this.responseBody(), entity.getQueryParams(),entity.getOutput()));
        } catch (Exception e) {
            throw new QueryTaskException(entity,"查询奖励活动查询结果失败:"+e.getMessage(),ICCQueryHandler.RESP_CODE_QUERY,"query data err");
        }
    }

    private static class RewardResultSetHandler implements ResultSetHandler {

        private String ftl;
        private OutputStream out;
        private Map<String,Object> params;
        public RewardResultSetHandler(String ftl, Map<String,Object> params,OutputStream out) {
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
                rowData.put("TREATMENT_DESC", rs.getString("TREATMENT_DESC"));
                rowData.put("TREAT_TYPE", rs.getString("TREAT_TYPE"));
                rowData.put("REWARD_AMT", rs.getString("REWARD_AMT"));
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
        return "protocol/SoapRespRewardQuery.ftl";
    }

    /**
     * 测试入口
     * @param args
     * @throws QueryTaskException
     */
    public static void main(String[] args) throws QueryTaskException {
        RewardQueryHandler handler = new RewardQueryHandler();
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
/*			//将活动截止时间区间化为数字
			String startFlag = (String)entity.getQueryParams().get("OFFER_END_DATE_START");
			String endFlag =  (String)entity.getQueryParams().get("OFFER_END_DATE_END");
			if(!StringUtil.isNull(startFlag))
				entity.getQueryParams().put("OFFER_END_DATE_START", startFlag.substring(0,6));
			if(!StringUtil.isNull(endFlag))
				entity.getQueryParams().put("OFFER_END_DATE_END", endFlag.substring(0,6));*/

            QueryContext cxt = QueryContext.create();
//			QueryConfig.initDefault("WebContent/WEB-INF/querys");
//			QueryContext cxt = QueryContext.create(QueryConfig.defaultConfig());
            cnt = CommonQueryHelper.executeQuery("Interface/Q_INTERFACE.xml",
                    "Q_REWARD_INFO_CNT", entity.getQueryParams(), cxt,
                    QueryRunner.LONG_RESULT_HANDLER).intValue();
        } catch (Exception e) {
            throw new QueryTaskException(entity,"查询奖励活动数量失败:"+e.getMessage(),ICCQueryHandler.RESP_CODE_QUERY,"query data err");
        }
        return cnt;
    }

    @Override
    public boolean isVali(QueryTaskEntity entity) throws QueryTaskException {
        //客户号不能为空
        String custId = (String)entity.getQueryParams().get("CUST_ID");
        if(StringUtil.isNull(custId))
            throw new QueryTaskException(entity,"查询奖励活动数量失败:持卡人号码不能为空",ICCQueryHandler.RESP_CODE_NOT_VALI,"cust_id is null");
        return true;
    }
}
