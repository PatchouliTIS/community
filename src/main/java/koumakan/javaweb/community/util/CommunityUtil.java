package koumakan.javaweb.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * @Package: koumakan.javaweb.community.util
 * @Author: Alice Maetra
 * @Date: 2023/3/21 15:17
 * @Decription:
 *      // 对随机生成的用户唯一标识符UUID进行加密后存储到数据库中
 */
public class CommunityUtil {

    /**
     * // 生成随机字符串
     * @return 随机生成的UUID字符串
     */
    public  static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * MD5 加密
     * @param key
     * @return
     */
    public static String md5(String key) {
        if(StringUtils.isBlank(key)) {
            return null;
        }

        return DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
    }


    /**
     * 使用JSON数据格式进行前后端交互。前端使用jQuery
     * @param code
     * @param msg
     * @param map
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);

        if (map != null) {
            for(String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();
    }

    // 重载
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
}
