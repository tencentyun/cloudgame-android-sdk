package com.example.demop.view;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.demop.R;

/**
 * 该类是自定义悬浮按钮
 */
public class FloatingSettingBarView {

    private static final String TAG = "FloatingSettingBarView";

    private LeftSideSetting leftSide;

    public FloatingSettingBarView(View parent) {
        View leftSideContainer = parent.findViewById(R.id.floating_setting_left);
        leftSide = new LeftSideSetting(leftSideContainer);
    }

    public void setViewShow(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        leftSide.getRootView().setVisibility(visibility);
    }

    /**
     * 设置事件响应
     */
    public void setEventListener(SettingEventListener settingEventListener) {
        leftSide.setEventListener(settingEventListener);
    }

    public void setNetworkInfo(int rtt) {
        leftSide.setNetworkInfo(rtt);
    }

    public void setFps(float fps) {
        leftSide.setFps(fps);
    }


    public interface SettingEventListener {

        /**
         * 点击记载道本地
         */
        void onClickDebug();

        void onExit();
    }

    /**
     * 左侧悬浮设置
     */
    private static class LeftSideSetting {

        private final View arrowHolder;
        private final ImageView arrow;
        private final View splitLine;
        private final View splitLine2;
        private final View splitLine5;
        private final View interactiveGameHolder;
        private final View exitHolder;
        private final ImageView interactiveGameIcon;
        private final ImageView exitIcon;
        private final TextView interactiveGameText;
        private final TextView exitText;
        private final View rightPadding;
        private final View rightPadding3;
        private final View rightPadding5;
        private final TextView fpsText;
        private final TextView networkText;
        private final ImageView networkIcon;
        private boolean isExpand = false;
        private View rootView;
        private SettingEventListener eventListener;

        LeftSideSetting(View parent) {
            rootView = parent;
            arrowHolder = parent.findViewById(R.id.floating_setting_left_arrow_holder);
            arrow = parent.findViewById(R.id.floating_setting_left_arrow_icon);
            arrowHolder.setOnClickListener(view -> {
                isExpand = !isExpand;
                setExpand(isExpand);
            });

            splitLine = parent.findViewById(R.id.floating_setting_left_line);
            splitLine2 = parent.findViewById(R.id.floating_setting_left_line_2);
            splitLine5 = parent.findViewById(R.id.floating_setting_left_line_5);

            interactiveGameHolder = parent.findViewById(R.id.floating_setting_interactive_game_holder);
            exitHolder = parent.findViewById(R.id.floating_setting_exit_holder);

            interactiveGameIcon = parent.findViewById(R.id.floating_setting_interactive_game_icon);
            exitIcon = parent.findViewById(R.id.floating_setting_exit_icon);

            interactiveGameText = parent.findViewById(R.id.floating_setting_interactive_game_text);
            exitText = parent.findViewById(R.id.floating_setting_exit_text);

            rightPadding = parent.findViewById(R.id.floating_setting_left_right_padding);
            rightPadding3 = parent.findViewById(R.id.floating_setting_left_right_padding_3);
            rightPadding5 = parent.findViewById(R.id.floating_setting_left_right_padding_5);

            fpsText = parent.findViewById(R.id.floating_setting_right_fps);
            networkText = parent.findViewById(R.id.floating_setting_right_network);
            networkIcon = parent.findViewById(R.id.floating_setting_right_network_icon);
            setFps(0);
            setNetworkInfo(0);
            setExpand(isExpand);
        }


        public void setFps(float fps) {
            fpsText.setText(" FPS: " + Math.round(fps) + "  ");
        }

        public void setNetworkInfo(int rtt) {
            networkText.setText(" " + rtt + "ms ") ;
        }


        public void setEventListener(SettingEventListener settingEventListener) {
            this.eventListener = settingEventListener;

            interactiveGameHolder.setOnClickListener(view -> {
                if (eventListener != null) {
                    eventListener.onClickDebug();
                }
            });

            exitHolder.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (eventListener != null) {
                        eventListener.onExit();
                    }
                }
            });

        }

        public View getRootView() {
            return rootView;
        }

        /**
         * 展开和折叠的样式
         */
        private void setExpand(boolean expand) {
            View[] views = {
                    splitLine, splitLine2, splitLine5,
                    interactiveGameHolder, interactiveGameIcon, interactiveGameText,
                    exitHolder, exitIcon, exitText,
                    rightPadding, rightPadding3, rightPadding5
            };

            if (expand) {
                // 展开的样式箭头向左
                arrow.setImageResource(R.drawable.icon_left_arrow);
                for (View view : views) {
                    view.setVisibility(View.VISIBLE);
                }
            } else {
                // 折叠的样式箭头向右
                arrow.setImageResource(R.drawable.icon_right_arrow);
                for (View view : views) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }
}
