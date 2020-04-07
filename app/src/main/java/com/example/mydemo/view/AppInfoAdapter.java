package com.example.mydemo.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemo.R;
import com.example.mydemo.util.App;

import java.util.List;

public class AppInfoAdapter extends RecyclerView.Adapter<AppInfoAdapter.MyViewHolder> {
    private List<App> appList;
    private Context context;

    public AppInfoAdapter(Context context,List<App> appList) {
        this.context = context;
        this.appList = appList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //实例化得到Item布局文件的View对象
        View v = View.inflate(context, R.layout.item_recycler,null);
        //返回MyViewHolder的对象
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.appName.setText(appList.get(position).getRealName());
        String frontTime = appList.get(position).getFrontTime() + "毫秒";
        holder.usedTime.setText(frontTime);
    }

    @Override
    public int getItemCount() {
        if(null != appList && appList.size() != 0){
            return appList.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView appName;
        TextView usedTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.tv_app_name);
            usedTime = itemView.findViewById(R.id.tv_app_front_time);
        }
    }
}
