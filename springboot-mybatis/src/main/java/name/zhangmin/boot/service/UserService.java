package name.zhangmin.boot.service;


import name.zhangmin.boot.model.User;
/**
 * @author hzzhangmin15
 * @date 2018/07/05
 */
public interface UserService {
	/**
	 * add user
	 * @param user
	 * @return
	 */
    int addUser(User user);
    /**
     * get user 
     * @param userId
     * @return
     */
    User getUserById(Integer userId);
}
