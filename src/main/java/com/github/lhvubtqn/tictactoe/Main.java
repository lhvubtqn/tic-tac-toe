package com.github.lhvubtqn.tictactoe;

import java.io.IOException;

import com.github.lhvubtqn.tictactoe.objects.MCTSPlayer;
import com.github.lhvubtqn.tictactoe.objects.MinimaxPlayer;
import com.github.lhvubtqn.tictactoe.objects.Player;
import com.github.lhvubtqn.tictactoe.objects.RandomPlayer;
import com.github.lhvubtqn.tictactoe.objects.SimpleNNQPlayer;
import com.github.lhvubtqn.tictactoe.objects.TabularQPlayer;
import com.github.lhvubtqn.tictactoe.objects.UserPlayer;
import com.github.lhvubtqn.tictactoe.utils.Debug;
import com.github.lhvubtqn.tictactoe.utils.Utils;

public class Main {

	public static void main(String[] args) throws IOException {
		Debug.isTest = true;
		
		Player mcst = new MCTSPlayer(10000);
		Player random = new RandomPlayer();
		Player user = new UserPlayer();
		Player minimax = new MinimaxPlayer();
		Player table_q = new TabularQPlayer(0.9f, 0.95f, 0.6f);
		table_q.LoadData();
		
//		Player nn_q1 = new SimpleNNQPlayer(0.05f, 0.95f, 0.0f, 0.99f);
//		Player nn_q2 = new SimpleNNQPlayer(0.9f, 0.95f, 0.0f, 0.99f);
//		nn_q1.LoadData();
//		Utils.Train(minimax, nn_q1, 100, 50);
//		Utils.Train(nn_q1, minimax, 100, 50);
		Utils.Battle(user, mcst, 10, false);
//		Utils.Battle(mcst, random, 10, false);

//		nn_q1.WriteData();
	}

}
