package com.same.part.assistant.data.model

data class CashierAllProduct(val categoryList: List<CustomCategory>) {

    //存放二级分类的id 和 对应的商品集合
    private val _categoryProductMap: HashMap<String, ArrayList<CashierProduct>> = HashMap()
    val categoryProductMap: Map<String, ArrayList<CashierProduct>> = _categoryProductMap
    //存放一级、二级分类
    private val _categoryMap: HashMap<String, List<CustomCategory>> = HashMap()
    val categoryMap: Map<String, List<CustomCategory>> = _categoryMap

    init {
        categoryList.filterNot { it.son.isNullOrEmpty() }.forEach { firstCategory ->
            _categoryMap[firstCategory.customCategoryId] = firstCategory.son

            firstCategory.son.forEach { secondCategory ->
                _categoryProductMap[secondCategory.customCategoryId] =
                    arrayListOf<CashierProduct>()
            }

        }
    }

    /**
     * 获取一级分类的名称
     */
    fun getFirstCategory(position: Int): CustomCategory {
        return categoryList[position]
    }

    /**
     * 获取二级分类集合
     */
    fun getSecondTitleList(firstCategoryId: String): List<CustomCategory> {
        return categoryMap[firstCategoryId]?: arrayListOf()
    }

    /**
     * 获取二级分类中的商品集合
     */
    fun getProductList(secondCategoryId: String): ArrayList<CashierProduct> {
        return categoryProductMap[secondCategoryId]?: arrayListOf()
    }
}