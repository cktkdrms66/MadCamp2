package com.chajun.madcamp.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.data.model.request.JoinRoomRequest;
import com.chajun.madcamp.data.model.response.JoinRoomResponse;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.enums.GameState;
import com.chajun.madcamp.data.model.response.Room;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.ui.game.GameActivity;
import com.chajun.madcamp.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder>{

    private List<Room> roomList;

    public RoomAdapter() {
        roomList = new ArrayList<>();
    }

    public void setItems(List<Room> items) {
        roomList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_room_list, parent, false);
        return new RoomViewHolder(context, view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Room room = roomList.get(holder.getAdapterPosition());
        holder.roomNumberTxt.setText(String.valueOf(room.getRoomNumber()));
        holder.titleTxt.setText(room.getTitle());
        holder.gameTypeTxt.setText(room.getNumTurns() + "T " + room.getNumMoves() + "D");
        holder.hostNameTxt.setText(room.getHostName());

        if (room.isLocked()) {
            holder.lockImg.setImageResource(R.drawable.locked);
        } else {
            holder.lockImg.setImageResource(R.drawable.unlocked);
        }

        if (room.getState() == GameState.P) {
            holder.background.setBackgroundColor(Color.GRAY);
        } else {
            holder.background.setBackgroundColor(Color.WHITE);
        }

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = holder.background.getContext();
                if (room.getState() == GameState.P) {
                    Toast.makeText(context, R.string.prevent_join_room, Toast.LENGTH_SHORT).show();
                } else {
                    if (room.isLocked()) {
                        Dialog dialog;
                        dialog = new Dialog(MainActivity.context);
                        dialog.setContentView(R.layout.dialog_password);

                        dialog.findViewById(R.id.dialog_password_btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String password = ((EditText)dialog.findViewById(R.id.dialog_password_edit_txt)).getText().toString();
                                joinRoom(holder.itemView.getContext(), room, new JoinRoomRequest(room.getId(), AppData.userId, password));
                                dialog.cancel();
                            }
                        });

                        dialog.findViewById(R.id.dialog_password_btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();
                            }
                        });
                        dialog.show();

                    } else {
                        joinRoom(holder.itemView.getContext(), room, new JoinRoomRequest(room.getId(), AppData.userId, ""));
                    }
                }
            }
        });
    }

    private void joinRoom(Context context, Room room, JoinRoomRequest request) {
        Repository.getInstance().joinRoom(request, new Callback<JoinRoomResponse>() {
            @Override
            public void onResponse(Call<JoinRoomResponse> call, Response<JoinRoomResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess() == 1) {
                        Intent intent = new Intent(context, GameActivity.class);
                        intent.putExtra(IntentKey.ROOM_ID, request.getRoomId());
                        intent.putExtra(IntentKey.IS_EXPANDED, Objects.equals(room.getGameType(), GameType.E.toString()));
                        intent.putExtra(IntentKey.NUM_TURNS, room.getNumTurns());
                        intent.putExtra(IntentKey.NUM_MOVES, room.getNumMoves());
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, R.string.prevent_join_room, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, R.string.prevent_join_room, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JoinRoomResponse> call, Throwable t) {
                Toast.makeText(context, R.string.prevent_join_room, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder {

        TextView roomNumberTxt;
        TextView titleTxt;
        TextView gameTypeTxt;
        TextView hostNameTxt;

        ImageView lockImg;

        ViewGroup background;

        RoomViewHolder(Context context, View itemView) {
            super(itemView);

            roomNumberTxt = itemView.findViewById(R.id.list_item_room_txt_id);
            titleTxt = itemView.findViewById(R.id.list_item_room_txt_title);
            gameTypeTxt = itemView.findViewById(R.id.list_item_room_txt_game_type);
            hostNameTxt = itemView.findViewById(R.id.list_item_room_txt_host_name);
            lockImg = itemView.findViewById(R.id.list_item_room_img_lock);
            background = itemView.findViewById(R.id.list_item_room_layout);


        }
    }
}
