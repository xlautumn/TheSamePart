package com.same.part.assistant.manager

import com.same.part.assistant.model.GoodClassModel

class GoodPurchaseManager private constructor() {

    companion object {
        @JvmStatic
        val instance: GoodPurchaseManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GoodPurchaseManager()
        }
    }

    private var mGoodPurchaseModels: ArrayList<GoodClassModel> = ArrayList()
    private var mCartGoodModels: ArrayList<GoodClassModel.GoodModel> = ArrayList()

    internal fun getGoodPurchaseModels(): ArrayList<GoodClassModel> = mGoodPurchaseModels
    internal fun getCartGoodModels(): ArrayList<GoodClassModel.GoodModel> = mCartGoodModels

    //TODO 请求数据: 从服务器获取数据，缓存在本地（尽量添加版本号，用于判断是否下拉数据）
    fun syncGoodPurchaseData() {
        for (n in 1..4) {
            val list = GoodClassModel()
            list.selectNum = 0
            list.level = n.toString()
            list.title = if (n % 2 == 0) "水果" else "蔬菜"

            var goodModelList = ArrayList<GoodClassModel.GoodModel>()
            for (i in 1..15) {
                val goodModel = GoodClassModel.GoodModel()
                goodModel.desc = "我是测试数据——$i"
                goodModel.firstLevel = n.toString()
                goodModel.inCart = false
                goodModel.name = "花菜_$i"
                goodModel.price = "2.2"
                goodModelList.add(goodModel)
            }
            list.contentModels = goodModelList
            mGoodPurchaseModels.add(list)
        }
    }
}