package com.pixelnx.eacademy.ui.generatepapers;

import android.content.Context;
import android.text.Html;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixelnx.eacademy.R;

import com.pixelnx.eacademy.utils.AppConsts;

import com.pixelnx.eacademy.utils.widgets.CustomSmallText;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AdapterGetPaper extends RecyclerView.Adapter<AdapterGetPaper.AdapterGeneratePaperViewHolder> {

    ArrayList<ModelGetPaper.TotalExamData.QuestionDetails> paperList;
    Context mContext;
    AppConsts appConsts;
    String currentTimeString = "";
    HashMap<String, String> holdGenerateAnswer;
    HashMap<String, String> hasIdsAns;
    HashMap<String, String> questionTimeHash;
    RecyclerView recyclerView;

    public AdapterGetPaper(Context mContext, ArrayList<ModelGetPaper.TotalExamData.QuestionDetails> paperList, HashMap<String,
            String> holdGenerateAnswer, RecyclerView recyclerView, AppConsts appConsts) {
        this.paperList = paperList;
        this.mContext = mContext;
        this.holdGenerateAnswer = holdGenerateAnswer;
        this.hasIdsAns = new HashMap<>();
        this.questionTimeHash = new HashMap<>();
        this.recyclerView = recyclerView;
        this.appConsts = appConsts;
        currentTimeString = getCurrentDate("HH:mm:ss");
    }

    @NonNull
    @Override
    public AdapterGeneratePaperViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_generate_paper, viewGroup, false);
        return new AdapterGeneratePaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterGeneratePaperViewHolder holder, final int i) {
        final ModelGetPaper.TotalExamData.QuestionDetails data = paperList.get(i);


        if (i == 0) {
            holder.btPrevious.setVisibility(View.GONE);
        } else {
            holder.btPrevious.setVisibility(View.VISIBLE);
        }
        if (i == paperList.size() - 1) {
            holder.btNext.setVisibility(View.GONE);
        } else {
            holder.btNext.setVisibility(View.VISIBLE);
        }
        try {
            JSONArray jsonArray = new JSONArray(data.getOptions());
            holder.rd1.setText("" + jsonArray.get(0));
            holder.rd2.setText("" + jsonArray.get(1));
            holder.rd3.setText("" + jsonArray.get(2));
            holder.rd4.setText("" + jsonArray.get(3));


        } catch (JSONException e) {

            e.printStackTrace();
        }

        holder.tvQuestion.setVisibility(View.VISIBLE);
        holder.tvQuestion.setTextSize(17f);


        String replacedWith = "<font color = 'black'>" + " Q:" + (i + 1) + "</font>";


        String originalString = "Q:" + (i + 1) + "  " + data.getQuestion();


        String modifiedString = originalString.replaceAll("Q:" + (i + 1), replacedWith);


        holder.tvQuestion.setText(Html.fromHtml(modifiedString));
        if (data.getQuestion().length() > 170) {
            holder.tvQuestion.setText("Q:"+(i+1)+" " +data.getQuestion().substring(0,170));
            holder.viewMore.setVisibility(View.VISIBLE);
        }else{
            holder.viewMore.setVisibility(View.GONE);
        }
        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.viewMore.getText().toString().equalsIgnoreCase(mContext.getResources().getString(R.string.hide__))) {
                    holder.tvQuestion.setMaxLines(Integer.MAX_VALUE);
                    holder.tvQuestion.setText("Q:"+(i + 1) +" "+ data.getQuestion());
                    holder.viewMore.setText(mContext.getResources().getString(R.string.hide__));
                } else {
                    holder.tvQuestion.setText("Q:" +(i + 1) +" "+ data.getQuestion().substring(0,170));

                    holder.viewMore.setText(mContext.getResources().getString(R.string.Viewmore));
                }
            }
        });

        holder.rd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityGetPapers.updateCountBack(i, true);
                holdGenerateAnswer.put(data.getId(), "" + holder.rd1.getHint().toString().trim());

                ActivityGetPapers.tvRemainingQuestion.setText("Q : " + (paperList.size() - holdGenerateAnswer.size()) + "/" + paperList.size());

            }
        });

        holder.rd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityGetPapers.updateCountBack(i, true);
                holdGenerateAnswer.put(data.getId(), "" + holder.rd2.getHint().toString().trim());

                ActivityGetPapers.tvRemainingQuestion.setText("Q : " + (paperList.size() - holdGenerateAnswer.size()) + "/" + paperList.size());

            }
        });

        holder.rd3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityGetPapers.updateCountBack(i, true);
                holdGenerateAnswer.put(data.getId(), "" + holder.rd3.getHint().toString().trim());
                ActivityGetPapers.tvRemainingQuestion.setText("Q : " + (paperList.size() - holdGenerateAnswer.size()) + "/" + paperList.size());

            }
        });

        holder.rd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityGetPapers.updateCountBack(i, true);
                holdGenerateAnswer.put(data.getId(), "" + holder.rd4.getHint().toString().trim());
                ActivityGetPapers.tvRemainingQuestion.setText("Q : " + (paperList.size() - holdGenerateAnswer.size()) + "/" + paperList.size());
            }
        });
        holder.btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityGetPapers.nextPre(i, "prev");
            }
        });
        holder.btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityGetPapers.nextPre(i, "next");

            }
        });
        holder.btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityGetPapers.updateCountBack(i, false);
                holder.rdGroup.clearCheck();
                holdGenerateAnswer.remove(data.getId());
                questionTimeHash.remove(data.getId());
                hasIdsAns.remove(data.getId());
                currentTimeString = getCurrentDate("HH:mm:ss");
                appConsts.setIdsOfAllSelectedQuestions(hasIdsAns, questionTimeHash);
                ActivityGetPapers.tvRemainingQuestion.setText("Q : " + (paperList.size() - holdGenerateAnswer.size()) + "/" + paperList.size());
            }

        });

    }


    public String getCurrentDate(String outputFormat) {
        DateFormat dateFormat = new SimpleDateFormat(outputFormat);
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }

    class AdapterGeneratePaperViewHolder extends RecyclerView.ViewHolder {

        CustomSmallText tvQuestion, viewMore;
        RadioButton rd1;
        RadioButton rd2;
        RadioButton rd3;
        RadioButton rd4;
        LinearLayout btReset;
        RadioGroup rdGroup;
        LinearLayout btPrevious;
        LinearLayout btNext;


        public AdapterGeneratePaperViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            viewMore = itemView.findViewById(R.id.viewMore);
            rd1 = itemView.findViewById(R.id.rd1);
            rd2 = itemView.findViewById(R.id.rd2);
            rd3 = itemView.findViewById(R.id.rd3);
            rd4 = itemView.findViewById(R.id.rd4);
            btReset = itemView.findViewById(R.id.btReset);
            rdGroup = itemView.findViewById(R.id.rdGroup);
            btNext = itemView.findViewById(R.id.btNext);
            btPrevious = itemView.findViewById(R.id.btPrevious);
        }
    }

}
