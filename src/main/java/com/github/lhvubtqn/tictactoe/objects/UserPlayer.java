package com.github.lhvubtqn.tictactoe.objects;

import java.util.Scanner;

public class UserPlayer extends Player {
	
	int side;
	Scanner input;
	
	public UserPlayer() {
		input = new Scanner(System.in);
	}
	
	@Override
	public int Move(Board board) {
		System.out.print("----> Your move (x, y): ");
		int x = input.nextInt();
		int y = input.nextInt();
		
		int move = x * 3 + y;
		if (board.IsValid(move))
			return board.Move(move, side);
		
		System.out.println("Invalid move, type again!");
		return Move(board);
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
