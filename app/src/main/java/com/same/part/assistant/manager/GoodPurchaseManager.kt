package com.same.part.assistant.manager

import android.util.Log
import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.data.model.CategoryData
import com.same.part.assistant.data.model.ProductData
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.utils.HttpUtil
import org.json.JSONArray
import org.json.JSONObject

class GoodPurchaseManager private constructor() {

    companion object {
        @JvmStatic
        val instance: GoodPurchaseManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GoodPurchaseManager()
        }
    }

    private var mPurchaseCategoryData: ArrayList<CategoryData> = ArrayList()
    private var mPurchaseProductData: ArrayList<ProductDetailData> = ArrayList()

    internal fun getPurchaseCategoryData(): ArrayList<CategoryData> = mPurchaseCategoryData
    internal fun getPurchaseProductData(): ArrayList<ProductDetailData> = mPurchaseProductData

    internal var mCurrentCategoryFirstLevel = 0

    //TODO 请求数据: 从服务器获取数据，缓存在本地（尽量添加版本号，用于判断是否下拉数据）
    //添加入参回调
    fun syncPurchaseCategoryData(onSuccess: (() -> Unit)?) {
        val url =
            "${ApiService.SERVER_URL}productCategory/getAllProductCategoryList?appKey=99B9LGSypeHjWB11&appSecret=99osxt7y6szsf211"

        HttpUtil.instance.getUrl(url, { result ->
            JSONArray(result).takeIf { it.length() > 0 }?.also { jsonArray ->
                mPurchaseCategoryData.clear()
                for (i in 0 until jsonArray.length()) {
                    jsonArray.optJSONObject(i)?.let { jsonObject ->
                        CategoryData().apply {
                            this.productCategoryId = jsonObject.optString("productCategoryId")
                            this.addTime = jsonObject.optString("addTime")
                            this.name = jsonObject.optString("name")
                            this.description = jsonObject.optString("description")
                            this.img = jsonObject.optString("img")
                            this.unit = jsonObject.optString("unit")
                            this.sequence = jsonObject.optString("sequence")
                            this.ifShow = jsonObject.optBoolean("ifShow")
                            jsonObject.optJSONArray("sons")?.let { sons ->
                                var sonsList = ArrayList<CategoryData>()
                                for (k in 0 until sons.length()) {
                                    sons.optJSONObject(k)?.let { son ->
                                        CategoryData().let { secondCategory ->
                                            secondCategory.productCategoryId =
                                                son.optString("productCategoryId")
                                            secondCategory.addTime = son.optString("addTime")
                                            secondCategory.name = son.optString("name")
                                            secondCategory.description =
                                                son.optString("description")
                                            secondCategory.img = son.optString("img")
                                            secondCategory.unit = son.optString("unit")
                                            secondCategory.sequence = son.optString("sequence")
                                            secondCategory.ifShow = son.optBoolean("ifShow")
                                            sonsList.add(secondCategory)
                                        }
                                    }
                                }
                                this.sons = sonsList
                            }
                            mPurchaseCategoryData.add(this)
                        }
                    }
                }
                onSuccess?.invoke()
            }

        })
    }

    fun syncPurchaseProductData(productCategoryId: String?, name: String?, onSuccess: (() -> Unit)?) {
        val url = "${ApiService.SERVER_URL}product/getProductList?" +
                "appKey=99B9LGSypeHjWB11&appSecret=99osxt7y6szsf211" +
                "&productCategoryId=$productCategoryId&name=$name"
        Log.e("jml", "产品列表")
        HttpUtil.instance.getUrl(url, { result ->
            Log.e("jml", "产品列表 success")
            val result="{\n" +
                    "    \"content\": [\n" +
                    "        {\n" +
                    "            \"productId\": 2024,\n" +
                    "            \"addTime\": \"2019-12-20T18:11:44\",\n" +
                    "            \"updateTime\": \"2020-03-22T21:52:28\",\n" +
                    "            \"name\": \"浓缩沙果浆礼盒\",\n" +
                    "            \"brand\": null,\n" +
                    "            \"sequence\": 0,\n" +
                    "            \"shopBrand\": null,\n" +
                    "            \"shopName\": \"仓加互联网科技\",\n" +
                    "            \"specification\": null,\n" +
                    "            \"number\": \"352-2024\",\n" +
                    "            \"barcode\": null,\n" +
                    "            \"weight\": 1.0,\n" +
                    "            \"quantity\": 50,\n" +
                    "            \"withHoldQuantity\": 0,\n" +
                    "            \"pointRate\": 0.0,\n" +
                    "            \"coinRate\": 0.0,\n" +
                    "            \"price\": 600.0,\n" +
                    "            \"costPrice\": 300.0,\n" +
                    "            \"linePrice\": 600.0,\n" +
                    "            \"finalPrice\": null,\n" +
                    "            \"ifWarn\": false,\n" +
                    "            \"warnQuantity\": 2,\n" +
                    "            \"totalSales\": 0,\n" +
                    "            \"monthSales\": 0,\n" +
                    "            \"unit\": null,\n" +
                    "            \"img\": \"https://qiniu.easyapi.com/2019/12/20/1576836587251\",\n" +
                    "            \"imgs\": \"https://qiniu.easyapi.com/2019/12/20/1576836587251\",\n" +
                    "            \"description\": null,\n" +
                    "            \"count\": 0,\n" +
                    "            \"favoriteCount\": 0,\n" +
                    "            \"state\": 1,\n" +
                    "            \"auditState\": 1,\n" +
                    "            \"auditResult\": \"\",\n" +
                    "            \"ratio\": 0.0,\n" +
                    "            \"ifPlatformCut\": false,\n" +
                    "            \"platformCutRate\": 0.0,\n" +
                    "            \"grouponState\": 0,\n" +
                    "            \"limitDiscountState\": 0,\n" +
                    "            \"stockDeduct\": 0,\n" +
                    "            \"productCategory\": \"饮料冲品\",\n" +
                    "            \"groupIds\": [],\n" +
                    "            \"shop\": {\n" +
                    "                \"shopId\": 159,\n" +
                    "                \"name\": \"仓加互联网科技\",\n" +
                    "                \"brand\": null,\n" +
                    "                \"moods\": 0,\n" +
                    "                \"firstScore\": 4.92,\n" +
                    "                \"secondScore\": 0.0,\n" +
                    "                \"thirdScore\": 4.91,\n" +
                    "                \"commentCount\": 881,\n" +
                    "                \"monthOrders\": 0,\n" +
                    "                \"totalOrders\": 2942,\n" +
                    "                \"open\": false\n" +
                    "            },\n" +
                    "            \"productExtends\": [\n" +
                    "                {\n" +
                    "                    \"productExtendId\": 12294,\n" +
                    "                    \"addTime\": \"2019-12-25T18:21:34\",\n" +
                    "                    \"updateTime\": \"2019-12-25T18:21:34\",\n" +
                    "                    \"fieldKey\": \"ifOrdinary\",\n" +
                    "                    \"fieldName\": \"是否为综合区商品\",\n" +
                    "                    \"fieldValue\": \"true\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"productExtendId\": 12295,\n" +
                    "                    \"addTime\": \"2019-12-25T18:21:34\",\n" +
                    "                    \"updateTime\": \"2019-12-25T18:21:34\",\n" +
                    "                    \"fieldKey\": \"pvRate\",\n" +
                    "                    \"fieldName\": \"PV分成比例\",\n" +
                    "                    \"fieldValue\": \"0.00\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"productExtendId\": 12296,\n" +
                    "                    \"addTime\": \"2019-12-25T18:21:34\",\n" +
                    "                    \"updateTime\": \"2019-12-25T18:21:34\",\n" +
                    "                    \"fieldKey\": \"promoter_rate\",\n" +
                    "                    \"fieldName\": \"推荐分成比例\",\n" +
                    "                    \"fieldValue\": \"0\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"productSku\": [\n" +
                    "                {\n" +
                    "                    \"productSkuId\": 16298,\n" +
                    "                    \"addTime\": \"2019-12-20T18:11:44\",\n" +
                    "                    \"updateTime\": \"2019-12-20T18:12:03\",\n" +
                    "                    \"number\": \"2024-16298\",\n" +
                    "                    \"barcode\": null,\n" +
                    "                    \"weight\": 1.0,\n" +
                    "                    \"properties\": \"[{\\\"project\\\":\\\"支\\\",\\\"value\\\":\\\"30\\\"}]\",\n" +
                    "                    \"quantity\": 50,\n" +
                    "                    \"warnQuantity\": 10,\n" +
                    "                    \"price\": 600.0,\n" +
                    "                    \"costPrice\": 300.0,\n" +
                    "                    \"linePrice\": 600.0,\n" +
                    "                    \"totalSales\": 0,\n" +
                    "                    \"withHoldQuantity\": 0,\n" +
                    "                    \"img\": \"http://qiniu.easyapi.com/2019/12/20/1576836623695\",\n" +
                    "                    \"state\": 1\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"productSkuId\": 16299,\n" +
                    "                    \"addTime\": \"2019-12-20T18:12:03\",\n" +
                    "                    \"updateTime\": \"2019-12-21T10:59:18\",\n" +
                    "                    \"number\": \"2024-16298\",\n" +
                    "                    \"barcode\": null,\n" +
                    "                    \"weight\": 1.0,\n" +
                    "                    \"properties\": \"[{\\\"project\\\":\\\"支\\\",\\\"value\\\":\\\"30\\\"}]\",\n" +
                    "                    \"quantity\": 50,\n" +
                    "                    \"warnQuantity\": 10,\n" +
                    "                    \"price\": 600.0,\n" +
                    "                    \"costPrice\": 300.0,\n" +
                    "                    \"linePrice\": 600.0,\n" +
                    "                    \"totalSales\": 0,\n" +
                    "                    \"withHoldQuantity\": 0,\n" +
                    "                    \"img\": \"http://qiniu.easyapi.com/2019/12/20/1576836623695\",\n" +
                    "                    \"state\": 1\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"productSkuId\": 16300,\n" +
                    "                    \"addTime\": \"2019-12-21T10:59:18\",\n" +
                    "                    \"updateTime\": \"2019-12-25T18:21:33\",\n" +
                    "                    \"number\": \"2024-16298\",\n" +
                    "                    \"barcode\": null,\n" +
                    "                    \"weight\": 1.0,\n" +
                    "                    \"properties\": \"[{\\\"project\\\":\\\"支\\\",\\\"value\\\":\\\"30\\\"}]\",\n" +
                    "                    \"quantity\": 50,\n" +
                    "                    \"warnQuantity\": 10,\n" +
                    "                    \"price\": 600.0,\n" +
                    "                    \"costPrice\": 300.0,\n" +
                    "                    \"linePrice\": 600.0,\n" +
                    "                    \"totalSales\": 0,\n" +
                    "                    \"withHoldQuantity\": 0,\n" +
                    "                    \"img\": \"http://qiniu.easyapi.com/2019/12/20/1576836623695\",\n" +
                    "                    \"state\": 1\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"productSkuId\": 16316,\n" +
                    "                    \"addTime\": \"2019-12-25T18:21:33\",\n" +
                    "                    \"updateTime\": \"2020-03-17T01:14:16\",\n" +
                    "                    \"number\": \"2024-16298\",\n" +
                    "                    \"barcode\": null,\n" +
                    "                    \"weight\": 1.0,\n" +
                    "                    \"properties\": \"[{\\\"project\\\":\\\"支\\\",\\\"value\\\":\\\"30\\\"}]\",\n" +
                    "                    \"quantity\": 50,\n" +
                    "                    \"warnQuantity\": 10,\n" +
                    "                    \"price\": 600.0,\n" +
                    "                    \"costPrice\": 300.0,\n" +
                    "                    \"linePrice\": 600.0,\n" +
                    "                    \"totalSales\": 0,\n" +
                    "                    \"withHoldQuantity\": 0,\n" +
                    "                    \"img\": \"http://qiniu.easyapi.com/2019/12/20/1576836623695\",\n" +
                    "                    \"state\": 1\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"pageable\": {\n" +
                    "        \"sort\": {\n" +
                    "            \"sorted\": true,\n" +
                    "            \"unsorted\": false,\n" +
                    "            \"empty\": false\n" +
                    "        },\n" +
                    "        \"offset\": 0,\n" +
                    "        \"pageSize\": 10,\n" +
                    "        \"pageNumber\": 0,\n" +
                    "        \"unpaged\": false,\n" +
                    "        \"paged\": true\n" +
                    "    },\n" +
                    "    \"totalElements\": 1,\n" +
                    "    \"totalPages\": 1,\n" +
                    "    \"last\": true,\n" +
                    "    \"number\": 0,\n" +
                    "    \"size\": 10,\n" +
                    "    \"sort\": {\n" +
                    "        \"sorted\": true,\n" +
                    "        \"unsorted\": false,\n" +
                    "        \"empty\": false\n" +
                    "    },\n" +
                    "    \"numberOfElements\": 1,\n" +
                    "    \"first\": true,\n" +
                    "    \"empty\": false\n" +
                    "}"
            JSONObject(result).also { jsonObject ->
                jsonObject.optJSONArray("content")?.takeIf { it.length() > 0 }?.let { jsonArray ->
                    mPurchaseProductData.clear()
                    for (i in 0 until jsonArray.length()) {
                        jsonArray.optJSONObject(i)?.let { jsonObject ->
                            ProductDetailData().apply {
                                this.productId = jsonObject.optString("productId")
                                this.name = jsonObject.optString("name")
                                this.price = jsonObject.optString("price")
                                this.img = jsonObject.optString("img")
                                //todo 解析规格
                                mPurchaseProductData.add(this)
                            }
                        }
                    }
                    onSuccess?.invoke()
                }
            }
        })
    }
}