package com.tianyue.tv.Config;

/**
 *
 * 接口请求地址
 * Created by hasee on 2016/10/27.
 */
public class InterfaceUrl {
    /**
     * 登陆接口 请求方式:post
     * 必传参数
     * userName 账号
     *
     * password 密码
     */
    public final static String MOBILE_LOGIN = "http://www.tianyue.tv/mobileLogin";
    //public final static String MOBILE_LOGIN = "http://192.168.0.88:8081/mobileLogin";

    /**
     * 注册获取验证码 请求方式:post
     * 必传参数
     * telephone 电话号码
     */
    public final static String GET_CODE = "http://www.tianyue.tv/getPhoneCodemobile";

    /**
     * 注册用户 请求方式:post
     * 必传参数
     * telephone 电话号码  nickName 昵称  code 验证码   password 密码
     */
    public final static String REGISTER_USER = "http://www.tianyue.tv/mobileSendRegister";

    /**
     * 废弃接口
     * 获取文章列表 请求方式get
     * 必传参数
     *  pageNo    当前页（传0默认第1页）ssz
     *
     *  pageSize    当前每页显示条数（传0默认10条）
     */

    public final static String DISCOVERY = "http://www.tianyue.tv/mobileNewsList";

    /**
     *
     * 废弃接口
     * 获取直播列表 请求方式get
     * 必传参数
     *  pageNo    当前页（传0默认第1页）
     *
     *  pageSize    当前每页显示条数（传0默认10条）
     */
    public final static String LIVE_LIST = "http://192.168.0.88:8080/mobileAllBroadcastLiving";

    /**
     * 修改密码获取验证码 请求方式post
     * 必传参数
     *  telephone     电话号码
     *
     */
    public final static String ALTER_PWD_CODE ="http://www.tianyue.tv/getPxgaiCodemobile" ;
    /**
     * 必传参数：
     * telephone 电话号码 code 验证码 password 密码 nowpassword 重复密码
     */
    public final static String ALTER_PWD = "http://www.tianyue.tv/findPasswordmobile" ;
    /**
     * 必传参数：
     String userName //真实姓名              必传
     String identityCard //身份证号          必传
     String cardImage //身份证照片           必传
     Integer brandType //个人或者 品牌        必传
     String   user_id   //user_id              必传
     */
    public final static String CERTIFICATION = "http://www.tianyue.tv/identifyAjaxapp";
    /**
     * 获取所有直播间
     * 传参：无 如果需要分页加上 tyid  分类表 TypeId
     *
     */
    public final static String ALL_BROADCAST_LIVE = "http://www.tianyue.tv/mobileAllBroadcastLiving";
//    public final static String ALL_BROADCAST_LIVE = "http://192.168.0.88:8081/mobileAllBroadcastLiving";
    /**
     * 获取用户信息
     * 传参：用户uId  post
     *
     */
    public final static String GET_USER_INFO = "http://www.tianyue.tv/mobileUserCenter";
    /**
     * 修改用户信息
     * 传参： 用户userId  用户头像headUrl     post
     */
    public final static String ALTER_USER_INFO = "http://192.168.0.88:8081/update_headUrl";
    /**
     * 查询直播间信息
     * 传参： 用户uId    post
     */
    public final static String QUERY_BUCKET = "http://www.tianyue.tv/broadcast_app";
    /**
     *
     * 申请直播间
     * 传参： Name 房间名   typeName（6接口二级的typeName） user_id 用户id
     * 返回值：fails 已有直播间  fail 用户不存在
     * success 成功    currUser ：user对象
     */
    public final static String APPLY_FOR_BUCKET = "http://www.tianyue.tv/insertRoommobile";
    /**
     *
     * 修改直播间
     * 传参：id （user_id）必传
     * name  可选  不填就默认原本的传
     * typeName  可选  不填就默认原本的传
     * keyWord   可选  不填就默认原本的传
     * 返回值：success   成功
     * error     发生错误了
     */
    public final static String ALTER_BUCKET = "http://www.tianyue.tv/updateZbj_app";

    /**
     * 发现模块，搜索接口
     * 传参：name 必传
     */
    public final static String SEARCH_INFO  = "http://www.tianyue.tv/Querycorrespondence";
    public final static String TEST  = "http://www.baidu.com";
}
