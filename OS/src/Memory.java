public class Memory {
	
	private short[] memory;
	private short address = 0;

	public Memory() {
		this.memory = new short[512];
	}
		
	public short load(short mar) {
		return this.memory[mar];
	}

	public void store(short mar, short mbr) {
		this.memory[mar] = mbr;
	}

	public short allocate(int i) {
		this.address += (short) i; 
		return (short ) (address - i);
	}
	

}
