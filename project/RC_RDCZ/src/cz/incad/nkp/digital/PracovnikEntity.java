/* *****************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.incad.nkp.digital;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.dbdef.DbBundles;
import com.amaio.plaant.dbdef.DbListI;
import com.amaio.plaant.dbdef.ListValue;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.nkp.digital.constants.CnkpPracovnik;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class PracovnikEntity extends RecordEntity implements DbListI, CnkpPracovnik {

    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.SYSTEM_APPLICATION;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {};
    private static final String SECURITY_POLE_HLIDANA[] = {};
    private static final String SELECT_VALUES_SQL = "SELECT IDCISLO,JMENO,PRIJMENI FROM PRACOVNIK ORDER BY PRIJMENI";
    private boolean alphabetize = false;
    private String name = null;

//    /***************************************************************************
//     * Vrátí metadata vstupní agendy
//     * @param deskName
//     * @return
//     */
//    public Metadata getMetadata(String deskName) {
//        //FIX v tuhle chvili jeste nemam context, musim to udelat pres SQL
//        ReliefLogger.severe(getTriggerContext().toString());
//        GenericFactory fact = getTriggerContext().getFactory(Metadata.class);
//        return (Metadata) fact.create(deskName);
//    }
    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.AddException
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        super.onCreateLocal(rec);
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        return rec;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.AddException
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     */
    @Override
    public Record onCreate(Record rec) throws AddException, ValidationException {
        super.onCreate(rec);
        return rec;
    }

    /**
     ***************************************************************************
     * not implemented
     *
     * @param rec
     * @param poleHlidana
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        return canErase;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     ***************************************************************************
     *
     * @param nm
     */
    public void setName(String nm) {
        this.name = nm;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public boolean isAlphabetize() {
        return alphabetize;
    }

    /**
     ***************************************************************************
     *
     * @param alphabetize
     */
    public void setAlphabetize(boolean alphabetize) {
        this.alphabetize = alphabetize;
    }

    /**
     ***************************************************************************
     *
     * @param locale
     * @return
     */
    public List getLocalizedList(Locale locale) {
        Connection con = null;
        List<ListValue> items = new Vector<ListValue>(0, 1);
        ResultSet rs = null;
        Statement stmt = null;

        try {
            con = DirectConnection.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(SELECT_VALUES_SQL);
            while (rs.next()) {
                items.add(new ListValue(rs.getString("PRIJMENI") + " " + rs.getString("JMENO"), rs.getString("IDCISLO")));
            }
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException exc) {
            ReliefLogger.throwing("", "", exc);
        }
        return items;
    }

    /**
     ***************************************************************************
     * Metoda není implementována
     *
     * @param bundleName
     */
    public void setBundle(String bundleName) {
    }

    /**
     ***************************************************************************
     * Metoda není implementována
     *
     * @param dbBundles
     */
    public void setDbBundles(DbBundles dbBundles) {
    }

}
