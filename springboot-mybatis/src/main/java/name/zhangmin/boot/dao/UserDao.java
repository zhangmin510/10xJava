package name.zhangmin.boot.dao;


import java.util.List;

import name.zhangmin.boot.model.User;
/**
 * @author hzzhangmin15
 * @date 2018/07/05
 */
public interface UserDao {
	/**
	 * delete user by id
	 * @param userId
	 * @return
	 */
    int deleteByPrimaryKey(Integer userId);

    /**
     * insert user
     * @param record
     * @return
     */
    int insert(User record);

    /**
     * insert user 
     * @param record
     * @return
     */
    int insertSelective(User record);

    /**
     * select user by id 
     * @param userId
     * @return
     */
    User selectByPrimaryKey(Integer userId);

    /**
     * update user 
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * update user
     * @param record
     * @return
     */
    int updateByPrimaryKey(User record);

    /**
     * select all user
     * @return
     */
    List<User> selectAllUser();
}