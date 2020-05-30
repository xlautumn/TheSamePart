package com.same.part.assistant.manager

import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.CategoryData
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.utils.HttpUtil
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder

class PurchaseProductManager private constructor() {

    companion object {
        @JvmStatic
        val INSTANCE: PurchaseProductManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PurchaseProductManager()
        }

        const val CART_LIST_URL = "carts/group-by-shop"
        const val CATEGORY_LIST_URL = "productCategory/getAllProductCategoryList"
        const val PRODUCT_LIST_URL = "product/getProductList"
    }

    private var mPurchaseCategoryData: ArrayList<CategoryData> = ArrayList()
    private var mPurchaseProductData: ArrayList<ProductDetailData> = ArrayList()
    private var mCartProductData: ArrayList<ProductDetailData> = ArrayList()

    internal fun getPurchaseCategoryData(): ArrayList<CategoryData> = mPurchaseCategoryData
    internal fun getPurchaseProductData(): ArrayList<ProductDetailData> = mPurchaseProductData
    internal fun getCartProductData(): ArrayList<ProductDetailData> = mCartProductData

    internal var mCurrentCategoryFirstLevel = 0

    fun syncPurchaseCategoryData(onSuccess: (() -> Unit)?) {
        val url = StringBuilder("${ApiService.SERVER_URL}$CATEGORY_LIST_URL")
            .append("?appKey=${CacheUtil.getAppKey()}")
            .append("&appSecret=${CacheUtil.getAppSecret()}")

        HttpUtil.instance.getUrl(url.toString(), { result ->
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
                                val sonsList = ArrayList<CategoryData>()
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

    fun syncPurchaseProductData(
        productCategoryId: String?,
        name: String?,
        onSuccess: (() -> Unit)?
    ) {
        val url = StringBuilder("${ApiService.SERVER_URL}$PRODUCT_LIST_URL")
            .append("?appKey=${CacheUtil.getAppKey()}")
            .append("&appSecret=${CacheUtil.getAppSecret()}")
            .append("&productCategoryId=$productCategoryId")
            .append("&shopId=2000")

        HttpUtil.instance.getUrl(url.toString(), { result ->
            JSONObject(result).also { jsonObject ->
                mPurchaseProductData.clear()
                jsonObject.optJSONArray("content")?.takeIf { it.length() > 0 }?.let { jsonArray ->
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
                }
                onSuccess?.invoke()
            }
        })
    }

    fun getCartProductList(onSuccess: (() -> Unit)?) {
        val url = StringBuilder("${ApiService.SERVER_URL}$CART_LIST_URL")
            .append("?appKey=${CacheUtil.getAppKey()}")
            .append("&appSecret=${CacheUtil.getAppSecret()}")
            .append("&shopId=${CacheUtil.getShopId()}")
            .append("&userId=${CacheUtil.getUserId()}")

        HttpUtil.instance.getUrl(url.toString(), { result ->
            //todo 解析列表
            var uu = result
        })
    }

    fun uploadCartProduct(
        endTime: String, productCategory: String, quantity: String, productId: String,
        properties: String, productSkuNumber: String, onSuccess: (() -> Unit)?
    ) {
        val url = "${ApiService.SERVER_URL}cart"
        val jsonMap = hashMapOf(
            "appKey" to CacheUtil.getAppKey(),
            "appSecret" to CacheUtil.getAppSecret(),
            "accessToken" to CacheUtil.getToken(),
            "endTime" to endTime,
            "category" to productCategory,
            "quantity" to quantity,
            "productId" to productId,
            "properties" to properties,
            "productSkuNumber" to productSkuNumber
        )
        HttpUtil.instance.postUrl(url, jsonMap, { result ->
            //TODO 加入购物车后得数据
            JSONObject(result).let { jsonObject ->
                jsonObject.optJSONObject("content")?.let { content ->
                    //TODO 解析数据

                }
            }
        })
    }

    fun getProductSpecs(productId: String, onSuccess: (() -> Unit)?) {
        var url = StringBuilder("${ApiService.SERVER_URL}$productId")
            .append("?appKey=${CacheUtil.getAppKey()}")
            .append("&appSecret=${CacheUtil.getAppSecret()}")
        HttpUtil.instance.getUrl(url.toString(), { result ->
                val uu =result
        })
    }
}