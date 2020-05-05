package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.RecentOrderDetails;
import com.medicento.retailerappmedi.activity.PaymentSummaryActivity;
import com.medicento.retailerappmedi.data.Notification;

import java.util.ArrayList;

public class    NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private ArrayList<Notification> notifications;
    private Context context;

    public NotificationAdapter(ArrayList<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_notification, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Notification notification = notifications.get(i);

        viewHolder.notification_title.setText(notification.getTitle());
        viewHolder.notification_message.setText(notification.getMessage());
        viewHolder.notification_date.setText(notification.getCreated_at());
        viewHolder.notification_time.setText(notification.getTime());

        if(notification.getStatus().equals("seen")) {
            viewHolder.notification_rl.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            viewHolder.notification_title.setTextColor(Color.parseColor("#000000"));
//            viewHolder.notification_date.setTextColor(Color.parseColor("#000000"));
//            viewHolder.notification_time.setTextColor(Color.parseColor("#000000"));
//            viewHolder.notification_message.setTextColor(Color.parseColor("#000000"));
        } else {
            viewHolder.notification_rl.setBackgroundColor(Color.parseColor("#B218989E"));
//            viewHolder.notification_title.setTextColor(Color.parseColor("#FFFFFF"));
//            viewHolder.notification_date.setTextColor(Color.parseColor("#FFFFFF"));
//            viewHolder.notification_time.setTextColor(Color.parseColor("#FFFFFF"));
//            viewHolder.notification_message.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView notification_title, notification_message, notification_date, notification_time;
        RelativeLayout notification_rl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notification_title = itemView.findViewById(R.id.notification_title);
            notification_message = itemView.findViewById(R.id.notification_message);
            notification_date = itemView.findViewById(R.id.notification_date);
            notification_time = itemView.findViewById(R.id.notification_time);

            notification_rl = itemView.findViewById(R.id.notification_rl);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (notifications.get(getAdapterPosition()).getTitle().contains("Payment")) {
                            context.startActivity(new Intent(context, PaymentSummaryActivity.class)
                                    .putExtra("notification", notifications.get(getAdapterPosition())));
                        } else {
                            context.startActivity(new Intent(context, RecentOrderDetails.class)
                                    .putExtra("id", notifications.get(getAdapterPosition()).getOrder_id()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
