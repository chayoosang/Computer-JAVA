public class CPU {
	
	public enum ERegisters{
		eIR,
		eAC,
		
		eCS,
		eDS,
		eSS,
		eHS,
		
		
		ePC,
		eCP,
		eSP,

		eSR,
		eIN, //intterupt
		
		eMAR,
		eMBR
	}
	
	public class Register{
		protected short value;
		public short getValue() {return this.value;}
		public void setValue(short value) {this.value=value;}
	}
	class IR extends Register{
		public short getOpCode() {return (short) (this.value >> 8); }
		public short getOperand() {return (short) (this.value & 0x00FF); }
	}
	
	private enum EOpcode{
		eHALT,
		eLDC,
		eLDA,
		eSTA,
		eADDA,
		eADDC,
		eSUBA,
		eSUBC,
		eMULA,
		eDIVA,
		eDIVC,
		eANDA,
		eNOTA,
		eJMPZ,
		eJMPBZ,
		eJMPBZEQ,
		eJMP
	}
	

	private class CU{		
		public boolean isZero(Register sr) {
			if((sr.getValue() & 0x8000) == 0)
				return false;
			else 
				return true;
		}
		public boolean isBelowZero(Register sr) {
			if((sr.getValue() & 0x4000) == 0)
				return false;
			else 
				return true;
		}
	}
	
	private class ALU{
		
		private short storage;

		public void store(short ac) {
			this.storage = ac;
		}
		public void add(short ac) {
			registers[ERegisters.eAC.ordinal()].setValue((short)(this.storage + ac));
		}
		public void subtract(short ac) {
			registers[ERegisters.eAC.ordinal()].setValue((short)(this.storage - ac));
			
			if(registers[ERegisters.eAC.ordinal()].getValue() < 0)
				registers[ERegisters.eSR.ordinal()].setValue((short) 0x4000);
			else if(registers[ERegisters.eAC.ordinal()].getValue() == 0)
				registers[ERegisters.eSR.ordinal()].setValue((short) 0x8000);
			else
				registers[ERegisters.eSR.ordinal()].setValue((short) 0x0000);
		}
		public void multiply(short ac) {
			registers[ERegisters.eAC.ordinal()].setValue((short)(this.storage * ac));
		}
		public void division(short ac) {
			registers[ERegisters.eAC.ordinal()].setValue((short)(this.storage / ac));		
		}
		public void andOperator(short ac) {
			registers[ERegisters.eAC.ordinal()].setValue((short)(this.storage & ac));	
		}
		public void notOperator(short ac) {
			registers[ERegisters.eAC.ordinal()].setValue((short)(~ac));
		}
	
		
		
	}
	

	//components
	private CU cu;
	private ALU alu;
	public Register registers[];
	
	
	//associations
	private Memory memory;
	
	
	// status
	private boolean bPowerOn;
	private boolean isPowerOn() {return this.bPowerOn;}
	public void setPowerOn() {
		this.bPowerOn = true;
		this.run();
		}
	public void shutDown() {this.bPowerOn = false;}
	
	// constructor
	public CPU() {
		this.registers = new Register[ERegisters.values().length];
		for(int i=0; i<ERegisters.values().length; i++)
			if(i==4)
				this.registers[i] = new IR();
			else
			this.registers[i] = new Register();
		this.alu = new ALU();
		this.cu = new CU();
	}
	
	public void associate(Memory memory) {
		this.memory = memory;
		}
	
	public void setPC(short pc) {
		this.registers[ERegisters.ePC.ordinal()].setValue(pc);
		this.registers[ERegisters.eCP.ordinal()].setValue(pc);
	}
	public void setSP(short sp) {
		this.registers[ERegisters.eSP.ordinal()].setValue(sp);
	}
	
	
	private void HALT() {
	}
	private void LDC() {
		// IR.operand -> MBR
		// MBR -> AC
		this.registers[ERegisters.eMBR.ordinal()].setValue(((IR) (this.registers[ERegisters.eIR.ordinal()])).getOperand());
		this.registers[ERegisters.eAC.ordinal()].setValue(this.registers[ERegisters.eMBR.ordinal()].getValue());
	}
    private void LDA() {
		this.registers[ERegisters.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegisters.eMAR.ordinal()].getValue()));
		this.registers[ERegisters.eAC.ordinal()].setValue(this.registers[ERegisters.eMBR.ordinal()].getValue());
	}
	private void STA() {
		this.registers[ERegisters.eMBR.ordinal()].setValue(this.registers[ERegisters.eAC.ordinal()].getValue());
		this.memory.store(
				this.registers[ERegisters.eMAR.ordinal()].getValue(), 
				this.registers[ERegisters.eMBR.ordinal()].getValue());
	}
	private void ADDA() {
		this.alu.store(this.registers[ERegisters.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.add(this.registers[ERegisters.eAC.ordinal()].getValue());
	}
	private void ADDC() {
		this.alu.store(this.registers[ERegisters.eAC.ordinal()].getValue());
		this.LDC();
		this.alu.add(this.registers[ERegisters.eAC.ordinal()].getValue());
	}
	private void SUBA() {
		this.alu.store(this.registers[ERegisters.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.subtract(this.registers[ERegisters.eAC.ordinal()].getValue());
	}
	private void SUBC() {
		this.alu.store(this.registers[ERegisters.eAC.ordinal()].getValue());
		this.LDC();
		this.alu.subtract(this.registers[ERegisters.eAC.ordinal()].getValue());
	}
	private void MULA() {
		this.alu.store(this.registers[ERegisters.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.multiply(this.registers[ERegisters.eAC.ordinal()].getValue());
	}
	private void DIVA() {
		this.alu.store(this.registers[ERegisters.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.division(this.registers[ERegisters.eAC.ordinal()].getValue());
	}
	private void DIVC() {
		this.alu.store(this.registers[ERegisters.eAC.ordinal()].getValue());
		this.LDC();
		this.alu.division(this.registers[ERegisters.eAC.ordinal()].getValue());
	}
	private void ANDA() {
		this.alu.store(this.registers[ERegisters.eAC.ordinal()].getValue());
		this.LDA();
		this.alu.andOperator(this.registers[ERegisters.eAC.ordinal()].getValue());
	}
	private void NOTA() {
		this.alu.notOperator(this.registers[ERegisters.eAC.ordinal()].getValue());
	}
	private void JMPZ() {
		if(cu.isZero(this.registers[ERegisters.eSR.ordinal()])) {
			this.registers[ERegisters.ePC.ordinal()].setValue(
					(short)(((IR) (this.registers[ERegisters.eIR.ordinal()])).getOperand() + this.registers[ERegisters.eCP.ordinal()].getValue() ));
		}
		else
			this.addPC();
	}
	private void JMPBZ() {
		if(cu.isBelowZero(this.registers[ERegisters.eSR.ordinal()])) {
			this.registers[ERegisters.ePC.ordinal()].setValue(
					(short)(((IR) (this.registers[ERegisters.eIR.ordinal()])).getOperand() + this.registers[ERegisters.eCP.ordinal()].getValue()));
		}
		else
			this.addPC();
	}
	private void JMPBZEQ() {
		if(cu.isBelowZero(this.registers[ERegisters.eSR.ordinal()])||cu.isZero(this.registers[ERegisters.eSR.ordinal()])) {
			this.registers[ERegisters.ePC.ordinal()].setValue(
					(short)(((IR) (this.registers[ERegisters.eIR.ordinal()])).getOperand() + this.registers[ERegisters.eCP.ordinal()].getValue()));
		}
		else
			this.addPC();
	}
	private void JMP() {
		this.registers[ERegisters.ePC.ordinal()].setValue(
				(short)(((IR) (this.registers[ERegisters.eIR.ordinal()])).getOperand() + this.registers[ERegisters.eCP.ordinal()].getValue()));
	}
	private void addPC() {
		this.registers[ERegisters.ePC.ordinal()].setValue((short)(this.registers[ERegisters.ePC.ordinal()].getValue()+2));		
	}
		
	private void fetch() {
		// pc-> mar  mbr -> ir
		this.registers[ERegisters.eMAR.ordinal()].setValue(this.registers[ERegisters.ePC.ordinal()].getValue());
		this.registers[ERegisters.eMBR.ordinal()].setValue(this.memory.load(this.registers[ERegisters.eMAR.ordinal()].getValue()));
		this.registers[ERegisters.eIR.ordinal()].setValue(this.registers[ERegisters.eMBR.ordinal()].getValue());
	}
	
	private void decode() {
		// operand啊 林家老 版快父 IR.operand -> MAR
		this.registers[ERegisters.eMAR.ordinal()].setValue(
				(short)
				(((IR) this.registers[ERegisters.eIR.ordinal()]).getOperand()
						+ this.registers[ERegisters.eSP.ordinal()].getValue()));
	}
	
	private void execute() {
		EOpcode[] opcode = EOpcode.values();
		switch(opcode[((IR)this.registers[ERegisters.eIR.ordinal()]).getOpCode()]) { 
		case eHALT:
			this.HALT();
			break;
		case eLDC:
			this.LDC();
			this.addPC();
			break;
		case eLDA:
			this.LDA();
			this.addPC();
			break;
		case eSTA:
			this.STA();
			this.addPC();
			break;
		case eADDA:
			this.ADDA();
			this.addPC();
			break;
		case eADDC:
			this.ADDC();
			this.addPC();
			break;
		case eSUBA:
			this.SUBA();
			this.addPC();
			break;
		case eSUBC:
			this.SUBC();
			this.addPC();
			break;
		case eMULA:
			this.MULA();
			this.addPC();
			break;
		case eDIVA:
			this.DIVA();
			this.addPC();
			break;
		case eDIVC:
			this.DIVC();
			this.addPC();
			break;
		case eANDA:
			this.ANDA();
			this.addPC();
			break;
		case eNOTA:
			this.NOTA();
			this.addPC();
			break;
		case eJMPZ:
			this.JMPZ();
			break;
		case eJMPBZ:
			this.JMPBZ();
			break;
		case eJMPBZEQ:
			this.JMPBZEQ();
			break;
		case eJMP:
			this.JMP();
			break;
		default:
			break;
		}
	}
	
	private void checkInterrupt(){
		
	}
	public void run() {
		while(this.isPowerOn()) {
		this.fetch();
		this.decode();
		this.execute();
		this.checkInterrupt();
		}
	}
	

}
