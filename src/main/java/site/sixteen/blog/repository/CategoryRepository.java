package site.sixteen.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.sixteen.blog.entity.Article;
import site.sixteen.blog.entity.Category;

import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface CategoryRepository extends JpaRepository<Category,Long> {

    /**
     * 通过用户ID获取其所有的文章类别
     * @param userId
     * @return
     */
    List<Category> findAllByUserIdOrderByIdDesc(long userId);


    /**
     * 通过用户ID和对于文章类别ID获取类别
     * @param id
     * @param userId
     * @return
     */
    Category findCategoryByIdAndUserId(long id,long userId);

    /**
     *  通过用户ID和对于文章类别名称判断类别是否存在
     * @param name
     * @param userId
     * @return
     */
    boolean existsByNameAndUserId(String name, Long userId);

    /**
     * 通过类别名称和用户ID查找类别
     * @param name
     * @param userId
     * @return
     */
    Category findCategoryByNameAndUserId(String name,Long userId);

}
