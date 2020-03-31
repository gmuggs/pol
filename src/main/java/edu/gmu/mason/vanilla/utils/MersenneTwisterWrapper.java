package edu.gmu.mason.vanilla.utils;

import org.apache.commons.math3.random.RandomGenerator;

import ec.util.MersenneTwisterFast;

/**
 * General description_________________________________________________________
 * A class for wrapping MersenneTwister object
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class MersenneTwisterWrapper implements RandomGenerator, java.io.Serializable {	
	private static final long serialVersionUID = 2422924466992151756L;
	MersenneTwisterFast random;
	
	public MersenneTwisterWrapper(MersenneTwisterFast random) {
		this.random = random;
	}

	@Override
	public void setSeed(int seed) {
		random.setSeed(seed);
	}

	@Override
	public void setSeed(int[] seed) {
		random.setSeed(seed);
	}

	@Override
	public void setSeed(long seed) {
		random.setSeed(seed);		
	}

	@Override
	public void nextBytes(byte[] bytes) {
		random.nextBytes(bytes);
	}

	@Override
	public int nextInt() {
		return random.nextInt();
	}

	@Override
	public int nextInt(int n) {
		return random.nextInt(n);
	}

	@Override
	public long nextLong() {
		return random.nextLong();
	}

	@Override
	public boolean nextBoolean() {
		return random.nextBoolean();
	}

	@Override
	public float nextFloat() {
		return random.nextFloat();
	}

	@Override
	public double nextDouble() {
		return random.nextDouble();
	}

	@Override
	public double nextGaussian() {
		return random.nextGaussian();
	}
	
	
	public MersenneTwisterFast getRandom() {
		return random;
	}
}
