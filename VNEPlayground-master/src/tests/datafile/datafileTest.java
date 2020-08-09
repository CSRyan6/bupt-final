package tests.datafile;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GlpkException;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_tran;
import vnreal.core.Consts;

//import static vnreal.algorithms.utils.LpSolver.initialize;

/**
 * Created by IntelliJ IDEA.
 * User: CalvonBai
 * Date: 2019/9/9
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class datafileTest {
    private static void test() {
        String dataFileName = Consts.LP_SOLVER_DATAFILE
                + Integer.toString(1812) + ".dat";
//        initialize();
        glp_prob lp = null;
        glp_tran tran;
        int ret;
        try {
            lp = GLPK.glp_create_prob();
            tran = GLPK.glp_mpl_alloc_wksp();

            ret = GLPK.glp_mpl_read_model(tran, Consts.LP_SOLVER_FOLDER + Consts.LP_VINEYARDNODEMAPPING_MODEL, 1);
            if (ret != 0) {
                GLPK.glp_mpl_free_wksp(tran);
                GLPK.glp_delete_prob(lp);
                throw new RuntimeException("Errorcode: " + ret + " - Data file not found: " + Consts.LP_SOLVER_FOLDER
                        + dataFileName + "\n(Maybe there's problems with the locale?)");
            }
            ret = GLPK.glp_mpl_read_data(tran, Consts.LP_SOLVER_FOLDER + dataFileName);
            if (ret != 0) {
                GLPK.glp_mpl_free_wksp(tran);
                GLPK.glp_delete_prob(lp);
                throw new RuntimeException("Errorcode: " + ret + " - Data file not found: " + Consts.LP_SOLVER_FOLDER
                        + dataFileName + "\n(Maybe there's problems with the locale?)");
            }

            // generate model
            GLPK.glp_mpl_generate(tran, null);
            GLPK.glp_mpl_build_prob(tran, lp);

            GLPK.glp_mpl_free_wksp(tran);
            GLPK.glp_delete_prob(lp);


        }catch (GlpkException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args){
        // System.out.println(System.getProperty("java.library.path"));

        test();
    }






}
