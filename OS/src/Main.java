
public class Main {

	public static void main(String[] args) {
		CPU cpu = new CPU();
		Memory memory = new Memory();
		Loader loader = new Loader(cpu,memory);

		cpu.associate(memory);

		loader.loadProcess("exe1");
		loader.createPorcess();
		loader.storeProcess();
	}


}
