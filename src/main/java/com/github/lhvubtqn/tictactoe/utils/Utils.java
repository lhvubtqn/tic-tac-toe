package com.github.lhvubtqn.tictactoe.utils;

import com.github.lhvubtqn.tictactoe.objects.Board;
import com.github.lhvubtqn.tictactoe.objects.Player;

public class Utils {
	public static int PlayGame(Board board, Player player1, Player player2, boolean silent) {
		player1.NewGame(Board.Field.CROSS);
		player2.NewGame(Board.Field.NAUGHT);
		board.Reset();
		int final_result = 0;
		
		boolean finished = false;
		while (!finished) {
			int result = player1.Move(board);
			if (!silent)
				board.PrintBoard();
			switch (result) {
				case Board.GameResult.FINISHED:
					final_result = Board.GameResult.CROSS_WIN;
					finished = true;
					break;
				case Board.GameResult.DRAW:
					final_result = Board.GameResult.DRAW;
					finished = true;
					break;
				case Board.GameResult.UNFINISHED:	
					result = player2.Move(board);
					if (!silent)
						board.PrintBoard();
					switch (result) {
						case Board.GameResult.FINISHED:
							final_result = Board.GameResult.NAUGHT_WIN;
							finished = true;
							break;
						case Board.GameResult.DRAW:
							final_result = Board.GameResult.DRAW;
							finished = true;
							break;	
						case Board.GameResult.UNFINISHED:
							break;
						default:
							throw new ArithmeticException("Unexpected error!");
					}
					break;
				default:
					throw new ArithmeticException("Unexpected error!");
			}
		}
		
		if (!silent) {
			switch (final_result) {
				case Board.GameResult.CROSS_WIN:
					System.out.println("----> Player 1 has won!");
					break;
				case Board.GameResult.NAUGHT_WIN:
					System.out.println("----> Player 2 has won!");
					break;
				default:
					System.out.println("----> Draw!");
					break;
			}
			System.out.println("");
		}
		player1.Learn(final_result);
		player2.Learn(final_result);
		return final_result;
	}
	
	public static int[] Battle(Player player1, Player player2, int num_games, boolean silent) {
		Board board = new Board();
		int draw_count = 0;
		int cross_count = 0;
		int naught_count = 0;
		
		for(int i = 0; i < num_games; ++i) {
			int result = PlayGame(board, player1, player2, silent);
			if (result == Board.GameResult.CROSS_WIN)
				cross_count++;
			else if (result == Board.GameResult.NAUGHT_WIN)
				naught_count++;
			else 
				draw_count++;
		}
		
		System.out.printf("After %d game, we have draws: %d, Player 1 wins: %d, Player 2 wins: %d.\n", 
				num_games, draw_count, cross_count, naught_count);
		System.out.printf("Which gives percentages of draws: %.2f%%, Player 1 wins: %.2f%%, Player 2 wins: %.2f%%.%n", 
				draw_count * 100.0f / num_games, cross_count * 100.0f / num_games, naught_count * 100.0f / num_games);
		
		int result[] = new int[] { draw_count, cross_count, naught_count };
		return result;
	}
	
	public static void Train(Player player1, Player player2, int num_battles, int game_per_battle) {
		int p1_wins = 0;
		int p2_wins = 0;
		int draws = 0;
		
		for(int i = 0; i < num_battles; ++i) {
			int result[] = Battle(player1, player2, game_per_battle, true);
			if (result[1] > result[2]) 
				p1_wins++;
			else if (result[1] < result[2])
				p2_wins++;
			else
				draws++;
		}
		System.out.println("vvvvvvvvvvvvvvvv");
		System.out.printf("After %d battles, we have draws: %d, Player 1 wins: %d, Player 2 wins: %d.\n", 
				num_battles, draws, p1_wins, p2_wins);
		System.out.printf("Which gives percentages of draws: %.2f%%, Player 1 wins: %.2f%%, Player 2 wins: %.2f%%.%n", 
				draws * 100.0f / num_battles, p1_wins * 100.0f / num_battles, p2_wins * 100.0f / num_battles);
		System.out.println("^^^^^^^^^^^^^^^^");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
