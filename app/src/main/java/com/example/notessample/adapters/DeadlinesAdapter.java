package com.example.notessample.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notessample.R;
import com.example.notessample.entities.Deadline;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class DeadlinesAdapter extends RecyclerView.Adapter<DeadlinesAdapter.DeadlinesViewHolder> {

    private List<Deadline> deadline;
    private com.example.notessample.listeners.DeadlinesListener deadlinesListener;
    private Timer timerDeadline;
    private List<Deadline> deadlineSource;


    public DeadlinesAdapter(List<Deadline> deadline, com.example.notessample.listeners.DeadlinesListener deadlinesListener) {
        this.deadline = deadline;
        this.deadlinesListener = deadlinesListener;
        deadlineSource = deadline;

    }

    @NonNull
    @Override
    public DeadlinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeadlinesViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_deadlines,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DeadlinesViewHolder holder, int position) {
        holder.setDeadline(deadline.get(position));
        holder.layoutDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadlinesListener.onDeadlineClicked(deadline.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deadline.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class DeadlinesViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutDeadline;
        RoundedImageView imageDeadline;

        DeadlinesViewHolder(@NonNull View itemview) {
            super(itemview);
            textTitle = itemview.findViewById(R.id.textTitleDeadlines);
            textSubtitle = itemview.findViewById(R.id.textSubtitleDeadlines);
            textDateTime = itemview.findViewById(R.id.textDateTimeDeadline);
            layoutDeadline = itemview.findViewById(R.id.layoutDeadlines);
            imageDeadline = itemview.findViewById(R.id.imageDeadline);
        }

        void setDeadline(Deadline deadline) {
            textTitle.setText(deadline.getTitle());
            if (deadline.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            }
            else {
                textSubtitle.setText(deadline.getSubtitle());
            }
            textDateTime.setText(deadline.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutDeadline.getBackground();
            if (deadline.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(deadline.getColor()));
            }
            else
            {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

        }
    }

    public void searchDeadline(final String searchKeyword) {
        timerDeadline = new Timer();
        timerDeadline.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    deadline = deadlineSource;
                } else {
                    ArrayList<Deadline> temp = new ArrayList<>();
                    for (Deadline deadline : deadlineSource) {
                        if (deadline.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || deadline.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || deadline.getDeadlineText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(deadline);
                        }
                    }
                    deadline = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer() {
        if(timerDeadline !=null) {
            timerDeadline.cancel();
        }
    }

}
