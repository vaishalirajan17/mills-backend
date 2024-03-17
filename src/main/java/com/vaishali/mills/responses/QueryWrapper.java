package com.vaishali.mills.responses;

import lombok.Getter;

@Getter
public class QueryWrapper {

        private String result;
        private int state;

        public QueryWrapper(String result, int state) {
            this.result = result;
            this.state = state;
        }

}
