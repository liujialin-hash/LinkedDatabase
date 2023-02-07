import BaseDao.Base;
import BaseDao.Dao.User;
import org.junit.Test;

import java.sql.SQLException;

public class baseTest {

    @Test
    public void testSelect() throws SQLException {
        Base base = new Base();
        String sql="select * from user ";
        Object params[] ={2};
        User select = base.select(sql, null, User.class);
        System.out.println(select);

    }

}
