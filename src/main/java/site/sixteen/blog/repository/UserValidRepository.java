package site.sixteen.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.sixteen.blog.entity.UserValid;

import java.util.Date;
import java.util.List;

/**
 * @author panhainan@yeah.net(@link http://sixteen.site)
 **/
public interface UserValidRepository extends JpaRepository<UserValid,Long> {


    List<UserValid> findAllByUsernameAndValidSubjectAndAndCodeTypeAndEndTimeGreaterThanEqual(String username, String validSubject, int codeType, Date endTime);
}
