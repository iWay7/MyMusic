package site.iway.mymusic.net.res.base;

/**
 * Created by iWay on 2017/7/18.
 */

public class BasicRes extends RPCRes {

    public static final int UNKNOWN = -1;
    public static final String UNKNOWN_STRING = "结果未知";
    public static final int OK = 0;
    public static final String OK_STRING = "成功";
    public static final int FAIL = 1;
    public static final String FAIL_STRING = "失败";

    public int resultCode = UNKNOWN;
    public String resultMessage = UNKNOWN_STRING;

}
