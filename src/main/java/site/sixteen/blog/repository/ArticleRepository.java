package site.sixteen.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.sixteen.blog.entity.Article;

import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface ArticleRepository extends JpaRepository<Article,Long> {
    /**
     * 通过类别查找文章
     * @param categoryId
     * @return
     */
    List<Article> findArticlesByCategoryId(Long categoryId);
}
