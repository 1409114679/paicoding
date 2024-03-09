package com.github.paicoding.forum.web.front.home.helper;

import cn.hutool.core.date.StopWatch;
import com.github.paicoding.forum.api.model.context.ReqInfoContext;
import com.github.paicoding.forum.api.model.enums.ConfigTypeEnum;
import com.github.paicoding.forum.api.model.vo.PageListVo;
import com.github.paicoding.forum.api.model.vo.PageParam;
import com.github.paicoding.forum.api.model.vo.article.dto.ArticleDTO;
import com.github.paicoding.forum.api.model.vo.article.dto.CategoryDTO;
import com.github.paicoding.forum.api.model.vo.banner.dto.ConfigDTO;
import com.github.paicoding.forum.api.model.vo.recommend.CarouseDTO;
import com.github.paicoding.forum.api.model.vo.user.dto.UserStatisticInfoDTO;
import com.github.paicoding.forum.core.async.AsyncUtil;
import com.github.paicoding.forum.core.common.CommonConstants;
import com.github.paicoding.forum.service.article.service.ArticleReadService;
import com.github.paicoding.forum.service.article.service.CategoryService;
import com.github.paicoding.forum.service.config.service.ConfigService;
import com.github.paicoding.forum.service.sidebar.service.SidebarService;
import com.github.paicoding.forum.service.user.service.UserService;
import com.github.paicoding.forum.web.front.home.vo.IndexVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 首页推荐相关
 *
 * @author YiHui
 * @date 2022/9/6
 */
@Component
public class IndexRecommendHelper {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleReadService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private SidebarService sidebarService;

    @Autowired
    private ConfigService configService;

    public IndexVo buildIndexVo(String activeTab) {
        IndexVo vo = new IndexVo();
        CategoryDTO category = categories(activeTab, vo);
        vo.setCategoryId(category.getCategoryId());
        vo.setCurrentCategory(category.getCategory());
        // 并行调度实例，提高响应性能
        AsyncUtil.concurrentExecutor("首页响应")
                .runAsyncWithTimeRecord(() -> vo.setArticles(articleList(category.getCategoryId())), "文章列表")
                .runAsyncWithTimeRecord(() -> vo.setTopArticles(topArticleList(category)), "置顶文章")
                .runAsyncWithTimeRecord(() -> vo.setHomeCarouselList(homeCarouselList()), "轮播图")
                .runAsyncWithTimeRecord(() -> vo.setSideBarItems(sidebarService.queryHomeSidebarList()), "侧边栏")
                .runAsyncWithTimeRecord(() -> vo.setUser(loginInfo()), "用户信息")
                .allExecuted()
                .prettyPrint();

//        IndexVo vo = new IndexVo();
//        CategoryDTO category = categories(activeTab, vo);
//        StopWatch stopWatch = new StopWatch("首页信息返回");
//        stopWatch.start("类目文章获取");
//        vo.setArticles(articleList(category.getCategoryId()));
//        stopWatch.stop();
//        stopWatch.start("置顶文章获取");
//        vo.setTopArticles(topArticleList(category));
//        stopWatch.stop();
//        stopWatch.start("轮播图获取");
//        vo.setHomeCarouselList(homeCarouselList());
//        stopWatch.stop();
//        stopWatch.start("侧边栏获取");
//        vo.setSideBarItems(sidebarService.queryHomeSidebarList());
//        stopWatch.stop();
//
//        vo.setCurrentCategory(category.getCategory());
//        vo.setCategoryId(category.getCategoryId());
//        stopWatch.start("登录用户信息获取");
//        vo.setUser(loginInfo());
//        stopWatch.stop();
//        prettyPrint(stopWatch);

        return vo;
    }

    private void prettyPrint(StopWatch stopWatch) {
        StopWatch.TaskInfo[] taskInfos = stopWatch.getTaskInfo();
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append("StopWatch '").append(stopWatch.getId()).append("':running time = ").append(stopWatch.getTotalTimeMillis()).append("ms");
        sb.append('\n');
        if (taskInfos.length<1){
            sb.append("No task info kept");
        }else {
            sb.append("--------------------------------\n");
            sb.append("ms     %          Task name\n");
            sb.append("--------------------------------\n");
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMinimumFractionDigits(3);
            nf.setGroupingUsed(false);
            NumberFormat pf = NumberFormat.getPercentInstance();
            pf.setMinimumFractionDigits(2);
            pf.setMinimumIntegerDigits(2);
            pf.setGroupingUsed(false);
            for (StopWatch.TaskInfo task : taskInfos) {
                sb.append(nf.format(task.getTimeNanos() / 1000_000.0)).append("   ");
                sb.append(pf.format((double) task.getTimeNanos() / stopWatch.getTotalTimeNanos())).append("   ");
                sb.append(task.getTaskName()).append("\n");
            }
        }
        System.out.println(sb.toString());
    }

