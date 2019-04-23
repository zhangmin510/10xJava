package name.zhangmin.boot;

import name.zhangmin.boot.model.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;

import java.io.IOException;
import java.sql.Connection;

/**
 * @author zhangmin.name
 * @date 2019/4/13
 */
public class MyBatisExample {
    public static void main(String[] args) throws Exception {
        String resource = "mybatis-config.xml";
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
                .build(Resources.getResourceAsStream(resource));
        SqlSession session = sqlSessionFactory.openSession();
        User u = session.selectOne("name.zhangmin.boot.dao.UserDao.selectByPrimaryKey", 1);
        System.out.println("query user: " + u.getPhone());
    }
}
