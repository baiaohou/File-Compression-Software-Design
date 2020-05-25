import java.io.IOException;

public class RunMain {

	/**
	 * This main method takes in a file (inFile), compress it as compressedFile,
	 * and then uncompress it as uncompressedFile.
	 */
	public static void main(String[] args) throws IOException {
		/**
		 * CHANGE HERE FOR PROPER DIRECTORY
		 * inFile - the file you are taking in
		 * compressedFile - the compressed output file
		 * uncompressedFile - the uncompressed output
		 * boolean force - true if forcing compress; false if do not compress when expected size is larger
		 */
		String inFile = "/Users/baiaohou/Downloads/CIT 594 master/hw1/FileCompression-Students/src/test.txt";
		String compressedFile = "/Users/baiaohou/Downloads/CIT 594 master/hw1/FileCompression-Students/output.txt";
		String uncompressedFile = "/Users/baiaohou/Downloads/CIT 594 master/hw1/FileCompression-Students/outputttt.txt";
		boolean force = true; // true = compress anyways; false = do not compress if compressed file is bigger
		
		
		// Welcoming message
		System.out.println("Welcome to Baiao's compressor and uncompressor!");
		System.out.println();
		System.out.println("******************************************************");
		System.out.println("******************************************************");
		System.out.println("**************  C O M P R E S S I O N  ***************");
		System.out.println("******************************************************");
		if (force == true) {
			System.out.println("**********  FORCED MODE - COMPRESS ANYWAYS  **********");
		} else {
			System.out.println("******************  UNFORCED  MODE  ******************");
			System.out.println("***** WON'T COMPRESS IF EXPECTED SIZE > ORIG SIZE ****");
		}

		System.out.println();
		System.out.println("File taking in:\n>>>" + inFile);
		System.out.println();
		System.out.println("Target directory for compression: \n>>>" + compressedFile);
		System.out.println();

		
		Huff a = new Huff();
		a.write(inFile, compressedFile, force);
		System.out.println("compression processing ...\n");
		
		// if compression did not happen, flag is set to false, end program
		if (a.flag == false) {
			System.out.println("Expected compressed file is larger than original!\n");
			System.out.println("No compressed file made. Program ends");
			return;
		} 
		
		// else, do uncompress
		System.out.println("******************************************************");
		System.out.println("**************  COMPRESSION SUCCESS!  ****************");
		System.out.println("******************************************************");
		System.out.println();
		System.out.println();
		System.out.println("//////////////////////////////////////////////////////");
		System.out.println("\n\nNow uncompress the file you just created!\n");
		System.out.println("******************************************************");
		System.out.println("******************************************************");
		System.out.println("************  U N C O M P R E S S I O N  *************");
		System.out.println("******************************************************");
		System.out.println("********* uncompress the file you just made **********\n");
		System.out.println("Target directory for uncompression: \n>>>" + uncompressedFile);
		System.out.println();

		BitInputStream bis = new BitInputStream(compressedFile);
		
		String output = "";
		int i = bis.read(1);
		while (i != -1) {
			if (i == 0) output = output + '0';
			if (i == 1) output = output + '1';
			i = bis.read(1);
		}
		BitInputStream bism = new BitInputStream(compressedFile);
		a.uncompress(compressedFile, uncompressedFile);
		System.out.println("uncompression processing ...\n");
		System.out.println("******************************************************");
		System.out.println("*************  UNCOMPRESSION SUCCESS!  ***************");
		System.out.println("******************************************************\n");
		System.out.println("Program ends");
	}

}
