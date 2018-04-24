package site.sixteen.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
