package com.lixn.login.POJO;

/**
 * @创建人 lixiangnan
 * @创建时间 2019/9/19
 * @描述
 */
public class CurrentUser {
    private static final ThreadLocal<UserBO> currentUser = new ThreadLocal<>();

    public static void put(UserBO userBO) {
        currentUser.set(userBO);
    }

    public static UserBO get() {
        return currentUser.get();
    }
}
