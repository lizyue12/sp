package ltd.newbee.mall.api.mall.vo;

import ltd.newbee.mall.entity.GoodsCategory;

import java.io.Serializable;
import java.util.List;

/**
 * 商品搜索页面分类信息展示的视图对象
 */
public class SearchPageCategoryVO implements Serializable {

    // 一级分类名称
    private String firstLevelCategoryName;

    // 二级分类列表
    private List<GoodsCategory> secondLevelCategoryList;

    // 二级分类名称
    private String secondLevelCategoryName;

    // 三级分类列表
    private List<GoodsCategory> thirdLevelCategoryList;

    // 当前分类名称
    private String currentCategoryName;

    // 省略getter和setter方法
}
