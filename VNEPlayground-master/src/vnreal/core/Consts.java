package vnreal.core;

import java.io.File;
import java.nio.file.Paths;

/**
Collected constants of general utility.

<P>All members of this class are immutable.

 Class extracted from:
 
 http://www.javapractices.com/topic/TopicAction.do?Id=2
 
 Modified by:

@author Juan Felipe Botero
@author Andreas Fischer
@since 27-04-2011 
*/
public final class Consts  {

	/** 
	 * Useful for String operations, which return an index of <tt>-1</tt> when 
	 * an item is not found. 
	 */
	public static final int NOT_FOUND = -1;

	/** System property - <tt>line.separator</tt>*/
	public static final String NEW_LINE = System.getProperty("line.separator");
	/** System property - <tt>file.separator</tt>*/
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");

	public static final String ALEVIN_DIR = Paths.get("").toAbsolutePath().toString(); // Assume pwd is basedir
	public static final String RESULTS_DIR = ALEVIN_DIR + File.separator  + "results";
	public static final String SYSINFOTOOL = ALEVIN_DIR + File.separator  + "tools" + File.separator + "sysinfo.sh";
	public static final String EXPERIMENT_XML = ALEVIN_DIR + File.separator + "src" + File.separator + "XML" + File.separator + "Experiment.xsd";

	// LP solver files folder
	public static String LP_SOLVER_FOLDER = ALEVIN_DIR + File.separator + "ILP-LP-Models" + File.separator;
	public static String LP_SOLVER_DATAFILE = "datafile";
	public static String LP_VINEYARDNODEMAPPING_MODEL = "ViNEYard-CNLM-LP.mod";
	public static String LP_VINEYARDLINKMAPPINGMCF_MODEL = "ViNEYard-MCF.mod";
	public static String ILP_EXACTMAPPING_MODEL = "VNE-ExactMIP.mod";


	// PRIVATE //

	/**
	 * The caller references the constants using <tt>Consts.EMPTY_STRING</tt>, 
	 * and so on. Thus, the caller should be prevented from constructing objects of 
	 * this class, by declaring this private constructor. 
	 */
	private Consts(){
		//this prevents even the native class from 
		//calling this actor as well :
		throw new AssertionError();
	}
}