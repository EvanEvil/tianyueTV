package com.tianyue.tv.Config;

/**
 * 实名认证审核状态
 * Created by hasee on 2016/11/23.
 */
public class AuditStateConfig {
    /**
     * 审核成功
     */
    public static final int AUDIT_SUCCESS = 1;
    /**
     * 审核中
     */
    public static final int AUDITING = 2;
    /**
     * 审核未通过
     */
    public static final int AUDIT_FAILURE = 0;
    /**
     * 直播延续
     */
    public static final int AUDIT_CONTINUE = 3;
}
