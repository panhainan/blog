package site.sixteen.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.sixteen.blog.entity.User;

import javax.persistence.ColumnResult;
import javax.persistence.EntityResult;
import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface UserRepository extends JpaRepository<User,Long>,UserExtraRepository {
    /**
     * 通过username(唯一)查找用户
     * @param username
     * @return
     */
    User findUserByUsername(String username);

    /**
     * 根据邮箱号获取用户（邮箱号唯一对应）
     * @param email
     * @return
     */
    User findUserByEmail(String email);


}
