package site.sixteen.blog.repository;

import org.springframework.data.jpa.repository.Query;
import site.sixteen.blog.entity.User;

import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface UserExtraRepository {
    /**
     * 获取指定数量的活跃用户
     * @param num
     * @return
     */
    List<User> findActiveUsers(int num);
}
