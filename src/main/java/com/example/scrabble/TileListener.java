package com.example.scrabble;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class TileListener implements View.OnClickListener, Comparable<TileListener>{
    public static TileListener CurrentTile = null;

    public boolean selected;
    public boolean used;
    public TextView tileView;
    public int row;
    public int column;
    public static boolean AllOnSameRow(ArrayList<TileListener> lis){
//        return true if all in same row, make new function for column
        int a = lis.get(0).row;
        boolean is = true;
        for(TileListener el : lis){
            if(el.row != a){
                is = false;
            }
        }
        if(is) {
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean AllOnSameColumn(ArrayList<TileListener> lis){
//        return true if all in same row, make new function for column
        int a = lis.get(0).column;
        boolean is = true;
        for(TileListener el : lis){
            if(el.column != a){
                is = false;
            }
        }
        if(is) {
            return true;
        }
        else{
            return false;
        }
    }

    public TileListener(TextView v){
        this.selected = false;
        this.tileView = v;
        this.row = -1;
        this.column = -1;
    }
    @Override
    public void onClick(View view){
        Log.d("test","click");
        if(this.selected==false && this.used==false) {
            this.selected = true;
            if (TileListener.CurrentTile != null){
                TileListener.CurrentTile.selected = false;
                TileListener.CurrentTile.tileView.setBackgroundResource(R.drawable.scrabble_tile);

            }

            TileListener.CurrentTile = this;
            view.setBackgroundResource(R.drawable.selected_tile);
        }
        else if(this.selected == true){
            this.selected = false;
            TileListener.CurrentTile = null;
            view.setBackgroundResource(R.drawable.scrabble_tile);
        }
    }
    public String getLetter(){
        return (String) this.tileView.getText();
    }


    public void deselect(){
        this.selected = false;
        this.tileView.setBackgroundResource(R.drawable.scrabble_tile);
    }

    public void useTile(int row, int column){
        this.row = row;
        this.column = column;
        this.selected = false;
        this.used = true;
        this.tileView.setBackgroundResource(R.drawable.used_tile);
    }
    public void unusedTile(){
        this.selected = false;
        this.used = false;
        this.tileView.setBackgroundResource(R.drawable.scrabble_tile);
    }

    @Override
    public int compareTo(TileListener o) {
        if(this.row == o.row){
            if(this.column > o.column) {
                return 1;
            }
            else {
                return -1;
            }
        }
        else{
            if(this.row > o.row) {
                return 1;
            }
            else{
                return -1;
            }
        }

    }
}
