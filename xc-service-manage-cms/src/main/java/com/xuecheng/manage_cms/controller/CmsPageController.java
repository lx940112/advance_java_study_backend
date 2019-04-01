package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    PageService pageService;

    /**
     * 页面查询
     *
     * @param page
     * @param size
     * @param queryPageRequest
     * @return QueryResponseResult
     */
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
       /* // 暂时采用测试数据，测试接口是否可以正常运行
        QueryResult queryResult = new QueryResult();
        queryResult.setTotal(2);
        // 静态数据列表
        List list = new ArrayList<>();
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("测试页面");
        list.add(cmsPage);
        queryResult.setList(list);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return queryResponseResult;*/
        return pageService.findList(page, size, queryPageRequest);
    }

    /**
     * 新增页面
     *
     * @param cmsPage
     * @return CmsPageResult
     */
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }

    /**
     * 根据ID查询页面
     *
     * @param id
     * @return CmsPage
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.getPageById(id);
    }

    /**
     * 修改页面
     *
     * @param cmsPage
     * @param id
     * @return CmsPageResult
     */
    @Override
    @PutMapping("/update/{id}")//这里使用put方法，http 方法中put表示更新
    public CmsPageResult update(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return pageService.update(id, cmsPage);
    }

    /**
     * 通过ID删除页面
     *
     * @param id
     * @return ResponseResult
     */
    @Override
    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable("id") String id) {
        return pageService.delete(id);
    }

    /**
     * 查询站点列表信息
     * @return
     */
    @Override
    @GetMapping("getSiteList")
    public QueryResponseResult getSiteList() {
        return pageService.getSiteList();
    }
}
