package com.example.scrabble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static HashMap<Character,Integer> TileCount = new HashMap<Character, Integer>(){
        {
            put('A',9);
            put('B',2);
            put('C',2);
            put('D',4);
            put('E',12);
            put('F',2);
            put('G',3);
            put('H',2);
            put('I',9);
            put('J',1);
            put('K',1);
            put('L',4);
            put('M',2);
            put('N',6);
            put('O',8);
            put('P',2);
            put('Q',1);
            put('R',6);
            put('S',4);
            put('T',6);
            put('U',4);
            put('V',2);
            put('W',2);
            put('X',1);
            put('Y',2);
            put('Z',1);
            put(' ',2);
        }
    };
    public static HashMap<Character, Integer> pointMap = new HashMap<Character,Integer>(){
        {
            put(' ',0);
            put('A',1);
            put('E',1);
            put('I',1);
            put('L',1);
            put('N',1);
            put('O',1);
            put('R',1);
            put('S',1);
            put('T',1);
            put('U',1);
            put('D',2);
            put('G',2);
            put('B',3);
            put('C',3);
            put('M',3);
            put('P',3);
            put('F',4);
            put('H',4);
            put('V',4);
            put('W',4);
            put('Y',4);
            put('K',5);
            put('J',8);
            put('X',8);
            put('Q',10);
            put('Z',10);


        }
    };
    public static ArrayList<TileListener> playerTiles = new ArrayList<TileListener>();
    public static ArrayList<String> words = new ArrayList<String>();
    public static ArrayList<Character> bagOfTiles = new ArrayList<Character>();
    public static Context m_context;
    public static Square[][] SquareTable = new Square[15][15];
    public static int pointtotal = 0;
    public static LinearLayout playerTilesLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_context = getApplicationContext();
        playerTilesLayout = findViewById(R.id.players_tiles);

        try{
            InputStream is = this.getResources().openRawResource(R.raw.scrabble_words_a_to_l);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while(line != null){
                words.add(line);
                line = br.readLine();
            }
            InputStream is2 = this.getResources().openRawResource(R.raw.scrabble_words_m_to_z);
            InputStreamReader isr2 = new InputStreamReader(is2);
            BufferedReader br2 = new BufferedReader(isr2);
            line = br2.readLine();
            while(line != null){
                words.add(line);
                line = br2.readLine();
            }

        }
        catch(Exception e){
          Log.e("corrupt", e.toString());
        }

        ImageView cancelbutton = findViewById(R.id.cancel_button);
        ImageView confirmbutton = findViewById(R.id.confirm_button);
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "click cancel");
//                onclick for cancel button needs to undo any used tiles
//                for loop through each tile listener item of player tiles, check if used is true, then use row and column, clear the tile, erase
                for(TileListener tile : playerTiles){
                    Log.d("test", "" + tile.used);
                    if(tile.used == true){
                        Log.d("test", tile.row + " " + tile.column);
                        SquareTable[tile.row][tile.column].clearTile();
                        tile.unusedTile();
                    }
                }

            }
        });
        confirmbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "click cancel");
