package com.github.lhvubtqn.tictactoe.objects;

import java.util.Random;

public class MinimaxPlayer extends Player {
	/* Constants */
	public static final int WIN_VALUE = 100;
	public static final int DRAW_VALUE = 0;
	public static final int LOSS_VALUE = -100;
	
	/* Variables */
	int side;
	Random rand;
	
	public MinimaxPlayer() {
		rand = new Random();
	}
	
	@Override
	public int Move(Board board) {
		int result[] = Minimax(board, true);
		return board.Move(result[1], side);
	}

	@Override
	public void Learn(int final_result) {
		// Nothing
	}

	@Override
	public void NewGame(int iside) {
		side = iside;
	}
	
	int[] Minimax(Board board, boolean is_max)
	{
		int result[] = new int[] { is_max ? LOSS_VALUE : WIN_VALUE, -1 };
		switch (board.CheckResult()) {
			case Board.GameResult.FINISHED:
				result[0] = is_max ? LOSS_VALUE : WIN_VALUE;
				return result;
			case Board.GameResult.DRAW:
				result[0] = DRAW_VALUE;
				return result;
			default:
				break;
		}
		
		for(int move = 0; move < Board.BOARD_SIZE; ++move)
			if (board.IsValid(move))
			{
				if (result[1] == -1)
					result[1] = move;
				
				Board new_board = board.Clone();
				new_board.Move(move, is_max ? side : new_board.OtherSide(side));
				// new_board.PrintBoard();
				
				int value[] = Minimax(new_board, !is_max);
				if (is_max) {
					if (result[0] < value[0] || (result[0] == value[0] && rand.nextBoolean())) {
						result[0] = value[0];
						result[1] = move;
					}
				} else {
					if (result[0] > value[0] || (result[0] == value[0] && rand.nextBoolean())) {
						result[0] = value[0];
						result[1] = move;
					}
				}
			}
		return result;
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
