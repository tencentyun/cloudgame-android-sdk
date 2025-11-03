package com.tencent.tcr.demo.cloudphone.common.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.tencent.tcr.demo.cloudphone.R;
import java.util.List;

public class InstanceListAdapter extends RecyclerView.Adapter<InstanceListAdapter.ViewHolder> {

    private List<InstanceListItem> instanceListItems;
    private OnItemCheckedChangeListener onItemCheckedChangeListener;

    public interface OnItemCheckedChangeListener {

        void onItemCheckedChanged();
    }

    public InstanceListAdapter(List<InstanceListItem> instanceListItems) {
        this.instanceListItems = instanceListItems;
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        this.onItemCheckedChangeListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_screenshot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InstanceListItem item = instanceListItems.get(position);
        holder.instanceIdText.setText(item.instanceId);
        holder.checkbox.setChecked(item.isSelected);
        // 先设置占位符或清空图片，再加载新图片
        holder.screenshotImage.setImageResource(0);
        // 新方案下没有可用的图片 url 了，先注释掉，避免 Glide 一直打印失败日志
//        Glide.with(holder.itemView.getContext())
//                .load(item.imageUrl)
//                .placeholder(holder.screenshotImage.getDrawable())
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .into(holder.screenshotImage);
    }

    @Override
    public int getItemCount() {
        return instanceListItems.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        try {
            Glide.with(holder.itemView).clear(holder.screenshotImage);
        } catch (IllegalArgumentException e) {
            // 忽略。 此时Activity已销毁，Glide会自动清理资源
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView screenshotImage;
        TextView instanceIdText;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            screenshotImage = itemView.findViewById(R.id.screenshotImage);
            instanceIdText = itemView.findViewById(R.id.instanceIdText);
            checkbox = itemView.findViewById(R.id.checkbox);
            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                InstanceListItem item = instanceListItems.get(getBindingAdapterPosition());
                item.isSelected = isChecked;

                if (onItemCheckedChangeListener != null) {
                    onItemCheckedChangeListener.onItemCheckedChanged();
                }
            });
        }
    }
}