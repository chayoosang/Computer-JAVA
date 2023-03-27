import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Loader {
	//private FileManger fileManger;
	private Memory memory;
	private Exe exe;
	private CPU cpu;
	private Process process;
	
	private short currentAddress;
	private short[] register;
	private short[] codeList;
	
	public Loader(CPU cpu, Memory memory) {
		this.cpu = cpu;
		this.memory = memory;
	}

	public void loadProcess(String fileName) {
		try {
		this.exe = new Exe();
		Scanner scanner = new Scanner(new File("./exe/"+fileName));
		this.exe.load(scanner);
		scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			}
	}
	
	public void createPorcess() {
		this.register = new short[4];
		this.register[0] = this.exe.getSzCodeSegment();
		this.register[1] = this.exe.getSzDataSegment();
		this.register[2] = this.exe.getSzStackSegment();
		this.register[3] = this.exe.getSzHeapSegment();
		
		this.codeList = this.exe.getCodeList();
		
		this.process = new Process(register, codeList, cpu);
	}
	
	public void storeProcess() {
		this.currentAddress = this.memory.allocate(this.register[0]+this.register[1]+4);
		
		for(int i=0; i<register.length; i++) {
		this.memory.store(this.currentAddress++, this.register[i]);
		}
		
		int i=0;
		while(this.codeList[i]!=0) {
			this.memory.store(this.currentAddress++, this.codeList[i++]);
		}	
		System.out.println("load process complete");
	}
	
	
	
	
	
}
