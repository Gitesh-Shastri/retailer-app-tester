package com.medicento.retailerappmedi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.medicento.retailerappmedi.R;
import com.medicento.retailerappmedi.data.WebCodes;

import java.util.ArrayList;

public class UserWebLoginAdapter extends RecyclerView.Adapter<UserWebLoginAdapter.ViewHolder> {

    private ArrayList<WebCodes> webCodes;
    private Context context;

    public UserWebLoginAdapter(ArrayList<WebCodes> webCodes, Context context) {
        this.webCodes = webCodes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_web_codes, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        WebCodes webCode = this.webCodes.get(i);

        viewHolder.user_os.setText(webCode.getUser_os());

        switch (webCode.getAgent()) {
            case "Chrome":
                viewHolder.browser_icon.setImageResource(R.drawable.chrome_logo);
                break;
            case "Firefox":
                viewHolder.browser_icon.setImageResource(R.drawable.firefox_logo);
                break;
            case "Opera":
                viewHolder.browser_icon.setImageResource(R.drawable.opera);
                break;
            case "Safari":
                viewHolder.browser_icon.setImageResource(R.drawable.safari);
                break;
            case "IE":
                viewHolder.browser_icon.setImageResource(R.drawable.ie);
                break;
            default:
                viewHolder.browser_icon.setImageResource(R.drawable.web_browser);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return webCodes != null ? webCodes.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView browser_icon;
        TextView user_os;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            browser_icon = itemView.findViewById(R.id.browser_icon);
            user_os = itemView.findViewById(R.id.user_os);
        }
    }
}
