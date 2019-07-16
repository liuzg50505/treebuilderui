package com.liuzg.def;

import java.util.ArrayList;
import java.util.List;

public class EnumDefinition extends TypeDefinition {

    public static class EnumItem{
        public String key;
        public String description;
        public String code;

        public EnumItem() {
        }

        public EnumItem(String key, String code) {
            this.key = key;
            this.code = code;
        }

        public EnumItem(String key, String code, String description) {
            this.key = key;
            this.description = description;
            this.code = code;
        }
    }

    protected List<EnumItem> enumItems = new ArrayList<>();

    public List<EnumItem> getEnumItems() {
        return enumItems;
    }

    public void addEnumItem(EnumItem enumItem){
        enumItems.add(enumItem);
    }

    public void setEnumItems(List<EnumItem> enumItems) {
        this.enumItems = enumItems;
    }

    public EnumItem parse(String key) {
        for (EnumItem enumItem: enumItems) {
            if(key.equals(enumItem.key)) {
                return enumItem;
            }
        }
        return null;
    }
}