    public IndexVo buildSearchVo(String key) {
        IndexVo vo = new IndexVo();
        vo.setArticles(articleService.queryArticlesBySearchKey(key, PageParam.newPageInstance()));
        vo.setSideBarItems(sidebarService.queryHomeSidebarList());
        return vo;
    }

    /**
     * 轮播图
     *
     * @return
     */
    private List<CarouseDTO> homeCarouselList() {
        List<ConfigDTO> configList = configService.getConfigList(ConfigTypeEnum.HOME_PAGE);
        return configList.stream()
                .map(configDTO -> new CarouseDTO()
                        .setName(configDTO.getName())
                        .setImgUrl(configDTO.getBannerUrl())
                        .setActionUrl(configDTO.getJumpUrl()))
                .collect(Collectors.toList());
    }

    /**
     * 文章列表
     */
    private PageListVo<ArticleDTO> articleList(Long categoryId) {
        return articleService.queryArticlesByCategory(categoryId, PageParam.newPageInstance());
    }

    /**
     * 置顶top 文章列表
     */
    private List<ArticleDTO> topArticleList(CategoryDTO category) {
        List<ArticleDTO> topArticles = articleService.queryTopArticlesByCategory(category.getCategoryId() == 0 ? null : category.getCategoryId());
        if (topArticles.size() < PageParam.TOP_PAGE_SIZE) {
            // 当分类下文章数小于置顶数时，为了避免显示问题，直接不展示
            topArticles.clear();
            return topArticles;
        }

        // 查询分类对应的头图列表
        List<String> topPicList = CommonConstants.HOMEPAGE_TOP_PIC_MAP.getOrDefault(category.getCategory(),
                CommonConstants.HOMEPAGE_TOP_PIC_MAP.get(CommonConstants.CATEGORY_ALL));

        // 替换头图，下面做了一个数组越界的保护，避免当topPageSize数量变大，但是默认的cover图没有相应增大导致数组越界异常
        AtomicInteger index = new AtomicInteger(0);
        topArticles.forEach(s -> s.setCover(topPicList.get(index.getAndIncrement() % topPicList.size())));
        return topArticles;
    }

    /**
     * 返回分类列表
     *
     * @param active 选中的分类
     * @param vo     返回结果
     * @return 返回选中的分类；当没有匹配时，返回默认的全部分类
     */
    private CategoryDTO categories(String active, IndexVo vo) {
        List<CategoryDTO> allList = categoryService.loadAllCategories();
        // 查询所有分类的对应的文章数
        Map<Long, Long> articleCnt = articleService.queryArticleCountsByCategory();
        // 过滤掉文章数为0的分类
        allList.removeIf(c -> articleCnt.getOrDefault(c.getCategoryId(), 0L) <= 0L);

        // 刷新选中的分类
        AtomicReference<CategoryDTO> selectedArticle = new AtomicReference<>();
        allList.forEach(category -> {
            if (category.getCategory().equalsIgnoreCase(active)) {
                category.setSelected(true);
                selectedArticle.set(category);
            } else {
                category.setSelected(false);
            }
        });

        // 添加默认的全部分类
        allList.add(0, new CategoryDTO(0L, CategoryDTO.DEFAULT_TOTAL_CATEGORY));
        if (selectedArticle.get() == null) {
            selectedArticle.set(allList.get(0));
            allList.get(0).setSelected(true);
        }

        vo.setCategories(allList);
        return selectedArticle.get();
    }


    private UserStatisticInfoDTO loginInfo() {
        if (ReqInfoContext.getReqInfo() != null && ReqInfoContext.getReqInfo().getUserId() != null) {
            return userService.queryUserInfoWithStatistic(ReqInfoContext.getReqInfo().getUserId());
        }
        return null;
    }
}
