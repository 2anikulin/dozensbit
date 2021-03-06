package net.dozensbit.benchmark;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import java.sql.*;

/**
 * H2 in-memory database benchmark.
 *
 * @author Anatoliy Nikulin
 *         2anikulin@gmail.com
 */
public class H2Test
{
    private final int OBJECTS_COUNT;
    private final int TEST_COUNT;

    private Statement stat;
    private Connection con;

    public H2Test(final int objectsCount, final int testsCount)
    {
        OBJECTS_COUNT = objectsCount;
        TEST_COUNT = testsCount;
    }

    /**
     * Single thread benchmark.
     * all objects includes to output result.
     */
    public void allInclusiveSingleThread()
    {
        System.out.println("=========================================================");
        System.out.println("H2 database - all inclusive. Single thread");

        try {
            prepareEnvironment();

            Thread.sleep(5000);

            DescriptiveStatistics statistics = new DescriptiveStatistics();

            for (int i = 0; i < TEST_COUNT; i++) {
                statistics.addValue(execQuery());
            }


            System.out.println(
                    "Average time Math: " + statistics.getMean() / 1000000.0 + " ms"
            );

            System.out.println(
                    "95 percentile: " + statistics.getPercentile(95) / 1000000.0 + " ms"
            );

            System.out.println(
                    "99 percentile: " + statistics.getPercentile(99) / 1000000.0 + " ms"
            );

            System.out.println("Rows count: " + OBJECTS_COUNT);
            System.out.println("Tests count: " + TEST_COUNT);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stat != null) {
                    stat.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void prepareEnvironment() throws ClassNotFoundException, SQLException
    {
        Class.forName("org.h2.Driver");
        con = DriverManager.getConnection("jdbc:h2:mem:mytest", "sa", "");

        stat = con.createStatement();

        stat.execute("CREATE TABLE ACTIVITY (ID INTEGER, ATTR_1 VARCHAR(10), ATTR_2 VARCHAR(10), ATTR_3 VARCHAR(15)," +
                "ATTR_4 VARCHAR(10), ATTR_5 VARCHAR(10), ATTR_6 VARCHAR(10), ATTR_7 VARCHAR(10), ATTR_8 VARCHAR(10)," +
                "ATTR_9 VARCHAR(10), ATTR_10 VARCHAR(10), PRIMARY KEY (ID))");

        PreparedStatement prep = con.prepareStatement("INSERT INTO ACTIVITY " +
                "(ID, ATTR_1, ATTR_2, ATTR_3, ATTR_4, ATTR_5, ATTR_6, ATTR_7, ATTR_8, ATTR_9, ATTR_10) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

        for (int i = 0; i < OBJECTS_COUNT; i++){
            prep.setLong(1, i);
            prep.setString(2, "omsk");
            prep.setString(3, "tomsk");
            prep.setString(4, "novosibirsk");
            prep.setString(5, "moscow");
            prep.setString(6, "male");
            prep.setString(7, "ru");
            prep.setString(8, "de");
            prep.setString(9, "en");
            prep.setString(10, "au");

            if (i <  OBJECTS_COUNT / 2) {
                prep.setString(11, "it");
            } else {
                prep.setString(11, "usa");
            }

            prep.addBatch();
        }
        con.setAutoCommit(false);
        prep.executeBatch();
        con.setAutoCommit(true);

        stat.execute("CREATE INDEX IDX_1 ON ACTIVITY(ATTR_1);");
        stat.execute("CREATE INDEX IDX_2 ON ACTIVITY(ATTR_2);");
        stat.execute("CREATE INDEX IDX_3 ON ACTIVITY(ATTR_3);");
        stat.execute("CREATE INDEX IDX_4 ON ACTIVITY(ATTR_4);");
        stat.execute("CREATE INDEX IDX_5 ON ACTIVITY(ATTR_5);");
        stat.execute("CREATE INDEX IDX_6 ON ACTIVITY(ATTR_6);");
        stat.execute("CREATE INDEX IDX_7 ON ACTIVITY(ATTR_7);");
        stat.execute("CREATE INDEX IDX_8 ON ACTIVITY(ATTR_8);");
        stat.execute("CREATE INDEX IDX_9 ON ACTIVITY(ATTR_9);");
        stat.execute("CREATE INDEX IDX_10 ON ACTIVITY(ATTR_10);");
    }

    private long execQuery() throws SQLException
    {
        long start = System.nanoTime();

        ResultSet rs = null;

        try {
            rs = stat.executeQuery(
                    "SELECT * FROM ACTIVITY WHERE " +
                            "ATTR_1='omsk' AND " +
                            "ATTR_2='tomsk' AND " +
                            "ATTR_3='novosibirsk' AND " +
                            "ATTR_4='moscow' AND " +
                            "ATTR_5='male' AND " +
                            "ATTR_6='ru' AND " +
                            "ATTR_7='de' AND " +
                            "ATTR_8='en' AND " +
                            "ATTR_9='au' AND " +
                            "ATTR_10='it' "
            );

            while (rs.next()) {

                String a1 = rs.getString(2);
                String a2 = rs.getString(3);
                String a3 = rs.getString(4);
                String a4 = rs.getString(5);
                String a5 = rs.getString(6);
                String a6 = rs.getString(7);
                String a7 = rs.getString(8);
                String a8 = rs.getString(9);
                String a9 = rs.getString(10);
                String a10 = rs.getString(11);
            }
        }finally {
            if (rs != null) {
                rs.close();
            }
        }

        return System.nanoTime() - start;
    }
}
