package com.same.part.assistant.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.same.part.assistant.helper.PayHelper;
import com.same.part.assistant.helper.PayResultViewModel;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import me.hgj.jetpackmvvm.base.BaseApp;
import me.hgj.jetpackmvvm.network.AppException;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, PayHelper.Companion.getWXPAY_APP_ID());
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
		Log.d(TAG,"onPayFinish, errCode = " + resp.errCode);
		ToastUtils.showShort("onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            PayResultViewModel viewModel = ((BaseApp) Utils.getApp()).getAppViewModelProvider().get(PayResultViewModel.class);
            if (resp.errCode == 0) {
                viewModel.onPaySuccess();
            } else if (resp.errCode==-2){
                viewModel.onPayError(new AppException("-2", "取消支付", ""));
            }else {
                viewModel.onPayError(new AppException(String.valueOf(resp.errCode), resp.errStr, ""));
            }
        }
        finish();
    }
}