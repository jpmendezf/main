package com.pixelnx.eacademy.ui.mcq.resultquestionlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixelnx.eacademy.R;
import com.pixelnx.eacademy.model.modelviewresult.ModelViewResult;

import com.pixelnx.eacademy.utils.widgets.CustomSmallText;
import com.pixelnx.eacademy.utils.widgets.CustomTextNormalBold;
import com.pixelnx.eacademy.utils.widgets.CustomTextSemiBold;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class AdapterPaperResultList extends RecyclerView.Adapter<AdapterPaperResultList.ViewHolderAdapterPaperResultList> {

    Context mContext;
    String examType;
    ArrayList<ModelViewResult.AllData.AllQuestion> subjectWiseResultList;

    public AdapterPaperResultList(Context mContext, ArrayList<ModelViewResult.AllData.AllQuestion> subjectWiseResultList, String examType) {
        this.mContext = mContext;
        this.examType = examType;
        this.subjectWiseResultList = subjectWiseResultList;
    }

    @NonNull
    @Override
    public ViewHolderAdapterPaperResultList onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_exam_paper_details, viewGroup, false);
        return new ViewHolderAdapterPaperResultList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAdapterPaperResultList holder, int i) {

        if (!subjectWiseResultList.get(i).getStudentAnswer().isEmpty()) {
            holder.tvYourAns.setText("Your Answer : " + subjectWiseResultList.get(i).getStudentAnswer());
            try {
                JSONArray json = new JSONArray("" + subjectWiseResultList.get(i).getOptions());

                if (subjectWiseResultList.get(i).getStudentAnswer().equals("A")) {
                    holder.tvYourAns.setText("Your Answer : " + json.get(0));
                } else {
                    if (subjectWiseResultList.get(i).getStudentAnswer().equals("B")) {
                        holder.tvYourAns.setText("Your Answer : " + json.get(1));
                    } else {
                        if (subjectWiseResultList.get(i).getStudentAnswer().equals("C")) {
                            holder.tvYourAns.setText("Your Answer : " + json.get(2));
                        } else {
                            if (subjectWiseResultList.get(i).getStudentAnswer().equals("D")) {
                                holder.tvYourAns.setText("Your Answer : " + json.get(3));
                            } else {
                                holder.tvYourAns.setText("Your Answer : Not Attempted");
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        holder.tvQuestion.setText("Q:" + (i + 1) + " " + subjectWiseResultList.get(i).getQuestion());
        if (subjectWiseResultList.get(i).getRightAnswer().equalsIgnoreCase("wrong")) {
            holder.paperDetailsBack.setBackgroundResource(R.drawable.ans_details_back_wrong_grad);
        }
        if (subjectWiseResultList.get(i).getRightAnswer().equalsIgnoreCase("right")) {
            holder.paperDetailsBack.setBackgroundResource(R.drawable.ans_details_back_right_grad);
        }
        try {
            JSONArray jsonArray = new JSONArray("" + subjectWiseResultList.get(i).getOptions());


            if (subjectWiseResultList.get(i).getAnswer().equalsIgnoreCase("A")) {
                holder.tvCorrectAnswer.setText("" + jsonArray.get(0));
            }
            if (subjectWiseResultList.get(i).getAnswer().equalsIgnoreCase("B")) {
                holder.tvCorrectAnswer.setText("" + jsonArray.get(1));
            }
            if (subjectWiseResultList.get(i).getAnswer().equalsIgnoreCase("C")) {
                holder.tvCorrectAnswer.setText("" + jsonArray.get(2));
            }
            if (subjectWiseResultList.get(i).getAnswer().equalsIgnoreCase("D")) {
                holder.tvCorrectAnswer.setText("" + jsonArray.get(3));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            if (holder.tvYourAns.getText().toString().equals(holder.tvCorrectAnswer.getText().toString())) {
                holder.hideLayout.setVisibility(View.GONE);
            } else {
                holder.hideLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return subjectWiseResultList.size();
    }

    class ViewHolderAdapterPaperResultList extends RecyclerView.ViewHolder {

        ImageView ivQuestionImage;
        CustomTextNormalBold tvTimeUsed;
        CustomTextSemiBold tvQuestion;
        CustomSmallText tvYourAns;
        LinearLayout hideLayout;
        LinearLayout paperDetailsBack;
        CustomTextSemiBold tvCorrectAnswer;

        public ViewHolderAdapterPaperResultList(@NonNull View itemView) {
            super(itemView);
            ivQuestionImage = itemView.findViewById(R.id.ivQuestionImage);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvYourAns = itemView.findViewById(R.id.tvYourAns);
            hideLayout = itemView.findViewById(R.id.hideLayout);
            tvTimeUsed = itemView.findViewById(R.id.tvTimeUsed);
            paperDetailsBack = itemView.findViewById(R.id.paperDetailsBack);
            tvCorrectAnswer = itemView.findViewById(R.id.tvCorrectAnswer);
        }
    }
}
