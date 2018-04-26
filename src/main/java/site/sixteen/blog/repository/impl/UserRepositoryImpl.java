package site.sixteen.blog.repository.impl;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import site.sixteen.blog.entity.User;
import site.sixteen.blog.repository.UserExtraRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public class UserRepositoryImpl implements UserExtraRepository {
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<User> findActiveUsers(int num) {
        String sql = "SELECT u.username,u.nickname,u.face FROM user u LEFT OUTER JOIN ( SELECT a.user_id AS userId, sum( a.read_count + a.comment_count + a.vote_count ) AS liveness FROM article a GROUP BY a.user_id ) ua ON ua.userId = u.id ORDER BY ua.liveness DESC limit ?1";
        Query query= entityManager.createNativeQuery(sql);
        query.setParameter(1,num);
        List<User> list=query.unwrap(SQLQuery.class)
                .setResultTransformer(
                        Transformers.aliasToBean(User.class)
                )
                .list();
        entityManager.close();
        return list;
    }
}
