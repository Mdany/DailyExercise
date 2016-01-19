package cn.yuguo.mydoctor.utils;

import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringsUtils {

    /**
     * 设置正则表达式来匹配手机号
     * 长度：11位
     * 现有字段：130~139,150~153,155~159,180~189,170,176~178;
     * 抱歉。。不会简写的正则
     */
    private final static String regExp = "^[1]([3][0-9]|[4][0-9]|[5][0-3]|[5][5-9]|70|74|[7][6-8]|[8][0-9])[0-9]{8}$";

    /**
     * 是否含有中文
     *
     * @param str
     * @return
     */
    public static boolean isHasChinese(String str) {
        char[] chars = str.toCharArray();
        for (char c : chars) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将第一字符串的长度缩减到与第二个相同
     * 中文字符长度为2
     * 英文及其他字符长度为1
     */
    public static String reduceFirstLength(String str1, String str2) {
        if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str1)) {
            int length2 = getStrLength(str2);
            return reduceFirstLength(str1, length2);

        }
        return "";
    }

    /**
     * 将第一字符串的长度缩减到与第二个相同
     * 中文字符长度为2
     * 英文及其他字符长度为1
     */
    public static String reduceFirstLength(String str1, int length2) {
        if (!TextUtils.isEmpty(str1) && length2 >= 0) {
            char[] chars1 = str1.toCharArray();
            int length1 = 0;

            int len = 0;
            boolean need = false;
            for (char c : chars1) {
                Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
                if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                        || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                        || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                        || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                        || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                    if (length1 + 2 > length2) {
                        need = true;
                        break;
                    } else {
                        length1 += 2;
                        len++;
                    }
                } else {
                    if (length1 + 1 > length2) {
                        need = true;
                        break;
                    } else {
                        length1++;
                        len++;
                    }
                }
            }
            if (need && len >= 1) {
                if (length1 == length2) {
                    return str1.substring(0, len - 2) + "...";
                } else {
                    return str1.substring(0, len - 1) + "...";
                }
            } else {
                return str1.substring(0, len);
            }
        }
        return "";
    }

    /**
     * 得到文字的计算长度
     * 中文字符长度为2
     * 英文及其他字符长度为1
     *
     * @return
     */
    public static int getStrLength(String str) {
        char[] chars = str.toCharArray();
        int length = 0;
        for (char c : chars) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
            if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                    || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                    || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                    || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                length += 2;
            } else {
                length++;
            }
        }
        return length;
    }

    /**
     * 验证手机号是否合法
     */

    public static boolean verifyPhoneNum(String Phone) {
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(Phone);
        return matcher.find();
    }

    /**
     * 得到特殊颜色的文本
     *
     * @param boldText
     * @param allText
     * @param color
     * @return
     */
    public static SpannableStringBuilder getBoldText(String boldText, String allText, int color) {
        StringBuilder special = new StringBuilder(boldText);
        int len = special.length();
        special.append(allText);
        SpannableStringBuilder specialStyle = new SpannableStringBuilder(special);
        specialStyle.setSpan(new ForegroundColorSpan(color), 0, len, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return specialStyle;
    }

    /**
     * 过滤空格
     */
    public static void inputFilterSpace(final EditText edit) {
        edit.setFilters(new InputFilter[]
                {
                        new InputFilter() {
                            public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
                                if (src.length() < 1) {
                                    return null;
                                } else {
                                    char temp[] = (src.toString()).toCharArray();
                                    char result[] = new char[temp.length];
                                    for (int i = 0, j = 0; i < temp.length; i++) {
                                        if (temp[i] != ' ')
                                            result[j++] = temp[i];
                                    }
                                    return String.valueOf(result).trim();
                                }

                            }
                        }
                });
    }

    /**
     * 得到转码后的数据
     */
    public static String getEncodeStr(String str) {
        try {
            str = new String(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        str = URLEncoder.encode(str);
        return str;
    }

    /**
     * 第一个汉字转拼音  王 to WANG return W
     *
     * @param str 王
     * @return W
     */
    public static String getPinYinFirstLetter(String str) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);//大写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);//无声调
        format.setVCharType(HanyuPinyinVCharType.WITH_V);//v调输出方式伟原样
        try {
            if (!TextUtils.isEmpty(str)) {
                char firstChar = str.charAt(0);
                if (Character.toString(firstChar).matches("[\\u4E00-\\u9FA5]+") || Character.toString(firstChar).matches("[\\u3007]"))
                    return PinyinHelper.toHanyuPinyinStringArray(str.charAt(0), format)[0].charAt(0) + "";
                else if (firstChar >= 'a' && firstChar <= 'z')
                    return String.valueOf(firstChar).toUpperCase();
                else
                    return String.valueOf(firstChar);
            } else {
                return "";
            }
        } catch (Exception ex) {
            //badHanyuPinyinOutputFormatCombination.printStackTrace();
            return "";
        }
    }

    /**
     * 得到InputStream
     *
     * @param inputStream
     * @return
     */
    public static String getStrFromInputStream(InputStream inputStream, String charsetName) {
        StringBuffer sb = new StringBuffer();
        if (inputStream != null) {
            byte[] b = new byte[4096];
            int len = -1;
            try {
                while ((len = inputStream.read(b)) != -1) {
                    sb.append(new String(b, 0, len, charsetName));
                }
            } catch (Exception e) {
                sb = new StringBuffer();
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}

