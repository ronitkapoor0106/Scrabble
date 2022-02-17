package com.example.scrabble;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Square implements View.OnClickListener{

    public final int row;
    public final int column;
    public final TextView tv;

    public Square(TextView tv, int row, int column){
        this.row = row;
        this.column = column;
        this.tv = tv;

        this.tv.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(TileListener.CurrentTile != null){
            this.tv.setText(TileListener.CurrentTile.tileView.getText());
            this.tv.setBackgroundResource(R.drawable.scrabble_tile);
            TileListener.CurrentTile.useTile(row, column);
            Log.d("test", " " + TileListener.CurrentTile.used);
            TileListener.CurrentTile = null;
        }
    }
    public void clearTile(){
        this.tv.setText("");
        this.tv.setBackgroundResource(R.drawable.empty_square);

    }
}
