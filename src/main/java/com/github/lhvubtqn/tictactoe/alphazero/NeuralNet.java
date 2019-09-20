package com.github.lhvubtqn.tictactoe.alphazero;

import java.io.IOException;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import com.github.lhvubtqn.tictactoe.objects.Board;
import com.github.lhvubtqn.tictactoe.utils.Debug;

public class NeuralNet {
	private int epochs;
	private int batch_size;
	
	private ComputationGraph model;
	
	/**
	 * Load existing model.
	 * @param filename name of the file that contains the model.
	 */
	public NeuralNet(String filename) {
		LoadModel(filename);
		this.epochs = 10;
		this.batch_size = 64;
		
	}
	
	/**
	 * Create a new neural network with default parameters.
	 */
	public NeuralNet() {
		this.model = new ComputationGraph(CreateGraphConfiguration(0.001f, 0.3f, 64));
		this.model.init();
		this.epochs = 10;
		this.batch_size = 64;
		
	}
	
	/**
	 * Create a new neural network.
	 * @param learning_rate
	 * @param dropout the drop out rate.
	 * @param epochs the number of epochs when training.
	 * @param batch_size the number of batches each epoch.
	 * @param num_channels the number of channels in network layers.
	 */
	public NeuralNet(double learning_rate, double dropout, int epochs, int batch_size, int num_channels) {
		this.model = new ComputationGraph(CreateGraphConfiguration(learning_rate, dropout, num_channels));
		this.model.init();
		this.batch_size = batch_size;
		this.epochs = epochs;
	}
	
	/**
	 * Add listener to model. To see statistics, access {@link http://localhost:9000}
	 */
	public void ActivateListener() {
	    UIServer uiServer = UIServer.getInstance();
	    StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later
	    uiServer.attach(statsStorage);
	    this.model.setListeners(new StatsListener(statsStorage));
	}
	
	/**
	 * Train current model (In this case: Board dim: 3, Board size: 9, Number of features planes: 3)
	 * @param input {@code INDArray input = Nd4j.create(miniBatchSize, num_feature_planes, board_dim, board_dim)}
	 * @param policy_output {@code INDArray policy_output = Nd4j.create(miniBatchSize, board_size)}
	 * @param value_output {@code INDArray value_output = Nd4j.create(miniBatchSize, 1)}
	 */
	public void Train(INDArray input, INDArray policy_output, INDArray value_output) {
        this.model.fit(new INDArray[] {input}, new INDArray[] {policy_output, value_output});
	}
	
	public INDArray[] Output(INDArray input) {
		return this.model.output(new INDArray[] {input});
	}
	
	public void SaveModel(String filename) {
		try {
			ModelSerializer.writeModel(this.model, "models/" + filename, false);
		} catch (IOException e) {
			Debug.LogError("Error while saving model!");
		}
	}
	
	public void LoadModel(String filename) {
		try {
			this.model = ModelSerializer.restoreComputationGraph("models/" + filename, false);
		} catch (IOException e) {
			Debug.LogError("Error while loading model!");
		}
	}
	
	/**
	 * Create a computation graph configuration with given parameters.
	 * @param learning_rate
	 * @param dropout
	 * @param num_channels
	 * @return
	 */
	private ComputationGraphConfiguration CreateGraphConfiguration(double learning_rate, double dropout, int num_channels) {
		ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
				.updater(new Adam(learning_rate))
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.weightInit(WeightInit.LECUN_NORMAL)
		        .graphBuilder().setInputTypes(InputType.convolutional(Board.BOARD_DIM, Board.BOARD_DIM, 3))
		        .addInputs("input_layer")
		        .addLayer("convolutional_layer_1", new ConvolutionLayer.Builder()
		        		.kernelSize(1, 1)
		        		.stride(1, 1)
		        		.activation(Activation.RELU)
		        		.nIn(3)
		        		.nOut(num_channels)  
		        		.build(), "input_layer")
		        .addLayer("batch_norm_1", new BatchNormalization.Builder()
		        		.nOut(num_channels)
		        		.build(), "convolutional_layer_1")
		        .addLayer("convolutional_layer_2", new ConvolutionLayer.Builder()
		        		.kernelSize(1, 1)
		        		.stride(1, 1)
		        		.activation(Activation.RELU)
		        		.nIn(3)
		        		.nOut(num_channels)  
		        		.build(), "batch_norm_1")
		        .addLayer("batch_norm_2", new BatchNormalization.Builder()
		        		.nOut(num_channels)
		        		.build(), "convolutional_layer_2")
		        .addLayer("convolutional_layer_3", new ConvolutionLayer.Builder()
		        		.kernelSize(1, 1)
		        		.stride(1, 1)
		        		.activation(Activation.RELU)
		        		.nIn(3)
		        		.nOut(num_channels)  
		        		.build(), "batch_norm_2")
		        .addLayer("batch_norm_3", new BatchNormalization.Builder()
		        		.nOut(num_channels)
		        		.build(), "convolutional_layer_3")
		        .addLayer("convolutional_layer_4", new ConvolutionLayer.Builder()
		        		.kernelSize(1, 1)
		        		.stride(1, 1)
		        		.activation(Activation.RELU)
		        		.nIn(3)
		        		.nOut(num_channels)  
		        		.build(), "batch_norm_3")
		        .addLayer("batch_norm_4", new BatchNormalization.Builder()
		        		.nOut(num_channels)
		        		.build(), "convolutional_layer_4")
		        .addLayer("dense_layer_1", new DenseLayer.Builder()
		        		.activation(Activation.RELU)
		        		.dropOut(dropout)
		        		.nOut(64).build(), "batch_norm_4")
		        .addLayer("batch_norm_5", new BatchNormalization.Builder()
		        		.nOut(num_channels)
		        		.build(), "dense_layer_1")
		        .addLayer("dense_layer_2", new DenseLayer.Builder()
		        		.activation(Activation.RELU)
		        		.dropOut(dropout)
		        		.nOut(32).build(), "batch_norm_5")
		        .addLayer("batch_norm_6", new BatchNormalization.Builder()
		        		.nOut(num_channels)
		        		.build(), "dense_layer_2")
		        .addLayer("output_layer_pi", new OutputLayer.Builder()
		        		.lossFunction(LossFunction.XENT)
		        		.activation(Activation.SOFTMAX)
		                .nOut(Board.BOARD_SIZE)
		                .build(), "batch_norm_6")
		        .addLayer("output_layer_v", new OutputLayer.Builder()
		        		.lossFunction(LossFunction.MSE)
		        		.activation(Activation.TANH)
		                .nOut(1)
		                .build(), "batch_norm_6")
		        .setOutputs("output_layer_pi","output_layer_v")
		        .build();
		return conf;
	}
}
