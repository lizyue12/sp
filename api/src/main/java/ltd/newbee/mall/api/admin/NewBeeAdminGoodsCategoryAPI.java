package ltd.newbee.mall.api.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import ltd.newbee.mall.api.admin.param.BatchIdParam;
import ltd.newbee.mall.api.admin.param.GoodsCategoryAddParam;
import ltd.newbee.mall.api.admin.param.GoodsCategoryEditParam;
import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.config.annotation.TokenToAdminUser;
import ltd.newbee.mall.entity.AdminUserToken;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.service.NewBeeMallCategoryService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// RestController注解表示这个类处理REST请求。
// Api注解用于Swagger API文档。
@RestController
@Api(value = "v1", tags = "8-2.后台管理系统分类模块接口")
@RequestMapping("/manage-api/v1")
public class NewBeeAdminGoodsCategoryAPI {

    // 用于记录日志的Logger。
    private static final Logger logger = LoggerFactory.getLogger(NewBeeAdminGoodsCategoryAPI.class);

    // Resource注解用于依赖注入。
    @Resource
    private NewBeeMallCategoryService newBeeMallCategoryService;

    // 用于列举商品分类的端点。
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @ApiOperation(value = "商品分类列表", notes = "根据级别和上级分类id查询")
    public Result list(@RequestParam(required = false) @ApiParam(value = "页码") Integer pageNumber,
                       @RequestParam(required = false) @ApiParam(value = "每页条数") Integer pageSize,
                       @RequestParam(required = false) @ApiParam(value = "分类级别") Integer categoryLevel,
                       @RequestParam(required = false) @ApiParam(value = "上级分类的id") Long parentId, @TokenToAdminUser AdminUserToken adminUser) {
        // 记录adminUser信息。
        logger.info("adminUser:{}", adminUser.toString());
        // 参数验证。
        if (pageNumber == null || pageNumber < 1 || pageSize == null || pageSize < 10 || categoryLevel == null || categoryLevel < 0 || categoryLevel > 3 || parentId == null || parentId < 0) {
            return ResultGenerator.genFailResult("分页参数异常！");
        }
        // 创建查询分类的参数。
        Map params = new HashMap(8);
        params.put("page", pageNumber);
        params.put("limit", pageSize);
        params.put("categoryLevel", categoryLevel);
        params.put("parentId", parentId);
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        // 调用服务获取分类并返回结果。
        return ResultGenerator.genSuccessResult(newBeeMallCategoryService.getCategorisPage(pageUtil));
    }

    // 用于列举供选择的商品分类的端点。
    @RequestMapping(value = "/categories4Select", method = RequestMethod.GET)
    @ApiOperation(value = "商品分类列表", notes = "用于三级分类联动效果制作")
    public Result listForSelect(@RequestParam("categoryId") Long categoryId, @TokenToAdminUser AdminUserToken adminUser) {
        // 记录adminUser信息。
        logger.info("adminUser:{}", adminUser.toString());
        // 参数验证。
        if (categoryId == null || categoryId < 1) {
            return ResultGenerator.genFailResult("缺少参数！");
        }
        // 根据categoryId检索分类。
        GoodsCategory category = newBeeMallCategoryService.getGoodsCategoryById(categoryId);
        // 检查分类及其级别。
        if (category == null || category.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        // 创建一个Map以存储与分类相关的结果。
        Map categoryResult = new HashMap(4);
        // 处理不同的分类级别。
        if (category.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel()) {
            List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                categoryResult.put("secondLevelCategories", secondLevelCategories);
                categoryResult.put("thirdLevelCategories", thirdLevelCategories);
            }
        }
        if (category.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel()) {
            List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(categoryId), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
            categoryResult.put("thirdLevelCategories", thirdLevelCategories);
        }
        // 返回结果。
        return ResultGenerator.genSuccessResult(categoryResult);
    }

    // 用于添加新分类的端点。
    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    @ApiOperation(value = "新增分类", notes = "新增分类")
    public Result save(@RequestBody @Valid GoodsCategoryAddParam goodsCategoryAddParam, @TokenToAdminUser AdminUserToken adminUser) {
        // 记录adminUser信息。
        logger.info("adminUser:{}", adminUser.toString());
        // 创建GoodsCategory对象并复制属性。
        GoodsCategory goodsCategory = new GoodsCategory();
        BeanUtil.copyProperties(goodsCategoryAddParam, goodsCategory);
        // 调用服务保存分类并返回结果。
        String result = newBeeMallCategoryService.saveCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    // 用于更新分类信息的端点。
    @RequestMapping(value = "/categories", method = RequestMethod.PUT)
    @ApiOperation(value = "修改分类信息", notes = "修改分类信息")
    public Result update(@RequestBody @Valid GoodsCategoryEditParam goodsCategoryEditParam, @TokenToAdminUser AdminUserToken adminUser) {
        // 记录adminUser信息。
        logger.info("adminUser:{}", adminUser.toString());
        // 创建GoodsCategory对象并复制属性。
        GoodsCategory goodsCategory = new GoodsCategory();
        BeanUtil.copyProperties(goodsCategoryEditParam, goodsCategory);
        // 调用服务更新分类并返回结果。
        String result = newBeeMallCategoryService.updateGoodsCategory(goodsCategory);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    // 用于获取特定分类详细信息的端点。
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单条分类信息", notes = "根据id查询")
    public Result info(@PathVariable("id") Long id, @TokenToAdminUser AdminUserToken adminUser) {
        // 记录adminUser信息。
        logger.info("adminUser:{}", adminUser.toString());
        // 调用服务根据ID获取分类详细信息并返回结果。
        GoodsCategory goodsCategory = newBeeMallCategoryService.getGoodsCategoryById(id);
        if (goodsCategory == null) {
            return ResultGenerator.genFailResult("未查询到数据");
        }
        return ResultGenerator.genSuccessResult(goodsCategory);
    }

    // 用于批量删除分类的端点。
    @RequestMapping(value = "/categories", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除分类信息", notes = "批量删除分类信息")
    public Result delete(@RequestBody BatchIdParam batchIdParam, @TokenToAdminUser AdminUserToken adminUser) {
        // 记录adminUser信息。
        logger.info("adminUser:{}", adminUser.toString());
        // 参数验证。
        if (batchIdParam == null || batchIdParam.getIds().length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        // 调用服务批量删除分类并返回结果。
        if (newBeeMallCategoryService.deleteBatch(batchIdParam.getIds())) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
}
