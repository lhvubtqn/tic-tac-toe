package com.github.lhvubtqn.tictactoe.objects;

import java.util.Random;

public class RandomPlayer extends Player {
	int side;
	Random rand;
	
	public RandomPlayer() {
		rand = new Random();
	}
	
	@Override
	public int Move(Board board) {
		int move = rand.nextInt(9);
		while (!board.IsValid(move)) {
			move = rand.nextInt(9);
		}
		return board.Move(move, side);
	}

	@Override
	public void Learn(int final_result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void NewGame(int iside) {
		side = iside;

	}

	@Override
	public void WriteData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void LoadData() {
		// TODO Auto-generated method stub

	}

}
