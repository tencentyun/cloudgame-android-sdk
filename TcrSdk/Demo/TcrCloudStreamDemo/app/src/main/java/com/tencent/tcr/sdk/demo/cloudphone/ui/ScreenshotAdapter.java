package com.tencent.tcr.sdk.demo.cloudphone.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.tencent.tcr.sdk.demo.cloudphone.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ViewHolder> {

    private List<ScreenshotItem> screenshotItems;

    public ScreenshotAdapter(List<ScreenshotItem> screenshotItems) {
        this.screenshotItems = screenshotItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_screenshot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScreenshotItem item = screenshotItems.get(position);
        holder.instanceIdText.setText(item.getInstanceId());
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(holder.screenshotImage.getDrawable())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.screenshotImage);
    }

    @Override
    public int getItemCount() {
        return screenshotItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView screenshotImage;
        TextView instanceIdText;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            screenshotImage = itemView.findViewById(R.id.screenshotImage);
            instanceIdText = itemView.findViewById(R.id.instanceIdText);
            checkbox = itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), PlayActivity.class);
                intent.putExtra("instanceIds", new ArrayList<>(Collections.singletonList(instanceIdText.getText().toString())));
                intent.putExtra("isGroupControl", false);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}