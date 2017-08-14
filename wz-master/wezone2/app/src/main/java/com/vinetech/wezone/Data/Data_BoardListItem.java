package com.vinetech.wezone.Data;

/**
 * Created by galuster3 on 2017-02-24.
 */

public class Data_BoardListItem {
    public static final int BOARD_TYPE_TITLE = 0;
    public static final int BOARD_TYPE_NOTICE = 1;
    public static final int BOARD_TYPE_BOARD = 2;

    private int type;
    private int cnt;
    public Data_Board board;

    public Data_BoardListItem(int type, int cnt){
        this.type = type;
        this.cnt = cnt;
    }

    public Data_BoardListItem(int type, Data_Board board){
        this.type = type;
        this.board = board;
    }

    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }

    public void setCnt(int cnt){
        this.cnt = cnt;
    }
    public int getCnt(){
        return this.cnt;
    }

    public void setBoard(Data_Board board){
        this.board = board;
    }
    public Data_Board getBoard(){
        return this.board;
    }

    public boolean isSelected;
}
