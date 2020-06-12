package com.same.part.assistant.app.util;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

public class NumberInputUtil {

    /**
     * 限制EditText输入小数位数
     *
     * @param editText 相应的EditText
     * @param digits   限制的小数位数
     */
    public static void setPriceMode(final EditText editText, final int digits) {
//        设置输入类型为小数数字，允许十进制小数点提供分数值。
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
//        给EditText设置文本变动监听事件
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                如果文本包括"."，删除“.”后面超过2位后的数据
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > digits) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + digits + 1);
                        editText.setText(s);
                        editText.setSelection(s.length()); //光标移到最后
                    }
                }
//                未输入数字的情况下输入小数点时，个位数字自动补零
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
//                如果文本以"0"开头并且第二个字符不是"."，不允许继续输入
                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }
}
