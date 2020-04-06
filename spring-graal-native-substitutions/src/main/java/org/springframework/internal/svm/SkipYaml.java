package org.springframework.internal.svm;

import java.util.function.BooleanSupplier;

public class SkipYaml implements BooleanSupplier {

	@Override
	public boolean getAsBoolean() {
		return Boolean.valueOf(System.getProperty("spring.graal.remove-yaml-support", "false"));
	}

}
