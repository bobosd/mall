package com.jiezipoi.mall.commons;

public enum IndexConfigEnum {

    DEFAULT(0, "DEFAULT"),
    INDEX_SEARCH_HOTS(1, "INDEX_SEARCH_HOTS"),
    INDEX_SEARCH_DOWN_HOTS(2, "INDEX_SEARCH_DOWN_HOTS"),
    INDEX_GOODS_HOT(3, "INDEX_GOODS_HOTS"),
    INDEX_GOODS_NEW(4, "INDEX_GOODS_NEW"),
    INDEX_GOODS_RECOMMEND(5, "INDEX_GOODS_RECOMMEND");

    private int type;
    private String name;

    IndexConfigEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static IndexConfigEnum getIndexConfigEnumByType(int type) {
        for (IndexConfigEnum configEnum : IndexConfigEnum.values()) {
            if (configEnum.getType() == type) {
                return configEnum;
            }
        }
        return DEFAULT;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
