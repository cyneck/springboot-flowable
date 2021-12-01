package com.cyneck.workflow.utils;

import com.cyneck.workflow.model.entity.BUserInfoEntity;

/**
 * 业务用户信息工具类
 *
 * @author Eric Lee
 * @version v1.0.0
 * @since 2021/2/18 18:20
 **/
public class BizSecurityUtil {

    private static ThreadLocal<BUserInfoEntity> userInfoThreadLocal=new ThreadLocal<>();

    private BizSecurityUtil() {
    }

    public static String getCurrentUserId() {
        BUserInfoEntity user = getCurrentUserObject();
        if (user != null) {
            return user.getUserId();
        }
        return null;
    }

    /**
     * 功能描述: 重写此方法
     *
     * @throws
     * @author: Eric Lee
     * @return: User
     * @date: 2021/02/9 17:15
     */
    public static BUserInfoEntity getCurrentUserObject() {
//        RemoteUser user = new RemoteUser();
//        user.setId("admin");
//        user.setDisplayName("admin");
//        user.setFirstName("admin");
//        user.setLastName("admin");
//        user.setEmail("admin@2021.com");
//        user.setPassword("");
//        List<String> pris = new ArrayList<>();
//        pris.add(DefaultPrivileges.ACCESS_MODELER);
//        pris.add(DefaultPrivileges.ACCESS_IDM);
//        pris.add(DefaultPrivileges.ACCESS_ADMIN);
//        pris.add(DefaultPrivileges.ACCESS_TASK);
//        pris.add(DefaultPrivileges.ACCESS_REST_API);
//        user.setPrivileges(pris);
        return userInfoThreadLocal.get();
    }

    public static void setMyUserInfo(BUserInfoEntity user) {
        if(user==null){
            return;
        }
        userInfoThreadLocal.set(user);
    }

}
