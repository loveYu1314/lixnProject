package com.lixn.login.service;

import com.lixn.login.POJO.UserBO;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/19
 * @描述
 */
public interface UserService {

    String buildUserInfo(UserBO userBO);

    void logout(String jwt);
}
