package com.same.part.assistant.manager

import com.same.part.assistant.app.network.ApiService
import com.same.part.assistant.app.util.CacheUtil
import com.same.part.assistant.data.model.CategoryData
import com.same.part.assistant.data.model.ProductDetailData
import com.same.part.assistant.data.model.ProductSku
import com.same.part.assistant.data.model.PropertyData
import com.same.part.assistant.utils.HttpUtil
import me.hgj.jetpackmvvm.network.ExceptionHandle
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder

class PurchaseProductManager private constructor() {

    companion object {
        @JvmStatic
        val INSTANCE: PurchaseProductManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PurchaseProductManager()
        }

        const val CATEGORY_LIST_URL = "productCategory/getAllProductCategoryList"
        const val PRODUCT_LIST_URL = "products"
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
            .append("&state=1")

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
                                val productSku = jsonObject.optJSONArray("productSku")
                                this.hasSku = productSku?.length()!=0?:false
                                mPurchaseProductData.add(this)
                            }
                        }
                    }
                }
                onSuccess?.invoke()
            }
        })
    }

    fun getProductSpecs(
        productId: String, onSuccess: ((
            LinkedHashMap<String, MutableSet<PropertyData>>?,
            LinkedHashSet<ProductSku>?
        ) -> Unit)?,
        onError: ((String) -> Unit)? = null
    ) {
        var url = StringBuilder("${ApiService.SERVER_URL}product/$productId")
            .append("?appKey=${CacheUtil.getAppKey()}")
            .append("&appSecret=${CacheUtil.getAppSecret()}")
        HttpUtil.instance.getUrl(url.toString(), { result ->
            try {
                val propertyList = linkedMapOf<String, MutableSet<PropertyData>>()
                val propertyPriceList = linkedSetOf<ProductSku>()
                result.takeIf { it.isNotEmpty() }?.let {
                    JSONObject(it).optJSONObject("content")?.optJSONArray("productSkus")
                        ?.takeIf { productSkus -> productSkus.length() > 0 }
                        ?.let { productSkus ->
                            for (i in 0 until productSkus.length()) {
                                productSkus.optJSONObject(i)?.also { productSku ->
                                    val propertiesStr = productSku.optString("properties")
                                    JSONArray(propertiesStr).takeIf { properties -> properties.length() > 0 }
                                        ?.let { properties ->
                                            val propertyKey = linkedSetOf<String>()
                                            for (j in 0 until properties.length()) {
                                                properties.optJSONObject(j)?.let { property ->
                                                    val project = property.optString("project")
                                                    val value = property.optString("value")
                                                    val propertyData =
                                                        PropertyData(project, value, false)
                                                    if (!propertyList.containsKey(project)) {
                                                        //未包含该属性
                                                        propertyList[project] =
                                                            mutableSetOf(propertyData)
                                                    } else {
                                                        propertyList[project]?.add(propertyData)
                                                    }
                                                    propertyKey.add(value)
                                                }
                                            }
                                            propertyPriceList.add(
                                                ProductSku(
                                                    productSku.optString("productSkuId"),
                                                    productSku.optString("number"),
                                                    productSku.optString("price"),
                                                    propertyKey,
                                                    productSku.optString("weight")

                                                )
                                            )
                                        }
                                }
                            }
                        }
                }
                onSuccess?.invoke(propertyList, propertyPriceList)
            }catch (e:Exception){
                onError?.invoke(ExceptionHandle.handleException(e).errorMsg)
            }

        },onError)
    }
}