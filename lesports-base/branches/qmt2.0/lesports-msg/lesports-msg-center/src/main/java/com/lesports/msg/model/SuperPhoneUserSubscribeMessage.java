package com.lesports.msg.model;

import java.util.HashMap;
import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/19
 */
public class SuperPhoneUserSubscribeMessage extends HashMap<String, SuperPhoneUserSubscribeMessage.Action>{
    public static class Action {
        private Integer flag;
        private List<Long> add;
        private List<Long> del;

        public Integer getFlag() {
            return flag;
        }

        public void setFlag(Integer flag) {
            this.flag = flag;
        }

        public List<Long> getAdd() {
            return add;
        }

        public void setAdd(List<Long> add) {
            this.add = add;
        }

        public List<Long> getDel() {
            return del;
        }

        public void setDel(List<Long> del) {
            this.del = del;
        }

        public boolean isEvents() {
            return null != flag && flag == 1;
        }

        public boolean isLeword() {
            return null != flag && flag == 2;
        }
    }
}
