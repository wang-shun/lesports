package com.lesports.id;

import com.lesports.utils.LeProperties;

/**
 * User: ellios
 * Time: 16-3-3 : 上午10:39
 */
public interface IDConstants {

    static final long ID_INC_SPAN = LeProperties.getLong("id.inc.span", 100);
}
