package com.github.lhvubtqn.tictactoe.utils;

public class Debug {
	public static boolean isTest = true;
	public static void LogInline(String s) {
		if(isTest)
			System.out.println(s);
	}
	public static void Log(String s) {
		if(isTest)
			System.out.println(s);
	}
	public static void LogError(String s) {
		if(isTest)
			System.err.println(s);
	}
}
