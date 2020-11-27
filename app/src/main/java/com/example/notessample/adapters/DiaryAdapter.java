package com.example.notessample.adapters;

import android.graphics.BitmapFactory;
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
import com.example.notessample.entities.Diary;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private List<Diary> diaries;
    private com.example.notessample.listeners.DiaryListener diaryListener;
    private Timer timerDiary;
    private List<Diary> diarySource;


    public DiaryAdapter(List<Diary> diaries, com.example.notessample.listeners.DiaryListener diaryListener) {
        this.diaries = diaries;
        this.diaryListener = diaryListener;
        diarySource = diaries;

    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiaryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        holder.setDiary(diaries.get(position));
        holder.layoutDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaryListener.onDiaryClicked(diaries.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle, textSubtitle, textDateTime;
        LinearLayout layoutDiary;
        RoundedImageView imageDiary;

        DiaryViewHolder(@NonNull View itemview) {
            super(itemview);
            textTitle = itemview.findViewById(R.id.textTitleDiary);
            textSubtitle = itemview.findViewById(R.id.textSubtitleDiary);
            textDateTime = itemview.findViewById(R.id.textDateTimeDiary);
            layoutDiary = itemview.findViewById(R.id.layoutDiary);
            imageDiary = itemview.findViewById(R.id.imageDiary);
        }

        void setDiary(Diary diary) {
            textTitle.setText(diary.getTitle());
            if (diary.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            }
            else {
                textSubtitle.setText(diary.getSubtitle());
            }
            textDateTime.setText(diary.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutDiary.getBackground();
            if (diary.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(diary.getColor()));
            }
            else
            {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }


            if(diary.getImagePath() != null){
                imageDiary.setImageBitmap(BitmapFactory.decodeFile(diary.getImagePath()));
                imageDiary.setVisibility(View.VISIBLE);
            }
            else
            {
                imageDiary.setVisibility(View.GONE);
            }

        }
    }

    public void searchDiary(final String searchKeyword) {
        timerDiary = new Timer();
        timerDiary.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    diaries = diarySource;
                } else {
                    ArrayList<Diary> temp = new ArrayList<>();
                    for (Diary diary : diarySource) {
                        if (diary.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || diary.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || diary.getDiaryText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(diary);
                        }
                    }
                    diaries = temp;
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
        if(timerDiary !=null) {
            timerDiary.cancel();
        }
    }

}
