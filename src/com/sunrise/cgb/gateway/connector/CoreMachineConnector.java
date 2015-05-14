package com.sunrise.cgb.gateway.connector;

import com.sunrise.cgb.gateway.IConnectorRequest;
import com.sunrise.cgb.gateway.IConnectorResponse;

/**
 * @author 李嘉伟
 * @project:CGBTranser
 * @description:TODO
 */
public class CoreMachineConnector extends TransConnectorImpl {

	public CoreMachineConnector() {
		super(null,0);
	}

	@Override
	public IConnectorResponse sendRequest(IConnectorRequest request)
			throws Exception {
		throw new Exception("主机通讯未实现.");
	}

}
