package site.iway.mymusic.protocol.res;

import java.io.Serializable;

public class RPCRes implements Serializable {

    public static final int UNKNOWN = -1;
    public static final String UNKNOWN_STRING = "结果未知";
    public static final int OK = 0;
    public static final String OK_STRING = "成功";
    public static final int FAIL = 1;
    public static final String FAIL_STRING = "失败";

    public int resultCode = UNKNOWN;
    public String resultMessage = UNKNOWN_STRING;

}
