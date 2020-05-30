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
        HttpUtil.instance.getUrl(url, { result ->
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