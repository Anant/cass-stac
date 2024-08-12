package com.datastax.oss.cass_stac.util;

import java.util.List;

public enum Operator {
    EQ {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return actualValue.equals(expectedValue);
        }
    },
    NEQ {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return !actualValue.equals(expectedValue);
        }
    },
    GT {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return Double.parseDouble(actualValue) > Double.parseDouble(expectedValue);
        }
    },
    LT {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return Double.parseDouble(actualValue) < Double.parseDouble(expectedValue);
        }
    },
    GTE {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return Double.parseDouble(actualValue) >= Double.parseDouble(expectedValue);
        }
    },
    LTE {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return Double.parseDouble(actualValue) <= Double.parseDouble(expectedValue);
        }
    },
    STARTSWITH {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return actualValue.startsWith(expectedValue);
        }
    },
    ENDSWITH {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return actualValue.endsWith(expectedValue);
        }
    },
    CONTAINS {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return actualValue.contains(expectedValue);
        }
    },
    IN {
        @Override
        public boolean apply(String actualValue, String expectedValue) {
            return List.of(expectedValue.split(",")).contains(actualValue);
        }
    };

    public abstract boolean apply(String actualValue, String expectedValue);
}
