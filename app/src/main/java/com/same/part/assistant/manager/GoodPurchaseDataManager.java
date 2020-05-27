package com.same.part.assistant.manager;

import com.same.part.assistant.model.GoodClassModel;

import java.util.ArrayList;

public class GoodPurchaseDataManager {

    private static GoodPurchaseDataManager instance;

    private ArrayList<GoodClassModel> mGoodPurchaseModels = new ArrayList();
    private ArrayList<GoodClassModel.GoodModel> mCartGoodModels = new ArrayList();

    private GoodPurchaseDataManager() {
    }

    public static synchronized GoodPurchaseDataManager getInstance() {//使用同步锁
        if (instance == null) {
            instance = new GoodPurchaseDataManager();
        }
        return instance;
    }

    public ArrayList<GoodClassModel> getGoodPurchaseModels() {
        return mGoodPurchaseModels;
    }

    public ArrayList<GoodClassModel.GoodModel> getCartGoodModels() {
        return mCartGoodModels;
    }

    public void syncGoodPurchaseData() {
        mGoodPurchaseModels.clear();
        for (int i = 0; i < 4; i++) {
            GoodClassModel list = new GoodClassModel();
            list.setSelectNum(0);
            list.setLevel(String.valueOf(i));
            list.setTitle((i % 2 == 0) ? "水果" + i : "蔬菜" + i);

            ArrayList<GoodClassModel.GoodModel> goodModels = new ArrayList<>();
            for (int j = 0; j < 15; j++) {
                GoodClassModel.GoodModel goodModel = new GoodClassModel.GoodModel();
                goodModel.setName(list.getTitle() + "-->" + "花菜" + j);
                goodModel.setDesc("测试-" + j);
                goodModel.setInCart((j != 0 && j % 2 == 0) ? true : false);
                goodModel.setFirstLevel(String.valueOf(i));
                goodModel.setPrice("2.2");
                goodModels.add(goodModel);
            }
            list.setContentModels(goodModels);
            mGoodPurchaseModels.add(list);
        }
    }

}
