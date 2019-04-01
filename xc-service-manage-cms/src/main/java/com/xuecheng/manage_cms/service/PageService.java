package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PageService {

    private final CmsPageRepository cmsPageRepository;

    @Autowired
    public PageService(CmsPageRepository cmsPageRepository) {
        this.cmsPageRepository = cmsPageRepository;
    }

    /*
     * 页面列表分页查询
     * @param page
     * @param size
     * @param queryPageRequest
     * @return QueryResponseResult
     */
    /*public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        if (page <= 0) {
            page = 1;
        }
        page = page - 1;//为了适应MongoDB的接口将页码减一
        if (size <= 0) {
            size = 20;
        }
        // 分页对象
        Pageable pageable = PageRequest.of(page, size);
        // 分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }*/


    /**
     * 页面列表分页查询
     *
     * @param page             当前页码
     * @param size             页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        //条件匹配器
        //页面名称模糊查询，需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值
        CmsPage cmsPage = new CmsPage();
        //站点ID
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //模板ID
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //页面别名
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //页码
        page = page - 1;
        //分页对象
        Pageable pageable = PageRequest.of(page, size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }

    /**
     * 新增页面
     *
     * @param cmsPage
     * @return CmsPageResult
     */
    public CmsPageResult add(CmsPage cmsPage) {
        // 校验页面名称、站点ID、页面webPath的唯一性
        // 根据页面名称、站点ID、页面webPath去查询cms_page集合，如果查到说明此页面已经存在，如果查询不到再继续添加
        CmsPage oldPage = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if(oldPage != null){
            //校验页面是否存在，已存在则抛出异常
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }


        if (oldPage == null) {
            cmsPage.setPageId(null);
            // 调用dao新增页面
            CmsPage newPage = cmsPageRepository.save(cmsPage);
            return new CmsPageResult(CommonCode.SUCCESS, newPage);
        }
        // 添加失败
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 根据ID查询页面
     *
     * @param id
     * @return CmsPage
     */
    public CmsPage getPageById(String id) {
        Optional<CmsPage> cmsPage = cmsPageRepository.findById(id);
        return cmsPage.orElse(null);
    }

    /**
     * 修改页面
     *
     * @param id
     * @param cmsPage
     * @return CmsPageResult
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //根据id查询页面信息
        CmsPage oldPage = getPageById(id);
        if (oldPage != null) {
            //更新模板id
            oldPage.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            oldPage.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            oldPage.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            oldPage.setPageName(cmsPage.getPageName());
            //更新访问路径
            oldPage.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            oldPage.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            CmsPage newPage = cmsPageRepository.save(oldPage);
            // 返回成功
            return new CmsPageResult(CommonCode.SUCCESS, newPage);
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 通过ID删除页面
     *
     * @param id
     * @return ResponseResult
     */
    public ResponseResult delete(String id) {
        CmsPage oldPage = getPageById(id);
        if (oldPage != null) {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    public QueryResponseResult getSiteList() {
        List<CmsPage> all = cmsPageRepository.findAll();
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        cmsPageQueryResult.setList(all);
        cmsPageQueryResult.setTotal(all.size());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }
}
