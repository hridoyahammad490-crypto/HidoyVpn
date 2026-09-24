package com.raftechnology.hidoyvpn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {

    public interface OnServerClickListener {
        void onServerClick(Server server, int position);
    }

    private final List<Server> servers;
    private final OnServerClickListener listener;
    private int selectedPosition;

    public ServerAdapter(List<Server> servers, int selectedPosition, OnServerClickListener listener) {
        this.servers = servers;
        this.selectedPosition = selectedPosition;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_server, parent, false);
        return new ServerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerViewHolder holder, int position) {
        Server server = servers.get(position);
        holder.tvFlag.setText(server.getFlagEmoji());
        holder.tvCountry.setText(server.getCountryName());
        holder.tvPing.setText("Ping: " + server.getPingMs() + " ms");
        holder.ivSelected.setVisibility(position == selectedPosition ? View.VISIBLE : View.INVISIBLE);

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onServerClick(server, selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return servers.size();
    }

    static class ServerViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlag, tvCountry, tvPing;
        ImageView ivSelected;

        ServerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFlag = itemView.findViewById(R.id.tvFlag);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            tvPing = itemView.findViewById(R.id.tvPing);
            ivSelected = itemView.findViewById(R.id.ivSelected);
        }
    }
}