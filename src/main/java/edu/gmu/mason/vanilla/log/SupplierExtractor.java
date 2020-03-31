package edu.gmu.mason.vanilla.log;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * General description_________________________________________________________
 * Supplier extracting class
 * 
 * @author Joon-Seok Kim (jkim258 at gmu.edu)
 * 
 */
public class SupplierExtractor implements Function<Object, Object>, java.io.Serializable {
	private static final long serialVersionUID = -843790912087581049L;

	private Supplier supplier;

	public SupplierExtractor(Supplier supplier) {
		this.supplier = supplier;
	}

	@Override
	public Object apply(Object t) {
		return supplier.get();
	}

}
