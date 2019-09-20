package com.github.lhvubtqn.tictactoe.objects;


public abstract class Player {
	abstract public int Move(Board board);
	abstract public void Learn(int final_result);
	abstract public void NewGame(int side);
	abstract public void WriteData();
	abstract public void LoadData();
}	
