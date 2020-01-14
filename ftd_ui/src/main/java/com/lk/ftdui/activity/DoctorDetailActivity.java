package com.lk.ftdui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lk.ftd_core.task.FtdCore;
import com.lk.ftd_core.callback.FtdDoctorDetailCallback;
import com.lk.ftd_core.entity.DoctorDetailBean;
import com.lk.ftd_core.exception.FtdException;
import com.lk.ftdui.R;
import com.lk.ftdui.activity.config.ErrorDisplay;
import com.lk.ftdui.activity.param.DoctorInfo;
import com.lk.ftdui.widget.aboutRV.adapter.DoctorDetailAdapter;
import com.lk.ftdui.widget.aboutRV.decoration.StartScaleDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class DoctorDetailActivity extends BaseActivity {

    private Button btn;
    private SmartRefreshLayout refresh;
    private DoctorDetailAdapter adapter = new DoctorDetailAdapter();
    private DoctorDetailBean doctorDetailBean;
    private String memberCode;

    private FtdDoctorDetailCallback callback = new FtdDoctorDetailCallback() {

        @Override
        public void onSuccess(final DoctorDetailBean bean) {
            doctorDetailBean = bean;
            hideProgress();
            refresh.finishRefresh();
            adapter.updateData(bean);
        }

        @Override
        public void onError(final FtdException e) {
            hideProgress();
            refresh.finishRefresh();
            showToast(e.getMsg());
        }
    };

    public static void start(Context context, String memberCode) {
        Intent intent = new Intent(context, DoctorDetailActivity.class);
        intent.putExtra("memberCode", memberCode);
        context.startActivity(intent);
    }

    @Override
    public int setContentViewResId() {
        return R.layout.activity_doctor_detail;
    }

    @Override
    public void onCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.memberCode = getIntent().getStringExtra("memberCode");
        RecyclerView rv = findViewById(R.id.rv);
        rv.addItemDecoration(new StartScaleDecoration());
        rv.setAdapter(this.adapter);
        refresh = findViewById(R.id.refreshLayout);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                addTask(FtdCore.instance.getDoctorDetail(memberCode, true, callback));
            }
        });
        refresh.autoRefresh();
        TextView title = findViewById(R.id.tb_tv_title);
        title.setText("来康师详情");
        btn = findViewById(R.id.btn_go);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoctorInfo info = new DoctorInfo(doctorDetailBean.getMemberDTO().getMemberId(), doctorDetailBean.getMemberDTO().getMemberCode());
                FtdActivity.startWithThreePhoto(DoctorDetailActivity.this, info);
            }
        });
    }
}