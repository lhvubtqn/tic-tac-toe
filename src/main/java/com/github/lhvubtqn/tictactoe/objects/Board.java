package com.github.lhvubtqn.tictactoe.objects;

import java.util.Random;

import com.github.lhvubtqn.tictactoe.utils.Debug;

public class Board {
	/* Constant Variables */
	public class GameResult {
		public static final int NAUGHT_WIN = 0;
		public static final int CROSS_WIN = 1;
		public static final int DRAW = 2;
		public static final int FINISHED = 3;
		public static final int UNFINISHED = 4;
		public static final int ERROR = 5;
	}

	public class Field {
		public static final int EMPTY = 0;
		public static final int NAUGHT = 1;
		public static final int CROSS = 2;
	}

	public class ResultValue {
		public static final int WIN_VALUE = 1;
		public static final int DRAW_VALUE = 0;
		public static final int LOSS_VALUE = -1;
	}

	public static final int BOARD_DIM = 3;
	public static final int BOARD_SIZE = BOARD_DIM * BOARD_DIM;

	public static final Random RAND = new Random(System.currentTimeMillis());

	/* Variables Declaration */
	public int state[];
	public int num_empty;

	/*
	 * O|O|O O|O|O O|O|O
	 */

	public Board() {
		state = new int[BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; ++i)
			state[i] = Field.EMPTY;
		num_empty = BOARD_SIZE;
	}

	public Board Clone() {
		Board new_board = new Board();
		new_board.state = new int[BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; ++i)
			new_board.state[i] = this.state[i];
		new_board.num_empty = this.num_empty;
		return new_board;
	}

	public char StateToChar(int pos) {
		if (pos < 0 || pos >= BOARD_SIZE) {
			Debug.LogError("Invalid pos of state!");
			throw new ArithmeticException("Invalid pos");
		}
		if (state[pos] == Field.CROSS)
			return 'x';
		else if (state[pos] == Field.NAUGHT)
			return 'o';
		else
			return '.';
	}

	public void PrintBoard() {
		for (int i = 0; i < 3; ++i) {
			System.out.printf("%c|%c|%c%n", StateToChar(i * 3), StateToChar(i * 3 + 1), StateToChar(i * 3 + 2));
			if (i != 2)
				System.out.println("-----");
		}
		System.out.println("");
	}

	public int OtherSide(int side) {
		if (side == Field.CROSS)
			return Field.NAUGHT;
		if (side == Field.NAUGHT)
			return Field.CROSS;
		Debug.LogError("Invalid side!");
		throw new ArithmeticException("Invalid side");
	}

	public int GetHash() {
		int hash = 0;
		for (int i = 0; i < BOARD_SIZE; ++i)
			hash = hash * 3 + state[i];
		return hash;
	}

	public boolean IsValid(int move) {
		return (move >= 0 && move < BOARD_SIZE && state[move] == Field.EMPTY);
	}

	public int Move(int position, int piece) {
		if (state[position] != Field.EMPTY) {
			Debug.LogError("Illegal move!");
			throw new ArithmeticException("Invalid move");
		}
		state[position] = piece;
		num_empty--;
		return CheckResult();
	}

	// Check if the game is finished (whether one side has won)
	public int CheckResult() {
		if ((state[0] == state[4] && state[0] == state[8] && state[0] != Field.EMPTY)
				|| (state[0] == state[1] && state[0] == state[2] && state[0] != Field.EMPTY)
				|| (state[0] == state[3] && state[0] == state[6] && state[0] != Field.EMPTY)
				|| (state[1] == state[4] && state[1] == state[7] && state[1] != Field.EMPTY)
				|| (state[2] == state[5] && state[2] == state[8] && state[2] != Field.EMPTY)
				|| (state[2] == state[4] && state[2] == state[6] && state[2] != Field.EMPTY)
				|| (state[3] == state[4] && state[3] == state[5] && state[3] != Field.EMPTY)
				|| (state[6] == state[7] && state[6] == state[8] && state[6] != Field.EMPTY))
			return GameResult.FINISHED;
		if (num_empty == 0)
			return GameResult.DRAW;
		return GameResult.UNFINISHED;
	}

	public int GetWinner() {
		if ((state[0] != Field.EMPTY && state[0] == state[4] && state[0] == state[8])
				|| (state[0] != Field.EMPTY && state[0] == state[1] && state[0] == state[2])
				|| (state[0] != Field.EMPTY && state[0] == state[3] && state[0] == state[6]))
			return state[0];
		if (state[1] != Field.EMPTY && state[1] == state[4] && state[1] == state[7])
			return state[1];
		if (state[2] != Field.EMPTY && (state[2] == state[5] && state[2] == state[8])
				|| (state[2] != Field.EMPTY && state[2] == state[4] && state[2] == state[6]))
			return state[2];
		if (state[3] != Field.EMPTY && state[3] == state[4] && state[3] == state[5])
			return state[3];
		if (state[6] != Field.EMPTY && state[6] == state[7] && state[6] == state[8])
			return state[6];
		return 0;
	}

	public void Reset() {
		state = new int[BOARD_SIZE];
		for (int i = 0; i < BOARD_SIZE; ++i)
			state[i] = Field.EMPTY;
		num_empty = BOARD_SIZE;
	}
}
