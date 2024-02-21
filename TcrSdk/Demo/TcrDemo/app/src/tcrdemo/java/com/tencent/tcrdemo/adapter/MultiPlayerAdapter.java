package com.tencent.tcrdemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.tencent.tcr.sdk.api.TcrSession;
import com.tencent.tcrdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 多人互动云游场景: 用户Adapter，根据下发的数据更新ui显示
 */
public class MultiPlayerAdapter<T> extends BaseAdapter {

    public final List<T> data = new ArrayList<>();
    public final int itemLayoutId;
    public final int variableId;
    private TcrSession mSession;
    private String mHostUserID;
    private String mHandleUserID;

    public MultiPlayerAdapter(int itemLayoutId, int variableId) {
        this.itemLayoutId = itemLayoutId;
        this.variableId = variableId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewDataBinding binding;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemLayoutId, parent, false);
            holder.mute = binding.getRoot().findViewById(R.id.mute);
            holder.seatIndex = binding.getRoot().findViewById(R.id.seat_index);
            holder.userID = binding.getRoot().findViewById(R.id.user_id);
            binding.getRoot().setTag(holder);
        } else {
            binding = DataBindingUtil.getBinding(convertView);
            holder = (ViewHolder) binding.getRoot().getTag();
        }
        binding.setVariable(variableId, data.get(position));
        ViewHolder finalHolder = holder;
        holder.mute.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String userId = (String) finalHolder.userID.getText();
            int muteStatus = mHostUserID.equals(mHandleUserID) ? (isChecked ? 0 : 2) : (isChecked ? 1 : 2);
            mSession.setMicMute(userId, muteStatus, null);
        });

        return binding.getRoot();
    }

    public void updateData(List<T> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void setSession(TcrSession session, String hostUserID, String handlerUserId) {
        mSession = session;
        mHostUserID = hostUserID;
        mHandleUserID = handlerUserId;
    }

    private static class ViewHolder {
        CheckBox mute;
        TextView userID;
        TextView seatIndex;
    }
}
