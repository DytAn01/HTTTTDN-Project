
/**
 *
 * @author Hieu PC
 */
import gui.form.frmlogin;
import java.sql.SQLException;
import java.text.ParseException;
public class Convenience_store {

    /**
     * @param args the command line arguments
     * @throws SQLException 
     */
    public static void main(String[] args) throws SQLException, ParseException {
        new frmlogin().setVisible(true);
    }
}
