package com.github.paicoding.forum.test.demo;

import com.github.paicoding.forum.api.model.vo.article.ArticlePostReq;
import com.github.paicoding.forum.core.util.NumUtil;
import com.github.paicoding.forum.service.article.conveter.ArticleConverter;
import com.github.paicoding.forum.service.article.repository.entity.ArticleDO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: zhujun
 * @description: TODO
 * @date: 2024/3/2 20:16
 */
public class Transaction {
    public static void main(String[] args) {

    }

//    @Transactional
//    public Long saveArticle(ArticlePostReq req,Long author) throws Exception{
//        ArticleDO articleDO = ArticleConverter.toArticleDo(req,author);
//        Long ans;
//        if(NumUtil.nullOrZero(req.getArticleId())){
//            ans = insertArticle(articleDO, ans);
//        }else {
//            ans = update();
//        }
//        throw new Exception("异常");
//    }
}
