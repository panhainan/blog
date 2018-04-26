package site.sixteen.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.sixteen.blog.entity.Tag;

import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface TagRepository extends JpaRepository<Tag,Long> {


    /**
     * 判断当前tagName的tag是否存在并获取
     * @param tagName
     * @return
     */
    Tag findTagByName(String tagName);

    /**
     * 获取hot=true的热门标签
     * @param hot
     * @return
     */
    List<Tag> findTagsByHotOrderByIdDesc(boolean hot);
}
