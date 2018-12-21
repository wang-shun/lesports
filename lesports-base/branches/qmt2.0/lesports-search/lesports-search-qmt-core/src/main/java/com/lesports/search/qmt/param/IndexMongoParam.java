/**
 * 
 */
package com.lesports.search.qmt.param;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

/**
 * @author sunyue7
 *
 */
public class IndexMongoParam extends BaseSearchParam {

	@QueryParam("rpc")
	@DefaultValue("true")
	private boolean rpc;
	
	@QueryParam("batchSize")
	@DefaultValue("100")
	private int batchSize;

	public boolean isRpc() {
		return rpc;
	}

	public int getBatchSize() {
		return batchSize;
	}
}
