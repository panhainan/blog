package site.sixteen.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 通过用户id分页查找文章
     * @param userId
     * @param pageable
     * @return
     */
    Page<Article> findArticlesByUserIdOrderByCreateTimeDesc(long userId, Pageable pageable);


    /**
     * 通过文章状态查找对应分页文章（主要用来查找已发布状态文章）
     * @param status
     * @param pageable
     * @return
     */
    Page<Article> findArticlesByStatus(int status, Pageable pageable);

    /**
     * 通过用户ID和状态查找对应分页文章
     * @param userId
     * @param status
     * @param pageable
     * @return
     */
    Page<Article> findArticlesByUserIdAndStatus(Long userId, int status, Pageable pageable);

    /**
     * 通过类别ID和状态查找对应文章
     * @param categoryId
     * @param status
     * @return
     */
    List<Article> findArticlesByCategoryIdAndStatus(long categoryId, int status);

    /**
     * 通过用户ID和文章状态获取所有文章
     * @param userId
     * @param status
     * @return
     */
    List<Article> findArticlesByUserIdAndStatusOrderByCreateTimeDesc(long userId, int status);


    /**
     * 通过tag模糊查找
     * @param tagId
     * @param status
     * @param pageable
     * @return
     */
    Page<Article> findArticlesByTagIdStrLikeAndStatusOrderByCreateTimeDesc(String tagId,int status, Pageable pageable);
}


