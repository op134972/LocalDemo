package lc.q2;

/**
 * @Author: tangwenchuan
 * @Date: 2020-06-06 23:27
 *
 * 给定一个仅包含大小写字母和空格 ' ' 的字符串 s，返回其最后一个单词的长度。如果字符串从左向右滚动显示，那么最后一个单词就是最后出现的单词。
 *
 * 如果不存在最后一个单词，请返回 0 。
 *
 * 说明：一个单词是指仅由字母组成、不包含任何空格字符的 最大子字符串。
 *
 *  
 *
 * 示例:
 *
 * 输入: "Hello World"
 * 输出: 5
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/length-of-last-word
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class q58_ac {

    public int lengthOfLastWord(String s) {

        if (s == null || s.length() == 0) {
            return 0;
        }
        s = s.trim();

        int i = s.lastIndexOf(" ");

        return s.length() - i - 1;
    }

    public static void main(String[] args) {
        System.out.println(new q58_ac().lengthOfLastWord("hello world "));
    }
}
