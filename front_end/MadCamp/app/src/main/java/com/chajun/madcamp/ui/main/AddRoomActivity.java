package com.chajun.madcamp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chajun.madcamp.R;
import com.chajun.madcamp.config.IntentKey;
import com.chajun.madcamp.data.AppData;
import com.chajun.madcamp.enums.GameType;
import com.chajun.madcamp.data.model.request.AddRoomRequest;
import com.chajun.madcamp.data.model.response.AddRoomResponse;
import com.chajun.madcamp.data.repository.Repository;
import com.chajun.madcamp.ui.game.GameStep1Activity;
import com.chajun.madcamp.util.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRoomActivity extends AppCompatActivity {

    private Spinner gameTypeSpinner;
    private EditText titleEditTxt;
    private CheckBox lockedChkBox;
    private EditText passwordEditTxt;
    private Button addBtn;

    private int numTurns;
    private int numMoves;
    private GameType gameType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        initView();
        setGameTypeSpinner();
        setAddBtn();
        setLockedChkBox();
    }

    private void initView() {
        gameTypeSpinner = findViewById(R.id.add_room_spinner_game_type);
        titleEditTxt = findViewById(R.id.add_room_edit_txt_title);
        lockedChkBox = findViewById(R.id.add_room_chk_box_password);
        passwordEditTxt = findViewById(R.id.add_room_edit_txt_password);
        addBtn = findViewById(R.id.add_room_btn_add);
    }

    private void setLockedChkBox() {
        passwordEditTxt.setEnabled(false);
        lockedChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                passwordEditTxt.setEnabled(b);

            }
        });
    }
    private void setGameTypeSpinner() {
        List<String> gameTypeList = new ArrayList<>();
        gameTypeList.add("5턴 9덱 (일반 모드)");
        gameTypeList.add("3턴 5덱 (확장 모드)");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
                , gameTypeList);
        gameTypeSpinner.setAdapter(adapter);

        gameTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = gameTypeList.get(i);
                String[] arr = value.split(" ");
                numTurns = Character.getNumericValue(arr[0].charAt(0));
                numMoves = Character.getNumericValue(arr[1].charAt(0));
                gameType = arr[2].contains("노말") ? GameType.N : GameType.E;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setAddBtn() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddRoomRequest request = new AddRoomRequest(titleEditTxt.getText().toString(),
                        gameType,
                        numTurns,
                        numMoves,
                        AppData.userId,
                        lockedChkBox.isChecked() ? 1 : 0,
                        passwordEditTxt.getText().toString());

                Repository.getInstance().addRoom(request,
                        new Callback<AddRoomResponse>() {
                            @Override
                            public void onResponse(Call<AddRoomResponse> call, Response<AddRoomResponse> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        Intent intent = new Intent(AddRoomActivity.this, GameStep1Activity.class);
                                        intent.putExtra(IntentKey.ROOM_ID, response.body().getId());
                                        startActivity(new Intent(AddRoomActivity.this, GameStep1Activity.class));
                                        finish();
                                    } else {
                                        throw new Exception();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Util.makeDialog(AddRoomActivity.this, R.string.add_room_error);
                                }

                            }

                            @Override
                            public void onFailure(Call<AddRoomResponse> call, Throwable t) {
                                Util.makeDialog(AddRoomActivity.this, R.string.add_room_error);
                            }
                        }
                );
            }
        });
    }


}
