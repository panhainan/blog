package site.sixteen.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.sixteen.blog.entity.Comment;

import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface CommentRepository extends JpaRepository<Comment,Long> {


    /**
     * 获取文章id对应的评论
     * @param articleId
     * @return
     */
    List<Comment> findAllByArticleIdOrderByCreateTime(Long articleId);
    /**
     * 获取文章id对应的评论
     * @param articleIds
     * @param pageable
     * @return
     */
    @Query(value = "select * from comment where article_id in (:articleIds) order by create_time desc,?#{#pageable}",
            countQuery = "select count(*) from comment where article_id in (:articleIds)",
            nativeQuery = true)
    Page<Comment> findCommentsByArticleIdIn(@Param("articleIds")List<Long> articleIds, Pageable pageable);

    /**
     * 通过用户ID获取他的评论消息
     * @param userId
     * @param pageable
     * @return
     */
    Page<Comment> findCommentsByUserIdOrderByCreateTimeDesc(Long userId,Pageable pageable);

    /**
     * 删除对应文章ID下的所有评论
     * @param articleId
     */
    void deleteCommentsByArticleId(Long articleId);
}
