package com.github.lhvubtqn.tictactoe.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.github.lhvubtqn.tictactoe.utils.Debug;


public class SimpleNNQPlayer extends Player {
	// Constants
	public static final float WIN_VALUE = 1;
	public static final float DRAW_VALUE = 0;
	public static final float LOSS_VALUE = -1;
	
	// Variables
	private Random rand;
	private int side;
	private float discount_factor;
	private float random_move_prob;
	private float random_move_decrease;
	private MultiLayerNetwork q_values;
	private ArrayList<INDArray> state_log;
	private ArrayList<INDArray> qvalues_log;
	private ArrayList<Integer> action_log;
	private float final_result;
	
	public SimpleNNQPlayer(float learning_rate, float discount_factor, float random_move_prob, float random_move_decrease) {
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(1234)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .updater(new Adam(learning_rate))
            .l2(1e-4)
            .weightInit(WeightInit.XAVIER)
            .list()
            .layer(0, new ConvolutionLayer.Builder(1, 1)
            	.name("con1")
        		.stride(1, 1)
        		.nIn(3)
        		.nOut(9)
        		.activation(Activation.RELU)
        		.build())
            .layer(1, new ConvolutionLayer.Builder(1, 1)
            	.name("con2")
        		.stride(2, 2)
        		.nOut(18)
        		.activation(Activation.RELU)
        		.build())
            .layer(2, new DenseLayer.Builder()
        		.name("den")
                .nOut(27) 
                .activation(Activation.RELU) 
                .build())
            .layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
            	.name("out")
                .nOut(Board.BOARD_SIZE)
                .build())
            .setInputType(InputType.convolutionalFlat(Board.BOARD_DIM, Board.BOARD_DIM, 3))
            .build();
					
		this.q_values = new MultiLayerNetwork(conf);
		this.q_values.init();
		this.discount_factor = discount_factor;
		this.random_move_decrease = random_move_decrease;
		this.random_move_prob = random_move_prob;
		this.rand = new Random();
		
		//Initialize the user interface backend
	    UIServer uiServer = UIServer.getInstance();

	    //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
	    StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

	    //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
	    uiServer.attach(statsStorage);

	    //Then add the StatsListener to collect this information from the network, as it trains
	    this.q_values.setListeners(new StatsListener(statsStorage));
	}
	
	@Override
	public int Move(Board board) {
		
		INDArray nn_input = BoardToNNInput(board);
		this.state_log.add(nn_input);
		float[] qvalues = this.q_values.output(nn_input).toFloatVector();
		
		int move = -1;
		for(int i = 0; i < Board.BOARD_SIZE; ++i) 
			if (!board.IsValid(i))
				qvalues[i] = -1;
		
		if (board.num_empty == 1) {
			for (int i = 0; i < Board.BOARD_SIZE; ++i) {
				if (board.IsValid(i))  {
					move = i;
					break;
				}
			}
		} else {
			if (this.rand.nextFloat() < this.random_move_prob) {
				int next_move = rand.nextInt(board.num_empty) + 1;
				for(int i = 0; i < Board.BOARD_SIZE; ++i) {
					if (board.IsValid(i)) {
						next_move--;
						if (next_move == 0) {
							move = i;
							break;
						}
					}
				}
			}
			else {
				for(int i = 0; i < Board.BOARD_SIZE; ++i) {
					if (qvalues[i] != -1 && (move == -1 || qvalues[move] < qvalues[i]))
						move = i;
				}
			}
		}
		
		this.action_log.add(move);
		this.qvalues_log.add(Nd4j.create(qvalues, new int[] {1, Board.BOARD_SIZE}));
		return board.Move(move, side);
	}
	
	public INDArray BoardToNNInput(Board board) {
		float[] res = new float[Board.BOARD_SIZE * 3];
		for(int i = 0; i < Board.BOARD_SIZE * 3; ++i)
			res[i] = 0;
		
		for(int i = 0; i < Board.BOARD_SIZE; ++i)
		{
			if (board.state[i] == this.side) 
				res[i] = 1.0f;
			else if (board.state[i] == board.OtherSide(this.side)) 
				res[Board.BOARD_SIZE + i] = 1.0f;
			else
				res[2 * Board.BOARD_SIZE + i] = 1.0f;
		}
		return Nd4j.create(res, new int[] {1, Board.BOARD_SIZE * 3});
	}
	
	public ArrayList<INDArray> CalculateTargets() {
		ArrayList<INDArray> targets = new ArrayList<INDArray>();
		
		float next_max = -1;
		for(int i = this.qvalues_log.size() - 1; i >= 0; --i) {
			float[] target = qvalues_log.get(i).toFloatVector();
			
			if (next_max == -1)
				target[this.action_log.get(i)] = this.final_result;
			else
				target[this.action_log.get(i)] = this.discount_factor * next_max;
			
			for(int j = 0; j < Board.BOARD_SIZE; ++j)
				next_max = Math.max(next_max, target[j]);
			targets.add(Nd4j.create(target, new int[] {1, Board.BOARD_SIZE}));
		}
		return targets;
	}

	@Override
	public void Learn(int final_result) {
		float final_value = DRAW_VALUE;
		switch (final_result) {
			case Board.GameResult.NAUGHT_WIN:
				final_value = (side == Board.Field.NAUGHT) ? WIN_VALUE : LOSS_VALUE;
				break;
			case Board.GameResult.CROSS_WIN:
				final_value = (side == Board.Field.CROSS) ? WIN_VALUE : LOSS_VALUE;
				break;
			case Board.GameResult.DRAW:
				final_value = DRAW_VALUE;
				break;
			default:
				Debug.LogError("Unexpected error!");
				throw new ArithmeticException("Unexpected error!");
		}
		this.final_result = final_value;
		
		ArrayList<INDArray> targets = CalculateTargets();
		for(int i = targets.size() - 1; i >= 0; --i) {
			this.q_values.fit(this.state_log.get(i), targets.get(targets.size() - 1 - i));
		}
		this.random_move_prob *= this.random_move_decrease;
	}

	@Override
	public void NewGame(int side) {
		this.side = side;
		this.state_log = new ArrayList<INDArray>();
		this.qvalues_log = new ArrayList<INDArray>();
		this.action_log = new ArrayList<Integer>();
		this.final_result = 0;
	}

	@Override
	public void WriteData() {
		try {
			ModelSerializer.writeModel(this.q_values, "models/simple_nn_qplayer", false);
		} catch (IOException e) {
			Debug.LogError("Error while saving model!");
		}
	}

	@Override
	public void LoadData() {
		try {
			this.q_values = ModelSerializer.restoreMultiLayerNetwork("models/simple_nn_qplayer", false);
		} catch (IOException e) {
			Debug.LogError("Error while loading model!");
		}
	}
}
