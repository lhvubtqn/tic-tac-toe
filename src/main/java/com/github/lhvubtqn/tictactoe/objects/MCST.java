package com.github.lhvubtqn.tictactoe.objects;

import java.util.ArrayList;

import com.github.lhvubtqn.tictactoe.utils.Debug;

public class MCST {

	private Board board;
	private int source_move;
	private int side;
	private int total_value;
	private int total_simulations;
	private ArrayList<MCST> children;

	public MCST(Board board, int source_move, int side, boolean is_root) {
		this.board = board;
		this.source_move = source_move;
		this.side = side;
		this.total_value = 0;
		this.total_simulations = 0;
		this.children = new ArrayList<MCST>();

		if (is_root) {
			this.DoExpansion();
			int result = this.children.get(0).DoSimulation(board.Clone(), side);
			this.total_value = result;
			this.total_simulations = 1;
		}
	}

	public int DoIteration() {
		if (this.total_simulations == 0) {
			int result = DoSimulation(this.board.Clone(), side);
			this.total_value = result;
			this.total_simulations = 1;
			return result;
		}

		if (this.children.isEmpty()) {
			DoExpansion();
			if (this.children.isEmpty()) {
				int result = DoSimulation(this.board.Clone(), side);
				this.total_value = result;
				this.total_simulations = 1;
				return result;
			}
		}

		MCST selected = null;
		for (int i = 0; i < this.children.size(); ++i) {
			MCST child = this.children.get(i);
			if (child.total_simulations == 0) {
				selected = child;
				break;
			}

			if (selected == null)
				selected = child;
			else {
				double uct1 = UCT(selected.total_value, selected.total_simulations, 2, this.total_simulations);
				double uct2 = UCT(child.total_value, child.total_simulations, 2, this.total_simulations);
				if (uct1 < uct2) {
					selected = child;
				}
			}
		}

		int result = -selected.DoIteration();
		this.total_value += -result;
		this.total_simulations += 1;
		return result;
	}

	public void DoExpansion() {
		for (int i = 0; i < Board.BOARD_SIZE; ++i) {
			if (board.IsValid(i)) {
				Board new_board = board.Clone();
				new_board.Move(i, side);
				this.children.add(new MCST(new_board, i, new_board.OtherSide(side), false));
			}
		}
	}

	public int DoSimulation(Board board, int side) {
		if (board.num_empty == 0) {
			int winner = board.GetWinner();
			if (winner == 0)
				return 0;
			if (side == winner)
				return 1;
			else 
				return -1;
		}
		
		int next_move = Board.RAND.nextInt(board.num_empty) + 1;
		for (int i = 0; i < Board.BOARD_SIZE; ++i) {
			if (board.IsValid(i)) {
				next_move--;
				if (next_move == 0) {
					next_move = i;
					break;
				}
			}
		}

		int result = board.Move(next_move, side);
		if (board.num_empty == 0) {
			if (result == Board.GameResult.FINISHED)
				return (int) (this.side == side ? Board.ResultValue.WIN_VALUE : Board.ResultValue.LOSS_VALUE);
			else if (result == Board.GameResult.DRAW)
				return Board.ResultValue.DRAW_VALUE;
			else {
				Debug.LogError("Unexpected error!");
				throw new ArithmeticException("Unexpected error!");
			}
		}
		return -DoSimulation(board, board.OtherSide(side));
	}

	public int GetMove() {
		int move = -1;
		double max_value = Integer.MIN_VALUE;
		for(int i = 0; i < this.children.size(); ++i) {
			MCST child = this.children.get(i);
			double value = child.total_value * 1.0 / child.total_simulations;
			if (move == -1 || max_value < value) {
				move = child.source_move;
				max_value = value;
			}
		}
		return move;
	}

	/**
	 * Calculate the UCT value (Upper Confidence Bound 1)
	 * <p>
	 * {@code wi/ni + c * sqrt(lnN/ni) }
	 * </p>
	 * 
	 * @param wi total value of node i.
	 * @param ni total simulations of node i
	 * @param c  exploration parameter
	 * @param N  total simulations of parent
	 * @return the UCT value.
	 */
	private double UCT(int wi, int ni, int c, int N) {
		return (wi * 1.0 / ni) + c * Math.sqrt(Math.log(N) / ni);
	}
}
