package com.lesports.bole.utils;


import com.lesports.LeConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * User: ellios
 * Time: 15-6-5 : 上午12:42
 */
public class PageUtils {

    private static final Pageable DEFALT_PAGE_REQUEST = new PageRequest(0, LeConstants.DEFAULT_PAGE_SIZE,
            Sort.Direction.ASC, "_id");

    /**
     * 检查分页的边界条件，获取合法的分页请求
     *
     * @param pageable
     * @return
     */
    public static Pageable getValidPageable(Pageable pageable) {
        if (pageable == null) return DEFALT_PAGE_REQUEST;
        Pageable valid = pageable;
        if (valid.getPageNumber() < 0) {
            valid = new PageRequest(0, valid.getPageSize(), valid.getSort());
        }
        if (valid.getPageSize() > LeConstants.MAX_PAGE_SIZE) {
            valid = new PageRequest(valid.getPageNumber(),
                    LeConstants.MAX_PAGE_SIZE, pageable.getSort());
        }
        if (valid.getPageSize() <= 0) {
            return new PageRequest(pageable.getPageNumber(),
                    LeConstants.DEFAULT_PAGE_SIZE, pageable.getSort());
        }
        return valid;
    }

    /**
     * 创建分页
     *
     * @param page
     * @param count
     * @return
     */
    public static Pageable createPageable(int page, int count) {
        return new PageRequest(page, count);
    }

//    public static Pageable convertToPageable(TPageRequest tPageRequest) {
//        if (tPageRequest == null) {
//            return DEFALT_PAGE_REQUEST;
//        }
//
//        Pageable pageable = null;
//        if (StringUtils.isEmpty(tPageRequest.getSort())) {
//            int page = tPageRequest.getPage() > 0 ? tPageRequest.getPage() - 1 : tPageRequest.getPage();
//            page = page < 0 ? 0 : page;
//            pageable = new PageRequest(page, tPageRequest.getCount());
//        } else {
//            Sort.Direction direction = Sort.Direction.ASC;
//            if (tPageRequest.getDirection() == Direction.DESC) {
//                direction = Sort.Direction.DESC;
//            }
//            int page = tPageRequest.getPage() > 0 ? tPageRequest.getPage() - 1 : tPageRequest.getPage();
//            page = page < 0 ? 0 : page;
//            pageable = new PageRequest(page, tPageRequest.getCount(), direction, tPageRequest.getSort());
//        }
//        return pageable;
//    }
}
