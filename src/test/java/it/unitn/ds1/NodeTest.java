package it.unitn.ds1;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

/**
 * Tests for @{@link Node}.
 *
 * @author Davide Pedranz
 */
public class NodeTest {

	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	@Test
	public void wrongCommand() {
		exit.expectSystemExitWithStatus(2);
		Node.main(new String[]{"wrong"});
	}

	@Test
	public void joinWrongParametersNumber() {
		exit.expectSystemExitWithStatus(2);
		Node.main(new String[]{"join", "xyz"});
	}

	@Test
	public void joinWrongParameters() {
		exit.expectSystemExitWithStatus(2);
		Node.main(new String[]{"join", "xyz", "124"});
	}

	@Test
	public void recoverWrongParametersNumber() {
		exit.expectSystemExitWithStatus(2);
		Node.main(new String[]{"recover", "123.232.123.45"});
	}

	@Test
	public void recoverWrongParameters() {
		exit.expectSystemExitWithStatus(2);
		Node.main(new String[]{"recover", "333.333.333.333", "12312"});
	}

}
