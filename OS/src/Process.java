
public class Process {
	public enum EState{
		eReady,
		eRunning,
		eWaiting
	}
	private class PCB{
		private EState eState;
		private int id;
		private CPU.Register registers[];
		
		
		public PCB(short[] register, int id, CPU cpu) {
			this.eState = EState.eReady;
			
			this.registers = cpu.registers;
			
			this.registers[CPU.ERegisters.eCS.ordinal()].setValue(register[0]);
			this.registers[CPU.ERegisters.eDS.ordinal()].setValue(register[1]);
			this.registers[CPU.ERegisters.eSS.ordinal()].setValue(register[2]);
			this.registers[CPU.ERegisters.eHS.ordinal()].setValue(register[3]);
			
			this.id = id;
		}
	}
	
	private class CodeSegment{
		private short codeList[];
		
		public CodeSegment(short[] codeList) {
			this.codeList = codeList;
		}

	}
	
	private int count = 1;
	
	public Process(short[] register, short[] codeList, CPU cpu) {
		PCB pcb = new PCB(register, count, cpu);
		CodeSegment codeSegment = new CodeSegment(codeList);
		count++;
	}
	
	
	

}
