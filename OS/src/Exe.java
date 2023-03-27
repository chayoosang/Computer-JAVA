import java.util.Scanner;

public class Exe {


	private short szCodeSegment;
	private short szDataSegment;
	private short szStackSegment;
	private short szHeapSegment;

	private short codeList[];


	public short getSzCodeSegment() {
		return szCodeSegment;
	}
	public short getSzDataSegment() {
		return szDataSegment;
	}
	public short getSzStackSegment() {
		return szStackSegment;
	}
	public short getSzHeapSegment() {
		return szHeapSegment;
	}
	public short[] getCodeList() {
		return codeList;
	}

	public void load(Scanner scanner) {
		this.loadHeader(scanner);
		this.loadBody(scanner);
	}

	private void loadHeader(Scanner scanner) {
		this.szCodeSegment = scanner.nextShort(16);
		this.szDataSegment = scanner.nextShort(16);
		this.szStackSegment = (short) 12;
		this.szHeapSegment = (short) 12;
	}

	private void loadBody(Scanner scanner) {
		this.codeList = new short[this.szCodeSegment];
		int i = 0;
		while(scanner.hasNext()) {
			this.codeList[i++] = scanner.nextShort(16);
		}
	}
}
