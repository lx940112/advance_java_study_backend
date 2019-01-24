package com.xuecheng.manage_cms;

import com.alibaba.fastjson.JSONObject;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindPage() {
        // 分页参数
        int page = 0; // 从0开始
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        String s = JSONObject.toJSONString(all);
        System.out.println(s);
    }

    @Test
    public void testInsert() {
        // 定义实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("S01");
        cmsPage.setTemplateId("T01");
        cmsPage.setPageName("测试—insert—页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }

    @Test
    public void testUpdate() {
//        Optional<CmsPage> optional = cmsPageRepository.findOne("5c189f08254476f790a849cb");
        Optional<CmsPage> optional = cmsPageRepository.findById("5c189f08254476f790a849cb");
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            cmsPage.setPageName("测试—Update—页面");
            cmsPageRepository.save(cmsPage);
        }


    }

    @Test
    public void testCountBy() {
        int i = cmsPageRepository.countBySiteIdAndPageType("5a751fab6abb5044e0d19ea1", "0");
        System.out.println(i);

    }

    @Test
    public void testFindBy() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CmsPage> cmsPages = cmsPageRepository.findBySiteIdAndPageType("5a751fab6abb5044e0d19ea1", "0", pageable);
        String s = JSONObject.toJSONString(cmsPages);
        System.out.println(s);
    }

    @Test
    public void testDelete() {
        cmsPageRepository.deleteById("5c189f08254476f790a849cb");
    }


}