//                for loop through each tile listener item of player tiles, check if used is true, then use row and column, clear the tile, erase
                ArrayList<TileListener> use = new ArrayList<TileListener>();
                String word = "";
                for(TileListener tile : playerTiles) {
                    if (tile.used == true) {
                        use.add(tile);
                    }
                }
                boolean good;
                if(TileListener.AllOnSameRow(use) || TileListener.AllOnSameColumn(use)){
                    good = true;
                }

                else{
                    good = false;
                    Toast.makeText(m_context, " tiles are out of position", Toast.LENGTH_SHORT).show();
                    return;
                }
                Collections.sort(use);
                if(pointtotal == 0){
                    if(!gaps(use))
                        good = true;
                    else{
                        good = false;
                        Toast.makeText(m_context, word + " is not continuous", Toast.LENGTH_SHORT).show();
                    }
                    if(good){
                        for(TileListener tile : playerTiles){
                            if(tile.row == 8 && tile.column == 8)
                                good = true;
                        }
                        if(!good)
                            Toast.makeText(m_context, word + " isn't in the center of the board", Toast.LENGTH_SHORT).show();
                        else{
                            for(TileListener tl : use){
                                word = word + tl.getLetter();
                            }
                            if(words.contains(word)){
                                good = true;
                            }
                            else{
                                good = false;
                                Toast.makeText(m_context, word + " isn't a valid word", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }
                else{
                    boolean isConnected = false;
                    int temppoint = 0;
                    if(TileListener.AllOnSameColumn(use)) {
                        for(TileListener tile : use){
                            word = tile.getLetter();
                            int startrow = tile.row;
                            int startcol = tile.column;
                            if(startrow == 0){
                                if(SquareTable[startrow + 1][startcol] != null){
//                                    start to build the word, here its easy since we are just going down
                                    for(int r = startrow+1; r <= 15; r++){
                                        if(SquareTable[r][startcol] == null){break;}
                                        else{
                                            word += SquareTable[r][startcol].tv.getText();
                                            isConnected = true;
                                        }
                                    }
                                    good = words.contains(word);
                                }
                            }
                            else if(startrow == 15){
                                if(SquareTable[startrow - 1][startcol] != null){
//                                    start to build the word, here its easy since we are just going down
                                    for(int r = startrow+1; r >= 0; r--){
                                        if(SquareTable[r][startcol] == null){break;}
                                        else{
                                            word = SquareTable[r][startcol].tv.getText() + word;
                                            isConnected = true;
                                        }
                                    }
                                    good = words.contains(word);
                                }
                            }
                            else if(SquareTable[startrow + 1][startcol] != null || SquareTable[startrow - 1][startcol] != null){
//                              build word
                                for(int r = startrow+1; r <= 15; r++){
                                    if(SquareTable[r][startcol] == null){break;}
                                    else{
                                        word += SquareTable[r][startcol].tv.getText();
                                        isConnected = true;
                                    }

                                }
                                for(int r = startrow-1; r >= 0; r--){
                                    if(SquareTable[r][startcol] == null){break;}
                                    else{
                                        word = SquareTable[r][startcol].tv.getText() + word;
                                        isConnected = true;
                                    }
                                }

                                good = words.contains(word);
                            }
                            if(good){for(char c : word.toCharArray()){
                                temppoint += pointMap.get(c);
                            }}
                            else{
                                Toast.makeText(m_context, word + " is not valid", Toast.LENGTH_SHORT).show();
                                break;
                            }

                        }

                    }
                    else if(TileListener.AllOnSameRow(use)){
                        temppoint = 0;
                        for(TileListener tile : use){
                            int startrow = tile.row;
                            int startcol = tile.column;
                            if(startcol == 0){
                                if(SquareTable[startrow][startcol+1] != null){
//                                    start to build the word, here its easy since we are just going down
                                    for(int c = startcol+1; c <= 15; c++){
                                        if(SquareTable[startrow][c] == null){break;}
                                        else{
                                            word += SquareTable[startrow][c].tv.getText();
                                            isConnected = true;
                                        }
                                    }
                                    good = words.contains(word);
                                }
                            }
                            else if(startcol == 15){
                                if(SquareTable[startrow][startcol - 1] != null){
//                                    start to build the word, here its easy since we are just going down
                                    for(int c = startcol-1; c >= 0; c--){
                                        if(SquareTable[startrow][c] == null){break;}
                                        else{
                                            word = SquareTable[startrow][c].tv.getText() + word;
                                            isConnected = true;
                                        }
                                    }
                                    good = words.contains(word);
                                }
                            }
                            else if(SquareTable[startrow][startcol + 1] != null || SquareTable[startrow][startcol - 1] != null){
//                              build word
                                for(int c = startcol+1; c <= 15; c++){
                                    if(SquareTable[startrow][c] == null){break;}
                                    else{
                                        word += SquareTable[startrow][c].tv.getText();
                                        isConnected = true;
                                    }

                                }
                                for(int c = startcol-1; c >= 0; c--){
                                    if(SquareTable[startrow][c] == null){break;}
                                    else{
                                        word = SquareTable[startrow][c].tv.getText() + word;
                                        isConnected = true;
                                    }
                                }

                                good = words.contains(word);
                            }
                            if(good){for(char c : word.toCharArray()){
                                temppoint += pointMap.get(c);
                            }}
                            else{
                                Toast.makeText(m_context, word + " is not valid", Toast.LENGTH_SHORT).show();
                                break;
                            }

                        }
                        word = "";
                        if(TileListener.AllOnSameRow(use)){
                            int startrow = use.get(0).row;
                            int startcol = use.get(0).column;
                            if(startcol != 0 && SquareTable[startrow][startcol - 1] != null){
                                for(int c = startcol-1; c >= 0; c--){
                                    if(SquareTable[startrow][c] == null){break;}
                                    else{
                                        word = SquareTable[startrow][c].tv.getText() + word;
                                        isConnected = true;
                                    }
                                }
                            }
                            int r = use.get(0).row;
                            int c = use.get(0).column;
                            word += use.get(0).getLetter();
                            for(int i = 1; i < use.size(); i++){
                                r = use.get(i).row;
                                c = use.get(i).column;
                                if(use.get(i-1).column != c - 1){
                                    for(int c2 = use.get(i).column + 1; c2 < c; c++){
                                        if(SquareTable[startrow][c2] == null){
                                            good = false;
                                            Toast.makeText(m_context, "Word is not continuous", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        else{
                                            word += SquareTable[startrow][c2].tv.getText();
                                            isConnected = true;
                                        }
                                    }
                                }


                                if(!good){
                                    break;
                                }
                                word += use.get(i).getLetter();
                            }
                            if(c != 15 && SquareTable[r][c + 1] != null){
                                for(int c2 = c+1; c2 <= 15; c2++){
                                    if(SquareTable[r][c2] == null){break;}
                                    else{
                                        word += SquareTable[r][c2].tv.getText();
                                        isConnected = true;
                                    }
                                }
                            }
                            if(words.contains(word)){
                                good = true;
                            }
                            if(!isConnected && good){
                                good = false;
                                Toast.makeText(m_context, word + " is not a valid word", Toast.LENGTH_SHORT).show();
                            }
                            else{pointtotal += temppoint;}
                        }
                        else if(TileListener.AllOnSameColumn(use)){

                                int startrow = use.get(0).row;
                                int startcol = use.get(0).column;
                                if(startrow != 0 && SquareTable[startrow-1][startcol] != null){
                                    for(int r = startrow-1; r >= 0; r--){
                                        if(SquareTable[r][startcol] == null){break;}
                                        else{
                                            word = SquareTable[r][startcol].tv.getText() + word;
                                            isConnected = true;
                                        }
                                    }
                                }
                                int r = use.get(0).row;
                                int c = use.get(0).column;
                                word += use.get(0).getLetter();
                                for(int i = 1; i < use.size(); i++){
                                    r = use.get(i).row;
                                    c = use.get(i).column;
                                    if(use.get(i-1).row != r - 1){
                                        for(int r2 = use.get(i).row + 1; r2 < r; r++){
                                            if(SquareTable[r2][startcol] == null){
                                                good = false;
                                                Toast.makeText(m_context, "Word is not continuous", Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                            else{
                                                word += SquareTable[r2][startcol].tv.getText();
                                                isConnected = true;
                                            }
                                        }
                                    }


                                    if(!good){
                                        break;
                                    }
                                    word += use.get(i).getLetter();
                                }
                                if(r != 15 && SquareTable[r+1][c] != null){
                                    for(int r2 = r+1; r2 <= 15; r2++){
                                        if(SquareTable[r2][c] == null){break;}
                                        else{
                                            word += SquareTable[r2][c].tv.getText();
                                            isConnected = true;
                                        }
                                    }
                                }
                                if(words.contains(word)){
                                    good = true;
                                }
                                if(!isConnected && good){
                                    good = false;
                                    Toast.makeText(m_context, word + " is not a valid word", Toast.LENGTH_SHORT).show();
                                }
                                else{pointtotal += temppoint;
                                }
                            }
                        }
                    }




                if(good){
                    for(int i = playerTiles.size() - 1; i >= 0; i--){
                        if(playerTiles.get(i).used){
                            playerTilesLayout.removeView(playerTiles.get(i).tileView);
                            playerTiles.remove(i);

                            Character c = bagOfTiles.remove(0);
                            TileListener tl = CreateTile(c);
                            playerTilesLayout.addView(tl.tileView);
                            playerTiles.add(tl);
                        }
                    }
                    for(char c : word.toCharArray()){
                        pointtotal += pointMap.get(c);
                    }

                }
                else{
                    Toast.makeText(m_context, word + " is not part of the English Language. Click X button", Toast.LENGTH_SHORT).show();
                }



            }
        });
        //hashmap: key & value
        for(Character c : TileCount.keySet()){
            int numberOfTiles = TileCount.get(c);
            for(int i = 0; i < numberOfTiles; i++){
                bagOfTiles.add(c);
            }
        }

        Collections.shuffle(bagOfTiles);
        for(int i=0; i<7;i++){
            Character c = bagOfTiles.remove(0);
            TileListener tl = CreateTile(c);
            playerTilesLayout.addView(tl.tileView);
            playerTiles.add(tl);
        }



        TableLayout table = findViewById(R.id.scrabble_table);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        //loop add 19 rows
        for(int i = 0; i < 15; i++){
            TableRow row = new TableRow(this.getBaseContext());
            for(int j = 0; j < 15; j++){
                TextView tv = CreateSquare();
                row.addView(tv);
                Square s = new Square(tv, i, j);
                SquareTable[i][j] = s;
            }
            table.addView(row);
        }

        



    }

    public static boolean gaps(ArrayList<TileListener> use){
//        After the sort
        if(TileListener.AllOnSameRow(use)){
            int firstcol = use.get(0).column;

            for(int i = 1; i < (int) use.size(); i++){
                if(!(use.get(i).column == firstcol + 1)) {
                    return false;
                }
                firstcol = use.get(i).column;
            }
            return true;
        }
        else{
            int firstrow = use.get(0).row;
            for(int i = 1; i < (int) use.size(); i++){
                if(!(use.get(i).row == firstrow + 1)) {
                    return false;
                }
                firstrow = use.get(i).row;
            }
            return true;
        }

        }

    public static boolean firstturn(ArrayList<TileListener> use){
        boolean center = false;
        for(TileListener x : use){
            if(x.row == 8 && x.column == 8){
                center = true;
            }
        }
        if(center){
            return true;
        }
        else{
            return false;
        }

        }


    TileListener CreateTile(Character letter){
        TextView new_tile = new TextView(m_context);

        LinearLayout.LayoutParams sizing = new LinearLayout.LayoutParams(128,128);
        new_tile.setLayoutParams(sizing);
        new_tile.setText(letter.toString());
        new_tile.setBackgroundResource(R.drawable.scrabble_tile);

        new_tile.setGravity(Gravity.CENTER);
        new_tile.setTextSize(32);
        TileListener tl = new TileListener(new_tile);
        new_tile.setOnClickListener(tl);
        return tl;

    }
    TextView CreateSquare(){
        TextView square = new TextView(m_context);
        square.setLayoutParams(new TableRow.LayoutParams(128, 128));
        square.setBackgroundResource(R.drawable.empty_square);
        square.setText("");
        square.setTextSize(32);
        square.setGravity(Gravity.CENTER);
        return square;
    }
}
