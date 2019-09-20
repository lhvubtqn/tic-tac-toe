package com.github.lhvubtqn.tictactoe.objects;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.lhvubtqn.tictactoe.utils.Debug;

public class TabularQPlayer extends Player {
	/* Constants */
	public static final float WIN_VALUE = 1;
	public static final float DRAW_VALUE = (float) 0.5;
	public static final float LOSS_VALUE = 0;
	
	
	/* Variables */
	public int side;
	public float learning_rate;
	public float discount_factor;
	public float q_init_value;
	public HashMap<Integer, ArrayList<Float>> qtable;
	public ArrayList<Integer[]> move_history;

	public TabularQPlayer(float alpha, float gamma, float q_init) {
		learning_rate = alpha;
		discount_factor = gamma;
		q_init_value = q_init;
		qtable = new HashMap<Integer, ArrayList<Float>>();
		move_history = new ArrayList<Integer[]>();
	}
	
	@Override
	public int Move(Board board) {
		int move = GetMove(board);
		Integer pair[] = new Integer[] { board.GetHash(), move };
		move_history.add(pair);
		return board.Move(move, side);
	}

	@Override
	public void Learn(int final_result) {
		float final_value = DRAW_VALUE;
		switch (final_result) {
			case Board.GameResult.NAUGHT_WIN:
				final_value = (side == Board.Field.NAUGHT) ? WIN_VALUE : LOSS_VALUE;
				break;
			case Board.GameResult.CROSS_WIN:
				final_value = (side == Board.Field.CROSS) ? WIN_VALUE : LOSS_VALUE;
				break;
			case Board.GameResult.DRAW:
				final_value = DRAW_VALUE;
				break;
			default:
				Debug.LogError("Unexpected error!");
				throw new ArithmeticException("Unexpected error!");
		}
		
		float next_max = (float)-1.0;
		for(int i = move_history.size() - 1; i >= 0; --i) {
			Integer pair[] = move_history.get(i);
			ArrayList<Float> qvals = GetQ(pair[0]);	
			
			if (next_max < 0)
				qvals.set(pair[1], final_value);
			else
				qvals.set(pair[1], (float) (qvals.get(pair[1]) * (1.0 - learning_rate) + learning_rate * discount_factor * next_max));
			
			for(Float value : qvals)
				next_max = Math.max(next_max, value);
		}
	}

	@Override
	public void NewGame(int iside) {
		side = iside;
		move_history.clear();
	}

	public ArrayList<Float> GetQ(int board_hash) {
		if (qtable.containsKey(board_hash))
			return qtable.get(board_hash);
		
		ArrayList<Float> newq = new ArrayList<Float>(9);
		for(int i = 0; i < Board.BOARD_SIZE; ++i)
			newq.add(q_init_value);
		
		qtable.put(board_hash, newq);
		return newq;
	}
	
	public int GetMove(Board board) {
		int board_hash = board.GetHash();
		ArrayList<Float> qvals = GetQ(board_hash);
		
		while (true) {
			int move = 0;
			for(int i = 1; i < Board.BOARD_SIZE; ++i)
				if (qvals.get(move) < qvals.get(i))
					move = i;
			if (board.IsValid(move))
				return move;
			qvals.set(move, (float) -1.0);
		}
	}

	@Override
	public void WriteData() {
		try {
			FileOutputStream fos = new FileOutputStream("models/TabularQ.bin");
			DataOutputStream dos = new DataOutputStream(fos);
	      
			qtable.forEach((value, array) -> {
				try {
					dos.writeInt((int) value);
					for(int i = 0; i < Board.BOARD_SIZE; ++i)
			    		  dos.writeFloat((float) array.get(i));
				
				    } catch (IOException e) {
				      System.out.println("Error - " + e.toString());
				    }
			});
			dos.close();
		} catch (IOException e) {
		      System.out.println("Error - " + e.toString());
		}
	}

	@Override
	public void LoadData() {
		qtable.clear();
		try {
			FileInputStream fis = new FileInputStream("models/TabularQ.bin");
		    DataInputStream dis = new DataInputStream(fis);
		    
		    while (dis.available() > 0) {
		    	int value = (int) dis.readInt();
		    	ArrayList<Float> array = new ArrayList<Float>();
		    	for(int i = 0; i < Board.BOARD_SIZE; ++i) {
		    		array.add((float) dis.readFloat());
		    	}
		    	qtable.put(value, array);
		    }
		    dis.close();
		} catch (IOException e) {
			System.out.println("Error - " + e.toString());
		}
	}
}






















