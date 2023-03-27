package koumakan.javaweb.community.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package: koumakan.javaweb.community.util
 * @Author: Alice Maetra
 * @Date: 2023/3/27 13:47
 * @Decription:
 */
@Component
public class SensitiveFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    private final static String REPLACEMENT = "***";

    private Tree root = new Tree();

    @PostConstruct
    public void init() {
        // 方法直接返回编译之后/target/classes/目录，而resources中的内容也会一并加入
        // 注意：需要手动通过maven构件工具clean再重新编译 compile
        try (
                // 字节流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                // 字符流 -> 缓冲流 效率更高
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            while((keyword = reader.readLine()) != null) {
                // 加入前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            LOGGER.error("加载敏感词文件失败：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * 过滤敏感词
     * @param text 可能有敏感词的文本
     * @return 被过滤的文本
     */
    public String filter(String text) {
        if(StringUtils.isBlank(text) == true) return text;

        Tree ptr = root;
        int begin = 0;
        int end = 0;
        StringBuilder ans = new StringBuilder("");

        while(begin < text.length()) {
            if(end < text.length()) {
                char c = text.charAt(end);
                // 跳过符号
                if(isSymbol(c)) {
                    // 如果字典树的遍历指针处于根节点，则意味着当前字符并没有违规字符，可以加入最终结果。
                    if(ptr == root) {
                        ans.append(c);
                        begin++;
                    }
                    end++;
                    continue;
                }

                // 检查节点
                ptr = ptr.next.get(c);
                if(ptr == null) {
                    // 当前字符[begin, end]不属于违禁字符。
                    ans.append(text.charAt(begin));
                    end = ++begin;
                    ptr = root;
                }else if(ptr.isEnd == true) {
                    // 当前字符[begin, end]是违禁字符，进行替换。
                    ans.append(REPLACEMENT);
                    begin = ++end;
                    ptr = root;
                }else {
                    end++;
                }
            }else{
                ans.append(text.charAt(begin));
                end = ++begin;
                ptr = root;
            }
        }

        return ans.toString();
    }

    /**
     * 判断是否为符号
     * @param c  [0x2E80, 0x9FFF] 是东亚文字的范围
     * @return
     *
     */
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    private void addKeyword(String w) {
        Tree ptr = root;
        for(int i = 0; i < w.length(); ++i) {
            if(ptr.next.containsKey(w.charAt(i)) == false) {
                ptr.next.put(w.charAt(i), new Tree());
            }
            ptr = ptr.next.get(w.charAt(i));
        }
        ptr.isEnd = true;
    }





    // pre fit tree
    private class Tree{
        private boolean isEnd = false;

        private Map<Character, Tree> next = new HashMap<>();

        public boolean isEnd() {
            return isEnd;
        }
    }
}
