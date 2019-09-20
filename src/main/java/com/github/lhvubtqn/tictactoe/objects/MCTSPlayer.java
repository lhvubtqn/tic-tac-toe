package com.github.lhvubtqn.tictactoe.objects;

public class MCTSPlayer extends Player {

	/* Variables */
	int side;
	int iterations;

	public MCTSPlayer(int iterations) {
		this.iterations = iterations;
	}

	@Override
	public int Move(Board board) {
		MCST mcst = new MCST(board.Clone(), -1, this.side, true);
		for(int iter = 0; iter < this.iterations; ++iter) {
			mcst.DoIteration();
		}
		mcst.DoIteration();
		return board.Move(mcst.GetMove(), this.side);
	}

	@Override
	public void Learn(int final_result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void NewGame(int side) {
		this.side = side;
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
