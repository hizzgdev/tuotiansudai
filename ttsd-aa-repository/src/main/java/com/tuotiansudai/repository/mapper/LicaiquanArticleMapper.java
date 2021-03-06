package com.tuotiansudai.repository.mapper;

import com.tuotiansudai.repository.model.ArticleSectionType;
import com.tuotiansudai.repository.model.LicaiquanArticleModel;
import com.tuotiansudai.repository.model.SubArticleSectionType;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicaiquanArticleMapper {
    void createArticle(LicaiquanArticleModel licaiquanArticleModel);

    void deleteArticle(@Param("id") long id);

    void updateArticle(LicaiquanArticleModel licaiquanArticleModel);

    LicaiquanArticleModel findArticleById(@Param("id") long id);

    List<LicaiquanArticleModel> findExistedArticleListOrderByCreateTime(@Param("title") String title,
                                                                        @Param("section") ArticleSectionType sectionType,
                                                                        @Param("subSection")SubArticleSectionType subSectionType,
                                                                        @Param("index") long index,
                                                                        @Param("pageSize") int pageSize);
    List<LicaiquanArticleModel> findCarouselArticle();

    List<LicaiquanArticleModel> findArticleByArticleSectionType(@Param("section") ArticleSectionType section,
                                                                @Param("subSection") SubArticleSectionType subSection,
                                                                @Param("index") int index,
                                                                @Param("pageSize") int pageSize);

    int findCountArticleByArticleSectionType(@Param("section") ArticleSectionType section,
                                             @Param("subSection") SubArticleSectionType subSection);
}
