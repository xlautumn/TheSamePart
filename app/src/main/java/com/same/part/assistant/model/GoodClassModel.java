package com.same.part.assistant.model;

import java.util.ArrayList;

public class GoodClassModel {

    private String title;
    private int selectNum;
    private String level;
    private ArrayList<GoodModel> contentModels;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<GoodModel> getContentModels() {
        return contentModels;
    }

    public void setContentModels(ArrayList<GoodModel> contentModels) {
        this.contentModels = contentModels;
    }

    public int getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(int selectNum) {
        this.selectNum = selectNum;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public static class GoodModel {
        private String name;
        private String desc;
        private String price;
        private Boolean inCart;
        private String firstLevel;

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getInCart() {
            return inCart;
        }

        public void setInCart(Boolean inCart) {
            this.inCart = inCart;
        }

        public String getFirstLevel() {
            return firstLevel;
        }

        public void setFirstLevel(String firstLevel) {
            this.firstLevel = firstLevel;
        }
    }
}
